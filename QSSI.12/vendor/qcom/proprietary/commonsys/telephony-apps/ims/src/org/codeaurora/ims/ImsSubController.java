/*
 * Copyright (c) 2015-2016, 2019-2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */
package org.codeaurora.ims;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Looper;
import android.os.Handler;
import android.os.Message;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import androidx.annotation.VisibleForTesting;
import com.qualcomm.ims.utils.Log;
import org.codeaurora.telephony.utils.AsyncResult;
import org.codeaurora.telephony.utils.Preconditions;
import java.lang.Boolean;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

/**
 * The responsibility of this class to is to control which subscription
 * can support IMS for 7+1, 7+5 and 7+7 Reduced scope architectures.
 * The inputs to this class are
 *    1) RAF - 7 + 1 mode
 *    2) DDS - 7 + 5 mode
 *    3) PolicyManager Decision - 7 + 7 mode Reduced scope
 * The outputs from this class are
 *    1) Which subsciption(s) IMS is enabled on
 */
public class ImsSubController implements ImsSenderRxr.ImsRadioServiceListener {
    private final Context mContext;
    private List<ImsStateListener> mListeners = new CopyOnWriteArrayList<>();
    private List<ImsStackConfigListener> mStackConfigListeners = new CopyOnWriteArrayList<>();
    private List<OnMultiSimConfigChanged> mOnMultiSimConfigChangedListeners =
            new CopyOnWriteArrayList<>();
    private Handler mHandler;
    private static final int EVENT_SUB_CONFIG_CHANGED = 1;
    private static final int EVENT_GET_SUB_CONFIG = 2;
    private static final int EVENT_IMS_SERVICE_UP = 3;
    private static final int EVENT_IMS_SERVICE_DOWN = 4;
    private static final int EVENT_RADIO_AVAILABLE = 5;
    private static final int EVENT_RADIO_NOT_AVAILABLE = 6;
    private static final int EVENT_MSIM_VOICE_CAPABILITY_CHANGED = 7;
    private static final int EVENT_QUERY_MSIM_VOICE_CAPABILITY = 8;

    private static final int INVALID_PHONE_ID = -1;
    private static final int DEFAULT_PHONE_ID = 0;
    private int mNumMultiModeStacks = 0;
    private boolean mIsReceiverRegistered = false;
    private List<ImsSenderRxr> mSenderRxrs;
    private List<ImsServiceSub> mServiceSubs;
    private static final String ACTION_DDS_SWITCH_DONE
            = "org.codeaurora.intent.action.ACTION_DDS_SWITCH_DONE";
    private static int mSimultStackCount = 0;
    private static List<Boolean> mStackStatus;
    // Modem provides stack status for 6 slots.
    // NOTE: Currently, we expect only 2 stacks to be active.
    private final int MAX_VALID_STACK_STATUS_COUNT = 6;
    private boolean mActiveStacks[] = new boolean[MAX_VALID_STACK_STATUS_COUNT];
    private SubscriptionManager mSubscriptionManager = null;
    /*
     * DSDV (Dual Sim Dual Volte) i.e. 7+7
     * TRUE -- Dsdv is supported
     * FALSE -- Dsdv is not supported and can be inferred when
     *          lower layers inform that sub config request is
     *          not supported via ImsErrorCode.REQUEST_NOT_SUPPORTED
     */
    private boolean mIsDsdv = true;
    private TelephonyManager mTm = null;

    private static final String ACTION_MSIM_VOICE_CAPABILITY =
            "org.codeaurora.intent.action.MSIM_VOICE_CAPABILITY";
    private static final String PERMISSION_MSIM_VOICE_CAPABILITY =
            "com.qti.permission.RECEIVE_MSIM_VOICE_CAPABILITY";
    private static final String EXTRAS_MSIM_VOICE_CAPABILITY = "MsimVoiceCapability";

    public interface ImsStateListener {
        public void onActivateIms(int phoneId);
        public void onDeactivateIms(int phoneId);
    }

    public interface OnMultiSimConfigChanged {
        /**
         * Method that reports multi-sim configuration change
         * @param prevModemCount int value representing the previous number of active modem(s)
         * @param activeSimCount int value representing the current number of active modem(s)
         */
        public void onMultiSimConfigChanged(int prevModemCount, int activeModemCount);
    }

