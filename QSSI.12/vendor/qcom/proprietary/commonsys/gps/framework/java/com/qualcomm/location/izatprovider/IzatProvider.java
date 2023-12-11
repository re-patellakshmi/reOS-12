/*====*====*====*====*====*====*====*====*====*====*====*====*====*====*====*
  Copyright  (c) 2015, 2017-2021 Qualcomm Technologies, Inc.
  All Rights Reserved.
  Confidential and Proprietary - Qualcomm Technologies, Inc.

  Not a Contribution, Apache license notifications and
  license are retained for attribution purposes only.
=============================================================================*/
/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qualcomm.location.izatprovider;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.WorkSource;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;

import android.location.provider.LocationProviderBase;
import android.location.provider.ProviderRequest;
import com.android.location.provider.ProviderPropertiesUnbundled;

import com.qualcomm.location.izat.flp.FlpServiceProvider;
import com.qualcomm.location.izat.altitudereceiver.*;
import com.qualcomm.location.izat.esstatusreceiver.EsStatusReceiver;
import com.qualcomm.location.izat.IzatService;
import com.qualcomm.location.LocationService;
import com.qti.izat.XTProxy;
import com.qti.flp.IFlpService;
import com.qti.flp.ILocationCallback;

import vendor.qti.gnss.V1_0.ILocHidlGnss;
import vendor.qti.gnss.V1_0.ILocHidlIzatProvider;
import vendor.qti.gnss.V1_0.ILocHidlIzatProviderCallback;
import vendor.qti.gnss.V1_0.LocHidlIzatLocation;
import vendor.qti.gnss.V1_0.LocHidlIzatStreamType;
import vendor.qti.gnss.V1_0.LocHidlIzatRequest;
import vendor.qti.gnss.V1_0.LocHidlNetworkPositionSourceType;
import vendor.qti.gnss.V1_0.LocHidlIzatHorizontalAccuracy;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import com.qualcomm.location.utils.IZatServiceContext;

import com.qualcomm.location.idlclient.*;
import com.qualcomm.location.idlclient.LocIDLClientBase.*;
import vendor.qti.gnss.ILocAidlGnss;
import vendor.qti.gnss.ILocAidlIzatProvider;
import vendor.qti.gnss.ILocAidlIzatProviderCallback;
import vendor.qti.gnss.LocAidlIzatLocation;
import vendor.qti.gnss.LocAidlIzatRequest;
import vendor.qti.gnss.LocAidlNetworkPositionSourceType;


public class IzatProvider extends LocationProviderBase implements Handler.Callback {
    // member constants
    private static final int MSG_ENABLE_PROVIDER    = IZatServiceContext.MSG_IZAT_PROVIDER_BASE + 1;
    private static final int MSG_DISABLE_PROVIDER   = IZatServiceContext.MSG_IZAT_PROVIDER_BASE + 2;
    private static final int MSG_SET_REQUEST        = IZatServiceContext.MSG_IZAT_PROVIDER_BASE + 3;
    private static final int MSG_LOCATION_CHANGED   = IZatServiceContext.MSG_IZAT_PROVIDER_BASE + 4;
    private static final int MSG_STATUS_CHANGED     = IZatServiceContext.MSG_IZAT_PROVIDER_BASE + 5;
    private static final int MSG_DESTROY_ENGINE     = IZatServiceContext.MSG_IZAT_PROVIDER_BASE + 6;
    private static final int MSG_INIT               = IZatServiceContext.MSG_IZAT_PROVIDER_BASE + 7;

    private static final int GNSS_SESSION_BEGIN         = 4;
    private static final int GNSS_SESSION_END           = 5;
    private static final int GNSS_ENGINE_ON             = 6;
    private static final int GNSS_ENGINE_OFF            = 7;

    static final String NLP_TYPE_KEY                    = "networkLocationType";

    // These get set in JNI at the time of class laoding.
    private static int IZAT_STREAM_FUSED = LocHidlIzatStreamType.FUSED;
    private static int IZAT_STREAM_NETWORK = LocHidlIzatStreamType.NETWORK;

    private static final String XT_PREFS_FILE = "xt";
    private static final String XT_NPP_LOADED = "npp_loaded";

    private static IzatProvider sNetwkProvider;
    private static IzatProvider sFusedProvider;

