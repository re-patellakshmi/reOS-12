/*
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */

package com.qti.phone;

import com.qti.extphone.DcParam;
import com.qti.extphone.NrConfig;
import com.qti.extphone.NrConfigType;
import com.qti.extphone.NrIconType;
import com.qti.extphone.QtiCallForwardInfo;
import com.qti.extphone.QRadioResponseInfo;
import com.qti.extphone.QtiImeiInfo;
import com.qti.extphone.SignalStrength;
import com.qti.extphone.SmsResult;
import com.qti.extphone.Status;
import com.qti.extphone.Token;
import com.qti.extphone.BearerAllocationStatus;
import com.qti.extphone.UpperLayerIndInfo;

public interface IQtiRadioConnectionCallback {
    void onNrIconType(int slotId, Token token, Status status,
                      NrIconType nrIconType);

    /**
     * Response to enableEndc
     * @param - slotId
     * @param - token is the same token which is recived in enableEndc
     * @param - status SUCCESS/FAILURE based on the modem Result code
     */
    void onEnableEndc(int slotId, Token token, Status status);

    /**
     * Response to queryEndcStatus
     * @param - slotId
     * @param - token is the same token which is recived in queryEndcStatus
     * @param - status SUCCESS/FAILURE based on the modem Result code
     * @param - enableStatus true if endc is enabled otherwise false
     */
    void onEndcStatus(int slotId, Token token, Status status, boolean enableStatus);

    /**
     * Response to setNrConfig
     * @param - slotId
     * @param - token is the same token which is recived in setNrConfig
     * @param - status SUCCESS/FAILURE based on the modem Result code
     */
    void onSetNrConfig(int slotId, Token token, Status status);

    /**
     * Response to queryNrConfig
     * @param - slotId
     * @param - token is the same token which is recived in queryNrConfig
     * @param - status SUCCESS/FAILURE based on the modem Result code
     * @param - nrConfig: NSA + SA/NSA/SA
     */
    void onNrConfigStatus(int slotId, Token token, Status status, NrConfig nrConfig);

    /**
     * Response to setCarrierInfoForImsiEncryption
     * @param - slotId
     * @param - token is the same token which is recived in setNrConfig
     * @param - status SUCCESS/FAILURE based on the modem Result code
     *
     */
    void setCarrierInfoForImsiEncryptionResponse(int slotId, Token token, Status status,
            QRadioResponseInfo info);

    void on5gStatus(int slotId, Token token, Status status, boolean enableStatus);

    void onAnyNrBearerAllocation(int slotId, Token token, Status status,
            BearerAllocationStatus bearerStatus);

    void onNrDcParam(int slotId, Token token, Status status, DcParam dcParam);

    void onUpperLayerIndInfo(int slotId, Token token, Status status,
            UpperLayerIndInfo upperLayerInfo);

    void on5gConfigInfo(int slotId, Token token, Status status,
            NrConfigType nrConfigType);

    void onSignalStrength(int slotId, Token token, Status status,
            SignalStrength signalStrength);

    void getQtiRadioCapabilityResponse(int slotId, Token token, Status status, int raf);

    /**
    * Response to sendCdmaSms
    * @param - slotId
    * @param - token is the same token which is recived in sendCdmaSms
    * @param - status SUCCESS/FAILURE based on the modem Result code
    * @param sms Sms result struct as defined by SmsResult
    *
    */
    void sendCdmaSmsResponse(int slotId, Token token, Status status, SmsResult sms);

    void getCallForwardStatusResponse(int slotId, Token token, Status status,
            QtiCallForwardInfo[] callForwardInfoList);

    void getFacilityLockForAppResponse(int slotId, Token token, Status status, int[] result);

    void getImeiResponse(int slotId, Token token, Status status, QtiImeiInfo imeiInfo);

    void onImeiChange(int slotId, QtiImeiInfo imeiInfo);

    void onSendUserPreferenceForDataDuringVoiceCall(int slotId, Token token,
            Status status);

    void onDdsSwitchCapabilityChange(int slotId, Token token, Status status, boolean support);

    void onDdsSwitchCriteriaChange(int slotId, Token token, boolean telephonyDdsSwitch);

    void onDdsSwitchRecommendation(int slotId, Token token, int recommendedSlotId);
}
