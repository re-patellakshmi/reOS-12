/******************************************************************************
 *
 *  Copyright (c) 2020 - 2021 Qualcomm Technologies, Inc. and/or its subsidiaries.
 *  All Rights Reserved.
 *  Qualcomm Technologies, Inc. and/or its subsidiaries. Confidential and Proprietary.
 *
 ******************************************************************************/
package com.qualcomm.qtil.aptxacu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class aptxacuBootReceiver extends BroadcastReceiver {
  private static final String TAG = "aptxacuBootReceiver";

  @Override
  public void onReceive(Context context, Intent intent) {

    String action = intent.getAction();

    switch (action) {
      case Intent.ACTION_BOOT_COMPLETED:
      case Intent.ACTION_LOCKED_BOOT_COMPLETED:
        // Subsequently, application will now get launched automatically
        break;
      default:
        break;
    }
  }
}
