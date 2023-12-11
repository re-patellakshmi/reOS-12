/*
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */

package com.qualcomm.qti.internal.telephony;

import android.telephony.NetworkRegistrationInfo;
import android.util.Log;

import com.android.internal.telephony.CellularNetworkService;

/**
 * This class is used purely for converting an instance of HAL RegStateResult to an instance of
 * {@link NetworkRegistrationInfo}.
 */

public class QtiCellularNetworkService extends CellularNetworkService {
    private static final String TAG = "QtiCellularNetworkService";

    private class QtiCellularNetworkProvider extends CellularNetworkServiceProvider {
        protected NetworkRegistrationInfo getRegistrationStateFromResult(Object result,
                                                                         int domain) {
            NetworkRegistrationInfo info = super.getRegistrationStateFromResult(result, domain);
            Log.d(TAG, "Domain: " + domain + ", NRI: " + info);
            return info;
        }
    }

    protected QtiCellularNetworkProvider mProvider;

    public QtiCellularNetworkService() {
        mProvider = new QtiCellularNetworkProvider();
    }

    /**
     * Convert HAL RegStateResult, which is received as a response to Voice/Data registration state
     * query from RIL, to {@link NetworkRegistrationInfo}
     *
     * @param result Instance of RegStateResult for IRadio HAL 1.5 and beyond, or of
     *               VoiceRegStateResult/DataRegStateResult for the older versions.
     *
     * @param domain {@link NetworkRegistrationInfo#DOMAIN_CS} if this is for voice,
     *               {@link NetworkRegistrationInfo#DOMAIN_PS} if this is for data
     *
     * @return an instance of {@link NetworkRegistrationInfo}
     */
    NetworkRegistrationInfo getRegistrationStateFromResult(Object result, int domain) {
        Log.d(TAG, "getRegistrationStateFromResult, domain: " + domain);
        return mProvider.getRegistrationStateFromResult(result, domain);
    }
}
