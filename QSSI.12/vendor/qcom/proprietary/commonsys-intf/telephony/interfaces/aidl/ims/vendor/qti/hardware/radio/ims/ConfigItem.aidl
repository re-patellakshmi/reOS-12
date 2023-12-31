/*
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */

package vendor.qti.hardware.radio.ims;

import vendor.qti.hardware.radio.ims.ConfigItem;

@VintfStability
@Backing(type="int")
enum ConfigItem {
    INVALID,
    NONE,
    VOCODER_AMRMODESET,
    VOCODER_AMRWBMODESET,
    SIP_SESSION_TIMER,
    MIN_SESSION_EXPIRY,
    CANCELLATION_TIMER,
    T_DELAY,
    SILENT_REDIAL_ENABLE,
    SIP_T1_TIMER,
    SIP_T2_TIMER,
    SIP_TF_TIMER,
    VLT_SETTING_ENABLED,
    LVC_SETTING_ENABLED,
    DOMAIN_NAME,
    SMS_FORMAT,
    SMS_OVER_IP,
    PUBLISH_TIMER,
    PUBLISH_TIMER_EXTENDED,
    CAPABILITIES_CACHE_EXPIRATION,
    AVAILABILITY_CACHE_EXPIRATION,
    CAPABILITIES_POLL_INTERVAL,
    SOURCE_THROTTLE_PUBLISH,
    MAX_NUM_ENTRIES_IN_RCL,
    CAPAB_POLL_LIST_SUB_EXP,
    GZIP_FLAG,
    EAB_SETTING_ENABLED,
    MOBILE_DATA_ENABLED,
    VOICE_OVER_WIFI_ENABLED,
    VOICE_OVER_WIFI_ROAMING,
    VOICE_OVER_WIFI_MODE,
    CAPABILITY_DISCOVERY_ENABLED,
    EMERGENCY_CALL_TIMER,
    SSAC_HYSTERESIS_TIMER,
    VOLTE_USER_OPT_IN_STATUS,
    LBO_PCSCF_ADDRESS,
    KEEP_ALIVE_ENABLED,
    REGISTRATION_RETRY_BASE_TIME_SEC,
    REGISTRATION_RETRY_MAX_TIME_SEC,
    SPEECH_START_PORT,
    SPEECH_END_PORT,
    SIP_INVITE_REQ_RETX_INTERVAL_MSEC,
    SIP_INVITE_RSP_WAIT_TIME_MSEC,
    SIP_INVITE_RSP_RETX_WAIT_TIME_MSEC,
    SIP_NON_INVITE_REQ_RETX_INTERVAL_MSEC,
    SIP_NON_INVITE_TXN_TIMEOUT_TIMER_MSEC,
    SIP_INVITE_RSP_RETX_INTERVAL_MSEC,
    SIP_ACK_RECEIPT_WAIT_TIME_MSEC,
    SIP_ACK_RETX_WAIT_TIME_MSEC,
    SIP_NON_INVITE_REQ_RETX_WAIT_TIME_MSEC,
    SIP_NON_INVITE_RSP_RETX_WAIT_TIME_MSEC,
    AMR_WB_OCTET_ALIGNED_PT,
    AMR_WB_BANDWIDTH_EFFICIENT_PT,
    AMR_OCTET_ALIGNED_PT,
    AMR_BANDWIDTH_EFFICIENT_PT,
    DTMF_WB_PT,
    DTMF_NB_PT,
    AMR_DEFAULT_MODE,
    SMS_PSI,
    VIDEO_QUALITY,
    THRESHOLD_LTE1,
    THRESHOLD_LTE2,
    THRESHOLD_LTE3,
    THRESHOLD_1x,
    THRESHOLD_WIFI_A,
    THRESHOLD_WIFI_B,
    T_EPDG_LTE,
    T_EPDG_WIFI,
    T_EPDG_1x,
    VWF_SETTING_ENABLED,
    VCE_SETTING_ENABLED,
    RTT_SETTING_ENABLED,
    SMS_APP,
    VVM_APP,
    VOICE_OVER_WIFI_ROAMING_MODE,
    SET_AUTO_REJECT_CALL_MODE_CONFIG,
    VOWIFI_FQDN,
    MMTEL_CALL_COMPOSER_CONFIG,
    VOWIFI_ENTITLEMENT_ID,
}
