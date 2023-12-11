/*
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */
package com.qti.phone;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.IHwBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.Binder;
import android.util.Log;
import android.telephony.ImsiEncryptionInfo;
import androidx.preference.PreferenceManager;

import com.qti.extphone.Client;
import com.qti.extphone.IExtPhoneCallback;
import com.qti.extphone.DcParam;
import com.qti.extphone.NrConfig;
import com.qti.extphone.NrConfigType;
import com.qti.extphone.NrIconType;
import com.qti.extphone.QtiCallForwardInfo;
import com.qti.extphone.QtiImeiInfo;
import com.qti.extphone.QRadioResponseInfo;
import com.qti.extphone.SignalStrength;
import com.qti.extphone.SmsResult;
import com.qti.extphone.Status;
import com.qti.extphone.Token;
import com.qti.extphone.BearerAllocationStatus;
import com.qti.extphone.UpperLayerIndInfo;

import vendor.qti.data.factory.V2_4.IFactory;
import vendor.qti.hardware.data.dynamicdds.V1_0.IToken;
import vendor.qti.hardware.data.dynamicdds.V1_0.ISubscriptionManager;
import vendor.qti.hardware.data.dynamicdds.V1_0.StatusCode;

import java.util.ArrayList;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;
import java.util.NoSuchElementException;

public class QtiRadioProxy {

    private static final String TAG = "QtiRadioProxy";

    private static final int DEFAULT_PHONE_INDEX = 0;
    private static final int EVENT_ON_NR_ICON_TYPE = 1;
    private static final int EVENT_ON_ENABLE_ENDC = 2;
    private static final int EVENT_ON_ENDC_STATUS = 3;
    private static final int EVENT_ON_SET_NR_CONFIG = 4;
    private static final int EVENT_ON_NR_CONFIG_STATUS = 5;
    private static final int EVENT_ON_BEARER_ALLOCATION_CHANGE_IND = 6;
    private static final int EVENT_ON_5G_ENABLE_STATUS_CHANGE_IND = 7;
    private static final int EVENT_ON_NR_DC_PARAM = 8;
    private static final int EVENT_ON_UPPER_LAYER_IND_INFO = 9;
    private static final int EVENT_ON_5G_CONFIG_INFO = 10;
    private static final int EVENT_ON_SIGNAL_STRENGTH = 11;
    private static final int EVENT_QTI_RADIO_CAPABILITY_RESPONSE = 12;
    private static final int EVENT_SEND_CDMA_SMS_RESPONSE = 13;
    private static final int EVENT_SEND_CARRIER_INFO_RESPONSE = 14;
    private static final int EVENT_CALL_FORWARD_QUERY_RESPONSE = 15;
    private static final int EVENT_FACILITY_LOCK_QUERY_RESPONSE = 16;
    private static final int EVENT_SMART_DDS_SWITCH_TOGGLE = 17;
    private static final int EVENT_ON_SMART_DDS_SWITCH_TOGGLE_RESPONSE = 18;
    private static final int EVENT_GET_IMEI_RESPONSE = 19;
    private static final int EVENT_IMEI_CHANGE_IND_INFO = 20;
    private static final int EVENT_ON_ALLOW_MODEM_RECOMMENDATION_FOR_DATA_DURING_CALL = 21;
    private static final int EVENT_ON_DDS_SWITCH_CAPABILITY_CHANGE = 22;
    private static final int EVENT_ON_AUTO_DDS_SWITCH_CHANGE = 23;
    private static final int EVENT_ON_DDS_SWITCH_RECOMMENDATION = 24;

    private final String SMART_DDS_SWITCH_TOGGLE_VALUE = "smartDdsSwitchValue";
    private final int SMART_DDS_SWITCH_OFF = 0;
    private final int SMART_DDS_SWITCH_ON = 1;

    private boolean SUCCESS = true;
    private boolean FAILED = false;

    private static Context mContext;
    private IQtiRadioConnectionInterface[] mQtiRadio;
    private volatile int mSerial = -1;
    private AppOpsManager mAppOpsManager;
    private int mClientIndex = -1;
    private HandlerThread mWorkerThread = new HandlerThread(TAG + "BgThread");
    private Handler mWorkerThreadHandler;
    private final long mDeathBinderCookie = Integer.MAX_VALUE;
    private static ISubscriptionManager sDynamicSubscriptionManager;
    private static SharedPreferences sSharedPref;
    private final ArrayList<IExtPhoneCallback> mCallbackList = new ArrayList<IExtPhoneCallback>();
    private ConcurrentHashMap<Integer, Transaction> mInflightRequests = new
            ConcurrentHashMap<Integer, Transaction>();
    private ArrayList<IQtiRadioInternalCallback> mInternalCallbackList = new
            ArrayList<IQtiRadioInternalCallback>();


    private class WorkerHandler extends Handler {
        private static final String TAG = QtiRadioProxy.TAG + "Handler: ";

