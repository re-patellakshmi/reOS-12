/*====*====*====*====*====*====*====*====*====*====*====*====*====*====*====*
  Copyright (c) 2020-2021 Qualcomm Technologies, Inc.
  All Rights Reserved.
  Confidential and Proprietary - Qualcomm Technologies, Inc.
=============================================================================*/
package com.qualcomm.location.utils;

import android.util.Log;
import android.location.Location;
import android.os.HandlerThread;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.content.Context;
import com.qualcomm.location.izat.IzatService.ISystemEventListener;
import com.qualcomm.location.osagent.OsAgent;
import com.qualcomm.location.izat.flp.FlpServiceProvider;
import com.qti.flp.IFlpService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class IZatServiceContext {
    //Handler messages base
    public static final int MSG_FLP_BASE =                  0;
    public static final int MSG_NET_INITIATED_BASE =        100;
    public static final int MSG_LOCATION_SERVICE_BASE =     200;
    public static final int MSG_IZAT_PROVIDER_BASE =        300;
    public static final int MSG_NPPROXY_BASE =              400;
    public static final int MSG_OSAGENT_BASE =              500;
    public static final int MSG_RILINFO_MONITOR_BASE =      600;
    public static final int MSG_GEOCODER_PROXY_BASE =       700;
    public static final int MSG_ALTITUDE_RECEIVER =         800;

    private static final String TAG = "IZatServiceContext";
    private static final int FEATURE_BIT_PRECISE_LOCATION_IS_SUPPORTED = 0x100;
    private static final int CORE_POOL_SIZE = 1;
    private static final int MAX_POOL_SIZE = 10;
    private static final int KEEP_ALIVE_TIME = 60;
    private static IZatServiceContext sInstance = null;
    private final OsAgent mOsAgent;
    private final Context mContext;
    //** Static flag to track whether the ondevice logging has been initialized or not
    public static boolean sIsDiagJNILoaded = false;
    private int mFlpFeatureMask = -1;

    private final ThreadPoolExecutor mThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    private final HandlerThread mHandlerThd =
            new HandlerThread(IZatServiceContext.class.getSimpleName());
    private final Looper mLooper;

    static {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            try {
                System.loadLibrary("locsdk_diag_jni");
                sIsDiagJNILoaded = true;
            } catch (Throwable e) {
                Log.e(TAG, "Failed to loadLibrary liblocsdk_diag_jni: " + e);
            }
        }
    }
    private IZatServiceContext(Context ctx) {
        mContext = ctx;
        mHandlerThd.start();
        mLooper = mHandlerThd.getLooper();
        mOsAgent = OsAgent.GetInstance(mContext, mLooper);
    }

    public Looper getLooper() {
        return mLooper;
    }

    public Context getContext() {
        return mContext;
    }

    public boolean isPreciseLocationSupported() {
        if (mFlpFeatureMask == -1) {
            IFlpService flpBinder = FlpServiceProvider.getInstance(mContext).getFlpBinder();
            try {
                mFlpFeatureMask = flpBinder.getAllSupportedFeatures();
            } catch (Exception e) {
                Log.e(TAG, "Failed to call flpBinder.getAllSupportedFeatures()" + e);
            }
        }
        return (mFlpFeatureMask & FEATURE_BIT_PRECISE_LOCATION_IS_SUPPORTED) >= 0;
    }

    public synchronized static IZatServiceContext getInstance(Context ctx) {
        if (null == sInstance) {
            sInstance = new IZatServiceContext(ctx);
        }
        return sInstance;
    }

    public void registerSystemEventListener(int sysEventMsgId, ISystemEventListener listener) {
        mOsAgent.registerObserver(sysEventMsgId, listener);
    }

    public void diagLogBatchedFixes(Location[] locations) {
        if (sIsDiagJNILoaded) {
            native_diag_log_flp_batch(locations);
        }
    }

    public void executeRunnable(Runnable r) {
        mThreadPool.execute(r);
    }

    // native diag interface API
    public native void native_diag_log_flp_batch(Location[] locations);
}
