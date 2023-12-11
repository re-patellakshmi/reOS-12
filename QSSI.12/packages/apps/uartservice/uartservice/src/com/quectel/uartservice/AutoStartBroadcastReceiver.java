//
// Created by john.king on 2023/6/12
package com.quectel.uartservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoStartBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = "uartservice-AutoStartBroadcastReceiver";
    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    UARTService mUartService;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "AutoStartBroadcastReceiver onReceive, intent.getAction() = " + intent.getAction());
        mUartService = new UARTService();
        Log.d(TAG, "starting UARTService");
        context.startService(new Intent(context,UARTService.class));
    }
}

