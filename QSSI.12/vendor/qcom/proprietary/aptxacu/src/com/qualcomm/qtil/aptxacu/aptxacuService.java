/******************************************************************************
 *
 *  Copyright (c) 2020 - 2021 Qualcomm Technologies, Inc. and/or its subsidiaries.
 *  All Rights Reserved.
 *  Qualcomm Technologies, Inc. and/or its subsidiaries. Confidential and Proprietary.
 *
 ******************************************************************************/
package com.qualcomm.qtil.aptxacu;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class aptxacuService extends Service {

  private static final String TAG = "aptxacuService";
  private static final String APTXACU_SERVICE
                        = "com.qualcomm.qtil.aptxacu.aptxacuService";

  Messenger mMessenger;

  class IncomingHandler extends Handler {
    private Context applicationContext;

    IncomingHandler(Context context) {
      Log.i(TAG, "IncomingHandler");
      applicationContext = context.getApplicationContext();
    }

    @Override
    public void handleMessage(Message msg) {
      Log.i(TAG, "handleMessage");
      Context context = getApplicationContext();
      aptxacuApplication app = (aptxacuApplication) context;
      Intent intent = (Intent) msg.obj;
      Log.i(TAG, "handleMessage: " + intent);
      final String action = intent.getAction();
      Log.i(TAG, "handleMessage action: " + action);

      switch (action) {
        case aptxacuALSDefs.ACTION_ALS_PREFERENCES_UPDATED:
          Log.i(TAG, "handleMessage ACTION_ALS_PREFERENCES_UPDATED");
          app.alsPreferencesUpdated(intent);
          break;
        default:
          Log.i(TAG, "handleMessage unknown action: " + action);
          super.handleMessage(msg);
      }
    }
  }

  @Override
  public IBinder onBind(Intent intent) {
    Log.i(TAG, "onBind: " + intent);
    if (APTXACU_SERVICE.equals(intent.getAction())) {
      mMessenger = new Messenger(new IncomingHandler(this));
      return mMessenger.getBinder();
    }
    return null;
  }

  @Override
  protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
    Log.i(TAG, "dump");
    aptxacuApplication app = (aptxacuApplication) getApplicationContext();
    app.dump(fd, writer, args);
  }
}
