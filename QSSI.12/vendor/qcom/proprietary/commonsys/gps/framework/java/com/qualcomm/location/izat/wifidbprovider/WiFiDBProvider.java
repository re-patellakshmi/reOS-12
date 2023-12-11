/*====*====*====*====*====*====*====*====*====*====*====*====*====*====*====*
  Copyright (c) 2018, 2020-2021 Qualcomm Technologies, Inc.
  All Rights Reserved.
  Confidential and Proprietary - Qualcomm Technologies, Inc.
=============================================================================*/

package com.qualcomm.location.izat.wifidbprovider;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Binder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;

import com.qti.wifidbprovider.*;
import com.qti.wwandbreceiver.*;
import com.qualcomm.location.idlclient.LocIDLClientBase;
import com.qualcomm.location.idlclient.IDLClientUtils;
import com.qualcomm.location.izat.GTPClientHelper;
import com.qualcomm.location.izat.IzatService;

import vendor.qti.gnss.V2_1.ILocHidlGnss;
import vendor.qti.gnss.V2_1.ILocHidlWiFiDBProvider;
import vendor.qti.gnss.V2_1.ILocHidlWiFiDBProviderCallback;
import vendor.qti.gnss.V2_1.LocHidlWifiDBListStatus;

import com.qualcomm.location.idlclient.LocIDLClientBase.*;
import com.qualcomm.location.idlclient.*;
import vendor.qti.gnss.ILocAidlGnss;
import vendor.qti.gnss.ILocAidlWiFiDBProvider;
import vendor.qti.gnss.ILocAidlWiFiDBProviderCallback;
import vendor.qti.gnss.LocAidlApObsData;
import vendor.qti.gnss.LocAidlLocation;
import vendor.qti.gnss.LocAidlApScanData;

public class WiFiDBProvider implements IzatService.ISystemEventListener {
    private static final String TAG = "WiFiDBProvider";
    private static final boolean VERBOSE_DBG = Log.isLoggable(TAG, Log.VERBOSE);

    private static final Object sCallBacksLock = new Object();
    private final Context mContext;

    private IWiFiDBProviderResponseListener mWiFiDBProviderResponseListener = null;

    private PendingIntent mListenerIntent = null;
    private WiFiDBProviderIdlClient mIdlClient = null;
    private String mPackageName;

    private static WiFiDBProvider sInstance = null;
    public static WiFiDBProvider getInstance(Context ctx) {
        if (sInstance == null) {
            sInstance = new WiFiDBProvider(ctx);
        }
        return sInstance;
    }

    private WiFiDBProvider(Context ctx) {
        if (VERBOSE_DBG) {
            Log.d(TAG, "WiFiDBProvider construction");
        }

        if (LocIDLClientBase.getIDLServiceVersion().compareTo(IDLServiceVersion.V_AIDL) >= 0) {
            mIdlClient = new WiFiDBProviderIdlClient(this);
        } else {
            mIdlClient = new WiFiDBProviderHidlClient(this);
        }
        if (null == mIdlClient) {
            Log.e(TAG, "WiFiDBProvider construction fail: " + mIdlClient);
        }

        mContext = ctx;
        IzatService.AidlClientDeathNotifier.getInstance().registerAidlClientDeathCb(this);
    }

    @Override
    public void onAidlClientDied(String packageName) {
        if (mPackageName != null && mPackageName.equals(packageName)) {
            Log.d(TAG, "aidl client crash: " + packageName);
            synchronized (sCallBacksLock) {
                mWiFiDBProviderResponseListener = null;
            }
        }
    }

    /* Remote binder */
    private final IWiFiDBProvider.Stub mBinder = new IWiFiDBProvider.Stub() {

        public boolean registerResponseListener(final IWiFiDBProviderResponseListener callback,
                                                PendingIntent notifyIntent) {
            if (VERBOSE_DBG) {
                Log.d(TAG, "WiFiDBProvider registerResponseListener");
            }

            if (callback == null) {
                Log.e(TAG, "callback is null");
                return false;
            }

            if (notifyIntent == null) {
                Log.w(TAG, "notifyIntent is null");
            }

            if (null != mWiFiDBProviderResponseListener) {
                Log.e(TAG, "Response listener already provided.");
                return false;
            }

            synchronized (sCallBacksLock) {
                mWiFiDBProviderResponseListener = callback;
                mListenerIntent = notifyIntent;
            }

            mPackageName = mContext.getPackageManager().getNameForUid(Binder.getCallingUid());
            if (VERBOSE_DBG) {
                Log.d(TAG, "WiFiDBProvider GTPClientHelper.SetClientRegistrationStatus IN");
            }
            GTPClientHelper.SetClientRegistrationStatus(mContext,
                    GTPClientHelper.GTP_CLIENT_WIFI_PROVIDER, notifyIntent, true);

            return true;
        }

        public void removeResponseListener(final IWiFiDBProviderResponseListener callback) {
            if (callback == null) {
                Log.e(TAG, "callback is null");
                return;
            }
            if (VERBOSE_DBG) {
                Log.d(TAG, "WiFiDBProvider GTPClientHelper.SetClientRegistrationStatus OUT");
            }
            GTPClientHelper.SetClientRegistrationStatus(mContext,
                    GTPClientHelper.GTP_CLIENT_WIFI_PROVIDER, mListenerIntent, false);
            synchronized (sCallBacksLock) {
                mWiFiDBProviderResponseListener = null;
                mListenerIntent = null;
            }
            mPackageName = null;
        }


        public void requestAPObsLocData() {
            if (VERBOSE_DBG) {
                Log.d(TAG, "in IWiFiDBProvider.Stub(): requestAPObsLocData()");
            }

            mIdlClient.requestApObsData();
        }

    };

