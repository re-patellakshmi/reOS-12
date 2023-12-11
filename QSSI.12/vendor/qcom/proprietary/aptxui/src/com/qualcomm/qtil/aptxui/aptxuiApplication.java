/******************************************************************************
 *
 *  Copyright (c) 2020 - 2021 Qualcomm Technologies, Inc. and/or its subsidiaries.
 *  All Rights Reserved.
 *  Qualcomm Technologies, Inc. and/or its subsidiaries. Confidential and Proprietary.
 *
 ******************************************************************************/
package com.qualcomm.qtil.aptxui;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import java.lang.reflect.Field;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class aptxuiApplication extends Application {

  private static final String TAG = "aptxuiApplication";

  Messenger mALSService = null;
  boolean mALSBound = false;

  private String mALSAppPackageName = "com.qualcomm.qtil.aptxals";
  private String mALSAppServiceClass = "aptxalsService";

  private String mUIVersionName = "Version not found";
  private String mUIPackageName = "not available";

  private static int mCodec = aptxuiALSDefs.SOURCE_CODEC_TYPE_NONE;
  private static String mAudioProfile = "";
  private static String mConnectedDeviceAddress = "";
  private static String mIsQssSupported = "";

  private static int mPrevCodec = aptxuiALSDefs.SOURCE_CODEC_TYPE_NONE;
  private static String mPrevAudioProfile = "";
  private static String mPrevConnectedDeviceAddress = "";

  private boolean mQssEnabled = false;
  private boolean mNotifyQssSupport = false;

  private static final int EVENT_BIND = 1;
  private static final int EVENT_CODEC_CONFIGURED = 2;

  private final aptxuiApplicationHandler mHandler = new aptxuiApplicationHandler();

  private ServiceConnection mUIConnection = new ServiceConnection() {
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
    super.onCreate();

    try {
      mUIPackageName = getPackageName();
      mUIVersionName = getPackageManager().getPackageInfo(mUIPackageName, 0).versionName;
    } catch (NameNotFoundException e) {
      Log.e(TAG, "Exception requesting versions: " + e.getLocalizedMessage());
    }

    Class BluetoothCodecConfig = null;
    try {
      // Get BluetoothCodecConfig class object
      BluetoothCodecConfig = Class.forName("android.bluetooth.BluetoothCodecConfig");
    } catch (ClassNotFoundException e) {
      Log.e(TAG, "ClassNotFoundException: " + e.toString());
    }

    try {
      // Get SOURCE_CODEC_TYPE_APTX codec ID definition
      Field sourceCodecTypeAptx = BluetoothCodecConfig.getField("SOURCE_CODEC_TYPE_APTX");
      aptxuiALSDefs.SOURCE_CODEC_TYPE_APTX = sourceCodecTypeAptx.getInt(BluetoothCodecConfig);
    } catch (Exception e) {
      Log.e(TAG, "Exception: " + e.toString());
    }

      try {
      // Get SOURCE_CODEC_TYPE_APTX_HD codec ID definition
      Field sourceCodecTypeAptxHd = BluetoothCodecConfig.getField("SOURCE_CODEC_TYPE_APTX_HD");
      aptxuiALSDefs.SOURCE_CODEC_TYPE_APTX_HD = sourceCodecTypeAptxHd.getInt(BluetoothCodecConfig);
    } catch (Exception e) {
      Log.e(TAG, "Exception: " + e.toString());
    }

    try {
      // Get SOURCE_CODEC_TYPE_APTX_ADAPTIVE codec ID definition
      Field sourceCodecTypeAptxAdaptive = BluetoothCodecConfig.getField("SOURCE_CODEC_TYPE_APTX_ADAPTIVE");
      aptxuiALSDefs.SOURCE_CODEC_TYPE_APTX_ADAPTIVE = sourceCodecTypeAptxAdaptive.getInt(BluetoothCodecConfig);
    } catch (Exception e) {
      Log.e(TAG, "Exception: " + e.toString());
    }

    try {
      // Get SOURCE_CODEC_TYPE_APTX_TWSP codec ID definition
      Field sourceCodecTypeAptxTwsp = BluetoothCodecConfig.getField("SOURCE_CODEC_TYPE_APTX_TWSP");
      aptxuiALSDefs.SOURCE_CODEC_TYPE_APTX_TWSP = sourceCodecTypeAptxTwsp.getInt(BluetoothCodecConfig);
    } catch (Exception e) {
      Log.e(TAG, "Exception: " + e.toString());
    }

    try {
      // Get SOURCE_CODEC_TYPE_MAX codec ID definition
      Field sourceCodecTypeMax = BluetoothCodecConfig.getField("SOURCE_CODEC_TYPE_MAX");
      aptxuiALSDefs.SOURCE_CODEC_TYPE_MAX = sourceCodecTypeMax.getInt(BluetoothCodecConfig);
      aptxuiALSDefs.SOURCE_CODEC_TYPE_NONE = aptxuiALSDefs.SOURCE_CODEC_TYPE_MAX;
    } catch (Exception e) {
      Log.e(TAG, "Exception: " + e.toString());
    }

    try {
      // Get SOURCE_QVA_CODEC_TYPE_MAX codec ID definition
      Field sourceQvaCodecTypeMax = BluetoothCodecConfig.getField("SOURCE_QVA_CODEC_TYPE_MAX");
      aptxuiALSDefs.SOURCE_QVA_CODEC_TYPE_MAX = sourceQvaCodecTypeMax.getInt(BluetoothCodecConfig);
      aptxuiALSDefs.SOURCE_CODEC_TYPE_NONE = aptxuiALSDefs.SOURCE_QVA_CODEC_TYPE_MAX;
    } catch (Exception e) {
      Log.e(TAG, "Exception: " + e.toString());
    }

    try {
      // Get SOURCE_CODEC_TYPE_LC3 codec ID definition
      Field sourceCodecTypeLc3 = BluetoothCodecConfig.getField("SOURCE_CODEC_TYPE_LC3");
      aptxuiALSDefs.SOURCE_CODEC_TYPE_LC3 = sourceCodecTypeLc3.getInt(BluetoothCodecConfig);
    } catch (Exception e) {
      Log.e(TAG, "Exception: " + e.toString());
    }

    String QssEnabled = getResources().getString(R.string.qss_enabled);
    mQssEnabled = QssEnabled.equalsIgnoreCase("true");
    Log.i(TAG, "mQssEnabled: " + mQssEnabled);

    Log.i(TAG, "aptxui (aptX UI app) Version Name: " + mUIVersionName);
    Log.i(TAG, "aptxui (aptX UI app) Package Name: " + mUIPackageName);
    Log.d(TAG, "creation complete");

    final Intent mainServiceIntent = new Intent(this, aptxuiService.class);
    mainServiceIntent.setAction(aptxuiService.class.getName());
    bindService(mainServiceIntent, mUIConnection, BIND_AUTO_CREATE);
  }

  public boolean IsQssEnabled() {
    return mQssEnabled;
  }

  public String GetCodecName() {
    if (mCodec == aptxuiALSDefs.SOURCE_CODEC_TYPE_APTX) {
      return "aptX";
    } else if (mCodec == aptxuiALSDefs.SOURCE_CODEC_TYPE_APTX_HD) {
      return "aptX HD";
    } else if (mCodec == aptxuiALSDefs.SOURCE_CODEC_TYPE_APTX_ADAPTIVE) {
      return "aptX Adaptive";
    } else if (mCodec == aptxuiALSDefs.SOURCE_CODEC_TYPE_APTX_TWSP) {
      return "aptX TWS+";
    } else if (mCodec == aptxuiALSDefs.SOURCE_CODEC_TYPE_LC3) {
      return "LC3";
    }
    return "not available";
  }

  public void CodecConfigured(Intent intent) {
    intent.setComponent(new ComponentName(mALSAppPackageName, mALSAppPackageName + "." + mALSAppServiceClass));
    Message msg = mHandler.obtainMessage(EVENT_CODEC_CONFIGURED);
    msg.obj = intent;
    mHandler.sendMessage(msg);
  }

  private class aptxuiApplicationHandler extends Handler {

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

        case EVENT_CODEC_CONFIGURED:
        {
          try {
            if (intent == null) {
              Log.e(TAG, "missing intent");
              return;
            }

            mCodec = intent.getIntExtra(aptxuiALSDefs.CODEC, aptxuiALSDefs.SOURCE_CODEC_TYPE_NONE);
            mAudioProfile = intent.getStringExtra(aptxuiALSDefs.AUDIO_PROFILE);
            mConnectedDeviceAddress = intent.getStringExtra(aptxuiALSDefs.CONNECTED_DEVICE_ADDRESS);
            mIsQssSupported = intent.getStringExtra(aptxuiALSDefs.QSS_SUPPORT);

            // Check if Snapdragon Sound is enabled
            if (IsQssEnabled()) {
              // If connected device address changes, set mNotifyQssSupport as false
              if (mConnectedDeviceAddress != null && (!mConnectedDeviceAddress.equals(mPrevConnectedDeviceAddress))) {
                mNotifyQssSupport = false;
              }

              // Only send ACTION_NOTIFY_QSS_SUPPORT Intent if Snapdragon Sound is supported and mNotifyQssSupport is false
              if ((mIsQssSupported != null && mIsQssSupported.equalsIgnoreCase("true")) && !mNotifyQssSupport) {
                Intent serviceIntent = new Intent(context, aptxuiNotify.class);
                serviceIntent.setAction(aptxuiNotify.ACTION_NOTIFY_QSS_SUPPORT);
                aptxuiNotify.enqueueWork(context, serviceIntent);
                mNotifyQssSupport = true;
                Log.d(TAG, "handleMessage ACTION_NOTIFY_QSS_SUPPORT");
              }
            }

            // Only send Intent if codec, profile or connected device address changes
            if (mCodec != mPrevCodec || (mAudioProfile != null && (!mAudioProfile.equals(mPrevAudioProfile)))
                || (mConnectedDeviceAddress != null && (!mConnectedDeviceAddress.equals(mPrevConnectedDeviceAddress)))) {
              // Only send ACTION_NOTIFY_CODEC Intent if codec, profile or connected device address changes
              Intent serviceIntent = new Intent(context, aptxuiNotify.class);
              serviceIntent.setAction(aptxuiALSDefs.ACTION_CODEC_CONFIGURED);
              serviceIntent.putExtra(aptxuiALSDefs.CODEC, mCodec);
              serviceIntent.putExtra(aptxuiALSDefs.AUDIO_PROFILE, mAudioProfile);
              aptxuiNotify.enqueueWork(context, serviceIntent);
              Log.d(TAG, "handleMessage ACTION_CODEC_CONFIGURED");

              mPrevCodec = mCodec;
              mPrevAudioProfile = mAudioProfile;
              mPrevConnectedDeviceAddress = mConnectedDeviceAddress;
              break;
            }
          } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.toString());
          }
        }

        default:
          break;
      }
    }
  }

  private void bind() {
    try {
      Log.d(TAG, "bind to " + mALSAppPackageName + "." + mALSAppServiceClass);
      Intent intent = new Intent();
      intent.setAction(mALSAppPackageName + "." + mALSAppServiceClass);
      intent.setComponent(new ComponentName(mALSAppPackageName, mALSAppPackageName + "." + mALSAppServiceClass));
      bindService(intent, mALSConnection, Context.BIND_AUTO_CREATE);
    } catch (SecurityException e) {
      Log.e(TAG, "Can't bind to aptxuiService: " + e.toString());
    }
  }

  //
  // Produce full dumpsys output:
  //    adb shell dumpsys activity service aptxuiService
  // Show codec ID definitions only
  //   adb shell dumpsys activity service aptxuiService codec
  //
  public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
    synchronized (this) {
      boolean codecIdDef = false;

      if (args.length > 0 ) {
        if (args[0].startsWith("codec")) {
          codecIdDef = true;
        }
      }

      pw.println("aptxui (aptX UI app) Version Name: " + mUIVersionName);
      pw.println("aptxui (aptX UI app) Package Name: " + mUIPackageName);
      pw.println("aptxui Bound: " + mALSBound);

      if (codecIdDef) {
        pw.println("");
        pw.println("SOURCE_CODEC_TYPE_APTX: " + aptxuiALSDefs.SOURCE_CODEC_TYPE_APTX);
        pw.println("SOURCE_CODEC_TYPE_APTX_HD: " + aptxuiALSDefs.SOURCE_CODEC_TYPE_APTX_HD);
        pw.println("SOURCE_CODEC_TYPE_APTX_ADAPTIVE: " + aptxuiALSDefs.SOURCE_CODEC_TYPE_APTX_ADAPTIVE);
        pw.println("SOURCE_CODEC_TYPE_APTX_TWSP: " + aptxuiALSDefs.SOURCE_CODEC_TYPE_APTX_TWSP);
        pw.println("SOURCE_CODEC_TYPE_MAX: " + aptxuiALSDefs.SOURCE_CODEC_TYPE_MAX);
        pw.println("SOURCE_QVA_CODEC_TYPE_MAX: " + aptxuiALSDefs.SOURCE_QVA_CODEC_TYPE_MAX);
        pw.println("SOURCE_CODEC_TYPE_LC3: " + aptxuiALSDefs.SOURCE_CODEC_TYPE_LC3);
        pw.println("SOURCE_CODEC_TYPE_NONE: " + aptxuiALSDefs.SOURCE_CODEC_TYPE_NONE);
        return;
      }

      if (mALSBound) {
        pw.println("");
        pw.println("Codec ID: " + mCodec);
        pw.println("Codec name: " + GetCodecName());
        pw.println("Audio Profile: " + mAudioProfile);
        pw.println("QSS enabled: " + mQssEnabled);
        pw.println("QSS supported: " + mIsQssSupported);
        pw.println("Notify QSS support: " + mNotifyQssSupport);
      }
      pw.flush();
    }
  }
}
