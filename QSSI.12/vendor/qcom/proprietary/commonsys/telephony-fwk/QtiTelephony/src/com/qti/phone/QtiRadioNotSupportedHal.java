/*
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */

package com.qti.phone;

import android.os.RemoteException;
import android.telephony.ImsiEncryptionInfo;
import android.util.Log;

import com.qti.extphone.NrConfig;
import com.qti.extphone.NrIconType;
import com.qti.extphone.Status;
import com.qti.extphone.Token;

/*
 * Default HAL class that is invoked when no IQtiRadio HAL is available.
 * Typical use case for this when the target does not support telephony/ril
 */

public class QtiRadioNotSupportedHal implements IQtiRadioConnectionInterface {

    private static final String TAG = "QtiRadioNotSupportedHal";

    private void fail() throws RemoteException {
        throw new RemoteException("Radio is not supported");
    }

    // Implementation of IQtiRadio java interface where all methods throw an exception
    @Override
    public int getPropertyValueInt(String property, int def) throws RemoteException {
        fail();
        return -1;
    }

    @Override
    public boolean getPropertyValueBool(String property, boolean def) throws RemoteException {
        fail();
        return false;
    }

    @Override
    public String getPropertyValueString(String property, String def) throws RemoteException {
        fail();
        return null;
    }

    @Override
    public void enableEndc(boolean enable, Token token) throws RemoteException {
        fail();
    }

    @Override
    public void queryNrIconType(Token token) throws RemoteException {
        fail();
    }

    @Override
    public void queryEndcStatus(Token token) throws RemoteException {
        fail();
    }

    @Override
    public void setNrConfig(NrConfig config, Token token) throws RemoteException {
        fail();
    }

    @Override
    public void queryNrConfig(Token token) throws RemoteException {
        fail();
    }

    @Override
    public void setCarrierInfoForImsiEncryption(Token token,
            ImsiEncryptionInfo imsiEncryptionInfo) throws RemoteException {
        fail();
    }

    @Override
    public void sendCdmaSms(byte[] pdu, boolean expectMore, Token token) throws RemoteException {
        fail();
    }

    @Override
    public void enable5g(Token token) throws RemoteException {
        fail();
    }

    @Override
    public void disable5g(Token token) throws RemoteException {
        fail();
    }

    @Override
    public void queryNrBearerAllocation(Token token) throws RemoteException {
        fail();
    }

    @Override
    public void  enable5gOnly(Token token) throws RemoteException {
        fail();
    }

    @Override
    public void  query5gStatus(Token token) throws RemoteException {
        fail();
    }

    @Override
    public void  queryNrDcParam(Token token) throws RemoteException {
        fail();
    }

    @Override
    public void  queryNrSignalStrength(Token token) throws RemoteException {
        fail();
    }

    @Override
    public void  queryUpperLayerIndInfo(Token token) throws RemoteException {
        fail();
    }

    @Override
    public void  query5gConfigInfo(Token token) throws RemoteException {
        fail();
    }

    @Override
    public void getQtiRadioCapability(Token token) throws RemoteException {
        fail();
    }

    @Override
    public void queryCallForwardStatus(Token token, int cfReason, int serviceClass,
            String number, boolean expectMore) throws RemoteException {
        fail();
    }

    @Override
    public void getFacilityLockForApp(Token token, String facility, String password,
            int serviceClass, String appId, boolean expectMore) throws RemoteException {
        fail();
    }

    @Override
    public void getImei(Token token) throws RemoteException {
        fail();
    }

    @Override
    public boolean isFeatureSupported(int feature) {
        Log.e(TAG, "isFeatureSupported not supported");
        return false;
    }

    @Override
    public void getDdsSwitchCapability(Token token) {
        Log.e(TAG, "getDdsSwitchCapability not supported");
    }

    @Override
    public void sendUserPreferenceForDataDuringVoiceCall(Token token,
            boolean userPreference) {
        Log.e(TAG, "sendUserPreferenceForDataDuringVoiceCall not supported");
    }

    @Override
    public void registerCallback(IQtiRadioConnectionCallback callback) {
        Log.e(TAG, "registerCallback not supported");
        return;
    }

    @Override
    public void unRegisterCallback(IQtiRadioConnectionCallback callback) {
        Log.e(TAG, "unRegisterCallback not supported");
        return;
    }
}
