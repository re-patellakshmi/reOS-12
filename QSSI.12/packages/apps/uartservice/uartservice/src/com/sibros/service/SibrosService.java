package com.sibros.service;

import android.content.Intent;
import android.util.Log;

public class SibrosService {
    private static final String TAG = "SibrosService";
    private static final String topicName = "com.royalenfield.telemetry.can.message.ACTION_SEND";
    private static final String keyName = "data";
    private static final String VIN = "4Y1SL65848Z411439";
    private SibrosBroadcaster sibrosBroadcaster = null;

    public SibrosService(){
        if( sibrosBroadcaster == null){
            Log.d(TAG,"Creating object for SibroBroadcaster");
            this.sibrosBroadcaster = new SibrosBroadcaster();
        }else{
            Log.d(TAG,"SibroBroadcaster was already created");
        }
    }
    public void processData(char[] data){
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
        sibrosBroadcaster.sendBroadcast(intent);
        Log.d(TAG,"Sibro broadcaster sent the data successfully");
    }
}