    public interface ImsStackConfigListener {
        /**
         * Method that reports the active/inactive status of each
         * IMS-capable stack.
         * @param activeStacks Array corresponding to IMS stacks (subscriptions).
         *        True and False values correspond to active and inactive respectively.
         * @param phoneId the serviceSub instance id that needs to act on this update
         */
        public void onStackConfigChanged(boolean[] activeStacks, int phoneId);
    }

    /**
     * Registers a stackConfigListener.
     * @param stackConfigListener Listener to be registered.
     * @param phoneId the serviceSub instance id that is registering for stack config updates
     * @see ImsSubController#ImsStackConfigListener
     * @throws IllegalArgumentException Will throw an error if stackConfigListener is null.
     */
    public void registerListener(ImsStackConfigListener stackConfigListener, int phoneId) {
        if (isDisposed()) {
            Log.d(this, "returning as ImsSubController is disposed");
            return;
        }
        if (stackConfigListener == null) {
            throw new IllegalArgumentException("stackConfigListener is null!");
        }
        if (!mStackConfigListeners.contains(stackConfigListener)) {
            mStackConfigListeners.add(stackConfigListener);
        } else {
            Log.w(this, "registerListener :: duplicate stackConfigListener!");
        }
        synchronized(this.getClass()) {
            notifyStackConfigChanged(mActiveStacks, phoneId);
        }
    }

    /**
     * Unregisters a stackConfigListener.
     * @param stackConfigListener Listener to unregister
     * @see ImsSubController#ImsStackConfigListener
     * @throws IllegalArgumentException Will throw an error if listener is null.
     * @return true of listener is removed, false otherwise.
     */
    public boolean unregisterListener(ImsStackConfigListener stackConfigListener) {
        if (isDisposed()) {
            Log.d(this, "returning as ImsSubController is disposed");
            return false;
        }
        if (stackConfigListener == null) {
            throw new IllegalArgumentException("stackConfigListener is null");
        }
        return mStackConfigListeners.remove(stackConfigListener);
    }

    /**
     * Registers a OnMultiSimConfigChanged listener.
     * @param listener Listener to be registered.
     * @see ImsSubController#OnMultiSimConfigChanged
     * @throws IllegalArgumentException Will throw an error if simConfigChangedListener is null.
     */
    public void registerListener(OnMultiSimConfigChanged listener) {
        if (isDisposed()) {
            Log.d(this, "returning as ImsSubController is disposed");
            return;
        }
        if (listener == null) {
            throw new IllegalArgumentException("simConfigChangedListener is null!");
        }
        if (!mOnMultiSimConfigChangedListeners.contains(listener)) {
            mOnMultiSimConfigChangedListeners.add(listener);
        } else {
            Log.w(this, "registerListener :: duplicate OnMultiSimConfigChanged listener!");
        }
    }

    /**
     * Unregisters a OnMultiSimConfigChanged listener.
     * @param listener Listener to unregister
     * @see ImsSubController#OnMultiSimConfigChanged
     * @throws IllegalArgumentException Will throw an error if listener is null.
     * @return true of listener is removed, false otherwise.
     */
    public boolean unregisterListener(OnMultiSimConfigChanged listener) {
        if (isDisposed()) {
            Log.d(this, "returning as ImsSubController is disposed");
            return false;
        }
        if (listener == null) {
            throw new IllegalArgumentException("simConfigChangedListener");
        }
        return mOnMultiSimConfigChangedListeners.remove(listener);
    }

    public ImsSubController(Context context) {
        this(context, Looper.getMainLooper());
    }

    public ImsSubController(Context context, Looper looper) {
        this(context, new CopyOnWriteArrayList<ImsSenderRxr>(),
                new CopyOnWriteArrayList<ImsServiceSub>(), looper);
        int activeModemCount = getActiveModemCount();
        for (int i = 0; i < activeModemCount; i++) {
            createImsSenderRxr(context, i);
            createImsServiceSub(context, i, mSenderRxrs.get(i));
        }
    }

