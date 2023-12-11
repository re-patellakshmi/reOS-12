/*
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */

package org.codeaurora.ims;

/* This class is responsible to cache the callcomposer and ecnam information received as part of
 * prealerting indication.
 */

public final class PreAlertingCallInfo {
    // Call Id
    private int mCallId;
    // Call composer information
    CallComposerInfo mCcInfo;
    // ecnam information
    EcnamInfo mEcnamInfo;

    public PreAlertingCallInfo() {}

    public PreAlertingCallInfo(int id, CallComposerInfo ccInfo) {
        this(id, ccInfo, null);
    }

    public PreAlertingCallInfo(int id, EcnamInfo ecnamInfo) {
        this(id, null, ecnamInfo);
    }

    public PreAlertingCallInfo(int id, CallComposerInfo ccInfo, EcnamInfo ecnamInfo) {
        mCallId = id;
        mCcInfo = ccInfo;
        mEcnamInfo = ecnamInfo;
    }

    /**
     * Method used to return callId.
     */
    public int getCallId() {
        return mCallId;
    }

    /**
     * Method used to return CallComposer Information.
     */
    public CallComposerInfo getCallComposerInfo() {
        return mCcInfo;
    }

    /**
     * Method used to return Ecnam Information.
     */
    public EcnamInfo getEcnamInfo() {
        return mEcnamInfo;
    }

    public String toString() {
        return "PreAlertingCallInfo CallId: " + mCallId + " CallComposerInfo: " + mCcInfo +
                " EcnamInfo: " + mEcnamInfo;
    }
}
