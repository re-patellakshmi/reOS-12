/*
* Copyright (c) 2021 Qualcomm Technologies, Inc.
* All Rights Reserved.
* Confidential and Proprietary - Qualcomm Technologies, Inc.
*/

package vendor.qti.gnss;

import vendor.qti.gnss.LocAidlIzatLocation;
import vendor.qti.gnss.LocAidlIzatProviderStatus;

@VintfStability
interface ILocAidlIzatProviderCallback {
    void onLocationChanged(in LocAidlIzatLocation location);

    void onStatusChanged(in LocAidlIzatProviderStatus status);
}
