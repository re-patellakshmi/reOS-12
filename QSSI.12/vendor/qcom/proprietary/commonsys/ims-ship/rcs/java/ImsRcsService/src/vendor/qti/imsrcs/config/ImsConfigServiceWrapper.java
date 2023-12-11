/**********************************************************************
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 **********************************************************************/


package vendor.qti.imsrcs.config;

import android.app.DownloadManager;
import android.os.RemoteException;
import android.telephony.ims.RcsClientConfiguration;
import android.telephony.ims.feature.RcsFeature;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;


import vendor.qti.ims.configservice.V1_0.AutoConfig;
import vendor.qti.ims.configservice.V1_0.AutoConfigResponse;
import vendor.qti.ims.configservice.V1_0.ConfigData;
import vendor.qti.ims.configservice.V1_0.IConfigService;
import vendor.qti.ims.configservice.V1_0.IConfigServiceListener;
import vendor.qti.ims.configservice.V1_0.ImsServiceEnableConfigKeys;
import vendor.qti.ims.configservice.V1_0.KeyValuePairTypeBool;
import vendor.qti.ims.configservice.V1_0.KeyValuePairTypeInt;
import vendor.qti.ims.configservice.V1_0.KeyValuePairTypeString;
import vendor.qti.ims.configservice.V1_0.RequestStatus;
import vendor.qti.ims.configservice.V1_0.SettingsData;
import vendor.qti.ims.configservice.V1_0.SettingsId;
import vendor.qti.ims.configservice.V1_0.SettingsValues;
import vendor.qti.ims.configservice.V1_0.StandaloneMessagingConfigKeys;
import vendor.qti.ims.configservice.V1_0.UceCapabilityInfo;
import vendor.qti.imsrcs.config.ImsConfigCbListener;
import vendor.qti.imsrcs.config.ImsConfigServiceImpl;
import vendor.qti.imsrcs.config.ImsConfigServiceImpl.QueryAutoConfigListener;

import static android.telephony.ims.feature.RcsFeature.RcsImsCapabilities.CAPABILITY_TYPE_NONE;
import static android.telephony.ims.feature.RcsFeature.RcsImsCapabilities.CAPABILITY_TYPE_OPTIONS_UCE;
import static android.telephony.ims.feature.RcsFeature.RcsImsCapabilities.CAPABILITY_TYPE_PRESENCE_UCE;

public class ImsConfigServiceWrapper {
    IConfigService mHidlConfigService;
    ConfigListener mHidlConfigListener = new ConfigListener();
   // ImsConfigServiceImpl.UceCapUpdateCallback mlocalListener;
    private final String LOG_TAG = "ImsConfigServiceWrapper";
    private byte[] cachedConfig;
    private int mUserData = 1234;
    private ImsConfigCbListener configCbListener;
    //List<Executor> mExecList = new ArrayList<>();
    private Executor uceEx;
    private Executor rcsEx;
    private ImsConfigServiceImpl.UceCapUpdateCallback uceCbListener;
    private ImsConfigServiceImpl.SipTransportCapUpdateCallback rcsCbListener;
    private boolean isRegForInd = false;
    private ImsConfigServiceImpl.QueryAutoConfigListener acsQueryListener;

    public void setConfigCbListener(ImsConfigCbListener cb) {
        this.configCbListener = cb;
    }

    public void setUceExecutor(Executor ex){
        this.uceEx = ex;
    }

    public void setRcsExecutor(Executor ex){
        this.rcsEx = ex;
    }

    private Executor getUceExecutor(){
        return uceEx;
    }

    private Executor getRcsExecutor(){
        return rcsEx;
    }

    public void clearConfigCache() {
        cachedConfig = null;
    }

    public void clear(){
        clearConfigCache();
        if(configCbListener != null){
          configCbListener.handleConfigWrapperCleanup();
        }
        //mHidlConfigListener = null;
        //mHidlConfigService = null;
    }

