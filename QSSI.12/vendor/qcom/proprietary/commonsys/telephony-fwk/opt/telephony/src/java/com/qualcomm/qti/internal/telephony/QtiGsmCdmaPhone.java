/*
 * Copyright (c) 2016, 2019-2022 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 *
 * Not a Contribution, Apache license notifications and license are retained
 * for attribution purposes only.
 *
 * Copyright (C) 2006 The Android Open Source Project
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


package com.qualcomm.qti.internal.telephony;


import android.content.Context;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.CarrierConfigManager;
import android.telephony.ImsiEncryptionInfo;
import android.telephony.Rlog;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.util.Pair;

import com.android.internal.telephony.CommandsInterface;
import com.android.internal.telephony.vendor.VendorGsmCdmaPhone;
import com.android.internal.telephony.IccCardConstants;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.PhoneNotifier;
import com.android.internal.telephony.RadioCapability;
import com.android.internal.telephony.RIL;
import com.android.internal.telephony.TelephonyComponentFactory;
import com.android.internal.telephony.CarrierInfoManager;
import com.android.internal.telephony.SubscriptionController;
import com.android.internal.telephony.uicc.IccCardStatus;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.internal.telephony.uicc.IccUtils;
import com.android.internal.telephony.uicc.UiccCard;
import com.android.internal.telephony.uicc.UiccSlot;

import com.qti.extphone.ExtTelephonyManager;

public class QtiGsmCdmaPhone extends VendorGsmCdmaPhone {
    private static final String LOG_TAG = "QtiGsmCdmaPhone";
    private static final int PROP_EVENT_START = EVENT_LAST;
    private static final int EVENT_RESET_CARRIER_KEY_IMSI_ENCRYPTION = PROP_EVENT_START + 1;
    private static final int EVENT_ESSENTIAL_SIM_RECORDS_LOADED      = PROP_EVENT_START + 2;

    private BaseRilInterface mQtiRilInterface;
    private int imsiToken = 0;

    public QtiGsmCdmaPhone(Context context,
            CommandsInterface ci, PhoneNotifier notifier, int phoneId,
            int precisePhoneType, TelephonyComponentFactory telephonyComponentFactory) {
        this(context, ci, notifier, false, phoneId, precisePhoneType,
                telephonyComponentFactory);
    }

    public QtiGsmCdmaPhone(Context context,
            CommandsInterface ci, PhoneNotifier notifier, boolean unitTestMode, int phoneId,
            int precisePhoneType, TelephonyComponentFactory telephonyComponentFactory) {
        super(context, ci, notifier, unitTestMode, phoneId, precisePhoneType,
                telephonyComponentFactory);
        Rlog.d(LOG_TAG, "Constructor");
        mQtiRilInterface = getQtiRilInterface();
        mCi.registerForCarrierInfoForImsiEncryption(this,
                EVENT_RESET_CARRIER_KEY_IMSI_ENCRYPTION, null);
    }

    public boolean setLocalCallHold(boolean enable) {
        if (!mQtiRilInterface.isServiceReady()) {
            Rlog.e(LOG_TAG, "mQtiRilInterface is not ready yet");
            return false;
        }
        return mQtiRilInterface.setLocalCallHold(mPhoneId, enable);
    }

    @Override
    public void dispose() {
        mQtiRilInterface = null;
        super.dispose();
    }

    @Override
    public void handleMessage(Message msg) {
        Rlog.d(LOG_TAG, "handleMessage: Event: " + msg.what);
        AsyncResult ar;
        switch(msg.what) {

            case EVENT_SIM_RECORDS_LOADED:
                if(isPhoneTypeGsm()) {
                    Rlog.d(LOG_TAG, "notify call forward indication, phone id:" + mPhoneId);
                    notifyCallForwardingIndicator();
                }
                super.handleMessage(msg);
                break;

            case EVENT_NV_READY:{
                Rlog.d(LOG_TAG, "Event EVENT_NV_READY Received");
                mSST.pollState();
                // Notify voicemails.
                Rlog.d(LOG_TAG, "notifyMessageWaitingChanged");
                mNotifier.notifyMessageWaitingChanged(this);
                updateVoiceMail();
                // Do not call super.handleMessage().
                // AOSP do not handle EVENT_NV_READY.
                break;
            }

            case EVENT_RESET_CARRIER_KEY_IMSI_ENCRYPTION:
                Rlog.d(LOG_TAG, "Event EVENT_RESET_CARRIER_KEY_IMSI_ENCRYPTION");
                super.resetCarrierKeysForImsiEncryption();
                break;

            case EVENT_ESSENTIAL_SIM_RECORDS_LOADED:
                logd("Event EVENT_ESSENTIAL_SIM_RECORDS_LOADED Received");
                handleEssentialRecordsLoaded();
                break;

            case EVENT_MODEM_RESET:
                Rlog.d(LOG_TAG,"Event EVENT_MODEM_RESET Received");
                ScbmHandler.getInstance().handleModemReset();
                super.handleMessage(msg);
                break;

            default: {
                super.handleMessage(msg);
            }

        }
    }

    private BaseRilInterface getQtiRilInterface() {
        BaseRilInterface qtiRilInterface;
        if (getUnitTestMode()) {
            logd("getQtiRilInterface, unitTestMode = true");
            qtiRilInterface = SimulatedQtiRilInterface.getInstance(mContext);
        } else {
            qtiRilInterface = QtiRilInterface.getInstance(mContext);
        }
        return qtiRilInterface;
    }


    @Override
    public void setCarrierInfoForImsiEncryption(ImsiEncryptionInfo imsiEncryptionInfo) {
        Phone phone = PhoneFactory.getPhone(mPhoneId);
        if (phone != null && phone.getHalVersion().greaterOrEqual(RIL.RADIO_HAL_VERSION_1_6)) {
            super.setCarrierInfoForImsiEncryption(imsiEncryptionInfo);
        } else {
            CarrierInfoManager.setCarrierInfoForImsiEncryption(imsiEncryptionInfo,
                    mContext, mPhoneId);
            QtiTelephonyComponentFactory.getInstance().getRil(mPhoneId)
                    .setCarrierInfoForImsiEncryption(++imsiToken, imsiEncryptionInfo);
        }
    }
    /**
     * Retrieves the full serial number of the ICC (including hex digits), if applicable.
     */
    @Override
    public String getFullIccSerialNumber() {
        String iccId = super.getFullIccSerialNumber();

        if (TextUtils.isEmpty(iccId)) {
            UiccSlot uiccSlot = mUiccController.getUiccSlotForPhone(mPhoneId);
            iccId = (uiccSlot != null) ? uiccSlot.getIccId() : null;
        }
        return iccId;
    }

    private static final String SIM_STATUS = "sim_status-%d";
    private static final int INVALID_SIM_STATUS = -1;

    /*
     * Skip reapply if sim status is never set by Telephony
     */
    @Override
    protected void reapplyUiccAppsEnablementIfNeeded(int retries) {
        UiccSlot slot = mUiccController.getUiccSlotForPhone(mPhoneId);

        // If no card is present or we don't have mUiccApplicationsEnabled yet, do nothing.
        if (slot == null || slot.getCardState() != IccCardStatus.CardState.CARDSTATE_PRESENT
                || mUiccApplicationsEnabled == null) {
            return;
        }

        String iccId = IccUtils.stripTrailingFs(slot.getIccId());
        if (iccId == null) {
            logd("reapplyUiccAppsEnablementIfNeeded: iccid is null, do nothing");
            return;
        }

        SubscriptionInfo info = SubscriptionController.getInstance().getSubInfoForIccId(iccId);

        // If info is null, it could be a new subscription. By default we enable it.
        boolean expectedValue = info == null ? true : info.areUiccApplicationsEnabled();

        SharedPreferences sp
                = PreferenceManager.getDefaultSharedPreferences(getContext());
        String key = String.format(SIM_STATUS, mPhoneId);
        int simStatus = sp.getInt(key, INVALID_SIM_STATUS);
        if (simStatus == INVALID_SIM_STATUS) {
            logd("reapplyUiccAppsEnablementIfNeeded: Apply SIM status from RIL," +
                    "mUiccApplicationsEnabled=" + mUiccApplicationsEnabled);
            //Update SIM status in db
            ContentValues value = new ContentValues(1);
            value.put(SubscriptionManager.UICC_APPLICATIONS_ENABLED, mUiccApplicationsEnabled);
            mContext.getContentResolver().update(SubscriptionManager.CONTENT_URI, value,
                    SubscriptionManager.ICC_ID + "=\'" + iccId + "\'", null);

            //Update SIM status in shared preference
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(key, mUiccApplicationsEnabled ? 1 : 0);
            editor.commit();

            mCi.enableUiccApplications(mUiccApplicationsEnabled, Message.obtain(
                     this, EVENT_REAPPLY_UICC_APPS_ENABLEMENT_DONE,
                     new Pair<Boolean, Integer>(expectedValue, retries)));
        } else {
            // If for any reason current state is different from configured state, re-apply the
            // configured state.
            if (expectedValue != mUiccApplicationsEnabled) {
                mCi.enableUiccApplications(expectedValue, Message.obtain(
                        this, EVENT_REAPPLY_UICC_APPS_ENABLEMENT_DONE,
                        new Pair<Boolean, Integer>(expectedValue, retries)));

                //Update SIM status in shared preference
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt(key, expectedValue ? 1 : 0);
                editor.commit();
            }
        }
    }

    /*
     * Store sim status in shared preference, this is to track sim state change by user
     */
    @Override
    public void enableUiccApplications(boolean enable, Message onCompleteMessage) {
        UiccSlot slot = mUiccController.getUiccSlotForPhone(mPhoneId);
        if (slot != null && slot.getCardState() == IccCardStatus.CardState.CARDSTATE_PRESENT) {
            SharedPreferences sp
                = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = sp.edit();
            String key = String.format(SIM_STATUS, mPhoneId);
            editor.putInt(key, enable ? 1 : 0);
            editor.commit();
        }
        super.enableUiccApplications(enable, onCompleteMessage);
    }

    private void handleEssentialRecordsLoaded() {
        logd("handleEssentialRecordsLoaded, mPhoneId: " + mPhoneId);

        // Resolve the carrierId
        resolveSubscriptionCarrierId(IccCardConstants.INTENT_VALUE_ICC_LOADED);

        // Request the carrier config manager to update the config for mPhoneId
        CarrierConfigManager configManager =
                (CarrierConfigManager) mContext.getSystemService(Context.CARRIER_CONFIG_SERVICE);
        configManager.updateConfigForPhoneId(mPhoneId,
                ExtTelephonyManager.SIM_STATE_ESSENTIAL_RECORDS_LOADED);

        // Set essential records loaded state of DcTracker to true
        for (int transport : mTransportManager.getAvailableTransports()) {
            if (getDcTracker(transport) != null) {
                getDcTracker(transport).setEssentialRecordsLoaded(true);
            }
        }
    }

    public void onCarrierConfigLoadedForEssentialRecords() {
        logd("onCarrierConfigLoadedForEssentialRecords");
        // We are here because CarrierConfigs have been fetched after essential records were loaded.
        // Inform DcTracker so that it can begin data setup early
        for (int transport : mTransportManager.getAvailableTransports()) {
            if (getDcTracker(transport) != null) {
                getDcTracker(transport).onCarrierConfigLoadedForEssentialRecords();
            }
        }
    }

    @Override
    protected void registerForIccRecordEvents() {
        QtiServiceStateTracker qtiSst = (QtiServiceStateTracker) mSST;
        IccRecords r = mIccRecords.get();

        if (r != null) {
            logd("registerForEssentialRecordsLoaded");
            r.registerForEssentialRecordsLoaded(this, EVENT_ESSENTIAL_SIM_RECORDS_LOADED, null);
        }
        super.registerForIccRecordEvents();
    }

    @Override
    protected void unregisterForIccRecordEvents() {
        QtiServiceStateTracker qtiSst = (QtiServiceStateTracker) mSST;
        IccRecords r = mIccRecords.get();

        if (r != null) {
            logd("unregisterForEssentialRecordsLoaded");
            r.unregisterForEssentialRecordsLoaded(this);
        }
        super.unregisterForIccRecordEvents();
    }

    @Override
    public void setSmartTempDdsSwitchSupported(boolean smartDdsSwitch) {
        mSmartTempDdsSwitchSupported = smartDdsSwitch;
    }

    @Override
    public boolean getSmartTempDdsSwitchSupported() {
        return mSmartTempDdsSwitchSupported;
    }

    @Override
    public void setTelephonyTempDdsSwitch(boolean telephonyDdsSwitch) {
        mTelephonyTempDdsSwitch = telephonyDdsSwitch;
    }

    @Override
    public boolean getTelephonyTempDdsSwitch() {
        return mTelephonyTempDdsSwitch;
    }

    public boolean isInScbm() {
        return ScbmHandler.getInstance().isInScbm();
    }

    public void exitScbm() {
        ScbmHandler.getInstance().exitScbm();
    }

    public void setOnScbmExitResponse(Handler h, int what, Object obj) {
        ScbmHandler.getInstance().setOnScbmExitResponse(h, what, obj);
    }

    public void unsetOnScbmExitResponse(Handler h) {
        ScbmHandler.getInstance().unsetOnScbmExitResponse(h);
    }

    public void registerForScbmTimerReset(Handler h, int what, Object obj) {
        ScbmHandler.getInstance().registerForScbmTimerReset(h, what, obj);
    }

    public void unregisterForScbmTimerReset(Handler h) {
        ScbmHandler.getInstance().unregisterForScbmTimerReset(h);
    }

    public boolean isInScbm(int subId) {
        return ScbmHandler.getInstance().isInScbm(subId);
    }

    public boolean isExitScbmFeatureSupported() {
        return ScbmHandler.getInstance().isExitScbmFeatureSupported();
    }

    public boolean isScbmTimerCanceledForEmergency() {
        return ScbmHandler.getInstance().isScbmTimerCanceledForEmergency();
    }

    private void logd(String msg) {
        Rlog.d(LOG_TAG, "[" + mPhoneId +" ] " + msg);
    }

    private void loge(String msg) {
        Rlog.e(LOG_TAG, "[" + mPhoneId +" ] " + msg);
    }
}
