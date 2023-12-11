/*
 * Copyright (c) 2016, 2019 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */
/*
 * Not a Contribution, Apache license notifications and license are retained
 * for attribution purposes only.
 *
 * Copyright (C) 2015 The Android Open Source Project
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

import static android.telephony.TelephonyManager.RADIO_POWER_UNAVAILABLE;

import android.content.Context;
import android.os.AsyncResult;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.telephony.Rlog;
import android.telephony.SubscriptionManager;

import com.android.internal.telephony.Call;
import com.android.internal.telephony.CommandException;
import com.android.internal.telephony.dataconnection.DataEnabledSettings;
import com.android.internal.telephony.imsphone.ImsPhone;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.PhoneSwitcher;

import com.qti.extphone.Client;
import com.qti.extphone.ExtPhoneCallbackBase;
import com.qti.extphone.ExtTelephonyManager;
import com.qti.extphone.IExtPhoneCallback;
import com.qti.extphone.ServiceCallback;
import com.qti.extphone.Status;
import com.qti.extphone.Token;
import com.qualcomm.qcrilhook.QcRilHook;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class QtiPhoneSwitcher extends PhoneSwitcher {

    private QtiRilInterface mQtiRilInterface;

    private final int USER_INITIATED_SWITCH = 0;
    private final int NONUSER_INITIATED_SWITCH = 1;

    private static final int DEFAULT_PHONE_INDEX = 0;

    protected final String PROPERTY_TEMP_DDSSWITCH = "persist.vendor.radio.enable_temp_dds";
    private static final int RECONNECT_EXT_TELEPHONY_SERVICE_DELAY_MILLISECOND = 2000;

    private Client mClient;
    private ExtTelephonyManager mExtTelephonyManager;

    public QtiPhoneSwitcher(int maxActivePhones, Context context, Looper looper) {
        super (maxActivePhones, context, looper);
        mQtiRilInterface = QtiRilInterface.getInstance(context);
        mQtiRilInterface.registerForUnsol(this, EVENT_UNSOL_MAX_DATA_ALLOWED_CHANGED, null);
        mExtTelephonyManager = ExtTelephonyManager.getInstance(mContext);
        mExtTelephonyManager.connectService(mServiceCallback);
    }

    public static QtiPhoneSwitcher make(int maxActivePhones, Context context, Looper looper) {
        if (sPhoneSwitcher == null) {
            sPhoneSwitcher = new QtiPhoneSwitcher(maxActivePhones, context, looper);
        }

        return (QtiPhoneSwitcher)sPhoneSwitcher;
    }

    private ServiceCallback mServiceCallback = new ServiceCallback() {
        @Override
        public void onConnected() {
            log("ExtTelephony Service connected");
            mClient = mExtTelephonyManager.registerCallback(mContext.getPackageName(),
                    mCallback);
            log("Client = " + mClient);
        }
        @Override
        public void onDisconnected() {
            log("ExtTelephony Service disconnected...");
            mExtTelephonyManager.unRegisterCallback(mCallback);
            mClient = null;
            Message msg = obtainMessage(EVENT_RECONNECT_EXT_TELEPHONY_SERVICE);
            sendMessageDelayed(msg, RECONNECT_EXT_TELEPHONY_SERVICE_DELAY_MILLISECOND);
        }
    };

    protected IExtPhoneCallback mCallback = new ExtPhoneCallbackBase() {

        @Override
        public void onDdsSwitchCriteriaChange(int slotId,
                boolean telephonyDdsSwitch) throws RemoteException {
            log("ExtPhoneCallback: onDdsSwitchCriteriaChange: " + telephonyDdsSwitch +
                    " slotId: " + slotId);
            if (!SubscriptionManager.isValidPhoneId(slotId)) {
                return;
            }
            PhoneFactory.getPhone(slotId).setTelephonyTempDdsSwitch(telephonyDdsSwitch);
        }

        @Override
        public void onDdsSwitchRecommendation(int slotId,
                int recommendedSlotId) throws RemoteException {
            log("ExtPhoneCallback: onDdsSwitchRecommendation" +
                    "recommendedSlotId: " + recommendedSlotId);
            if (!SubscriptionManager.isValidPhoneId(recommendedSlotId)) {
                return;
            }

            mPreferredDataPhoneId = recommendedSlotId;
            mPreferredDataSubId.set(mSubscriptionController
                    .getSubIdUsingPhoneId(mPreferredDataPhoneId));

            Message message = Message.obtain(PhoneSwitcher.getInstance(),
                    EVENT_MODEM_COMMAND_DONE, recommendedSlotId);
            if (mHalCommandToUse == HAL_COMMAND_PREFERRED_DATA) {
                mRadioConfig.setPreferredDataModem(mPreferredDataPhoneId, message);
            }
        }
    };

    private void queryMaxDataAllowed() {
        mMaxDataAttachModemCount = mQtiRilInterface.getMaxDataAllowed();
    }

    private void handleUnsolMaxDataAllowedChange(Message msg) {
        if (msg == null ||  msg.obj == null) {
            log("Null data received in handleUnsolMaxDataAllowedChange");
            return;
        }
        ByteBuffer payload = ByteBuffer.wrap((byte[])msg.obj);
        payload.order(ByteOrder.nativeOrder());
        int rspId = payload.getInt();
        if ((rspId == QcRilHook.QCRILHOOK_UNSOL_MAX_DATA_ALLOWED_CHANGED)) {
            int response_size = payload.getInt();
            if (response_size < 0 ) {
                log("Response size is Invalid " + response_size);
                return;
            }
            mMaxDataAttachModemCount = payload.get();
            log(" Unsol Max Data Changed to: " + mMaxDataAttachModemCount);
        }
    }

    @Override
    public void handleMessage(Message msg) {
        final int ddsSubId = mSubscriptionController.getDefaultDataSubId();
        final int ddsPhoneId = mSubscriptionController.getPhoneId(ddsSubId);

        log("handle event - " + msg.what);
        AsyncResult ar = null;
        switch (msg.what) {
            case EVENT_RADIO_ON: {
                if (mQtiRilInterface.isServiceReady()) {
                    queryMaxDataAllowed();
                } else {
                    log("Oem hook service is not ready");
                }
                super.handleMessage(msg);
                break;
            }
            case EVENT_UNSOL_MAX_DATA_ALLOWED_CHANGED: {
                org.codeaurora.telephony.utils.AsyncResult asyncresult =
                        (org.codeaurora.telephony.utils.AsyncResult) msg.obj;
                if (asyncresult.result != null) {
                    handleUnsolMaxDataAllowedChange((Message) asyncresult.result);
                } else {
                    log("Error: empty result, EVENT_UNSOL_MAX_DATA_ALLOWED_CHANGED");
                }
                break;
            }
            case EVENT_RECONNECT_EXT_TELEPHONY_SERVICE: {
                log("EVENT_RECONNECT_EXT_TELEPHONY_SERVICE");
                mExtTelephonyManager.connectService(mServiceCallback);
                break;
            }
            default:
                super.handleMessage(msg);
        }
    }

    private int getPrimaryDataPhoneId() {
        int primaryDataPhoneId = SubscriptionManager.INVALID_PHONE_INDEX;
        if (SubscriptionManager.isValidSubscriptionId(mPrimaryDataSubId)) {
            primaryDataPhoneId = mSubscriptionController.getPhoneId(mPrimaryDataSubId);
        }
        return primaryDataPhoneId;
    }

    /*
     * Method to check if any of the calls are started
     */
    @Override
    protected boolean isPhoneInVoiceCall(Phone phone) {
        if (phone == null) {
            log("isPhoneInVoiceCall: phone is null");
            return false;
        }

        try {
            boolean tmpSwitchProperty = QtiTelephonyComponentFactory.getInstance().
                    getRil(DEFAULT_PHONE_INDEX).getPropertyValueBool(
                    PROPERTY_TEMP_DDSSWITCH, false);
            boolean dataDuringCallsEnabled = false;
            DataEnabledSettings dataEnabledSettings;
            if (phone instanceof ImsPhone) {
                dataEnabledSettings = phone.getDefaultPhone().getDataEnabledSettings();
            } else {
                dataEnabledSettings = phone.getDataEnabledSettings();
            }
            if (dataEnabledSettings != null) {
                dataDuringCallsEnabled = dataEnabledSettings.isDataAllowedInVoiceCall();
            }

            if (!tmpSwitchProperty && !dataDuringCallsEnabled) {
                log("isPhoneInVoiceCall: phoneid=" + phone.getPhoneId() + ", tmpSwitchProperty=" +
                        tmpSwitchProperty + ", dataDuringCallsEnabled=" + dataDuringCallsEnabled);
                return false;
            }
        } catch (RemoteException ex) {
            log("isPhoneInVoiceCall: Exception" + ex);
            return false;
        }

        return super.isPhoneInVoiceCall(phone);
    }

    private void informDdsToRil(int ddsSubId) {
        int ddsPhoneId = mSubscriptionController.getPhoneId(ddsSubId);

        if (!mQtiRilInterface.isServiceReady()) {
            log("Oem hook service is not ready yet");
            return;
        }
        if (!updateHalCommandToUse()) {
           log("sendRilCommands: waiting for HAL command update, may be radio is unavailable");
           return;
        }

        for (int i = 0; i < mActiveModemCount; i++) {
            log("InformDdsToRil rild= " + i + ", DDS=" + ddsPhoneId);
            if (isCallInProgress()) {
                mQtiRilInterface.qcRilSendDDSInfo(ddsPhoneId,
                        NONUSER_INITIATED_SWITCH, i);
            } else {
                mQtiRilInterface.qcRilSendDDSInfo(ddsPhoneId,
                        USER_INITIATED_SWITCH, i);
            }
        }
    }

    /*
     * Returns true if mPhoneIdInVoiceCall is set for active calls
     */
    private boolean isCallInProgress() {
        return SubscriptionManager.isValidPhoneId(mPhoneIdInVoiceCall);
    }

    @Override
    protected boolean onEvaluate(boolean requestsChanged, String reason) {
        if (!updateHalCommandToUse()) {
            log("Wait for HAL command update");
            return false;
        }

        return super.onEvaluate(requestsChanged, reason);
    }

    @Override
    protected void sendRilCommands(int phoneId) {
        if (!updateHalCommandToUse()) {
            log("Wait for HAL command update");
            return;
        }

        super.sendRilCommands(phoneId);
    }

    @Override
    protected void onDdsSwitchResponse(AsyncResult ar) {
        boolean commandSuccess = ar != null && ar.exception == null;
        int phoneId = (int) ar.userObj;
        if (mEmergencyOverride != null) {
            log("Emergency override result sent = " + commandSuccess);
            mEmergencyOverride.sendOverrideCompleteCallbackResultAndClear(commandSuccess);
            // Do not retry , as we do not allow changes in onEvaluate during an emergency
            // call. When the call ends, we will start the countdown to remove the override.
        } else if (!commandSuccess) {
            log("onDdsSwitchResponse: DDS switch failed. with exception " + ar.exception);
            //Remove this check once these errors are passed through dds hal response
            if (isAnyVoiceCallActiveOnDevice()) {
                mCurrentDdsSwitchFailure.get(phoneId).add(CommandException.Error.
                        OP_NOT_ALLOWED_DURING_VOICE_CALL);
                log("onDdsSwitchResponse: Wait for call end indication");
                return;
            } else if (!isSimApplicationReady(phoneId)) {
               /* If there is a attach failure due to sim not ready then
                  hold the retry until sim gets ready */
                mCurrentDdsSwitchFailure.get(phoneId).add(CommandException.Error.INVALID_SIM_STATE);
                log("onDdsSwitchResponse: Wait for SIM to get READY");
                return;
            }
            log("onDdsSwitchResponse: Scheduling DDS switch retry");
            sendMessageDelayed(Message.obtain(this, EVENT_MODEM_COMMAND_RETRY,
                    phoneId), MODEM_COMMAND_RETRY_PERIOD_MS);
            return;
        }
        if (commandSuccess) log("onDdsSwitchResponse: DDS switch success on phoneId = " + phoneId);
        mCurrentDdsSwitchFailure.get(phoneId).clear();
        if (isSmartTempDdsSwitchSupported()) {
            sendDataDuringVoiceCallInfo();
        }
        // Notify all registrants
        mActivePhoneRegistrants.notifyRegistrants();
        notifyPreferredDataSubIdChanged();
    }

    private boolean isSmartTempDdsSwitchSupported() {
        for (int i = 0; i < mActiveModemCount; i++) {
            if (!PhoneFactory.getPhone(i).getSmartTempDdsSwitchSupported()) return false;
        }
        return true;
    }

    private void sendDataDuringVoiceCallInfo() {
        int primaryDataPhoneId = getPrimaryDataPhoneId();
        if (!SubscriptionManager.isValidPhoneId(primaryDataPhoneId)
                || mExtTelephonyManager == null) {
            return;
        }

        // Always send data during calls UI info as false on DDS
        mExtTelephonyManager.sendUserPreferenceForDataDuringVoiceCall(
                primaryDataPhoneId, false, mClient);

        // for all nonDDS subs, send data during calls UI info as false if DDS mobile data is off,
        // otherwise send user selected data during calls UI info.
        boolean isDataEnableOnPrimaryDataSub = PhoneFactory.getPhone(primaryDataPhoneId)
                .getDataEnabledSettings().isDataEnabled();
        for (int i = 0; i < mActiveModemCount; i++) {
            if (i == primaryDataPhoneId) continue;
            if (!isDataEnableOnPrimaryDataSub) {
                mExtTelephonyManager.sendUserPreferenceForDataDuringVoiceCall(
                        i, false, mClient);
            } else {
                mExtTelephonyManager.sendUserPreferenceForDataDuringVoiceCall(
                        i, PhoneFactory.getPhone(i).getDataEnabledSettings().
                        isDataAllowedInVoiceCall(), mClient);
            }
        }
    }

    private boolean updateHalCommandToUse() {
        if (mHalCommandToUse == HAL_COMMAND_UNKNOWN) {
            boolean isRadioAvailable = true;
            for (int i = 0; i < mActiveModemCount; i++) {
                isRadioAvailable &= PhoneFactory.getPhone(i).mCi.getRadioState()
                                != RADIO_POWER_UNAVAILABLE;
            }
            if (isRadioAvailable) {
                log("update HAL command");
                mHalCommandToUse = mRadioConfig.isSetPreferredDataCommandSupported()
                        ? HAL_COMMAND_PREFERRED_DATA : HAL_COMMAND_ALLOW_DATA;
                return true;
            } else {
                log("radio is unavailable");
            }
        }
        return mHalCommandToUse != HAL_COMMAND_UNKNOWN;
    }
}