    public void setHidlConfigService(IConfigService s) {
        mHidlConfigService = s;
    }
    public  IConfigServiceListener gethidlConfigListener() {
        return mHidlConfigListener;
    }

    /**
     * this method will call setSettings for SMConfig
     * @param rcc : rcs client configuration
     */
    public int sendRcsClientConfig(RcsClientConfiguration rcc) {
        ArrayList<KeyValuePairTypeString> strList = new ArrayList<>();

        KeyValuePairTypeString kvStr = new KeyValuePairTypeString();
        kvStr.key = StandaloneMessagingConfigKeys.RCS_VERSION_KEY;
        kvStr.value = rcc.getRcsVersion();
        strList.add(kvStr);

        KeyValuePairTypeString kvStr1 = new KeyValuePairTypeString();
        kvStr1.key = StandaloneMessagingConfigKeys.RCS_PROFILE_KEY;
        kvStr1.value = rcc.getRcsProfile();
        strList.add(kvStr1);

        KeyValuePairTypeString kvStr2 = new KeyValuePairTypeString();
        kvStr2.key = StandaloneMessagingConfigKeys.CLIENT_VENDOR_KEY;
        kvStr2.value = rcc.getClientVendor();
        strList.add(kvStr2);

        KeyValuePairTypeString kvStr3 = new KeyValuePairTypeString();
        kvStr3.key = StandaloneMessagingConfigKeys.CLIENT_VERSION_KEY;
        kvStr3.value = rcc.getClientVersion();
        strList.add(kvStr3);

        SettingsValues values = new SettingsValues();
        values.stringData = strList ;
        return sendConfigSettings(SettingsId.STANDALONE_MESSAGING_CONFIG,values);
    }

    /**
     * This method will set UCE status on modem.
     * to be executed under uce executor by UCE module
     * @param isPresenceEnabled
     * @param isOptionsEnabled
     */
    public void setUceStatus(boolean isPresenceEnabled, boolean isOptionsEnabled){

        ArrayList<KeyValuePairTypeBool> boolList = new ArrayList<>();
        KeyValuePairTypeBool kvBool = new KeyValuePairTypeBool();
        kvBool.key = ImsServiceEnableConfigKeys.PRESENCE_ENABLED_KEY;
        kvBool.value = isPresenceEnabled;
        boolList.add(kvBool);
        KeyValuePairTypeBool kvBoolOpt = new KeyValuePairTypeBool();
        kvBoolOpt.key = ImsServiceEnableConfigKeys.OPTIONS_ENABLED_KEY;
        kvBoolOpt.value = isOptionsEnabled;
        boolList.add(kvBoolOpt);
        SettingsValues v = new SettingsValues();
        v.boolData = boolList;
        sendConfigSettings(SettingsId.IMS_SERVICE_ENABLE_CONFIG,v);
    }


    public void notifyUceStatus(SettingsValues values)
    {
        boolean isPresenceEnabled=false;
        boolean isOptionsEnabled=false;
        ArrayList<KeyValuePairTypeBool> boolList = values.boolData;
        for(KeyValuePairTypeBool kvBool : boolList){
           if(kvBool.key == ImsServiceEnableConfigKeys.PRESENCE_ENABLED_KEY)
              isPresenceEnabled = kvBool.value;
           if(kvBool.key == ImsServiceEnableConfigKeys.OPTIONS_ENABLED_KEY)
              isOptionsEnabled = kvBool.value;
        }

        UceCapabilityInfo uceCapabilityInfo = new UceCapabilityInfo();
        uceCapabilityInfo.isPresenceEnabled = isPresenceEnabled;
        uceCapabilityInfo.isOptionsEnabled = isOptionsEnabled;

        if(getUceExecutor() != null){
            getUceExecutor().execute(
                    () -> {
                        if(uceCbListener != null)
                        uceCbListener.onUceConfigStatusReceived(getRcsCapabilities(uceCapabilityInfo));
                        else
                           Log.e(LOG_TAG,"onUceConfigStatusReceived received presenceStatus: "+
                     String.valueOf(uceCapabilityInfo.isPresenceEnabled) + "optionsStatus: "
                     + String.valueOf(uceCapabilityInfo.isOptionsEnabled));
                    }
            );
           }
           else
           {
                    Log.e(LOG_TAG,"getUceExecutor null onUceConfigStatusReceived received presenceStatus: "+
                     String.valueOf(uceCapabilityInfo.isPresenceEnabled) + "optionsStatus: "
                     + String.valueOf(uceCapabilityInfo.isOptionsEnabled));
           }


        //return getRcsCapabilities(info);

    }

