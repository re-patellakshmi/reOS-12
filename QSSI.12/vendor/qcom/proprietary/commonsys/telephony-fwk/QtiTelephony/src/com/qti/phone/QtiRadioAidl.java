/*
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */

package com.qti.phone;

import android.os.RemoteException;
import android.os.Binder;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.util.Log;
import android.telephony.ImsiEncryptionInfo;
import android.telephony.PhoneNumberUtils;

import com.qti.extphone.QtiImeiInfo;
import com.qti.extphone.NrConfig;
import com.qti.extphone.NrIconType;
import com.qti.extphone.QtiCallForwardInfo;
import com.qti.extphone.Status;
import com.qti.extphone.Token;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ConcurrentHashMap;

import vendor.qti.hardware.radio.qtiradio.CallForwardInfo;
import vendor.qti.hardware.radio.qtiradio.FacilityLockInfo;
import vendor.qti.hardware.radio.qtiradio.ImeiInfo;

public class QtiRadioAidl implements IQtiRadioConnectionInterface {
    private final String LOG_TAG = "QtiRadioAidl";
    private IQtiRadioConnectionCallback mCallback;
    private int mSlotId;

    private final Token UNSOL = new Token(-1);
    private ConcurrentHashMap<Integer, Token> mInflightRequests = new ConcurrentHashMap<Integer,
            Token>();

    // Synchronization object of HAL interfaces.
    private final Object mHalSync = new Object();
    private IBinder mBinder;
    // The death recepient object which gets notified when IQtiRadio service dies.
    private QtiRadioDeathRecipient mDeathRecipient;
    private final String IQTI_RADIO_STABLE_AIDL_SERVICE_INSTANCE = "slot";
    private String mServiceInstance;

    private vendor.qti.hardware.radio.qtiradio.IQtiRadio mQtiRadio;
    private vendor.qti.hardware.radio.qtiradio.IQtiRadioResponse mQtiRadioResponseAidl;
    private vendor.qti.hardware.radio.qtiradio.IQtiRadioIndication mQtiRadioIndicationAidl;

    private int mCurrentVersion = -1;
    private final int VERSION_ONE = 1;
    private final int BACK_BACK_SS_REQ = 1;

    public QtiRadioAidl(int slotId) {
        mSlotId = slotId;
        mServiceInstance = IQTI_RADIO_STABLE_AIDL_SERVICE_INSTANCE + (mSlotId + 1);
        mDeathRecipient = new QtiRadioDeathRecipient();
        initQtiRadio();
    }

    private void initQtiRadio() {
        Log.i(LOG_TAG,"initQtiRadio");
        mBinder = Binder.allowBlocking(
                ServiceManager.waitForDeclaredService(
                        "vendor.qti.hardware.radio.qtiradio.IQtiRadioStable/"+mServiceInstance));
        if (mBinder == null) {
            Log.e(LOG_TAG, "initQtiRadio failed");
            return;
        }

        vendor.qti.hardware.radio.qtiradio.IQtiRadio qtiRadio =
                vendor.qti.hardware.radio.qtiradio.IQtiRadio.Stub.asInterface(mBinder);
        if(qtiRadio == null) {
            Log.e(LOG_TAG,"Get binder for QtiRadio StableAIDL failed");
            return;
        }
        Log.i(LOG_TAG,"Get binder for QtiRadio StableAIDL is successful");

        try {
            mBinder.linkToDeath(mDeathRecipient, 0 /* Not Used */);
        } catch (android.os.RemoteException ex) {
        }

        synchronized (mHalSync) {
            mQtiRadioResponseAidl = new QtiRadioResponseAidl();
            mQtiRadioIndicationAidl = new QtiRadioIndicationAidl();
            try {
                qtiRadio.setCallbacks(mQtiRadioResponseAidl, mQtiRadioIndicationAidl);
            } catch (android.os.RemoteException ex) {
                Log.e(LOG_TAG, "Failed to call setCallbacks stable AIDL API" + ex);
            }
            mQtiRadio = qtiRadio;

            try {
                mCurrentVersion = mQtiRadio.getInterfaceVersion();
            } catch (android.os.RemoteException ex) {
                Log.e(LOG_TAG, "Exception for getInterfaceVersion()" + ex);
            }
        }
    }

