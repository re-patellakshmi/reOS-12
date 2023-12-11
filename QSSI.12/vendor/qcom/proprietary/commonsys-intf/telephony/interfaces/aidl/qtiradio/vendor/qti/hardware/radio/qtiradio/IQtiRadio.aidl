/*
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */

package vendor.qti.hardware.radio.qtiradio;

import vendor.qti.hardware.radio.qtiradio.CallForwardInfo;
import vendor.qti.hardware.radio.qtiradio.FacilityLockInfo;
import vendor.qti.hardware.radio.qtiradio.IQtiRadioResponse;
import vendor.qti.hardware.radio.qtiradio.IQtiRadioIndication;
import vendor.qti.hardware.radio.qtiradio.NrIconType;
import vendor.qti.hardware.radio.qtiradio.NrConfig;

@VintfStability
interface IQtiRadio {

    /**
     * Set callback for QtiRadio requests
     *
     * @param responseCallback Object contains response callback functions
     * @param indicationCallback Object contains indication callback functions
     */
    oneway void setCallbacks(in IQtiRadioResponse responseCallback,
            in IQtiRadioIndication indicationCallback);
    /**
     * To get 5G icon info to be shown on UI.
     *
     * @param serial to match request/response. Responses must include the same token as requests.
     *
     * Response callback is IQtiRadioResponse.onNrIconTypeResponse()
     */
    oneway void queryNrIconType(in int serial);

    /**
     * To enable/disable ENDC addition in modem to save power consumption.
     *
     * @param serial to match request/response. Responses must include the same token as requests.
     * @param enable set to true/false
     *
     * Response callback is IQtiRadioResponse.onEnableEndcResponse()
     */
    oneway void enableEndc(in int serial, in boolean enable);

    /**
     * To query if ENDC is enabled/disabled.
     *
     * @param serial to match request/response. Responses must include the same token as requests.
     *
     * Response callback is IQtiRadioResponse.onEndcStatusResponse()
     */
    oneway void queryEndcStatus(in int serial);

    /**
     * To get property values
     *
     * @param property name to get value
     *
     * @return string value of property
     *
     */
    String getPropertyValue(in String prop, in String def);

    /**
     * To enable SA or NSA or both(NSA and SA).
     *
     * @param serial to match request/response. Responses must include the same token as requests.
     * @param config option to enable SA or NSA or both(NSA and SA)
     *
     * Response callback is IQtiRadioResponse.setNrConfigResponse()
     */
    oneway void setNrConfig(in int serial, in NrConfig config);


    /**
     * To query which NR configuration is enabled
     *
     * @param serial to match request/response. Responses must include the same token as requests.
     *
     * Response callback is IQtiRadioResponse.onNrConfigResponse()
     */
    oneway void queryNrConfig(in int serial);


    /**
     * To get modem capabilities include 5G SA.
     *
     * @param serial Serial number of request.
     *
     * Response callback is IQtiRadioResponse.getQtiRadioCapabilityResponse()
     */
    oneway void getQtiRadioCapability(in int serial);

    /**
     * Request call forward status.
     *
     * @param serial Serial number of request.
     * @param callForwardInfo CallForwardInfo
     *
     * Response function is IQtiRadioResponse.getCallForwardStatusResponse()
     */
    oneway void getCallForwardStatus(in int serial, in CallForwardInfo callForwardInfo);

    /**
     * Query the status of a facility lock state
     *
     * @param serial Serial number of request.
     * @param facilityLockInfo is FacilityLockInfo
     * Response function is IQtiRadioResponse.getFacilityLockForAppResponse()
     */
    oneway void getFacilityLockForApp(in int serial, in FacilityLockInfo facilityLockInfo);

    /**
     * Query the IMEI and its type, Primary/Secondary
     *
     * @param serial Serial number of request.
     * Response function is IQtiRadioResponse.getImeiResponse()
     */
    oneway void getImei(in int serial);

    /**
     * Request for smart DDS switch capability
     *
     * @param serial Serial number of request.
     * Response function is IQtiRadioResponse.getDdsSwitchCapabilityResponse()
     */
    oneway void getDdsSwitchCapability(in int serial);

    /**
     * Inform modem if user enabled/disabled UI preference for data during voice call.
     * if its enabled then modem can send recommendations to switch DDS during
     * voice call on nonDDS.
     *
     * @param serial Serial number of request.
     * @param userPreference true/false based on UI preference
     * Response function is IQtiRadioResponse.
     * sendUserPreferenceForDataDuringVoiceCallResponse()
     */
    oneway void sendUserPreferenceForDataDuringVoiceCall(in int serial,
            in boolean userPreference);

    /**
     * Request for epdg over cellular data (cellular IWLAN) feature is supported or not.
     *
     * @param serial Serial number of request
     * @return - boolean value indicates if the feature is supported or not.
     */
     boolean isEpdgOverCellularDataSupported();
}
