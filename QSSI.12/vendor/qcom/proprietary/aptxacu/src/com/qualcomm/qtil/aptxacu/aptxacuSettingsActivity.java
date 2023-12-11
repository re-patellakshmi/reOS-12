/******************************************************************************
 *
 *  Copyright (c) 2020 - 2021 Qualcomm Technologies, Inc. and/or its subsidiaries.
 *  All Rights Reserved.
 *  Qualcomm Technologies, Inc. and/or its subsidiaries. Confidential and Proprietary.
 *
 ******************************************************************************/
package com.qualcomm.qtil.aptxacu;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

public class aptxacuSettingsActivity extends AppCompatActivity {
  private static final String TAG = "aptxacuSettingsActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.i(TAG, "onCreate");
    super.onCreate(savedInstanceState);
    Context context = getApplicationContext();
    String title = context.getString(R.string.acu_label);
    setTitle(title);

    // Display the fragment as the main content.
    getSupportFragmentManager()
        .beginTransaction()
        .replace(android.R.id.content, new aptxacuSettingsFragment())
        .commit();
  }
}