    private void onApObsLocDataAvailable(List<APObsLocData> obsDataList, int status) {
        if (VERBOSE_DBG) {
            Log.d(TAG, "onApObsLocDataAvailable status: " + status);
        }

        Log.d(TAG, "onApObsLocDataAvailable status: " + status);
        Log.d(TAG, "onApObsLocDataAvailable obsDataList size: " + obsDataList.size());
        synchronized (sCallBacksLock) {
            if (null != mWiFiDBProviderResponseListener) {
                try {
                    mWiFiDBProviderResponseListener.onApObsLocDataAvailable(obsDataList, status);
                } catch (RemoteException e) {
                    Log.w(TAG, "onApObsLocDataAvailable remote exception, sending intent");
                    GTPClientHelper.SendPendingIntent(mContext, mListenerIntent, "WiFiDBProvider");
                }
            }
        }
    }

    private void onServiceRequest() {
        if (VERBOSE_DBG) {
            Log.d(TAG, "onServiceRequest");
        }
        synchronized (sCallBacksLock) {
            if (null != mWiFiDBProviderResponseListener) {
                try {
                    mWiFiDBProviderResponseListener.onServiceRequest();
                } catch (RemoteException e) {
                    Log.w(TAG, "onServiceRequest remote exception, sending intent");
                    GTPClientHelper.SendPendingIntent(mContext, mListenerIntent, "WiFiDBProvider");
                }
            }
        }
    }

    public IWiFiDBProvider getWiFiDBProviderBinder() {
        return mBinder;
    }

