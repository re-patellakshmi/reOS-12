/*
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */

package com.qti.phone;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.ImsiEncryptionInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.Thread;

import com.qti.extphone.Client;
import com.qti.extphone.IDepersoResCallback;
import com.qti.extphone.IExtPhone;
import com.qti.extphone.IExtPhoneCallback;
import com.qti.extphone.QtiImeiInfo;
import com.qti.extphone.NrConfig;
import com.qti.extphone.Token;
import com.qti.phone.powerupoptimization.PowerUpOptimizationService;
import com.qti.phone.primarycard.PrimaryCardService;
import com.qti.phone.subsidylock.SubsidyDeviceController;
import com.qti.phone.subsidylock.SubsidyLockUtils;

public class ExtTelephonyServiceImpl {

    private static final String LOG_TAG = "ExtTelephonyServiceImpl";
    private static Context mContext;
    private static int mNumPhones;
    private static final int SLOT_INVALID = -1;
    private static final int ACTIVE_SIM_SUPPORTED_SINGLE = 1;
    private static final String CONFIG_CURRENT_PRIMARY_SUB = "config_current_primary_sub";
    private static final String MULTI_SIM_SMS_PROMPT = "multi_sim_sms_prompt";
    private static final int NOT_ENABLED = 0;
    private static final int DEFAULT_PHONE_INDEX = 0;
    private static final String PROPERTY_POWER_UP_OPTIMIZATION = "persist.vendor.radio.poweron_opt";
    private static final String PROPERTY_PRIMARY_CARD = "persist.vendor.radio.primarycard";
    private static final String SUBSIDY_DEVICE_PROPERTY_NAME =
            "persist.vendor.radio.subsidydevice";
    private boolean mRegisterReceiver = false;

    private QtiRadioProxy mQtiRadioProxy;
    private QtiPrimaryImeiHandler mPrimaryImeiHandler = null;
    private SubsidyDeviceController mSubsidyDevController;

    public ExtTelephonyServiceImpl(Context context) {
        mContext = context;
        mNumPhones = getPhoneCount();
        QtiMsgTunnelClient.init(mContext);
        mQtiRadioProxy = new QtiRadioProxy(mContext);
        ExtTelephonyThread extTelephonyThread = new ExtTelephonyThread();
        extTelephonyThread.start();
    }

    protected void cleanUp() {
        if(mRegisterReceiver) {
            mContext.unregisterReceiver(mMultisimBroadcastReceiver);
            mRegisterReceiver = false;
        }
    }

    class ExtTelephonyThread extends Thread {
        public void run() {
            startPowerUpOptimizationServiceIfRequired();
            startPrimaryCardServiceIfRequired();
            mContext.registerReceiver(mMultisimBroadcastReceiver,
                    new IntentFilter(TelephonyManager.ACTION_MULTI_SIM_CONFIG_CHANGED));
            mRegisterReceiver = true;
            makeQtiPrimaryImeiHandler();
            initSubsidyDeviceController();
        }
    }

    /**
     * Starts PowerUpOptimizationService if required.
     * This service should be started only when {@link #PROPERTY_POWER_UP_OPTIMIZATION} is
     * enabled.
     */
    private void startPowerUpOptimizationServiceIfRequired() {
        int powerUpOptimizationPropVal = NOT_ENABLED;
        try {
            powerUpOptimizationPropVal = mQtiRadioProxy.
                    getPropertyValueInt(PROPERTY_POWER_UP_OPTIMIZATION, NOT_ENABLED);
        } catch (RemoteException | NullPointerException ex) {
            Log.e(LOG_TAG, "Exception: ", ex);
        }

        if (powerUpOptimizationPropVal == NOT_ENABLED) {
            Log.d(LOG_TAG, "PowerUpOptimization is not enabled.");
            return;
        }

        // Start PowerUpOptimizationService
        Intent serviceIntent = new Intent(mContext, PowerUpOptimizationService.class);
        ComponentName serviceComponent = mContext.startService(serviceIntent);
        if (serviceComponent == null) {
            Log.e(LOG_TAG, "Could not start PowerUpOptimizationService");
        } else {
            Log.d(LOG_TAG, "Successfully started PowerUpOptimizationService");
        }
    }