        public WorkerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage msg.what = " + msg.what);
            switch (msg.what) {
                case EVENT_SMART_DDS_SWITCH_TOGGLE: {
                    Log.d(TAG, "EVENT_SMART_DDS_SWITCH_TOGGLE");
                    Result result = (Result) msg.obj;
                    Token token = result.mToken;
                    boolean isEnabled = (Boolean) result.mData;
                    setDynamicSubscriptionChange(token, isEnabled);
                    break;
                }

                case EVENT_ON_SMART_DDS_SWITCH_TOGGLE_RESPONSE: {
                    Log.d(TAG, "EVENT_ON_SMART_DDS_SWITCH_TOGGLE_RESPONSE");
                    Token token = (Token) msg.obj;
                    int status =  msg.arg1;
                    int toggleValue = msg.arg2;
                    setSmartDdsSwitchToggleResponse(token, status, toggleValue);
                    break;
                }

                case EVENT_ON_NR_ICON_TYPE: {
                    Log.d(TAG, "EVENT_ON_NR_ICON_TYPE");
                    int slotId = msg.arg1;
                    Result result = (Result) msg.obj;
                    onNrIconType(slotId, result.mToken, result.mStatus,
                            (NrIconType) result.mData);
                    break;
                }

                case EVENT_ON_ENABLE_ENDC: {
                    Log.d(TAG, "EVENT_ON_ENABLE_ENDC");
                    int slotId = msg.arg1;
                    Result result = (Result) msg.obj;
                    onEnableEndc(slotId, result.mToken, result.mStatus);
                    break;
                }

                case EVENT_ON_ENDC_STATUS: {
                    Log.d(TAG, "EVENT_ON_ENDC_STATUS");
                    int slotId = msg.arg1;
                    Result result = (Result) msg.obj;
                    onEndcStatus(slotId, result.mToken, result.mStatus, (boolean) result.mData);
                    break;
                }

                case EVENT_ON_SET_NR_CONFIG: {
                    Log.d(TAG, "EVENT_ON_SET_NR_CONFIG");
                    int slotId = msg.arg1;
                    Result result = (Result) msg.obj;
                    onSetNrConfig(slotId, result.mToken, result.mStatus);
                    break;
                }

                case EVENT_ON_NR_CONFIG_STATUS: {
                    Log.d(TAG, "EVENT_ON_NR_CONFIG_STATUS");
                    int slotId = msg.arg1;
                    Result result = (Result) msg.obj;
                    onNrConfigStatus(slotId, result.mToken, result.mStatus,
                            (NrConfig) result.mData);
                    break;
                }

                case EVENT_ON_5G_ENABLE_STATUS_CHANGE_IND: {
                    Log.d(TAG, "EVENT_ON_5G_ENABLE_STATUS");
                    int slotId = msg.arg1;
                    Result result = (Result) msg.obj;
                    on5gStatus(slotId, result.mToken, result.mStatus, (boolean) result.mData);
                    break;
                }

                case EVENT_ON_BEARER_ALLOCATION_CHANGE_IND: {
                    Log.d(TAG, "EVENT_ON_BEARER_ALLOCATION_CHANGE_IND");
                    int slotId = msg.arg1;
                    Result result = (Result) msg.obj;
                    onAnyNrBearerAllocation(slotId, result.mToken, result.mStatus,
                            (BearerAllocationStatus) result.mData);
                    break;
                }

                case EVENT_ON_NR_DC_PARAM: {
                    Log.d(TAG, "EVENT_ON_NR_DC_PARAM");
                    int slotId = msg.arg1;
                    Result result = (Result) msg.obj;
                    onNrDcParam(slotId, result.mToken, result.mStatus,
                            (DcParam) result.mData);
                    break;
                }

                case EVENT_ON_UPPER_LAYER_IND_INFO: {
                    Log.d(TAG, "EVENT_ON_UPPER_LAYER_IND_INFO");
                    int slotId = msg.arg1;
                    Result result = (Result) msg.obj;
                    onUpperLayerIndInfo(slotId, result.mToken, result.mStatus,
                            (UpperLayerIndInfo) result.mData);
                    break;
                }

                case EVENT_ON_5G_CONFIG_INFO: {
                    Log.d(TAG, "EVENT_ON_5G_CONFIG_INFO");
                    int slotId = msg.arg1;
                    Result result = (Result) msg.obj;
                    on5gConfigInfo(slotId, result.mToken, result.mStatus,
                            (NrConfigType) result.mData);
                    break;
                }

                case EVENT_ON_SIGNAL_STRENGTH: {
                    Log.d(TAG, "EVENT_ON_SIGNAL_STRENGTH");
                    int slotId = msg.arg1;
                    Result result = (Result) msg.obj;
                    onSignalStrength(slotId, result.mToken, result.mStatus,
                            (SignalStrength) result.mData);
                    break;
                }

                case EVENT_QTI_RADIO_CAPABILITY_RESPONSE: {
                    Log.d(TAG, "EVENT_QTI_RADIO_CAPABILITY_RESPONSE");
                    int slotId = msg.arg1;
                    Result result = (Result) msg.obj;
                    getQtiRadioCapabilityResponse(slotId, result.mToken, result.mStatus,
                            (int) result.mData);
                    break;
                }

                case EVENT_SEND_CDMA_SMS_RESPONSE: {
                    Log.d(TAG, "EVENT_SEND_CDMA_SMS_RESPONSE");
                    int slotId = msg.arg1;
                    Result result = (Result) msg.obj;
                    sendCdmaSmsResponse(slotId, result.mToken, result.mStatus,
                            (SmsResult) result.mData);
                    break;
                }

                case EVENT_SEND_CARRIER_INFO_RESPONSE: {
                    Log.d(TAG, "EVENT_SEND_CARRIER_INFO_RESPONSE");
                    int slotId = msg.arg1;
                    Result result = (Result) msg.obj;
                    setCarrierInfoForImsiEncryptionResponse(slotId, result.mToken, result.mStatus,
                            (QRadioResponseInfo) result.mData);
                    break;
                }

                case EVENT_CALL_FORWARD_QUERY_RESPONSE: {
                    Log.d(TAG, "EVENT_CALL_FORWARD_QUERY_RESPONSE");
                    Result result = (Result) msg.obj;
                    sendcallforwardqueryResponse(result.mToken, result.mStatus,
                            (QtiCallForwardInfo[]) result.mData);
                    break;
                }

                case EVENT_FACILITY_LOCK_QUERY_RESPONSE: {
                    Log.d(TAG, "EVENT_FACILITY_LOCK_RESPONSE");
                    Result result = (Result) msg.obj;
                    sendfacilityLockResponse(result.mToken, result.mStatus, (int[]) result.mData);
                    break;
                }

                case EVENT_GET_IMEI_RESPONSE: {
                    Log.d(TAG, "EVENT_GET_IMEI_RESPONSE");
                    int slotId = msg.arg1;
                    Result result = (Result) msg.obj;
                    sendImeiInfoResponse(slotId, result.mToken, result.mStatus,
                            (QtiImeiInfo) result.mData);
                    break;
                }

                case EVENT_IMEI_CHANGE_IND_INFO: {
                    Log.d(TAG, "EVENT_IMEI_CHANGE_IND_INFO");
                    int slotId = msg.arg1;
                    Result result = (Result) msg.obj;
                    sendImeiInfoIndInternal(slotId, (QtiImeiInfo) result.mData);
                    break;
                }

                case EVENT_ON_ALLOW_MODEM_RECOMMENDATION_FOR_DATA_DURING_CALL: {
                    int slotId = msg.arg1;
                    Result result = (Result) msg.obj;
                    onSendUserPreferenceForDataDuringVoiceCall(slotId, result.mToken,
                            result.mStatus);
                    break;
                }

                case EVENT_ON_DDS_SWITCH_CAPABILITY_CHANGE: {
                    int slotId = msg.arg1;
                    Result result = (Result) msg.obj;
                    onDdsSwitchCapabilityChange(slotId, result.mToken, result.mStatus,
                            (boolean) result.mData);
                    break;
                }

                case EVENT_ON_AUTO_DDS_SWITCH_CHANGE: {
                    int slotId = msg.arg1;
                    Result result = (Result) msg.obj;
                    onDdsSwitchCriteriaChange(slotId, result.mToken,
                            (boolean) result.mData);
                    break;
                }

                case EVENT_ON_DDS_SWITCH_RECOMMENDATION: {
                    int slotId = msg.arg1;
                    Result result = (Result) msg.obj;
                    onDdsSwitchRecommendation(slotId, result.mToken,
                            (int) result.mData);
                    break;
                }
            }
        }
    }

    class Result {
        Token mToken;
        Status mStatus;
        Object mData;

        public Result(Token mToken, Status mStatus, Object mData) {
            this.mToken = mToken;
            this.mStatus = mStatus;
            this.mData = mData;
        }

        @Override
        public String toString() {
            return "Result{" + "mToken=" + mToken + ", mStatus=" + mStatus + ", mData=" + mData +
                    '}';
        }
    }

    class Transaction {
        Token mToken;
        String mName;
        Client mClient;

        public Transaction(Token token, String name, Client client) {
            mToken = token;
            mName = name;
            mClient = client;
        }

        @Override
        public String toString() {
            return "Transaction{" + "mToken=" + mToken + ", mName='" + mName + '\'' + ", mClient="
                    + mClient + '}';
        }
    }

    private Token getNextToken() {
        return new Token(++mSerial);
    }

    IQtiRadioConnectionCallback mQtiRadioCallback = new IQtiRadioConnectionCallback() {
        @Override
        public void onNrIconType(int slotId, Token token, Status status,
                                 NrIconType nrIconType) {
            Log.d(TAG, "onNrIconType slotId = " + slotId + " NrIconType = " + nrIconType);
            mWorkerThreadHandler.sendMessage(mWorkerThreadHandler.obtainMessage
                    (EVENT_ON_NR_ICON_TYPE, slotId, -1, new Result(token, status,
                            nrIconType)));
        }

        @Override
        public void onEnableEndc(int slotId, Token token, Status status) {
            Log.d(TAG, "onEnableEndc slotId = " + slotId + " token = " + token + " status = " +
                    status);

            mWorkerThreadHandler.sendMessage(mWorkerThreadHandler.obtainMessage
                    (EVENT_ON_ENABLE_ENDC, slotId, -1, new Result(token, status, null)));
        }

        @Override
        public void onEndcStatus(int slotId, Token token, Status status, boolean enableStatus) {
            Log.d(TAG, "onEndcStatus slotId = " + slotId + " token = " + token + " status = " +
                    status + " enable = " + enableStatus);

            mWorkerThreadHandler.sendMessage(mWorkerThreadHandler.obtainMessage
                    (EVENT_ON_ENDC_STATUS, slotId, -1, new Result(token, status,
                            enableStatus)));
        }

        @Override
        public void onSetNrConfig(int slotId, Token token, Status status) {
            Log.d(TAG,"setNrConfigStatus: slotId = " + slotId + " token = " + token + " status= " +
                    status);
            mWorkerThreadHandler.sendMessage(mWorkerThreadHandler.obtainMessage
                    (EVENT_ON_SET_NR_CONFIG, slotId, -1, new Result(token, status, null)));
        }

        @Override
        public void onNrConfigStatus(int slotId, Token token, Status status, NrConfig nrConfig) {
            Log.d(TAG, "onNrConfigStatus: slotId = " + slotId + " token = " + token + " status= " +
                    status + " nrConfig = " + nrConfig);
            mWorkerThreadHandler.sendMessage(mWorkerThreadHandler.obtainMessage
                    (EVENT_ON_NR_CONFIG_STATUS, slotId, -1, new Result(token, status, nrConfig)));
        }

        @Override
        public void setCarrierInfoForImsiEncryptionResponse(int slotId,
                Token token, Status status, QRadioResponseInfo info) {
            Log.d(TAG, "setCarrierInfoForImsiEncryptionResponse: slotId = " + slotId +
                    " info = " + info);
            mWorkerThreadHandler.sendMessage(mWorkerThreadHandler.obtainMessage
                    (EVENT_SEND_CARRIER_INFO_RESPONSE, slotId, -1,
                            new Result(token, status, info)));
        }

        @Override
        public void on5gStatus(int slotId, Token token, Status status, boolean enableStatus) {
            Log.d(TAG, "on5gStatus slotId = " + slotId + " token = " + token + " status = " +
                    status + " enableStatus = " + enableStatus);
            mWorkerThreadHandler.sendMessage(mWorkerThreadHandler.obtainMessage
                    (EVENT_ON_5G_ENABLE_STATUS_CHANGE_IND, slotId, -1, new Result(token, status,
                            enableStatus)));
        }

        @Override
        public void onAnyNrBearerAllocation(int slotId, Token token, Status status,
                                            BearerAllocationStatus bearerStatus) {
            Log.d(TAG, "onAnyNrBearerAllocation slotId = " + slotId +
                    " bearerStatus = " + bearerStatus);
            mWorkerThreadHandler.sendMessage(mWorkerThreadHandler.obtainMessage
                    (EVENT_ON_BEARER_ALLOCATION_CHANGE_IND, slotId, -1, new Result(token, status,
                            bearerStatus)));
        }

        @Override
        public void onNrDcParam(int slotId, Token token, Status status,
                                            DcParam dcParam) {
            Log.d(TAG, "onNrDcParam slotId = " + slotId +
                    " dcParam = " + dcParam);
            mWorkerThreadHandler.sendMessage(mWorkerThreadHandler.obtainMessage
                    (EVENT_ON_NR_DC_PARAM, slotId, -1, new Result(token, status,
                            dcParam)));
        }

        @Override
        public void onUpperLayerIndInfo(int slotId, Token token, Status status,
                                            UpperLayerIndInfo upperLayerInfo) {
            Log.d(TAG, "onUpperLayerIndInfo slotId = " + slotId +
                    " upperLayerInfo = " + upperLayerInfo);
            mWorkerThreadHandler.sendMessage(mWorkerThreadHandler.obtainMessage
                    (EVENT_ON_UPPER_LAYER_IND_INFO, slotId, -1, new Result(token, status,
                            upperLayerInfo)));
        }

        @Override
        public void on5gConfigInfo(int slotId, Token token, Status status,
                                            NrConfigType nrConfigType) {
            Log.d(TAG, "on5gConfigInfo slotId = " + slotId +
                    " nrConfigType = " + nrConfigType);
            mWorkerThreadHandler.sendMessage(mWorkerThreadHandler.obtainMessage
                    (EVENT_ON_5G_CONFIG_INFO, slotId, -1, new Result(token, status,
                            nrConfigType)));
        }

        @Override
        public void onSignalStrength(int slotId, Token token, Status status,
                                            SignalStrength signalStrength) {
            Log.d(TAG, "onSignalStrength slotId = " + slotId +
                    " signalStrength = " + signalStrength);
            mWorkerThreadHandler.sendMessage(mWorkerThreadHandler.obtainMessage
                    (EVENT_ON_SIGNAL_STRENGTH, slotId, -1, new Result(token, status,
                            signalStrength)));
        }

        @Override
        public void getQtiRadioCapabilityResponse(int slotId, Token token, Status status,
                                     int raf) {
            Log.d(TAG, "getQtiRadioCapabilityResponse slotId = " + slotId +
                    " raf = " + raf);
            mWorkerThreadHandler.sendMessage(mWorkerThreadHandler.obtainMessage
                    (EVENT_QTI_RADIO_CAPABILITY_RESPONSE, slotId, -1, new Result(token, status,
                            raf)));
        }

        @Override
        public void sendCdmaSmsResponse(int slotId, Token token, Status status, SmsResult sms) {
            Log.d(TAG, "sendCdmaSmsResponse slotId = " + slotId +
                    " status = " + status);
            mWorkerThreadHandler.sendMessage(mWorkerThreadHandler.obtainMessage
                    (EVENT_SEND_CDMA_SMS_RESPONSE, slotId, -1, new Result(token, status, sms)));
        }

        @Override
        public void getCallForwardStatusResponse(int slotId, Token token, Status status,
                QtiCallForwardInfo[] callForwardInfoList) {
            Log.d(TAG, "getCallForwardStatusResponse slotId = " + slotId +
                    " status = " + status);
            mWorkerThreadHandler.sendMessage(mWorkerThreadHandler.obtainMessage
                    (EVENT_CALL_FORWARD_QUERY_RESPONSE, slotId, -1, new Result(token, status,
                    callForwardInfoList)));
        }

        @Override
        public void getFacilityLockForAppResponse(int slotId, Token token, Status status,
                int[] result) {
            Log.d(TAG, "getFacilityLockForAppResponse slotId = " + slotId +
                    " status = " + status);
            mWorkerThreadHandler.sendMessage(mWorkerThreadHandler.obtainMessage
                    (EVENT_FACILITY_LOCK_QUERY_RESPONSE, slotId, -1,
                    new Result(token, status, result)));
        }

        @Override
        public void getImeiResponse(int slotId, Token token, Status status, QtiImeiInfo imeiInfo) {
            Log.d(TAG, "getImeiResponse slotId = " + slotId +
                    " status = " + status + " imeiInfo = " + imeiInfo);
            mWorkerThreadHandler.sendMessage(mWorkerThreadHandler.obtainMessage
                    (EVENT_GET_IMEI_RESPONSE, slotId, -1,
                    new Result(token, status, imeiInfo)));
        }

        @Override
        public void onImeiChange(int slotId, QtiImeiInfo imeiInfo) {
            Log.d(TAG, "onImeiChange slotId = " + slotId +
                    " imeiInfo = " + imeiInfo);
            mWorkerThreadHandler.sendMessage(mWorkerThreadHandler.obtainMessage
                    (EVENT_IMEI_CHANGE_IND_INFO, slotId, -1,
                    new Result(null, null, imeiInfo)));
        }

        @Override
        public void onSendUserPreferenceForDataDuringVoiceCall(int slotId, Token token,
                Status status) {
            mWorkerThreadHandler.sendMessage(mWorkerThreadHandler.obtainMessage
                    (EVENT_ON_ALLOW_MODEM_RECOMMENDATION_FOR_DATA_DURING_CALL,
                    slotId, -1, new Result(token, status, null)));
        }

        @Override
        public void onDdsSwitchCapabilityChange(int slotId, Token token,
                Status status, boolean support) {
            mWorkerThreadHandler.sendMessage(mWorkerThreadHandler.obtainMessage
                    (EVENT_ON_DDS_SWITCH_CAPABILITY_CHANGE, slotId, -1,
                    new Result(token, status, support)));
        }

        @Override
        public void onDdsSwitchCriteriaChange(int slotId, Token token, boolean telephonyDdsSwitch) {
            mWorkerThreadHandler.sendMessage(mWorkerThreadHandler.obtainMessage
                    (EVENT_ON_AUTO_DDS_SWITCH_CHANGE, slotId, -1,
                    new Result(token, null, telephonyDdsSwitch)));
        }

        @Override
        public void onDdsSwitchRecommendation(int slotId, Token token, int recommendedSlotId) {
            mWorkerThreadHandler.sendMessage(mWorkerThreadHandler.obtainMessage
                    (EVENT_ON_DDS_SWITCH_RECOMMENDATION, slotId, -1,
                    new Result(token, null, recommendedSlotId)));
        }

    };

    public QtiRadioProxy(Context context) {
        mContext = context;
        if (sSharedPref == null) {
            Context directBootContext = mContext.getApplicationContext()
                    .createDeviceProtectedStorageContext();
            sSharedPref = PreferenceManager.getDefaultSharedPreferences(directBootContext);
        }
        mQtiRadio = QtiRadioFactory.makeQtiRadio(mContext);
        IntStream.range(0, mQtiRadio.length).forEach(i -> registerCallback(i));
        mWorkerThread.start();
        setLooper(mWorkerThread.getLooper());
        callDynamicDdsSwitchOnBoot();
    }

    private void setLooper(Looper workerLooper) {
        mWorkerThreadHandler = new QtiRadioProxy.WorkerHandler(workerLooper);
    }

    private void registerCallback(int slotId) {
        mQtiRadio[slotId].registerCallback(mQtiRadioCallback);
    }

    public int getPropertyValueInt(String property, int def) throws RemoteException {
        return mQtiRadio[DEFAULT_PHONE_INDEX].getPropertyValueInt(property, def);
    }

    public boolean getPropertyValueBool(String property, boolean def) throws RemoteException {
        return mQtiRadio[DEFAULT_PHONE_INDEX].getPropertyValueBool(property, def);
    }

    public String getPropertyValueString(String property, String def) throws RemoteException {
        return mQtiRadio[DEFAULT_PHONE_INDEX].getPropertyValueString(property, def);
    }

    public Token queryNrIconType(int slotId, Client client) throws RemoteException {
        Log.d(TAG, "queryNrIconType: slotId = " + slotId);
        if (!isClientValid(client)) return null;
        Token token = getNextToken();
        mInflightRequests.put(token.get(), new Transaction(token, "queryNrIconType",
                client));
        mQtiRadio[slotId].queryNrIconType(token);
        return token;
    }

    public Token enableEndc(int slotId, boolean enabled, Client client) throws RemoteException {
        Log.d(TAG, "enableEndc: slotId = " + slotId);
        if (!isClientValid(client)) return null;
        Token token = getNextToken();
        mInflightRequests.put(token.get(), new Transaction(token, "enableEndc",
                client));
        mQtiRadio[slotId].enableEndc(enabled, token);
        return token;
    }

    public Token queryEndcStatus(int slotId, Client client) throws RemoteException {
        Log.d(TAG, "queryEndcStatus: slotId = " + slotId);
        if (!isClientValid(client)) return null;
        Token token = getNextToken();
        mInflightRequests.put(token.get(), new Transaction(token, "queryEndcStatus",
                client));
        mQtiRadio[slotId].queryEndcStatus(token);
        return token;
    }

    public Token setNrConfig(int slotId, NrConfig config, Client client) throws RemoteException {
        int uid = Binder.getCallingUid();
        String packageName = mContext.getPackageManager().getNameForUid(uid);
        Log.d(TAG, "setNrConfig: slotId = " + slotId  + " config = " + config
                + " uid = " + uid + " package=" + packageName);
        if (!isClientValid(client)) return null;
        Token token = getNextToken();
        mInflightRequests.put(token.get(), new Transaction(token, "setNrConfig", client));
        mQtiRadio[slotId].setNrConfig(config, token);
        return token;
    }

    public Token queryNrConfig(int slotId, Client client) throws RemoteException {
        Log.d(TAG, "queryNrConfig: slotId = " + slotId);
        if (!isClientValid(client)) return null;
        Token token = getNextToken();
        mInflightRequests.put(token.get(), new Transaction(token, "queryNrConfig", client));
        mQtiRadio[slotId].queryNrConfig(token);
        return token;
    }

    public Token enable5g(int slotId, Client client) throws RemoteException {
        Log.d(TAG, "enable5g: slotId = " + slotId);
        if (!isClientValid(client)) return null;
        Token token = getNextToken();
        mInflightRequests.put(token.get(), new Transaction(token, "enable5g", client));
        mQtiRadio[slotId].enable5g(token);
        return token;
    }

    public Token disable5g(int slotId, Client client) throws RemoteException {
        Log.d(TAG, "disable5g: slotId = " + slotId);
        if (!isClientValid(client)) return null;
        Token token = getNextToken();
        mInflightRequests.put(token.get(), new Transaction(token, "disable5g", client));
        mQtiRadio[slotId].disable5g(token);
        return token;
    }

    public Token queryNrBearerAllocation(int slotId, Client client) throws RemoteException {
        Log.d(TAG, "queryNrBearerAllocation: slotId = " + slotId);
        if (!isClientValid(client)) return null;
        Token token = getNextToken();
        mInflightRequests.put(token.get(), new Transaction(token, "queryNrBearerAllocation",
                client));
        mQtiRadio[slotId].queryNrBearerAllocation(token);
        return token;
    }

    public Token enable5gOnly(int slotId, Client client) throws RemoteException {
        Log.d(TAG, "enable5gOnly: slotId = " + slotId);
        if (!isClientValid(client)) return null;
        Token token = getNextToken();
        mInflightRequests.put(token.get(), new Transaction(token, "enable5gOnly",
                client));
        mQtiRadio[slotId].enable5gOnly(token);
        return token;
    }

    public Token query5gStatus(int slotId, Client client) throws RemoteException {
        Log.d(TAG, "query5gStatus: slotId = " + slotId);
        if (!isClientValid(client)) return null;
        Token token = getNextToken();
        mInflightRequests.put(token.get(), new Transaction(token, "query5gStatus",
                client));
        mQtiRadio[slotId].query5gStatus(token);
        return token;
    }

    public Token queryNrDcParam(int slotId, Client client) throws RemoteException {
        Log.d(TAG, "queryNrDcParam: slotId = " + slotId);
        if (!isClientValid(client)) return null;
        Token token = getNextToken();
        mInflightRequests.put(token.get(), new Transaction(token, "queryNrDcParam",
                client));
        mQtiRadio[slotId].queryNrDcParam(token);
        return token;
    }

    public Token queryNrSignalStrength(int slotId, Client client) throws RemoteException {
        Log.d(TAG, "queryNrSignalStrength: slotId = " + slotId);
        if (!isClientValid(client)) return null;
        Token token = getNextToken();
        mInflightRequests.put(token.get(), new Transaction(token, "queryNrSignalStrength",
                client));
        mQtiRadio[slotId].queryNrSignalStrength(token);
        return token;
    }

    public Token queryUpperLayerIndInfo(int slotId, Client client) throws RemoteException {
        Log.d(TAG, "queryUpperLayerIndInfo: slotId = " + slotId);
        if (!isClientValid(client)) return null;
        Token token = getNextToken();
        mInflightRequests.put(token.get(), new Transaction(token, "queryUpperLayerIndInfo",
                client));
        mQtiRadio[slotId].queryUpperLayerIndInfo(token);
        return token;
    }

    public Token query5gConfigInfo(int slotId, Client client) throws RemoteException {
        Log.d(TAG, "query5gConfigInfo: slotId = " + slotId);
        if (!isClientValid(client)) return null;
        Token token = getNextToken();
        mInflightRequests.put(token.get(), new Transaction(token, "query5gConfigInfo",
                client));
        mQtiRadio[slotId].query5gConfigInfo(token);
        return token;
    }

    public Token getQtiRadioCapability(int slotId, Client client) throws RemoteException {
        Log.d(TAG, "getQtiRadioCapability: slotId = " + slotId);
        if (!isClientValid(client)) return null;
        Token token = getNextToken();
        mInflightRequests.put(token.get(), new Transaction(token, "getQtiRadioCapability",
                client));
        mQtiRadio[slotId].getQtiRadioCapability(token);
        return token;
    }

    public Token sendCdmaSms(int slotId, byte[] pdu,
            boolean expectMore,  Client client) throws RemoteException {
        Log.d(TAG, "sendCdmaSms: slotId = " + slotId);
        if (!isClientValid(client)) return null;
        Token token = getNextToken();
        mInflightRequests.put(token.get(), new Transaction(token, "sendCdmaSms",
                client));
        mQtiRadio[slotId].sendCdmaSms(pdu, expectMore, token);
        return token;
    }

    public Token setCarrierInfoForImsiEncryption(int slotId, ImsiEncryptionInfo info,
                Client client) throws RemoteException {
        Log.d(TAG, "setCarrierInfoForImsiEncryption: slotId = " + slotId);
        if (!isClientValid(client)) return null;
        Token token = getNextToken();
        mInflightRequests.put(token.get(), new Transaction(token, "setCarrierInfoForImsiEncryption",
                client));
        mQtiRadio[slotId].setCarrierInfoForImsiEncryption(token, info);
        return token;
    }

    public void queryCallForwardStatus(int slotId, int cfReason, int serviceClass,
            String number, boolean expectMore, Client client) throws RemoteException {
        Log.d(TAG, "queryCallForwardStatus: slotId = " + slotId);
        if (!isClientValid(client)) return;
        Token token = getNextToken();
        mInflightRequests.put(token.get(), new Transaction(token, "queryCallForwardStatus",
                client));
        mQtiRadio[slotId].queryCallForwardStatus(token, cfReason, serviceClass, number,
                expectMore);
    }

    public void getFacilityLockForApp(int slotId, String facility, String password,
            int serviceClass, String appId, boolean expectMore, Client client)
            throws RemoteException {
        Log.d(TAG, "getFacilityLockForApp: slotId = " + slotId);
        if (!isClientValid(client)) return;
        Token token = getNextToken();
        mInflightRequests.put(token.get(), new Transaction(token, "getFacilityLockForApp",
                client));
        mQtiRadio[slotId].getFacilityLockForApp(token, facility, password, serviceClass,
                appId, expectMore);
    }

    public Token getImei(int slotId, Client client) throws RemoteException {
        Log.d(TAG, "getImei: slotId = " + slotId);
        Token token = getNextToken();
        mInflightRequests.put(token.get(), new Transaction(token, "getImei",
                client));
        mQtiRadio[slotId].getImei(token);
        return token;
    }

    public boolean isSmartDdsSwitchFeatureAvailable() {
        IFactory cneFactory = null;
        try {
            Log.d(TAG, "Call IFactory getService");
            cneFactory = IFactory.getService();
        } catch (RemoteException | NoSuchElementException e) {
            Log.e(TAG, "CnE factory not supported: " + e);
            return false;
        }
        if (cneFactory == null) {
            Log.e(TAG, "CnE IFactory.getService() returned null");
            return false;
        }
        return true;
    }

    public void setSmartDdsSwitchToggle(boolean isEnabled, Client client) throws RemoteException {
        Log.d(TAG, "setSmartDdsSwitchToggle: isEnabled = " + isEnabled);
        if (!isClientValid(client)) return;
        Token token = getNextToken();
        mInflightRequests.put(token.get(), new Transaction(token, "setSmartDdsSwitchToggle",
                client));
        mWorkerThreadHandler.sendMessage(mWorkerThreadHandler.obtainMessage(
                EVENT_SMART_DDS_SWITCH_TOGGLE, new Result(token, null, isEnabled)));
    }

    public boolean isFeatureSupported(int feature) {
        return mQtiRadio[DEFAULT_PHONE_INDEX].isFeatureSupported(feature);
    }

    public Token sendUserPreferenceForDataDuringVoiceCall(int slotId,
            boolean userPreference, Client client) throws RemoteException {
        Log.d(TAG, "sendUserPreferenceForDataDuringVoiceCall: slotId = " + slotId);
        if (!isClientValid(client)) return null;
        Token token = getNextToken();
        mInflightRequests.put(token.get(), new Transaction(token,
                "sendUserPreferenceForDataDuringVoiceCall", client));
        mQtiRadio[slotId].sendUserPreferenceForDataDuringVoiceCall(token,
                userPreference);
        return token;
    }

    public Token getDdsSwitchCapability(int slotId, Client client) throws RemoteException {
        Log.d(TAG, "getDdsSwitchCapability: slotId = " + slotId);
        if (!isClientValid(client)) return null;
        Token token = getNextToken();
        mInflightRequests.put(token.get(), new Transaction(token, "getDdsSwitchCapability",
                client));
        mQtiRadio[slotId].getDdsSwitchCapability(token);
        return token;
    }

    private IHwBinder.DeathRecipient mDeathRecipient = new IHwBinder.DeathRecipient() {
        @Override
        public void serviceDied(long cookie) {
            Log.d(TAG, "CnE HAL is down");
            sDynamicSubscriptionManager = null;
        }
    };

    private void callDynamicDdsSwitchOnBoot() {
        boolean savedSmartDdsSwitchValue =
                sSharedPref.getBoolean(SMART_DDS_SWITCH_TOGGLE_VALUE, false);
        Log.d(TAG, "savedSmartDdsSwitchValue: " + savedSmartDdsSwitchValue);
        Token token = getNextToken();
        mWorkerThreadHandler.sendMessage(mWorkerThreadHandler.obtainMessage(
                EVENT_SMART_DDS_SWITCH_TOGGLE, new Result(token, null, savedSmartDdsSwitchValue)));
    }

    private void setDynamicSubscriptionChange(Token token, boolean isEnabled) {
        int status = StatusCode.FAILED;
        if (sDynamicSubscriptionManager == null) {
            sDynamicSubscriptionManager = getDynamicSubscriptionManager();
            if (sDynamicSubscriptionManager == null) {
                Log.e(TAG, "getDynamicSubscriptionManager() returned null");
                return;
            }
        }
        try {
            status = sDynamicSubscriptionManager.setDynamicSubscriptionChange(isEnabled);
        } catch (RemoteException | NullPointerException e) {
            Log.e(TAG, "setDynamicSubscriptionChange exception: " + e);
        } finally {
            int toggleValue = isEnabled ? SMART_DDS_SWITCH_ON : SMART_DDS_SWITCH_OFF;
            mWorkerThreadHandler.sendMessage(mWorkerThreadHandler.obtainMessage(
                    EVENT_ON_SMART_DDS_SWITCH_TOGGLE_RESPONSE, status, toggleValue, token));
        }
    }

    private ISubscriptionManager getDynamicSubscriptionManager() {
        final CbResults results = new CbResults();
        IFactory cneFactory = null;

        try {
            Log.d(TAG, "Call IFactory getService");
            cneFactory = IFactory.getService();
        } catch (RemoteException | NoSuchElementException e) {
            Log.e(TAG, "CnE factory not supported: " + e);
        }
        if (cneFactory == null) {
            Log.e(TAG, "CnE IFactory.getService() returned null");
            return null;
        }

        try {
            Log.d(TAG, "Call createDynamicddsISubscriptionManager");
            cneFactory.createDynamicddsISubscriptionManager(new IToken.Stub() {},
                (int status, ISubscriptionManager service) -> {
                    results.status = status;
                    results.service = service;
                });
            Log.d(TAG, "createDynamicddsISubscriptionManager success");
            if (!results.service.linkToDeath(mDeathRecipient, mDeathBinderCookie)) {
                Log.e(TAG, "Failed to link to death recipient");
            }
            return results.service;
        } catch (RemoteException e) {
            Log.e(TAG, "createDynamicddsISubscriptionManager exception: " + e);
        }

        return null;
    }

    private static class CbResults {
        int status;
        ISubscriptionManager service;
    }

    private void updateSharedPrefs(boolean isEnabled) {
        Log.d(TAG, "Update the shared preference");
        SharedPreferences.Editor editor = sSharedPref.edit();
        editor.putBoolean(SMART_DDS_SWITCH_TOGGLE_VALUE, isEnabled);
        editor.apply();
    }

    private void setSmartDdsSwitchToggleResponse(Token token, int status, int toggleValue) {
        Log.d(TAG, "setSmartDdsSwitchToggleResponse status = " + status);
        boolean result = status == StatusCode.OK;
        if (result) {
            boolean isEnabled = toggleValue == SMART_DDS_SWITCH_ON;
            updateSharedPrefs(isEnabled);
        }
        try {
            int tokenKey = token.get();
            synchronized (mCallbackList) {
                for (IExtPhoneCallback callback : retrieveCallbacks(tokenKey)) {
                    Log.d(TAG, "setSmartDdsSwitchToggleResponse: Responding back for transaction = "
                            + mInflightRequests.get(tokenKey));
                    callback.setSmartDdsSwitchToggleResponse(token, result);
                    mInflightRequests.remove(tokenKey);
                }
            }
        } catch (RemoteException e) {
            Log.d(TAG, "setSmartDdsSwitchToggleResponse: Exception = " + e);
        }
    }

    private boolean addCallback(IExtPhoneCallback callback) {
        IBinder binder = callback.asBinder();
        synchronized (mCallbackList) {
            for (IExtPhoneCallback it : mCallbackList) {
                if (it.asBinder().equals(binder)) {
                    // Found an existing callback with same binder.
                    Log.e(TAG, "Found an existing callback with same binder. " + callback);
                    return FAILED;
                }
            }
            Log.d(TAG, "add callback= " + callback);
            mCallbackList.add(callback);
        }
        return SUCCESS;
    }

    private void removeCallback(IExtPhoneCallback callback) {
        IBinder binder = callback.asBinder();
        Log.d(TAG, "removeCallback: callback= " + callback + ", Binder = " + binder);
        synchronized (mCallbackList) {
            for (IExtPhoneCallback it : mCallbackList) {
                if (it.asBinder().equals(binder)) {
                    Log.d(TAG, "remove callback= " + it);
                    mCallbackList.remove(it);
                    return;
                }
            }
        }
    }

    private void removeClientFromInflightRequests(IExtPhoneCallback callback) {
        for(int key : mInflightRequests.keySet()) {
            Transaction txn = mInflightRequests.get(key);
            if (txn.mClient.getCallback().asBinder() == callback.asBinder()) {
                Log.d(TAG, "removeClientFromInflightRequests: Token = " + key + " => " +
                        mInflightRequests.get(key));
                mInflightRequests.remove(key);
            }
        }
    }

    private boolean isClientValid(Client client) {
        if (client == null || client.getCallback() == null) {
            Log.e(TAG, "Invalid client");
            return false;
        }
        synchronized (mCallbackList) {
            for (IExtPhoneCallback it : mCallbackList) {
                if (it.asBinder().equals(client.getCallback().asBinder())) {
                    return true;
                }
            }
        }
        Log.d(TAG, "This client is unregistered: " + client);
        return false;
    }

    public Client registerCallback(String packageName, IExtPhoneCallback callback) throws
            RemoteException {
        Client client = null;
        IBinder binder = callback.asBinder();

        binder.linkToDeath(new ClientBinderDeathRecipient(callback), 0);

        int uid = Binder.getCallingUid();
        String callerPackageName = mContext.getPackageManager().getNameForUid(uid);
        Log.d(TAG, "registerCallback: uid = " + uid + " callerPackage=" + callerPackageName +
                "callback = " + callback + "binder = " + binder);

        if (addCallback(callback) == SUCCESS) {
            client = new Client(++mClientIndex, uid, packageName, callback);
            Log.d(TAG, "registerCallback: client = " + client);

        } else {
            Log.d(TAG, "registerCallback: callback could not be added.");
        }
        return client;
    }

    public void unRegisterCallback(IExtPhoneCallback callback) throws RemoteException {
        removeCallback(callback);
        removeClientFromInflightRequests(callback);
    }

    class ClientBinderDeathRecipient implements IBinder.DeathRecipient {
        IExtPhoneCallback mCallback;

        public ClientBinderDeathRecipient(IExtPhoneCallback callback) {
            Log.d(TAG, "registering for client cb = " + callback + " binder = "
                    + callback.asBinder() + " death " + "notification");
            mCallback = callback;
        }

        @Override
        public void binderDied() {
            Log.d(TAG, "Client callback = " + mCallback +" binder = " + mCallback.asBinder() +
                    "died");

            IBinder binder = mCallback.asBinder();
            binder.unlinkToDeath(this, 0);

            try {
                unRegisterCallback(mCallback);
            } catch (RemoteException e) {
                Log.d(TAG, "Exception while unregistering callback = " + mCallback + " binder = "
                        + mCallback.asBinder());
            }
        }
    }

    ArrayList<IExtPhoneCallback> retrieveCallbacks(int tokenKey) {
        ArrayList<IExtPhoneCallback> list = new ArrayList<IExtPhoneCallback>();
        if (tokenKey != Token.UNSOL) {
            if (mInflightRequests.containsKey(tokenKey)) {
                Transaction txn = mInflightRequests.get(tokenKey);
                Client client = txn.mClient;
                if (isClientValid(client)) {
                    list.add(client.getCallback());
                } else {
                    Log.e(TAG, "This client is invalid now: " + client);
                }
            }
        } else {
            list = mCallbackList;
        }

        return list;
    }

    /* Private delegates */
    private void onNrIconType(int slotId, Token token, Status status,
                              NrIconType nrIconType) {
        try {
            int tokenKey = token.get();
            synchronized (mCallbackList) {
                for (IExtPhoneCallback callback : retrieveCallbacks(tokenKey)) {
                    Log.d(TAG, "onNrIconType: Responding back for transaction = " +
                            mInflightRequests.get(tokenKey));
                    callback.onNrIconType(slotId, token, status, nrIconType);
                    mInflightRequests.remove(tokenKey);
                }
            }
        } catch (RemoteException e) {
            Log.d(TAG, "onNrIconType: Exception = " + e);
        }
    }

    private void onEnableEndc(int slotId, Token token, Status status) {
        try {
            int tokenKey = token.get();
            synchronized (mCallbackList) {
                for (IExtPhoneCallback callback : retrieveCallbacks(tokenKey)) {
                    Log.d(TAG, "onEnableEndc: Responding back for transaction = " +
                            mInflightRequests.get(tokenKey));
                    callback.onEnableEndc(slotId, token, status);
                    mInflightRequests.remove(tokenKey);
                }
            }
        } catch (RemoteException e) {
            Log.d(TAG, "onEnableEndc: Exception = " + e);
        }
    }

    private void onEndcStatus(int slotId, Token token, Status status, boolean enableStatus) {
        try {
            int tokenKey = token.get();
            synchronized (mCallbackList) {
                for (IExtPhoneCallback callback : retrieveCallbacks(tokenKey)) {
                    Log.d(TAG, "onEndcStatus: Responding back for transaction = " +
                            mInflightRequests.get(tokenKey));
                    callback.onEndcStatus(slotId, token, status, enableStatus);
                    mInflightRequests.remove(tokenKey);
                }
            }
        } catch (RemoteException e) {
            Log.d(TAG, "onEndcStatus: Exception = " + e);
        }
    }

    public void onSetNrConfig(int slotId, Token token, Status status) {
        try {
            int tokenKey = token.get();
            synchronized (mCallbackList) {
                for (IExtPhoneCallback callback : retrieveCallbacks(tokenKey)) {
                    Log.d(TAG, "onSetNrConfig: Responding back for transaction = " +
                            mInflightRequests.get(tokenKey));
                    callback.onSetNrConfig(slotId, token, status);
                    mInflightRequests.remove(tokenKey);
                }
            }
        } catch (RemoteException e) {
            Log.d(TAG, "onSetNrConfig: Exception = " + e);
        }
    }

    public void onNrConfigStatus(int slotId, Token token, Status status, NrConfig nrConfig) {
        try {
            int tokenKey = token.get();
            synchronized (mCallbackList) {
                for (IExtPhoneCallback callback : retrieveCallbacks(tokenKey)) {
                    Log.d(TAG, "onNrConfigStatus: Responding back for transaction = " +
                            mInflightRequests.get(tokenKey));
                    callback.onNrConfigStatus(slotId, token, status, nrConfig);
                    mInflightRequests.remove(tokenKey);
                }
            }
        } catch (RemoteException e) {
            Log.d(TAG, "onNrConfigStatus: Exception = " + e);
        }
    }

    private void on5gStatus(int slotId, Token token, Status status, boolean enableStatus) {
        try {
            int tokenKey = token.get();
            synchronized (mCallbackList) {
                for (IExtPhoneCallback callback : retrieveCallbacks(tokenKey)) {
                    Log.d(TAG, "on5gStatus: Responding back for transaction = " +
                            mInflightRequests.get(tokenKey));
                    callback.on5gStatus(slotId, token, status, enableStatus);
                    mInflightRequests.remove(tokenKey);
                }
            }
        } catch (RemoteException e) {
            Log.d(TAG, "on5gStatus: Exception = " + e);
        }
    }

    private void onAnyNrBearerAllocation(int slotId, Token token, Status status,
                                         BearerAllocationStatus bearerStatus) {
        try {
            int tokenKey = token.get();
            synchronized (mCallbackList) {
                for (IExtPhoneCallback callback : retrieveCallbacks(tokenKey)) {
                    Log.d(TAG, "onAnyNrBearerAllocation: Responding back for transaction = " +
                            mInflightRequests.get(tokenKey));
                    callback.onAnyNrBearerAllocation(slotId, token, status, bearerStatus);
                    mInflightRequests.remove(tokenKey);
                }
            }
        } catch (RemoteException e) {
            Log.d(TAG, "onAnyNrBearerAllocation: Exception = " + e);
        }
    }

    private void onNrDcParam(int slotId, Token token, Status status,
                                        DcParam dcParam) {
        try {
            int tokenKey = token.get();
            synchronized (mCallbackList) {
                for (IExtPhoneCallback callback : retrieveCallbacks(tokenKey)) {
                    Log.d(TAG, "onNrDcParam: Responding back for transaction = " +
                            mInflightRequests.get(tokenKey));
                    callback.onNrDcParam(slotId, token, status, dcParam);
                    mInflightRequests.remove(tokenKey);
                }
            }
        } catch (RemoteException e) {
            Log.d(TAG, "onNrDcParam: Exception = " + e);
        }
    }

    private void onUpperLayerIndInfo(int slotId, Token token, Status status,
                                        UpperLayerIndInfo upperLayerInfo) {
        try {
            int tokenKey = token.get();
            synchronized (mCallbackList) {
                for (IExtPhoneCallback callback : retrieveCallbacks(tokenKey)) {
                    Log.d(TAG, "onUpperLayerIndInfo: Responding back for transaction = " +
                            mInflightRequests.get(tokenKey));
                    callback.onUpperLayerIndInfo(slotId, token, status, upperLayerInfo);
                    mInflightRequests.remove(tokenKey);
                }
            }
        } catch (RemoteException e) {
            Log.d(TAG, "onUpperLayerIndInfo: Exception = " + e);
        }
    }

    private void on5gConfigInfo(int slotId, Token token, Status status,
                                        NrConfigType nrConfigType) {
        try {
            int tokenKey = token.get();
            synchronized (mCallbackList) {
                for (IExtPhoneCallback callback : retrieveCallbacks(tokenKey)) {
                    Log.d(TAG, "on5gConfigInfo: Responding back for transaction = " +
                            mInflightRequests.get(tokenKey));
                    callback.on5gConfigInfo(slotId, token, status, nrConfigType);
                    mInflightRequests.remove(tokenKey);
                }
            }
        } catch (RemoteException e) {
            Log.d(TAG, "on5gConfigInfo: Exception = " + e);
        }
    }

    private void onSignalStrength(int slotId, Token token, Status status,
                                        SignalStrength signalStrength) {
        try {
            int tokenKey = token.get();
            synchronized (mCallbackList) {
                for (IExtPhoneCallback callback : retrieveCallbacks(tokenKey)) {
                    Log.d(TAG, "onSignalStrength: Responding back for transaction = " +
                            mInflightRequests.get(tokenKey));
                    callback.onSignalStrength(slotId, token, status, signalStrength);
                    mInflightRequests.remove(tokenKey);
                }
            }
        } catch (RemoteException e) {
            Log.d(TAG, "onSignalStrength: Exception = " + e);
        }
    }

    private void getQtiRadioCapabilityResponse(int slotId, Token token, Status status,
                                int raf) {
        try {
            int tokenKey = token.get();
            synchronized (mCallbackList) {
                for (IExtPhoneCallback callback : retrieveCallbacks(tokenKey)) {
                    Log.d(TAG, "getQtiRadioCapabilityResponse: Responding back" +
                            "for transaction = " + mInflightRequests.get(tokenKey));
                    callback.getQtiRadioCapabilityResponse(slotId, token, status, raf);
                    mInflightRequests.remove(tokenKey);
                }
            }
        } catch (RemoteException e) {
            Log.d(TAG, "getQtiRadioCapabilityResponse: Exception = " + e);
        }
    }

    private void sendCdmaSmsResponse(int slotId, Token token, Status status,
                        SmsResult sms) {
        try {
            int tokenKey = token.get();
            synchronized (mCallbackList) {
                for (IExtPhoneCallback callback : retrieveCallbacks(tokenKey)) {
                    Log.d(TAG, "sendCdmaSmsResponse: Responding back for transaction = " +
                            mInflightRequests.get(tokenKey));
                    callback.sendCdmaSmsResponse(slotId, token, status, sms);
                    mInflightRequests.remove(tokenKey);
                }
            }
        } catch (RemoteException e) {
            Log.d(TAG, "sendCdmaSmsResponse: Exception = " + e);
        }
    }

    private void setCarrierInfoForImsiEncryptionResponse(int slotId, Token token, Status status,
                        QRadioResponseInfo info) {
        try {
            int tokenKey = info.getSerial();
            synchronized (mCallbackList) {
                for (IExtPhoneCallback callback : retrieveCallbacks(tokenKey)) {
                    Log.d(TAG, "setCarrierInfoForImsiEncryptionResponse: Responding back for" +
                            " transaction = " + mInflightRequests.get(tokenKey));
                    callback.setCarrierInfoForImsiEncryptionResponse(slotId, token, info);
                    mInflightRequests.remove(tokenKey);
                }
            }
        } catch (RemoteException e) {
            Log.d(TAG, "setCarrierInfoForImsiEncryptionResponse: Exception = " + e);
        }
    }

    private void sendcallforwardqueryResponse(Token token, Status status, QtiCallForwardInfo[]
            callForwardInfoList) {
        try {
            int tokenKey = token.get();
            synchronized (mCallbackList) {
                for (IExtPhoneCallback callback : retrieveCallbacks(tokenKey)) {
                    Log.d(TAG, "sendcallforwardqueryResponse: Responding back for transaction = " +
                            mInflightRequests.get(tokenKey));
                    callback.queryCallForwardStatusResponse(status, callForwardInfoList);
                    mInflightRequests.remove(tokenKey);
                }
            }
        } catch (RemoteException e) {
            Log.d(TAG, "sendcallforwardqueryResponse: Exception = " + e);
        }
    }

    private void sendfacilityLockResponse(Token token, Status status, int[] result) {
        try {
            int tokenKey = token.get();
            synchronized (mCallbackList) {
                for (IExtPhoneCallback callback : retrieveCallbacks(tokenKey)) {
                    Log.d(TAG, "sendfacilityLockResponse: Responding back for transaction = " +
                            mInflightRequests.get(tokenKey));
                    callback.getFacilityLockForAppResponse(status, result);
                    mInflightRequests.remove(tokenKey);
                }
            }
        } catch (RemoteException e) {
            Log.d(TAG, "sendfacilityLockResponse: Exception = " + e);
        }
    }

    // inform IMEI change indication to registered external clients
    void sendImeiInfoInd(QtiImeiInfo[] imeiInfo) {
        try {
            synchronized (mCallbackList) {
                for (IExtPhoneCallback callback : retrieveCallbacks(Token.UNSOL)) {
                    Log.d(TAG, "sendImeiInfoInd: = " + imeiInfo);
                    callback.onImeiTypeChanged(imeiInfo);
                }
            }
        } catch (RemoteException e) {
            Log.d(TAG, "sendImeiInfoInd: Exception = " + e);
        }
    }

    private void sendImeiInfoResponse(int slotId,
            Token token, Status status, QtiImeiInfo imeiInfo) {
        int tokenKey = token.get();
        synchronized (mInternalCallbackList) {
            for (IQtiRadioInternalCallback callback : mInternalCallbackList) {
                Log.d(TAG, "sendImeiInfoResponse: Responding back for transaction = " +
                        mInflightRequests.get(tokenKey));
                callback.getImeiResponse(slotId, token, status, imeiInfo);
                mInflightRequests.remove(tokenKey);
            }
        }
    }

    private void sendImeiInfoIndInternal(int slotId, QtiImeiInfo imeiInfo) {
        synchronized (mInternalCallbackList) {
            for (IQtiRadioInternalCallback callback : mInternalCallbackList) {
                Log.d(TAG, "sendImeiInfoIndInternal: slotId = " + slotId);
                callback.onImeiChanged(slotId, imeiInfo);
            }
        }
    }

    void registerInternalCallback(IQtiRadioInternalCallback callback) {
        synchronized (mInternalCallbackList) {
            Log.d(TAG, "add internal callback = " + callback);
            mInternalCallbackList.add(callback);
        }
    }

    void unRegisterInternalCallback(IQtiRadioInternalCallback callback) {
        synchronized (mInternalCallbackList) {
            Log.d(TAG, "remove internal callback = " + callback);
            mInternalCallbackList.remove(callback);
        }
    }

    public static class IQtiRadioInternalCallback {

        public void getImeiResponse(int slotId, Token token, Status status, QtiImeiInfo imeiInfo) {
             // do nothing
        }

        public void onImeiChanged(int slotId, QtiImeiInfo imeiInfo) {
             // do nothing
        }
    }

    private void onSendUserPreferenceForDataDuringVoiceCall(int slotId,
            Token token, Status status) {
        int tokenKey = token.get();
        try {
            synchronized (mCallbackList) {
                for (IExtPhoneCallback callback : retrieveCallbacks(tokenKey)) {
                    Log.d(TAG, "onSendUserPreferenceForDataDuringVoiceCall:" +
                            " Responding back for transaction = " +
                            mInflightRequests.get(tokenKey));
                    callback.onSendUserPreferenceForDataDuringVoiceCall(slotId,
                            token, status);
                }
            }
        } catch (RemoteException e) {
            Log.d(TAG, "onSendUserPreferenceForDataDuringVoiceCall: Exception = " + e);
        }
        mInflightRequests.remove(tokenKey);
    }

    private void onDdsSwitchCapabilityChange(int slotId, Token token,
            Status status, boolean support) {
        int tokenKey = token.get();
        try {
            synchronized (mCallbackList) {
                for (IExtPhoneCallback callback : retrieveCallbacks(tokenKey)) {
                    Log.d(TAG, "onDdsSwitchCapabilityChange: " +
                            " Responding back for transaction = " +
                            mInflightRequests.get(tokenKey));
                    callback.onDdsSwitchCapabilityChange(slotId, token, status, support);
                }
            }
        } catch (RemoteException e) {
            Log.d(TAG, "onDdsSwitchCapabilityChange: Exception = " + e);
        }
        mInflightRequests.remove(tokenKey);
    }

    private void onDdsSwitchCriteriaChange(int slotId, Token token, boolean telephonyDdsSwitch) {
        int tokenKey = token.get();
        try {
            synchronized (mCallbackList) {
                for (IExtPhoneCallback callback : retrieveCallbacks(tokenKey)) {
                    Log.d(TAG, "onDdsSwitchCriteriaChange:" +
                            " Responding back for transaction = " +
                            mInflightRequests.get(tokenKey));
                    callback.onDdsSwitchCriteriaChange(slotId, telephonyDdsSwitch);
                }
            }
        } catch (RemoteException e) {
            Log.d(TAG, "onDdsSwitchCriteriaChange: Exception = " + e);
        }
        mInflightRequests.remove(tokenKey);
    }

    private void onDdsSwitchRecommendation(int slotId, Token token, int recommendedSlotId) {
        int tokenKey = token.get();
        try {
            synchronized (mCallbackList) {
                for (IExtPhoneCallback callback : retrieveCallbacks(tokenKey)) {
                    Log.d(TAG, "onDdsSwitchRecommendation:" +
                            " Responding back for transaction = " +
                            mInflightRequests.get(tokenKey));
                    callback.onDdsSwitchRecommendation(slotId, recommendedSlotId);
                }
            }
        } catch (RemoteException e) {
            Log.d(TAG, "onDdsSwitchRecommendation: Exception = " + e);
        }
        mInflightRequests.remove(tokenKey);
    }

    public int getAidlClientsCount() {
        synchronized (mCallbackList) {
            return mCallbackList.size();
        }
    }

    public int getInflightRequestsCount() {
        return mInflightRequests.size();
    }

    private void dumpAidlClients(PrintWriter pw) {
        synchronized (mCallbackList) {
            for (IExtPhoneCallback callback : mCallbackList) {
                IBinder binder = callback.asBinder();
                pw.println("Callback = " + callback + "-> Binder = " + binder);
            }
        }
    }

    private void dumpInflightRequests(PrintWriter pw){
        for(Integer key : mInflightRequests.keySet()) {
            pw.println("Token = " + key + " => " + mInflightRequests.get(key));
        }
    }

    // Dump service.
    public void dump(FileDescriptor fd, PrintWriter printwriter, String[] args) {
        PrintWriter pw = printwriter;
        pw.println("5G-Middleware:");
        pw.println("mQtiRadio = " + mQtiRadio);
        pw.println("AIDL clients : ");
        dumpAidlClients(pw);
        pw.flush();

        pw.println("Inflight requests : ");
        dumpInflightRequests(pw);
        pw.flush();
    }

}
