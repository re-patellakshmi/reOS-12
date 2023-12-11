/**********************************************************************
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 **********************************************************************/

package vendor.qti.imsrcs.siptransport;

import android.util.Log;
import android.telephony.ims.stub.ImsRegistrationImplBase;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import vendor.qti.imsrcs.siptransport.hidl.SipTransportServiceWrapper;
import vendor.qti.imsrcs.ImsRcsServiceMgr;

public class ImsRcsRegistrationImpl extends ImsRegistrationImplBase {

    int mSlotId;
    ImsRcsServiceMgr mManager;
    SipTransportServiceWrapper mSipTransportWrapper;
    Executor mRcsRegistrationExecutor;
    final String LOG_TAG = "ImsRcsRegistrationImpl";

    public ImsRcsRegistrationImpl(int slotId) {
        super();
        Log.d(LOG_TAG, "ctor ImsRcsRegistrationImpl");
        mRcsRegistrationExecutor = new ScheduledThreadPoolExecutor(1);
        mSlotId = slotId;
        mManager = ImsRcsServiceMgr.getInstance();
    }

    @Override
    public void updateSipDelegateRegistration() {
        Log.d(LOG_TAG, "updateSipDelegateRegistration");
        if(isSipTransportWrapperAvailable())
            mSipTransportWrapper.triggerRegistration();
    }

    @Override
    public void triggerSipDelegateDeregistration() {
        // we dont do anything here
        super.triggerSipDelegateDeregistration();
        Log.d(LOG_TAG, "triggerSipDelegateDeregistration");
    }

    @Override
    public void triggerFullNetworkRegistration(int sipCode, String sipReason) {
        Log.d(LOG_TAG, "triggerFullNetworkRegistration");
        if(isSipTransportWrapperAvailable())
            mSipTransportWrapper.triggerRegRestoration(sipCode, sipReason);
    }

    private boolean isSipTransportWrapperAvailable() {
        Log.d(LOG_TAG, "isSipTransportWrapperAvailable");
        mSipTransportWrapper = mManager.getSipTransportService(mSlotId);
        if(mSipTransportWrapper == null) return false;
        return true;
    }
}