    private final boolean DBG;
    private final String TAG;
    private final String NAME;
    private final boolean mHighPowerCapable;
    private final int mIzatProviderType;
    private Handler mHandler;
    private AppOpsManager mAppOpsMgr;
    private WorkSource mActiveWorkSource;
    private boolean mNavigating;

    private IzatProviderClient mProviderClient;
    private final Context mAppContext;
    private static Context sContext;
    private AtomicBoolean mUserConsent = new AtomicBoolean(false);
    private static int sFusedRequestCnt;

    public void onLoad() {
        Log.d(TAG,"location provider loaded");
        mHandler.obtainMessage(MSG_ENABLE_PROVIDER).sendToTarget();
    }

    public void onUnload() {
        Log.d(TAG,"location provider unloaded");
        mHandler.obtainMessage(MSG_DISABLE_PROVIDER).sendToTarget();
    }

    @Override
    public void onSetRequest(ProviderRequest request) {
        mHandler.obtainMessage(MSG_SET_REQUEST, request).sendToTarget();

    }

    @Override
    public void onSendExtraCommand(String command, Bundle extras) {}
    @Override
    public void onFlush(OnFlushCompleteCallback callback) {}

    // called from hidl client
    private void onLocationChanged(long utcTime,
                                   long elapsedRealTimeNanos, double latitude,
                                   double longitude, boolean hasAltitude,
                                   double altitude, boolean hasVerticalUnc,
                                   float verticalUnc, boolean hasSpeed,
                                   float speed,boolean hasSpeedUnc,
                                   float speedUnc, boolean hasBearing,
                                   float bearing, boolean hasBearingUnc,
                                   float bearingUnc, boolean hasAccuracy,
                                   float accuracy, short positionSource) {
        String emptyStr = "";
        log("onLocationChanged - UTC Time: " + utcTime +
            "; Elapsed Real Time Nanos: " + elapsedRealTimeNanos +
            "; Latitude: " + latitude +"; Longitude: " + longitude +
            "; Accuracy: " + (hasAccuracy ? accuracy : emptyStr) +
            "; Altitude: " + (hasAltitude ? altitude : emptyStr) +
            "; Vertical Unc: " + (hasVerticalUnc ? verticalUnc: emptyStr) +
            "; Speed: " + (hasSpeed ? speed : emptyStr) +
            "; Speed Unc " + (hasSpeedUnc ? speedUnc: emptyStr) +
            "; Bearing: " + (hasBearing ? bearing : emptyStr) +
            "; Bearing Unc " + (hasBearingUnc ? bearingUnc: emptyStr) +
            "; positionSource: " + emptyStr +
            "; providerName: " + NAME);

        Location location = new Location(NAME);
        location.setTime(utcTime);
        location.setElapsedRealtimeNanos(elapsedRealTimeNanos);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        if (hasAltitude) location.setAltitude(altitude);
        if (hasSpeed) location.setSpeed(speed);
        if (hasBearing) location.setBearing(bearing);
        if (hasAccuracy) location.setAccuracy(accuracy);
        if (hasVerticalUnc) location.setVerticalAccuracyMeters(verticalUnc);
        if (hasSpeedUnc) location.setSpeedAccuracyMetersPerSecond(speedUnc);
        if (hasBearingUnc) location.setBearingAccuracyDegrees(bearingUnc);
        location.makeComplete();

        Bundle extras = new Bundle();
        switch (positionSource) {
        case 0:
            extras.putString(NLP_TYPE_KEY, "cell");
            break;
        case 1:
            extras.putString(NLP_TYPE_KEY, "wifi");
            break;
        }
        location.setExtras(extras);

        if (mIzatProviderType == IZAT_STREAM_NETWORK) {
            // Network locations must have a noGPSLocation extra parameter
            Bundle extras1 = location.getExtras();
            if (extras1 == null) {
                extras1 = new Bundle();
            }
            extras1.putParcelable(Location.EXTRA_NO_GPS_LOCATION, new Location(location));
            location.setExtras(extras1);
        } else {
            // else we need add an extras bundle to this location. It is a little wasting this
            // way because Location makes a copy of the pass in bundle. So this Bundle is to
            // be dereferenced as soon as the call is done.
            location.setExtras(new Bundle());
        }

        mHandler.obtainMessage(MSG_LOCATION_CHANGED, location).sendToTarget();
    }