    public void notifyRcsStatus(SettingsValues values)
    {
        boolean isRcsEnabled=false;
        //boolean isOptionsEnabled=false;
        ArrayList<KeyValuePairTypeBool> boolList = values.boolData;
        for(KeyValuePairTypeBool kvBool : boolList){
           if(kvBool.key == ImsServiceEnableConfigKeys.RCS_MESSAGING_ENABLED_KEY){
              isRcsEnabled = kvBool.value;
              break;
           }
        }

        final boolean rcsstatus = isRcsEnabled;
        if(getRcsExecutor() != null){
            getRcsExecutor().execute(
                    () -> {
                        if(rcsCbListener != null)
                        rcsCbListener.onRcsStatusReceived(rcsstatus);
                        else
                           Log.e(LOG_TAG,"notifyRcsStatus received rcsStatus: "+
                     String.valueOf(rcsstatus) );
                    }
            );
        }
           else
           {
                    Log.e(LOG_TAG,"getRcsExecutor null notifyRcsStatus received rcsStatus: "+
                     String.valueOf(rcsstatus) );
           }

        //return isRcsEnabled;

    }

    /**
     * This method will set RcsStatus on modem
     * To be executed on siptransport executor by Sip Transport module
     * @param isRcsEnabled
     */
    public void setRcsStatus(boolean isRcsEnabled){
        ArrayList<KeyValuePairTypeBool> boolList = new ArrayList<>();
        KeyValuePairTypeBool kvBool = new KeyValuePairTypeBool();
        kvBool.key = ImsServiceEnableConfigKeys.RCS_MESSAGING_ENABLED_KEY;
        kvBool.value = isRcsEnabled;
        boolList.add(kvBool);

        SettingsValues v = new SettingsValues();
        v.boolData = boolList;
        sendConfigSettings(SettingsId.IMS_SERVICE_ENABLE_CONFIG,v);
    }