    @VisibleForTesting
    public ImsSubController(Context context, List<ImsSenderRxr> senderRxrs,
            List<ImsServiceSub> serviceSubs, Looper looper) {
        mContext = context;
        mTm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        mContext.registerReceiver(mMultiSimConfigChangedReceiver,
                new IntentFilter(TelephonyManager.
                        ACTION_MULTI_SIM_CONFIG_CHANGED));
        mSenderRxrs = senderRxrs;
        mServiceSubs = serviceSubs;
        mHandler = new ImsSubControllerHandler(looper);
        mSubscriptionManager = (SubscriptionManager) mContext.getSystemService(
                Context.TELEPHONY_SUBSCRIPTION_SERVICE);
    }

    public boolean isMultiSimEnabled() {
        return getActiveModemCount() > 1;
    }

    public int getActiveModemCount() {
        return mTm.getActiveModemCount();
    }

    private void createImsSenderRxr(Context context, int phoneId) {
        ImsSenderRxr senderRxr = new ImsSenderRxr(mContext, phoneId);
        senderRxr.registerForAvailable(mHandler, EVENT_RADIO_AVAILABLE, phoneId);
        senderRxr.registerForNotAvailable(mHandler, EVENT_RADIO_NOT_AVAILABLE, phoneId);
        senderRxr.registerListener(this);
        mSenderRxrs.add(senderRxr);
    }

    private void createImsServiceSub(Context context, int phoneId, ImsSenderRxr senderRxr) {
        ImsServiceSub serviceSub = new ImsServiceSub(context, phoneId, senderRxr, this);
        mServiceSubs.add(serviceSub);
    }

    @VisibleForTesting
    public void setIsDsdv(boolean isDsdv) {
        mIsDsdv = isDsdv;
    }

    @VisibleForTesting
    public Handler getHandler() {
        return mHandler;
    }

    @Override
    public void onServiceUp(int phoneId) {
        Log.i(this, "onServiceUp :: phoneId=" + phoneId);
        if (isDisposed()) {
            Log.d(this, "onServiceUp, returning as isDisposed");
            return;
        }
        Message msg = mHandler.obtainMessage(EVENT_IMS_SERVICE_UP);
        msg.arg1 = phoneId;
        msg.sendToTarget();
    }

    @Override
    public void onServiceDown(int phoneId) {
        Log.i(this, "onServiceDown :: phoneId=" + phoneId);
        if (isDisposed()) {
            Log.d(this, "onServiceDown, returning as isDisposed");
            return;
        }
        Message msg = mHandler.obtainMessage(EVENT_IMS_SERVICE_DOWN);
        msg.arg1 = phoneId;
        msg.sendToTarget();
    }