    /**
     * Class that implements the binder death recipient to be notified when
     * IImsRadio service dies.
     */
    final class QtiRadioDeathRecipient implements IBinder.DeathRecipient {
        /**
         * Callback that gets called when the service has died
         */
        @Override
        public void binderDied() {
            Log.e(LOG_TAG, "IQtiRadio Died");
            resetHalInterfaces();
            initQtiRadio();
        }
    }

    private void resetHalInterfaces() {
        Log.d(LOG_TAG, "resetHalInterfaces: Resetting HAL interfaces.");
        if (mBinder != null) {
            try {
                boolean result = mBinder.unlinkToDeath(mDeathRecipient, 0 /* Not used */);
                mBinder = null;
            } catch (Exception ex) {}
        }
        synchronized (mHalSync) {
            mQtiRadio = null;
            mQtiRadioResponseAidl = null;
            mQtiRadioIndicationAidl = null;
        }
    }

    private NrIconType convertHalNrIconType(int iconType) {
        return new NrIconType(iconType);
    }
    private Status convertHalErrorcode(int rilErrorCode) {
        return new Status((rilErrorCode == 0)? Status.SUCCESS : Status.FAILURE);
    }
    private NrConfig convertHalNrConfig(int nrConfig) {
        return new NrConfig(nrConfig);
    }

    private QtiCallForwardInfo[] convertHidlCallForwardInfo2Aidl(
            CallForwardInfo[] callForwardInfos) {
        if (callForwardInfos == null) return null;
        int size = callForwardInfos.length;
        QtiCallForwardInfo[] ret = new QtiCallForwardInfo[size];
        for (int i = 0; i < size; i++) {
            ret[i] = new QtiCallForwardInfo();
            CallForwardInfo cfInfo = callForwardInfos[i];
            ret[i].status = cfInfo.status;
            ret[i].reason = cfInfo.reason;
            ret[i].serviceClass = cfInfo.serviceClass;
            ret[i].toa = cfInfo.toa;
            ret[i].number = cfInfo.number;
            ret[i].timeSeconds = cfInfo.timeSeconds;
        }
        return ret;
    }

    private QtiImeiInfo convertHidlImeiInfo2Aidl(ImeiInfo imeiInfo) {
        return new QtiImeiInfo(mSlotId, imeiInfo.imei, imeiInfo.type);
    }

