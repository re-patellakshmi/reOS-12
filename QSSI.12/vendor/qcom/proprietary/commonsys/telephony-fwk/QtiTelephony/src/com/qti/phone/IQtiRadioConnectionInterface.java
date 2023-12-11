/*
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */

package com.qti.phone;

import android.os.RemoteException;
import android.telephony.ImsiEncryptionInfo;

import com.qti.extphone.NrConfig;
import com.qti.extphone.NrIconType;
import com.qti.extphone.Status;
import com.qti.extphone.Token;

public interface IQtiRadioConnectionInterface {
    public int getPropertyValueInt(String property, int def) throws RemoteException;

    public boolean getPropertyValueBool(String property, boolean def) throws RemoteException;

    public String getPropertyValueString(String property, String def) throws RemoteException;

    public void enableEndc(boolean enable, Token token) throws RemoteException;

    public void queryNrIconType(Token token) throws RemoteException;

    public void queryEndcStatus(Token token) throws RemoteException;

    public void setNrConfig(NrConfig config, Token token) throws RemoteException;

    public void queryNrConfig(Token token) throws RemoteException;

    public void setCarrierInfoForImsiEncryption(Token token,
            ImsiEncryptionInfo imsiEncryptionInfo) throws RemoteException;

    public void sendCdmaSms(byte[] pdu, boolean expectMore, Token token) throws RemoteException;

    public void enable5g(Token token) throws RemoteException;

    public void disable5g(Token token) throws RemoteException;

    public void queryNrBearerAllocation(Token token) throws RemoteException;

    public void  enable5gOnly(Token token) throws RemoteException;

    public void  query5gStatus(Token token) throws RemoteException;

    public void  queryNrDcParam(Token token) throws RemoteException;

    public void  queryNrSignalStrength(Token token) throws RemoteException;

    public void  queryUpperLayerIndInfo(Token token) throws RemoteException;

    public void  query5gConfigInfo(Token token) throws RemoteException;

    public void getQtiRadioCapability(Token token) throws RemoteException;

    public void queryCallForwardStatus(Token token, int cfReason, int serviceClass,
            String number, boolean expectMore) throws RemoteException;

    public void getImei(Token token) throws RemoteException;

    public void getFacilityLockForApp(Token token, String facility, String password,
                int serviceClass, String appId, boolean expectMore) throws RemoteException;

    public boolean isFeatureSupported(int feature);

    public void getDdsSwitchCapability(Token token);

    public void sendUserPreferenceForDataDuringVoiceCall(Token token,
            boolean userPreference);

    public void registerCallback(IQtiRadioConnectionCallback callback);

    public void unRegisterCallback(IQtiRadioConnectionCallback callback);
}
