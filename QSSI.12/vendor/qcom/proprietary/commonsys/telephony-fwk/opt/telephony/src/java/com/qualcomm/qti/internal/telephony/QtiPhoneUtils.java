/*
 * Copyright (c) 2018 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 *
 * Not a Contribution.
 * Apache license notifications and license are retained
 * for attribution purposes only.
 */
/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qualcomm.qti.internal.telephony;

import android.content.Context;
import android.os.Message;
import android.os.AsyncResult;
import android.os.RemoteException;
import android.telephony.Rlog;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.ImsiEncryptionInfo;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.RIL;
import com.android.internal.telephony.SmsResponse;

import com.qualcomm.qti.internal.telephony.QtiCarrierInfoManager.QtiCarrierInfoResponse;
import com.qti.extphone.Client;
import com.qti.extphone.ExtPhoneCallbackBase;
import com.qti.extphone.ExtTelephonyManager;
import com.qti.extphone.IExtPhoneCallback;
import com.qti.extphone.QRadioResponseInfo;
import com.qti.extphone.ServiceCallback;
import com.qti.extphone.SmsResult;
import com.qti.extphone.Status;
import com.qti.extphone.Token;

import java.util.HashMap;

public class QtiPhoneUtils {
    private static final String TAG = "QtiPhoneUtils";

    private Context mContext;
    private static QtiPhoneUtils sInstance;
    private ExtTelephonyManager mExtTelephonyManager;
    private Client mClient;
    private final HashMap<Integer, Message> mPendingRequests = new HashMap<>();
    private QtiCarrierInfoResponse mQtiCarrierInfoResponse;

    static QtiPhoneUtils init(Context context) {
        synchronized (QtiPhoneUtils.class) {
            if (sInstance == null) {
                sInstance = new QtiPhoneUtils(context);
            }
        }
        return sInstance;
    }

    public static QtiPhoneUtils getInstance() {
        synchronized (QtiPhoneUtils.class) {
            if (sInstance == null) {
                throw new RuntimeException("QtiPhoneUtils was not initialized!");
            }
            return sInstance;
        }
    }

    private QtiPhoneUtils(Context context) {
        mContext = context;
        mExtTelephonyManager = ExtTelephonyManager.getInstance(context);
        mExtTelephonyManager.connectService(mExtTelManagerServiceCallback);
    }

    private ServiceCallback mExtTelManagerServiceCallback = new ServiceCallback() {

        @Override
        public void onConnected() {
            mClient = mExtTelephonyManager.registerCallback(
                    mContext.getPackageName(), mExtPhoneCallback);
            Rlog.d(TAG, "mExtTelManagerServiceCallback: service connected " + mClient);
        }

        @Override
        public void onDisconnected() {
            Rlog.d(TAG, "mExtTelManagerServiceCallback: service disconnected");
        }
    };

    protected IExtPhoneCallback mExtPhoneCallback = new ExtPhoneCallbackBase() {
        @Override
        public void sendCdmaSmsResponse(int slotId, Token token, Status status, SmsResult sms) {
            Message msg = null;

            Rlog.d(TAG, "sendCdmaSmsResponse " + token.get());
            synchronized (mPendingRequests) {
                msg =  mPendingRequests.get(token.get());
            }
            SmsResponse ret = new SmsResponse(sms.getMessageRef(),
                    sms.getAckPDU(), sms.getErrorCode());
            if (status.get() == Status.SUCCESS) {
                if (msg != null) {
                    AsyncResult.forMessage(msg, ret, null);
                    msg.sendToTarget();
                }
            }
            synchronized (mPendingRequests) {
                mPendingRequests.remove(token.get());
            }
        }

        @Override
        public void getQtiRadioCapabilityResponse(int slotId, Token token, Status status, int raf) {
            Message msg = null;

            Rlog.d(TAG, "getQtiRadioCapabilityResponse " + token.get());
            synchronized (mPendingRequests) {
                msg =  mPendingRequests.get(token.get());
            }
            if (status.get() == Status.SUCCESS) {
                if (msg != null) {
                    AsyncResult.forMessage(msg, raf, null);
                    msg.sendToTarget();
                }
            }
            synchronized (mPendingRequests) {
                mPendingRequests.remove(token.get());
            }
        }

        @Override
        public void setCarrierInfoForImsiEncryptionResponse(int slotId, Token token,
                QRadioResponseInfo info) {
            mQtiCarrierInfoResponse.setCarrierInfoForImsiEncryptionResponse(info);
        }
    };

    boolean sendCdmaSms(int phoneId, byte[] pdu, Message result, boolean expectMore) {
        if (!mExtTelephonyManager.isServiceConnected()) {
            return false;
        }
        int serial = 0;
        Rlog.d(TAG, "sendCdmaSms, expectMore=" + expectMore);

        try {
            Token token = mExtTelephonyManager.sendCdmaSms(
                    phoneId,pdu, expectMore, mClient);
            serial = token.get();
        } catch (RuntimeException e) {
            Rlog.e(TAG, "Exception sendCdmaSms " + e);
        }
        synchronized (mPendingRequests) {
            mPendingRequests.put(serial, result);
        }
        return true;
    }

