/*
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */

package org.codeaurora.ims;

/*
 * This class contains feature IDs that clients can use to check if
 * a particular feature is supported or not
 */

public final class Feature {

    private Feature() {}

    public static final int SMS = 0;
    public static final int CONSOLIDATED_SET_SERVICE_STATUS = 1;
    public static final int EMERGENCY_DIAL = 2;
    public static final int CALL_COMPOSER_DIAL = 3;
    public static final int USSD = 4;
    public static final int CRS = 5;
    public static final int SIP_DTMF = 6;
    public static final int CONFERENCE_CALL_STATE_COMPLETED = 7;
    public static final int SET_MEDIA_CONFIG = 8;
    public static final int MULTI_SIM_VOICE_CAPABILITY = 9;
    public static final int EXIT_SCBM = 10;
}
