package com.sibros.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


public class SibrosBroadcaster extends Service {
    private static final String TAG = "SibrosBroadcaster";
    final String topicName = "com.royalenfield.telemetry.can.message.ACTION_SEND";
    final String keyName = "data";
    final String VIN = "4Y1SL65848Z411439";

    private final IBinder binder = new SibrosLocalBinder();
    SibrosThread sibrosThread;
    public void processAndBroadcast(char[] data){

        Log.d(TAG,"Prepering the data for SibroBoradcaster");

        Intent intent = new Intent(topicName);
        intent.setAction(topicName);

        char[] vinPlusCanData = new char[30];
        int len = VIN.length();

        Log.d(TAG,"Copying... VIN for sibros");
        for(int i = len-1, j = 19; i >=0 ; j--, i--){ vinPlusCanData[i] = VIN.charAt(i); }

        Log.d(TAG,"Copying... actual data for sibros");
        for(int i = 20, j = 0; i > 30; i++, j++){ vinPlusCanData[i] = data[j]; }

        Log.d(TAG,"Publishing the data to sibros on topic"+topicName+": "+"with keyName:"+keyName);
        intent.putExtra(keyName, vinPlusCanData);

        Log.d(TAG,"Sibro broadcaster sent the data successfully");
        try{
            sendBroadcast(intent);
        }catch (Exception e){
            Log.e(TAG, "Exception:"+e);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        sibrosThread = new SibrosThread();
        sibrosThread.start();
        return 0;
    }

    class SibrosThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {
                Log.e(TAG, "ReDigitalService started... successfully");
            }
        }
    }

    public class SibrosLocalBinder extends Binder {
        public SibrosBroadcaster getService() {
            // Return this instance of LocalService so clients can call public methods.
            return SibrosBroadcaster.this;
        }
    }
}
