/*
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */

package org.codeaurora.ims;

import android.os.Build;
import android.os.ServiceManager;
import android.os.SystemProperties;
import androidx.annotation.VisibleForTesting;

import com.qualcomm.ims.utils.Log;

/*
 * This is the factory class that creates a HIDL or AIDL IImsRadio HAL instance
 * or a default Notsupported HAL if the target does not support telephony/ril
 */

public final class ImsRadioHalFactory {

    private static final String TAG = "ImsRadioHalFactory";

    public static final IImsRadio newImsRadioHal(IImsRadioResponse respCallback,
                                              IImsRadioIndication indCallback,
                                              int phoneId) {
        if (SystemProperties.getBoolean("ro.radio.noril", false)) {
            Log.i(TAG, "Initialize default HAL as target does not support ril");
            return new ImsRadioNotSupportedHal();
        } else if (SystemProperties.getInt("ro.board.api_level", 0) >= Build.VERSION_CODES.S &&
                ImsRadioAidl.isAidlAvailable(phoneId)) {
            Log.i(TAG, "Initializing IImsRadio AIDL");
            return new ImsRadioAidl(respCallback, indCallback, phoneId);
        } else {
            Log.i(TAG, "Initializing IImsRadio HIDL as AIDL is not available");
            return new ImsRadioHidl(respCallback, indCallback, phoneId);
        }
    }
}