    /**
     * Starts PrimaryCardService if required.
     * This service should be started only when {@link #PROPERTY_PRIMARY_CARD} is
     * enabled, and the device supports multi-sim configuration.
     */
    private void startPrimaryCardServiceIfRequired() {
        // Check multi-sim configuration
        if (mNumPhones < 2) {
            Log.d(LOG_TAG, "Device is not multi-sim. PrimaryCard is not supported.");
            return;
        }

        // check the value of PROPERTY_PRIMARY_CARD
        boolean isPrimaryCardEnabled = false;
        try {
            isPrimaryCardEnabled = mQtiRadioProxy.
                    getPropertyValueBool(PROPERTY_PRIMARY_CARD, false);
        } catch (RemoteException | NullPointerException ex) {
            Log.e(LOG_TAG, "Exception: ", ex);
        }

        if (!isPrimaryCardEnabled) {
            Log.d(LOG_TAG, "PrimaryCard feature is not enabled.");
            return;
        }

        // Start PrimaryCardService
        Intent serviceIntent = new Intent(mContext, PrimaryCardService.class);
        ComponentName serviceComponent = mContext.startService(serviceIntent);
        if (serviceComponent == null) {
            Log.e(LOG_TAG, "Could not start PrimaryCardService");
        } else {
            Log.d(LOG_TAG, "Successfully started PrimaryCardService");
        }
    }

    private void initSubsidyDeviceController() {
        if (mNumPhones < 2) {
           Log.d(LOG_TAG, "Device should be multi-sim for Subsidyfeature to be supported.");
           return;
        }

        if (!isSubsidyFeatureEnabled()) {
            Log.d(LOG_TAG, "Subsidylock feature is not enabled");
            return;
        }

        if (mSubsidyDevController == null) {
            mSubsidyDevController = new SubsidyDeviceController(mContext);
        }
    }

    boolean isSubsidyFeatureEnabled() {
        boolean isFeatureEnabled = false;
        try {
            isFeatureEnabled = mQtiRadioProxy.getPropertyValueBool(
                    SUBSIDY_DEVICE_PROPERTY_NAME, false);
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Exception: ", ex);
        }
        return isFeatureEnabled;
    }

    private void disposeSubsidyDeviceController() {
        if (mSubsidyDevController != null) {
            mSubsidyDevController.dispose();
            mSubsidyDevController = null;
        }
    }

    public boolean isPrimaryCarrierSlotId(int slotId) {
        return SubsidyLockUtils.isPrimaryCapableSimCard(mContext, slotId);
    }

