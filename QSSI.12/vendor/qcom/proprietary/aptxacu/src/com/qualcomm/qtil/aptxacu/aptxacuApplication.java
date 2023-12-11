/******************************************************************************
 *
 *  Copyright (c) 2020 - 2021 Qualcomm Technologies, Inc. and/or its subsidiaries.
 *  All Rights Reserved.
 *  Qualcomm Technologies, Inc. and/or its subsidiaries. Confidential and Proprietary.
 *
 ******************************************************************************/
package com.qualcomm.qtil.aptxacu;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.UserManager;
import android.preference.PreferenceManager;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class aptxacuApplication extends Application {

  private static final String TAG = "aptxacuApplication";
  Messenger mACUService = null;
  Messenger mALSService = null;
  boolean mACUBound = false;
  boolean mALSBound = false;

  private String mALSAppPackageName = "com.qualcomm.qtil.aptxals";
  private String mALSAppServiceClass = "aptxalsService";
  private String mVersionName = "Version not found";
  private String mPackageName = "not available";

  private static final int EVENT_BIND = 1;
  private static final int EVENT_ACU_APTX_AND_APTX_HD_PRIORITY = 2;
  private static final int EVENT_ACU_APTX_ADAPTIVE_96KHZ_SAMPLE_RATE = 3;
  private static final int EVENT_ACU_AUDIO_PROFILE_OVERRIDE = 4;
  private static final int EVENT_ACU_APP_AUDIO_PROFILE_PREFERENCE_LIST = 5;
  private static final int EVENT_ALS_PREFERENCES_UPDATED = 6;

  private static final int ALS_RETRY_ATTEMPTS = 5;
  private static final int ALS_RETRY_DELAY_MS = 1000;

  private final aptxacuApplicationHandler mHandler = new aptxacuApplicationHandler();
  private List<OnStateChangedListener> mListener = new ArrayList<OnStateChangedListener>();

  private final ServiceConnection mACUServiceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
      Log.i(TAG, "onServiceConnected: " + className);
      mACUService = new Messenger(service);
      mACUBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName className) {
      Log.i(TAG, "onServiceDisconnected: " + className);
      mACUService = null;
      mACUBound = false;
    }
  };

  private ServiceConnection mALSConnection = new ServiceConnection() {
    public void onServiceConnected(ComponentName className, IBinder service) {
      Log.i(TAG, "onServiceConnected: " + className);
      mALSService = new Messenger(service);
      mALSBound = true;
    }

    public void onServiceDisconnected(ComponentName className) {
      Log.i(TAG, "onServiceDisconnected: " + className);
      mALSService = null;
      mALSBound = false;
    }
  };

  @Override
  public void onCreate() {
    Log.i(TAG, "onCreate");
    super.onCreate();

    try {
      mPackageName = getPackageName();
      mVersionName = getPackageManager().getPackageInfo(mPackageName, 0).versionName;
    } catch (NameNotFoundException e) {
      Log.e(TAG, "Exception requesting versions: " + e.getLocalizedMessage());
    }

    Log.i(TAG, "aptxacu (aptX ACU app) Version Name: " + mVersionName);
    Log.i(TAG, "aptxacu (aptX ACU app) Package Name: " + mPackageName);

    final Intent mainServiceIntent = new Intent(this, aptxacuService.class);
    mainServiceIntent.setAction(aptxacuService.class.getName());
    bindService(mainServiceIntent, mACUServiceConnection, BIND_AUTO_CREATE);

    Message msg = mHandler.obtainMessage(EVENT_BIND);
    msg.arg1 = 0;
    mHandler.sendMessage(msg);

    try {
      if (checkPermissions() == false) {
        throw new RuntimeException("insufficient permissions to start");
      }
    } catch (Exception e) {
      Log.e(TAG, "Failed to create app: " + e);
      throw new RuntimeException("cannot continue");
    }
  }

  private boolean checkPermissions() {
    // Make sure this contains all permissions listed in the app Manifest
    String[] permissions = {
      "RECEIVE_BOOT_COMPLETED",
      "QUERY_ALL_PACKAGES",
    };

    for (String perm : permissions) {
      //Log.d(TAG, "Checking for granting of permission: android.permission." + perm);
      String manifestPermission = "android.permission." + perm;
      if ((checkSelfPermission(manifestPermission) != PackageManager.PERMISSION_GRANTED)) {
        Log.e(TAG, "android.permission." + perm + " denied");
        return false;
      }
    }
    return true;
  }

  public String GetAptxAndAptxHdPriority() {
    String value = "DEFAULT";
    try {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
      value = prefs.getString(aptxacuALSDefs.APTX_AND_APTX_HD_PRIORITY, "DEFAULT");
    } catch (Exception e) {
      Log.e(TAG, "Exception: " + e);
    }
    return value;
  }

  public String GetAptxAdaptive96KHzSampleRate() {
    String value = "OFF";
    try {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
      value = prefs.getString(aptxacuALSDefs.APTX_ADAPTIVE_96KHZ_SAMPLE_RATE, "OFF");
    } catch (Exception e) {
      Log.e(TAG, "Exception: " + e);
    }
    return value;
  }

  public String GetAudioProfileOverride() {
    String value = "AUTO_ADJUST";
    try {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
      value = prefs.getString(aptxacuALSDefs.AUDIO_PROFILE_OVERRIDE, "AUTO_ADJUST");
    } catch (Exception e) {
      Log.e(TAG, "Exception: " + e);
    }
    return value;
  }

  public String GetAppAudioProfilePreferenceList() {
    String value = "";
    try {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
      value = prefs.getString(aptxacuALSDefs.APP_AUDIO_PROFILE_PREFERENCE_LIST, "");
    } catch (Exception e) {
      Log.e(TAG, "Exception: " + e);
    }
    return value;
  }

  public void SetAptxAndAptxHdPriority(String value) {
    try {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
      Editor editor = prefs.edit();
      editor.putString(aptxacuALSDefs.APTX_AND_APTX_HD_PRIORITY, value);
      editor.commit();
    } catch (Exception e) {
      Log.e(TAG, "Exception: " + e);
    }
  }

  public void SetAptxAdaptive96KHzSampleRate(String value) {
    try {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
      Editor editor = prefs.edit();
      editor.putString(aptxacuALSDefs.APTX_ADAPTIVE_96KHZ_SAMPLE_RATE, value);
      editor.commit();
    } catch (Exception e) {
      Log.e(TAG, "Exception: " + e);
    }
  }

  public void SetAudioProfileOverride(String value) {
    try {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
      Editor editor = prefs.edit();
      editor.putString(aptxacuALSDefs.AUDIO_PROFILE_OVERRIDE, value);
      editor.commit();
    } catch (Exception e) {
      Log.e(TAG, "Exception: " + e);
    }
  }

  public void SetAppAudioProfilePreferenceList(String value) {
    try {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
      Editor editor = prefs.edit();
      editor.putString(aptxacuALSDefs.APP_AUDIO_PROFILE_PREFERENCE_LIST, value);
      editor.commit();
      acuAudioProfilePreferenceListUpdated();
    } catch (Exception e) {
      Log.e(TAG, "Exception: " + e);
    }
  }

  public void alsPreferencesUpdated(Intent intent) {
    intent.setComponent(new ComponentName(mALSAppPackageName, mALSAppPackageName + "." + mALSAppServiceClass));
    Message msg = mHandler.obtainMessage(EVENT_ALS_PREFERENCES_UPDATED);
    msg.obj = intent;
    mHandler.sendMessage(msg);
  }

  public void acuAptxAndAptxHdPriority() {
    Message msg = mHandler.obtainMessage(EVENT_ACU_APTX_AND_APTX_HD_PRIORITY);
    mHandler.sendMessage(msg);
  }

  public void acuAptxAdaptive96KHzSampleRate() {
    Message msg = mHandler.obtainMessage(EVENT_ACU_APTX_ADAPTIVE_96KHZ_SAMPLE_RATE);
    mHandler.sendMessage(msg);
  }

  public void acuAudioProfileOverride() {
    Message msg = mHandler.obtainMessage(EVENT_ACU_AUDIO_PROFILE_OVERRIDE);
    mHandler.sendMessage(msg);
  }

  public void acuAudioProfilePreferenceListUpdated() {
    Message msg = mHandler.obtainMessage(EVENT_ACU_APP_AUDIO_PROFILE_PREFERENCE_LIST);
    mHandler.sendMessage(msg);
  }

  private void retryMessage(Message msg) {
    int retry = msg.arg2;
    if (retry < ALS_RETRY_ATTEMPTS) {
      msg.arg2 = retry + 1;
      mHandler.sendMessageDelayed(msg, ALS_RETRY_DELAY_MS);
    }
    return;
  }

  private class aptxacuApplicationHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
      Intent intent = (Intent) msg.obj;
      Context context = getApplicationContext();

      switch (msg.what) {
        case EVENT_BIND:
        {
          bind();
          break;
        }

        case EVENT_ACU_APTX_AND_APTX_HD_PRIORITY:
        {
          if (!mALSBound) {
            bind();
            retryMessage(msg);
            return;
          }

          Intent intentALS = new Intent(context, aptxacuService.class);
          intentALS.setAction(aptxacuALSDefs.ACTION_ACU_APTX_AND_APTX_HD_PRIORITY);
          intentALS.putExtra(aptxacuALSDefs.APTX_AND_APTX_HD_PRIORITY, GetAptxAndAptxHdPriority());

          Message msgALS = obtainMessage(aptxacuALSDefs.MESSAGE_TYPE_ALS);
          msgALS.obj = intentALS;

          try {
            mALSService.send(msgALS);
          } catch (RemoteException e) {
            e.printStackTrace();
          }
          break;
        }

        case EVENT_ACU_APTX_ADAPTIVE_96KHZ_SAMPLE_RATE:
        {
          if (!mALSBound) {
            bind();
            retryMessage(msg);
            return;
          }

          Intent intentALS = new Intent(context, aptxacuService.class);
          intentALS.setAction(aptxacuALSDefs.ACTION_ACU_APTX_ADAPTIVE_96KHZ_SAMPLE_RATE);
          intentALS.putExtra(aptxacuALSDefs.APTX_ADAPTIVE_96KHZ_SAMPLE_RATE, GetAptxAdaptive96KHzSampleRate());

          Message msgALS = obtainMessage(aptxacuALSDefs.MESSAGE_TYPE_ALS);
          msgALS.obj = intentALS;

          try {
            mALSService.send(msgALS);
          } catch (RemoteException e) {
            e.printStackTrace();
          }
          break;
        }

        case EVENT_ACU_AUDIO_PROFILE_OVERRIDE:
        {
          if (!mALSBound) {
            bind();
            retryMessage(msg);
            return;
          }

          Intent intentALS = new Intent(context, aptxacuService.class);
          intentALS.setAction(aptxacuALSDefs.ACTION_ACU_AUDIO_PROFILE_OVERRIDE);
          intentALS.putExtra(aptxacuALSDefs.AUDIO_PROFILE_OVERRIDE, GetAudioProfileOverride());

          Message msgALS = obtainMessage(aptxacuALSDefs.MESSAGE_TYPE_ALS);
          msgALS.obj = intentALS;

          try {
            mALSService.send(msgALS);
          } catch (RemoteException e) {
            e.printStackTrace();
          }
          break;
        }

        case EVENT_ACU_APP_AUDIO_PROFILE_PREFERENCE_LIST:
        {
          if (!mALSBound) {
            bind();
            retryMessage(msg);
            return;
          }

          Intent intentALS = new Intent(context, aptxacuService.class);
          intentALS.setAction(aptxacuALSDefs.ACTION_ACU_APP_AUDIO_PROFILE_PREFERENCE_LIST);
          intentALS.putExtra(aptxacuALSDefs.APP_AUDIO_PROFILE_PREFERENCE_LIST, GetAppAudioProfilePreferenceList());

          Message msgALS = obtainMessage(aptxacuALSDefs.MESSAGE_TYPE_ALS);
          msgALS.obj = intentALS;

          try {
            mALSService.send(msgALS);
          } catch (RemoteException e) {
            e.printStackTrace();
          }
          break;
        }

        case EVENT_ALS_PREFERENCES_UPDATED:
        {
          if (intent == null) {
            Log.e(TAG, "missing intent");
            return;
          }

          String aptx_and_aptx_hd_priority = intent.getStringExtra(aptxacuALSDefs.APTX_AND_APTX_HD_PRIORITY);
          String aptx_adaptive_96khz_sample_rate = intent.getStringExtra(aptxacuALSDefs.APTX_ADAPTIVE_96KHZ_SAMPLE_RATE);
          String audio_profile_override = intent.getStringExtra(aptxacuALSDefs.AUDIO_PROFILE_OVERRIDE);

          SetAptxAndAptxHdPriority(aptx_and_aptx_hd_priority);
          SetAptxAdaptive96KHzSampleRate(aptx_adaptive_96khz_sample_rate);
          SetAudioProfileOverride(audio_profile_override);

          // Setting Fragment, if shown will have registered to receive updates
          updateListeners();
          break;
        }

        default:
          break;
      }
    }
  }

  public void registerOnStateChangedListener(OnStateChangedListener listener) {
    mListener.add(listener);
  }

  public void unregisterOnStateChangedListener(OnStateChangedListener settingsScreen) {
    mListener.remove(settingsScreen);
  }

  private void updateListeners() {
    for (OnStateChangedListener settingsScreen : mListener) {
      settingsScreen.onStateChanged(this);
    }
  }

  public interface OnStateChangedListener {
    public void onStateChanged(aptxacuApplication app);
  }

  private void bind() {
    try {
      Log.i(TAG, "bind to " + mALSAppPackageName + "." + mALSAppServiceClass);
      Intent intent = new Intent();
      intent.setAction(mALSAppPackageName + "." + mALSAppServiceClass);
      intent.setComponent(new ComponentName(mALSAppPackageName, mALSAppPackageName + "." + mALSAppServiceClass));
      bindService(intent, mALSConnection, Context.BIND_AUTO_CREATE);
    } catch (SecurityException e) {
      Log.e(TAG, "can't bind to " + mALSAppPackageName + "." + mALSAppServiceClass);
    }
  }

  private static boolean isUserUnlocked(Context context) {
    UserManager userManager = context.getSystemService(UserManager.class);
    return userManager.isUserUnlocked();
  }

  public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
    synchronized (this) {
      pw.println("aptxacu (aptX ACU app) Version Name: " + mVersionName);
      pw.println("aptxacu (aptX ACU app) Package Name: " + mPackageName);
      pw.println("aptxacu Bound: " + mACUBound);

      if (mACUBound && isUserUnlocked(this)) {
        pw.println("");
        pw.println("User Preferences");
        pw.println("Set aptX and aptX HD priority: " + GetAptxAndAptxHdPriority());
        pw.println("Enable use 96KHz Samplerate: " + GetAptxAdaptive96KHzSampleRate());
        pw.println("Set audio profile override: " + GetAudioProfileOverride());
        pw.println("Get app audio profile preference list: " + GetAppAudioProfilePreferenceList());
      }
      pw.flush();
    }
  }
}