    /**
     * Registers listener.
     * @param listener Listener to be registered
     * @see ImsSubController#Listener
     * @throws IllegalArgumentException Will throw an error if listener is null.
     */
    public void registerListener(ImsStateListener listener) {
        if (isDisposed()) {
            Log.d(this, "registerListener, returning as isDisposed");
            return;
        }
        if (listener == null) {
            throw new IllegalArgumentException("listener is null");
        }
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        } else {
            Log.w(this, "Duplicate listener " + listener);
        }
    }

    /**
     * Unregisters listener.
     * @param listener Listener to unregister
     * @see ImsSubContrller#Listener
     * @throws IllegalArgumentException Will throw an error if listener is null.
     * @return true of listener is removed, false otherwise.
     */
    public boolean unregisterListener(ImsStateListener listener) {
        if (isDisposed()) {
            Log.d(this, "unregisterListener, returning as isDisposed");
            return false;
        }
        if (listener == null) {
            throw new IllegalArgumentException("listener is null");
        }
        return mListeners.remove(listener);
    }

    public static int getDefaultPhoneId() {
        return DEFAULT_PHONE_ID;
    }

    public boolean isDsdv() {
        return mIsDsdv;
    }

    private void notifyStackConfigChanged(boolean[] activeStacks, int phoneId) {
        Log.v(this, "notifyStackConfigChanged: activeStacks:" + Arrays.toString(activeStacks)
                + " phoneId: " + phoneId);
        for (ImsStackConfigListener listener : mStackConfigListeners) {
            listener.onStackConfigChanged(activeStacks, phoneId);
        }
    }

    private void notifyOnMultiSimConfigChanged(int prevModemCount, int activeModemCount) {
        Log.v(this, "notifyOnMultiSimConfigChanged: prevModemCount: " + prevModemCount
                + " activeModemCount: " + activeModemCount);
        if (prevModemCount == activeModemCount) {
            return;
        }
        for (OnMultiSimConfigChanged listener : mOnMultiSimConfigChangedListeners) {
            listener.onMultiSimConfigChanged(prevModemCount, activeModemCount);
        }
    }

    private void handleSubConfigException(Throwable exception) {
        Preconditions.checkArgument(exception != null);
        final int errorCode = ((ImsRilException)exception).getErrorCode();
        Log.i(this, "handleSubConfigException error : " + errorCode);
        if (errorCode == ImsErrorCode.REQUEST_NOT_SUPPORTED) {
            mIsDsdv = false;
        // This error means an older modem that does not support the request is
        // being paired with HLOS that has L+L feature. In this case fallback
        // to legacy behavior.
            initSubscriptionStatus();
        } else {
            Log.w (this, "Unhandled error code : " + errorCode);
        }
    }

    private void handleSubConfigChanged(AsyncResult ar) {
        if (ar.exception != null) {
            handleSubConfigException(ar.exception);
        } else if(ar.result != null) {
            ImsSubConfigDetails config = (ImsSubConfigDetails) ar.result;
            mSimultStackCount = config.getSimultStackCount();
            mStackStatus = config.getImsStackEnabledList();
            boolean[] activeStacks = new boolean[MAX_VALID_STACK_STATUS_COUNT];

            for(int i = 0; i < mSimultStackCount; ++i) {
                activeStacks[i] = mStackStatus.get(i);
            }

            if (ar.userObj == null) {
                Log.e(this, "handleSubConfigChanged ar.userObj is null");
                return;
            }

            notifyStackConfigChanged(activeStacks, (int)ar.userObj);
        } else {
            Log.e(this, "ar.result and ar.exception are null");
        }
    }

    private void handleMultiSimVoiceCapability(AsyncResult ar) {
        if (ar.exception != null) {
            final int errorCode = ((ImsRilException)ar.exception).getErrorCode();
            Log.e(this, "handleMultiSimVoiceCapability errorCode: " + errorCode);
            return;
        }

        if (ar.result == null) {
            Log.e(this, "handleMultiSimVoiceCapability ar.result is null");
            return;
        }

        broadcastConcurrentCallsIntent((int)ar.result);
    }

    private void broadcastConcurrentCallsIntent(int voiceCapability) {
        Intent intent = new Intent(ACTION_MSIM_VOICE_CAPABILITY);
        intent.putExtra(EXTRAS_MSIM_VOICE_CAPABILITY, voiceCapability);
        mContext.sendBroadcast(intent, PERMISSION_MSIM_VOICE_CAPABILITY);
    }

    private class ImsSubControllerHandler extends Handler {
        public ImsSubControllerHandler() {
            super();
        }

        public ImsSubControllerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.i(this, "Message received: what = " + msg.what);
            if (isDisposed()) {
                Log.d(this, "handleMessage, returning as isDisposed");
                return;
            }
            AsyncResult ar;
            int phoneId = INVALID_PHONE_ID;
            try {
                switch (msg.what) {
                    case EVENT_SUB_CONFIG_CHANGED:
                    case EVENT_GET_SUB_CONFIG:
                        ar = (AsyncResult)msg.obj;
                        phoneId = (int)ar.userObj;
                        Log.i(this, "Received SubConfig Event phoneId = " + phoneId);
                        handleSubConfigChanged(ar);
                        break;
                    case EVENT_IMS_SERVICE_UP:
                        phoneId = (int) msg.arg1;
                        Log.i(this, "Received EVENT_IMS_SERVICE_UP phoneId = " + phoneId);
                        registerForRadioEvents(phoneId);
                        break;
                    case EVENT_IMS_SERVICE_DOWN:
                        phoneId = (int) msg.arg1;
                        Log.i(this, "Received EVENT_IMS_SERVICE_DOWN phoneId = " + phoneId);
                        deRegisterFromRadioEvents(phoneId);
                        updateStackConfig(phoneId, false);
                        break;

                    case EVENT_RADIO_NOT_AVAILABLE:
                        ar = (AsyncResult)msg.obj;
                        phoneId = (int)ar.userObj;
                        Log.i(this, "Received EVENT_RADIO_NOT_AVAILABLE phoneId = " + phoneId);
                        updateStackConfig(phoneId, false);
                        break;

                    case EVENT_RADIO_AVAILABLE:
                        ar = (AsyncResult)msg.obj;
                        phoneId = (int)ar.userObj;
                        Log.i(this, "Received EVENT_RADIO_AVAILABLE phoneId = " + phoneId);
                        handleRadioAvailable(phoneId);
                        break;

                    case EVENT_MSIM_VOICE_CAPABILITY_CHANGED:
                    case EVENT_QUERY_MSIM_VOICE_CAPABILITY:
                        ar = (AsyncResult)msg.obj;
                        phoneId = (int)ar.userObj;
                        Log.i(this, "Received multi sim voice capability phoneId = " + phoneId);
                        handleMultiSimVoiceCapability(ar);
                        break;

                    default:
                        Log.w(this, "Unknown message = " + msg.what);
                        break;
                }
            } catch (IndexOutOfBoundsException exc) {
                Log.e(this, "handleMessage :: Invalid phoneId " + phoneId);
            }
        }
    }

    private void handleRadioAvailable(int phoneId) {
        if (mTm == null || mTm.getActiveModemCount() <= 1) {
            Log.v(this, "handleRadioAvailable: Single SIM mode");
            initSubscriptionStatus();
            return;
        }

        // Query stack configuration and multi sim voice capability when radio is available
        // to update the latest status from modem.
        // This is required to handle modem SSR use cases.
        ImsSenderRxr ci = mSenderRxrs.get(phoneId);
        if (ci == null) {
            Log.e(this, "handleRadioAvailable: ImsSenderRxr is null");
            return;
        }

        ci.getImsSubConfig(mHandler.obtainMessage(EVENT_GET_SUB_CONFIG, phoneId));
        if (phoneId == DEFAULT_PHONE_ID) {
            ci.queryMultiSimVoiceCapability(mHandler.obtainMessage(
                    EVENT_QUERY_MSIM_VOICE_CAPABILITY, phoneId));
        }
    }

    private void registerForRadioEvents(int phoneId) {
        if (mTm == null || mTm.getActiveModemCount() <= 1) {
            Log.v(this, "registerForRadioEvents: Single SIM mode");
            initSubscriptionStatus();
            return;
        }

        ImsSenderRxr ci = mSenderRxrs.get(phoneId);
        if (ci == null) {
            Log.e(this, "registerForRadioEvents: ImsSenderRxr is null");
            return;
        }

        final boolean isPrimarySubscription = phoneId == DEFAULT_PHONE_ID;
        final boolean isRadioAvailable = ci.getRadioState() != null &&
                ci.getRadioState().isAvailable();
        ci.registerForImsSubConfigChanged(mHandler, EVENT_SUB_CONFIG_CHANGED, phoneId);
        if (isRadioAvailable) {
            ci.getImsSubConfig(mHandler.obtainMessage(EVENT_GET_SUB_CONFIG, phoneId));
        }

        // Some events will be triggered on primary subscription only. Register for those events if
        // this is primary subscription.
        if (!isPrimarySubscription ) {
            Log.v(this, "registerForRadioEvents: phoneId: " + phoneId +
                    " is not primary subscription.");
            return;
        }

        ci.registerForMultiSimVoiceCapabilityChanged(mHandler, EVENT_MSIM_VOICE_CAPABILITY_CHANGED,
                phoneId);
        if (isRadioAvailable) {
            ci.queryMultiSimVoiceCapability(mHandler.obtainMessage(
                    EVENT_QUERY_MSIM_VOICE_CAPABILITY, phoneId));
        }
    }

    private void deRegisterFromRadioEvents(int phoneId) {
        if (mTm == null || mTm.getActiveModemCount() <= 1) {
            Log.v(this, "deRegisterFromRadioEvents: Single SIM mode");
            return;
        }

        ImsSenderRxr ci = mSenderRxrs.get(phoneId);
        if (ci == null) {
            Log.e(this, "deRegisterFromRadioEvents: ImsSenderRxr is null");
            return;
        }

        ci.deregisterForImsSubConfigChanged(mHandler);

        // Some events will be triggered on primary subscription only. Deregister for those events
        // if this is deafult subscription.
        final boolean isPrimarySubscription = phoneId == DEFAULT_PHONE_ID;
        if (!isPrimarySubscription ) {
            Log.v(this, "deRegisterFromRadioEvents: phoneId: " + phoneId +
                    " is not primary subscription.");
            return;
        }

        ci.deregisterForMultiSimVoiceCapabilityChanged(mHandler);
    }

    private void updateStackConfig(int phoneId, boolean isEnabled) {
        boolean[] activeStacks;
        Log.v(this, "updateStackConfig phoneId: " + phoneId + " isEnabled: " + isEnabled
                 + " mIsDsdv : " + mIsDsdv);

        if (mIsDsdv) {
            if (mStackStatus == null) {
                Log.w(this, "updateStackConfig Stacks are not yet initialized");
                return;
            }

            if (mStackStatus.get(phoneId) == isEnabled) {
                Log.w(this, "updateStackConfig nothing to update");
                return;
            }

            activeStacks = new boolean[MAX_VALID_STACK_STATUS_COUNT];
            mStackStatus.set(phoneId, isEnabled);
            for(int i = 0; i < mSimultStackCount; ++i) {
                activeStacks[i] = mStackStatus.get(i);
            }
        } else {

            /* Unregister the earlier receiver, if any */
            if (mIsReceiverRegistered) {
                Log.v(this, "updateStackConfig: unregistering BroadcastReceiver");
                mContext.unregisterReceiver(mBroadcastReceiver);
                mIsReceiverRegistered = false;
            }

            if (mActiveStacks[phoneId] == isEnabled) {
                Log.w(this, "updateStackConfig nothing to update");
                return;
            }

            if (!isEnabled) {
                /*
                 * When we notify that a particular stack is not enabled (for eg. due to modem
                 * ssr, rild crash etc.), feature state for that stack will be set to UNAVAILABLE.
                 * When lower layers again inform that the stack is enabled, feature state for
                 * stack needs to be set to AVAILABLE. In 7+1/7+5 cases, this is not happening.
                 * Reset <mNumMultiModeStacks> to ensure that same sequence of events that happen
                 * on normal boot-up happens in modem ssr/rild crash etc. use-cases.
                 */
                mNumMultiModeStacks = 0;
            }

            mActiveStacks[phoneId] = isEnabled;
            activeStacks = mActiveStacks;
        }
        notifyStackConfigChanged(activeStacks, phoneId);
    }

    /* Method to initialize the Phone Id */
    private void initSubscriptionStatus() {
        mSimultStackCount = 0;
        mStackStatus = null;
        // TODO: Check how Single-SIM/backward compatible multi-SIM scenarios are handled.
        if (mTm.getActiveModemCount() > 1) {
            Log.i(this, "initSubscriptionStatus: [Multi-sim] Using RAF and DDS to decide IMS Sub");
            handleRafInfo();
        } else {
            /* If not multi-sim, a change in socket communication is not required */
            Log.i(this, "initSubscriptionStatus: Not multi-sim.");
            mIsDsdv = false;
            updateActiveImsStackForPhoneId(DEFAULT_PHONE_ID);
        }
    }

    /* Method to re-initialize the Ims Phone instances.
       This API will only be used for single IMS stack. */
    public void updateActiveImsStackForPhoneId(int phoneId) {
        if (isDisposed()) {
            Log.d(this, "updateActiveImsStackForPhoneId return as ImsSubController is disposed");
            return;
        }

        if (phoneId == SubscriptionManager.INVALID_SIM_SLOT_INDEX) {
            Log.e(this, "switchImsPhone: Invalid phoneId: " + phoneId);
            return;
        }

        synchronized(this.getClass()) {
            for (int i = 0; i < mActiveStacks.length; ++i) {
                if (i == phoneId) {
                    mActiveStacks[i] = true;
                } else {
                    mActiveStacks[i] = false;
                }
            }

            notifyStackConfigChanged(mActiveStacks, phoneId);
        }
    }

    /* Method to validate a subId and then check for IMSPhonechange.
       This is only used for single IMS stack. */
    private void updateActiveImsStackForSubId(int ddsSubId) {
        int phoneId = 0; /* By default, always assume dds as default phone ID */

        /*
         * If the subscription ID received via intent is valid,
         * then retrieve the corresponding phone ID.
         */
        if (SubscriptionManager.isValidSubscriptionId(ddsSubId)) {
            phoneId = SubscriptionManager.getSlotIndex(ddsSubId);
        } else {
            /* Recheck the subId via API */
            ddsSubId = SubscriptionManager.getDefaultDataSubscriptionId();
            /* If the subscription ID is Valid, then retrive corresponding phone ID */
            if (SubscriptionManager.isValidSubscriptionId(ddsSubId)) {
                phoneId = SubscriptionManager.getSlotIndex(ddsSubId);
            }
        }

        Log.i(this, "updateActiveImsStackForSubId: new DDS = " + ddsSubId);
        updateActiveImsStackForPhoneId(phoneId);
    }

    /**
     * Method to identify the RAT mask for Multimode support.
     * Checks for LTE, to support IMS.
     */
    private boolean isMultiModeSupported(long nRatMask) {
        return (nRatMask & TelephonyManager.NETWORK_TYPE_BITMASK_LTE) != 0;
    }

    /* Method to handle 7+5 and 7+1 use cases */
    private void handleRafInfo() {
        /* If mNumMultiModeStacks is greater than zero then return */
        if (mNumMultiModeStacks > 0) {
            Log.i(this, "handleRafInfo: " + mNumMultiModeStacks +
                    "Multimode stacks greater than zero. EXIT!!!");
            return;
        }
        /* Check for the number of multi-mode stacks */
        final int numPhones = mTm.getActiveModemCount();
        int tempPhoneId = 0; /* Always assume default phone ID as 0 */
        for (int i = 0; i < numPhones; i++) {
            final SubscriptionInfo subInfo =
                    mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(i);
            if (subInfo != null) {
                final int subId = subInfo.getSubscriptionId();
                long rafInfo = mTm.createForSubscriptionId(subId).getSupportedRadioAccessFamily();
                Log.i(this, "handleRafInfo: Phone: " + i + " Info:" + rafInfo);
                if (isMultiModeSupported(rafInfo)) {
                    mNumMultiModeStacks++;
                    tempPhoneId = i;
                }
            } else {
                Log.e(this, "handleRafInfo: subIds not valid");
            }
        }
        Log.i(this, "handleRafInfo: NumPhones: " + numPhones +
                "Multiple Multimode stacks: " + mNumMultiModeStacks);

        /*
         * If more than 1 Multimode stack is present, then listen to DDS change event
         * else just update the ims stack
         */
        if (mNumMultiModeStacks > 1) {

            /* Unregister the earlier receiver, if any */
            if (mIsReceiverRegistered) {
                mContext.unregisterReceiver(mBroadcastReceiver);
                mIsReceiverRegistered = false;
            }

            /*
             * To avoid the scenarios where DDS change event is not broadcasted yet,
             * by default check the data subscription information once.
             */
            updateActiveImsStackForSubId(SubscriptionManager.INVALID_SUBSCRIPTION_ID);

            /* Start listening to the DDS change event. */
            mContext.registerReceiver(mBroadcastReceiver,
                    new IntentFilter(ACTION_DDS_SWITCH_DONE));
            mIsReceiverRegistered = true;
            Log.i(this, "handleRafInfo: registered for DDS switch...");

        } else {
            updateActiveImsStackForPhoneId(tempPhoneId);
        }
    }

    /* Receiver to handle RAF and DDS change event */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (isDisposed()) {
                Log.d(this, "mBroadcastReceiver onReceive, returning as is disposed");
                return;
            }
            String action = intent.getAction();
            Log.i(this, "mBroadcastReceiver - " + action);
            if (action.equals(ACTION_DDS_SWITCH_DONE)) {
                int ddsSubId = intent.getIntExtra(SubscriptionManager.EXTRA_SUBSCRIPTION_INDEX,
                        SubscriptionManager.INVALID_SUBSCRIPTION_ID);
                Log.i(this, "got ACTION_DDS_SWITCH_DONE, new DDS = "
                        + ddsSubId);
                updateActiveImsStackForSubId(ddsSubId);
            }
        }
    };

    private boolean isDisposed() {
        return mHandler == null;
    }

    public List<ImsServiceSub> getServiceSubs() {
        return mServiceSubs;
    }

    public ImsServiceSub getServiceSub(int phoneId) {
        if (phoneId > INVALID_PHONE_ID && phoneId < mServiceSubs.size()) {
            return mServiceSubs.get(phoneId);
        }
        return null;
    }

    private BroadcastReceiver mMultiSimConfigChangedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (isDisposed()) {
                Log.d(this, "onReceive, returning as isDisposed");
                return;
            }
            String action = intent.getAction();
            if (action.equals(TelephonyManager.ACTION_MULTI_SIM_CONFIG_CHANGED)) {
                int activeModemCount = intent.getIntExtra(
                        TelephonyManager.EXTRA_ACTIVE_SIM_SUPPORTED_COUNT, 1);
                handleOnMultiSimConfigChanged(activeModemCount);
            }
        }
    };

    // Helper function that handles the multi sim configuration change
    private void handleOnMultiSimConfigChanged(int activeModemCount) {
        int prevModemCount = mServiceSubs.size();
        if (activeModemCount == prevModemCount) {
            Log.d(this, "The number of slots is equal to the current size, nothing to do");
            return;
        }
        if (activeModemCount > prevModemCount) {
            switchToMultiSim(prevModemCount, activeModemCount);
        } else {
            switchToSingleSim(prevModemCount, activeModemCount);
            broadcastConcurrentCallsIntent(MultiSimVoiceCapability.UNKNOWN);
        }
        notifyOnMultiSimConfigChanged(prevModemCount, activeModemCount);
    }

    // Helper function to handle the transition to single sim
    private void switchToSingleSim(int prevModemCount, int activeModemCount) {
        for (int i = prevModemCount - 1; i >= activeModemCount; --i) {
            mServiceSubs.get(i).dispose();
            mServiceSubs.remove(i);
            mSenderRxrs.remove(i);
        }
    }

    // Helper function to handle the transition to multi sim
    private void switchToMultiSim(int prevModemCount, int activeModemCount) {
        for (int i = prevModemCount; i < activeModemCount; ++i) {
            createImsSenderRxr(mContext, i);
            createImsServiceSub(mContext, i, mSenderRxrs.get(i));
        }
    }

    public void dispose() {
        if (isDisposed()) {
            Log.d(this, "dispose: returning as already disposed");
            return;
        }
        Log.d(this, "dispose ImsSubController, unregistering handler and listeners");
        mContext.unregisterReceiver(mMultiSimConfigChangedReceiver);
        for(ImsServiceSub sub : mServiceSubs) {
            sub.dispose();
        }
        for(ImsSenderRxr senderRxr : mSenderRxrs) {
            senderRxr.unregisterForAvailable(mHandler);
            senderRxr.unregisterForNotAvailable(mHandler);
            senderRxr.unregisterListener(this);
            senderRxr.deregisterForImsSubConfigChanged(mHandler);
            senderRxr.deregisterForMultiSimVoiceCapabilityChanged(mHandler);
        }
        if (mIsReceiverRegistered) {
            mContext.unregisterReceiver(mBroadcastReceiver);
        }
        mTm = null;
        mHandler = null;
        mServiceSubs.clear();
        mServiceSubs = null;
        mSenderRxrs.clear();
        mSenderRxrs = null;
        mSubscriptionManager = null;
        mStackConfigListeners.clear();
        mStackConfigListeners = null;
        mOnMultiSimConfigChangedListeners.clear();
        mOnMultiSimConfigChangedListeners = null;
        mBroadcastReceiver = null;
        mListeners.clear();
        mListeners = null;
    }
}
