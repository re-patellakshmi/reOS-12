/*
* Copyright (c) 2021 Qualcomm Technologies, Inc.
* All Rights Reserved.
* Confidential and Proprietary - Qualcomm Technologies, Inc.
*/

package vendor.qti.gnss;

import vendor.qti.gnss.LocAidlApCellInfo;
import vendor.qti.gnss.LocAidlApScanData;
import vendor.qti.gnss.LocAidlUlpLocation;

@VintfStability
parcelable LocAidlApObsData {
    long scanTimestamp_ms;
    LocAidlUlpLocation gpsLoc;
    boolean bUlpLocValid;
    LocAidlApCellInfo cellInfo;
    int ap_scan_info_size;
    LocAidlApScanData[] ap_scan_info;
}

