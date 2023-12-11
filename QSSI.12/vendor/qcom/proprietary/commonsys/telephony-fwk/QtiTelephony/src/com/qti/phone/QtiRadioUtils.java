/*
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */

package com.qti.phone;

import org.codeaurora.telephony.utils.EnhancedRadioCapabilityResponse;

class QtiRadioUtils {
    protected static int convertToQtiNetworkTypeBitMask(int raf) {
        int networkTypeRaf = 0;

        if ((raf & vendor.qti.hardware.radio.qtiradio.V2_6.RadioAccessFamily.GSM) != 0) {
            networkTypeRaf |= EnhancedRadioCapabilityResponse.NETWORK_TYPE_BITMASK_GSM;
        }
        if ((raf & vendor.qti.hardware.radio.qtiradio.V2_6.RadioAccessFamily.GPRS) != 0) {
            networkTypeRaf |= EnhancedRadioCapabilityResponse.NETWORK_TYPE_BITMASK_GPRS;
        }
        if ((raf & vendor.qti.hardware.radio.qtiradio.V2_6.RadioAccessFamily.EDGE) != 0) {
            networkTypeRaf |= EnhancedRadioCapabilityResponse.NETWORK_TYPE_BITMASK_EDGE;
        }
        // convert both IS95A/IS95B to CDMA as network mode doesn't support CDMA
        if ((raf & vendor.qti.hardware.radio.qtiradio.V2_6.RadioAccessFamily.IS95A) != 0) {
            networkTypeRaf |= EnhancedRadioCapabilityResponse.NETWORK_TYPE_BITMASK_CDMA;
        }
        if ((raf & vendor.qti.hardware.radio.qtiradio.V2_6.RadioAccessFamily.IS95B) != 0) {
            networkTypeRaf |= EnhancedRadioCapabilityResponse.NETWORK_TYPE_BITMASK_CDMA;
        }
        if ((raf & vendor.qti.hardware.radio.qtiradio.V2_6.RadioAccessFamily.ONE_X_RTT) != 0) {
            networkTypeRaf |= EnhancedRadioCapabilityResponse.NETWORK_TYPE_BITMASK_1xRTT;
        }
        if ((raf & vendor.qti.hardware.radio.qtiradio.V2_6.RadioAccessFamily.EVDO_0) != 0) {
            networkTypeRaf |= EnhancedRadioCapabilityResponse.NETWORK_TYPE_BITMASK_EVDO_0;
        }
        if ((raf & vendor.qti.hardware.radio.qtiradio.V2_6.RadioAccessFamily.EVDO_A) != 0) {
            networkTypeRaf |= EnhancedRadioCapabilityResponse.NETWORK_TYPE_BITMASK_EVDO_A;
        }
        if ((raf & vendor.qti.hardware.radio.qtiradio.V2_6.RadioAccessFamily.EVDO_B) != 0) {
            networkTypeRaf |= EnhancedRadioCapabilityResponse.NETWORK_TYPE_BITMASK_EVDO_B;
        }
        if ((raf & vendor.qti.hardware.radio.qtiradio.V2_6.RadioAccessFamily.EHRPD) != 0) {
            networkTypeRaf |= EnhancedRadioCapabilityResponse.NETWORK_TYPE_BITMASK_EHRPD;
        }
        if ((raf & vendor.qti.hardware.radio.qtiradio.V2_6.RadioAccessFamily.HSUPA) != 0) {
            networkTypeRaf |= EnhancedRadioCapabilityResponse.NETWORK_TYPE_BITMASK_HSUPA;
        }
        if ((raf & vendor.qti.hardware.radio.qtiradio.V2_6.RadioAccessFamily.HSDPA) != 0) {
            networkTypeRaf |= EnhancedRadioCapabilityResponse.NETWORK_TYPE_BITMASK_HSDPA;
        }
        if ((raf & vendor.qti.hardware.radio.qtiradio.V2_6.RadioAccessFamily.HSPA) != 0) {
            networkTypeRaf |= EnhancedRadioCapabilityResponse.NETWORK_TYPE_BITMASK_HSPA;
        }
        if ((raf & vendor.qti.hardware.radio.qtiradio.V2_6.RadioAccessFamily.HSPAP) != 0) {
            networkTypeRaf |= EnhancedRadioCapabilityResponse.NETWORK_TYPE_BITMASK_HSPAP;
        }
        if ((raf & vendor.qti.hardware.radio.qtiradio.V2_6.RadioAccessFamily.UMTS) != 0) {
            networkTypeRaf |= EnhancedRadioCapabilityResponse.NETWORK_TYPE_BITMASK_UMTS;
        }
        if ((raf & vendor.qti.hardware.radio.qtiradio.V2_6.RadioAccessFamily.TD_SCDMA) != 0) {
            networkTypeRaf |= EnhancedRadioCapabilityResponse.NETWORK_TYPE_BITMASK_TD_SCDMA;
        }
        if ((raf & vendor.qti.hardware.radio.qtiradio.V2_6.RadioAccessFamily.LTE) != 0) {
            networkTypeRaf |= EnhancedRadioCapabilityResponse.NETWORK_TYPE_BITMASK_LTE;
        }
        if ((raf & vendor.qti.hardware.radio.qtiradio.V2_6.RadioAccessFamily.LTE_CA) != 0) {
            networkTypeRaf |= EnhancedRadioCapabilityResponse.NETWORK_TYPE_BITMASK_LTE_CA;
        }
        if ((raf & vendor.qti.hardware.radio.qtiradio.V2_6.RadioAccessFamily.NR_NSA) != 0) {
            networkTypeRaf |= EnhancedRadioCapabilityResponse.NETWORK_TYPE_BITMASK_NR_NSA;
        }
        if ((raf & vendor.qti.hardware.radio.qtiradio.V2_6.RadioAccessFamily.NR_SA) != 0) {
            networkTypeRaf |= EnhancedRadioCapabilityResponse.NETWORK_TYPE_BITMASK_NR_SA;
        }
        return (networkTypeRaf == 0) ? EnhancedRadioCapabilityResponse.
                NETWORK_TYPE_UNKNOWN : networkTypeRaf;
    }
}