/*
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */

package vendor.qti.hardware.radio.qtiradio;

import vendor.qti.hardware.radio.qtiradio.NrIconType;
import vendor.qti.hardware.radio.qtiradio.NrConfig;
import vendor.qti.hardware.radio.qtiradio.ImeiInfo;

@VintfStability
interface IQtiRadioIndication {

    /*
     * Unsol msg to indicate changes in 5G Icon Type.
     *
     * @param NrIconType as per NrIconType.aidl to indicate 5G icon - NONE(Non-5G) or
     *         5G BASIC or 5G UWB shown on the UI.
     *
     */
    oneway void onNrIconTypeChange(in NrIconType iconType);

    /*
     * Unsol msg to indicate change in NR Config.
     *
     * @param NrConfig as per types.hal to indicate NSA/SA/NSA+SA.
     *
     */
    oneway void onNrConfigChange(in NrConfig config);

    /**
     * Unsol msg to indicate change in IMEI info.
     *
     * @param ImeiInfo IMEI information.
     *
     */
    oneway void onImeiChange(in ImeiInfo info);

    /**
     * Unsol msg to inform HLOS that smart DDS switch capability changed.
     * Upon receiving this unsol, HLOS has to inform modem if user has enabled
     * temp DDS switch from UI or not.
     *
     */
    oneway void onDdsSwitchCapabilityChange();

    /**
     * Unsol msg to indicate if telephony has to enable/disable its temp DDS switch logic.
     * If telephony temp DDS switch logic is disabled, then telephony will wait for
     * modem recommendations in seperate indication to perform temp DDS switch.
     *
     * @param telephonyDdsSwitch true/false based on telephony temp DDS switch
     * logic should be enabled/disabled..
     *
     */
    oneway void onDdsSwitchCriteriaChange(in boolean telephonyDdsSwitch);

    /**
     * Unsol msg to indicate modem recommendation for temp DDS switch.
     *
     * @param recommendedSlotId slot ID to which DDS has to be switched.
     *
     */
    oneway void onDdsSwitchRecommendation(in int recommendedSlotId);

    /**
     * Unsol msg to indicate the delay time to deactivate default data pdn
     * when cellular IWLAN feature is ON.
     *
     * @param - delayTimeMilliSecs delayTimeMilliSecs>0 indicates one or more pdns
     *           are established over Cellular IWLAN and wait for delayTimeMilliSecs
     *           to deactivate default data pdn if required.
     *           delayTimeMilliSecs<=0 indicates no pdns are established over Cellular IWLAN.
     */
    oneway void onDataDeactivateDelayTime(in long delayTimeMilliSecs);

    /**
     * Unsol msg to indicate if epdg over cellular data (cellular IWLAN) feature is supported or not.
     *
     * @param - support support indicates if the feature is supported or not.
     */
    oneway void onEpdgOverCellularDataSupported(in boolean support);
}
