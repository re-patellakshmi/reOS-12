/*
 * Copyright (c) 2015,2017-2018 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 *
 * Copyright (c) 2011-2014, The Linux Foundation. All rights reserved.
 */

package com.qualcomm.qti;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.Trace;
import android.util.Log;
import java.util.ArrayList;
import android.os.Build;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.Intent;

public class Performance
{
    private static final String TAG = "Perf";
    private static final String PERF_SERVICE_BINDER_NAME = "vendor.perfservice";
    private static final boolean sPerfServiceDisabled = false;
    private static final boolean DEBUG = false;

    private static IBinder clientBinder;
    private static IBinder sPerfServiceBinder;
    private static IPerfManager sPerfService;
    private static boolean sLoaded = false;
    private final Object mLock = new Object();
    private static PerfServiceDeathRecipient sPerfServiceDeathRecipient;
    private static boolean sIsPlatformOrPrivApp = true;
    private static boolean sIsUntrustedDomain = false;
    private int UXE_EVENT_BINDAPP = 2;
    private static Boolean sIsChecked = false;
    public static final int VENDOR_FEEDBACK_WORKLOAD_TYPE = 0x00001601;
    public static final int GAME = 2;
    private static boolean RestrictUnTrustedAppAccess = false;
    private static double DEF_PERF_HAL =  2.2f;

    public static final int VENDOR_APP_LAUNCH_HINT = 0x00001081;
    public static final int TYPE_START_PROC = 101;
    private static Context mContext;
    private static Intent mIntent;

    /** @hide */
    public Performance() {
    }

    /** @hide */
    public Performance(Context context) {
        mContext = context;
        mIntent = new Intent();
        if (mIntent != null) {
            mIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            mIntent.setAction("com.qualcomm.qti.workloadclassifier.APP_LAUNCH");
        }

        if (DEBUG)Trace.traceBegin(Trace.TRACE_TAG_ALWAYS, "Create Performance instance");
        synchronized (Performance.class) {
            if (!sLoaded) {
                connectPerfServiceLocked();
                if (sPerfService == null && !sPerfServiceDisabled)
                    Log.e(TAG, "Perf service is unavailable.");
                else
                    sLoaded = true;
            }
        }
        checkAppPlatformSigned(context);
        if (DEBUG)Trace.traceEnd(Trace.TRACE_TAG_ALWAYS);
    }

    /** @hide */
    public Performance(boolean isUntrusterdDomain) {
        sIsUntrustedDomain = isUntrusterdDomain;
    }

    /* The following are the PerfLock API return values*/
    /** @hide */ public static final int REQUEST_FAILED = -1;
    /** @hide */ public static final int REQUEST_SUCCEEDED = 0;

    /** @hide */ private int mHandle = 0;