    class QtiRadioResponseAidl extends vendor.qti.hardware.radio.qtiradio.
            IQtiRadioResponse.Stub {
        @Override
        public final int getInterfaceVersion() {
            return vendor.qti.hardware.radio.qtiradio.IQtiRadioResponse.VERSION;
        }

        @Override
        public String getInterfaceHash() {
            return vendor.qti.hardware.radio.qtiradio.IQtiRadioResponse.HASH;
        }
        /**
         * Response to IQtiRadio.queryNrIconType
         *
         * @param serial to match request/response. Response must include same serial as request.
         * @param errorCode - errorCode as per types.hal returned from RIL.
         * @param NrIconType as per NrIconType.aidl to indicate 5G icon - NONE(Non-5G) or
         *        5G BASIC or 5G UWB shown on the UI.
         */
        @Override
        public void onNrIconTypeResponse(int serial, int errorCode, int iconType)
        {
            Log.d(LOG_TAG, "onNrIconTypeResponse:slotId ="+ mSlotId + "serial = " + serial +
                    " errorCode = " + errorCode + " iconType = " + iconType);
            if (mInflightRequests.containsKey(serial)) {
                Token token = mInflightRequests.get(serial);

                NrIconType nrIconType = convertHalNrIconType(iconType);
                mCallback.onNrIconType(mSlotId, token, convertHalErrorcode(errorCode),
                        nrIconType);
                mInflightRequests.remove(serial);
            } else {
                Log.d(LOG_TAG, "onNrIconTypeResponse: No previous request found for serial = " +
                        serial);
            }
        }
        /**
         * Response to IQtiRadio.enableEndc
         *^M
         * @param serial to match request/response. Response must inclue same serial as request.
         * @param errorCode - errorCode as per types.hal returned from RIL.
         * @param status SUCCESS/FAILURE of the request.
         */
        @Override
        public void onEnableEndcResponse(int serial, int errorCode, int status)
        {
            Log.d(LOG_TAG, "onEnableEndcResponse:slotId ="+ mSlotId + "serial = " + serial +
                    " errorCode = " + errorCode + " " + "status = " + status);
            if (mInflightRequests.containsKey(serial)) {
                Token token = mInflightRequests.get(serial);
                mCallback.onEnableEndc(mSlotId, token, convertHalErrorcode(errorCode));
                mInflightRequests.remove(serial);
            } else {
                Log.d(LOG_TAG, "onEnableEndcResponse: No previous request found for serial = " +
                        serial);
            }
        }
        /**
         * Response to IQtiRadio.queryEndcStatus
         *
         * @param serial to match request/response. Response must inclue same serial as request.
         * @param errorCode - errorCode as per types.hal returned from RIL.
         * @param endcStatus values as per types.hal to indicate ENDC is enabled/disabled.
         */
        @Override
        public void onEndcStatusResponse(int serial, int errorCode, int endcStatus)
        {
            Log.d(LOG_TAG, "onEndcStatusResponse:slotId ="+ mSlotId + "serial = " + serial +
                    " errorCode = " + errorCode + " " + "enabled = " + endcStatus);
            if (mInflightRequests.containsKey(serial)) {
                Token token = mInflightRequests.get(serial);

                boolean isEnabled = (endcStatus == vendor.qti.hardware.radio.qtiradio
                        .EndcStatus.ENABLED);
                mCallback.onEndcStatus(mSlotId, token, convertHalErrorcode(errorCode), isEnabled);
                mInflightRequests.remove(serial);
            } else {
                Log.d(LOG_TAG, "onEndcStatusResponse: No previous request found for serial = " +
                        serial);
            }
        }
        /**
         * Response to IQtiRadio.SetNrConfig
         *
         * @param serial to match request/response. Response must inclue same serial as request.
         * @param errorCode - errorCode as per types.hal returned from RIL.
         * @param status SUCCESS/FAILURE of the request.
         */
        @Override
        public void setNrConfigResponse(int serial, int errorCode, int status)
        {
            Log.d(LOG_TAG,"setNrConfigResponse:slotId ="+ mSlotId + " serial = " + serial +
                    " errorCode = " + errorCode + " status = " + status);
            if (mInflightRequests.containsKey(serial)) {
                Token token = mInflightRequests.get(serial);
                mCallback.onSetNrConfig(mSlotId, token, convertHalErrorcode(errorCode));
                mInflightRequests.remove(serial);
            } else {
                Log.d(LOG_TAG, "setNrConfigResponse: No previous request found for serial = " +
                        serial);
            }
        }
        /**
         * Response to IQtiRadio.queryNrConfig
         *
         * @param serial to match request/response. Response must inclue same serial as request.
         * @param errorCode - errorCode as per types.hal returned from RIL.
         * @param enabled values as per NrConfig.aidl to indicate status of NrConfig.
         */
        @Override
        public void onNrConfigResponse(int serial, int errorCode, int nrConfig)
        {
            Log.d(LOG_TAG, "onNrConfigResponse:slotId ="+ mSlotId + "serial = " + serial +
                    " errorCode = " + errorCode + " nrConfig = " + nrConfig);
            if (mInflightRequests.containsKey(serial)) {
                Token token = mInflightRequests.get(serial);
                NrConfig config = convertHalNrConfig(nrConfig);
                mCallback.onNrConfigStatus(mSlotId, token, convertHalErrorcode(errorCode), config);
                mInflightRequests.remove(serial);
            } else {
                Log.d(LOG_TAG, "onNrConfigResponse: No previous request found for serial = " +
                        serial);
            }
        }

        /**
         * Response to IQtiRadio.getQtiRadioCapability
         *
         * @param serial to match request/response. Response must inclue same serial as request.
         * @param errorCode - errorCode as per types.hal returned from RIL.
         * @param enabled values as per NrConfig.aidl to indicate status of NrConfig.
         */
        @Override
        public void getQtiRadioCapabilityResponse(int serial, int errorCode, int radioAccessFamily)
        {
            Log.d(LOG_TAG, "getQtiRadioCapabilityResponse:slotId ="+ mSlotId + "serial = " +
                    serial + " errorCode = " + errorCode + " raf = " + radioAccessFamily);
            if (mInflightRequests.containsKey(serial)) {
                Token token = mInflightRequests.get(serial);
                mCallback.getQtiRadioCapabilityResponse(mSlotId, token,
                        convertHalErrorcode(errorCode),
                        QtiRadioUtils.convertToQtiNetworkTypeBitMask(radioAccessFamily));
                mInflightRequests.remove(serial);
            } else {
                Log.d(LOG_TAG, "getQtiRadioCapabilityResponse: No previous request" +
                        "found for serial = " + serial);
            }
        }

        /**
        * Response to IQtiRadio.getCallForwardStatus
        * @param serial to match request/response. Response must include same serial as request.
        * @param errorCode - errorCode as per types.hal returned from RIL.
        * @param callInfoForwardInfoList list of call forward status information for different
        * service classes.
        */
        @Override
        public void getCallForwardStatusResponse(int serial, int errorCode, CallForwardInfo[]
                callForwardInfoList) {
            Log.d(LOG_TAG, "getCallForwardStatusResponse:slotId ="+ mSlotId + "serial = " +
                    serial + " errorCode = " + errorCode);
            if (mInflightRequests.containsKey(serial)) {
                Token token = mInflightRequests.get(serial);
                mCallback.getCallForwardStatusResponse(mSlotId, token,
                        convertHalErrorcode(errorCode),
                        convertHidlCallForwardInfo2Aidl(callForwardInfoList));
                mInflightRequests.remove(serial);
            } else {
                Log.d(LOG_TAG, "getCallForwardStatusResponse: No previous request" +
                        "found for serial = " + serial);
            }
        }

        /**
        * Response to IQtiRadio.getFacilityLockForApp
        * @param serial to match request/response. Response must include same serial as request.
        * @param errorCode - errorCode as per types.hal returned from RIL.
        * @param response 0 is the TS 27.007 service class bit vector of services for which the
        *        specified barring facility is active. "0" means "disabled for all"
        */
        @Override
        public void getFacilityLockForAppResponse(int serial, int errorCode, int response) {
            Log.d(LOG_TAG, "getFacilityLockForAppResponse:slotId ="+ mSlotId + "serial = " +
                    serial + " errorCode = " + errorCode);
            if (mInflightRequests.containsKey(serial)) {
                Token token = mInflightRequests.get(serial);

                //Lock status enabled or disabled
                int[] ret = new int[1];
                ret[0] = response;

                mCallback.getFacilityLockForAppResponse(mSlotId, token,
                        convertHalErrorcode(errorCode), ret);
                mInflightRequests.remove(serial);
            } else {
                Log.d(LOG_TAG, "getFacilityLockForAppResponse: No previous request" +
                        "found for serial = " + serial);
            }
        }

        /**
        * Response to IQtiRadio.getImei
        * @param serial to match request/response. Response must include same serial as request.
        * @param errorCode - errorCode as per types.hal returned from RIL.
        * @param imeiInfo - provides current slot IMEI, its type as Primary, Secondary or Invalid
        */
        @Override
        public void getImeiResponse(int serial, int errorCode, ImeiInfo imeiInfo) {
            Log.d(LOG_TAG, "getImeiResponse: slotId ="+ mSlotId + "serial = " +
                    serial + " errorCode = " + errorCode + " imeitype " + imeiInfo.type);
            if (mInflightRequests.containsKey(serial)) {
                Token token = mInflightRequests.get(serial);

                mCallback.getImeiResponse(mSlotId, token,
                        convertHalErrorcode(errorCode), convertHidlImeiInfo2Aidl(imeiInfo));
                mInflightRequests.remove(serial);
            } else {
                Log.d(LOG_TAG, "getImeiResponse: No previous request" +
                        "found for serial = " + serial);
            }
        }
  
        /* Response to IQtiRadio.getDdsSwitchCapability
         *
         * @param serial to match request/response. Response must include same serial as request.
         * @param errorCode - errorCode as per RIL_Errno part of
         *                  hardware/ril/include/telephony/ril.h.
         * @param support true/false if smart dds switch capability is supported or not.
         */
        public void getDdsSwitchCapabilityResponse(int serial,
                int errorCode, boolean support) {
            Log.d(LOG_TAG, "getDdsSwitchCapabilityResponse:slotId = "+ mSlotId +
                    " serial = " + serial + " errorCode = " + errorCode + " support = " + support);
            if (mInflightRequests.containsKey(serial)) {
                Token token = mInflightRequests.get(serial);
                mCallback.onDdsSwitchCapabilityChange(mSlotId, token,
                        convertHalErrorcode(errorCode), support);
                mInflightRequests.remove(serial);
            } else {
                Log.d(LOG_TAG, "getDdsSwitchCapabilityResponse:" +
                        "No previous request found for serial = " + serial);
            }
        }

        /**
         * Response to IQtiRadio.sendUserPreferenceForDataDuringVoiceCall
         *
         * @param serial to match request/response. Response must include same serial as request.
         * @param errorCode - errorCode as per RIL_Errno part of
         *                  hardware/ril/include/telephony/ril.h.
         * @param status SUCCESS/FAILURE of the request.
         */
        public void sendUserPreferenceForDataDuringVoiceCallResponse(int serial,
                int errorCode) {
            Log.d(LOG_TAG, "sendUserPreferenceForDataDuringVoiceCallResponse:slotId = "
                    + mSlotId + " serial = " + serial + " errorCode = " + errorCode);
            if (mInflightRequests.containsKey(serial)) {
                Token token = mInflightRequests.get(serial);
                mCallback.onSendUserPreferenceForDataDuringVoiceCall(mSlotId, token,
                        convertHalErrorcode(errorCode));
                mInflightRequests.remove(serial);
            } else {
                Log.d(LOG_TAG, "sendUserPreferenceForDataDuringVoiceCallResponse:" +
                        "No previous request found for serial = " + serial);
            }
        }
    }