    // ======================================================================
    // HIDL client
    // ======================================================================
    static class WiFiDBProviderHidlClient extends WiFiDBProviderIdlClient
            implements LocIDLClientBase.IServiceDeathCb {
        private final String TAG = "WiFiDBProviderHidlClient";
        private final boolean VERBOSE = Log.isLoggable(TAG, Log.VERBOSE);

        private LocHidlWiFiDBProviderCallback mLocHidlWiFiDBProvicerCallback;
        private ILocHidlWiFiDBProvider mLocHidlWiFiDBProvider;



        public WiFiDBProviderHidlClient(WiFiDBProvider provider) {
            super(provider);
            getWiFiDBProviderIface();
            mLocHidlWiFiDBProvicerCallback = new LocHidlWiFiDBProviderCallback(provider);

            if (null != mLocHidlWiFiDBProvider) {
                try {
                    mLocHidlWiFiDBProvider.init(mLocHidlWiFiDBProvicerCallback);
                    mLocHidlWiFiDBProvider.registerWiFiDBProvider(mLocHidlWiFiDBProvicerCallback);
                    registerServiceDiedCb(this);
                } catch (RemoteException e) {
                    Log.e(TAG, "Exception on provider init: " + e);
                }
            }
        }

        private void getWiFiDBProviderIface() {
            Log.i(TAG, "getWiFiDBProviderIface");
            ILocHidlGnss gnssService = (vendor.qti.gnss.V2_1.ILocHidlGnss) getGnssService();

            if (null != gnssService) {
                try {
                    mLocHidlWiFiDBProvider = gnssService.getExtensionLocHidlWiFiDBProvider();
                } catch (RemoteException e) {
                    throw new RuntimeException("Exception getting wifidb provider: " + e);
                }
            } else {
                throw new RuntimeException("gnssService is null!");
            }
        }

        @Override
        public void onServiceDied() {
            mLocHidlWiFiDBProvider = null;
            getWiFiDBProviderIface();
            if (null != mLocHidlWiFiDBProvider) {
                try {
                    mLocHidlWiFiDBProvider.init(mLocHidlWiFiDBProvicerCallback);
                    mLocHidlWiFiDBProvider.registerWiFiDBProvider(mLocHidlWiFiDBProvicerCallback);
                } catch (RemoteException e) {
                    Log.e(TAG, "Exception on provider init: " + e);
                }
            }
        }

        @Override
        public void requestApObsData() {
            if (null != mLocHidlWiFiDBProvider) {
                try {
                    mLocHidlWiFiDBProvider.sendAPObsLocDataRequest();
                } catch (RemoteException e) {
                    throw new RuntimeException("Exception on sendAPObsLocDataRequest: " + e);
                }
            } else {
                throw new RuntimeException("mLocHidlWiFiDBProvider is null!");
            }
        }

        // ======================================================================
        // Callbacks
        // ======================================================================

        class LocHidlWiFiDBProviderCallback extends ILocHidlWiFiDBProviderCallback.Stub {

            private WiFiDBProvider mWiFiDBProvider;

            private LocHidlWiFiDBProviderCallback(WiFiDBProvider wiFiDBProvider) {
                mWiFiDBProvider = wiFiDBProvider;
            }

            public void attachVmOnCallback() {
                // ???
            }

            public void serviceRequestCallback() {
                mWiFiDBProvider.onServiceRequest();
            }

            public void apObsLocDataUpdateCallback(ArrayList<LocHidlApObsData> apObsLocDataList,
                    int apObsLocDataListSize, int apListStatus) {

                ArrayList<APObsLocData> aPObsLocDataList = new ArrayList<APObsLocData>();

                for (LocHidlApObsData apObsData: apObsLocDataList) {
                    APObsLocData apObsLocData = new APObsLocData();
                    apObsLocData.mScanTimestamp = (int) (apObsData.scanTimestamp_ms / 1000);
                    apObsLocData.mLocation = IDLClientUtils.translateHidlLocation(
                            apObsData.gpsLoc.gpsLocation);

                    BSInfo bsInfo = new BSInfo();
                    bsInfo.mCellType = IDLClientUtils.RiltoIZatCellTypes(
                            apObsData.cellInfo.cell_type);
                    bsInfo.mCellRegionID1 = apObsData.cellInfo.cell_id1;
                    bsInfo.mCellRegionID2 = apObsData.cellInfo.cell_id2;
                    bsInfo.mCellRegionID3 = apObsData.cellInfo.cell_id3;
                    bsInfo.mCellRegionID4 = apObsData.cellInfo.cell_id4;

                    apObsLocData.mCellInfo = bsInfo;

                    ArrayList<APScan> apScanList = new ArrayList<APScan>();
                    for (LocHidlApScanData apHidlScan: apObsData.ap_scan_info) {
                        APScan apScan = new APScan();
                        apScan.mMacAddress = IDLClientUtils.longMacToHex(apHidlScan.mac_R48b);
                        apScan.mRssi = apHidlScan.rssi;
                        apScan.mDeltaTime = (int) apHidlScan.age_usec;
                        apScan.mChannelNumber = apHidlScan.channel_id;
                        apScan.mSSID = apHidlScan.ssid.getBytes();;

                        apScanList.add(apScan);
                    }

                    APScan[] apScanArray = new APScan[apScanList.size()];
                    apScanList.toArray(apScanArray);
                    apObsLocData.mScanList = apScanArray;

                    aPObsLocDataList.add(apObsLocData);
                }

                mWiFiDBProvider.onApObsLocDataAvailable(aPObsLocDataList, apListStatus);
            }
        }
    }