    boolean getQtiRadioCapability(int phoneId, Message response) throws RemoteException {
        if (!mExtTelephonyManager.isServiceConnected()) {
            return false;
        }
        int serial = 0;
        Rlog.d(TAG, "getQtiRadioCapability, response=" + response);
        Token token = mExtTelephonyManager.getQtiRadioCapability(phoneId, mClient);
        serial = token.get();
        synchronized (mPendingRequests) {
            mPendingRequests.put(serial, response);
        }
        return true;
    }

    void setCarrierInfoForImsiEncryption(int phoneId, ImsiEncryptionInfo imsiEncryptionInfo,
                QtiCarrierInfoResponse response) {
        if (!mExtTelephonyManager.isServiceConnected()) {
            return;
        }
        mQtiCarrierInfoResponse = response;
        int tokenSerial = 0;
        Rlog.d(TAG, "setCarrierInfoForImsiEncryption, phoneId=" + phoneId);
        try {
            Token token = mExtTelephonyManager.setCarrierInfoForImsiEncryption(
                    phoneId, imsiEncryptionInfo, mClient);
            tokenSerial = token.get();
        } catch (RuntimeException e) {
            Rlog.e(TAG, "Exception setCarrierInfoForImsiEncryption " + e);
        }
        synchronized (mPendingRequests) {
            mPendingRequests.put(tokenSerial, new Message());
        }
    }

    public int getPhoneCount() {
        return ((TelephonyManager) mContext.getSystemService(
                Context.TELEPHONY_SERVICE)).getPhoneCount();
    }

    public boolean isValidPhoneId(int phoneId) {
        return phoneId >= 0 && phoneId < getPhoneCount();
    }

    public static boolean putIntAtIndex(android.content.ContentResolver cr,
            String name, int index, int value) {
        String data = "";
        String valArray[] = null;
        String v = android.provider.Settings.Global.getString(cr, name);

        if (index == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("putIntAtIndex index == MAX_VALUE index=" + index);
        }
        if (index < 0) {
            throw new IllegalArgumentException("putIntAtIndex index < 0 index=" + index);
        }
        if (v != null) {
            valArray = v.split(",");
        }

        // Copy the elements from valArray till index
        for (int i = 0; i < index; i++) {
            String str = "";
            if ((valArray != null) && (i < valArray.length)) {
                str = valArray[i];
            }
            data = data + str + ",";
        }

        data = data + value;

        // Copy the remaining elements from valArray if any.
        if (valArray != null) {
            for (int i = index+1; i < valArray.length; i++) {
                data = data + "," + valArray[i];
            }
        }
        return android.provider.Settings.Global.putString(cr, name, data);
    }


    public static int getIntAtIndex(android.content.ContentResolver cr,
            String name, int index)
            throws android.provider.Settings.SettingNotFoundException {
        String v = android.provider.Settings.Global.getString(cr, name);
        if (v != null) {
            String valArray[] = v.split(",");
            if ((index >= 0) && (index < valArray.length) && (valArray[index] != null)) {
                try {
                    return Integer.parseInt(valArray[index]);
                } catch (NumberFormatException e) {
                    //Log.e(TAG, "Exception while parsing Integer: ", e);
                }
            }
        }
        throw new android.provider.Settings.SettingNotFoundException(name);
    }

    public int getCurrentUiccCardProvisioningStatus(int slotId) {
        int currentStatus = QtiUiccCardProvisioner.UiccProvisionStatus.INVALID_STATE;
        if (!isValidPhoneId(slotId)) {
            return currentStatus;
        }

        Phone phone = PhoneFactory.getPhone(slotId);
        if (phone == null)  {
            return currentStatus;
        }

        if (phone.getHalVersion().greaterOrEqual(RIL.RADIO_HAL_VERSION_1_5)) {
            int[] subIds = QtiSubscriptionController.getInstance().getSubId(slotId);
            if ((subIds != null) && (subIds.length != 0)) {
                SubscriptionManager subscriptionManager = (SubscriptionManager) mContext
                        .getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                SubscriptionInfo si = subscriptionManager.getActiveSubscriptionInfo(subIds[0]);
                currentStatus = si.areUiccApplicationsEnabled() ?
                        QtiUiccCardProvisioner.UiccProvisionStatus.PROVISIONED :
                        QtiUiccCardProvisioner.UiccProvisionStatus.NOT_PROVISIONED;
            } else {
                currentStatus = QtiUiccCardProvisioner.UiccProvisionStatus.CARD_NOT_PRESENT;
            }
        } else {
            currentStatus =  QtiUiccCardProvisioner.getInstance().
                    getCurrentUiccCardProvisioningStatus(slotId);
        }
        Rlog.d(TAG, " getCurrentUiccCardProvisioningStatus, state[" +
                slotId + "] = " + currentStatus);
        return currentStatus;
    }
}