    // called from idlclient
    private void onStatusChanged (int status) {
        mHandler.obtainMessage(MSG_STATUS_CHANGED, status).sendToTarget();
    }

    public static synchronized boolean hasNetworkProvider() {
        return sNetwkProvider != null;
    }

    public static IzatProvider getNetworkProvider(Context ctx) {
        Log.d("IzatProvider", "getNetworkProvider");
        synchronized(IzatProvider.class) {
            if (sNetwkProvider == null) {
                sNetwkProvider = new IzatProvider(
                        ctx, IZAT_STREAM_NETWORK, LocationManager.NETWORK_PROVIDER,
                        ProviderPropertiesUnbundled.create(true, false, true, false, false, false,
                        false, Criteria.POWER_LOW, Criteria.ACCURACY_COARSE));
                Log.d("IzatProvider", "getNetworkProvider finish");
            }
        }
        return sNetwkProvider;
    }

    public static IzatProvider getFusedProvider(Context ctx) {
        Log.d("IzatProvider", "getFusedProvider");
        synchronized(IzatProvider.class) {
            if (sFusedProvider == null) {
                sFusedProvider = new IzatProvider(
                        ctx, IZAT_STREAM_FUSED, LocationManager.FUSED_PROVIDER,
                        ProviderPropertiesUnbundled.create(true, false, true, false, false, false,
                        false, Criteria.POWER_LOW, Criteria.ACCURACY_FINE));
                Log.d("IzatProvider", "getFusedProvider finish");
            }
        }
        return sFusedProvider;
    }

    private IzatProvider(Context ctx, int providerType, String providerName,
                         ProviderPropertiesUnbundled properties) {
        super(ctx, "IzatProvider_"+providerName, properties.getProviderProperties());
        boolean firstInit = false;
        TAG = "IzatProvider_"+providerName;
        DBG = Log.isLoggable(TAG, Log.DEBUG);
        NAME = providerName;
        mIzatProviderType = providerType;
        mHighPowerCapable = (mIzatProviderType == IZAT_STREAM_FUSED);
        sContext = ctx;
        mAppContext = ctx.getApplicationContext();
        mHandler = new Handler(IZatServiceContext.getInstance(sContext).getLooper(), this);
        mHandler.obtainMessage(MSG_INIT).sendToTarget();
    }

    @Override
    public boolean handleMessage (Message msg) {
        int msgID = msg.what;
        log("handleMessage what - " + msgID);

        switch (msgID) {
        case MSG_INIT:
            mAppOpsMgr = mAppContext.getSystemService(AppOpsManager.class);
            mActiveWorkSource = new WorkSource();
            mNavigating = false;

            if (mIzatProviderType == IZAT_STREAM_FUSED) {
                mProviderClient = new FusedProviderClient();
            } else {
                if (LocIDLClientBase.getIDLServiceVersion().compareTo(IDLServiceVersion.V_AIDL)
                        >= 0) {
                    mProviderClient = new IzatProviderIdlClient(mIzatProviderType);
                } else {
                    mProviderClient = new IzatProviderHidlClient(mIzatProviderType);
                }
            }
            break;
        case MSG_ENABLE_PROVIDER:
            if (mIzatProviderType == IZAT_STREAM_NETWORK) {
                // Mark QNP_ACTIVE flag to show opt-in app
                try {
                    XTProxy.setNpProxyLoaded(mAppContext, true);
                } catch (Exception e) {
                    Log.e(TAG, "Exception Saving setting XT_NPP_LOADED: ", e);
                }
            }
            mProviderClient.onEnableProvider();
            break;
        case MSG_DISABLE_PROVIDER:
            if (mIzatProviderType == IZAT_STREAM_NETWORK) {
                // Mark QNP_ACTIVE flag to show opt-in app
                try {
                    XTProxy.setNpProxyLoaded(mAppContext, false);
                } catch (Exception e) {
                    Log.e(TAG, "Exception Saving setting XT_NPP_LOADED: ", e);
                }
            }
            mProviderClient.onDisableProvider();
            break;
        case MSG_SET_REQUEST:
            ProviderRequest params = (ProviderRequest)msg.obj;
            handleSetRequest(params);
            break;
        case MSG_LOCATION_CHANGED:
            Location location = (Location)msg.obj;
            log("MSG_LOCATION_CHANGED: " + location.getProvider());
            reportLocation(location);
            break;
        case MSG_STATUS_CHANGED:
            int status = (int)msg.obj;
            log("MSG_STATUS_CHANGED: Status: " + status);
            boolean wasNavigating = mNavigating;
            if (status == GNSS_SESSION_BEGIN) {
                mNavigating = true;
            } else if (status ==  GNSS_SESSION_END ||
                       status ==  GNSS_ENGINE_OFF) {
                mNavigating = false;
            }

            if (wasNavigating != mNavigating) {
                // Send an intent to toggle the GPS icon
                updateHighPowerLocationMonitoringOnClientUids(
                        mActiveWorkSource, mNavigating);
                mAppContext.sendBroadcastAsUser(
                        new Intent(LocationManager.HIGH_POWER_REQUEST_CHANGE_ACTION),
                        UserHandle.ALL);
            }
            break;
        }

        return true;
    }


