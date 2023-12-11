/*
* Copyright (c) 2021 Qualcomm Technologies, Inc.
* All Rights Reserved.
* Confidential and Proprietary - Qualcomm Technologies, Inc.
*/

package vendor.qti.gnss;

@VintfStability
parcelable LocAidlApLocationData {
    long mac_R48b;
    float latitude;
    float longitude;
    float max_antenna_range;
    float horizontal_error;
    byte reliability;
    byte valid_bits;
}

