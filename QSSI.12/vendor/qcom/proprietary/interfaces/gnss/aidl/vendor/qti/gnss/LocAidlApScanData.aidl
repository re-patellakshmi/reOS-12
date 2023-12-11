/*
* Copyright (c) 2021 Qualcomm Technologies, Inc.
* All Rights Reserved.
* Confidential and Proprietary - Qualcomm Technologies, Inc.
*/

package vendor.qti.gnss;

@VintfStability
parcelable LocAidlApScanData {
    long mac_R48b;
    int rssi;
    long age_usec;
    byte channel_id;
    String ssid;
    byte ssid_valid_byte_count;
}

