/*
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */

package vendor.qti.hardware.radio.qtiradio;

import vendor.qti.hardware.radio.qtiradio.CallForwardInfo;
import vendor.qti.hardware.radio.qtiradio.NrIconType;
import vendor.qti.hardware.radio.qtiradio.Status;
import vendor.qti.hardware.radio.qtiradio.EndcStatus;
import vendor.qti.hardware.radio.qtiradio.NrConfig;
import vendor.qti.hardware.radio.qtiradio.RadioAccessFamily;
import vendor.qti.hardware.radio.qtiradio.ImeiInfo;

@VintfStability
interface IQtiRadioResponse {

     /**
     * Response to IQtiRadio.queryNrIconType
     *
     * @param serial to match request/response. Response must include same serial as request.
     * @param errorCode - errorCode as per types.hal returned from RIL.
     * @param NrIconType as per NrIconType.aidl to indicate 5G icon - NONE(Non-5G) or
     *        5G BASIC or 5G UWB shown on the UI.
     */
    oneway void onNrIconTypeResponse(in int serial, in int errorCode, in NrIconType iconType);

     /**
     * Response to IQtiRadio.enableEndc
     *
     * @param serial to match request/response. Response must include same serial as request.
     * @param errorCode - errorCode as per types.hal returned from RIL.
     * @param status SUCCESS/FAILURE of the request.
     */
    oneway void onEnableEndcResponse(in int serial, in int errorCode, in Status status);

     /**
     * Response to IQtiRadio.queryEndcStatus
     *
     * @param serial to match request/response. Response must include same serial as request.
     * @param errorCode - errorCode as per types.hal returned from RIL.
     * @param endcStatus values as per types.hal to indicate ENDC is enabled/disabled.
     */
    oneway void onEndcStatusResponse(in int serial, in int errorCode, in EndcStatus endcStatus);

     /**
     * Response to IQtiRadio.SetNrConfig
     *
     * @param serial to match request/response. Response must include same serial as request.
     * @param errorCode - errorCode as per types.hal returned from RIL.
     * @param status SUCCESS/FAILURE of the request.
     */
    oneway void setNrConfigResponse(in int serial, in int errorCode, in Status status);

     /**
     * Response to IQtiRadio.queryNrConfig
     *
     * @param serial to match request/response. Response must include same serial as request.
     * @param errorCode - errorCode as per types.hal returned from RIL.
     * @param enabled values as per NrConfig.aidl to indicate status of NrConfig.
     */
    oneway void onNrConfigResponse(in int serial, in int errorCode, in NrConfig config);

     /**
     * Response to IQtiRadio.getQtiRadioCapability
     *
     * @param serial to match request/response. Response must include same serial as request.
     * @param errorCode - errorCode as per types.hal returned from RIL.
     * @param raf Radio Access family as defined by RadioAccessFamily in RadioAccessFamily.aidl
     */
    oneway void getQtiRadioCapabilityResponse(in int serial, in int errorCode,
            in RadioAccessFamily raf);

    /**
     * Response to IQtiRadio.getCallForwardStatus
     *
     * @param serial to match request/response. Response must include same serial as request.
     * @param errorCode - errorCode as per types.hal returned from RIL.
     * @param callInfoForwardInfoList list of call forward status information for different
     *         service classes.
     */

    oneway void getCallForwardStatusResponse(in int serial, in int errorCode,
            in CallForwardInfo[] callForwardInfoList);

    /**
     * Response to IQtiRadio.getFacilityLockForApp
     *
     * @param serial to match request/response. Response must include same serial as request.
     * @param errorCode - errorCode as per types.hal returned from RIL.
     * @param response 0 is the TS 27.007 service class bit vector of services for which the
     *        specified barring facility is active. "0" means "disabled for all"
     */
    oneway void getFacilityLockForAppResponse(in int serial, in int errorCode, in int response);

    /**
     * Response to IQtiRadio.getImei
     *
     * @param ImeiInfo IMEI information.
     */
    oneway void getImeiResponse(in int serial, in int errorCode, in ImeiInfo info);

    /** Response to IQtiRadio.getDdsSwitchCapability
     *
     * @param serial to match request/response. Response must include same serial as request.
     * @param errorCode - errorCode as per RIL_Errno part of hardware/ril/include/telephony/ril.h.
     * @param support true/false if smart dds switch capability is supported or not.
     */
    oneway void getDdsSwitchCapabilityResponse(in int serial,
            in int errorCode, in boolean support);

    /**
     * Response to IQtiRadio.sendUserPreferenceForDataDuringVoiceCall
     *
     * @param serial to match request/response. Response must include same serial as request.
     * @param errorCode - errorCode as per RIL_Errno part of hardware/ril/include/telephony/ril.h
     */
    oneway void sendUserPreferenceForDataDuringVoiceCallResponse(in int serial,
            in int errorCode);
}