    class QtiRadioIndicationAidl extends vendor.qti.hardware.radio.qtiradio.
            IQtiRadioIndication.Stub {
        @Override
        public final int getInterfaceVersion() {
            return vendor.qti.hardware.radio.qtiradio.IQtiRadioIndication.VERSION;
        }

        @Override
        public String getInterfaceHash() {
            return vendor.qti.hardware.radio.qtiradio.IQtiRadioIndication.HASH;
        }
        /*
         * Unsol msg to indicate changes in 5G Icon Type.
         *
         * @param NrIconType as per NrIconType.aidl to indicate 5G icon - NONE(Non-5G) or
         *         5G BASIC or 5G UWB shown on the UI.
         *
         */
        @Override
        public void onNrIconTypeChange(int iconType)
        {
            Log.d(LOG_TAG, "onNrIconTypeChange: slotId = " + mSlotId + "NrIconType = " + iconType);
            NrIconType nrIconType = convertHalNrIconType(iconType);
            mCallback.onNrIconType(mSlotId, UNSOL, new Status(Status.SUCCESS),
                    nrIconType);
        }
        /*
         * Unsol msg to indicate change in NR Config.
         *
         * @param NrConfig as per types.hal to indicate NSA/SA/NSA+SA.
         *
         */
        @Override
        public void onNrConfigChange(int config)
        {
        }

        /*
         * Unsol msg to indicate change in Primary IMEI mapping.
         *
         * @param imeiInfo, IMEI value and its Type, Primary/Secondary/Invalid.
         */
        @Override
        public void onImeiChange(ImeiInfo imeiInfo) {
            Log.d(LOG_TAG, "onImeiChange: slotId = " + mSlotId + "Imei = " +
                    imeiInfo.imei + " type: " + imeiInfo.type);
            QtiImeiInfo qtiImeiInfo = convertHidlImeiInfo2Aidl(imeiInfo);
            mCallback.onImeiChange(mSlotId, qtiImeiInfo);
        }

        /* Unsol msg to inform HLOS that smart DDS switch capability changed.
         * Upon receiving this unsol, HLOS has to inform modem if user has enabled
         * temp DDS switch from UI or not.
         *
         */
        public void onDdsSwitchCapabilityChange() {
            Log.d(LOG_TAG, "onDdsSwitchCapabilityChange: slotId = " + mSlotId);
            mCallback.onDdsSwitchCapabilityChange(mSlotId, UNSOL,
                    new Status(Status.SUCCESS), true);
        }

        /*
         * Unsol msg to indicate if telephony has to enable/disable its temp DDS switch logic
         * If telephony temp DDS switch is disabled, then telephony will wait for
         * modem recommendations in seperate indication to perform temp DDS switch.
         *
         * @param telephonyDdsSwitch true/false based on telephony temp DDS switch
         *          logic should be enabled/disabled.
         */
        public void onDdsSwitchCriteriaChange(boolean telephonyDdsSwitch) {
            Log.d(LOG_TAG, "onDdsSwitchCriteriaChange: slotId = " + mSlotId +
                    "telephonyDdsSwitch = " + telephonyDdsSwitch);
            mCallback.onDdsSwitchCriteriaChange (mSlotId, UNSOL, telephonyDdsSwitch);
        }

        /*
         * Unsol msg to indicate modem recommendation for temp DDS switch.
         *
         * @param recommendedSlotId slot ID to which DDS has to be switched.
         *
         */
        public void onDdsSwitchRecommendation(int recommendedSlotId) {
            Log.d(LOG_TAG, "onDdsSwitchRecommendation: slotId = " + mSlotId +
                    "recommendedSlotId = " + recommendedSlotId);
            mCallback.onDdsSwitchRecommendation(mSlotId, UNSOL, recommendedSlotId);
        }
    }