    private void handleSetRequest(ProviderRequest request) {
        log("handleSetRequest");

        try {
            if (request.getWorkSource() != null) {
                // Fused Location Request, trigger Time Based Tracking session.
                WorkSource[] changes = mActiveWorkSource.setReturningDiffs(request.getWorkSource());
                if (null != changes && mNavigating && mHighPowerCapable) {
                    // start for new work, true for start
                    updateHighPowerLocationMonitoringOnClientUids(changes[0], true);
                    // finish for gone work, false for stop
                    updateHighPowerLocationMonitoringOnClientUids(changes[1], false);
                }
            }

            boolean isEmergency = request.isLocationSettingsIgnored();
            if (request.isActive()) {
                mProviderClient.OnAddRequest(Integer.MAX_VALUE,
                        request.getIntervalMillis(),
                        0,
                        LocHidlIzatHorizontalAccuracy.BLOCK,
                        isEmergency);
            } else {
                mProviderClient.OnRemoveRequest(Integer.MAX_VALUE,
                        request.getIntervalMillis(),
                        0,
                        LocHidlIzatHorizontalAccuracy.BLOCK,
                        isEmergency);
            }

        } catch (Exception e) {
            Log.w(TAG, "Exception ", e);
        }
    }
    // FLP specific API
    private void updateHighPowerLocationMonitoringOnClientUids(WorkSource newWork, boolean start) {
        if (newWork != null) {
            for (int i=0; i<newWork.size(); i++) {
                int uid = newWork.get(i);
                String packageName = newWork.getName(i);
                if (start) {
                    mAppOpsMgr.startOpNoThrow(AppOpsManager.OP_GPS, uid, packageName);
                } else {
                    mAppOpsMgr.finishOp(AppOpsManager.OP_GPS, uid, packageName);
                }
                log(String.format("%sOp - uid: %d; packageName: %s",
                                  (start ? "start" : "finish"), uid, packageName));
            }
        }
    }

    // NLP specific API
    public void setUserConsent(boolean consentAccepted) {
        // For OSnp only this can't happen (opt-in not shown)
        if (mUserConsent.compareAndSet(!consentAccepted, consentAccepted)) {
            setAllowed(consentAccepted);
        }
    }

    public boolean getUserConsent() {
        return mUserConsent.get();
    }

    private void log(String log) {
        if (DBG) Log.d(TAG, log);
    }

    //=============================================================
    //Java IDL client interface
    //=============================================================
    interface IzatProviderClient {
        public void onEnableProvider();
        public void onDisableProvider();
        public void OnAddRequest(int numFixes, long tbf, float displacement,
                int accuracy, boolean isEmergency);
        public void OnRemoveRequest(int numFixes, long tbf, float displacement,
                int accuracy, boolean isEmergency);
    }

