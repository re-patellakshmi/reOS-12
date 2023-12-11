/*
 * Copyright (c) 2015, 2019, 2022 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 *
 * Not a Contribution.
 * Apache license notifications and license are retained
 * for attribution purposes only.
 *
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qualcomm.qti.internal.telephony.dataconnection;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncResult;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.telephony.AccessNetworkConstants;
import android.telephony.Annotation.ApnType;
import android.telephony.CarrierConfigManager;
import android.telephony.DataFailCause;
import android.telephony.data.ApnSetting;
import android.telephony.data.DataCallResponse.HandoverFailureMode;
import android.telephony.Rlog;
import android.telephony.ServiceState;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.TelephonyManager.SimState;
import com.android.internal.telephony.DctConstants;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.RetryManager;
import com.android.internal.telephony.SubscriptionController;
import com.android.internal.telephony.SubscriptionInfoUpdater;
import com.android.internal.telephony.dataconnection.ApnContext;
import com.android.internal.telephony.dataconnection.DcTracker;
import com.qti.extphone.Client;
import com.qti.extphone.ExtTelephonyManager;
import com.qti.extphone.ExtPhoneCallbackBase;
import com.qti.extphone.IExtPhoneCallback;
import com.qti.extphone.ServiceCallback;
import com.qti.extphone.Status;
import com.qti.extphone.Token;
import com.qualcomm.qti.internal.telephony.QtiTelephonyComponentFactory;
import com.qualcomm.qti.internal.telephony.QtiRIL;
import android.view.WindowManager;
import org.codeaurora.telephony.utils.EnhancedRadioCapabilityResponse;
import java.util.ArrayList;

public final class QtiDcTracker extends DcTracker {
    private String LOG_TAG = "QtiDCT";

    private static final int GID = 0;
    private static final int APN_TYPE = 1;
    private static final int DEVICE_CAPABILITY = 2;
    private static final int APN_NAME = 3;
    private static final int KEY_MULTI_APN_ARRAY_FOR_SAME_GID_ENTRY_LENGTH = 4;
    private static final int RETRY_DELAY = 500;     // in milliseconds
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final int RECONNECT_EXT_TELEPHONY_SERVICE_DELAY_MILLISECOND = 2000;
    private static final int EVENT_RECONNECT_QTI_EXT_TELEPHONY_SERVICE = DctConstants.BASE + 200;
    private static final int EVENT_CARRIER_CONFIG_LOADED_ON_ESSENTIAL_RECORDS =
            DctConstants.BASE + 201;

    private EnhancedRadioCapabilityResponse mEnhancedRadioCapability;
    private static int mGetEnhancedRadioCapabilityRetryCount = 0;

    private int mTransportType = AccessNetworkConstants.TRANSPORT_TYPE_WWAN;
    // Maximum data reject count
    public static final int MAX_PDP_REJECT_COUNT = 3;
    // Data reset event tracker to know reset events.
    private QtiDataResetEventTracker mQtiDataResetEventTracker = null;
    // data reject dialog, made static because only one dialog object can be
    // used between multiple dataconnection objects.
    protected static AlertDialog mDataRejectDialog = null;
    //Store data reject cause for comparison
    private String mDataRejectReason = "NONE";
    //Store data reject count
    private int mDataRejectCount = 0;
    //Store data reject cause code
    private int mPdpRejectCauseCode = 0;
    // Whether essential SIM records are loaded.
    private boolean mIsEssentialRecordsLoaded = false;

    private Client mClient;
    private ExtTelephonyManager mExtTelephonyManager;

    private void initQtiRadioCapability() {
        Rlog.d(LOG_TAG, "initQtiRadioCapability");
        mEnhancedRadioCapability = new EnhancedRadioCapabilityResponse();
        mPhone.mCi.getEnhancedRadioCapability(obtainMessage(
                DctConstants.EVENT_GET_ENHANCED_RADIO_CAPABILITY));
    }

    // Constructor
    public QtiDcTracker(Phone phone, int transportType) {
        super(phone, transportType);
        mTransportType = transportType;
        LOG_TAG +=
                 (transportType == AccessNetworkConstants.TRANSPORT_TYPE_WWAN) ? "-C" : "-I";

        initQtiRadioCapability();

        if (transportType == AccessNetworkConstants.TRANSPORT_TYPE_WWAN) {
            mExtTelephonyManager = ExtTelephonyManager.getInstance(mPhone.getContext());
            mExtTelephonyManager.connectService(mServiceCallback);
        }
    }

    private ServiceCallback mServiceCallback = new ServiceCallback() {
        @Override
        public void onConnected() {
            if (DBG) log("ExtTelephony Service connected");
            mClient = mExtTelephonyManager.registerCallback(mPhone.getContext().getPackageName(),
                    mCallback);
            mExtTelephonyManager.getDdsSwitchCapability(mPhone.getPhoneId(), mClient);
            if (DBG) log("Client = " + mClient);
        }
        @Override
        public void onDisconnected() {
            if (DBG) log("ExtTelephony Service disconnected...");
            mExtTelephonyManager.unRegisterCallback(mCallback);
            mClient = null;
            Message msg = obtainMessage(EVENT_RECONNECT_QTI_EXT_TELEPHONY_SERVICE);
            sendMessageDelayed(msg, RECONNECT_EXT_TELEPHONY_SERVICE_DELAY_MILLISECOND);
        }
    };

    protected IExtPhoneCallback mCallback = new ExtPhoneCallbackBase() {
        @Override
        public void onDdsSwitchCapabilityChange(int slotId, Token token,
                Status status, boolean smartDdsSupport) throws RemoteException {
            if (DBG) log("ExtPhoneCallback: onDdsSwitchCapabilityChange support: " +
                    smartDdsSupport + " SlotId: " + slotId);

            if (!SubscriptionManager.isValidSlotIndex(slotId)) {
                return;
            }

            if (mPhone.getPhoneId() == slotId) {
                PhoneFactory.getPhone(slotId).setSmartTempDdsSwitchSupported(smartDdsSupport);
                if (SubscriptionManager.isValidSubscriptionId(mPhone.getSubId())) {
                    sendDataDuringVoiceCallInfo(mDataEnabledSettings.isDataAllowedInVoiceCall(),
                            mPhone.getPhoneId());
                }
            }
        }
    };

    private boolean isDataEnabledOnDds() {
        int defaultDataPhoneId = SubscriptionManager.INVALID_PHONE_INDEX;
        Phone defaultDataPhone = null;
        boolean isDataEnableOnDds = false;
        SubscriptionController subscriptionController = SubscriptionController.getInstance();

        int defaultDataSubId = subscriptionController.getDefaultDataSubId();
        if (SubscriptionManager.isValidSubscriptionId(defaultDataSubId)) {
            defaultDataPhoneId = subscriptionController.getPhoneId(defaultDataSubId);
        }

        if (SubscriptionManager.isValidPhoneId(defaultDataPhoneId)) {
            defaultDataPhone = PhoneFactory.getPhone(defaultDataPhoneId);
        }

        if (defaultDataPhone != null) {
            isDataEnableOnDds = defaultDataPhone.getDataEnabledSettings().isDataEnabled();
        }

        return isDataEnableOnDds;
    }

    private void sendDataDuringVoiceCallInfo(boolean dataDuringCall, int phoneId) {
        if (!SubscriptionManager.isValidPhoneId(phoneId)) {
            return;
        }

        // Always send false on DDS
        if ( dataDuringCall && isCurrentSubDds(PhoneFactory.getPhone(phoneId))) {
            dataDuringCall = false;
        // if current instance is non DDS, send false if mobile data on DDS is disabled
        } else if (dataDuringCall && !isDataEnabledOnDds()) {
            dataDuringCall = false;
        }

        if (mExtTelephonyManager != null && PhoneFactory.getPhone(phoneId)
                .getSmartTempDdsSwitchSupported()) {
            if (DBG) log("Data during voice call: " + dataDuringCall);
            mExtTelephonyManager.sendUserPreferenceForDataDuringVoiceCall(
                    phoneId, dataDuringCall, mClient);
        }
    }

    @Override
    protected void onVoiceCallEnded() {
        if (DBG) log("onVoiceCallEnded");
        mInVoiceCall = false;
        if (isAnyDataConnected()) {
            if (!mPhone.getServiceStateTracker().isConcurrentVoiceAndDataAllowed()) {
                startNetStatPoll();
                startDataStallAlarm(DATA_STALL_NOT_SUSPECTED);
            } else {
                // clean slate after call end.
                resetPollStats();
            }
        }
        //Allow data call retry only on DDS sub
        if (mPhone.getSubId() == SubscriptionManager.getDefaultDataSubscriptionId()) {
            // reset reconnect timer
            setupDataOnAllConnectableApns(Phone.REASON_VOICE_CALL_ENDED, RetryFailures.ALWAYS);
        }
    }

    @Override
    protected void log(String s) {
        Rlog.d(LOG_TAG, "[" + mPhone.getPhoneId() + "]" + s);
    }

    @Override
    public void handleMessage (Message msg) {
        if (VDBG) log("handleMessage msg=" + msg);
        AsyncResult ar;

        switch (msg.what) {
            case DctConstants.EVENT_GET_ENHANCED_RADIO_CAPABILITY:
                log("EVENT_GET_ENHANCED_RADIO_CAPABILITY");
                if (mGetEnhancedRadioCapabilityRetryCount >= MAX_RETRY_ATTEMPTS) {
                    break;
                }
                ar = (AsyncResult) msg.obj;
                if (ar.exception != null || ar.result == null) {
                    mGetEnhancedRadioCapabilityRetryCount++;
                    Message message = obtainMessage(
                            DctConstants.EVENT_GET_ENHANCED_RADIO_CAPABILITY_RETRY);
                    sendMessageDelayed(message, RETRY_DELAY);
                    break;
                }
                int raf = (int) ar.result;
                mEnhancedRadioCapability.updateEnhancedRadioCapability(raf);
                break;
            case DctConstants.EVENT_GET_ENHANCED_RADIO_CAPABILITY_RETRY:
                log("EVENT_GET_ENHANCED_RADIO_CAPABILITY_RETRY");
                mPhone.mCi.getEnhancedRadioCapability(obtainMessage(
                        DctConstants.EVENT_GET_ENHANCED_RADIO_CAPABILITY));
                break;
            case DctConstants.EVENT_DATA_ENABLED_OVERRIDE_RULES_CHANGED:
                log("EVENT_DATA_ENABLED_OVERRIDE_RULES_CHANGED");
                if (SubscriptionManager.isValidSubscriptionId(mPhone.getSubId())) {
                    sendDataDuringVoiceCallInfo(mDataEnabledSettings.isDataAllowedInVoiceCall(),
                            mPhone.getPhoneId());
                }
                super.handleMessage(msg);
                break;
            case DctConstants.EVENT_DATA_ENABLED_CHANGED:
                log("EVENT_DATA_ENABLED_CHANGED");
                if(SubscriptionManager.isValidSubscriptionId(mPhone.getSubId())
                        && isCurrentSubDds(mPhone)) {
                    boolean isDataEnabledOnDds = mDataEnabledSettings.isDataEnabled();

                    // If user disables mobile data on DDS, send data during
                    // voice call UI option as false on nonDDS as temp DDS switch is not allowed.
                    // If user enables mobile data on DDS, send user selected data during voice
                    // call UI info on nonDDS.
                    for (int i = 0; i < getPhoneCount(); i++) {
                        Phone phone = PhoneFactory.getPhone(i);
                        if ((phone != null) && (!isCurrentSubDds(phone))) {
                            if (!isDataEnabledOnDds) {
                                sendDataDuringVoiceCallInfo(false, i);
                            } else {
                                sendDataDuringVoiceCallInfo(phone.getDataEnabledSettings().
                                        isDataAllowedInVoiceCall(), i);
                            }
                        }
                    }
                }
                super.handleMessage(msg);
                break;
            case EVENT_RECONNECT_QTI_EXT_TELEPHONY_SERVICE:
                log("EVENT_RECONNECT_QTI_EXT_TELEPHONY_SERVICE");
                if (mTransportType == AccessNetworkConstants.TRANSPORT_TYPE_WWAN) {
                    mExtTelephonyManager.connectService(mServiceCallback);
                }
                break;
            case EVENT_CARRIER_CONFIG_LOADED_ON_ESSENTIAL_RECORDS:
                log("EVENT_CARRIER_CONFIG_LOADED_ON_ESSENTIAL_RECORDS");
                onCarrierConfigChanged();
                break;
            default:
                super.handleMessage(msg);
        }
    }

    private boolean isCurrentSubDds(Phone phone) {
        if (phone == null) {
            return false;
        }
        return SubscriptionManager.getDefaultDataSubscriptionId() == phone.getSubId();
    }

    private int getPhoneCount() {
        TelephonyManager tm = (TelephonyManager) mPhone.getContext().
                getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getActiveModemCount();
    }

    /*Filters out multipe apns based on radio capability if the APN's GID value is listed in
    CarrierConfigManager#KEY_MULTI_APN_ARRAY_FOR_SAME_GID as per the operator requirement*/
    @Override
    protected void filterApnSettingsWithRadioCapability() {
        if (VDBG) {
            log(" filterApnSettingsWithRadioCapability start: mAllApnSettings:" + mAllApnSettings);
        }
        int i = 0;
        while (i < mAllApnSettings.size()) {
            ApnSetting apn = mAllApnSettings.get(i);
            String apnType = ApnSetting.getApnTypesStringFromBitmask(apn.getApnTypeBitmask());
            if(apn.hasMvnoParams() && (apn.getMvnoType() == ApnSetting.MVNO_TYPE_GID) &&
                    isApnFilteringRequired(apn.getMvnoMatchData(), apnType)) {
                String apnName = getApnBasedOnRadioCapability(apn.getMvnoMatchData(),
                        apnType, mEnhancedRadioCapability.getEnhancedRadioCapability());
                if(apnName != null && !apnName.equals(apn.getApnName())) {
                    mAllApnSettings.remove(i);
                    log("filterApnSettingsWithRadioCapability: removed not supported apn:" + apn);
                } else {
                    i++;
                }
            } else {
                i++;
            }
        }
        if(VDBG) {
            log("filterApnSettingsWithRadioCapability: end: mAllApnSettings:" + mAllApnSettings);
        }
    }

     /**
     * Check if APN filtering is required
     *
     * @param gid gid value of the apn.
     * @param apnType  apn type.
     * @return True if multipe apns present in CarrierConfigManager#KEY_MULTI_APN_ARRAY_FOR_SAME_GID
               for this gid .
     */
    private boolean isApnFilteringRequired(String gid, String apnType) {
        final String[] apnConfig =
                CarrierConfigManager.getDefaultConfig().
                getStringArray(CarrierConfigManager.KEY_MULTI_APN_ARRAY_FOR_SAME_GID);
        for(String apnEntry: apnConfig) {
            String[] split = apnEntry.split(":");
            if (split.length == KEY_MULTI_APN_ARRAY_FOR_SAME_GID_ENTRY_LENGTH) {
                if(gid.equals(split[GID]) && apnType.equals(split[APN_TYPE])) {
                    return true;
                }
            }
        }

        return false;
    }

     /**
     * Return apn name based on the device capability if the coresponding
     * entry present in CarrierConfigManager#KEY_MULTI_APN_ARRAY_FOR_SAME_GID
     *
     * @param gid gid value of the apn.
     * @param apnType  apn type.
     * @param deviceCapability  device capability, Ex: SA, NSA, LTE etc..
     * @return apn name from the entry matches with all the above three params.
     */

    private String getApnBasedOnRadioCapability(String gid, String apnType,
            String deviceCapability) {
        if(deviceCapability == null) {
            loge("getApnBasedOnRadioCapability: deviceCapability is null");
            return null;
        }
        final String[] apnConfig =
                CarrierConfigManager.getDefaultConfig().
                getStringArray(CarrierConfigManager.KEY_MULTI_APN_ARRAY_FOR_SAME_GID);
        for(String apnEntry: apnConfig) {
            String[] split = apnEntry.split(":");
            if (split.length == KEY_MULTI_APN_ARRAY_FOR_SAME_GID_ENTRY_LENGTH &&
                    deviceCapability != null) {
                if(gid.equals(split[GID]) && apnType.equals(split[APN_TYPE]) &&
                        deviceCapability.equals(split[DEVICE_CAPABILITY])) {
                    return split[APN_NAME];
                }
            }
        }
        return null;
    }

    @Override
    protected void setupDataOnConnectableApn(ApnContext apnContext, String reason,
            RetryFailures retryFailures) {
        if (mPhone.getContext().getResources().getBoolean(
                com.android.internal.R.bool.config_pdp_reject_enable_retry) &&
                mDataRejectCount > 0 &&
                TextUtils.equals(apnContext.getApnType(), PhoneConstants.APN_TYPE_DEFAULT)) {
            log("setupDataOnConnectableApn: data retry in progress, skip processing");
        } else {
            super.setupDataOnConnectableApn(apnContext, reason, retryFailures);
        }
    }

    @Override
    protected void cleanUpConnectionInternal(boolean detach, @ReleaseNetworkType int releaseType,
            ApnContext apnContext) {
        if (apnContext == null) {
            if (DBG) log("cleanUpConnectionInternal: apn context is null");
            return;
        }
        if (mPhone.getContext().getResources().getBoolean(
                 com.android.internal.R.bool.config_pdp_reject_enable_retry) &&
                 mDataRejectCount > 0 &&
                 TextUtils.equals(apnContext.getApnType(), PhoneConstants.APN_TYPE_DEFAULT)) {
            log("cleanUpConnectionInternal: data retry in progress, skip cleanup");
        } else {
            super.cleanUpConnectionInternal(detach, releaseType, apnContext);
        }
    }

    @Override
    protected boolean retryAfterDisconnected(ApnContext apnContext) {
        if (mPhone.getContext().getResources().getBoolean(
                com.android.internal.R.bool.config_pdp_reject_enable_retry) &&
                mDataRejectCount > 0 &&
                TextUtils.equals(apnContext.getApnType(), PhoneConstants.APN_TYPE_DEFAULT)) {
            log("retryAfterDisconnected: data retry in progress, skip this retry");
            return false;
        } else {
            return super.retryAfterDisconnected(apnContext);
        }
    }

    @Override
    protected void onDataSetupComplete(ApnContext apnContext, boolean success, int cause,
            @RequestNetworkType int requestType, @HandoverFailureMode int handoverFailureMode) {
        boolean isPdpRejectConfigEnabled = mPhone.getContext().getResources().getBoolean(
                com.android.internal.R.bool.config_pdp_reject_enable_retry);
        if (success) {
            if (isPdpRejectConfigEnabled &&
                    TextUtils.equals(apnContext.getApnType(), PhoneConstants.APN_TYPE_DEFAULT)) {
                handlePdpRejectCauseSuccess();
            }
        } else {
            mPdpRejectCauseCode = cause;
        }
        super.onDataSetupComplete(apnContext, success, cause, requestType, handoverFailureMode);
    }


    @Override
    public void enableApn(@ApnType int apnType, @RequestNetworkType int requestType,
            Message onHandoverCompleteMsg) {
        if (mPhone.getContext().getResources().getBoolean(
                com.android.internal.R.bool.config_pdp_reject_enable_retry) &&
                mDataRejectCount > 0 &&
                TextUtils.equals(ApnSetting.getApnTypeString(apnType),
                PhoneConstants.APN_TYPE_DEFAULT)) {
            log("enableApn: data retry in progress, skip processing");
        } else {
            super.enableApn(apnType, requestType, onHandoverCompleteMsg);
        }
    }

    @Override
    protected void onDataSetupCompleteError(ApnContext apnContext,
            @RequestNetworkType int requestType, boolean fallbackOnFailedHandover) {
        long delay = apnContext.getDelayForNextApn(mFailFast);
        if (mPhone.getContext().getResources().getBoolean(
                com.android.internal.R.bool.config_pdp_reject_enable_retry) &&
                TextUtils.equals(apnContext.getApnType(), PhoneConstants.APN_TYPE_DEFAULT)) {
            String reason = DataFailCause.toString(mPdpRejectCauseCode);
            //Reset apn permanent failure to allow retry
            ApnSetting apn = apnContext.getApnSetting();
            if (apn != null) {
                if (DBG) log("onDataSetupCompleteError: reset permanent failure on apn");
                apn.setPermanentFailed(false);
            }
            if (isMatchingPdpRejectCause(reason)) {
                if (mQtiDataResetEventTracker == null) {
                    mQtiDataResetEventTracker = new QtiDataResetEventTracker(mTransportType,
                            mPhone, mResetEventListener);
                }
                if (mDataRejectCount == 0) {
                    mQtiDataResetEventTracker.startResetEventTracker();
                }
                boolean isHandled = handlePdpRejectCauseFailure(reason);
                /* If MAX Reject count reached, display pop-up to user */
                if (MAX_PDP_REJECT_COUNT <= mDataRejectCount) {
                    if (DBG) log("onDataSetupCompleteError: reached max retry count");
                    displayPopup(mDataRejectReason);
                    delay = -1;
                } else if (isHandled) {
                    delay = mPhone.getContext().getResources().getInteger(
                            com.android.internal.R.integer.config_pdp_reject_retry_delay_ms);
                    if (DBG) log("onDataSetupCompleteError: delay from config: " + delay);
                }
            } else {
                if (DBG) log("onDataSetupCompleteError: reset reject count");
                resetDataRejectCounter();
            }
        }
        // Check if we need to retry or not.
        if (delay >= 0 && delay != RetryManager.NO_RETRY && !fallbackOnFailedHandover) {
            if (DBG) {
                log("onDataSetupCompleteError: APN type=" + apnContext.getApnType()
                        + ". Request type=" + requestTypeToString(requestType) + ", Retry in "
                        + delay + "ms.");
            }
            startReconnect(delay, apnContext, requestType);
        } else {
            // If we are not going to retry any APN, set this APN context to failed state.
            // This would be the final state of a data connection.
            apnContext.setState(DctConstants.State.FAILED);
            apnContext.setDataConnection(null);
            log("onDataSetupCompleteError: Stop retrying APNs. delay=" + delay
                    + ", requestType=" + requestTypeToString(requestType));
            //send request network complete messages as needed
            sendHandoverCompleteMessages(apnContext.getApnTypeBitmask(), false,
                    fallbackOnFailedHandover);
        }
    }

    @Override
    protected void onSimStateUpdated(@SimState int simState) {
        resetEssentialRecordsLoadedStateIfRequired(simState);
        super.onSimStateUpdated(simState);
    }

    /*
     * Reset the state of flag {@link mIsEssentialRecordsLoaded} to false when the SIM
     * state moves to a state which is not SIM_STATE_LOADED
     */
    private void resetEssentialRecordsLoadedStateIfRequired(@SimState int simState) {
        switch(simState) {
            case TelephonyManager.SIM_STATE_UNKNOWN:
            case TelephonyManager.SIM_STATE_CARD_RESTRICTED:
            case TelephonyManager.SIM_STATE_ABSENT:
            case TelephonyManager.SIM_STATE_CARD_IO_ERROR:
            case TelephonyManager.SIM_STATE_NOT_READY:
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
            case TelephonyManager.SIM_STATE_PERM_DISABLED:
                setEssentialRecordsLoaded(false);
                break;
        }
    }

    protected boolean isSimCardPresentAndEssentialRecordsLoaded() {
        int simCardState = mTelephonyManager.getSimCardState();
        int simApplicationState = SubscriptionManager.getSimStateForSlotIndex(mPhone.getPhoneId());
        log("simCardState: "
                + SubscriptionInfoUpdater.simStateString(simCardState)
                + ", simApplicationState: "
                + SubscriptionInfoUpdater.simStateString(simApplicationState));
        return (simCardState == TelephonyManager.SIM_STATE_PRESENT
                && simApplicationState == TelephonyManager.SIM_STATE_READY
                && isEssentialRecordsLoaded());
    }

    public void onCarrierConfigLoadedForEssentialRecords() {
        log("onCarrierConfigLoadedForEssentialRecords");
        sendEmptyMessage(EVENT_CARRIER_CONFIG_LOADED_ON_ESSENTIAL_RECORDS);
    }

    /*
     * Set the value of {@link mIsEssentialRecordsLoaded} to true or false
     * This is set to true when SIM moves to SIM_STATE_READY and all essential records are loaded
     * It is reset to false when the SIM state moves to any state other than SIM_STATE_LOADED
     */
    public void setEssentialRecordsLoaded(boolean isLoaded) {
        log("setEssentialRecordsLoaded to " + isLoaded);
        mIsEssentialRecordsLoaded = isLoaded;
    }

    private boolean isEssentialRecordsLoaded() {
        log("isEssentialRecordsLoaded: " + mIsEssentialRecordsLoaded);
        return mIsEssentialRecordsLoaded;
    }

    /*
     * Reset data reject params on data call success
     */
    private void handlePdpRejectCauseSuccess() {
        if (mDataRejectCount > 0) {
            if (DBG) log("handlePdpRejectCauseSuccess: reset reject count");
            resetDataRejectCounter();
        }
    }

    /*
     * Process data failure if RAT is WCDMA
     * And if the failure cause matches one of the following cause codes:
     * 1. USER_AUTHENTICATION
     * 2. SERVICE_OPTION_NOT_SUBSCRIBED
     * 3. MULTI_CONN_TO_SAME_PDN_NOT_ALLOWED
     */
    private boolean handlePdpRejectCauseFailure(String reason) {
        boolean handleFailure = false;
        // Check if data rat is WCDMA
        if (isWCDMA(getDataRat())) {
            if (DBG) log("handlePdpRejectCauseFailure: reason=" + reason +
                    ", mDataRejectReason=" + mDataRejectReason);
            /*
             * If previously rejected code is not same as current data reject reason,
             * then reset the count and reset the reject reason
             */
            if (!reason.equalsIgnoreCase(mDataRejectReason)) {
                resetDataRejectCounter();
            }

                        /*
             * If failure reason is USER_AUTHENTICATION or
             * SERVICE_OPTION_NOT_SUBSCRIBED or MULTI_CONN_TO_SAME_PDN_NOT_ALLOWED,
             * increment counter and store reject cause
             */
            if (isMatchingPdpRejectCause(reason)) {
                mDataRejectCount++;
                mDataRejectReason = reason;
                if (DBG) log ("handlePdpRejectCauseFailure: DataRejectCount = " +
                        mDataRejectCount);
                handleFailure = true;
            }
        } else {
            if (DBG) log("isPdpRejectCauseFailureHandled: DataConnection not on wcdma");
            resetDataRejectCounter();
        }
        return handleFailure;
    }

    /*
     * Data reset event listener. Dc will get get onResetEvent
     * whenever any data reset event occurs
     */
    private QtiDataResetEventTracker.ResetEventListener mResetEventListener =
           new QtiDataResetEventTracker.ResetEventListener() {
        @Override
        public void onResetEvent(boolean retry) {
            if (DBG) log("onResetEvent: retry=" + retry);
            //Dismiss dialog
            if (mDataRejectDialog != null && mDataRejectDialog.isShowing()) {
                if (DBG) log("onResetEvent: Dismiss dialog");
                mDataRejectDialog.dismiss();
            }
            mQtiDataResetEventTracker.stopResetEventTracker();
            for (ApnContext apnContext : mApnContexts.values()) {
                if (mDataRejectCount > 0) {
                    if (DBG) log("onResetEvent: reset reject count=" + mDataRejectCount);
                    resetDataRejectCounter();
                    cancelReconnect(apnContext);
                    if (retry) {
                        if (DBG) log("onResetEvent: retry data call on apnContext=" + apnContext);
                        sendMessage(obtainMessage(DctConstants.EVENT_TRY_SETUP_DATA,
                                REQUEST_TYPE_NORMAL, 0, apnContext));
                    }
                }
            }
        }
    };

    /**
     * This function will display the pdp reject message
     */
    private void displayPopup(String pdpRejectCause) {
        if (DBG) log("displayPopup : " + pdpRejectCause);
        String title = mPhone.getContext().getResources().
                getString(com.android.internal.R.string.config_pdp_reject_dialog_title);
        String message = null;
        if (pdpRejectCause.equalsIgnoreCase("USER_AUTHENTICATION")) {
            message = mPhone.getContext().getResources().
                    getString(com.android.internal.R.string.config_pdp_reject_user_authentication_failed);
        } else if (pdpRejectCause.equalsIgnoreCase("SERVICE_OPTION_NOT_SUBSCRIBED")) {
            message = mPhone.getContext().getResources().getString(
                    com.android.internal.R.string.config_pdp_reject_service_not_subscribed);
        } else if (pdpRejectCause.equalsIgnoreCase("MULTI_CONN_TO_SAME_PDN_NOT_ALLOWED")) {
            message = mPhone.getContext().getResources().getString(
                    com.android.internal.R.string.config_pdp_reject_multi_conn_to_same_pdn_not_allowed);
        }
        if (mDataRejectDialog == null || !mDataRejectDialog.isShowing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    mPhone.getContext());
            builder.setPositiveButton(android.R.string.ok, null);
            mDataRejectDialog = builder.create();
        }
        mDataRejectDialog.setMessage(message);
        mDataRejectDialog.setCanceledOnTouchOutside(false);
        mDataRejectDialog.setTitle(title);
        mDataRejectDialog.getWindow().setType(
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mDataRejectDialog.show();
    }

    /*
     * returns true if data reject cause matches errors listed
     */
    private boolean isMatchingPdpRejectCause(String reason) {
        return reason.equalsIgnoreCase("USER_AUTHENTICATION") ||
               reason.equalsIgnoreCase("SERVICE_OPTION_NOT_SUBSCRIBED") ||
               reason.equalsIgnoreCase("MULTI_CONN_TO_SAME_PDN_NOT_ALLOWED");
    }

    /**
     * returns true if radioTechnology is WCDMA rat, else false
     */
    private boolean isWCDMA(int radioTechnology) {
        return radioTechnology == ServiceState.RIL_RADIO_TECHNOLOGY_UMTS
            || radioTechnology == ServiceState.RIL_RADIO_TECHNOLOGY_HSDPA
            || radioTechnology == ServiceState.RIL_RADIO_TECHNOLOGY_HSUPA
            || radioTechnology == ServiceState.RIL_RADIO_TECHNOLOGY_HSPA
            || radioTechnology == ServiceState.RIL_RADIO_TECHNOLOGY_HSPAP;
    }

    /*
     * Reset data reject count and reason
     */
    private void resetDataRejectCounter() {
        mDataRejectCount = 0;
        mDataRejectReason = "NONE";
    }
}