    @java.lang.Override
    public int getPropertyValueInt(String property, int def) throws RemoteException {
        Log.d(LOG_TAG, "getPropertyValueInt: property = " + property + "default = " + def);
        try {
            return Integer.parseInt(mQtiRadio.getPropertyValue(property, String.valueOf(def)));
        } catch(android.os.RemoteException ex) {
            Log.e(LOG_TAG, "getPropertyValue Failed" + ex);
            return SystemProperties.getInt(property, def);
        }
    }

    @java.lang.Override
    public boolean getPropertyValueBool(String property, boolean def) throws RemoteException {
        Log.d(LOG_TAG, "getPropertyValueBool: property = " + property + "default = " + def);
        try {
            return Boolean.parseBoolean(mQtiRadio.getPropertyValue(property, String.valueOf(def)));
        } catch(android.os.RemoteException ex) {
            Log.e(LOG_TAG, "getPropertyValue Failed" + ex);
            return SystemProperties.getBoolean(property, def);
        }
    }

    @java.lang.Override
    public String getPropertyValueString(String property, String def) throws RemoteException {
        Log.d(LOG_TAG, "getPropertyValueString: property = " + property + "default = " + def);
        try {
            return mQtiRadio.getPropertyValue(property, def);
        } catch(android.os.RemoteException ex) {
            Log.e(LOG_TAG, "getPropertyValue Failed" + ex);
            return SystemProperties.get(property, def);
        }
    }

