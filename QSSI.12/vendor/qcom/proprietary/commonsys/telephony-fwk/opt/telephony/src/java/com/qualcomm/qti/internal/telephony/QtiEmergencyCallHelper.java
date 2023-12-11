/*
 * Copyright (c) 2015, 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */

package com.qualcomm.qti.internal.telephony;

import android.content.Context;
import android.os.SystemProperties;
import android.telephony.PhoneNumberUtils;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.Call;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.imsphone.ImsPhone;

/**
 * This class has logic to return the primary stack phone id or the phone id
 * most suitable for emergency call
 */
public class QtiEmergencyCallHelper {
    private static final String LOG_TAG = "QtiEmergencyCallHelper";
    private static QtiEmergencyCallHelper sInstance = null;
    private static final int INVALID = -1;
    private static final int PRIMARY_STACK_MODEMID = 0;
    private static final int PROVISIONED = 1;
    private static final String ALLOW_ECALL_ENHANCEMENT_PROPERTY =
        "persist.vendor.radio.enhance_ecall";

    /**
      * Pick the best possible phoneId for emergency call
      * Following are the conditions applicable when deciding the sub/phone for dial.
      * 1. In Dual Standby/Dual Active mode, consider the sub(ie Phone), which is in
      *    OFFHOOK state.
      * 2. If a sub is in SCBM state, that sub will be chosen to place the Ecall.
      * 3. Place Ecall on a sub(i.e Phone) whichever has the emergency number in it's
      *    ecclist irrespective of service state.
      * 4. If emergency number is available in both SUB's ecclist and both subs are
      *    activated and both subs are In service/Limited service/Out of service then
      *    place call on voice pref sub.
      * 5. If both subs are not activated(i.e NO SIM/PIN/PUK lock state) then choose
      *    the sub mapped to primary stack.
      */
    public static int getPhoneIdForECall(Context context) {
        QtiSubscriptionController scontrol = QtiSubscriptionController.getInstance();

        int voicePhoneId = scontrol.getPhoneId(scontrol.getDefaultVoiceSubId());
        int phoneId = INVALID;

        TelephonyManager tm = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int phoneCount = tm.getPhoneCount();

        if (!isDeviceInSingleStandby(context)) {
            // In dual active mode, the below algorithm will take effect
            // The SUB with the ACTIVE call receives the highest priority
            // The SUB with a HELD call receives second priority
            // If both SUBs have a HELD call, then the first SUB is selected
            if (tm.isConcurrentCallsPossible()) {
                for (Phone phone: PhoneFactory.getPhones()) {
                    if (phone.getState() == PhoneConstants.State.OFFHOOK) {
                        Phone imsPhone = phone.getImsPhone();
                        if (imsPhone == null) {
                            Log.w(LOG_TAG, "ImsPhone should not be null");
                            continue;
                        }
                        if (imsPhone.getForegroundCall().getState() == Call.State.ACTIVE) {
                            return phone.getPhoneId();
                        }
                        if (imsPhone.getBackgroundCall().getState() == Call.State.HOLDING &&
                                phoneId == INVALID) {
                            phoneId = phone.getPhoneId();
                        }
                    }
                }
                if (phoneId != INVALID) {
                    return phoneId;
                }
            }

            // If there is active call, place on same SUB when concurrent calls are not supported
            // or if ImsPhone is null
            for (Phone phone: PhoneFactory.getPhones()) {
                if (phone.getState() == PhoneConstants.State.OFFHOOK) {
                    Log.d(LOG_TAG, "Call already active on phoneId: " + phone.getPhoneId());
                    // Since already call is active on phone, send ecall also on
                    // same phone to avoid delay in emergency call setup by modem
                    return phone.getPhoneId();
                }
            }
        }
        final int ddsPhoneId = scontrol.getPhoneId(scontrol.getDefaultDataSubId());
        boolean hasUserSetDefaultVoiceSub = false;
        boolean hasUserSetDefaultDataSub = false;
        for (int phId = 0; phId < phoneCount; phId++) {
            Phone phone = PhoneFactory.getPhone(phId);
            int ss = phone.getServiceState().getState();
            if (ss == ServiceState.STATE_IN_SERVICE) {
                //If sub is in service and scbm is enabled on the phone, outgoing emergency
                //call goes over that phone.
                if (isInScbm(phId)) {
                    Log.i(LOG_TAG, "In Sms Callback Mode on phoneId: " + phId);
                    return phId;
                }
                phoneId = phId;
                // If call setting is preferred, chooses the default outgoing phone account,
                // otherwise, goes over DDS phone.
                if (voicePhoneId != INVALID) {
                    if (phoneId == voicePhoneId) hasUserSetDefaultVoiceSub = true;
                } else {
                    if (phoneId == ddsPhoneId) hasUserSetDefaultDataSub = true;
                }
            }
        }
        // If no sub in service is in scbm, only then choose the default voice or data sub.
        // If no default voice or data sub, it will choose the last sub in service.
        if (hasUserSetDefaultVoiceSub) {
            phoneId = voicePhoneId;
        } else if (hasUserSetDefaultDataSub) {
            phoneId = ddsPhoneId;
        }
        Log.d(LOG_TAG, "Voice phoneId in service = "+ phoneId);

        if (phoneId == INVALID) {
            for (int phId = 0; phId < phoneCount; phId++) {
                Phone phone = PhoneFactory.getPhone(phId);
                int ss = phone.getServiceState().getState();
                if (phone.getServiceState().isEmergencyOnly()) {
                    //If sub is emergency only and scbm is enabled on the phone, outgoing emergency
                    //call goes over that phone.
                    if (isInScbm(phId)) {
                        Log.i(LOG_TAG, "In Sms Callback Mode on phoneId: " + phId);
                        return phId;
                    }
                    phoneId = phId;
                    if (phoneId == voicePhoneId) hasUserSetDefaultVoiceSub = true;
                }
            }
            // If no emergency only sub is in scbm, only then choose the default voice sub.
            // If no default voice sub, it will choose the last emergency only phone.
            if (hasUserSetDefaultVoiceSub) {
                phoneId = voicePhoneId;
            }
            Log.d(LOG_TAG, "Voice phoneId in Limited service = "+ phoneId);
        }

        if (phoneId == INVALID) {
            phoneId = getPrimaryStackPhoneId(context);
            for (int phId = 0; phId < phoneCount; phId++) {
                Phone phone = PhoneFactory.getPhone(phId);
                QtiPhoneUtils qtiPhoneUtils =
                        QtiPhoneUtils.getInstance();

                if ((tm.getSimState(phId) == TelephonyManager.SIM_STATE_READY)
                        // phone id can be mapped to slot id
                        && (qtiPhoneUtils.getCurrentUiccCardProvisioningStatus(phId)
                        == PROVISIONED)) {
                    //If scbm is enabled on the phone, outgoing emergency
                    //call goes over that phone.
                    if (isInScbm(phId)) {
                        Log.i(LOG_TAG, "In Sms Callback Mode on phoneId: " + phId);
                        return phId;
                    }
                    phoneId = phId;
                    if (phoneId == voicePhoneId) hasUserSetDefaultVoiceSub = true;
                }
            }
            // If no READY sub is in scbm, only then choose the default outgoing phone account.
            // If no default voice sub, it will choose the last READY SIM.
            if (hasUserSetDefaultVoiceSub) {
                phoneId = voicePhoneId;
            }
        }
        Log.d(LOG_TAG, "Voice phoneId in service = "+ phoneId +
                " preferred phoneId = " + voicePhoneId +
                " hasUserSetDefaultVoiceSub = " + hasUserSetDefaultVoiceSub +
                " hasUserSetDefaultDataSub = " + hasUserSetDefaultDataSub);

        return phoneId;
    }

