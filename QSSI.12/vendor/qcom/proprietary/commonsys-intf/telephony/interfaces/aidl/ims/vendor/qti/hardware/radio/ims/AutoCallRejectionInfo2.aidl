/*
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */

package vendor.qti.hardware.radio.ims;

import vendor.qti.hardware.radio.ims.AutoCallRejectionInfo;
import vendor.qti.hardware.radio.ims.CallComposerInfo;
import vendor.qti.hardware.radio.ims.EcnamInfo;

/**
 * AutoCallRejectionInfo2 is used to notify the rejected call information.
 * Telephony will consider AutoCallRejectionInfo2 only if
 *         AutoCallRejectionInfo2#autoCallRejectionInfo is not null.
 */
@VintfStability
parcelable AutoCallRejectionInfo2 {
    AutoCallRejectionInfo autoCallRejectionInfo;
    @nullable
    CallComposerInfo callComposerInfo;
    @nullable
    EcnamInfo ecnamInfo;
}