    @java.lang.Override
    public void enableEndc(boolean enable, Token token) throws RemoteException {
        int serial = token.get();
        Log.d(LOG_TAG, "enableEndc: serial = " + serial + "enable = " + enable);
        mInflightRequests.put(serial, token);
        try {
            mQtiRadio.enableEndc(serial, enable);
        } catch(android.os.RemoteException ex) {
            mInflightRequests.remove(serial, token);
            Log.e(LOG_TAG, "enableEndc Failed." + ex);
        }
    }

    @java.lang.Override
    public void queryNrIconType(Token token) throws RemoteException {
        int serial = token.get();
        Log.d(LOG_TAG, "queryNrIconType: serial = " + serial);
        mInflightRequests.put(serial, token);
        try {
            mQtiRadio.queryNrIconType(serial);
        } catch(android.os.RemoteException ex) {
            mInflightRequests.remove(serial, token);
            Log.e(LOG_TAG, "queryNrIconType Failed." + ex);
        }
    }

    @java.lang.Override
    public void queryEndcStatus(Token token) throws RemoteException {
        int serial = token.get();
        Log.d(LOG_TAG, "queryEndcStatus: serial = " + serial);
        mInflightRequests.put(serial, token);
        try {
            mQtiRadio.queryEndcStatus(serial);
        } catch(android.os.RemoteException ex) {
            mInflightRequests.remove(serial, token);
            Log.e(LOG_TAG, "queryEndcStatus Failed." + ex);
        }
    }

