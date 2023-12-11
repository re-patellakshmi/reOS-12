/*
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */

package com.qti.phone;

import android.content.Context;
import android.os.Build;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.util.Log;
import android.telephony.TelephonyManager;

/*
 * This is the factory class that creates a HIDL or AIDL IQtiRadio HAL instance
 */
public final class QtiRadioFactory {
    private static final String TAG = "QtiRadioFactory";
    private static IQtiRadioConnectionInterface[] mQtiRadioHidl, mQtiRadioAidl,
            mQtiRadioNotSupportedHal;
    private static Context mContext;
    private static int mPhoneCount;

    public static final IQtiRadioConnectionInterface[] makeQtiRadio(Context context) {
        mContext = context;
        mPhoneCount = getPhoneCount();
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(
                Context.TELEPHONY_SERVICE);
        boolean noRil = SystemProperties.getBoolean("ro.radio.noril", false);
        boolean isCellularSupported = !noRil &&
                (tm.isVoiceCapable() || tm.isSmsCapable() || tm.isDataCapable());
        if (!isCellularSupported) {
            Log.i(TAG, "RIL is not supported");
            return makeQtiRadioNotSupportedHal();
        } else if (isAidlAvailable()) {
            return makeQtiRadioAidl();
        } else {
            return makeQtiRadioHidl();
        }
    }

    private static IQtiRadioConnectionInterface[] makeQtiRadioNotSupportedHal() {
        mQtiRadioNotSupportedHal = new QtiRadioNotSupportedHal[mPhoneCount];
        for (int i = 0; i < mPhoneCount; i++) {
            mQtiRadioNotSupportedHal[i] = new QtiRadioNotSupportedHal();
        }
        return mQtiRadioNotSupportedHal;
    }

    private static IQtiRadioConnectionInterface[] makeQtiRadioAidl() {
        mQtiRadioAidl = new QtiRadioAidl[mPhoneCount];
        for (int i = 0; i < mPhoneCount; i++) {
            mQtiRadioAidl[i] = new QtiRadioAidl(i);
        }
        return mQtiRadioAidl;
    }

    private static IQtiRadioConnectionInterface[] makeQtiRadioHidl() {
        mQtiRadioHidl = new QtiRadioHidl[mPhoneCount];
        for (int i = 0; i < mPhoneCount; i++) {
            mQtiRadioHidl[i] = new QtiRadioHidl(i);
        }
        return mQtiRadioHidl;
    }

    static boolean isAidlAvailable() {
        if (SystemProperties.getInt("ro.board.api_level", 0) >= Build.VERSION_CODES.S) {
            try {
                return ServiceManager.isDeclared(
                        "vendor.qti.hardware.radio.qtiradio.IQtiRadioStable/slot1");
            } catch (SecurityException e) {
                Log.e(TAG, "Security exception while call into AIDL: "+ e);
            }
        }
        return false;
    }

    private static int getPhoneCount() {
        TelephonyManager tm = (TelephonyManager) mContext.
                getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getActiveModemCount();
    }
}