    // ======================================================================
    // AIDL client
    // ======================================================================
    static class WiFiDBProviderIdlClient extends LocIDLClientBase
            implements LocIDLClientBase.IServiceDeathCb {
        private final String TAG = "WiFiDBProviderIdlClient";
        private final boolean VERBOSE = Log.isLoggable(TAG, Log.VERBOSE);

        private LocAidlWiFiDBProviderCallback mLocAidlWiFiDBProvicerCallback;
        private ILocAidlWiFiDBProvider mLocAidlWiFiDBProvider;

        public WiFiDBProviderIdlClient(WiFiDBProvider provider) {
            getWiFiDBProviderIface();
            mLocAidlWiFiDBProvicerCallback = new LocAidlWiFiDBProviderCallback(provider);

            if (null != mLocAidlWiFiDBProvider) {
                try {
                    mLocAidlWiFiDBProvider.init(mLocAidlWiFiDBProvicerCallback);
                    mLocAidlWiFiDBProvider.registerWiFiDBProvider(mLocAidlWiFiDBProvicerCallback);
                    registerServiceDiedCb(this);
                } catch (RemoteException e) {
                    Log.e(TAG, "Exception on provider init: " + e);
                }
            }
        }

        private void getWiFiDBProviderIface() {
            Log.i(TAG, "getWiFiDBProviderIface");
            ILocAidlGnss gnssService = (vendor.qti.gnss.ILocAidlGnss) getGnssAidlService();

            if (null != gnssService) {
                try {
                    mLocAidlWiFiDBProvider = gnssService.getExtensionLocAidlWiFiDBProvider();
                } catch (RemoteException e) {
                    throw new RuntimeException("Exception getting wifidb provider: " + e);
                }
            }
        }

        @Override
        public void onServiceDied() {
            mLocAidlWiFiDBProvider = null;
            getWiFiDBProviderIface();
            if (null != mLocAidlWiFiDBProvider) {
                try {
                    mLocAidlWiFiDBProvider.init(mLocAidlWiFiDBProvicerCallback);
                    mLocAidlWiFiDBProvider.registerWiFiDBProvider(mLocAidlWiFiDBProvicerCallback);
                } catch (RemoteException e) {
                    Log.e(TAG, "Exception on provider init: " + e);
                }
            }
        }

        public void requestApObsData() {
            if (null != mLocAidlWiFiDBProvider) {
                try {
                    mLocAidlWiFiDBProvider.sendAPObsLocDataRequest();
                } catch (RemoteException e) {
                    throw new RuntimeException("Exception on sendAPObsLocDataRequest: " + e);
                }
            } else {
                throw new RuntimeException("mLocAidlWiFiDBProvider is null!");
            }
        }

        // ======================================================================
        // Callbacks
        // ======================================================================

        class LocAidlWiFiDBProviderCallback extends ILocAidlWiFiDBProviderCallback.Stub {

            private WiFiDBProvider mWiFiDBProvider;

            private LocAidlWiFiDBProviderCallback(WiFiDBProvider wiFiDBProvider) {
                mWiFiDBProvider = wiFiDBProvider;
            }

            public void attachVmOnCallback() {
                // ???
            }

            public void serviceRequestCallback() {
                mWiFiDBProvider.onServiceRequest();
            }

            public void apObsLocDataUpdateCallback(LocAidlApObsData[] apObsLocDataList,
                    int apObsLocDataListSize, int apListStatus) {

                ArrayList<APObsLocData> aPObsLocDataList = new ArrayList<APObsLocData>();

                for (LocAidlApObsData apObsData: apObsLocDataList) {
                    APObsLocData apObsLocData = new APObsLocData();
                    apObsLocData.mScanTimestamp = (int) (apObsData.scanTimestamp_ms / 1000);
                    apObsLocData.mLocation = IDLClientUtils.translateAidlLocation(
                            apObsData.gpsLoc.gpsLocation);

                    BSInfo bsInfo = new BSInfo();
                    bsInfo.mCellType = IDLClientUtils.RiltoIZatCellTypes(
                            apObsData.cellInfo.cell_type);
                    bsInfo.mCellRegionID1 = apObsData.cellInfo.cell_id1;
                    bsInfo.mCellRegionID2 = apObsData.cellInfo.cell_id2;
                    bsInfo.mCellRegionID3 = apObsData.cellInfo.cell_id3;
                    bsInfo.mCellRegionID4 = apObsData.cellInfo.cell_id4;

                    apObsLocData.mCellInfo = bsInfo;

                    ArrayList<APScan> apScanList = new ArrayList<APScan>();
                    for (LocAidlApScanData apHidlScan: apObsData.ap_scan_info) {
                        APScan apScan = new APScan();
                        apScan.mMacAddress = IDLClientUtils.longMacToHex(apHidlScan.mac_R48b);
                        apScan.mRssi = apHidlScan.rssi;
                        apScan.mDeltaTime = (int) apHidlScan.age_usec;
                        apScan.mChannelNumber = apHidlScan.channel_id;
                        apScan.mSSID = apHidlScan.ssid.getBytes();;

                        apScanList.add(apScan);
                    }

                    APScan[] apScanArray = new APScan[apScanList.size()];
                    apScanList.toArray(apScanArray);
                    apObsLocData.mScanList = apScanArray;

                    aPObsLocDataList.add(apObsLocData);
                }

                mWiFiDBProvider.onApObsLocDataAvailable(aPObsLocDataList, apListStatus);
            }
            @Override
            public final int getInterfaceVersion() {
                return ILocAidlWiFiDBProviderCallback.VERSION;
            }
            @Override
            public final String getInterfaceHash() {
                return ILocAidlWiFiDBProviderCallback.HASH;
            }
        }
    }
}