    //checks if the Phone is in SCBM
    private static boolean isInScbm(int phoneId) {
        return ScbmHandler.getInstance().isInScbm(phoneId);
    }

    public static int getPrimaryStackPhoneId(Context context) {
        String modemUuId = null;
        Phone phone = null;
        int primayStackPhoneId = INVALID;
        int phoneCount = ((TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE)).getPhoneCount();

        for (int i = 0; i < phoneCount; i++) {

            phone = PhoneFactory.getPhone(i);
            if (phone == null) continue;

            Log.d(LOG_TAG, "Logical Modem id: " + phone.getModemUuId() + " phoneId: " + i);
            modemUuId = phone.getModemUuId();
            if ((modemUuId == null) || (modemUuId.length() <= 0) ||
                    modemUuId.isEmpty()) {
                continue;
            }
            // Select the phone id based on modemUuid
            // if modemUuid is 0 for any phone instance, primary stack is mapped
            // to it so return the phone id as the primary stack phone id.
            if (Integer.parseInt(modemUuId) == PRIMARY_STACK_MODEMID) {
                primayStackPhoneId = i;
                Log.d(LOG_TAG, "Primay Stack phone id: " + primayStackPhoneId + " selected");
                break;
            }
        }

        // never return INVALID
        if( primayStackPhoneId == INVALID){
            Log.d(LOG_TAG, "Returning default phone id");
            primayStackPhoneId = 0;
        }

        return primayStackPhoneId;
    }

    public static boolean isDeviceInSingleStandby(Context context) {
        if (!SystemProperties.getBoolean(ALLOW_ECALL_ENHANCEMENT_PROPERTY, true)) {
            Log.d(LOG_TAG, "persist.vendor.radio.enhance_ecall not enabled" );
            return false;
        }

        TelephonyManager tm =
               (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int phoneCnt = tm.getPhoneCount();

        // If phone count is 1, then it is single sim device.
        // return true
        if (phoneCnt == 1)
            return true;

        for (int phoneId = 0; phoneId < phoneCnt; phoneId++) {
            QtiPhoneUtils qtiPhoneUtils =
                        QtiPhoneUtils.getInstance();

            if ((tm.getSimState(phoneId) != TelephonyManager.SIM_STATE_READY) ||
                    (qtiPhoneUtils.getCurrentUiccCardProvisioningStatus(phoneId)
                    != PROVISIONED)) {
                Log.d(LOG_TAG, "modem is in single standby mode" );
                return true;
            }
        }

        Log.d(LOG_TAG, "modem is in dual standby mode" );
        return false;
    }
}
