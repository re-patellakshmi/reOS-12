/**********************************************************************
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 **********************************************************************/

package vendor.qti.imsrcs.siptransport.hidl;

import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.Executor;

import vendor.qti.ims.rcssip.V1_0.ISipConnection;
import vendor.qti.ims.rcssip.V1_0.ISipConnectionListener;
import vendor.qti.ims.rcssip.V1_0.ISipTransportService;
import vendor.qti.ims.rcssip.V1_0.SipTransportStatusCode;
import vendor.qti.ims.rcssip.V1_0.featureTagData;

public class SipDelegateWrapper {

    public SipDelegateWrapper.ImsSipDelegateEventListener
        mSipDelegateEventListener;
    private ISipConnection mHidlSipConnection;
    private SipDelegateListener mHidlSipConnectionListener = new
        SipDelegateListener();
    private int mDestroyReason;
    private long mConnectionHandle = 0;
    final String LOG_TAG = "SipDelegateWrapper";

    public SipDelegateWrapper(){

    }

    public ISipConnection getHidlSipConnection() {
        return mHidlSipConnection;
    }

    public ISipConnectionListener getHidlSipConnectionListener() {
        Log.d(LOG_TAG, ": getHidlSipConnectionListener invoked");
        return mHidlSipConnectionListener;
    }

    public void setHidlConnectionHandle(long handle) {
        mConnectionHandle = handle;
    }

   public long getHidlConnectionHandle(long handle) {
        return mConnectionHandle;
    }

    public void setSipDelegateEventListener(
        ImsSipDelegateEventListener listener){
        mSipDelegateEventListener = listener;
    }
    public long getHidlConnectionHandle() {
        return mConnectionHandle;
    }

    public void setHidlSipConnection(ISipConnection iSipConnection) {
        mHidlSipConnection = iSipConnection;
    }

    private class SipDelegateListener extends ISipConnectionListener.Stub {

        @Override
        public void onConnectionCreated(
            int status,
            ArrayList<featureTagData> deniedFTList) throws RemoteException {
            mSipDelegateEventListener.handleDelegateCreation(status, deniedFTList);
        }

        @Override
        public void onConnectionDestroyed(
            int status, int reason) throws RemoteException {
            mSipDelegateEventListener.handleDelegateDestroyed(status, reason);
        }

        @Override
        public void onEventReceived(int i) throws RemoteException {
            mSipDelegateEventListener.handleOnEventReceived(i);
        }

        @Override
        public void handleIncomingMessage(
            ArrayList<Byte> arrayList) throws RemoteException {
            Log.d(LOG_TAG, ": handleIncomingMessage invoked");
            mSipDelegateEventListener.handleIncomingMsg(arrayList);
        }

        @Override
        public void onCommandStatus(int status, int userdata)
            throws RemoteException {
            mSipDelegateEventListener.handleSipDelegateCmdStatus(
                status,
                userdata);
        }

        @Override
        public void onFeatureTagStatusChange(
            ArrayList<featureTagData> arrayList) throws RemoteException {
            mSipDelegateEventListener.handleFeatureTagStatusChange(
                    arrayList);
        }
    };

    public static abstract class ImsSipDelegateEventListener {
        protected Executor mExecutor;
        public ImsSipDelegateEventListener(Executor e) {
            mExecutor = e;
        }

        public final void handleSipDelegateCmdStatus(
            int status, int userData) {
            mExecutor.execute(()-> {
                onConnectionCmdStatus(status, userData);
            });
        }

        public final void handleIncomingMsg(ArrayList<Byte> sipMsg) {
            mExecutor.execute(()-> {
                onIncomingMessageReceived(sipMsg);
            });
        }

        public final void handleOnEventReceived(int connectionStatus) {
            mExecutor.execute(()->{
                onEventReceived(connectionStatus);
            });
        }

        public final void handleFeatureTagStatusChange(
            ArrayList<featureTagData> featureTagList) {
            mExecutor.execute(()->{
                onFeatureTagStatusChange(featureTagList);
            });
        }

        public final void handleDelegateCreation(
            int status, ArrayList<featureTagData> deniedFeatureTags) {
            mExecutor.execute(()->{
                onDelegateCreated(status, deniedFeatureTags);
            });
        }

        public final void handleDelegateDestroyed(int status, int reason) {
            //TO have translator of reason code
            mExecutor.execute(()->{
                onDelegateDestroyed(status, reason);
            });
        }

        public void onDelegateCreated(
            int status,ArrayList<featureTagData> deniedFts) {}
        public void onDelegateDestroyed(int status, int reason) {}
        public void onEventReceived(int connectionStatus) {}
        public void onIncomingMessageReceived(ArrayList<Byte> sipMsg) {}
        public void onConnectionCmdStatus(int status, int userData) {}
        public void onFeatureTagStatusChange(
            ArrayList<featureTagData> featureTagStatus) {}
    };

}