    private class FusedProviderClient implements IzatProviderClient,
            AltitudeReceiver.IAltitudeReportCb, EsStatusReceiver.IEsStatusListener {
        private IFlpService mFlpBinder;
        private FusedLocationCallback mFusedProviderCb;
        private AltitudeReceiver mAltReceiver;
        private volatile boolean mIsUserEmergency;
        private boolean mIsInSession;
        private IzatProvider mProvider;

        public boolean getUserEsStatus() {
            return mIsUserEmergency;
        }

        private FusedProviderClient() {
            mFlpBinder = FlpServiceProvider.getInstance(sContext).getFlpBinder();
            mAltReceiver = AltitudeReceiver.getInstance(sContext);
            mFusedProviderCb = new FusedLocationCallback();
            mProvider = IzatProvider.this;
            EsStatusReceiver.getInstance(sContext).registerEsStatusListener(this);
            //retrieve LocHidlIzatProvider once to update this ALE Flp type
            try {
                ILocAidlGnss service = LocIDLClientBase.getGnssAidlService();
                if (null != service) {
                    service.getExtensionLocAidlIzatFusedProvider();
                }
            } catch (Exception e) {
                Log.e(TAG,"Exception: " + e);
            }
        }

        @Override
        public void onEnableProvider(){}
        @Override
        public void onDisableProvider(){}
        @Override
        public void OnAddRequest(int numFixes, long tbf, float displacement,
                int accuracy, boolean isEmergency) {
            //there is ongoing session
            if (mIsInSession) {
                try {
                    mFlpBinder.updateFlpSession(sFusedRequestCnt, 2, tbf, 0, 0);
                } catch(Exception e) {}
                return;
            }
            int hwId = ++sFusedRequestCnt;
            try {
                mFlpBinder.registerCallback(FlpServiceProvider.FLP_SESSION_FOREGROUND, hwId,
                        mFusedProviderCb, System.currentTimeMillis());
                mFlpBinder.startFlpSessionWithPower(hwId, 2, tbf,
                        0, 0, FlpServiceProvider.POWER_MODE_INVALID, 0);
            } catch(Exception e) {}
            mIsInSession = true;
            //Set altitudeReport callback
            mAltReceiver.setAltitudeCallback(this);
        }

        @Override
        public void OnRemoveRequest(int numFixes, long tbf, float displacement,
                int accuracy, boolean isEmergency) {
            try {
                mFlpBinder.unregisterCallback(FlpServiceProvider.FLP_SESSION_FOREGROUND,
                        mFusedProviderCb);
                mFlpBinder.stopFlpSession(sFusedRequestCnt);
                if (mIsUserEmergency) {
                }
            } catch(Exception e) {}
            mIsInSession = false;
            mAltReceiver.setAltitudeCallback(null);
        }

        @Override
        public void onLocationReportWithAlt(Location location, boolean isAltitudeAvail) {
            Log.d(TAG, "onLocationReportWithAlt: " + location.toString());
            mProvider.onLocationChanged(location.getTime(),
                    location.getElapsedRealtimeNanos(), location.getLatitude(),
                    location.getLongitude(),
                    location.hasAltitude(), location.getAltitude(),
                    location.hasVerticalAccuracy(),
                    location.getVerticalAccuracyMeters(),
                    location.hasSpeed(), location.getSpeed(),
                    location.hasSpeedAccuracy(),
                    location.getSpeedAccuracyMetersPerSecond(),
                    location.hasBearing(),
                    location.getBearing(),
                    location.hasBearingAccuracy(), location.getBearingAccuracyDegrees(),
                    location.hasAccuracy(), location.getAccuracy(), (short)-1);

        }

        @Override
        public void onStatusChanged(boolean isEmergencyMode) {
            Log.d(TAG, "Emergency mode changed to : " + isEmergencyMode);
            if (!mIsUserEmergency && isEmergencyMode) {
                //Start network location provider in emergency mode
                ProviderRequest.Builder requestBuilder =
                        new ProviderRequest.Builder();
                requestBuilder.setIntervalMillis(1000);
                requestBuilder.setLocationSettingsIgnored(true);
                //Indicate this is an emergency NLP request
                ProviderRequest request = requestBuilder.build();
                getNetworkProvider(sContext).onSetRequest(request);
            } else if (mIsUserEmergency && !isEmergencyMode) {
                ProviderRequest.Builder requestBuilder =
                        new ProviderRequest.Builder();
                requestBuilder.setIntervalMillis(Long.MAX_VALUE);
                requestBuilder.setLocationSettingsIgnored(true);
                ProviderRequest request = requestBuilder.build();
                getNetworkProvider(sContext).onSetRequest(request);
            }
            mIsUserEmergency = isEmergencyMode;
        }

        private class FusedLocationCallback extends ILocationCallback.Stub {
            @Override
            public void onLocationAvailable(Location[] locations) {
                for (Location location : locations) {
                    Log.d(TAG, "onLocationAvailable: " + location.toString());
                    //Comment out getUserEsStatus() for now for test purpose
                    if (getUserEsStatus() && mAltReceiver.isPresent()) {
                        Log.d(TAG, "In Emergency and Z Provider present, query Z");
                        mAltReceiver.getAltitudeFromLocation(location, true);
                    } else {
                        Log.d(TAG, "Not in Emergency or Z Provider not present, report location");
                        mProvider.onLocationChanged(location.getTime(),
                                location.getElapsedRealtimeNanos(), location.getLatitude(),
                                location.getLongitude(),
                                location.hasAltitude(), location.getAltitude(),
                                location.hasVerticalAccuracy(),
                                location.getVerticalAccuracyMeters(),
                                location.hasSpeed(), location.getSpeed(),
                                location.hasSpeedAccuracy(),
                                location.getSpeedAccuracyMetersPerSecond(),
                                location.hasBearing(),
                                location.getBearing(),
                                location.hasBearingAccuracy(), location.getBearingAccuracyDegrees(),
                                location.hasAccuracy(), location.getAccuracy(), (short)-1);
                    }
                }
            }
        }
    }