    @java.lang.Override
    public void setNrConfig(NrConfig config, Token token) throws RemoteException {
        int serial = token.get();
        Log.d(LOG_TAG, "setNrConfig: serial = " + serial +
                "NrConfig= " + config.get());
        mInflightRequests.put(serial, token);
        try {
            mQtiRadio.setNrConfig(serial, config.get());
        } catch(android.os.RemoteException ex) {
            mInflightRequests.remove(serial, token);
            Log.e(LOG_TAG, "setNrConfig Failed." + ex);
        }
    }

    @java.lang.Override
    public void queryNrConfig(Token token) throws RemoteException {
        int serial = token.get();
        Log.d(LOG_TAG, "queryNrConfig: serial = " + serial);
        mInflightRequests.put(serial, token);
        try {
            mQtiRadio.queryNrConfig(serial);
        } catch(android.os.RemoteException ex) {
            mInflightRequests.remove(serial, token);
            Log.e(LOG_TAG, "queryNrConfig Failed." + ex);
        }
    }

    @java.lang.Override
    public void getQtiRadioCapability(Token token) throws RemoteException {
        int serial = token.get();
        Log.d(LOG_TAG, "getQtiRadioCapability: serial = " + serial);
        mInflightRequests.put(serial, token);
        try {
            mQtiRadio.getQtiRadioCapability(serial);
        } catch(android.os.RemoteException ex) {
            mInflightRequests.remove(serial, token);
            Log.e(LOG_TAG, "getQtiRadioCapability Failed." + ex);
        }
    }

    @Override
    public void setCarrierInfoForImsiEncryption(Token token,
            ImsiEncryptionInfo imsiEncryptionInfo) {
        Log.d(LOG_TAG, "Not Supported");
    }

    @java.lang.Override
    public void enable5g(Token token) throws RemoteException {
        Log.d(LOG_TAG, "Not Supported");
    }

    @java.lang.Override
    public void disable5g(Token token) throws RemoteException {
        Log.d(LOG_TAG, "Not Supported");
    }

    @java.lang.Override
    public void queryNrBearerAllocation(Token token) throws RemoteException {
        Log.d(LOG_TAG, "Not Supported");
    }

    @java.lang.Override
    public void enable5gOnly(Token token) throws RemoteException {
        Log.d(LOG_TAG, "Not Supported");
    }

    @java.lang.Override
    public void query5gStatus(Token token) throws RemoteException {
        Log.d(LOG_TAG, "Not Supported");
    }

    @java.lang.Override
    public void queryNrDcParam(Token token) throws RemoteException {
        Log.d(LOG_TAG, "Not Supported");
    }

    @java.lang.Override
    public void queryNrSignalStrength(Token token) throws RemoteException {
        Log.d(LOG_TAG, "Not Supported");
    }

    @java.lang.Override
    public void queryUpperLayerIndInfo(Token token) throws RemoteException {
        Log.d(LOG_TAG, "Not Supported");
    }

    @java.lang.Override
    public void query5gConfigInfo(Token token) throws RemoteException {
        Log.d(LOG_TAG, "Not Supported");
    }

    @Override
    public void sendCdmaSms(byte[] pdu, boolean expectMore, Token token) {
        Log.d(LOG_TAG, "Not Supported");
    }

    @Override
    public boolean isFeatureSupported(int feature) {
        switch (feature) {
            case BACK_BACK_SS_REQ:
                if (mCurrentVersion > VERSION_ONE) {
                    Log.d(LOG_TAG, "BACK_BACK_SS_REQ feature Supported");
                    return true;
                }
            default:
                return false;
        }
    }

