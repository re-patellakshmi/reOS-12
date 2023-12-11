/*
 * Copyright (c) 2018, 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */
package org.codeaurora.ims.sms;
import android.telephony.ims.stub.ImsSmsImplBase;

public class SmsResponse {

    private final int mMessageRef;
    private final int mSendSmsResult;
    private final int mSendSmsReason;
    private final int mNetworkErrorCode;

    public SmsResponse(int msgRef, int result, int reason){
        this(msgRef, result, reason, ImsSmsImplBase.RESULT_NO_NETWORK_ERROR);
    }

    public SmsResponse(int msgRef, int result, int reason, int networkErrorCode){
        mMessageRef = msgRef;
        mSendSmsResult = result;
        mSendSmsReason = reason;
        mNetworkErrorCode = networkErrorCode;
    }

    public int getMsgRef(){
        return mMessageRef;
    }

    public int getResult(){
        return mSendSmsResult;
    }

    public int getReason(){
        return mSendSmsReason;
    }

    public int getNetworkErrorCode(){
        return mNetworkErrorCode;
    }

    @Override
    public String toString() {
        return "{ mMessageRef = " + mMessageRef
                + ", mSendSmsResult = " + mSendSmsResult
                + ", mSendSmsReason = " + mSendSmsReason
                + ", mNetworkErrorCode = " + mNetworkErrorCode
                + "}";
    }
}
