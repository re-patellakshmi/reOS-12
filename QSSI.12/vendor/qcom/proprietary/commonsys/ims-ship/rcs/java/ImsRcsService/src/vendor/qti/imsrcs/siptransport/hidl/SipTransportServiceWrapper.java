/**********************************************************************
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 **********************************************************************/

package vendor.qti.imsrcs.siptransport.hidl;

import android.os.RemoteException;


import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;
import android.util.Log;

import vendor.qti.ims.configservice.V1_0.IConfigService;
import vendor.qti.ims.factory.V2_0.IImsFactory;
import vendor.qti.ims.rcssip.V1_0.ISipConnection;
import vendor.qti.ims.rcssip.V1_0.ISipTransportListener;
import vendor.qti.ims.rcssip.V1_0.ISipTransportService;
import vendor.qti.ims.rcssip.V1_0.configData;
import vendor.qti.ims.rcssip.V1_0.keyValuePairStringType;
import vendor.qti.ims.rcssip.V1_0.regRestorationDataKeys;


public class SipTransportServiceWrapper {

    private int userData = 1000;
    public ISipTransportService mHidlSipTransportService;
    public ImsSipTransportEventListener mSipTransportEventListener;
    SipTransportListener mhidlSipTransportListener = new
        SipTransportListener();
    final String LOG_TAG = "SipTransportServiceWrapper";
    private static int INVALID_SLOT_ID = -1;

    private int mSlotId = INVALID_SLOT_ID;

    public SipTransportServiceWrapper(int slotId,
                       ImsSipTransportEventListener listener) {
        Log.d(LOG_TAG, ": ctor for slotId["+Integer.toString(slotId)+"]");
        mSlotId = slotId;
        mSipTransportEventListener = listener;
    }

    public void setHidlSipTransportService(ISipTransportService service) {
        Log.d(LOG_TAG, ": setHidlSipTransportService clled");
        if(service !=null)
            Log.d(LOG_TAG, ": setHidlSipTransportService"+
                           " service obj not null");
        mHidlSipTransportService = service;
    }

    public void setSipTransportEventListener(
        ImsSipTransportEventListener listener) {
        Log.d(LOG_TAG, ": setSipTransportEventListener");
        mSipTransportEventListener = listener;
    }

    public ISipTransportListener getHidlSipTransportListener() {
        return mhidlSipTransportListener;
    }

    public int getSlotId() {
        return mSlotId;
    }

    public void sipTransportDied() {
        Log.d(LOG_TAG, ": sipTransportDied");
        if(mSipTransportEventListener != null)
           mSipTransportEventListener.handleSipTransportServiceDied();
        mHidlSipTransportService = null;
    }

    public int createConnection(
        String featureTagsString,
        SipDelegateWrapper mSipDelegateWrapper) {
        userData++;
        Log.d(LOG_TAG, ": createConnection - userData:"+userData);
        ISipTransportService.createConnectionCallback hidl_createConnection_cb =
                new ISipTransportService.createConnectionCallback() {
                    @Override
                    public void onValues(
                        int i,
                        ISipConnection iSipConnection,
                        long l) {
                        Log.d(LOG_TAG, ": createConnectionCallback onValues");
                        mSipDelegateWrapper.setHidlSipConnection(
                            iSipConnection);
                        mSipDelegateWrapper.setHidlConnectionHandle(l);
                    }
        };

        try {
            Log.d(LOG_TAG, ": createConnection - Before calling hidl fns:");
            mHidlSipTransportService.createConnection(
                    mSipDelegateWrapper.getHidlSipConnectionListener(),
                    featureTagsString,
                    userData,
                    hidl_createConnection_cb);
        } catch (RemoteException e) {
            Log.e(LOG_TAG, ": createConnection - Exception: "+e);
            e.printStackTrace();
            return -1;
        }
        return userData;
    }

    public int closeConnection(
        int reason, SipDelegateWrapper mSipDelegateWrapper) {
        Log.d(LOG_TAG, ": closeConnection");
        userData++;
        try {
            mHidlSipTransportService.closeConnection(
                mSipDelegateWrapper.getHidlConnectionHandle(),
                reason,
                userData);
        } catch (RemoteException e) {
            e.printStackTrace();
            return -1;
        }
        return userData;
    }

    public int triggerRegistration() {
        Log.d(LOG_TAG, ": triggerRegistration");
        userData++;
        try {
            mHidlSipTransportService.triggerRegistration(userData);
        } catch (RemoteException e) {
            e.printStackTrace();
            return -1;
        }
        return userData;
    }

    public int triggerRegRestoration(int sipCode, String sipReason) {
        Log.d(LOG_TAG, ": triggerRegRestoration");
        userData++;
        ArrayList<keyValuePairStringType> regRestorationData = new
            ArrayList<>();
        keyValuePairStringType regData = new keyValuePairStringType();
        regData.key = regRestorationDataKeys.responseCode;
        regData.value = Integer.toString(sipCode);
        regRestorationData.add(regData);
        try {
            mHidlSipTransportService.triggerRegRestoration(
                regRestorationData,userData);
        } catch (RemoteException e) {
            e.printStackTrace();
            return -1;
        }
        return userData;
    }

    public int clearSipTransportService() {
        Log.d(LOG_TAG, ": clearSipTransportService");
        try {
            return mHidlSipTransportService.clearSipTransportService();
        } catch (RemoteException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private class SipTransportListener extends ISipTransportListener.Stub {

        @Override
        public void onServiceStatus(int i) throws RemoteException {
            if(mSipTransportEventListener != null)
                mSipTransportEventListener.handleServiceStatus(i);
        }

        @Override
        public void onConfigurationChange(
            configData configData) throws RemoteException {
            if(mSipTransportEventListener != null)
                mSipTransportEventListener.handleConfigurationChanged(
                    configData);
        }

        @Override
        public void onCommandStatus(
            int cmdStatus, int userData) throws RemoteException {
            if(mSipTransportEventListener != null)
                mSipTransportEventListener.handleCmdStatus(
                    cmdStatus, userData);
        }
    };


    public static abstract class ImsSipTransportEventListener {
        protected Executor mExecutor;
        static final String LOG_TAG = "ImsSipTransportEventListener";
        public ImsSipTransportEventListener(Executor e) {
            mExecutor = e;
            Log.d(LOG_TAG, ": ImsSipTransportEventListener");
        }

        public final void handleServiceStatus(int s) {
            mExecutor.execute(()->{
                onServiceStatus(s);
            });
        }

        public final void handleSipTransportServiceDied() {
            mExecutor.execute(()->{
                onSipTransportServiceDied();
            });
        }

        public final void handleCmdStatus(int status,int userdata) {
            mExecutor.execute(()->{
                onCmdStatus(status,userdata);
            });
        }

        public final void handleConfigurationChanged(configData config) {
            mExecutor.execute(()->{
                onConfigurationChange(config);
            });
        }
        public void onServiceStatus(int status) {}
        public void onSipTransportServiceDied(){}
        public void onCmdStatus(int status,int userdata) {}
        public void onConfigurationChange(configData configData) {}
    };

}
