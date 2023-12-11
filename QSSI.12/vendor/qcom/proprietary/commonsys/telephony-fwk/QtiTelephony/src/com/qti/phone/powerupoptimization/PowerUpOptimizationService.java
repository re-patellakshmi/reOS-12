/*
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */

package com.qti.phone.powerupoptimization;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.ims.ImsMmTelManager;
import android.telephony.ims.feature.ImsFeature;
import android.util.Log;

import com.qti.phone.QtiMsgTunnelClient;
import com.qti.phone.QtiMsgTunnelClient.InternalOemHookCallback;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PowerUpOptimizationService extends Service {
    private static final String TAG = "PowerUpOptService";

    private int mNumPhones;
    private boolean mIsImsSupported;
    private final Object mIsAtelReadySentLock = new Object();
    private Map<Integer, ImsMmTelManager> mImsMmTelManagers;
    private Set<Integer> mAvailableSubs = new HashSet<>();

    private Context mContext;
    private Handler mHandler;
    private QtiMsgTunnelClient mQtiMsgTunnelClient;
    private TelephonyManager mTelephonyManager;
    private SubscriptionManager mSubscriptionManager;

    // Indicates that IMS stack is up
    private boolean[] mIsImsStackUpForSlot;

    // Indicates that connection to IRadio HAL has been established
    private boolean[] mIsRilConnectedForSlot;

    // Indicates that connection to IQtiOemHook HAL has been established
    private boolean mIsOemHookConnected;

    private boolean[] isAtelReadySentForSlot;

    private static final int READY = 1;

    // Timeout for consecutive IMS state query
    private static final int TIMEOUT_MILLIS = 1000;
    // Try checking for IMS stack up for at most 3 minutes
    // from TIMEOUT_MILLIS * MAX_RETRY_COUNT
    private static final int MAX_RETRY_COUNT = 60 * 3 - 1;
    private static final int RETRY_COUNT_INITIALIZER = -1;

    // Events for the handler
    private static final int GET_IMS_STATE = 1;
    private static final int EVENT_IMS_STACK_UP = 2;

    private static final String EXTRA_STATE = "state";
    private static final String ACTION_RADIO_POWER_STATE_CHANGED =
            "org.codeaurora.intent.action.RADIO_POWER_STATE";

    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            String action = intent.getAction();
            log("onReceive: " + action);
            if (ACTION_RADIO_POWER_STATE_CHANGED.equals(action)) {
                int slotIdExtra = intent.getIntExtra(SubscriptionManager.EXTRA_SLOT_INDEX,
                        SubscriptionManager.INVALID_SIM_SLOT_INDEX);
                int radioStateExtra = intent.getIntExtra(EXTRA_STATE,
                        TelephonyManager.RADIO_POWER_UNAVAILABLE);
                handleRadioPowerStateChanged(slotIdExtra, radioStateExtra);

            } else if (TelephonyManager.ACTION_SIM_APPLICATION_STATE_CHANGED.equals(action)
                    || TelephonyManager.ACTION_SIM_CARD_STATE_CHANGED.equals(action)) {
                final int simState = intent.getIntExtra(TelephonyManager.EXTRA_SIM_STATE,
                        TelephonyManager.SIM_STATE_UNKNOWN);
                final int slotId = intent.getIntExtra(SubscriptionManager.EXTRA_SLOT_INDEX,
                        SubscriptionManager.INVALID_SIM_SLOT_INDEX);
                if (isValidSlotIndex(slotId)) {
                    if (simState == TelephonyManager.SIM_STATE_ABSENT
                            || simState == TelephonyManager.SIM_STATE_UNKNOWN
                            || simState == TelephonyManager.SIM_STATE_CARD_RESTRICTED
                            || simState == TelephonyManager.SIM_STATE_CARD_IO_ERROR) {
                        onSimAbsent(slotId);
                    } else if (simState == TelephonyManager.SIM_STATE_LOADED
                            || isSimLocked(simState)) {
                        onSimLoadedOrLocked(slotId);
                    }
                } else {
                    log("invalid slot id: " + slotId);
                }

            } else {
                log("received unknown intent: " + action);
            }
        }
    };

    private boolean isValidSlotIndex(int slotId) {
        return slotId >= 0 && mTelephonyManager != null
                && slotId < mTelephonyManager.getActiveModemCount();
    }

    private boolean isSimLocked(int simState) {
        return simState == TelephonyManager.SIM_STATE_PIN_REQUIRED
                || simState == TelephonyManager.SIM_STATE_PUK_REQUIRED
                || simState == TelephonyManager.SIM_STATE_NETWORK_LOCKED
                || simState == TelephonyManager.SIM_STATE_PERM_DISABLED;
    }

    private final InternalOemHookCallback mOemHookCallback = new InternalOemHookCallback() {
        @Override
        public String getCallBackName() {
            return "PowerUpOptService";
        }

        @Override
        public void onOemHookConnected() {
            log("QcRilHook Service ready");
            mIsOemHookConnected = true;
            trySendPhoneReadyForAllSlots();
        }

        @Override
        public void onOemHookDisconnected() {
            log("QcRilHook Service disconnected");
            mIsOemHookConnected = false;
        }
    };

    private void handleRadioPowerStateChanged(int slotId, int radioState) {
        if (radioState == TelephonyManager.RADIO_POWER_UNAVAILABLE) {
            log("radio is unavailable for slot: " + slotId);
            mIsRilConnectedForSlot[slotId] = false;
        } else {
            log("radio is available for slot: " + slotId);
            mIsRilConnectedForSlot[slotId] = true;
            trySendPhoneReadyForSlot(slotId);
        }
    }

    private void onSimLoadedOrLocked(int slotId) {
        log("SIM is loaded or locked on slot: " + slotId);
        SubscriptionInfo subInfo =
                mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotId);
        if (subInfo != null) {
            log("subInfo: " + subInfo);
            int subId = subInfo.getSubscriptionId();
            if (mAvailableSubs.contains(subId)) {
                log("This sub was handled");
                return;
            }
            mAvailableSubs.add(subId);

            // Fetch radio power state for this subscription
            TelephonyManager telMgrForSub = mTelephonyManager.createForSubscriptionId(subId);
            int radioState = telMgrForSub.getRadioPowerState();
            handleRadioPowerStateChanged(slotId, radioState);

            if (mIsImsSupported) {
                // fetch IMS state for this subscription
                mImsMmTelManagers.put(subId, ImsMmTelManager.createForSubscriptionId(subId));
                getImsState(subId, slotId, RETRY_COUNT_INITIALIZER);
            }
        } else {
            Log.d(TAG, "subInfo is null for slot: " + slotId);
        }
    }

    private void onSimAbsent(int slotId) {
        log("SIM is absent on slot: " + slotId);
        mIsImsStackUpForSlot[slotId] = true;
        mIsRilConnectedForSlot[slotId] = true;
        trySendPhoneReadyForSlot(slotId);
    }

    void checkImsState(int subId, int slotId, int retryCount) {
        try {
            final IntegerConsumer intResult = new IntegerConsumer();

            mImsMmTelManagers.get(subId).getFeatureState(mExecutor, intResult);

            if (intResult.get(TIMEOUT_MILLIS) != ImsFeature.STATE_READY) {
                log("IMS state not ready, calling the method with 1000 ms timeout");
                mIsImsStackUpForSlot[slotId] = false;
                // Fetch IMS state after TIMEOUT_MILLIS
                getImsState(subId, slotId, retryCount);
            } else {
                log("IMS state ready for sub: " + subId);
                mHandler.sendMessage(mHandler.obtainMessage(EVENT_IMS_STACK_UP, slotId));
            }
        } catch (Exception ex) {
            Log.e(TAG,"Exception in checkImsState", ex);
        }
    }

    private void getImsState(int subId, int slotId, int retryCount) {
        if (retryCount > MAX_RETRY_COUNT) {
            log("Reach the max retry time: " + retryCount + " for slot: " + slotId);
            return;
        }
        final int interval = retryCount > 0 ? TIMEOUT_MILLIS : 0;
        ImsStackCheck checker = new ImsStackCheck();
        checker.subId = subId;
        checker.slotId = slotId;
        checker.retryCount = retryCount + 1;
        mHandler.sendMessageDelayed(
                mHandler.obtainMessage(GET_IMS_STATE, checker), interval);

    }

    private void onImsStackReadyForSlot(int slotId) {
        mIsImsStackUpForSlot[slotId] = true;
        log("mIsImsStackUpForSlot: " + Arrays.toString(mIsImsStackUpForSlot));

        // try sending phone state ready for the given slotId
        trySendPhoneReadyForSlot(slotId);
    }

    private void trySendPhoneReadyForAllSlots() {
        for (int slot = 0; slot < mNumPhones; slot++) {
            trySendPhoneReadyForSlot(slot);
        }
    }

    private void trySendPhoneReadyForSlot(int slotId) {
        log("trySendPhoneReady for slot: " + slotId);

        synchronized (mIsAtelReadySentLock) {
            /*
             To send ready to RIL, we need to wait for three events:
             1. RIL is connected
             2. OEM HOOK is ready
             3. IMS stack is ready (if IMS is supported)
             */

            if (mIsOemHookConnected
                    && mIsRilConnectedForSlot[slotId]
                    && mIsImsStackUpForSlot[slotId]
                    && !isAtelReadySentForSlot[slotId]) {

                isAtelReadySentForSlot[slotId] = true;

                Thread powerUpOptServiceThread = new Thread(() -> {
                    log("Sending ATEL Ready to RIL for slot: " + slotId);
                    mQtiMsgTunnelClient.sendAtelReadyStatus(READY, slotId);
                    // TODO: maybe add a retry mechanism if we fail to send the message to RIL.

                    if (shouldStopService()) {
                        log("Phone ready sent for all slots. Stopping service.");
                        stopSelf();
                    }
                });
                powerUpOptServiceThread.start();
            } else {
                log("Not sending ATEL ready: " + dumpStates(slotId));
            }
        }
    }

    private boolean shouldStopService() {
        // We can stop the service when ATEL Ready has been sent for all the slots
        for (int slot = 0; slot < mNumPhones; slot++) {
            if (!isAtelReadySentForSlot[slot]) {
                return false;
            }
        }
        return true;
    }

    private String dumpStates(int slot) {
        return "States: [" + slot +
                ": {" + mIsOemHookConnected +
                ", " + mIsRilConnectedForSlot[slot] +
                ", " + mIsImsStackUpForSlot[slot] +
                "}]";
    }

    @Override
    public void onCreate() {
        super.onCreate();
        log("PowerUpOptimizationService created");
        mContext = this;

        mQtiMsgTunnelClient = QtiMsgTunnelClient.getInstance();
        mQtiMsgTunnelClient.registerOemHookCallback(mOemHookCallback);

        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mSubscriptionManager =
                (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

        mNumPhones = mTelephonyManager.getActiveModemCount();

        mIsRilConnectedForSlot = new boolean[mNumPhones];
        mIsImsStackUpForSlot = new boolean[mNumPhones];
        isAtelReadySentForSlot = new boolean[mNumPhones];
        mImsMmTelManagers = new HashMap<>();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_RADIO_POWER_STATE_CHANGED);
        intentFilter.addAction(TelephonyManager.ACTION_SIM_CARD_STATE_CHANGED);
        intentFilter.addAction(TelephonyManager.ACTION_SIM_APPLICATION_STATE_CHANGED);

        if (Looper.myLooper() == null) {
            Log.e(TAG, "Preparing Looper");
            Looper.prepare();
        }

        mIsImsSupported =
                mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY_IMS);
        if (mIsImsSupported) {
            log("IMS is supported");

            mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == GET_IMS_STATE) {
                        ImsStackCheck checker = (ImsStackCheck) msg.obj;
                        log("GET_IMS_STATE, slot " + checker.slotId + ", sub: " + checker.subId
                                + ", retry: " + checker.retryCount);
                        checkImsState(checker.subId, checker.slotId, checker.retryCount);
                    } else if (msg.what == EVENT_IMS_STACK_UP) {
                        int slotId = (Integer) msg.obj;
                        log("EVENT_IMS_STACK_UP, slot " + slotId);
                        onImsStackReadyForSlot(slotId);
                    }
                }
            };
        } else {
            log("IMS is not supported");
            // Set IMS stack up bit for all slots
            for (int slot = 0 ; slot < mNumPhones; slot++) {
                mIsImsStackUpForSlot[slot] = true;
            }
            trySendPhoneReadyForAllSlots();
        }
        mContext.registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        try {
            mContext.unregisterReceiver(mBroadcastReceiver);
            mQtiMsgTunnelClient.unregisterOemHookCallback(mOemHookCallback);
        } catch (Exception ex) {
            Log.e(TAG, "onDestroy exception", ex);
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log("onStartCommand");
        return START_NOT_STICKY;
    }

    private void log(String msg) {
        Log.d(TAG, msg);
    }

    private static class ImsStackCheck {
        int subId;
        int slotId;
        int retryCount;
    }
}