    private final BroadcastReceiver mMultisimBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (TelephonyManager.ACTION_MULTI_SIM_CONFIG_CHANGED.equals(intent.getAction())) {
                int newCount =
                        intent.getIntExtra(TelephonyManager.EXTRA_ACTIVE_SIM_SUPPORTED_COUNT, 1);
                Log.d(LOG_TAG, "Received ACTION_MULTI_SIM_CONFIG_CHANGED, newCount: " + newCount);

                if (newCount == ACTIVE_SIM_SUPPORTED_SINGLE) {
                    // The device is no longer multi-sim. Stop PrimaryCardService.
                    if (isServiceRunning(PrimaryCardService.class)) {
                        Log.d(LOG_TAG, "Stopping PrimaryCardService");
                        mContext.stopService(new Intent(mContext, PrimaryCardService.class));
                    }
                    if (mPrimaryImeiHandler != null) {
                        mPrimaryImeiHandler.destroy();
                        mPrimaryImeiHandler = null;
                    }
                    disposeSubsidyDeviceController();
                } else {
                    // Config has changed to multi-sim, which could be 2 or 3
                    if (!isServiceRunning(PrimaryCardService.class)) {
                        startPrimaryCardServiceIfRequired();
                    }
                    makeQtiPrimaryImeiHandler();
                    initSubsidyDeviceController();
                }
            }
        }
    };

    private void makeQtiPrimaryImeiHandler() {
        Log.d(LOG_TAG, "makeQtiPrimaryImeiHandler " + mPrimaryImeiHandler);
        if (mPrimaryImeiHandler == null && mNumPhones > 1 && QtiRadioFactory.isAidlAvailable()) {
            mPrimaryImeiHandler = new QtiPrimaryImeiHandler(mContext, mQtiRadioProxy);
        }
    }

    private static boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager =
                (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    private int getPhoneCount() {
        TelephonyManager tm = (TelephonyManager) mContext.
                getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getActiveModemCount();
    }

    public int getPropertyValueInt(String property, int def) throws RemoteException {
        return mQtiRadioProxy.getPropertyValueInt(property, def);
    }

    public boolean getPropertyValueBool(String property, boolean def) throws RemoteException {
        return mQtiRadioProxy.getPropertyValueBool(property, def);
    }

    public String getPropertyValueString(String property, String def) throws RemoteException {
        return mQtiRadioProxy.getPropertyValueString(property, def);
    }

    public int getCurrentPrimaryCardSlotId() {
        int slotId = Settings.Global.getInt(mContext.getContentResolver(),
                CONFIG_CURRENT_PRIMARY_SUB, SLOT_INVALID);
        Log.d(LOG_TAG, "getCurrentPrimaryCardSlotId slotId="+slotId);
        return slotId;
    }

    public boolean performIncrementalScan(int slotId) {
        return QtiMsgTunnelClient.getInstance().performIncrementalScan(slotId);
    }

    public boolean abortIncrementalScan(int slotId) {
        return QtiMsgTunnelClient.getInstance().abortIncrementalScan(slotId);
    }

    public boolean isSMSPromptEnabled() {
        boolean prompt = false;
        int value = 0;
        try {
            value = Settings.Global.getInt(mContext.getContentResolver(),
                    MULTI_SIM_SMS_PROMPT);
        } catch (SettingNotFoundException snfe) {
            Log.d(LOG_TAG, "Exception Reading Dual Sim SMS Prompt Values");
        }
        prompt = (value == 0) ? false : true ;
        Log.d(LOG_TAG, "isSMSPromptEnabled: SMS Prompt option:" + prompt);
        return prompt;
    }

    public void setSMSPromptEnabled(boolean enabled) {
        int value = (enabled == false) ? 0 : 1;
        Settings.Global.putInt(mContext.getContentResolver(),
                MULTI_SIM_SMS_PROMPT, value);
        Log.d(LOG_TAG, "setSMSPromptEnabled to " + enabled + " Done");
    }

    public void supplyIccDepersonalization(String netpin, String type,
                                           IDepersoResCallback callback, int phoneId) {
        QtiMsgTunnelClient.getInstance().
                supplyIccDepersonalization(netpin, type, callback, phoneId);
    }

    public Token enableEndc(int slot, boolean enable, Client client) throws RemoteException {
        return mQtiRadioProxy.enableEndc(slot, enable, client);
    }

    public Token queryNrIconType(int slot, Client client) throws RemoteException {
        return mQtiRadioProxy.queryNrIconType(slot, client);
    }

    public Token queryEndcStatus(int slot, Client client) throws RemoteException {
        return mQtiRadioProxy.queryEndcStatus(slot, client);
    }

    public Token setNrConfig(int slot, NrConfig config, Client client) throws RemoteException {
        return mQtiRadioProxy.setNrConfig(slot, config, client);
    }

    public Token queryNrConfig(int slot, Client client) throws RemoteException {
        return mQtiRadioProxy.queryNrConfig(slot, client);
    }

    public Token sendCdmaSms(int slot, byte[] pdu,
                             boolean expectMore, Client client) throws RemoteException {
        return mQtiRadioProxy.sendCdmaSms(slot, pdu, expectMore, client);
    }

    public Token enable5g(int slot, Client client) throws RemoteException {
        return mQtiRadioProxy.enable5g(slot, client);
    }

    public Token disable5g(int slot, Client client) throws RemoteException {
        return mQtiRadioProxy.disable5g(slot, client);
    }

    public Token queryNrBearerAllocation(int slot, Client client) throws RemoteException {
        return mQtiRadioProxy.queryNrBearerAllocation(slot, client);
    }

    public Token getQtiRadioCapability(int slot, Client client) throws RemoteException {
        return mQtiRadioProxy.getQtiRadioCapability(slot, client);
    }

    public Token enable5gOnly(int slot, Client client) throws RemoteException {
        return mQtiRadioProxy.enable5gOnly(slot, client);
    }

    public Token query5gStatus(int slot, Client client) throws RemoteException {
        return mQtiRadioProxy.query5gStatus(slot, client);
    }

    public Token queryNrDcParam(int slot, Client client) throws RemoteException {
        return mQtiRadioProxy.queryNrDcParam(slot, client);
    }

    public Token queryNrSignalStrength(int slot, Client client) throws RemoteException {
        return mQtiRadioProxy.queryNrSignalStrength(slot, client);
    }

    public Token queryUpperLayerIndInfo(int slot, Client client) throws RemoteException {
        return mQtiRadioProxy.queryUpperLayerIndInfo(slot, client);
    }

    public Token query5gConfigInfo(int slot, Client client) throws RemoteException {
        return mQtiRadioProxy.query5gConfigInfo(slot, client);
    }

    public Token setCarrierInfoForImsiEncryption(int slot, ImsiEncryptionInfo info,
                                                 Client client) throws RemoteException {
        return mQtiRadioProxy.setCarrierInfoForImsiEncryption(slot, info, client);
    }

    public void queryCallForwardStatus(int slotId, int cfReason, int serviceClass,
                String number, boolean expectMore, Client client) throws RemoteException {
        mQtiRadioProxy.queryCallForwardStatus(slotId, cfReason, serviceClass, number,
                expectMore, client);
    }

    public void getFacilityLockForApp(int slotId, String facility, String password,
                int serviceClass, String appId, boolean expectMore, Client client)
                throws RemoteException {
        mQtiRadioProxy.getFacilityLockForApp(slotId, facility, password, serviceClass,
                appId, expectMore, client);
    }

    public QtiImeiInfo[] getImeiInfo() throws RemoteException {
        if (mPrimaryImeiHandler == null) {
            Log.e(LOG_TAG, "getImeiInfo, not supported");
            return null;
        }
        return mPrimaryImeiHandler.getImeiInfo();
    }

    public boolean isSmartDdsSwitchFeatureAvailable() {
        return mQtiRadioProxy.isSmartDdsSwitchFeatureAvailable();
    }

    public void setSmartDdsSwitchToggle(boolean isEnabled, Client client) throws RemoteException {
        mQtiRadioProxy.setSmartDdsSwitchToggle(isEnabled, client);
    }

    public boolean isFeatureSupported(int feature) {
        return mQtiRadioProxy.isFeatureSupported(feature);
    }

    public Token sendUserPreferenceForDataDuringVoiceCall(int slotId,
            boolean userPreference, Client client) throws RemoteException {
        return mQtiRadioProxy.sendUserPreferenceForDataDuringVoiceCall(
                slotId, userPreference, client);
    }

    public Token getDdsSwitchCapability(int slotId, Client client) throws RemoteException {
        return mQtiRadioProxy.getDdsSwitchCapability(slotId, client);
    }

    public Client registerCallback(String packageName, IExtPhoneCallback callback)
            throws RemoteException {
        return mQtiRadioProxy.registerCallback(packageName, callback);
    }

    public void unRegisterCallback(IExtPhoneCallback callback) throws RemoteException {
        mQtiRadioProxy.unRegisterCallback(callback);
    }
}