    @Override
    public void queryCallForwardStatus(Token token, int cfReason, int serviceClass,
            String number, boolean expectMore) throws RemoteException {
        int serial = token.get();
        Log.d(LOG_TAG, "queryCallForwardStatus: serial = " + serial);
        mInflightRequests.put(serial, token);

        CallForwardInfo cfInfo = new CallForwardInfo();
        cfInfo.reason = cfReason;
        cfInfo.serviceClass = serviceClass;
        cfInfo.toa = PhoneNumberUtils.toaFromString(number);
        cfInfo.number = convertNullToEmptyString(number);
        cfInfo.timeSeconds = 0;
        cfInfo.expectMore = expectMore;

        try {
            mQtiRadio.getCallForwardStatus(serial, cfInfo);
        } catch(android.os.RemoteException ex) {
            mInflightRequests.remove(serial, token);
            Log.e(LOG_TAG, "queryCallForwardStatus Failed." + ex);
        }
    }

    @Override
    public void getFacilityLockForApp(Token token, String facility, String password,
                int serviceClass, String appId, boolean expectMore) throws RemoteException {
        int serial = token.get();
        Log.d(LOG_TAG, "getFacilityLockForApp: serial = " + serial);
        mInflightRequests.put(serial, token);

        FacilityLockInfo facLockInfo = new FacilityLockInfo();
        facLockInfo.facility = convertNullToEmptyString(facility);
        facLockInfo.password = convertNullToEmptyString(password);
        facLockInfo.serviceClass = serviceClass;
        facLockInfo.appId = convertNullToEmptyString(appId);
        facLockInfo.expectMore = expectMore;

        try {
            mQtiRadio.getFacilityLockForApp(serial, facLockInfo);
        } catch(android.os.RemoteException ex) {
            mInflightRequests.remove(serial, token);
            Log.e(LOG_TAG, "getFacilityLockForApp Failed." + ex);
        }
    }

    @Override
    public void getImei(Token token) throws RemoteException {
        int serial = token.get();
        Log.d(LOG_TAG, "getImei: serial = " + serial);
        mInflightRequests.put(serial, token);
        try {
            mQtiRadio.getImei(serial);
        } catch (android.os.RemoteException ex) {
            mInflightRequests.remove(serial, token);
            Log.e(LOG_TAG, "getImei Failed." + ex);
        }
    }

    @Override
    public void getDdsSwitchCapability(Token token) {
        int serial = token.get();
        Log.d(LOG_TAG, "getDdsSwitchCapability: serial = " + serial);
        mInflightRequests.put(serial, token);

        try {
            mQtiRadio.getDdsSwitchCapability(serial);
        } catch(android.os.RemoteException ex) {
            mInflightRequests.remove(serial, token);
            Log.e(LOG_TAG, "getDdsSwitchCapability Failed." + ex);
        }
    }

    @Override
    public void sendUserPreferenceForDataDuringVoiceCall(Token token,
            boolean userPreference) {
        int serial = token.get();
        Log.d(LOG_TAG, "sendUserPreferenceForDataDuringVoiceCall: serial = " + serial +
                " slotId = "+ mSlotId + " userPreference: " + userPreference);
        mInflightRequests.put(serial, token);

        try {
            mQtiRadio.sendUserPreferenceForDataDuringVoiceCall(serial, userPreference);
        } catch(android.os.RemoteException ex) {
            mInflightRequests.remove(serial, token);
            Log.e(LOG_TAG, "sendUserPreferenceForDataDuringVoiceCall Failed." + ex);
        }
    }

    private static String convertNullToEmptyString(String string) {
        return string != null ? string : "";
    }

    @Override
    public void registerCallback(IQtiRadioConnectionCallback callback) {
        Log.d(LOG_TAG, "registerCallback: callback = " + callback);
        mCallback = callback;
    }

    @Override
    public void unRegisterCallback(IQtiRadioConnectionCallback callback) {
        Log.d(LOG_TAG, "unRegisterCallback: callback = " + callback);
        if (mCallback == callback) {
            mCallback = null;
        }
    }
}