    private void connectPerfServiceLocked() {
        if (sPerfService != null || sPerfServiceDisabled) return;

        if (DEBUG)Trace.traceBegin(Trace.TRACE_TAG_ALWAYS, "connectPerfServiceLocked");
        Log.i(TAG, "Connecting to perf service.");

        sPerfServiceBinder = ServiceManager.getService(PERF_SERVICE_BINDER_NAME);
        if (sPerfServiceBinder == null) {
            Log.e(TAG, "Perf service is now down, set sPerfService as null.");
            if (DEBUG)Trace.traceEnd(Trace.TRACE_TAG_ALWAYS);
            return;
        }
        try {
            sPerfServiceDeathRecipient = new PerfServiceDeathRecipient();
            //link perfDeathRecipient to binder to receive DeathRecipient call back.
            sPerfServiceBinder.linkToDeath(sPerfServiceDeathRecipient, 0);
        } catch (RemoteException e) {
            Log.e(TAG, "Perf service is now down, leave sPerfService as null.");
            if (DEBUG)Trace.traceEnd(Trace.TRACE_TAG_ALWAYS);
            return;
        }
        if (sPerfServiceBinder != null)
            sPerfService = IPerfManager.Stub.asInterface(sPerfServiceBinder);

        if (sPerfService != null) {
            try {
                clientBinder = new Binder();
                sPerfService.setClientBinder(clientBinder);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (DEBUG)Trace.traceEnd(Trace.TRACE_TAG_ALWAYS);
    }

    /* The following functions are the PerfLock APIs*/
    /** @hide */
    public int perfLockAcquire(int duration, int... list) {
        if (sIsPlatformOrPrivApp && !sIsUntrustedDomain) {
            mHandle = native_perf_lock_acq(mHandle, duration, list);
        } else {
            synchronized (mLock) {
                try {
                    if (sPerfService != null && RestrictUnTrustedAppAccess == false)
                    {
                        mHandle = sPerfService.perfLockAcquire(duration, list);
                    }
                    else
                        return REQUEST_FAILED;

                } catch (RemoteException e) {
                    Log.e(TAG, "Error calling perfLockAcquire", e);
                    return REQUEST_FAILED;
                }
            }
        }
        if (mHandle <= 0)
            return REQUEST_FAILED;
        else
            return mHandle;
    }

    /** @hide */
    public int perfLockRelease() {
        int retValue = REQUEST_SUCCEEDED;
        if (sIsPlatformOrPrivApp && !sIsUntrustedDomain) {
            retValue = native_perf_lock_rel(mHandle);
            mHandle = 0;
            return retValue;
        } else {
            synchronized (mLock) {
                try {
                    if (sPerfService != null && RestrictUnTrustedAppAccess == false)
                    {
                        retValue = sPerfService.perfLockReleaseHandler(mHandle);
                    }
                    else
                        retValue = REQUEST_FAILED;
                } catch (RemoteException e) {
                    Log.e(TAG, "Error calling perfLockRelease", e);
                    return REQUEST_FAILED;
                }
            }
            return retValue;
        }
    }

    /** @hide */
    public int perfLockReleaseHandler(int _handle) {
        if (sIsPlatformOrPrivApp && !sIsUntrustedDomain) {
            return native_perf_lock_rel(_handle);
        } else {
            int retValue = REQUEST_SUCCEEDED;
            synchronized (mLock) {
                try {
                    if (sPerfService != null && RestrictUnTrustedAppAccess == false)
                    {
                        retValue = sPerfService.perfLockReleaseHandler(_handle);
                    }
                    else
                        retValue = REQUEST_FAILED;
                } catch (RemoteException e) {
                    Log.e(TAG, "Error calling perfLockRelease(handle)", e);
                    return REQUEST_FAILED;
                }
            }
            return retValue;
        }
    }

    /* Thread to send launch broadcast for games */
    private class SendGameLaunchBroadcast implements Runnable {
        public String pkgName;

        public SendGameLaunchBroadcast(String pkgName) {
            this.pkgName = pkgName;
        }

        public void run() {
            if (mContext != null && mIntent != null &&
                (perfGetFeedback(VENDOR_FEEDBACK_WORKLOAD_TYPE, pkgName) == GAME)) {
                mIntent.putExtra("PKG_NAME", pkgName);
                mContext.sendBroadcast(mIntent);
            }
            return;
        }
    }

    /** @hide */
    public int perfHint(int hint, String userDataStr, int userData1, int userData2) {

        //send broadcast on game launch hint
        if (mContext != null && mIntent != null && hint == VENDOR_APP_LAUNCH_HINT
                                                   && userData2 == TYPE_START_PROC)
        {
            new Thread(new SendGameLaunchBroadcast(userDataStr)).start();
        }

        if (sIsPlatformOrPrivApp && !sIsUntrustedDomain) {
            mHandle = native_perf_hint(hint, userDataStr, userData1, userData2);
        } else {
            synchronized (mLock) {
                try {
                    if (sPerfService != null && RestrictUnTrustedAppAccess == false)
                    {
                        mHandle = sPerfService.perfHint(hint, userDataStr, userData1, userData2,
                                Process.myTid());
                    }
                    else
                        return REQUEST_FAILED;
                } catch (RemoteException e) {
                    Log.e(TAG, "Error calling perfHint", e);
                    return REQUEST_FAILED;
                }
            }
        }
        if (mHandle <= 0)
            return REQUEST_FAILED;
        else
            return mHandle;
    }

    /** @hide */
    public int perfGetFeedback(int req, String pkg_name) {
        return perfGetFeedbackExtn(req, pkg_name, 0);
    }

    /** @hide */
    public int perfGetFeedbackExtn(int req, String pkg_name, int numArgs,int... list) {
        int mInfo = 0;
        if (sIsPlatformOrPrivApp && !sIsUntrustedDomain) {
            mInfo = native_perf_get_feedback_extn(req, pkg_name, numArgs, list);
        } else {
            synchronized (mLock) {
                try {
                    if (sPerfService != null)
                        mInfo = sPerfService.perfGetFeedbackExtn(req, pkg_name, Process.myTid(), numArgs, list);
                    else
                        return REQUEST_FAILED;
                } catch (RemoteException e) {
                    Log.e(TAG, "Error calling perfGetFeedbackExtn", e);
                    return REQUEST_FAILED;
                }
            }
        }
        if (mInfo <= 0)
            return REQUEST_FAILED;
        else
            return mInfo;
    }

    public int perfIOPrefetchStart(int PId, String Pkg_name, String Code_path)
    {
        return native_perf_io_prefetch_start(PId,Pkg_name, Code_path);
    }

    public int perfIOPrefetchStop(){
        return native_perf_io_prefetch_stop();
    }

    public int perfUXEngine_events(int opcode, int pid, String pkg_name, int lat, String CodePath)
    {

        if (opcode == UXE_EVENT_BINDAPP) {
            synchronized (mLock) {
                try {
                    if (sPerfService != null)
                        mHandle = sPerfService.perfUXEngine_events(opcode, pid, pkg_name, lat);
                    else
                        return REQUEST_FAILED;
                } catch (RemoteException e) {
                    Log.e(TAG, "Error calling perfHint", e);
                    return REQUEST_FAILED;
                }
            }

        } else {
            mHandle = native_perf_uxEngine_events(opcode, pid, pkg_name, lat);
        }
        if (mHandle <= 0)
            return REQUEST_FAILED;
        else
            return mHandle;
    }

    public int perfLockAcqAndRelease(int handle, int duration, int numArgs,int reserveNumArgs, int... list) {
        if (sIsPlatformOrPrivApp && !sIsUntrustedDomain) {
            mHandle = native_perf_lock_acq_rel(handle, duration, numArgs, reserveNumArgs, list);
        } else {
            synchronized (mLock) {

                try {
                    if (sPerfService != null && RestrictUnTrustedAppAccess == false) {
                        mHandle = sPerfService.perfLockAcqAndRelease(handle, duration, numArgs, reserveNumArgs, list);
                    }
                    else
                        return REQUEST_FAILED;

                } catch (RemoteException e) {
                    Log.e(TAG, "Error calling perfLockAcqAndRelease", e);
                    return REQUEST_FAILED;
                }
            }
        }
        if (mHandle <= 0)
            return REQUEST_FAILED;
        else
            return mHandle;
    }

    /** @hide */
    public void perfEvent(int eventId, String pkg_name, int numArgs, int... list) {
        if (sIsPlatformOrPrivApp && !sIsUntrustedDomain) {
            native_perf_event(eventId, pkg_name, numArgs, list);
        } else {
            synchronized (mLock) {
                try {
                    if (sPerfService != null)
                        sPerfService.perfEvent(eventId, pkg_name, Process.myTid(), numArgs, list);
                    else
                        return;
                } catch (RemoteException e) {
                    Log.e(TAG, "Error calling perfEvent", e);
                    return;
                }
            }
        }
    }

    /** @hide */
    public int perfHintAcqRel(int handle,int hint, String pkg_name, int duration, int type, int numArgs, int... list) {
        if (sIsPlatformOrPrivApp && !sIsUntrustedDomain) {
            mHandle = native_perf_hint_acq_rel(handle, hint, pkg_name, duration, type, numArgs, list);
        } else {
            synchronized (mLock) {
                try {
                    if (sPerfService != null)
                        mHandle = sPerfService.perfHintAcqRel(handle, hint, pkg_name, duration, type, Process.myTid(), numArgs, list);
                    else
                        return REQUEST_FAILED;
                } catch (RemoteException e) {
                    Log.e(TAG, "Error calling perfHintAcqRel", e);
                    return REQUEST_FAILED;
                }
            }
        }
        if (mHandle <= 0)
            return REQUEST_FAILED;
        else
            return mHandle;
    }

    /** @hide */
    public int perfHintRenew(int handle,int hint, String pkg_name, int duration, int type, int numArgs, int... list) {
        if (sIsPlatformOrPrivApp && !sIsUntrustedDomain) {
            mHandle = native_perf_hint_renew(handle, hint, pkg_name, duration, type, numArgs, list);
        } else {
            synchronized (mLock) {
                try {
                    if (sPerfService != null)
                        mHandle = sPerfService.perfHintRenew(handle, hint, pkg_name, duration, type, Process.myTid(), numArgs, list);
                    else
                        return REQUEST_FAILED;
                } catch (RemoteException e) {
                    Log.e(TAG, "Error calling perfHintRenew", e);
                    return REQUEST_FAILED;
                }
            }
        }
        if (mHandle <= 0)
            return REQUEST_FAILED;
        else
            return mHandle;
    }


    public String perfUXEngine_trigger(int opcode)
    {
        return native_perf_uxEngine_trigger(opcode);
    }

    private void checkAppPlatformSigned(Context context) {
        synchronized(sIsChecked){
            if (context == null || sIsChecked)return;
            if (DEBUG)Trace.traceBegin(Trace.TRACE_TAG_ALWAYS, "checkAppPlatformSigned");
            try {
                PackageInfo pkg = context.getPackageManager().getPackageInfo(
                        context.getPackageName(), PackageManager.GET_SIGNATURES);
                PackageInfo sys = context.getPackageManager().getPackageInfo(
                        "android", PackageManager.GET_SIGNATURES);
                sIsPlatformOrPrivApp =
                        (pkg != null && pkg.signatures != null
                        && pkg.signatures.length > 0
                        && sys.signatures[0].equals(pkg.signatures[0]))
                        || pkg.applicationInfo.isPrivilegedApp();

                /* Older apps(less than Android-P(SDK - 28)) are using the perf locks directly, below logic will restrict the access */
                if ((pkg != null
                    && pkg.applicationInfo != null
                    && pkg.applicationInfo.targetSdkVersion < Build.VERSION_CODES.P
                    && pkg.applicationInfo.getHiddenApiEnforcementPolicy() == ApplicationInfo.HIDDEN_API_ENFORCEMENT_ENABLED))
                {
                    RestrictUnTrustedAppAccess = true;
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "packageName is not found.");
                sIsPlatformOrPrivApp = true;
            }
            sIsChecked = true;
            if (DEBUG)Log.d(TAG, " perftest packageName : " + context.getPackageName() + " sIsPlatformOrPrivApp is :" + sIsPlatformOrPrivApp);
            if (DEBUG)Trace.traceEnd(Trace.TRACE_TAG_ALWAYS);
        }
    }

    private final class PerfServiceDeathRecipient implements IBinder.DeathRecipient {
        public void binderDied() {
            synchronized(mLock) {
                Log.e(TAG, "Perf Service died.");
                if (sPerfServiceBinder != null)
                    sPerfServiceBinder.unlinkToDeath(this, 0);
                sPerfServiceBinder = null;
                sPerfService = null;
            }
        }
    }

    public String perfGetProp(String prop_name, String def_val) {
        if (sIsPlatformOrPrivApp && !sIsUntrustedDomain) {
            return native_perf_get_prop(prop_name, def_val);
        } else {
            return def_val;
        }
    }

    public double perfGetHalVer() {
        double def_val = DEF_PERF_HAL;
        if (sIsPlatformOrPrivApp && !sIsUntrustedDomain) {
            def_val =  native_perf_get_hal_ver();
        }
        return def_val;
    }
    private native int  native_perf_lock_acq(int handle, int duration, int list[]);
    private native int  native_perf_lock_rel(int handle);
    private native int  native_perf_hint(int hint, String userDataStr, int userData1, int userData2);
    private native int  native_perf_get_feedback(int req, String pkg_name);
    private native int  native_perf_get_feedback_extn(int req, String pkg_name, int numArgs, int list[]);
    private native int  native_perf_io_prefetch_start(int pid, String pkg_name, String Code_path);
    private native int  native_perf_io_prefetch_stop();
    private native int  native_perf_uxEngine_events(int opcode, int pid, String pkg_name, int lat);
    private native String  native_perf_uxEngine_trigger(int opcode);
    private native String native_perf_get_prop(String prop_name, String def_val);
    private native int  native_perf_lock_acq_rel(int handle, int duration, int numArgs,int reserveNumArgs, int list[]);
    private native void  native_perf_event(int eventId, String pkg_name, int numArgs, int list[]);
    private native int  native_perf_hint_acq_rel(int handle, int hint, String pkg_name, int duration, int hint_type, int numArgs, int list[]);
    private native int  native_perf_hint_renew(int handle, int hint, String pkg_name, int duration, int hint_type, int numArgs, int list[]);
    private native double  native_perf_get_hal_ver();

}
