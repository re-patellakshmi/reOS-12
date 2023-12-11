/******************************************************************************
 *
 *  Copyright (c) 2020 - 2021 Qualcomm Technologies, Inc. and/or its subsidiaries.
 *  All Rights Reserved.
 *  Qualcomm Technologies, Inc. and/or its subsidiaries. Confidential and Proprietary.
 *
 ******************************************************************************/
package com.qualcomm.qtil.aptxui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class aptxuiNotifyReceiver extends BroadcastReceiver {
  private static final String TAG = "aptxuiNotifyReceiver";

  @Override
  public void onReceive(Context context, Intent intent) {
    intent.setClass(context, aptxuiNotify.class);
    aptxuiNotify.enqueueWork(context, intent);
  }
}
