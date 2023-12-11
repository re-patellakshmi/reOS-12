package com.util;

import android.content.Intent;

import com.pojo.SignalPacket;

public class Utility {
    public  static Intent getIntent(String topicName, String keyName, SignalPacket signalPacket){
        Intent intent = new Intent(topicName);
        intent.setAction(topicName);
        String jsonString = signalPacket.toJSON();
        intent.putExtra(keyName, jsonString);
        return intent;
    }

    public static int getCanId(char[] data){
        int firstByte = ( int ) data[0];
        int secondByte = (int ) data[1];
        int possibleCanID =  ( firstByte << 8 | secondByte );
        return possibleCanID;
    }

    public static double getSpeed(char[] data){
        int motorSpeedFirstByte = (int ) data[3];
        int motorSpeedSecondByte = (int ) data[2];
        int motorSpeed = (motorSpeedSecondByte << 8 | motorSpeedFirstByte );
        double calMotorSpeed = (double ) motorSpeed * ( 0.1 );
        return calMotorSpeed;
    }

    public static double getSoc(char[] data){
        int motorSOCFirstByte = (int ) data[3];
        int motorSOCSecondByte = (int ) data[2];
        int soc = ( motorSOCSecondByte << 8 | motorSOCFirstByte );
        double calSoc = (double ) soc * ( 0.01 );
        return calSoc;
    }

    public static double getLowSoc(){
        return 20.00;
    }
    public static boolean getKeyIgnition (char[] data){
        int keyIgnition = (int ) data[2];
        int calKeyIgnition = keyIgnition & 0x01;
        if(calKeyIgnition == 0)  return false;
        else return true;
    }

    public static boolean getRightIndicator (char[] data){
        int rightIndicator = (int ) data[2];
        int calRightindIcator = rightIndicator & 0b00100000;
        if( calRightindIcator == 0 ) return false;
        return true;
    }

    public static boolean getLeftindIcator (char[] data){
        int leftIndicator = (int ) data[2];
        int calLeftIndicator = leftIndicator & 0b00001000;
        if( calLeftIndicator == 0 ) return false;
        return true;
    }

    public static String getRidingMode ( char [] data) {
        int ridingMode = (int ) data[2];
        int calRidingMode = ridingMode & 0b00001111;
        if( calRidingMode == 0x00) return "PARK";
        else if( calRidingMode == 0x01) return "ECO";
        else if( calRidingMode == 0x02) return "TOUR";
        else if( calRidingMode == 0x03) return "SPORT";
        else if( calRidingMode == 0x04) return "BOOST";
        else if( calRidingMode == 0x05) return "REV";
        else if( calRidingMode == 0x06) return "LIMPH";
        else if( calRidingMode == 0x07) return "Hyper";
        else if( calRidingMode == 0x08) return "REGEN";
        else if( calRidingMode == 0x09) return "DERATE";
        else if( calRidingMode == 0x0A) return "GH3";
        else if( calRidingMode == 0x0B) return "BOOST2";
        else return "Fault";
    }


    public static String getChargingStatus (char[] data){
        int chargingStatus = (int ) data[2];
        int calChargingstatus = chargingStatus & 0b00000010;
        if( calChargingstatus == 0x00) return "DISCONNECTED";
        else return "CONNECTED";
        // FAULT;
    }


    public static long getOdoMeter (char[] data){
        int odoMeterFirstByte = (int ) data[4];
        int odoMeterSecondByte = (int ) data[3];
        int odoMeterThirdByte = (int ) data[2];
        long calOdoMeter = ( odoMeterThirdByte << 16 | odoMeterSecondByte << 8 | odoMeterFirstByte);
        return calOdoMeter;
    }

    public static boolean getVehicleErrorIndication (char [] data){
        int vehicleErrorIndication = (int ) data[2];
        int calVehicleErrorIndication = vehicleErrorIndication & 0x01;
        if (calVehicleErrorIndication == 0 ) return false;
        return true;
    }

    public static double getVehicleChargingTime (char [] data){
        int vehicleChargingTimeFirstByte = (int ) data [3];
        int vehicleChargingTimeSecondByte = (int ) data [2];
        int vehicleChargingTime = ( vehicleChargingTimeSecondByte << 8 | vehicleChargingTimeFirstByte );
        double calVehicleChargingTime = (double ) vehicleChargingTime * ( 0.01 );
        return calVehicleChargingTime;
    }
    public static long getBatterySoh (char [] data){
        int batterySohFirstByte = (int )data [2];
        long calSoh = (long) (batterySohFirstByte * (0.5 ));
        return calSoh;
    }

    public static boolean getVehicleServiceIndication (char [] data){
        int vehicleServiceIndication = (int ) data[2];
        int calVehicleServiceIndication = vehicleServiceIndication & 0x01;
        if (calVehicleServiceIndication == 0 ) return false;
        return true;
    }

    public static boolean getAbsActive (char [] data){
        int absActive = (int ) data[2];
        int calAbsActive = absActive & 0x01;
        if (calAbsActive == 0 ) return false;
        return true;
    }

    public static String getRegenerationActive (char[] data){
        int regenerationActive = (int ) data[2];
        int calRegenerationActive = regenerationActive & 0b00000001;
        if( calRegenerationActive == 0x00) return "INACTIVE";
        else return "ACTIVE";
    }

    public static String getSideStandStatus(char[] data){
        int sideStandStatus = (int ) data[2];
        int calSideStandStatus = sideStandStatus & 0b00000111;
        if( calSideStandStatus == 0x00) return "SNA";
        else if( calSideStandStatus == 0x01) return "Removed";
        else if( calSideStandStatus == 0x02) return "Applied";
        else if( calSideStandStatus == 0x03) return "Open_ShortCktFault";
        else if( calSideStandStatus == 0x04) return "ShortCKTFault";
        else return "";
    }

    public static String getBrakeStatus(char [] data){
        int brakeStatus = (int ) data[2];
        int calBrakeStatus = brakeStatus & 0b00000011;
        if( calBrakeStatus == 0x00) return "BothBrakeNotApplied";
        else if( calBrakeStatus == 0x01) return "FrontBrakeApplied";
        else if( calBrakeStatus == 0x02) return "RearBrakeApplied";
        else if( calBrakeStatus == 0x03) return "BothBrakeApplied";
        else return "";
    }

    public static boolean getImdError (char[] data){
        int imdErrorFirstByte = (int ) data[3];
        int imdErrorSecondByte = (int ) data[2];
        int imdError = ( imdErrorSecondByte << 8 | imdErrorFirstByte );
        int calImdError = imdError & 0b1000000000000000;
        if( calImdError == 0 ) return false;
        return true;
    }

    public static double getBatTemp(char[] data){
        int batTempFirstByte = (int ) data[3];
        int batTempSecondByte = (int ) data[2];
        int batTemp = (batTempSecondByte << 8 | batTempFirstByte );
        double calBatTemp = (double ) batTemp;
        return calBatTemp;
    }

}
