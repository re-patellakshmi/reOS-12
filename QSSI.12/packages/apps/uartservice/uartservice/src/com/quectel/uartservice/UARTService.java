//
// Created by lakshmi patel on 2023/6/12
package com.quectel.uartservice;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.sibros.service.SibrosBroadcaster;
import com.pojo.SignalPacket;
import com.util.Utility;


public class UARTService extends Service {
    private static final String TAG = "uartservice-UARTService";

    private static final String topicName = "com.royalenfield.telemetry.info.ACTION_SEND";
    private static final String keyName = "packet";
    private UART ttyHSLx;
    SibrosBroadcaster sibrosBroadcaster;
    boolean reDigitalBound = false;
    boolean sibrosBound = false;

    private UARTThread mUARTThread;
    private int RING_BUFFER_SIZE = 100;
    private int CAN_FRAME_SIZE = 10;
    private int START_FRAME_SIZE = 4;
    private char[]  speedData= new char[10];
    private char[]  readData= new char[10];
    private char[]  ringBuffer =  new char[RING_BUFFER_SIZE];

    private long timeCounter = 0l;

    private final IBinder binder = new UartServiceBinder();
    private ServiceConnection digitalConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            reDigitalBound = true;
        }


        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            reDigitalBound = false;
        }
    };

    public int onStartCommand(Intent intent, int flags, int startId) {
        mUARTThread = new UARTThread();
        mUARTThread.start();
        return 0;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    int uart_init()
    {
        ttyHSLx = new UART();
        int rt = ttyHSLx.uartOpen();
        if (rt != 0) {
            Log.e(TAG, "open port failed");
            return -1;
        }
        Log.d(TAG, "open port succeeded");
        return 0;
    }

    void uart_deinit()
    {
        ttyHSLx.uartClose();
    }

    char uart_read()
    {
        return ttyHSLx.uartRead();
    }

    void sendDataToSibrosService(char[] data){

            final String topicName = "com.royalenfield.telemetry.can.message.ACTION_SEND";
            final String keyName = "data";
            final String VIN = "4Y1SL65848Z411439";
            Log.d(TAG,"Prepering the data for SibroBoradcaster");
            Intent intent = new Intent(topicName);
            intent.setAction(topicName);
            char[] vinPlusCanData = new char[30];
            int len = VIN.length();
            Log.d(TAG,"Copying... VIN for sibros");
            for(int i = len-1, j = 19; i >=0 ; j--, i--){ vinPlusCanData[i] = VIN.charAt(i); }
            Log.d(TAG,"Copying... actual data for sibros");
            for(int i = 20, j = 0; i < 30; i++, j++){ vinPlusCanData[i] = data[j]; }
            Log.d(TAG,"Publishing the data to sibros on topic"+topicName+": "+"with keyName:"+keyName);
            intent.putExtra(keyName, vinPlusCanData);
            sendBroadcast(intent);
            Log.d(TAG,"Sibro broadcaster sent the data successfully");

    }

    public void broadcast(String topicName, String keyName, SignalPacket signalPacket){
        try {
            Intent intent = Utility.getIntent(topicName, keyName, signalPacket);;
            sendBroadcast(intent);
            Log.e(TAG, "SendBroadcast executed successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG, "12e routine has been completed");
    }

    public void processAndBroadcast(String topicName,String keyName, char[] data){
        int possibleCanID = Utility.getCanId(data);
        Log.e(TAG, "Possible CAN-ID in dec: "+possibleCanID);

        if( possibleCanID == 0x321){
            double calMotorSpeed = Utility.getSpeed(data);
            SignalPacket signalPacket = new SignalPacket("speed", possibleCanID, calMotorSpeed);
            broadcast(topicName, keyName, signalPacket);
            return;
        }

        if( possibleCanID == 0x12E){
            double calSoc = Utility.getSoc(data);
            SignalPacket signalPacket = new SignalPacket("soc", possibleCanID, calSoc);
            broadcast(topicName, keyName, signalPacket);

            double lowSoc = Utility.getLowSoc();
            signalPacket = new SignalPacket("low_soc", possibleCanID, lowSoc);
            broadcast(topicName, keyName, signalPacket);
            return;
        }

        if ( possibleCanID == 0x6BB){
            boolean calkeyignition = Utility.getKeyIgnition(data);
            SignalPacket signalPacket = new SignalPacket("ignition", possibleCanID, calkeyignition);
            broadcast(topicName, keyName, signalPacket);
        }

        if ( possibleCanID == 0x6BA){
            SignalPacket signalPacket = null;

            boolean calrightindicator = Utility.getRightIndicator(data);
            boolean calleftindicator = Utility.getLeftindIcator(data);
            boolean hazarTtl = calleftindicator && calrightindicator ? true : false;

            if( hazarTtl ){
                broadcast(topicName, keyName, new SignalPacket("hazard_ttl", possibleCanID, hazarTtl));
                broadcast(topicName, keyName, new SignalPacket("right_ttl", possibleCanID, false));
                broadcast(topicName, keyName, new SignalPacket("left_ttl", possibleCanID, false));
            }else if( calrightindicator ){
                broadcast(topicName, keyName, new SignalPacket("hazard_ttl", possibleCanID, false));
                broadcast(topicName, keyName, new SignalPacket("right_ttl", possibleCanID, calrightindicator));
                broadcast(topicName, keyName, new SignalPacket("left_ttl", possibleCanID, false));
            }else if( calleftindicator ){
                broadcast(topicName, keyName, new SignalPacket("hazard_ttl", possibleCanID, false));
                broadcast(topicName, keyName, new SignalPacket("right_ttl", possibleCanID, false));
                broadcast(topicName, keyName, new SignalPacket("left_ttl", possibleCanID, calleftindicator));
            }else{
                broadcast(topicName, keyName, new SignalPacket("hazard_ttl", possibleCanID, false));
                broadcast(topicName, keyName, new SignalPacket("right_ttl", possibleCanID, false));
                broadcast(topicName, keyName, new SignalPacket("left_ttl", possibleCanID, false));
            }


        }

        if ( possibleCanID == 0x121) {
            String calridingmode = Utility.getRidingMode(data);
            SignalPacket signalPacket = new SignalPacket("riding_mode", possibleCanID, calridingmode);
            broadcast(topicName, keyName, signalPacket);

            if( calridingmode == "REVERSE") {
                signalPacket = new SignalPacket("reverse", possibleCanID, calridingmode);
                broadcast(topicName, keyName, signalPacket);
            }


        }

        if ( possibleCanID == 0x12A) {
            String chargingStatus = Utility.getChargingStatus(data);
            SignalPacket signalPacket = new SignalPacket("charging_status", possibleCanID, chargingStatus);
            broadcast(topicName, keyName, signalPacket);
        }

        if ( possibleCanID == 0x151) {
            long calOdoMeter = Utility.getOdoMeter(data);
            SignalPacket signalPacket = new SignalPacket("odo", possibleCanID, calOdoMeter);
            broadcast(topicName, keyName, signalPacket);
            return;
        }

        if( possibleCanID == 0x0CD){
            boolean calVehicleErrorIndication = Utility.getVehicleErrorIndication(data);
            SignalPacket signalPacket = new SignalPacket("vehicle_error_ind", possibleCanID, calVehicleErrorIndication);
            broadcast(topicName, keyName, signalPacket);
        }

        if ( possibleCanID == 0x0EF){
            double calVehicleChargingTime = Utility.getVehicleChargingTime(data);
            SignalPacket signalPacket = new SignalPacket("charging_time", possibleCanID, calVehicleChargingTime);
            broadcast(topicName, keyName, signalPacket);
            return;
        }

        if ( possibleCanID == 0x16F){
            long calBatterySoh = Utility.getBatterySoh(data);
            SignalPacket signalPacket = new SignalPacket("battery_soh", possibleCanID, calBatterySoh);
            broadcast(topicName, keyName, signalPacket);
            return;
        }

        if( possibleCanID == 0x0AE){
            boolean calVehicleServiceIndication = Utility.getVehicleServiceIndication(data);
            SignalPacket signalPacket = new SignalPacket("vehicle_service_ind", possibleCanID, calVehicleServiceIndication);
            broadcast(topicName, keyName, signalPacket);
        }

        if( possibleCanID == 0xAD){
            boolean calAbsActive = Utility.getAbsActive(data);
            SignalPacket signalPacket = new SignalPacket("abs_active", possibleCanID, calAbsActive);
        }

        if( possibleCanID == 0xAA){
            String regenerationActive = Utility.getRegenerationActive(data);
            SignalPacket signalPacket = new SignalPacket("regen_active", possibleCanID, regenerationActive);
            broadcast(topicName, keyName, signalPacket);
        }

        if( possibleCanID == 0x361){
            String sideStandStatus = Utility.getSideStandStatus(data);
            SignalPacket signalPacket = new SignalPacket("side_stand", possibleCanID, sideStandStatus);
            broadcast(topicName, keyName, signalPacket);
        }

        if( possibleCanID == 0x169){
            String brakeStatus = Utility.getBrakeStatus(data);
            SignalPacket signalPacket = new SignalPacket("brake_status", possibleCanID, brakeStatus);
            broadcast(topicName, keyName, signalPacket);
        }

	if ( possibleCanID == 0x354){
            boolean calimderror = Utility.getImdError(data);
            SignalPacket signalPacket = new SignalPacket("imdstatus", possibleCanID, calimderror);
            broadcast(topicName, keyName, signalPacket);
        }

        if( possibleCanID == 0x156){
            double calBatTemp = Utility.getBatTemp(data);
            SignalPacket signalPacket = new SignalPacket("battemp", possibleCanID, calBatTemp);
            broadcast(topicName, keyName, signalPacket);
            return;
        }
    }

    public void broadcastToDigital(char[] data){
        String topicName = "com.royalenfield.digital.telemetry.info.ACTION_SEND";
        String keyName = "packet";
        processAndBroadcast(topicName, keyName, data);
    }

    public void broadcastToLocalApp(char[] data){
        String topicName = "com.royalenfield.digital.telemetry.info.for.inhouse.ACTION_SEND";
        String keyName = "packet";
        processAndBroadcast(topicName, keyName, data);
    }

    int uart_write(char sendbyte)
    {
        return ttyHSLx.uartWrite(sendbyte);
    }

    char randomChar(int counter){
        int x = (int) ((Math.random()*10 ) % 10);
        char[] chars ={ '0','1','2','3','4', '5', '6', '7', '8', '9'};
        if( counter % 11 == 0 ) return '\n';
        return chars[x];
    }

    char[] dataPacket(){
        char byteReceived;
        char[] tempData = new char[10];
        int counter = 0;
        int charIntValue;
        do {
            byteReceived = uart_read();
            charIntValue = ( int ) byteReceived;
            Log.e(TAG, "Byte-Index: "+counter +" "+"Byte-Value: "+charIntValue);
            counter++;
        }while (counter < START_FRAME_SIZE || byteReceived == 0xFF || byteReceived == 0x00 );

        Log.e(TAG, "Started collecting data packet-frame");
        tempData[0] = byteReceived;
        charIntValue = (int ) tempData[0];
        Log.e(TAG, "Byte-Index: "+ 0 +" "+"Byte-Value: "+charIntValue);
        for(int i = 1; i < 10; i++){
            tempData[i] = uart_read();
            charIntValue = (int ) tempData[i];
            Log.e(TAG, "Byte-Index: "+ i +" "+"Byte-Value: "+charIntValue);
        }

        return tempData;
    }

    class UARTThread extends Thread {
        @Override
        public void run() {
            super.run();
            uart_init();
            while (true) {
                Log.e(TAG, "Started reading of can-data...");
                readData = dataPacket();
                try{
                    sendDataToSibrosService(readData);
                    broadcastToDigital(readData);
                    broadcastToLocalApp(readData);
                }catch (Exception e){
                    Log.e("UartService", "Exception found during broadcosting to digit via bytes"+e);
                }


            }
        }
    }

    public class UartServiceBinder extends Binder {
        UartServiceBinder getService() {
            // Return this instance of LocalService so clients can call public methods.
            return UartServiceBinder.this;
        }
    }
}
