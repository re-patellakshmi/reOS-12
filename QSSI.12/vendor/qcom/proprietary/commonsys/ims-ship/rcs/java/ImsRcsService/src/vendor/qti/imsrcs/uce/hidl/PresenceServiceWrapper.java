/**********************************************************************
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 **********************************************************************/


package vendor.qti.imsrcs.uce.hidl;

import android.net.Uri;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Executor;

import vendor.qti.ims.rcsuce.V1_0.IPresenceListener;
import vendor.qti.ims.rcsuce.V1_0.IPresenceService;
import vendor.qti.ims.rcsuce.V1_0.PresPublishTriggerType;
import vendor.qti.ims.rcsuce.V1_0.SipResponse;
import vendor.qti.ims.rcsuce.V1_0.SubscriptionInfo;
import vendor.qti.ims.rcsuce.V1_0.SubscriptionStatus;
import vendor.qti.ims.rcsuce.V1_0.UceServiceStatus;
import vendor.qti.ims.rcsuce.V1_0.UceStatusCode;

import android.telephony.ims.RcsUceAdapter;

import android.util.Log;
import vendor.qti.imsrcs.ImsRcsService;


public class PresenceServiceWrapper {
    public IPresenceService mHidlPresenceService;

    private String LOG_TAG = ImsRcsService.LOG_TAG + ":PresenceServiceWrapper";

    int mPublishTrigger = -1;
    int mServiceAvailable = -1;

    public PresenceServiceWrapper(int SlotId) {
        LOG_TAG = LOG_TAG + "[" +SlotId+"]";
    }
    public void close() {

    }

    public void presenceDied() {
        for(ImsPresCapEventListener e : mCapEventListnerList) {
            e.handlePresenceServiceDied();
        }
    }
    public void publishCapabilities(String pidfXml, long userData) {
        try {
            mHidlPresenceService.publishCapability(pidfXml,userData);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void subscribeForCapabilities(Collection<Uri> list, long userData) {
        try {
            ArrayList<String> contacts = new ArrayList<String>();
            for(Uri s : list) {
                contacts.add(s.toString());
            }
            mHidlPresenceService.getContactCapability(contacts,userData);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private class PresenceListener extends IPresenceListener.Stub {

        @Override
        public void onServiceStatus(int i) throws RemoteException {
            Log.d(LOG_TAG, "PresenceListener: onServiceStatus :: received[" +i +"]");
            mServiceAvailable = i;
            for(ImsPresCapEventListener e : mCapEventListnerList) {
                e.handleServiceStatus(i);
            }
        }

        @Override
        public void onPublishTrigger(int i) throws RemoteException {
            Log.d(LOG_TAG, "PresenceListener: onPublishTrigger :: received [" +i +"]");
            mPublishTrigger = i;
            for(ImsPresCapEventListener e : mCapEventListnerList) {
                e.handlePublishTrigger(i);
            }
        }

        @Override
        public void onCmdStatus(long l, int i) throws RemoteException {
            Log.d(LOG_TAG, "PresenceListener: onCmdStatus :: received");
            for(ImsPresCapEventListener e : mCapEventListnerList) {
                e.handleCmdStatus(l,i);
            }
        }

        @Override
        public void onSipResponse(long l, SipResponse sipResponse, short i) throws RemoteException {
            Log.d(LOG_TAG, "PresenceListener: onSipResponse :: received");
            for(ImsPresCapEventListener e : mCapEventListnerList) {
                e.handleSipResponse(l,sipResponse, i);
            }
        }

        @Override
        public void onCapInfoReceived(long l, SubscriptionStatus subscriptionStatus,
                                      ArrayList<SubscriptionInfo> arrayList) throws RemoteException {
            Log.d(LOG_TAG, "PresenceListener: onCapInfoReceived :: received");
            for(ImsPresCapEventListener e : mCapEventListnerList) {
                e.handleCapInfo(l,subscriptionStatus, arrayList);
            }
        }


        @Override
        public void onUnpublishSent() throws RemoteException {
            Log.d(LOG_TAG, "PresenceListener: onUnpublishSent :: received");
            for(ImsPresCapEventListener e : mCapEventListnerList) {
                e.handlUnPublish();
            }
        }
    };
    PresenceListener mhidlPresenceListener = new PresenceListener();

    public IPresenceListener getHidlPresenceListener() {
        return mhidlPresenceListener;
    }
    public void setHidlPresenceService(IPresenceService service) {
        mHidlPresenceService = service;
    }

    List<ImsPresCapEventListener> mCapEventListnerList = new ArrayList<ImsPresCapEventListener>();
    public void setCapInfolistener(ImsPresCapEventListener l) {
        mCapEventListnerList.add(l);
        if(mServiceAvailable != -1) {
            l.handleServiceStatus(mServiceAvailable);
        }
        if(mPublishTrigger != -1) {
            l.handlePublishTrigger(mPublishTrigger);
        }
    }

}