    //=============================================================
    //IzatProvider Java HIDL client
    //=============================================================
    private class IzatProviderHidlClient extends IzatProviderIdlClient
            implements LocIDLClientBase.IServiceDeathCb, IzatProviderClient {

        private ILocHidlIzatProvider mIzatProviderIface;
        private IzatProviderCb mIzatProviderCb;
        private int mProviderType;
        private volatile LocHidlIzatRequest mReq = null;

        private IzatProviderHidlClient(int providerType) {
            super(providerType);
            mProviderType = providerType;
            getIzatProviderIface();
            if (null != mIzatProviderIface) {
                try {
                    mIzatProviderCb = new IzatProviderCb(IzatProvider.this);
                    mIzatProviderIface.init(mIzatProviderCb);
                    registerServiceDiedCb(this);
                } catch (RemoteException e) {
                }
            }
        }

        private void getIzatProviderIface() {
            if (null == mIzatProviderIface) {
                ILocHidlGnss service = (ILocHidlGnss)getGnssService();

                if (null != service) {
                    try {
                        mIzatProviderIface = service.getExtensionLocHidlIzatNetworkProvider();
                    } catch (RemoteException e) {
                    }
                }
            }
        }

        @Override
        public void onServiceDied() {
            mIzatProviderIface = null;
            getIzatProviderIface();
            if (null != mIzatProviderIface) {
                try {
                    mIzatProviderIface.init(mIzatProviderCb);
                    if (mReq != null) {
                        mIzatProviderIface.onAddRequest(mReq);
                    }
                } catch (RemoteException e) {
                }
            }
        }

        public class IzatProviderCb extends ILocHidlIzatProviderCallback.Stub {
            private IzatProvider mProvider;

            public IzatProviderCb(IzatProvider provider) {
                mProvider = provider;
            }

            @Override
            public void onLocationChanged(vendor.qti.gnss.V1_0.LocHidlIzatLocation location) {
                IDLClientUtils.fromIDLService(TAG);
                long utcTime = location.utcTimestampInMsec;
                long elapsedRealTimeNanos = location.elapsedRealTimeInNanoSecs;
                double latitude = location.latitude;
                double longitude = location.longitude;
                boolean hasAltitude = location.hasAltitudeWrtEllipsoid;
                double altitude = location.altitudeWrtEllipsoid;
                boolean hasVerticalUnc = location.hasVertUnc;
                float verticalUnc = location.vertUnc;
                boolean hasSpeed = location.hasSpeed;
                float speed = location.speed;
                boolean hasSpeedUnc = location.hasSpeedUnc;
                float speedUnc = location.speedUnc;
                boolean hasBearing = location.hasBearing;
                float bearing = location.bearing;
                boolean hasBearingUnc = location.hasBearingUnc;
                float bearingUnc = location.bearingUnc;
                boolean hasAccuracy = location.hasHorizontalAccuracy;
                float accuracy = location.horizontalAccuracy;
                short positionSource = -1;

                if (location.hasNetworkPositionSource) {
                    switch (location.networkPositionSource) {
                        case LocHidlNetworkPositionSourceType.WIFI:
                            positionSource = 1;
                            break;
                        case LocHidlNetworkPositionSourceType.CELL:
                            positionSource = 0;
                            break;
                    }
                }

                mProvider.onLocationChanged(utcTime, elapsedRealTimeNanos, latitude, longitude,
                        hasAltitude, altitude, hasVerticalUnc, verticalUnc, hasSpeed, speed,
                        hasSpeedUnc, speedUnc, hasBearing, bearing, hasBearingUnc, bearingUnc,
                        hasAccuracy, accuracy, positionSource);
            }

            @Override
            public void onStatusChanged(int status) {
                IDLClientUtils.fromIDLService(TAG);
                mProvider.onStatusChanged(status);
            }
        }

        @Override
        public void onEnableProvider() {
            if (null != mIzatProviderIface) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mIzatProviderIface.onEnable();
                } catch (RemoteException e) {
                }
            }
        }

        @Override
        public void onDisableProvider() {
            if (null != mIzatProviderIface) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mIzatProviderIface.onDisable();
                } catch (RemoteException e) {
                }
            }
        }

        @Override
        public void OnAddRequest(int numFixes, long tbf, float displacement,
                int accuracy, boolean isEmergency) {
            LocHidlIzatRequest req = new LocHidlIzatRequest();
            req.provider = mProviderType;
            //Use LocHidlIzatStreamType::GNSS to indicate this is a emergency NLP request
            //We can have a better name when next time HIDL/AIDL upgrade
            if (isEmergency) {
                req.provider = LocHidlIzatStreamType.GNSS;
            }
            req.numUpdates = numFixes;
            req.timeIntervalBetweenFixes = tbf;
            req.smallestDistanceBetweenFixes = displacement;
            req.suggestedHorizontalAccuracy = accuracy;

            if (null != mIzatProviderIface) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mIzatProviderIface.onAddRequest(req);
                    mReq = req;
                } catch (RemoteException e) {
                }
            }
        }

        @Override
        public void OnRemoveRequest(int numFixes, long tbf, float displacement,
                int accuracy, boolean isEmergency) {
            LocHidlIzatRequest req = new LocHidlIzatRequest();
            req.provider = mProviderType;
            //Use LocHidlIzatStreamType::GNSS to indicate this is a emergency NLP request
            //We can have a better name when next time HIDL/AIDL upgrade
            if (isEmergency) {
                req.provider = LocHidlIzatStreamType.GNSS;
            }
            req.numUpdates = numFixes;
            req.timeIntervalBetweenFixes = tbf;
            req.smallestDistanceBetweenFixes = displacement;
            req.suggestedHorizontalAccuracy = accuracy;

            if (null != mIzatProviderIface) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mReq = null;
                    mIzatProviderIface.onRemoveRequest(req);
                } catch (RemoteException e) {
                }
            }
        }
    }

    //=============================================================
    //IzatProvider Java AIDL client
    //=============================================================
    private class IzatProviderIdlClient extends LocIDLClientBase
            implements LocIDLClientBase.IServiceDeathCb, IzatProviderClient {

        private final String TAG = "IzatProviderIdlClient";
        private ILocAidlIzatProvider mIzatProviderIface;
        private IzatProviderCb mIzatProviderCb;
        private int mProviderType;
        private volatile LocAidlIzatRequest mReq = null;

        private IzatProviderIdlClient(int providerType) {
            mProviderType = providerType;
            getIzatProviderIface();
            if (null != mIzatProviderIface) {
                try {
                    mIzatProviderCb = new IzatProviderCb(IzatProvider.this);
                    mIzatProviderIface.init(mIzatProviderCb);
                    registerServiceDiedCb(this);
                } catch (RemoteException e) {
                }
            }
        }

        private void getIzatProviderIface() {
            if (null == mIzatProviderIface) {
                ILocAidlGnss service = (ILocAidlGnss)getGnssAidlService();

                if (null != service) {
                    try {
                        mIzatProviderIface = service.getExtensionLocAidlIzatNetworkProvider();
                    } catch (RemoteException e) {
                    }
                }
            }
        }

        @Override
        public void onServiceDied() {
            mIzatProviderIface = null;
            getIzatProviderIface();
            if (null != mIzatProviderIface) {
                try {
                    mIzatProviderIface.init(mIzatProviderCb);
                    if (mReq != null) {
                        mIzatProviderIface.onAddRequest(mReq);
                    }
                } catch (RemoteException e) {
                }
            }
        }

        public class IzatProviderCb extends ILocAidlIzatProviderCallback.Stub {
            private IzatProvider mProvider;

            public IzatProviderCb(IzatProvider provider) {
                mProvider = provider;
            }

            @Override
            public void onLocationChanged(LocAidlIzatLocation location) {
                IDLClientUtils.fromIDLService(TAG);
                long utcTime = location.utcTimestampInMsec;
                long elapsedRealTimeNanos = location.elapsedRealTimeInNanoSecs;
                double latitude = location.latitude;
                double longitude = location.longitude;
                boolean hasAltitude = location.hasAltitudeWrtEllipsoid;
                double altitude = location.altitudeWrtEllipsoid;
                boolean hasVerticalUnc = location.hasVertUnc;
                float verticalUnc = location.vertUnc;
                boolean hasSpeed = location.hasSpeed;
                float speed = location.speed;
                boolean hasSpeedUnc = location.hasSpeedUnc;
                float speedUnc = location.speedUnc;
                boolean hasBearing = location.hasBearing;
                float bearing = location.bearing;
                boolean hasBearingUnc = location.hasBearingUnc;
                float bearingUnc = location.bearingUnc;
                boolean hasAccuracy = location.hasHorizontalAccuracy;
                float accuracy = location.horizontalAccuracy;
                short positionSource = -1;

                if (location.hasNetworkPositionSource) {
                    switch (location.networkPositionSource) {
                        case LocAidlNetworkPositionSourceType.WIFI:
                            positionSource = 1;
                            break;
                        case LocAidlNetworkPositionSourceType.CELL:
                            positionSource = 0;
                            break;
                    }
                }

                mProvider.onLocationChanged(utcTime, elapsedRealTimeNanos, latitude, longitude,
                        hasAltitude, altitude, hasVerticalUnc, verticalUnc, hasSpeed, speed,
                        hasSpeedUnc, speedUnc, hasBearing, bearing, hasBearingUnc, bearingUnc,
                        hasAccuracy, accuracy, positionSource);
            }

            @Override
            public void onStatusChanged(int status) {
                IDLClientUtils.fromIDLService(TAG);
                mProvider.onStatusChanged(status);
            }
            @Override
            public final int getInterfaceVersion() {
                return ILocAidlIzatProviderCallback.VERSION;
            }
            @Override
            public final String getInterfaceHash() {
                return ILocAidlIzatProviderCallback.HASH;
            }
        }

        @Override
        public void onEnableProvider() {
            if (null != mIzatProviderIface) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mIzatProviderIface.onEnable();
                } catch (RemoteException e) {
                }
            }
        }

        @Override
        public void onDisableProvider() {
            if (null != mIzatProviderIface) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mIzatProviderIface.onDisable();
                } catch (RemoteException e) {
                }
            }
        }

        @Override
        public void OnAddRequest(int numFixes, long tbf, float displacement,
                int accuracy, boolean isEmergency) {
            LocAidlIzatRequest req = new LocAidlIzatRequest();
            req.provider = mProviderType;
            req.numUpdates = numFixes;
            req.timeIntervalBetweenFixes = tbf;
            req.smallestDistanceBetweenFixes = displacement;
            req.suggestedHorizontalAccuracy = accuracy;

            if (null != mIzatProviderIface) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mIzatProviderIface.onAddRequest(req);
                    mReq = req;
                } catch (RemoteException e) {
                }
            }
        }

        @Override
        public void OnRemoveRequest(int numFixes, long tbf, float displacement,
                int accuracy, boolean isEmergency) {
            LocAidlIzatRequest req = new LocAidlIzatRequest();
            req.provider = mProviderType;
            req.numUpdates = numFixes;
            req.timeIntervalBetweenFixes = tbf;
            req.smallestDistanceBetweenFixes = displacement;
            req.suggestedHorizontalAccuracy = accuracy;

            if (null != mIzatProviderIface) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mReq = null;
                    mIzatProviderIface.onRemoveRequest(req);
                } catch (RemoteException e) {
                }
            }
        }
    }
}