    public void triggerAcsRequest(int reasonCode) {
        try {
             Log.d(LOG_TAG,"triggerAcsRequest with ReasonCode: " + String.valueOf(reasonCode));
            mHidlConfigService.triggerAcsRequest(reasonCode,mUserData);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private class ConfigListener extends IConfigServiceListener.Stub {

        @Override
        public void onCommandStatus(int status, int userData) throws RemoteException {
             if(userData == mUserData && configCbListener!= null)
               configCbListener.notifyCommandStatus(status,userData);
             else
                 Log.d(LOG_TAG,"onCommandStatus from diff client, userdata: "+ String.valueOf(userData));
        }

        @Override
        public void onGetSettingsResponse(int status, SettingsData settingsData, int userData) throws RemoteException {
            if(userData == mUserData && configCbListener!= null )
                configCbListener.handleGetSettingsResp(status,settingsData,userData);
            else
                Log.d(LOG_TAG,"onGetSettingsResponse from diff client, userdata: "+ String.valueOf(userData));
        }

        @Override
        public void onAutoConfigurationReceived(AutoConfig autoConfig) throws RemoteException {
            if(configCbListener!= null)
            configCbListener.handleAutoConfigReceived(autoConfig);
        }

        @Override
        public void onReconfigNeeded() throws RemoteException {
         //ModemSSR
          Log.d(LOG_TAG,"onReconfigNeeded received");
          /* TODO check here if we want to store/take action for some params, as service loads later */
        }

        @Override
        public void onTokenFetchRequest(int i, int i1, int i2) throws RemoteException {
          Log.d(LOG_TAG,"onTokenFetchRequest received ");
          /* TODO check here if we want to store them, as service loads later */
        }

        @Override
        public void onUceStatusUpdate(UceCapabilityInfo uceCapabilityInfo) throws RemoteException {
           //configCbListener.handleUceStatusChange(getUceExecutor(),getRcsCapabilities(uceCapabilityInfo),uceCbListener);
           if(getUceExecutor() != null){
            getUceExecutor().execute(
                    () -> {
                        if(uceCbListener != null)
                        uceCbListener.onUceConfigStatusReceived(getRcsCapabilities(uceCapabilityInfo));
                        else
                           Log.e(LOG_TAG,"onUceConfigStatusReceived received presenceStatus: "+
                     String.valueOf(uceCapabilityInfo.isPresenceEnabled) + "optionsStatus: "
                     + String.valueOf(uceCapabilityInfo.isOptionsEnabled));
                    }
            );
           }
           else
           {
                    Log.e(LOG_TAG,"getUceExecutor null onUceConfigStatusReceived received presenceStatus: "+
                     String.valueOf(uceCapabilityInfo.isPresenceEnabled) + "optionsStatus: "
                     + String.valueOf(uceCapabilityInfo.isOptionsEnabled));
           }

        }

        @Override
        public void onRcsServiceStatusUpdate(boolean b) throws RemoteException {
            if(b && acsQueryListener != null){
                acsQueryListener.onRcsInit();
            }
            if(getRcsExecutor() != null){
            getRcsExecutor().execute(
                    () -> {
                        if(rcsCbListener != null)
                        rcsCbListener.onRcsStatusReceived(b);
                        else
                           Log.e(LOG_TAG,"onRcsStatusReceived received rcsStatus: "+
                     String.valueOf(b) );
                    }
            );
        }
           else
           {
                    Log.e(LOG_TAG,"onRcsStatusReceived received rcsStatus: "+
                     String.valueOf(b) );
           }
        }

        @Override
        public void onAutoConfigErrorSipResponse(AutoConfigResponse autoConfigResponse) throws RemoteException {
            if(configCbListener!= null){
            configCbListener.handleAutoConfigErrorCb(autoConfigResponse);
        }
        }

        @Override
        public void onGetUpdatedSettings(SettingsData settingsData) throws RemoteException {
            if(configCbListener!= null){
            if(settingsData != null){
              if(settingsData.settingsId == SettingsId.IMS_SERVICE_ENABLE_CONFIG)
              {
                  Log.e(LOG_TAG,"onGetUpdatedSettings received settingsData: " + String.valueOf(settingsData));
                  if(getUceExecutor() != null)
                  notifyUceStatus(settingsData.settingsValues);
                  if(getRcsExecutor() != null)
                  notifyRcsStatus(settingsData.settingsValues);
                  //return;
              }
              //handle other settings
            }
            configCbListener.handleGetUpdatedSettingsCb(settingsData);
         }
        }
    };


    public void updateSipStatusOnModem(Executor sipExec, boolean isRcsEnabled, ImsConfigServiceImpl.SipTransportCapUpdateCallback sipCb) {
      setRcsExecutor(sipExec);
      setRcsCbListener(sipCb);
      getRcsStatus(isRcsEnabled);
    }

    public void updateUceStatusOnModem(Executor uceExec, RcsFeature.RcsImsCapabilities caps, ImsConfigServiceImpl.UceCapUpdateCallback uceCb) {
      setUceExecutor(uceExec);
      setUceCbListener(uceCb);
      getUceStatus(caps);
    }

    public void getUceStatus(RcsFeature.RcsImsCapabilities caps) {

        IConfigService.getUceStatusCallback configCb = new IConfigService.getUceStatusCallback() {
            @Override
            public void onValues(int status, UceCapabilityInfo uceCapabilityInfo) {
                //post to Uce Thread
                Log.e(LOG_TAG,"IConfigService.getUceStatusCallback:  received rcsStatus: " + status);
                    /*TODO: print ThreadId in onValues */
                    if(status == RequestStatus.OK){
                    RcsFeature.RcsImsCapabilities localCaps = getRcsCapabilities(uceCapabilityInfo);
                     Log.d(LOG_TAG,"cap from UCE module presence : " + String.valueOf(caps.isCapable(CAPABILITY_TYPE_PRESENCE_UCE)) + " options: "+ caps.isCapable(CAPABILITY_TYPE_OPTIONS_UCE));
                    if(caps.isCapable(CAPABILITY_TYPE_PRESENCE_UCE) == localCaps.isCapable(CAPABILITY_TYPE_PRESENCE_UCE)
                        && caps.isCapable(CAPABILITY_TYPE_OPTIONS_UCE) == localCaps.isCapable(CAPABILITY_TYPE_OPTIONS_UCE)){
                         Log.d(LOG_TAG,"calling onUceConfigStatusReceived");
                         uceEx.execute( () -> {
                                       if(uceCbListener != null)
                                        uceCbListener.onUceConfigStatusReceived(localCaps);
                                } );
                    }
                    else{
                    //     //call set if anything mismatch frm modem and java
                          Log.d(LOG_TAG,"calling setUceStatus");
                        setUceStatus(caps.isCapable(CAPABILITY_TYPE_PRESENCE_UCE),caps.isCapable(CAPABILITY_TYPE_OPTIONS_UCE));
                            }

                } else if(status == RequestStatus.IN_PROGRESS){
                     Log.d(LOG_TAG,"calling setUceStatus status RequestStatus.IN_PROGRESS");
                    setUceStatus(caps.isCapable(CAPABILITY_TYPE_PRESENCE_UCE),caps.isCapable(CAPABILITY_TYPE_OPTIONS_UCE));
                        }
            }
        };
        try {
            Log.e(LOG_TAG, "starting to query UCE Status");
            mHidlConfigService.getUceStatus(configCb);
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Unable to query UCE Status");
        }
    }

    public void setUceCbListener(ImsConfigServiceImpl.UceCapUpdateCallback l) {
        this.uceCbListener = l;
    }

    private RcsFeature.RcsImsCapabilities getRcsCapabilities(UceCapabilityInfo uceCapabilityInfo) {
        RcsFeature.RcsImsCapabilities capabilities = new RcsFeature.RcsImsCapabilities(CAPABILITY_TYPE_NONE);
        int uceCaps = CAPABILITY_TYPE_NONE;
        if(uceCapabilityInfo.isOptionsEnabled) {
            uceCaps = uceCaps|CAPABILITY_TYPE_OPTIONS_UCE;
            Log.d(LOG_TAG,"getRcsCapabilities isOptionsEnabled");
        }
        if(uceCapabilityInfo.isPresenceEnabled) {
            uceCaps = uceCaps|CAPABILITY_TYPE_PRESENCE_UCE;
            Log.d(LOG_TAG,"getRcsCapabilities isPresenceEnabled");
        }
        capabilities.addCapabilities(uceCaps);
        return capabilities;
    }


    public void getRcsStatus(boolean isRcsEnabled){

        IConfigService.getRcsServiceStatusCallback rcsCb = new IConfigService.getRcsServiceStatusCallback() {
            //post rcsservice status to SipTransport thread
            @Override
            public void onValues(int status, boolean b) {

                if(status == RequestStatus.OK){
                    //if rcs status is enabled, query for ACS
                    if(b && acsQueryListener != null){
                        acsQueryListener.onRcsInit();
                    }
                    if(b == isRcsEnabled){
               rcsEx.execute(
                       () -> {
                                    if(rcsCbListener != null){
                                        Log.d(LOG_TAG,"calling onRcsStatusReceived status: " + String.valueOf(b));
                                        rcsCbListener.onRcsStatusReceived(b);
                        }
                        }
                        );
            }
                    else{
                        //call set if anything mismatch frm modem and java
                        Log.d(LOG_TAG,"calling setRcsStatus status" + String.valueOf(isRcsEnabled));
                        setRcsStatus(isRcsEnabled);
                    }
                } else if(status == RequestStatus.IN_PROGRESS){
                    //setUceStatus(caps.isCapable(CAPABILITY_TYPE_PRESENCE_UCE),caps.isCapable(CAPABILITY_TYPE_OPTIONS_UCE));
                    Log.d(LOG_TAG,"calling setRcsStatus status RequestStatus.IN_PROGRESS");
                    setRcsStatus(isRcsEnabled);
                }
            }
        };

        try {
            mHidlConfigService.getRcsServiceStatus(rcsCb);
        }
        catch (RemoteException e){
            Log.e(LOG_TAG,"Unable to fetch RCS Service Status");
        }
    }

    public void setRcsCbListener(ImsConfigServiceImpl.SipTransportCapUpdateCallback l) {
        this.rcsCbListener = l;
    }

    public int setConfig(byte[] data, boolean isCompressed){
        ConfigData configHidlData = new ConfigData();
        int status = RequestStatus.FAIL;
        configHidlData.isCompressed = isCompressed;
        for(Byte b : data) {
            configHidlData.config.add(b);
        }
        //TODO check the 2nd condition
        if(cachedConfig == null /*|| !Arrays.equals(cachedConfig,data) */){
            cachedConfig = data.clone();
        }
        // if iscompressed, check if we have to compress and send the xml or not TODO */
        try{
            Log.d(LOG_TAG,"calling setconfig");
            //custom userData to check the response properly
            status = mHidlConfigService.setConfig(configHidlData, 5678);
        }
        catch(RemoteException e){
            Log.d(LOG_TAG,e.toString());
        }
        return status;
    }

    private int sendConfigSettings(int id, SettingsValues values){
        int status = RequestStatus.FAIL;
        SettingsData settingsData = new SettingsData();
        SettingsValues valuesList = new SettingsValues();
        valuesList.intData = new ArrayList<>(values.intData);
        valuesList.stringData = new ArrayList<>(values.stringData);
        valuesList.boolData = new ArrayList<>(values.boolData);
        settingsData.settingsValues = valuesList;
        settingsData.settingsId = id;

        try {
            Log.d(LOG_TAG,"calling sendConfigSettings " + values.toString());
            //Temp, remove below
            //status = mHidlConfigService.getSettingsValue(SettingsId.IMS_SERVICE_ENABLE_CONFIG,mUserData);
            if(!isRegForInd){
            mHidlConfigService.registerForSettingsChange(mUserData);
               isRegForInd = true;
            }
            status = mHidlConfigService.setSettingsValue(settingsData,mUserData);
        } catch (RemoteException e) {
            Log.d(LOG_TAG,e.toString());
        }

        return status;
    }

    public void registerForSettingsChange()
    {
      try {
       if(!isRegForInd){
             Log.d(LOG_TAG,"registerForSettingsChange ");
            mHidlConfigService.registerForSettingsChange(mUserData);
               isRegForInd = true;
       }
      } catch(RemoteException e) {
        Log.d(LOG_TAG,e.toString());
      }

    }
    public void queryAcsConfiguration()
    {
      try {
            Log.d(LOG_TAG,"calling getAcsConfiguration ");
            mHidlConfigService.getAcsConfiguration(mUserData);
        } catch (RemoteException e) {
            Log.d(LOG_TAG,e.toString());
        }

    }

    public void deregisterForSettingsChange()
    {
      try {
        if(isRegForInd){
          mHidlConfigService.deregisterForSettingsChange(mUserData);
          isRegForInd=false;
        }
      } catch(RemoteException e) {
          Log.d(LOG_TAG,e.toString());
      }
    }

    public void registerAutoConfig(ImsConfigServiceImpl.QueryAutoConfigListener acsQueryListener){
       this.acsQueryListener = acsQueryListener;
    }

}
