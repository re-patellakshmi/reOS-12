/******************************************************************************
 * ---------------------------------------------------------------------------
 *  Copyright (c) 2015-2017, 2020-2021 Qualcomm Technologies, Inc.
 *  All Rights Reserved.
 *  Confidential and Proprietary - Qualcomm Technologies, Inc.
 * ---------------------------------------------------------------------------
 *******************************************************************************/
package com.qualcomm.location.osagent;

import android.util.Log;
import android.util.Range;
import android.os.Looper;
import android.os.Handler;
import android.os.Message;
import android.os.Build;
import android.os.UserHandle;
import android.os.SystemProperties;
import android.os.BatteryManager;
import android.os.IBinder;
import android.os.Binder;
import android.os.UserHandle;
import android.location.LocationManager;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import java.util.TimeZone;
import java.util.Date;
import java.util.Properties;
import java.util.regex.PatternSyntaxException;
import java.util.concurrent.Executor;
import java.io.FileInputStream;
import android.content.Context;
import android.content.ContentQueryMap;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.database.Cursor;
import android.database.ContentObserver;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.provider.Settings.Global;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.provider.Telephony.Sms.Intents;
import android.app.ActivityManager;

import android.telephony.CellLocation;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoCdma;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityCdma;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.telephony.cdma.CdmaCellLocation;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.TelephonyIntents;
import android.telephony.SubscriptionManager;
import android.telephony.SubscriptionInfo;
import android.text.TextUtils;
import android.telephony.SubscriptionManager.OnSubscriptionsChangedListener;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;

import com.qti.izat.XTProxy;
import com.qualcomm.location.izat.IzatService;
import com.qualcomm.location.izat.IzatService.ISystemEventListener;
import com.qualcomm.location.izatprovider.IzatProvider;

import android.location.Location;
import android.os.RemoteException;
import vendor.qti.gnss.V1_1.ILocHidlGnss;
import vendor.qti.gnss.V1_1.ILocHidlIzatSubscription;
import vendor.qti.gnss.V1_1.ILocHidlIzatSubscriptionCallback;
import vendor.qti.gnss.V1_1.LocHidlSubscriptionDataItemId_1_1;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import com.qualcomm.location.idlclient.*;
import com.qualcomm.location.idlclient.LocIDLClientBase.*;
import com.qualcomm.location.GpsNetInitiatedHandler;
import com.qualcomm.location.utils.IZatServiceContext;
import vendor.qti.gnss.ILocAidlGnss;
import vendor.qti.gnss.ILocAidlIzatSubscription;
import vendor.qti.gnss.ILocAidlIzatSubscriptionCallback;
import vendor.qti.gnss.LocAidlBoolDataItem;
import vendor.qti.gnss.LocAidlStringDataItem;
import vendor.qti.gnss.LocAidlNetworkInfoDataItem;
import vendor.qti.gnss.LocAidlRilServiceInfoDataItem;
import vendor.qti.gnss.LocAidlCellCdmaDataItem;
import vendor.qti.gnss.LocAidlCellLteDataItem;
import vendor.qti.gnss.LocAidlCellGwDataItem;
import vendor.qti.gnss.LocAidlCellOooDataItem;
import vendor.qti.gnss.LocAidlServiceStateDataItem;
import vendor.qti.gnss.LocAidlScreenStatusDataItem;
import vendor.qti.gnss.LocAidlPowerConnectStatusDataItem;
import vendor.qti.gnss.LocAidlTimeZoneChangeDataItem;
import vendor.qti.gnss.LocAidlTimeChangeDataItem;
import vendor.qti.gnss.LocAidlWifiSupplicantStatusDataItem;
import vendor.qti.gnss.LocAidlBatteryLevelDataItem;
import vendor.qti.gnss.LocAidlBtLeDeviceScanDetailsDataItem;
import vendor.qti.gnss.LocAidlBtDeviceScanDetailsDataItem;

public class OsAgent
{
    private static OsAgent mInstance;
    private static final String TAG = "OsAgent";
    private static final Object mLock = new Object();

    public static OsAgent GetInstance(Context context, Looper looperObj) {
        synchronized (mLock) {
            if (null == mInstance) {
                mInstance = new OsAgent(context, looperObj);
            }
        }
        return mInstance;
    }
    private OsAgent(Context context, Looper looperObj)
    {
        logv("OSAgent constructor");

        mContext = context;
        // initialize the msg handler
        mHandler = new Handler(looperObj, m_handler_callback);

        mTelephonyMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        mConnectivityMgr = (ConnectivityManager) mContext.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        mLocationMgr = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        mActivityMgr = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        // OsAgent init operations
        Message msgInit = Message.obtain(mHandler, MSG_OSAGENT_INIT);
        mHandler.sendMessage(msgInit);
    }

    public void subscribe(int[] dataItemArray)
    {
        logv("OSAgent subscribe.... +");

        if (dataItemArray == null) {
            loge("dataItemArray received is NULL");
            return;
        }

        int[] dataItemsList = (int[]) dataItemArray;
        for (int index = 0; index < dataItemArray.length ; ++index)
        {
            logd("OSAgent subscribe:: " + dataItemsList[index]);
        }

        Message msgObj = Message.obtain(mHandler, MSG_DATAITEM_SUBSCRIBE, dataItemArray);
        mHandler.sendMessage(msgObj);
    }

    public void requestData(int[] dataItemArray)
    {
        logv("OSAgent request data.... +");

        if (dataItemArray == null) {
            loge("dataItemArray received is NULL");
            return;
        }

        int[] dataItemsList = (int[]) dataItemArray;
        for (int index = 0; index < dataItemArray.length ; ++index)
        {
            logd("OSAgent request data:: " + dataItemsList[index]);
        }

        Message msgObj = Message.obtain(mHandler, MSG_DATAITEM_REQUEST_DATA, dataItemArray);
        mHandler.sendMessage(msgObj);
    }

    public void unsubscribe(int[] dataItemArray)
    {
       logv("OSAgent unsubscribe.... +");

        if (dataItemArray == null) {
            loge("dataItemArray received is NULL");
            return;
        }

        int[] dataItemsList = (int[]) dataItemArray;
        for (int index = 0; index < dataItemArray.length ; ++index)
        {
            logd("OSAgent unsubscribe:: " + dataItemsList[index]);
        }

        Message msgObj = Message.obtain(mHandler, MSG_DATAITEM_UNSUBSCRIBE, dataItemArray);
        mHandler.sendMessage(msgObj);
    }

    public void unsubscribeAll()
    {
        logv("OSAgent unsubscribeAll.... +");

        Message msgObj = Message.obtain(mHandler, MSG_DATAITEM_UNSUBSCRIBE_ALL);
        mHandler.sendMessage(msgObj);
    }

    public void turnOn(int dit, int timeOut)
    {
        logv("OSAgent turnOn+" + dit + " timeout " + timeOut);
        Message msgObj = Message.obtain(mHandler, MSG_FRAMEWORK_MODULE_TURNON, dit, timeOut);
        mHandler.sendMessage(msgObj);
    }

    public void turnOff(int dit)
    {
        logv("OSAgent turnOff+" + dit);
        Message msgObj = Message.obtain(mHandler, MSG_FRAMEWORK_MODULE_TURNOFF, dit, 0);
        mHandler.sendMessage(msgObj);
    }

    public void handleSubscribe(Object dataItemsArray)
    {
        int[] dataItems = (int[]) dataItemsArray;

        for (int index = 0; index < dataItems.length; ++index)
        {
            if (mDataItemList.contains(dataItems[index])) {
                // data item was already registered for
                continue;
            }

            // insert the data item
            mDataItemList.add(dataItems[index]);

            switch (dataItems[index])
            {
                case WIFIHARDWARESTATE_DATA_ITEM_ID:
                    mContentSettingsList.add(WIFIHARDWARESTATE_DATA_ITEM_ID);
                    // no break here, intentionally
                case AIRPLANEMODE_DATA_ITEM_ID:
                    // don't want to add the same item twice
                    if (!mContentSettingsList.contains(AIRPLANEMODE_DATA_ITEM_ID)) {
                        mContentSettingsList.add(AIRPLANEMODE_DATA_ITEM_ID);
                    }
                break;
                case ASSISTED_GPS_DATA_ITEM_ID:
                case ENH_DATA_ITEM_ID:
                case GPSSTATE_DATA_ITEM_ID:
                case NLPSTATUS_DATA_ITEM_ID:
                    mContentSettingsList.add(dataItems[index]);
                break;
                case SERVICESTATUS_DATA_ITEM_ID:
                    sendServiceStateInfo();
                break;
                case MODEL_DATA_ITEM_ID:
                    if (! Build.MODEL.equals(Build.UNKNOWN)) {
                        if (mIdlClient != null) {
                            mIdlClient.string_dataitem_update(MODEL_DATA_ITEM_ID, Build.MODEL);
                        }
                    }
                break;
                case MANUFACTURER_DATA_ITEM_ID:
                    if (! Build.MANUFACTURER.equals(Build.UNKNOWN)) {
                        if (mIdlClient != null) {
                            mIdlClient.string_dataitem_update(MANUFACTURER_DATA_ITEM_ID,
                                    Build.MANUFACTURER);
                        }
                    }
                break;

                case RIL_CELL_UPDATE_DATA_ITEM_ID:
                    sendCellUpdateInfo();
                break;

                case NETWORKINFO_DATA_ITEM_ID:
                {
                    // cache the connectivityManager service
                    if (mConnectivityMgr == null) {
                        loge("Fail to acquire connectivityManager service");
                        return;
                    }

                    IntentFilter filter = new IntentFilter();
                    filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                    mContext.registerReceiver(mNetworkConnChangeReceiver, filter, null, null);
                    updateConnectivityStatus();
                }
                break;

                case SCREEN_STATE_DATA_ITEM_ID:
                {
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(Intent.ACTION_SCREEN_ON);
                    filter.addAction(Intent.ACTION_SCREEN_OFF);
                    mContext.registerReceiver(mScreenStateChangeReceiver, filter, null, null);
                }
                break;

                case POWER_CONNECTED_STATE_DATA_ITEM_ID:
                {
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(Intent.ACTION_POWER_CONNECTED);
                    filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
                    mContext.registerReceiver(mPowerConnectChangeReceiver, filter, null, null);

                    // Get the current battery updates from a sticky intent. This is why a null
                    // broadcast receiver is allowed.
                    IntentFilter batteryFilter = new IntentFilter();
                    batteryFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
                    Intent batteryIntent = mContext.registerReceiver(null, batteryFilter);
                    int plugged = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                    boolean currentBatteryCharging = ((plugged == BatteryManager.BATTERY_PLUGGED_AC)
                                                      || (plugged == BatteryManager.BATTERY_PLUGGED_USB));
                    if (currentBatteryCharging) {
                        if (mIdlClient != null) {
                            mIdlClient.power_connect_status_update(true);
                        }
                    }
                }
                break;

                case BATTERY_LEVEL_DATA_ITEM_ID:
                {
                    IntentFilter filter = new IntentFilter();
                    logd("Registering for battery change notifications  ");
                    filter.addAction(Intent.ACTION_BATTERY_CHANGED);
                    mContext.registerReceiver(mBatteryLevelChangeReceiver, filter, null, null);

                    // Get the current battery updates from a sticky intent. This is why a null
                    // broadcast receiver is allowed.
                    IntentFilter batteryFilter = new IntentFilter();
                    batteryFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
                    Intent batteryIntent = mContext.registerReceiver(null, batteryFilter);
                    int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                    notifyBatteryPct(level, scale);
                }
                break;

                case TIMEZONE_CHANGE_DATA_ITEM_ID:
                {
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
                    mContext.registerReceiver(mTimeZoneChangeReceiver, filter, null, null);
                }
                break;

                case TIME_CHANGE_DATA_ITEM_ID:
                {
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(Intent.ACTION_TIME_CHANGED);
                    mContext.registerReceiver(mTimeChangeReceiver, filter, null, null);
                }
                break;

                case SHUTDOWN_STATE_DATA_ITEM_ID:
                {
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(Intent.ACTION_SHUTDOWN);
                    mContext.registerReceiver(mShutdownReceiver, filter, null, null);
                }
                break;

                case WIFI_SUPPLICANT_STATUS_DATA_ITEM_ID:
                {
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
                    mContext.registerReceiver(mWifiSupplicantStateChangeReceiver, filter, null, null);
                }
                break;

                case MCCMNC_DATA_ITEM_ID:
                {
                    if (mSubscriptionsChangedListener == null) {
                        mSubscriptionsChangedListener = new OnSubscriptionsChangedListener() {
                            @Override
                            public void onSubscriptionsChanged() {
                               updateMccmnc();
                            }
                        };

                        SubscriptionManager.from(mContext)
                            .addOnSubscriptionsChangedListener(mSubscriptionsChangedListener);
                    }

                    updateMccmnc();
                }
                break;
            }
        }

        if (mContentSettingsList.size() > 0) {
            subscribeForNewContentData();
        }
    }

    public void handleRequestData(Object dataItemsArray)
    {
        int[] dataItems = (int[]) dataItemsArray;

        for (int index = 0; index < dataItems.length; ++index)
        {
            switch (dataItems[index])
            {
                case RIL_SERVICE_INFO_DATA_ITEM_ID:
                    sendServiceInfo();
                break;

                case TIMEZONE_CHANGE_DATA_ITEM_ID:
                    sendTimeZoneInfo();
                break;

                case TIME_CHANGE_DATA_ITEM_ID:
                    sendTimeInfo();
                break;

                case TAC_DATA_ITEM_ID:
                    sendTac();
                break;
            }
        }
    }

    public void handleUnsubscribe(Object dataItemsArray)
    {
        int[] dataItems = (int[]) dataItemsArray;

        // populate the required items in these local arrays
        List<Integer> contentDataItemArray = new ArrayList<Integer> ();

        for (int index = 0; index < dataItems.length; ++index)
        {
            if (!mDataItemList.contains(dataItems[index])) {
                // dataitem was never added
                continue;
            }

            // remove the data item
            mDataItemList.remove((Integer)dataItems[index]);
            switch (dataItems[index])
            {
                case AIRPLANEMODE_DATA_ITEM_ID:
                case WIFIHARDWARESTATE_DATA_ITEM_ID:
                case ASSISTED_GPS_DATA_ITEM_ID:
                case ENH_DATA_ITEM_ID:
                case GPSSTATE_DATA_ITEM_ID:
                case NLPSTATUS_DATA_ITEM_ID:
                    contentDataItemArray.add(dataItems[index]);
                break;

                case RIL_CELL_UPDATE_DATA_ITEM_ID:
                    // unregister from listening for cell location changes, only listen for service state changes.
                    // because TelephonyManager does not provide an API to get the current service state.
                    mTelephonyMgr.listen(mRilListener, PhoneStateListener.LISTEN_SERVICE_STATE);
                break;

                case NETWORKINFO_DATA_ITEM_ID:
                    mContext.unregisterReceiver(mNetworkConnChangeReceiver);
                break;
                case SCREEN_STATE_DATA_ITEM_ID:
                    mContext.unregisterReceiver(mScreenStateChangeReceiver);
                break;
                case POWER_CONNECTED_STATE_DATA_ITEM_ID:
                    mContext.unregisterReceiver(mPowerConnectChangeReceiver);
                break;
                case BATTERY_LEVEL_DATA_ITEM_ID:
                    mContext.unregisterReceiver(mBatteryLevelChangeReceiver);
                break;
                case TIMEZONE_CHANGE_DATA_ITEM_ID:
                    mContext.unregisterReceiver(mTimeZoneChangeReceiver);
                break;
                case TIME_CHANGE_DATA_ITEM_ID:
                    mContext.unregisterReceiver(mTimeChangeReceiver);
                break;
                case SHUTDOWN_STATE_DATA_ITEM_ID:
                    mContext.unregisterReceiver(mShutdownReceiver);
                break;
                case WIFI_SUPPLICANT_STATUS_DATA_ITEM_ID:
                    mContext.unregisterReceiver(mWifiSupplicantStateChangeReceiver);
                case MCCMNC_DATA_ITEM_ID:
                    if (mSubscriptionsChangedListener != null) {
                        SubscriptionManager.from(mContext)
                            .removeOnSubscriptionsChangedListener(mSubscriptionsChangedListener);

                        mSubscriptionsChangedListener = null;
                    }
                break;
            }
        }

        if (contentDataItemArray.size() > 0) {
            removeUpdateForContentData(contentDataItemArray);
        }
    }

    public void handleUnsubscribeAll()
    {
        int size = mDataItemList.size();

        int[] dataItemArray = new int[size];

        for (int i = 0; i < size; i++) {
            dataItemArray[i] = mDataItemList.get(i).intValue();
        }

        handleUnsubscribe(dataItemArray);
        mDataItemList.clear();
        mContentSettingsList.clear();
    }

    public void handleModuleTurnOn(int dit, int timeOut)
    {
        logv("OSAgent handleModuleTurnOn+" + dit + " timeout " + timeOut);
        switch (dit)
        {
            case BTLE_SCAN_DATA_ITEM_ID:
            {
                // Start BTLE scan using Android BT api's
                handleBleScan(true);
            }
            break;

            case BT_SCAN_DATA_ITEM_ID:
            {
                // Start Classic BT device scan using Android BT api's
                handleClassicBtDevScan(true);
            }
            break;

            default:
            break;
        }
    }

    public void handleModuleTurnOff(int dit)
    {
        logv("OSAgent handleModuleTurnOff+" + dit);
        switch (dit)
        {
            case BTLE_SCAN_DATA_ITEM_ID:
            {
                // Stop BTLE scan
                handleBleScan(false);
            }
            break;

            case BT_SCAN_DATA_ITEM_ID:
            {
                // Stop Classic BT device scan
                handleClassicBtDevScan(false);
            }
            break;

            default:
            break;
        }
    }

    private void subscribeForNewContentData()
    {
        Iterator iterList = mContentSettingsList.iterator();
        List<Integer> dataItemListForUpdate = new ArrayList<Integer>();

        if (mContentObserver == null) {
            mContentObserver = new DataItemContentObserver(null);
        }

        while (iterList.hasNext()) {
            int newdataItem = (int) iterList.next();

            switch (newdataItem)
            {
                case AIRPLANEMODE_DATA_ITEM_ID:
                    Uri airplaneModeUri = Settings.Global.getUriFor(Settings.Global.AIRPLANE_MODE_ON);
                    if (airplaneModeUri != null) {
                        mContext.getContentResolver().registerContentObserver(
                            airplaneModeUri, true, mContentObserver, UserHandle.USER_ALL);
                        dataItemListForUpdate.add(AIRPLANEMODE_DATA_ITEM_ID);
                    }  else {
                        loge("getUriFor(AIRPLANE_MODE_ON) returned null");
                    }
                break;
                case WIFIHARDWARESTATE_DATA_ITEM_ID:
                    Uri wifiOnUri = Settings.Global.getUriFor(Settings.Global.WIFI_ON);
                    Uri wifiScanAlwaysAvailableUri = Settings.Global.getUriFor(Settings.Global.WIFI_SCAN_ALWAYS_AVAILABLE);

                    if ((wifiOnUri != null) && (wifiScanAlwaysAvailableUri != null)) {
                        mContext.getContentResolver().registerContentObserver(wifiOnUri,
                            true, mContentObserver, UserHandle.USER_ALL);
                        mContext.getContentResolver().registerContentObserver(wifiScanAlwaysAvailableUri,
                            true, mContentObserver, UserHandle.USER_ALL);
                        dataItemListForUpdate.add(WIFIHARDWARESTATE_DATA_ITEM_ID);
                    } else {
                      if (wifiOnUri == null) {
                        loge("getUriFor(WIFI_ON) returned NULL");
                      }
                      if (wifiScanAlwaysAvailableUri == null) {
                        loge("getUriFor(WIFI_SCAN_ALWAYS_AVAILABLE) returned null");
                      }
                    }
                break;
                case ASSISTED_GPS_DATA_ITEM_ID:
                    Uri assistedGpsUri = Settings.Global.getUriFor(Settings.Global.ASSISTED_GPS_ENABLED);
                    if (assistedGpsUri != null) {
                        mContext.getContentResolver().registerContentObserver(assistedGpsUri,
                            true, mContentObserver, UserHandle.USER_ALL);
                        dataItemListForUpdate.add(ASSISTED_GPS_DATA_ITEM_ID);
                    } else {
                        loge("getUriFor(ASSISTED_GPS_ENABLED) returned null");
                    }
                break;
                case ENH_DATA_ITEM_ID:
                    Uri eulaUri = Settings.Secure.getUriFor(ENH_LOCATION_SERVICES_ENABLED);
                    if (eulaUri != null) {
                        mContext.getContentResolver().registerContentObserver(
                            eulaUri, true, mContentObserver, UserHandle.USER_ALL);
                        dataItemListForUpdate.add(ENH_DATA_ITEM_ID);
                    } else {
                        loge("getUriFor(ENH_LOCATION_SERVICES_ENABLED) returned null");
                    }
                break;
                case GPSSTATE_DATA_ITEM_ID:
                case NLPSTATUS_DATA_ITEM_ID:
                    Uri gpsStateUri = Settings.Secure.getUriFor(Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                    if (gpsStateUri != null) {
                        mContext.getContentResolver().registerContentObserver(gpsStateUri,
                            true, mContentObserver, UserHandle.USER_ALL);
                        dataItemListForUpdate.add(newdataItem);
                    } else {
                        loge("getUriFor(LOCATION_PROVIDERS_ALLOWED) returned null");
                    }
                break;
                default:
                    loge("Unsupported data item");

            }
        }

        if (dataItemListForUpdate.size() > 0) {
            updateContentData(dataItemListForUpdate);
        }
    }

    private void updateContentData(List<Integer> updateDataItemList)
    {
        if (mContentSettingsList.size() == 0) {
            return;
        }

        // if we have received a list of data items to update,
        // then we update only those, else we update all
        List<Integer> list = (updateDataItemList != null) ?
                updateDataItemList : mContentSettingsList;

        Iterator iterList = list.iterator();
        int idxDataItem = 0;
        int size = list.size();
        int[] dataItemArray = new int[size];
        boolean[] dataItemValueArray = new boolean[size];

        while (iterList.hasNext()) {
            int newdataItem = (int) iterList.next();

            if (list != mContentSettingsList) {
                if (!mContentSettingsList.contains(newdataItem)) {
                    continue;
                }
            }

            switch (newdataItem)
            {
                case AIRPLANEMODE_DATA_ITEM_ID:
                {
                    String airplaneModeStr = Settings.Global.getStringForUser(
                        mContext.getContentResolver(),
                        Settings.Global.AIRPLANE_MODE_ON, mCurrentUserId);

                    if (airplaneModeStr != null) {
                        logd( "Airplane Mode: " + airplaneModeStr);

                        boolean airplaneMode = airplaneModeStr.contains("1");
                        dataItemArray[idxDataItem] = AIRPLANEMODE_DATA_ITEM_ID;
                        dataItemValueArray[idxDataItem] = airplaneMode;
                    } else {
                        loge("getString for AIRPLANE_MODE_ON returned null");
                    }
                }
                break;
                case WIFIHARDWARESTATE_DATA_ITEM_ID:
                    boolean wifiState = false;

                    String wifiHardwareStateStr = Settings.Global.getStringForUser(
                        mContext.getContentResolver(),
                        Settings.Global.WIFI_ON, mCurrentUserId);
                    String wifiScanAlwaysAvailableStr = Settings.Global.getStringForUser(
                        mContext.getContentResolver(),
                        Settings.Global.WIFI_SCAN_ALWAYS_AVAILABLE, mCurrentUserId);

                    if (wifiHardwareStateStr != null){
                        String airplaneModeStr = Settings.Global.getStringForUser(
                            mContext.getContentResolver(),
                            Settings.Global.AIRPLANE_MODE_ON, mCurrentUserId);

                        logd("Wifi Hardware State: " + wifiHardwareStateStr +
                             "; airplane mode: " + airplaneModeStr);
                        wifiState = !(wifiHardwareStateStr.contains("0") ||
                                      (wifiHardwareStateStr.contains("3") &&
                                       airplaneModeStr != null &&
                                       airplaneModeStr.contains("1")));
                    } else {
                        loge("getString for Settings.Global.WIFI_ON returned null");
                    }

                    if (wifiScanAlwaysAvailableStr != null) {
                        loge(" Wifi Scan Always Available: " + wifiScanAlwaysAvailableStr);
                        wifiState |= wifiScanAlwaysAvailableStr.contains("1");
                    } else {
                        loge("getString for WIFI_SCAN_ALWAYS_AVAILABLE returned null");
                    }

                    if ((wifiHardwareStateStr != null) || (wifiScanAlwaysAvailableStr != null)) {
                        dataItemArray[idxDataItem] = WIFIHARDWARESTATE_DATA_ITEM_ID;
                        dataItemValueArray[idxDataItem] = wifiState;
                    }
                break;
                case ASSISTED_GPS_DATA_ITEM_ID:
                    String assistedGpsStr = Settings.Global.getStringForUser(
                        mContext.getContentResolver(),
                        Settings.Global.ASSISTED_GPS_ENABLED, mCurrentUserId);
                    if (assistedGpsStr != null) {
                        logd("Assisted Gps: " + assistedGpsStr);

                        boolean assistedGps = assistedGpsStr.contains("1");
                        dataItemArray[idxDataItem] = ASSISTED_GPS_DATA_ITEM_ID;
                        dataItemValueArray[idxDataItem] = assistedGps;
                    } else {
                        loge("getStringForUser ASSISTED_GPS_ENABLED returned null");
                    }
                break;
                case ENH_DATA_ITEM_ID:
                    int eula_state = Settings.Secure.getIntForUser(
                        mContext.getContentResolver(),
                        ENH_LOCATION_SERVICES_ENABLED, -1, mCurrentUserId);

                    if ( mEulaState != eula_state) {
                        boolean eulaState = (eula_state == 0);
                        dataItemArray[idxDataItem] = ENH_DATA_ITEM_ID;
                        dataItemValueArray[idxDataItem] = eulaState;
                        // call setEnable if change in consent!
                        boolean consentAccepted = (eula_state & FEATURE_DISABLED_BY_CONSET) == 0;
                        boolean consentChanged = ( (eula_state & FEATURE_DISABLED_BY_CONSET) !=
                                                   (mEulaState & FEATURE_DISABLED_BY_CONSET) );

                        if (consentChanged && IzatProvider.hasNetworkProvider()) {
                            IzatProvider.getNetworkProvider(mContext).setUserConsent(
                                    consentAccepted);
                        }

                        mEulaState = eula_state;
                    }
                    else {
                        continue;
                    }
                break;
                case GPSSTATE_DATA_ITEM_ID:
                    String gpsStateStr = Settings.Secure.getStringForUser(
                        mContext.getContentResolver(),
                        Settings.Secure.LOCATION_PROVIDERS_ALLOWED, mCurrentUserId);
                    if (gpsStateStr != null) {
                        logd("Gps state: " + gpsStateStr);

                        boolean gpsState = gpsStateStr.contains("gps");
                        dataItemArray[idxDataItem] = GPSSTATE_DATA_ITEM_ID;
                        dataItemValueArray[idxDataItem] = gpsState;
                    } else {
                        loge("getStringForUser LOCATION_PROVIDERS_ALLOWED returned null");
                    }
                break;
                case NLPSTATUS_DATA_ITEM_ID:
                    String nlpStateStr = Settings.Secure.getStringForUser(
                        mContext.getContentResolver(),
                        Settings.Secure.LOCATION_PROVIDERS_ALLOWED, mCurrentUserId);
                    if (nlpStateStr != null) {
                        logd("NlpState: " + nlpStateStr);

                        boolean nlpState = nlpStateStr.contains("network");
                        dataItemArray[idxDataItem] = NLPSTATUS_DATA_ITEM_ID;
                        dataItemValueArray[idxDataItem] = nlpState;
                    } else {
                        loge("getStringForUser LOCATION_PROVIDERS_ALLOWED returned null");
                    }
                break;
            }

            idxDataItem++;
        }

        if (idxDataItem > 0) {
            // Note: assumption is that all data items values are boolean
            if (mIdlClient != null) {
                mIdlClient.bool_dataitem_update(dataItemArray, dataItemValueArray);
            }
        }
    }

    private void removeUpdateForContentData(List<Integer> removeDataItemList)
    {
        Iterator iterList = removeDataItemList.iterator();

        // remove the items from the content list
        while (iterList.hasNext()) {
            int dataItemToRemove = (int) iterList.next();
            mContentSettingsList.remove((Integer)dataItemToRemove);
        }

        // unregister the ContentObserver
        mContext.getContentResolver().unregisterContentObserver(mContentObserver);
        mContentObserver = null;

        if (mContentSettingsList.size() > 0) {
            // add back update for remaining data items
            subscribeForNewContentData();
        }
    }

    private final BroadcastReceiver mNetworkConnChangeReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                mHandler.obtainMessage(MSG_CONNECT_STATE_CHANGED).sendToTarget();
            }
        }
    };

    private final BroadcastReceiver mScreenStateChangeReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {

            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_ON)) {
                logd("Action screen state change:ON");
                mHandler.obtainMessage(MSG_SCREEN_STATE_CHANGED, true).sendToTarget();
            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                logd("Action screen state change:OFF");
                mHandler.obtainMessage(MSG_SCREEN_STATE_CHANGED, false).sendToTarget();
            }
        }
    };

    private final BroadcastReceiver mPowerConnectChangeReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
                logd("Action power connect:ON");
                mHandler.obtainMessage(MSG_POWER_STATE_CHANGED, true).sendToTarget();
            } else if (action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
                logd("Action power connect:OFF");
                mHandler.obtainMessage(MSG_POWER_STATE_CHANGED, false).sendToTarget();
            }
        }
    };

    private final BroadcastReceiver mBatteryLevelChangeReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                logd("Action Battery changed");
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                mHandler.obtainMessage(MSG_BATTERY_LEVEL_CHANGED, level, scale).sendToTarget();
            }

        }
    };

    private final BroadcastReceiver mTimeZoneChangeReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                mHandler.obtainMessage(MSG_TIME_ZONE_CHANGED).sendToTarget();
            }
        }
    };

    private final BroadcastReceiver mTimeChangeReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_CHANGED)) {
                mHandler.obtainMessage(MSG_TIME_CHANGED).sendToTarget();
            }
        }
    };

    private final BroadcastReceiver mShutdownReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SHUTDOWN)) {
                logd("Action shutdown fired");
                mHandler.obtainMessage(MSG_SHUT_DOWN_CHANGED).sendToTarget();
            }
        }
    };

    private final BroadcastReceiver mUserChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + intent.getAction());
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_USER_SWITCHED)) {
                mCurrentUserId = intent.getIntExtra(Intent.EXTRA_USER_HANDLE,
                        UserHandle.USER_CURRENT);
                logd("Action user switched: " + mCurrentUserId);

                // fetch all the data and send out.
                mHandler.obtainMessage(MSG_CONTENT_DATA_CHANGED, null).sendToTarget();
                mHandler.postDelayed(()->setRegulatedFeatureAllowed(), 5000);
            }

            mHandler.post(()->broadcastSystemEvent(
                        ISystemEventListener.MSG_USER_SWITCH_ACTION_UPDATE, intent));
        }
    };

    private final BroadcastReceiver mProviderChangedReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            logd("Received PROVIDERS_CHANGED_ACTION intent: " + intent.toString());
            XTProxy.setModeChanged(context, mCurrentUserId);
            boolean isLocationSettingsOn =
                    mLocationMgr.isLocationEnabledForUser(Binder.getCallingUserHandle());
            mHandler.post(()->broadcastSystemEvent(ISystemEventListener.MSG_LOCATION_MODE_CHANGE,
                    isLocationSettingsOn));
        }
    };

    private final ActivityManager.OnUidImportanceListener mUidImportanceListener =
            new ActivityManager.OnUidImportanceListener() {
                @Override
                public void onUidImportance(int uid, int importance) {
                    boolean isForegroundUid =
                            importance <= FOREGROUND_IMPORTANCE_CUTOFF;
                    mHandler.post(()->broadcastSystemEvent(
                            ISystemEventListener.MSG_UID_IMPORTANCE_CHANGE, uid, isForegroundUid));
                }
    };

    private final BroadcastReceiver mWifiSupplicantStateChangeReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
                mHandler.obtainMessage(MSG_WIFI_STATE_CHANGED, intent).sendToTarget();
            }
        }
    };

    // listening for emergency call begins, android.intent.action.NEW_OUTGOING_CALL
    private final BroadcastReceiver mOutgoingCallReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + intent.getAction());
            mHandler.post(()->broadcastSystemEvent(ISystemEventListener.MSG_OUTGOING_CALL, intent));
        }
    };

    // Used to detect when NI request is received, android.intent.action.NETWORK_INITIATED_VERIFY
    private final BroadcastReceiver mNetInitiatedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + intent.getAction());
            mHandler.post(()->broadcastSystemEvent(ISystemEventListener.MSG_NET_INITIATED, intent));
        }
    };

    // listen for package removed intent, android.intent.action.PACKAGE_REMOVED
    private final BroadcastReceiver mPackageRemovedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context conext, Intent intent) {
            Log.d(TAG, "Package uninstalled, onReceive: " +
                    intent.getData().getSchemeSpecificPart());
            mHandler.post(()->broadcastSystemEvent(ISystemEventListener.MSG_PKG_REMOVED, intent));
        }
    };

    //listen for RIL info related intents, Intents.DATA_SMS_RECEIVED_ACTION,
    //Intents.WAP_PUSH_RECEIVED_ACTION and TelephonyIntents.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED
    private final BroadcastReceiver mRilInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mHandler.post(()->broadcastSystemEvent(ISystemEventListener.MSG_RIL_INFO, intent));
        }
    };


    private DataItemContentObserver mContentObserver;
    public class DataItemContentObserver extends ContentObserver {

        DataItemContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            try {
                logd("onChange called for: " + uri.toString() + " user: " + mCurrentUserId);
                List<Integer> dataItemList = new ArrayList<Integer>();

                if (uri.compareTo(
                        Settings.Secure.getUriFor(Settings.Secure.LOCATION_PROVIDERS_ALLOWED)) == 0) {

                    if (mDataItemList.contains(GPSSTATE_DATA_ITEM_ID)) {
                        dataItemList.add(GPSSTATE_DATA_ITEM_ID);
                    }

                    if (mDataItemList.contains(NLPSTATUS_DATA_ITEM_ID)) {
                        dataItemList.add(NLPSTATUS_DATA_ITEM_ID);
                    }
                } else if (uri.compareTo(
                    Settings.Secure.getUriFor(ENH_LOCATION_SERVICES_ENABLED)) == 0) {
                    dataItemList.add(ENH_DATA_ITEM_ID);
                }
                else if (uri.compareTo(
                    Settings.Global.getUriFor(Settings.Global.AIRPLANE_MODE_ON)) == 0) {
                    dataItemList.add(AIRPLANEMODE_DATA_ITEM_ID);
                    dataItemList.add(WIFIHARDWARESTATE_DATA_ITEM_ID);
                }
                else if (uri.compareTo(
                    Settings.Global.getUriFor(Settings.Global.ASSISTED_GPS_ENABLED)) == 0) {
                    dataItemList.add(ASSISTED_GPS_DATA_ITEM_ID);
                }
                else if ((uri.compareTo(
                    Settings.Global.getUriFor(Settings.Global.WIFI_ON)) == 0) ||
                    (uri.compareTo(
                    Settings.Global.getUriFor(Settings.Global.WIFI_SCAN_ALWAYS_AVAILABLE)) == 0)) {
                    dataItemList.add(WIFIHARDWARESTATE_DATA_ITEM_ID);
                }

                if (dataItemList.size() > 0) {
                    mHandler.obtainMessage(MSG_CONTENT_DATA_CHANGED, dataItemList).sendToTarget();
                }
           } catch (NullPointerException e) {
                loge("getUriFor returned a NULL");
                e.printStackTrace();
           }
        }
    };

    private void sendTimeInfo()
    {
        TimeZone tz = TimeZone.getDefault();
        long currentTimeMillis = System.currentTimeMillis();
        int rawOffset = tz.getRawOffset()/1000;
        int dstSavings = tz.getDSTSavings()/1000;

        logd(String.format("Action time changed (%d, %d, %d)",
                           currentTimeMillis, rawOffset, dstSavings));

        if (mIdlClient != null) {
            mIdlClient.time_change_update(currentTimeMillis, rawOffset, dstSavings);
        }
    }

    private void sendTimeZoneInfo()
    {
        TimeZone tz = TimeZone.getDefault();
        long currentTimeMillis = System.currentTimeMillis();
        int rawOffset = tz.getRawOffset()/1000;
        int dstSavings = tz.getDSTSavings()/1000;

        logd(String.format("Action timezone changed (%d, %d, %d)",
                           currentTimeMillis, rawOffset, dstSavings));

        if (mIdlClient != null) {
            mIdlClient.timezone_change_update(currentTimeMillis, rawOffset,
                    dstSavings);
        }
    }

    private void updateConnectivityStatus()
    {
        NetworkInfo active_network_info;
        String type_name = "";
        String subtype_name = "";
        boolean is_available = false;
        boolean is_connected = false;
        boolean is_roaming = false;
        int type = 300;

        active_network_info = mConnectivityMgr.getActiveNetworkInfo();
        if (active_network_info != null) {
            // some default active network info is available

            switch(active_network_info.getType())
            {
                 // 'most probably free' types of connection
                 case ConnectivityManager.TYPE_WIFI:
                   type = 100;
                   break;
                 case ConnectivityManager.TYPE_ETHERNET:
                   type = 101;
                   break;
                 case ConnectivityManager.TYPE_BLUETOOTH:
                  type = 102;
                  break;

                // 'most probably not free' types of connection
                 case ConnectivityManager.TYPE_MOBILE:
                   type = 201;
                    break;
                 case ConnectivityManager.TYPE_MOBILE_DUN:
                  type = 202;
                  break;
                 case ConnectivityManager.TYPE_MOBILE_HIPRI:
                   type = 203;
                  break;
                 case ConnectivityManager.TYPE_MOBILE_MMS:
                   type = 204;
                   break;
                 case ConnectivityManager.TYPE_MOBILE_SUPL:
                    type = 205;
                    break;
                 case ConnectivityManager.TYPE_WIMAX:
                   type = 220;
                    break;

                 // 'unknown' types of connection
                 case ConnectivityManager.TYPE_NONE:
                 case ConnectivityManager.TYPE_DUMMY:
                  default:
                    type = 300;
                  break;
            }

            if (active_network_info.getTypeName() != null) {
                type_name = active_network_info.getTypeName();
            }

            if (active_network_info.getSubtypeName() != null) {
                subtype_name = active_network_info.getSubtypeName();
            }

            is_available = active_network_info.isAvailable();
            is_connected = active_network_info.isConnected();
            is_roaming = active_network_info.isRoaming();
        }

        if (mIdlClient != null) {
            mIdlClient.networkinfo_update(is_connected, type, type_name, subtype_name,
                    is_available, is_roaming);
        }
    }

    private void updateWiFiSupplicantState(Intent intent) {
        SupplicantState supplicantState =
            (SupplicantState)intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
        if (supplicantState == null) {
            loge("WifiManager.EXTRA_NEW_STATE returned null");
            return;
        }

        int i = 0;
        Integer val = 0;
        final int MAC_ADDR_LENGTH = 6;
        final int SSID_LENGTH = 32;

        byte apMacAddress[] = new byte[MAC_ADDR_LENGTH];
        int isAPMacAddressValid = 0;
        int isSSIDValid = 0;
        char ssid[] = null;

        logd("SUPPLICANT_STATE:" + supplicantState.name());

        if (supplicantState == (SupplicantState.COMPLETED)) {
            WifiManager wifiMgr = (WifiManager)mContext.getSystemService(mContext.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            if (wifiInfo != null)
            {
                try {
                    logv("Connection info - BSSID - " + wifiInfo.getBSSID()
                              + " - SSID - " + wifiInfo.getSSID() + "\n");
                    String[] bssid =  wifiInfo.getBSSID().split(":");

                    for(i = 0;i<MAC_ADDR_LENGTH; i++){
                        val = Integer.parseInt(bssid[i],16);
                        apMacAddress[i] = val.byteValue();
                    }
                    isAPMacAddressValid = 1;

                    ssid = wifiInfo.getSSID().toCharArray();
                    if((ssid != null) && (wifiInfo.getSSID().length() <= SSID_LENGTH))
                    {
                       logd("ssid string is valid");
                       isSSIDValid = 1;
                    }
                    else
                    {
                        logd("ssid string is invalid");
                        isSSIDValid = 0;
                    }

                } catch (NumberFormatException e) {
                        loge("Unable to parse mac address");
                }
                catch (NullPointerException e) {
                        loge("Unable to get BSSID/SSID");
                }
            }
        }

        if (mIdlClient != null) {
            mIdlClient.wifi_supplicant_status_update(supplicantState.ordinal(),
                    isAPMacAddressValid, apMacAddress, isSSIDValid, ssid);
        }
    }

    private void installRilListener()
    {
        if (null == mTelephonyMgr)
        {
          loge("Unable to get TELEPHONY_SERVICE");
          return;
        }

        mRilListener = new RilListener(mContext.getMainExecutor());
        mTelephonyMgr.listen(mRilListener,
            PhoneStateListener.LISTEN_CELL_LOCATION | PhoneStateListener.LISTEN_SERVICE_STATE);

    }

    private void installModeChangeReceiver()
    {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        mContext.registerReceiver(mProviderChangedReceiver, intentFilter, null, null);

        logd("Registered for PROVIDERS_CHANGED_ACTION");
    }

    private void installUserSwitchActionReceiver()
    {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_USER_SWITCHED);

        // use sticky broadcast receiver first to get the current user.
        Intent userIntent = mContext.registerReceiverAsUser(null, UserHandle.ALL, intentFilter,
                null, null);
        if (userIntent != null) {
            mCurrentUserId = userIntent.getIntExtra(Intent.EXTRA_USER_HANDLE,
                    UserHandle.USER_CURRENT);
        }

        // register Receiver for ACTION_USER_SWITCHED
        mContext.registerReceiverAsUser(mUserChangeReceiver, UserHandle.ALL, intentFilter,
                null, null);

        logd("Registered for ACTION_USER_SWITCHED CurrentUserId = " + mCurrentUserId);
    }

    private void installOutgoingCallReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        mContext.registerReceiver(mOutgoingCallReceiver, intentFilter, null, null);

        logd("Registered for NEW_OUTGOING_CALL");
    }

    private void installNetInitiatedReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GpsNetInitiatedHandler.ACTION_NI_VERIFY);
        mContext.registerReceiver(mNetInitiatedReceiver, intentFilter, null, null);

        logd("Registered for NETWORK_INITIATED_VERIFY");
    }

    private void installPackageRemovedReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        mContext.registerReceiver(mPackageRemovedReceiver, intentFilter, null, null);

        logd("Registered for ACTION_PACKAGE_REMOVED");
    }

    private void installRilReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intents.DATA_SMS_RECEIVED_ACTION);
        filter.addDataScheme("sms");
        filter.addDataAuthority("localhost","7275");
        mContext.registerReceiver(mRilInfoReceiver, filter);

        filter = new IntentFilter();
        filter.addAction(Intents.WAP_PUSH_RECEIVED_ACTION);
        try {
            filter.addDataType("application/vnd.omaloc-supl-init");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            Log.w(TAG, "Malformed SUPL init mime type");
        }
        mContext.registerReceiver(mRilInfoReceiver, filter);

        //Add listener for subscription change if this is a multisim device
        if(mTelephonyMgr.isMultiSimEnabled()) {
            logv("Multi Sim config detected");
            mContext.registerReceiver(mRilInfoReceiver,
                    new IntentFilter(TelephonyIntents.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED));
        }
    }

    private void installUidImportanceReceiver() {
        final long origId = Binder.clearCallingIdentity();
        mActivityMgr.addOnUidImportanceListener(mUidImportanceListener,
                FOREGROUND_IMPORTANCE_CUTOFF);
        Binder.restoreCallingIdentity(origId);
    }

    private void initAutoEula()
    {
        FileInputStream fileStream = null;
        final String gtpConfigFile = "/system/vendor/etc/xtwifi.conf";

        try {
            Properties config = new Properties();
            fileStream = new FileInputStream(gtpConfigFile);
            config.load(fileStream);

            String serverUrl = config.getProperty("XT_SERVER_ROOT_URL", "");
            mEnableAutoEula = !serverUrl.matches("https://(.*)oem(.*)/(.*)/(.*)");
            logv("Detected server URL: " + serverUrl + " Auto-EULA is " +
                 (mEnableAutoEula ? "enabled" : "disabled."));
            if(mEnableAutoEula) {
                // Populate Embargoed country information
                populateCountryList(
                    mEmbargoedCountryList,
                    config.getProperty("GTP_OSAGENT_EMBARGOED_MCC_LIST",
                                       mContext.getString(com.qualcomm.location.R.string.EmbargoedMcc)));
                logv("OSAgent Embargoed MCC: " + mEmbargoedCountryList.toString());
                populateSIDRanges(
                    mEmbargoedSIDRanges,
                    config.getProperty("GTP_OSAGENT_EMBARGOED_SID_LIST",
                                       mContext.getString(com.qualcomm.location.R.string.EmbargoedSID)));
                logv("OSAgent Embargoed SID: " + mEmbargoedSIDRanges.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileStream != null) {
                    fileStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void populateSIDRanges(ArrayList<Range<Integer>> sidRangeList, String configSidRangeList)
    {
        // expected format of SID ranges:
        // Each range is included in []
        // min and max values in SID ranges separated by ',' respectively
        // eg: [13568,14335][25600,26111][7825,7825]
        try {
            String[] sidRanges = configSidRangeList.trim().split("\\[");

            for (String sidRangeStr : sidRanges) {
                try {
                    String sidRangeStr_t = sidRangeStr.trim();
                    if(true == sidRangeStr_t.equals("")) {
                        continue;
                    }
                    String[] sidRange = sidRangeStr_t.substring(0, sidRangeStr_t.length() - 1).split(",");
                    Range<Integer> range =
                        new Range<Integer>(Integer.parseInt(sidRange[0].trim()),
                                           Integer.parseInt(sidRange[1].trim()));
                    sidRangeList.add(range);
                } catch (PatternSyntaxException e) {
                    loge("OsAgent: Error in spliting SID range" + sidRangeStr +
                         ":" + e.toString());
                } catch (IndexOutOfBoundsException e) {
                    loge("OsAgent: Error in reading SID range " + sidRangeStr +
                         ":" + e.toString());
                } catch (NumberFormatException e) {
                    loge("OsAgent: Error in reading SID range" + sidRangeStr + ":" + e.toString());
                }
            }
        } catch (PatternSyntaxException e){
            loge("OsAgent: Error in reading configurations:" + e.toString());
        }
    }

    private void populateCountryList(HashMap<Integer, HashSet<Integer>> mapMccMnc,
                                     String configMccList)
    {
        // expected format of MCC list:
        // MCCs are separated by delimiter ~
        // if regulation is specific to certain MNCs within MCC MNCs are
        // represented inside [] next to MCC separated with coma(,)
        // eg: ~460~250[32,33,34,60]~368~467~432~659~634~417
        try {
            String[] mccList = configMccList.split("~");

            for (String mccStr : mccList) {
                try {
                    if(mccStr.trim().contains("[")) {
                        String[] mccMncsCombo = mccStr.split("\\[");
                        String mncSingleString = mccMncsCombo[1].trim();
                        String[] mncs = mncSingleString.substring(0, mncSingleString.length() - 1).split(",");
                        HashSet<Integer> mnc_list = new HashSet<Integer>(mncs.length);
                        for (String mncStr : mncs) {
                            mnc_list.add(Integer.parseInt(mncStr.trim()));
                        }
                        mapMccMnc.put(Integer.parseInt(mccMncsCombo[0].trim()), mnc_list);
                    }else if(false == mccStr.trim().equals("")){
                        mapMccMnc.put(Integer.parseInt(mccStr.trim()), null);
                    }
                } catch (NullPointerException e) {
                    loge("OsAgent: Error in reading MCC" + mccStr + ":" + e.toString());
                } catch (PatternSyntaxException e) {
                    loge("OsAgent: Error in spliting MCC String" + mccStr + ":" + e.toString());
                } catch (IndexOutOfBoundsException e) {
                    loge("OsAgent: Error in reading MNC for MCC " + mccStr + ":" + e.toString());
                } catch (NumberFormatException e) {
                    loge("OsAgent: Error in reading MCC" + mccStr + ":" + e.toString());
                }
            }
        } catch (PatternSyntaxException e){
            loge("OsAgent: Error in reading configurations:" + e.toString());
        }
    }

    private int isDeviceInRegulatedArea()
    {
        int deviceInRegulatedArea = DEVICE_IN_REGULATED_REGION_UNKNOWN;

        if (ServiceState.STATE_IN_SERVICE == mCurrentServiceState) {
            // get MCC/SID from the device
            int iMcc = 0;
            int iMnc = 0;
            int iSid = 0;
            int phoneType = mTelephonyMgr.getPhoneType();
            logv("Before computing is current n/w state regulated area: " + mDeviceInRegulatedArea);

            switch (phoneType)
            {
                case (TelephonyManager.PHONE_TYPE_CDMA):
                {
                    List<CellInfo> cellInfos = mTelephonyMgr.getAllCellInfo();
                    for (CellInfo cellInfo : cellInfos) {
                        if (cellInfo instanceof CellInfoCdma) {
                            CellIdentityCdma cdmaCell = ((CellInfoCdma)cellInfo).getCellIdentity();
                            if (cdmaCell != null) {
                                iSid = cdmaCell.getSystemId();
                                break;
                            }
                        }
                    }
                    if (iSid == 0) {
                        logd("isDeviceInRegulatedArea: Sid is zero");
                    } else {
                        deviceInRegulatedArea = DEVICE_IN_REGULATED_REGION_KNOWN;
                        for (Range<Integer> sidRange: mEmbargoedSIDRanges) {
                            if(sidRange.contains(iSid)) {
                                deviceInRegulatedArea |= DEVICE_IN_REGULATED_REGION_AT_EMBARGOED;
                                break;
                            }
                        }
                    }
                    break;
                }

                case (TelephonyManager.PHONE_TYPE_GSM):
                {
                    String strNetworkOperator = mTelephonyMgr.getNetworkOperator();
                    if ((strNetworkOperator != null) && !strNetworkOperator.isEmpty()) {
                        iMcc = getMcc(Integer.parseInt(strNetworkOperator), strNetworkOperator.length());
                        iMnc = getMnc(Integer.parseInt(strNetworkOperator), strNetworkOperator.length());
                    }

                    if (iMcc == 0) {
                        logv("isDeviceInRegulatedArea: MCC is zero");
                    } else {
                        deviceInRegulatedArea = DEVICE_IN_REGULATED_REGION_KNOWN;
                        if (mEmbargoedCountryList.containsKey(iMcc)) {
                            HashSet<Integer> mncs = mEmbargoedCountryList.get(iMcc);
                            if(null == mncs || mncs.contains(iMnc)) {
                                deviceInRegulatedArea |= DEVICE_IN_REGULATED_REGION_AT_EMBARGOED;
                            }
                        }
                    }
                    break;
                }

                case (TelephonyManager.PHONE_TYPE_NONE):
                {
                    break;
                }

                default:
                {
                    break;
                }
            }
            logv("After computing is n/w state in regulated area?, " + deviceInRegulatedArea);
        }

        if(DEVICE_IN_REGULATED_REGION_UNKNOWN == deviceInRegulatedArea) {
            logv("isDeviceInRegulatedArea: unknown - keeping previous state");
            deviceInRegulatedArea = mDeviceInRegulatedArea;
        }

        return deviceInRegulatedArea;
    }

    private void handleServiceStateChanged (int service_state)
    {
        if (mCurrentServiceState != service_state)
        {
            mCurrentServiceState = service_state;
            sendServiceStateInfo();
            handleCellLocationChanged();
        }
    }

    private void setRegulatedFeatureAllowed()
    {
        if(0 != (mDeviceInRegulatedArea & DEVICE_IN_REGULATED_REGION_KNOWN)) {
            boolean regulated;
            regulated =
                    (0 != (mDeviceInRegulatedArea & DEVICE_IN_REGULATED_REGION_AT_EMBARGOED));
            XTProxy.setFeatureAllowed(mContext, mCurrentUserId, !regulated);
        }
    }

    private void handleCellLocationChanged()
    {
        sendCellUpdateInfo();
        cacheCDMACarrier();
        checkRegion();
    }

    private void checkRegion() {
        if (mEnableAutoEula) {
            int isDevInRegulatedArea = isDeviceInRegulatedArea();
            if(mDeviceInRegulatedArea != isDevInRegulatedArea) {
                mDeviceInRegulatedArea = isDevInRegulatedArea;
                setRegulatedFeatureAllowed();
            }
        }
    }

    // Btle device scanning
    public boolean handleBleScan(boolean enable)
    {
        boolean retVal = false;
        int state = 0;
        if (mBluetoothAdapter == null) {
            BluetoothManager bluetoothMngr;
            bluetoothMngr = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothMngr.getAdapter();
        }
        if (mBluetoothAdapter != null && (mBtleScanStarted != enable)) {
            mBtleScanStarted = enable;
            state = mBluetoothAdapter.getLeState();
            if (enable) {
                // Register for broadcasts on BluetoothAdapter state change
                IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_BLE_STATE_CHANGED);
                mContext.registerReceiver(mBtLeStateChangeReceiver, filter);

                retVal = mBluetoothAdapter.enableBLE();
                if (!retVal) {
                    byte dummyBtApMacAddress[] = new byte[2];
                    // send error message to java client

                    if (mIdlClient != null) {
                        mIdlClient.btle_scan_data_inject(false, 0, dummyBtApMacAddress, 0, 0, 0,
                                ERR_BTLE_DISABLED);
                    }
                } else if ((BluetoothAdapter.STATE_BLE_ON == state) ||
                        (BluetoothAdapter.STATE_ON == state)) {
                    // enableBLE succeded and if state is BLE_ON, then start scan
                    handleBleScanStart();
                }
                // else if enableBLE succeded and state if not BLE_ON or BT_ON ,wait for state
                // change to BLE_ON broadcast rcvr and start scan there
            }
            else {
                handleBleScanStop(false);
                retVal = true;
            }
        }
        logd("handleBleScan BTLE state " + state + " enable: " + enable + " retval: " + retVal);
        return retVal;
    }

    // Function - start ble scan, set the lescan callback etc
    public void handleBleScanStart()
    {
        boolean retVal = false;
        int state = 0;

        retVal = mBluetoothAdapter.startLeScan(mLeScanCallback);
        state = mBluetoothAdapter.getLeState();
        mBtLeScanStartTime = System.currentTimeMillis();
        logd("handleBleScanStart startLeScan ret: " + retVal + " state " + state +
                            " time " + mBtLeScanStartTime);
         if (!retVal) {
            byte dummyBtApMacAddress[] = new byte[2];
            // send error message to java client
            if (mIdlClient != null) {
                mIdlClient.btle_scan_data_inject(false, 0, dummyBtApMacAddress, 0, 0, 0,
                           ERR_BTLE_GENERAL_FAIL);
            }
        }
    }

    // Function - stop ble scan, unset the lescan callback etc
    public void handleBleScanStop(boolean sendErrMsg)
    {
        boolean retVal = false;
        int state = 0;

        logd("handleBleScanStop sendErrMsg " + sendErrMsg);
        mBtleScanStarted = false;
        // Unregister broadcast listeners for BT state change
        mContext.unregisterReceiver(mBtLeStateChangeReceiver);
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        mBluetoothAdapter.disableBLE();
        mBtLeScanStartTime = 0;
        if (sendErrMsg) {
            byte dummyBtApMacAddress[] = new byte[2];
            // send error message to java client
            if (mIdlClient != null) {
                mIdlClient.btle_scan_data_inject(false, 0, dummyBtApMacAddress, 0, 0, 0,
                        ERR_BTLE_DISABLED);
            }
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =new BluetoothAdapter.LeScanCallback() {
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            // le scan callback
            logd("onLeScan BTLE device: " + device.toString() + " rssi: " + rssi);

            // Inject the BTLE device data to java client.
            final int BTLE_MAC_ADDR_LENGTH = 6;
            int i = 0;
            Integer val = 0;

            byte btApMacAddress[] = new byte[BTLE_MAC_ADDR_LENGTH];
            String[] bssid =  device.getAddress().split(":");
            for(i = 0;i < BTLE_MAC_ADDR_LENGTH ; i++){
                val = Integer.parseInt(bssid[i],16);
                btApMacAddress[i] = val.byteValue();
            }
            long btleDevScanRcvTime = System.currentTimeMillis();
            if (mIdlClient != null) {
                mIdlClient.btle_scan_data_inject(true, rssi, btApMacAddress, btleDevScanRcvTime,
                        mBtLeScanStartTime, 0, 0);
            }

        }
    };

    // Handle BLE state changes
    private final BroadcastReceiver mBtLeStateChangeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_BLE_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                                               BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_BLE_ON:
                    {
                        logd("BtLeStateChangeReceiver STATE_BLE_ON");
                        Message msgObj = Message.obtain(mHandler, MSG_BTLE_START_BLE_SCAN);
                        mHandler.sendMessage(msgObj);
                    }
                    break;

                    case BluetoothAdapter.STATE_BLE_TURNING_OFF:
                    {
                        logd("BtLeStateChangeReceiver STATE_BLE_TURNING_OFF");
                        Message msgObj = Message.obtain(mHandler, MSG_BTLE_STOP_BLE_SCAN);
                        mHandler.sendMessage(msgObj);
                    }
                    break;
                }
            }
        }
    };

    // Bt classic device scanning.
    public boolean handleClassicBtDevScan(boolean enable)
    {
        boolean retVal = false;
        if (mBluetoothAdapter == null) {
            BluetoothManager bluetoothMngr;
            bluetoothMngr = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothMngr.getAdapter();
        }
        if (mBluetoothAdapter != null) {
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            if (enable) {
                mContext.registerReceiver(bBtClassicDevScanReceiver, filter);
                mBluetoothAdapter.startDiscovery();
            }
            else {
                mContext.unregisterReceiver(bBtClassicDevScanReceiver);
                mBluetoothAdapter.cancelDiscovery();
            }
            retVal = true;
        }
        logd("handleclassicBtdevscan enable: " + enable + " retval: " + retVal);
        return retVal;
    }

    private final BroadcastReceiver bBtClassicDevScanReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int  rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                logd("ClassisBtDevScanning btdevice: " + device.toString() +" rssi: " + rssi);

                // Inject the BT device data to java client.
                final int BT_MAC_ADDR_LENGTH = 6;
                int i = 0;
                Integer val = 0;

                byte btApMacAddress[] = new byte[BT_MAC_ADDR_LENGTH];
                String[] bssid =  device.getAddress().split(":");
                for(i = 0;i < BT_MAC_ADDR_LENGTH; i++){
                    val = Integer.parseInt(bssid[i],16);
                    btApMacAddress[i] = val.byteValue();
                }
                Message msgObj = Message.obtain(mHandler, MSG_BTSCAN_DATA_INJECT, rssi, 0,
                        btApMacAddress);
                mHandler.sendMessage(msgObj);
            }
        }
    };

    private final class RilListener extends PhoneStateListener
    {
        public RilListener(Executor executor) {
            super(executor);
        }

        @Override
        public void onCellLocationChanged(CellLocation location)
        {
            try
            {
                logd("Dump Cell Location:"+ location.toString());
            }
            catch (java.lang.NullPointerException e)
            {
                logd("Null pointer for CellLocation");
            }

            mHandler.sendEmptyMessage (MSG_CELL_LOCATION_CHANGED);
            // broadcast to other modules
            mHandler.post(()->broadcastSystemEvent(
                        ISystemEventListener.MSG_CELL_ID_CHANGE, location));
        }

        @Override
        public void onServiceStateChanged(ServiceState serviceState)
        {
            logd("CurrentServiceState = " + mCurrentServiceState + " NewVoiceServiceState = "
                        + serviceState.getVoiceRegState() + " NewDataServiceState = "
                        + serviceState.getDataRegState());

            //We want mCurrentServiceState to indicate that either voice or data is in service
            if((serviceState.getVoiceRegState() == serviceState.STATE_IN_SERVICE) ||
               (serviceState.getDataRegState() == serviceState.STATE_IN_SERVICE)) {
                Message msgServiceStateChanged = Message.obtain(mHandler, MSG_SERVICE_STATE_CHANGED);
                msgServiceStateChanged.arg1 = serviceState.STATE_IN_SERVICE;
                mHandler.sendMessage(msgServiceStateChanged);
            }
            else {
                Message msgServiceStateChanged = Message.obtain(mHandler, MSG_SERVICE_STATE_CHANGED);
                msgServiceStateChanged.arg1 = serviceState.STATE_OUT_OF_SERVICE;
                mHandler.sendMessage(msgServiceStateChanged);
            }
            // broadcast to other modules
            mHandler.post(()->broadcastSystemEvent(
                        ISystemEventListener.MSG_SERVICE_STATE_CHANGE, serviceState));
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            // broadcast to other modules
            mHandler.post(()->broadcastSystemEvent(
                        ISystemEventListener.MSG_CALL_STATE_CHANGE, state, incomingNumber));
        }
    };

    private void sendServiceStateInfo()
    {
        int networkStatus = LOC_NW_OOO;

        if (mDataItemList.contains(SERVICESTATUS_DATA_ITEM_ID)) {
            if ((mCurrentServiceState== ServiceState.STATE_OUT_OF_SERVICE) ||
                (mCurrentServiceState== ServiceState.STATE_POWER_OFF) ||
                (mCurrentServiceState == ServiceState.STATE_EMERGENCY_ONLY)) {
                networkStatus = LOC_NW_OOO;
            }
            else {
                // get network status
                boolean nw_status = mTelephonyMgr.isNetworkRoaming();
                networkStatus = (nw_status ? LOC_NW_ROAMING : LOC_NW_HOME);
            }

            if (mIdlClient != null) {
                mIdlClient.service_state_update(networkStatus);
            }
        }
    }

    private void sendServiceInfo()
    {
        try
        {
            int iMcc = 0, iMnc = 0;

            // The IMSI will be provisioned in the device of SIM if the home carrier supports G/W/L.
            // For example, Vzw with C+L support should have IMSI available on their devices.
            // But if IMSI is not available then we treat it as CDMA only phone

            if ((mCurrentServiceState == ServiceState.STATE_OUT_OF_SERVICE) ||
                (mCurrentServiceState == ServiceState.STATE_POWER_OFF) ||
                (mCurrentServiceState == ServiceState.STATE_EMERGENCY_ONLY)) {
                logd("Service State = " + mCurrentServiceState);
            }
            else {
                boolean nw_status = mTelephonyMgr.isNetworkRoaming();
                logd("Network Status: " + nw_status);

                int phoneType = mTelephonyMgr.getPhoneType();

                if (phoneType == PhoneConstants.PHONE_TYPE_CDMA) {
                    logd("Home carrier network is CDMA");

                    // Assumption is that this property ro.cdma.home.operator.alpha which
                    // is returned from getSimOperatorName may or may not be set.
                    if (!mCDMAHomeCarrier.isEmpty()) {
                        logd("CDMA Home Carrier = " + mCDMAHomeCarrier);
                        if (mIdlClient != null) {
                            mIdlClient.serviceinfo_update(LOC_RILAIRIF_CDMA,
                                    mCDMAHomeCarrier, 0, 0);
                        }
                    }
                    else
                        logd("Unknown CDMA Home Carrier");
                }
                else if (phoneType == PhoneConstants.PHONE_TYPE_GSM) {
                    logd("Home carrier is G/W/L");

                      String strSimOperator = mTelephonyMgr.getSimOperator();
                      if(!strSimOperator.isEmpty())
                      {
                          iMcc = getMcc(Integer.parseInt(strSimOperator), strSimOperator.length());
                          iMnc = getMnc(Integer.parseInt(strSimOperator), strSimOperator.length());
                      }

                      logv("Operator = " + strSimOperator +" Carrier Mcc = " + iMcc +
                                  " Carrier Mnc = " + iMnc);
                      if (mIdlClient != null) {
                          mIdlClient.serviceinfo_update(LOC_RILAIRIF_GSM, "", iMcc, iMnc);
                      }
                }
                else {
                    logd("Cannot determine or unsupported phone type: Phone Type = " + phoneType);
                }
            }
        }
        catch (Exception e)
        {
            loge("Cannot generate RIL Service Information");
            e.printStackTrace();
        }
    }

    private void sendCellUpdateInfo()
    {
        try
        {
            if (!mDataItemList.contains(RIL_CELL_UPDATE_DATA_ITEM_ID)) {
                return;
            }

            int networkStatus = LOC_NW_OOO;
            int air_interface_type = 0;
            int valid_mask = 0;

            if ((mCurrentServiceState== ServiceState.STATE_OUT_OF_SERVICE) ||
                (mCurrentServiceState== ServiceState.STATE_POWER_OFF) ||
                (mCurrentServiceState == ServiceState.STATE_EMERGENCY_ONLY)) {

                networkStatus = LOC_NW_OOO;
                valid_mask |= LOC_RIL_CELLINFO_HAS_NW_STATUS;


                if (mIdlClient != null) {
                    mIdlClient.cell_update_ooo(networkStatus, valid_mask);
                }
            }
            else {
                // get network status
                boolean nw_status = mTelephonyMgr.isNetworkRoaming();
                networkStatus = (nw_status ? LOC_NW_ROAMING : LOC_NW_HOME);
                valid_mask |= LOC_RIL_CELLINFO_HAS_NW_STATUS;

                String strNetworkOperator = mTelephonyMgr.getNetworkOperator();

                int nwtype = mTelephonyMgr.getNetworkType();
                logd("Network roaming status: " + nw_status + ", network type: " + nwtype);

                List<CellInfo> cellInfoValue = mTelephonyMgr.getAllCellInfo();
                if (cellInfoValue != null) {
                    for (CellInfo ci: cellInfoValue) {
                        if (ci instanceof CellInfoLte && ci.isRegistered()) {
                            logd("CellInfoLte instance is registered");
                            CellInfoLte lteCell = (CellInfoLte)ci;
                            CellIdentityLte cellIdentityLte = lteCell.getCellIdentity();

                            if(cellIdentityLte != null) {
                                int cid = -1;
                                int iMcc = 0;
                                int iMnc = 0;
                                int pci = -1;
                                int tac = -1;

                                cid = cellIdentityLte.getCi();
                                iMcc = cellIdentityLte.getMcc();
                                iMnc = cellIdentityLte.getMnc();
                                pci = cellIdentityLte.getPci();
                                tac = cellIdentityLte.getTac();

                                valid_mask |= LOC_RIL_CELLINFO_HAS_TECH_TYPE;
                                air_interface_type = LOC_RIL_TECH_LTE;

                                logv("RIL-TECH-TYPE = " + air_interface_type +
                                            " PCI = " + pci + " CID = " + cid +
                                            " TAC = " + tac +
                                            " Network Operator = " + strNetworkOperator +
                                            " Network Mcc = " + iMcc +
                                            " Network Mnc = " + iMnc);

                                if ((iMcc == -1) || (iMcc == Integer.MAX_VALUE)) {
                                    logv("RIL-TECH-TYPE = " + air_interface_type +
                                                " MCC= " + iMcc +
                                                ", mcc treated as unknown");
                                }
                                else if ((iMcc < -1) || (iMcc > 999)) {
                                    logv("RIL-TECH-TYPE = " + air_interface_type +
                                                " MCC= " + iMcc +
                                                ", mcc out of valid range [0, 999]");
                                }
                                else {
                                    // According to ASN 0 is a valid value for MCC,
                                    // but there does not seem to be any country
                                    // with MCC as 0. Keep this condition.
                                    if (iMcc != 0) {
                                      valid_mask |= LOC_RIL_TECH_LTE_HAS_MCC;
                                    }
                                }

                                if ((iMnc == -1) || (iMnc == Integer.MAX_VALUE)) {
                                    logv("RIL-TECH-TYPE = " + air_interface_type +
                                                " MNC= " + iMnc +
                                                ", mnc treated as unknown");
                                }
                                else if ((iMnc < -1) || (iMnc > 999)) {
                                    Log.v(TAG, "RIL-TECH-TYPE = " + air_interface_type +
                                               " MNC= " + iMnc +
                                               ", mnc out of valid range [0, 999]");
                                }
                                else {
                                    // MNC can be 0
                                    valid_mask |= LOC_RIL_TECH_LTE_HAS_MNC;
                                }

                                if ((tac == -1) || (tac == Integer.MAX_VALUE)) {
                                    logd("RIL-TECH-TYPE = " + air_interface_type +
                                                " TAC = " + tac +
                                                ", tac treated as unknown");
                                }
                                else if ((tac < -1) ||(tac > 65535)) {
                                    logd("RIL-TECH-TYPE = " + air_interface_type +
                                                " TAC = " + tac +
                                                ", tac out of valid range [0, 65535]");
                                }
                                else {
                                    valid_mask |= LOC_RIL_TECH_LTE_HAS_TAC;
                                }

                                if ((cid == -1) || (cid == Integer.MAX_VALUE)) {
                                    logv("RIL-TECH-TYPE = " + air_interface_type +
                                                " CID = " + cid +
                                                ", cid treated as unknown");
                                }
                                else if ((cid < -1) || (cid > 268435455)) {
                                    logv("RIL-TECH-TYPE = " + air_interface_type +
                                                " CID = " + cid +
                                                ", cid out of valid range [0, 268435455]");
                                }
                                else
                                {
                                    valid_mask |= LOC_RIL_TECH_LTE_HAS_CID;
                                }

                                // Physical cell -id is optional,
                                // can still send the remaining postcard.
                                if ((pci != -1) && (pci != Integer.MAX_VALUE) && (pci <= 503)) {
                                    valid_mask |= LOC_RIL_TECH_LTE_HAS_PCI;
                                }
                                if (mIdlClient != null) {
                                    mIdlClient.cell_update_lte(air_interface_type,
                                            networkStatus, iMcc, iMnc, cid,
                                            pci, tac, valid_mask);
                                }
                            }
                        } else if (ci instanceof CellInfoGsm && ci.isRegistered()) {
                            CellIdentityGsm gsmCell = ((CellInfoGsm)ci).getCellIdentity();
                            int psc = -1;
                            int cid = -1;
                            int lac = -1;
                            int iMcc = 0;
                            int iMnc = 0;

                            switch (nwtype)
                            {
                              case TelephonyManager.NETWORK_TYPE_EDGE:
                              case TelephonyManager.NETWORK_TYPE_GPRS:
                                air_interface_type = LOC_RILAIRIF_GSM;
                                break;
                              case TelephonyManager.NETWORK_TYPE_HSDPA:
                              case TelephonyManager.NETWORK_TYPE_HSPA:
                              case TelephonyManager.NETWORK_TYPE_HSPAP:
                              case TelephonyManager.NETWORK_TYPE_HSUPA:
                              case TelephonyManager.NETWORK_TYPE_UMTS:
                              //case 17 is NETWORK_TYPE_TD_SCDMA.
                              //It is hardcoded since telephony has not defined the
                              //constant NETWORK_TYPE_TD_SCDMA to be 17. Once it is
                              //defined, this hard-coded number can be replaced with the
                              //constant
                              case 17:
                                air_interface_type = LOC_RILAIRIF_WCDMA;
                                break;
                              case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                                if (gsmCell != null) {
                                    psc = gsmCell.getPsc();
                                }
                                if(psc == -1)
                                {
                                  air_interface_type = LOC_RILAIRIF_GSM;
                                }
                                else
                                {
                                  air_interface_type = LOC_RILAIRIF_WCDMA;
                                }
                                break;
                              default:
                                break;
                            }

                            if ((air_interface_type == LOC_RILAIRIF_GSM) ||
                                (air_interface_type == LOC_RILAIRIF_WCDMA))
                            {
                                  valid_mask |= LOC_RIL_CELLINFO_HAS_TECH_TYPE;
                            }

                            if(strNetworkOperator != null && !strNetworkOperator.isEmpty())
                            {
                              iMcc = getMcc(Integer.parseInt(strNetworkOperator),
                                      strNetworkOperator.length());
                              iMnc = getMnc(Integer.parseInt(strNetworkOperator),
                                      strNetworkOperator.length());

                              // Here we do not check for -1/Integer.MAX_VALUE for MCC or MNC,
                              // as the getNetworkOperator API does not explicitly say that.
                              // Also the max-width of the 2 values is either 2 bytes/3 bytes.

                              // According to ASN 0 is a valid value for MCC,
                              // but there does not seem to be any country
                              // with MCC as 0. Keep this condition.
                              if (iMcc != 0)
                              {
                                  valid_mask |= LOC_RIL_TECH_GW_HAS_MCC;
                              }
                              // MNC can be 0
                              valid_mask |= LOC_RIL_TECH_GW_HAS_MNC;
                            }

                            if (gsmCell != null) {
                                cid = gsmCell.getCid();
                                lac = gsmCell.getLac();
                            }

                            // -1/Integer.MAX_VALUE readings for cid and lac are seen
                            // in transitioning state during cell switch overs.
                            // if LAC is -1/Integer.MAX_VALUE for WCDMA
                            // the remaining postcard can still be sent
                            // as client will treat LAC as 0 in that case.
                            // but LAC as -1/Integer.MAX_VALUE for GSM is
                            // not valid and client will anyways reject
                            // incomplete postcard.
                            if ((lac == -1) || (lac == Integer.MAX_VALUE))
                            {
                                logd("RIL-TECH-TYPE = " + air_interface_type +
                                            " LAC = " + lac +
                                            ", lac treated as unknown");
                            }
                            else if ((lac < -1) || (lac > 65535))
                            {
                                logd("RIL-TECH-TYPE = " + air_interface_type +
                                            " LAC = " + lac +
                                            ", lac out of valid range [0, 65535]");
                            }
                            else
                            {
                                  valid_mask |= LOC_RIL_TECH_GW_HAS_LAC;
                            }

                            // if CID is -1/ Integer.MAX_VALUE then it is not at all acceptable
                            if ((cid == -1) || (cid == Integer.MAX_VALUE))
                            {
                                 logv("RIL-TECH-TYPE = " + air_interface_type +
                                             " CID = " + cid +
                                             ", cid treated as unknown");
                            }
                            else if ((air_interface_type == LOC_RILAIRIF_GSM) &&
                                     ((cid < -1) || (cid > 65535)))
                            {
                                logv("RIL-TECH-TYPE = " + air_interface_type +
                                            " CID = " + cid +
                                            ", cid out of valid range [0, 65535]");
                            }
                            else if ((air_interface_type == LOC_RILAIRIF_WCDMA) &&
                                     ((cid < -1) || (cid > 268435455)))
                            {
                                logv("RIL-TECH-TYPE = " + air_interface_type +
                                            " CID = " + cid +
                                            ", cid out of valid range [0, 268435455]");
                            }
                            else
                            {
                                  valid_mask |= LOC_RIL_TECH_GW_HAS_CID;
                            }

                            logv("RIL-TECH-TYPE = " + air_interface_type + " PSC = " + psc +
                                        " CID = " + cid + " LAC = " + lac +
                                        " Network Operator = " + strNetworkOperator +
                                        " Network Mcc = " + iMcc + " Network Mnc = " + iMnc);

                            if (mIdlClient != null) {
                                mIdlClient.cell_update_gw(air_interface_type, networkStatus,
                                        iMcc, iMnc, lac, cid, valid_mask);
                            }
                        } else if (ci instanceof CellInfoCdma && ci.isRegistered()) {
                            air_interface_type = LOC_RILAIRIF_CDMA;
                            valid_mask |= LOC_RIL_CELLINFO_HAS_TECH_TYPE;

                            CellIdentityCdma cdmaCell = ((CellInfoCdma)ci).getCellIdentity();
                            int sid = -1, nid= -1, bsid = -1, bslat = -1, bslong = -1, iMcc = 0;

                            if (cdmaCell != null) {
                                sid = cdmaCell.getSystemId();
                                nid = cdmaCell.getNetworkId();
                                bsid = cdmaCell.getBasestationId();
                                bslat = cdmaCell.getLatitude();
                                bslong = cdmaCell.getLongitude();
                            }

                            if ((sid == -1) || (sid == Integer.MAX_VALUE))
                            {
                              logv("RIL-TECH-TYPE = CDMA" + " SID = " + sid +
                                          ", sid treated as unknown");
                            }
                            else if ((sid < -1) || (sid > 32767))
                            {
                              logv("RIL-TECH-TYPE = CDMA" + " SID = " + sid +
                                          ", sid out of valid range [0, 32767]");
                            }
                            else
                            {
                                  valid_mask |= LOC_RIL_TECH_CDMA_HAS_SID;
                            }

                            if ((nid == -1) || (nid == Integer.MAX_VALUE))
                            {
                                logd("RIL-TECH-TYPE = CDMA" + " NID = " + nid +
                                           ", nid treated as unknown");
                            }
                            else if ((nid < -1) || (nid > 65535))
                            {
                                Log.d(TAG, "RIL-TECH-TYPE = CDMA" + " NID = " + nid +
                                           ", nid out of valid range [0, 65535]");
                            }
                            else
                            {
                                valid_mask |= LOC_RIL_TECH_CDMA_HAS_NID;
                            }

                            if ((bsid == -1) || (bsid == Integer.MAX_VALUE))
                            {
                                logv("RIL-TECH-TYPE = CDMA" + " BSID = " + bsid +
                                            ", bsid treated as unknown");
                            }
                            else if ((bsid < -1) || (bsid > 65535))
                            {
                                logv("RIL-TECH-TYPE = CDMA" + " BSID = " + bsid +
                                            ", bsid out of valid range [0, 65535]");
                            }
                            else
                            {
                                valid_mask |= LOC_RIL_TECH_CDMA_HAS_BSID;
                            }

                            if (bslat != Integer.MAX_VALUE)
                            {
                                 valid_mask |= LOC_RIL_TECH_CDMA_HAS_BSLAT;
                            }
                            if (bslong != Integer.MAX_VALUE)
                            {
                                  valid_mask |= LOC_RIL_TECH_CDMA_HAS_BSLONG;
                            }

                            // time zone information
                            TimeZone timezone = TimeZone.getDefault();
                            Date datenow = new Date();
                            boolean inDST = timezone.inDaylightTime(datenow);

                            long timenow = datenow.getTime();
                            int UTCTimeOffset = timezone.getOffset(timenow);

                            valid_mask |= LOC_RIL_TECH_CDMA_HAS_TIMEZONE;
                            valid_mask |= LOC_RIL_TECH_CDMA_HAS_DAYLIGHT_SAVING;

                            logv("RIL-TECH-TYPE = CDMA SID = " + sid +
                                        " NID = " + nid + " BSID = " + bsid +
                                        " BSLAT = " + bslat + " BSLONG = " +
                                        bslong + "DST = " + inDST +
                                        "TimeOffset from UTC = " + UTCTimeOffset);

                            if (mIdlClient != null) {
                                mIdlClient.cell_update_cdma(air_interface_type, networkStatus,
                                        sid, nid, bsid, bslat, bslong, inDST, UTCTimeOffset,
                                        valid_mask);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
              loge("Cannot generate RIL Cell Update Information");
              e.printStackTrace();
        }
    }

    // CDMA Carrier name has to be cached to handle Roaming scenarios
    private void cacheCDMACarrier()
    {
        if (mTelephonyMgr.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA)
            return;

        if (!mCDMAHomeCarrier.isEmpty())
            return;

        logd("CacheCDMACarrier: ServiceState: " + mCurrentServiceState +
                    "Roaming: " + mTelephonyMgr.isNetworkRoaming());
        if (mCurrentServiceState == ServiceState.STATE_IN_SERVICE) {
            if (mTelephonyMgr.isNetworkRoaming() == false) {
                mCDMAHomeCarrier = mTelephonyMgr.getNetworkOperatorName();
                logd("Operator name = " + mCDMAHomeCarrier);
                if ((mCDMAHomeCarrier == null) || mCDMAHomeCarrier.isEmpty())
                    return;

                // Check if the string contains roaming indicator
                // check if the string is all numeric, which is invalid, if operatorname returning "310000" for example
                if (mCDMAHomeCarrier.toLowerCase().contains("indicator") || isNumeric(mCDMAHomeCarrier))
                    mCDMAHomeCarrier = "";
            }
        }
        if (!mCDMAHomeCarrier.isEmpty()) {
            logd("Got CDMA Carrier Name = " + mCDMAHomeCarrier);
        }
    }


    private int getMnc(int mncmccCombo, int digits)
    {
        int mnc = 0;
        if (digits == 6)
            mnc = mncmccCombo % 1000;
        else if (digits == 5)
            mnc = mncmccCombo % 100;

       logv("getMnc() - "+mnc);
       return mnc;
    }

    private int getMcc(int mncmccCombo, int digits)
    {
        int mcc = 0;
        if (digits == 6)
            mcc = mncmccCombo / 1000;
        else if (digits == 5)
            mcc = mncmccCombo / 100;
        logv("getMcc() - "+mcc);
        return mcc;
    }

    private boolean isNumeric(String strToCheck)
    {
        try
        {
          double d = Double.parseDouble(strToCheck);
        }
        catch (NumberFormatException nfe)
        {
          return false;
        }

        return true;
    }

    private void sendTac() {
        String imei = null;
        int phoneType = mTelephonyMgr.getPhoneType();
        if (phoneType == PhoneConstants.PHONE_TYPE_GSM) {
            imei = mTelephonyMgr.getImei();
        } else if (phoneType == PhoneConstants.PHONE_TYPE_CDMA){
            imei = mTelephonyMgr.getMeid();
        }
        String tac = "-";

        if (imei != null && imei.length() >= 8) {
            tac = imei.substring(0, 8);
        }

        if (mIdlClient != null) {
            mIdlClient.string_dataitem_update(TAC_DATA_ITEM_ID, tac);
        }
    }

    private void updateMccmnc() {
        List<String> mccmnc = new ArrayList<String>();

        String operator  = mTelephonyMgr.getNetworkOperator();
        if (operator == null || operator.isEmpty()) {
            mccmnc.add("-");

        } else {
            // note: first 3 characters are for MCC
            String mccmnc_formatted =
                    String.format("%s|%s", operator.substring(0,3), operator.substring(3));

            if (mccmnc_formatted.equals("000|00")) {
                logd("operator MCCMNC is \"000|00\". filtered out.");
                mccmnc.add("-");

            } else {
                mccmnc.add(mccmnc_formatted);
            }
        }

        SubscriptionManager sm = SubscriptionManager.from(mContext);
        List<SubscriptionInfo> siList = sm.getActiveSubscriptionInfoList();
        if (siList != null && siList.size() != 0) {
            for (SubscriptionInfo si : siList) {
                String mccmnc_formatted = String.format("%d|%02d", si.getMcc(), si.getMnc());
                mccmnc.add(mccmnc_formatted);
            }
        }

        String mccmncStr =  TextUtils.join("+", mccmnc);

        if (mccmncStr != null && !mccmncStr.isEmpty()) {
            if (mIdlClient != null) {
                mIdlClient.string_dataitem_update(MCCMNC_DATA_ITEM_ID, mccmncStr);
            }
        }

        checkRegion();
    }

    private Handler.Callback m_handler_callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int msgID = msg.what;
            logv("handleMessage what - " + msgID);

            switch(msgID)
            {
                case MSG_OSAGENT_INIT:
                    if (LocIDLClientBase.getIDLServiceVersion().compareTo(IDLServiceVersion.V_AIDL)
                            >= 0) {
                        mIdlClient = new OsAgentIdlClient();
                    } else {
                        mIdlClient = new OsAgentHidlClient();
                    }
                    initAutoEula();
                    installUserSwitchActionReceiver();
                    // listen for service state and cell location changed events
                    // even if there is no client for it
                    installRilListener();
                    // listen for location mode changed intents
                    installModeChangeReceiver();
                break;
                case MSG_DATAITEM_SUBSCRIBE:
                    handleSubscribe(msg.obj);
                break;
                case MSG_DATAITEM_REQUEST_DATA:
                    handleRequestData(msg.obj);
                break;
                case MSG_DATAITEM_UNSUBSCRIBE:
                    handleUnsubscribe(msg.obj);
                break;
                case MSG_DATAITEM_UNSUBSCRIBE_ALL:
                    handleUnsubscribeAll();
                break;
                case MSG_SERVICE_STATE_CHANGED:
                    handleServiceStateChanged(msg.arg1);
                break;
                case MSG_CELL_LOCATION_CHANGED:
                    handleCellLocationChanged();
                break;
                case MSG_CONNECT_STATE_CHANGED:
                    updateConnectivityStatus();
                break;
                case MSG_SCREEN_STATE_CHANGED:
                    if (mIdlClient != null) {
                        mIdlClient.screen_status_update((boolean)msg.obj);
                    }
                break;
                case MSG_POWER_STATE_CHANGED:
                    if (mIdlClient != null) {
                        mIdlClient.power_connect_status_update((boolean)msg.obj);
                    }
                break;
                case MSG_BATTERY_LEVEL_CHANGED:
                    notifyBatteryPct((int)msg.arg1, (int)msg.arg2);
                break;
                case MSG_TIME_ZONE_CHANGED:
                    sendTimeZoneInfo();
                break;
                case MSG_TIME_CHANGED:
                    sendTimeInfo();
                break;
                case MSG_SHUT_DOWN_CHANGED:
                    IzatService.SsrHandler.get().clearAllPackages(mContext);
                    if (mIdlClient != null) {
                        mIdlClient.shutdown_update();
                    }
                break;
                case MSG_WIFI_STATE_CHANGED:
                    updateWiFiSupplicantState((Intent)msg.obj);
                break;
                case MSG_CONTENT_DATA_CHANGED:
                    if (null != msg.obj) {
                        updateContentData((List<Integer>)msg.obj);
                    } else {
                        updateContentData(null);
                    }
                break;
                case MSG_FRAMEWORK_MODULE_TURNON:
                    handleModuleTurnOn(msg.arg1,msg.arg2);
                break;
                case MSG_FRAMEWORK_MODULE_TURNOFF:
                    handleModuleTurnOff(msg.arg1);
                break;
                case MSG_BTLE_START_BLE_SCAN:
                    handleBleScanStart();
                break;
                case MSG_BTLE_STOP_BLE_SCAN:
                    handleBleScanStop(true);
                break;
                case MSG_INSTALL_OUTGOING_CALL:
                    installOutgoingCallReceiver();
                break;
                case MSG_INSTALL_PKG_REMOVED:
                    installPackageRemovedReceiver();
                break;
                case MSG_INSTALL_NET_INITIATED:
                    installNetInitiatedReceiver();
                break;
                case MSG_INSTALL_RIL_LISTENER:
                    installRilReceiver();
                break;
                case MSG_INSTALL_UID_IM_LISTENER:
                    installUidImportanceReceiver();
                break;
                case MSG_BTSCAN_DATA_INJECT:
                    if (mIdlClient != null) {
                        mIdlClient.bt_classic_scan_data_inject(true, msg.arg1, (byte[])msg.obj,
                                0, 0, 0, 0);
                    }
                break;
                case MSG_IDL_SERVICE_DIED:
                    if (mIdlClient != null) {
                        mIdlClient.resetIDLService();
                    }
                break;
                default:
                    loge("Unhandled message");
                break;
            }
            return true;
        }
    };

    private void notifyBatteryPct(int level, int scale)
    {
        if ((scale > 0) && (level >= 0)) {
            int batteryPct = (int)((level * 100) / scale);
            logd("Battery::Level - " + level + " Scale - " + scale
                     + "Pct - " + batteryPct);
            // call the java client API here to notify battery level pct
            if (mIdlClient != null) {
                mIdlClient.battery_level_update(batteryPct);
            }
        }
    }

    private static final void logv(String msg)
    {
        if (VERBOSE_LOG) {
            Log.v(TAG, msg);
        }
    }

    private static final void logd(String msg)
    {
        if (DEBUG_LOG) {
            Log.d(TAG, msg);
        }
    }

    private static final void loge(String msg)
    {
        if (ERROR_LOG) {
            Log.e(TAG, msg);
        }
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void registerObserver(int sysEventMsgId, ISystemEventListener listener) {
        int osagentMsgId = getOsAgentMsgId(sysEventMsgId);

        mSysEventListenerLock.writeLock().lock();
        Set<ISystemEventListener> observerSet = mObserverMap.get(sysEventMsgId);
        if (observerSet == null) {
            mObserverMap.put(sysEventMsgId,
                    new HashSet<ISystemEventListener>() { {add(listener);} });
        } else {
            observerSet.add(listener);
        }
        mSysEventListenerLock.writeLock().unlock();

        if (osagentMsgId <= IZatServiceContext.MSG_OSAGENT_BASE) {
            return;
        }
        mHandler.sendMessage(Message.obtain(mHandler, osagentMsgId));
    }

    public void broadcastSystemEvent(int sysEventMsgId, Object... args) {
        logv("broadcastSystemEvent - " + sysEventMsgId);
        mSysEventListenerLock.readLock().lock();
        //notify other listeners interested in it
        Set<ISystemEventListener> observerSet = mObserverMap.get(sysEventMsgId);
        if (observerSet != null) {
            for (ISystemEventListener observer: observerSet) {
                observer.notify(sysEventMsgId, args);
            }
        }
        mSysEventListenerLock.readLock().unlock();
    }

    // mirror SystemEventListener msg ID to OsAgent msg ID
    public static int getOsAgentMsgId(int sysEvtMsgId) {
        int osagentMsgId = IZatServiceContext.MSG_OSAGENT_BASE;
        switch(sysEvtMsgId) {
            case ISystemEventListener.MSG_RIL_INFO:
                osagentMsgId = MSG_INSTALL_RIL_LISTENER;
                break;
            case ISystemEventListener.MSG_OUTGOING_CALL:
                osagentMsgId = MSG_INSTALL_OUTGOING_CALL;
                break;
            case ISystemEventListener.MSG_NET_INITIATED:
                osagentMsgId = MSG_INSTALL_NET_INITIATED;
                break;
            case ISystemEventListener.MSG_PKG_REMOVED:
                osagentMsgId = MSG_INSTALL_PKG_REMOVED;
                break;
            case ISystemEventListener.MSG_USER_SWITCH_ACTION_UPDATE:
                osagentMsgId = MSG_INSTALL_USER_SWITCH_ACTION_UPDATE;
                break;
            case ISystemEventListener.MSG_UID_IMPORTANCE_CHANGE:
                osagentMsgId = MSG_INSTALL_UID_IM_LISTENER;
                break;
        }
        return osagentMsgId;
    }

    private static final boolean VERBOSE_LOG = Log.isLoggable(TAG, Log.VERBOSE);
    private static final boolean DEBUG_LOG = Log.isLoggable(TAG, Log.DEBUG);
    private static final boolean ERROR_LOG = Log.isLoggable(TAG, Log.ERROR);

    private static final String ENH_LOCATION_SERVICES_ENABLED = XTProxy.ENH_LOCATION_SERVICES_ENABLED;
    private static final String COUNTRY_SELECT_ACTION = "com.android.location.osagent.COUNTRY_SELECT_ACTION";
    private int mCurrentServiceState = ServiceState.STATE_OUT_OF_SERVICE;
    private String mCDMAHomeCarrier = "";
    private int mCurrentUserId = UserHandle.USER_CURRENT;

    private Context mContext;
    private Handler mHandler;

    private List<Integer> mDataItemList = new ArrayList<Integer> ();
    private List<Integer> mContentSettingsList = new ArrayList<Integer> ();
    private Map<Integer, Set<ISystemEventListener>> mObserverMap = new HashMap<>();
    private static final ReadWriteLock mSysEventListenerLock = new ReentrantReadWriteLock();

    private ConnectivityManager mConnectivityMgr;
    private TelephonyManager mTelephonyMgr;
    private LocationManager mLocationMgr;
    private ActivityManager mActivityMgr;
    private RilListener mRilListener;
    private WifiManager mWifiMgr;

    private boolean mEnableAutoEula;

    // bit 0: Set to 1 if Regulatory information is known. Look bit 1 and bit 2
    //        for more information. Set to 0 otherwise. bit 1 and 2 ignored
    // bit 1: Set to 1 if device is in embargoed region
    // bit 2: Set to 1 if device is in region where region NLP needed
    private static final int DEVICE_IN_REGULATED_REGION_UNKNOWN = 0x0;
    private static final int DEVICE_IN_REGULATED_REGION_KNOWN = 0x1;
    private static final int DEVICE_IN_REGULATED_REGION_AT_EMBARGOED = 0x2;
    private int mDeviceInRegulatedArea = DEVICE_IN_REGULATED_REGION_UNKNOWN;

    private int mDeviceInChina = DEVICE_IN_REGULATED_REGION_UNKNOWN;

    // EULA consent monitoring
    private static final int FEATURE_DISABLED_BY_CONSET = 4;
    private int mEulaState = Integer.MAX_VALUE;

    // UID importance
    private static final int FOREGROUND_IMPORTANCE_CUTOFF =
            ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND_SERVICE;
    // Class for Btle device scanning
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mBtleScanStarted = false;
    private long mBtLeScanStartTime = 0;

    private static final int ERR_BTLE_NONE = 0;
    private static final int ERR_BTLE_GENERAL_FAIL = 1;
    private static final int ERR_BTLE_NO_MEASUREMENTS_AVAILABLE = 2;
    private static final int ERR_BTLE_DISABLED = 3;
    private static final int ERR_BTLE_UNKNOWN = 4;

    // Register for changes to the list of active SubscriptionInfo records
    private OnSubscriptionsChangedListener mSubscriptionsChangedListener = null;

    private HashMap<Integer, HashSet<Integer>> mEmbargoedCountryList =
        new HashMap<Integer, HashSet<Integer>> ();
    private ArrayList<Range<Integer>> mEmbargoedSIDRanges =
        new ArrayList<Range<Integer>> ();

    private OsAgentIdlClient mIdlClient;
    // EULA is consider disabled when in China MCC 460
    // (China) CDMA SID range 1: [13568, 14335]
    // CDMA SID range 2:[25600, 26111]
    // CDMA SID (China Spacecom Mobile Satellite): 7825
    // EULA is enabled in China Hong Kong, Macau,
    // and Taiwan MCC Hong Kong: 454 MCC Macau: 455
    // CDMA SID range for Hong Kong: [10640, 10655]
    // CDMA SID range for Macau: [11296, 11311]
    private static final int CHINA_MCC = 460;
    private static final int CHINA0_SID_MIN = 13568;
    private static final int CHINA0_SID_MAX = 14335;
    private static final int CHINA1_SID_MIN = 25600;
    private static final int CHINA1_SID_MAX = 26111;
    private static final int CHINA_SCOM_SID = 7825;

    // OSAgent messages
    public static final int MSG_DATAITEM_SUBSCRIBE =       IZatServiceContext.MSG_OSAGENT_BASE + 1;
    public static final int MSG_DATAITEM_REQUEST_DATA =    IZatServiceContext.MSG_OSAGENT_BASE + 2;
    public static final int MSG_DATAITEM_UNSUBSCRIBE =     IZatServiceContext.MSG_OSAGENT_BASE + 3;
    public static final int MSG_DATAITEM_UNSUBSCRIBE_ALL = IZatServiceContext.MSG_OSAGENT_BASE + 4;
    public static final int MSG_INSTALL_RIL_LISTENER    =  IZatServiceContext.MSG_OSAGENT_BASE + 5;
    public static final int MSG_INSTALL_USER_SWITCH_ACTION_UPDATE =
            IZatServiceContext.MSG_OSAGENT_BASE + 6;
    public static final int MSG_CELL_LOCATION_CHANGED =    IZatServiceContext.MSG_OSAGENT_BASE + 7;
    public static final int MSG_SERVICE_STATE_CHANGED =    IZatServiceContext.MSG_OSAGENT_BASE + 8;
    public static final int MSG_INIT_AUTO_EULA =           IZatServiceContext.MSG_OSAGENT_BASE + 9;
    public static final int MSG_FRAMEWORK_MODULE_TURNON =  IZatServiceContext.MSG_OSAGENT_BASE + 10;
    public static final int MSG_FRAMEWORK_MODULE_TURNOFF = IZatServiceContext.MSG_OSAGENT_BASE + 11;
    public static final int MSG_BTLE_START_BLE_SCAN =      IZatServiceContext.MSG_OSAGENT_BASE + 12;
    public static final int MSG_BTLE_STOP_BLE_SCAN =       IZatServiceContext.MSG_OSAGENT_BASE + 13;
    public static final int MSG_OSAGENT_INIT =             IZatServiceContext.MSG_OSAGENT_BASE + 14;
    public static final int MSG_INSTALL_OUTGOING_CALL   =  IZatServiceContext.MSG_OSAGENT_BASE + 15;
    public static final int MSG_INSTALL_NET_INITIATED =    IZatServiceContext.MSG_OSAGENT_BASE + 16;
    public static final int MSG_INSTALL_PKG_REMOVED =      IZatServiceContext.MSG_OSAGENT_BASE + 17;
    public static final int MSG_INSTALL_UID_IM_LISTENER =  IZatServiceContext.MSG_OSAGENT_BASE + 18;
    public static final int MSG_INSTALL_PHONE_STATE =      IZatServiceContext.MSG_OSAGENT_BASE + 19;
    public static final int MSG_CONNECT_STATE_CHANGED =    IZatServiceContext.MSG_OSAGENT_BASE + 20;
    public static final int MSG_SCREEN_STATE_CHANGED =     IZatServiceContext.MSG_OSAGENT_BASE + 21;
    public static final int MSG_POWER_STATE_CHANGED =      IZatServiceContext.MSG_OSAGENT_BASE + 22;
    public static final int MSG_BATTERY_LEVEL_CHANGED =    IZatServiceContext.MSG_OSAGENT_BASE + 23;
    public static final int MSG_TIME_ZONE_CHANGED =        IZatServiceContext.MSG_OSAGENT_BASE + 24;
    public static final int MSG_TIME_CHANGED =             IZatServiceContext.MSG_OSAGENT_BASE + 25;
    public static final int MSG_SHUT_DOWN_CHANGED =        IZatServiceContext.MSG_OSAGENT_BASE + 26;
    public static final int MSG_WIFI_STATE_CHANGED =       IZatServiceContext.MSG_OSAGENT_BASE + 27;
    public static final int MSG_CONTENT_DATA_CHANGED =     IZatServiceContext.MSG_OSAGENT_BASE + 28;
    public static final int MSG_BTSCAN_DATA_INJECT =       IZatServiceContext.MSG_OSAGENT_BASE + 29;
    public static final int MSG_IDL_SERVICE_DIED =         IZatServiceContext.MSG_OSAGENT_BASE + 30;

    // Data Item Id's
    private static final int AIRPLANEMODE_DATA_ITEM_ID = 0;
    private static final int ENH_DATA_ITEM_ID = 1;
    private static final int GPSSTATE_DATA_ITEM_ID = 2;
    private static final int NLPSTATUS_DATA_ITEM_ID = 3;
    private static final int WIFIHARDWARESTATE_DATA_ITEM_ID = 4;
    private static final int NETWORKINFO_DATA_ITEM_ID = 5;
    private static final int RIL_VERSION_DATA_ITEM_ID = 6;
    private static final int RIL_SERVICE_INFO_DATA_ITEM_ID = 7;
    private static final int RIL_CELL_UPDATE_DATA_ITEM_ID = 8;
    private static final int SERVICESTATUS_DATA_ITEM_ID = 9;
    private static final int MODEL_DATA_ITEM_ID = 10;
    private static final int MANUFACTURER_DATA_ITEM_ID = 11;
    private static final int VOICECALL_DATA_ITEM = 12;
    private static final int ASSISTED_GPS_DATA_ITEM_ID = 13;
    private static final int SCREEN_STATE_DATA_ITEM_ID = 14;
    private static final int POWER_CONNECTED_STATE_DATA_ITEM_ID = 15;
    private static final int TIMEZONE_CHANGE_DATA_ITEM_ID = 16;
    private static final int TIME_CHANGE_DATA_ITEM_ID = 17;
    private static final int WIFI_SUPPLICANT_STATUS_DATA_ITEM_ID = 18;
    private static final int SHUTDOWN_STATE_DATA_ITEM_ID = 19;
    private static final int TAC_DATA_ITEM_ID = 20;
    private static final int MCCMNC_DATA_ITEM_ID = 21;
    private static final int BTLE_SCAN_DATA_ITEM_ID = 22;
    private static final int BT_SCAN_DATA_ITEM_ID = 23;
    private static final int BATTERY_LEVEL_DATA_ITEM_ID = 26;

    // LOC RIL definitions
    // Air Interface Type masks for LocRilServiceInfo
    private static final int LOC_RILAIRIF_CDMA = 1;
    private static final int LOC_RILAIRIF_GSM = 2;
    private static final int LOC_RILAIRIF_WCDMA = 4;
    private static final int LOC_RILAIRIF_LTE = 8;
    private static final int LOC_RILAIRIF_EVDO = 16;
    private static final int LOC_RILAIRIF_WIFI = 32;

    // LocNw_Status definitions
    private static final int LOC_NW_ROAMING = 1;
    private static final int LOC_NW_HOME = 2;
    private static final int LOC_NW_OOO = 3;

    // Air Interface type masks for LocRilCellInfo
    private static final int LOC_RIL_TECH_CDMA = 0x1;
    private static final int LOC_RIL_TECH_GSM = 0x2;
    private static final int LOC_RIL_TECH_WCDMA = 0x4;
    private static final int LOC_RIL_TECH_LTE = 0x8;
    private static final int LOC_RIL_TECH_TD_SCDMA = 0x16;

    // The following defines are for valid_mask in RilCellInfo
    private static final int LOC_RIL_CELLINFO_HAS_NW_STATUS = 0x00000001;
    private static final int LOC_RIL_CELLINFO_HAS_TECH_TYPE = 0x00000002;
    private static final int LOC_RIL_CELLINFO_HAS_CELL_INFO = 0x00000004;

    // The following defines are for valid mask for Loc_RilTechGsmCinfo
    // and Loc_RilTechWcdmaCinfo fields.
    private static final int LOC_RIL_TECH_GW_HAS_MCC = 0x00000008;
    private static final int LOC_RIL_TECH_GW_HAS_MNC = 0x00000010;
    private static final int LOC_RIL_TECH_GW_HAS_LAC = 0x00000020;
    private static final int LOC_RIL_TECH_GW_HAS_CID = 0x00000040;

    // The following defines are for valid_mask in LOC_RilTechCdmaCinfo
    private static final int LOC_RIL_TECH_CDMA_HAS_MCC              = 0x00000008;
    private static final int LOC_RIL_TECH_CDMA_HAS_SID              = 0x00000010;
    private static final int LOC_RIL_TECH_CDMA_HAS_NID              = 0x00000020;
    private static final int LOC_RIL_TECH_CDMA_HAS_BSID             = 0x00000040;
    private static final int LOC_RIL_TECH_CDMA_HAS_BSLAT            = 0x00000080;
    private static final int LOC_RIL_TECH_CDMA_HAS_BSLONG           = 0x00000100;
    private static final int LOC_RIL_TECH_CDMA_HAS_TIMEZONE         = 0x00000200;
    private static final int LOC_RIL_TECH_CDMA_HAS_DAYLIGHT_SAVING  = 0x00000400;

    // The following defines are for valid_mask in LOC_RilTechLteCinfo
    private static final int LOC_RIL_TECH_LTE_HAS_MCC  = 0x00000008;
    private static final int LOC_RIL_TECH_LTE_HAS_MNC  = 0x00000010;
    private static final int LOC_RIL_TECH_LTE_HAS_TAC  = 0x00000020;
    private static final int LOC_RIL_TECH_LTE_HAS_PCI  = 0x00000040;
    private static final int LOC_RIL_TECH_LTE_HAS_CID  = 0x00000080;

    /* =================================================
     *   HIDL Client
     * =================================================*/
    private class OsAgentHidlClient extends OsAgentIdlClient
            implements LocIDLClientBase.IServiceDeathCb {

        private vendor.qti.gnss.V1_0.ILocHidlIzatSubscription mOsAgentIface;
        private IDLServiceVersion mVer = IDLServiceVersion.V0_0;
        private OsAgentCb mOsAgentCb;
        private OsAgent mOsAgent;

        private OsAgentHidlClient() {
            getOsAgentIface();
            if (null != mOsAgentIface) {
                try {
                    mOsAgentCb = new OsAgentCb(OsAgent.this);
                    mOsAgentIface.init(mOsAgentCb);
                    registerServiceDiedCb(this);
                } catch (RemoteException e) {
                }
            }
        }

        private void getOsAgentIface() {
            if (null == mOsAgentIface) {
                ILocHidlGnss service = (ILocHidlGnss)getGnssService();
                mVer = getIDLServiceVersion();
                if (null != service) {
                    try {
                        if (mVer.compareTo(IDLServiceVersion.V1_1) >= 0) {
                            mOsAgentIface = service.getExtensionLocHidlIzatSubscription_1_1();
                        } else {
                            mOsAgentIface = service.getExtensionLocHidlIzatSubscription();
                        }
                    } catch (RemoteException e) {
                    }
                }
            }
        }
        @Override
        public void resetIDLService() {
            mOsAgentIface = null;
            getOsAgentIface();
            if (null != mOsAgentIface) {
                try {
                    mOsAgentIface.init(mOsAgentCb);
                } catch (RemoteException e) {
                }
            }
        }
        @Override
        public void onServiceDied() {
            Message msgObj = Message.obtain(mHandler, MSG_IDL_SERVICE_DIED);
            mHandler.sendMessage(msgObj);
        }
        private class OsAgentCb extends ILocHidlIzatSubscriptionCallback.Stub {
            private OsAgent mOsAgent;

            public OsAgentCb(OsAgent osAgent) {
                mOsAgent = osAgent;
            }

            @Override
            public void requestData(java.util.ArrayList<Integer> l) {
                IDLClientUtils.fromIDLService(TAG);
                int[] dataItemArray = new int[l.size()];
                for (int i = 0; i < l.size(); i++) {
                    dataItemArray[i] = l.get(i).intValue();
                }
                OsAgent.this.requestData(dataItemArray);
            }

            @Override
            public void updateSubscribe(java.util.ArrayList<Integer> l, boolean subscribe) {
                IDLClientUtils.fromIDLService(TAG);
                int[] dataItemArray = new int[l.size()];
                for (int i = 0; i < l.size(); i++) {
                    dataItemArray[i] = l.get(i).intValue();
                }
                if (subscribe) {
                    mOsAgent.subscribe(dataItemArray);
                } else {
                    mOsAgent.unsubscribe(dataItemArray);
                }
            }

            @Override
            public void unsubscribeAll() {
                IDLClientUtils.fromIDLService(TAG);
                mOsAgent.unsubscribeAll();
            }

            @Override
            public void turnOnModule(int di, int timeout) {
                IDLClientUtils.fromIDLService(TAG);
                mOsAgent.turnOn(di, timeout);
            }

            @Override
            public void turnOffModule(int di) {
                IDLClientUtils.fromIDLService(TAG);
                mOsAgent.turnOff(di);
            }
        }

        public void subscription_deinit() {
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.deinit();
                } catch (RemoteException e) {
                }
            }
        }
        @Override
        public void bool_dataitem_update(int[] dataItemId, boolean[] updated_value) {
            ArrayList dataItemArray = new ArrayList<ILocHidlIzatSubscription.BoolDataItem>();
            for (int i = 0; i < dataItemId.length; i++) {
                ILocHidlIzatSubscription.BoolDataItem item =
                    new ILocHidlIzatSubscription.BoolDataItem();
                item.id = dataItemId[i];
                item.enabled = updated_value[i];
                dataItemArray.add(item);
            }
            if (mOsAgentIface!= null && dataItemArray.size() > 0) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.boolDataItemUpdate(dataItemArray);
                } catch (RemoteException e) {
                }
            }
        }
        @Override
        public void string_dataitem_update(int dataItemId, String updated_value) {
            ILocHidlIzatSubscription.StringDataItem item =
                new ILocHidlIzatSubscription.StringDataItem();
            item.id = dataItemId;
            item.str = updated_value;
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.stringDataItemUpdate(item);
                } catch (RemoteException e) {
                }
            }
        }
        @Override
        public void networkinfo_update(boolean is_connected, int type,
                                       String type_name, String subtype_name,
                                       boolean is_available, boolean is_roaming) {
            ILocHidlIzatSubscription.NetworkInfoDataItem item =
                new ILocHidlIzatSubscription.NetworkInfoDataItem();
            item.type = type;
            item.typeName = type_name;
            item.subTypeName = subtype_name;
            item.available = is_available;
            item.connected = is_connected;
            item.roaming = is_roaming;
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.networkinfoUpdate(item);
                } catch (RemoteException e) {
                }
            }
        }
        @Override
        public void serviceinfo_update(int air_interface_type, String carrierName, int Mcc,
                                       int Mnc) {
            ILocHidlIzatSubscription.RilServiceInfoDataItem item =
                new ILocHidlIzatSubscription.RilServiceInfoDataItem();
            item.type = air_interface_type;
            item.mcc = Mcc;
            item.mnc = Mnc;
            item.name = carrierName;
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.serviceinfoUpdate(item);
                } catch (RemoteException e) {
                }
            }
        }
        @Override
        public void cell_update_lte(int air_interface_type, int nwStatus, int iMcc,
                int iMnc, int cid, int pci, int tac, int valid_mask) {
            ILocHidlIzatSubscription.CellLteDataItem item =
                new ILocHidlIzatSubscription.CellLteDataItem();
            item.type = air_interface_type;
            item.status = nwStatus;
            item.mcc = iMcc;
            item.mnc = iMnc;
            item.cid = cid;
            item.pci = pci;
            item.tac = tac;
            item.mask = valid_mask;
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.cellLteUpdate(item);
                } catch (RemoteException e) {
                }
            }
        }
        @Override
        public void cell_update_gw(int air_interface_type, int nwStatus, int iMcc, int iMnc,
                int lac, int cid, int valid_mask) {
            ILocHidlIzatSubscription.CellGwDataItem item =
                new ILocHidlIzatSubscription.CellGwDataItem();
            item.type = air_interface_type;
            item.status = nwStatus;
            item.mcc = iMcc;
            item.mnc = iMnc;
            item.lac = lac;
            item.cid = cid;
            item.mask = valid_mask;
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.cellGwUpdate(item);
                } catch (RemoteException e) {
                }
            }
        }
        @Override
        public void cell_update_cdma(int air_interface_type, int nwStatus, int sid, int nid,
                int bsid, int bslat, int bslong, boolean inDST, int UTCTimeOffset,
                int valid_mask) {
            ILocHidlIzatSubscription.CellCdmaDataItem item =
                new ILocHidlIzatSubscription.CellCdmaDataItem();
            item.type = air_interface_type;
            item.status = nwStatus;
            item.sid = sid;
            item.nid = nid;
            item.bsid = bsid;
            item.bslat = bslat;
            item.bslong = bslong;
            item.timeOffset = UTCTimeOffset;
            item.mask = valid_mask;
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.cellCdmaUpdate(item);
                } catch (RemoteException e) {
                }
            }
        }
        @Override
        public void cell_update_ooo(int nwStatus, int valid_mask) {
            ILocHidlIzatSubscription.CellOooDataItem item =
                new ILocHidlIzatSubscription.CellOooDataItem();
            item.status = nwStatus;
            item.mask = valid_mask;
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.cellOooUpdate(item);
                } catch (RemoteException e) {
                }
            }
        }
        @Override
        public void service_state_update(int nwStatus) {
            ILocHidlIzatSubscription.ServiceStateDataItem item =
                new ILocHidlIzatSubscription.ServiceStateDataItem();
            item.status = nwStatus;
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.serviceStateUpdate(item);
                } catch (RemoteException e) {
                }
            }
        }
        @Override
        public void screen_status_update(boolean status) {
            ILocHidlIzatSubscription.ScreenStatusDataItem item =
                new ILocHidlIzatSubscription.ScreenStatusDataItem();
            item.status = status;
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.screenStatusUpdate(item);
                } catch (RemoteException e) {
                }
            }
        }
        @Override
        public void power_connect_status_update(boolean status) {
            ILocHidlIzatSubscription.PowerConnectStatusDataItem item =
                new ILocHidlIzatSubscription.PowerConnectStatusDataItem();
            item.status = status;
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.powerConnectStatusUpdate(item);
                } catch (RemoteException e) {
                }
            }
        }
        @Override
        public void timezone_change_update(long currTimeMillis, int rawOffset, int dstOffset) {
            ILocHidlIzatSubscription.TimeZoneChangeDataItem item =
                new ILocHidlIzatSubscription.TimeZoneChangeDataItem();
            item.curTimeMillis = currTimeMillis;
            item.rawOffset = rawOffset;
            item.dstOffset = dstOffset;
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.timezoneChangeUpdate(item);
                } catch (RemoteException e) {
                }
            }
        }
        @Override
        public void time_change_update(long currTimeMillis, int rawOffset, int dstOffset) {
            ILocHidlIzatSubscription.TimeChangeDataItem item =
                new ILocHidlIzatSubscription.TimeChangeDataItem();
            item.curTimeMillis = currTimeMillis;
            item.rawOffset = rawOffset;
            item.dstOffset = dstOffset;
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.timeChangeUpdate(item);
                } catch (RemoteException e) {
                }
            }

        }
        @Override
        public void shutdown_update() {
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.shutdownUpdate();
                } catch (RemoteException e) {
                }
            }
        }
        @Override
        public void wifi_supplicant_status_update(int state,
                                                  int ap_mac_valid,
                                                  byte ap_mac_array[],
                                                  int ssid_valid,
                                                  char ssid_array[]) {
            ILocHidlIzatSubscription.WifiSupplicantStatusDataItem item =
                new ILocHidlIzatSubscription.WifiSupplicantStatusDataItem();
            item.state = state;
            item.apMacAddressValid = (ap_mac_valid != 0);
            item.apSsidValid = (ssid_valid != 0);
            if (ap_mac_valid != 0) {
                for (int i = 0; i < ap_mac_array.length; i++) {
                    item.apMacAddress.add(ap_mac_array[i]);
                }
            }
            if (ssid_valid != 0) {
                item.apSsid = String.valueOf(ssid_array);
            }
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.wifiSupplicantStatusUpdate(item);
                } catch (RemoteException e) {
                }
            }
        }
        @Override
        public void battery_level_update(int batteryPct) {
            ILocHidlIzatSubscription.BatteryLevelDataItem item =
                new ILocHidlIzatSubscription.BatteryLevelDataItem();
            item.batteryPct = (byte)batteryPct;
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    if (mVer.compareTo(IDLServiceVersion.V1_1) >= 0) {
                        ((vendor.qti.gnss.V1_1.ILocHidlIzatSubscription)mOsAgentIface)
                                .batteryLevelUpdate(item);
                    }
                } catch (RemoteException e) {
                }
            }
        }
        @Override
        public void btle_scan_data_inject(boolean is_valid,
                                          int rssi,
                                          byte btle_mac_array[],
                                          long btle_scan_req_timestamp,
                                          long scan_start_timestamp,
                                          long scan_recv_timestamp,
                                          int error_code) {
            ILocHidlIzatSubscription.BtLeDeviceScanDetailsDataItem item =
                new ILocHidlIzatSubscription.BtLeDeviceScanDetailsDataItem();
            item.validSrnData = is_valid;
            item.errorCause = error_code;
            if (is_valid) {
                item.apSrnRssi = rssi;
                item.apSrnTimestamp = scan_start_timestamp;
                item.requestTimestamp = btle_scan_req_timestamp;
                item.receiveTimestamp = scan_recv_timestamp;
                for (int i = 0; i < btle_mac_array.length; i++) {
                    item.apSrnMacAddress.add(btle_mac_array[i]);
                }
            }
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.btleScanDataInject(item);
                } catch (RemoteException e) {
                }
            }
        }
        @Override
        public void bt_classic_scan_data_inject(boolean is_valid,
                                                int rssi,
                                                byte bt_mac_array[],
                                                long bt_scan_req_timestamp,
                                                long scan_start_timestamp,
                                                long scan_recv_timestamp,
                                                int error_code) {
            ILocHidlIzatSubscription.BtDeviceScanDetailsDataItem item =
                new ILocHidlIzatSubscription.BtDeviceScanDetailsDataItem();
            item.validSrnData = is_valid;
            item.errorCause = error_code;
            if (is_valid) {
                item.apSrnRssi = rssi;
                item.apSrnTimestamp = scan_start_timestamp;
                item.requestTimestamp = bt_scan_req_timestamp;
                item.receiveTimestamp = scan_recv_timestamp;
                for (int i = 0; i < bt_mac_array.length; i++) {
                    item.apSrnMacAddress.add(bt_mac_array[i]);
                }
            }
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.btClassicScanDataInject(item);
                } catch (RemoteException e) {
                }
            }
        }

    }

    /* =================================================
     *   AIDL Client
     * =================================================*/
    private class OsAgentIdlClient extends LocIDLClientBase
            implements LocIDLClientBase.IServiceDeathCb {

        private final String TAG = "OsAgentAidlClient";
        private vendor.qti.gnss.ILocAidlIzatSubscription mOsAgentIface;
        private OsAgentCb mOsAgentCb;
        private OsAgent mOsAgent;

        private OsAgentIdlClient() {
            getOsAgentIface();
            if (null != mOsAgentIface) {
                try {
                    mOsAgentCb = new OsAgentCb(OsAgent.this);
                    mOsAgentIface.init(mOsAgentCb);
                    registerServiceDiedCb(this);
                } catch (RemoteException e) {
                }
            }
        }

        private void getOsAgentIface() {
            if (null == mOsAgentIface) {
                ILocAidlGnss service = (ILocAidlGnss)getGnssAidlService();
                if (null != service) {
                    try {
                        mOsAgentIface = service.getExtensionLocAidlIzatSubscription();
                    } catch (RemoteException e) {
                    }
                }
            }
        }

        public void resetIDLService() {
            mOsAgentIface = null;
            getOsAgentIface();
            if (null != mOsAgentIface) {
                try {
                    mOsAgentIface.init(mOsAgentCb);
                } catch (RemoteException e) {
                }
            }
        }
        @Override
        public void onServiceDied() {
            Message msgObj = Message.obtain(mHandler, MSG_IDL_SERVICE_DIED);
            mHandler.sendMessage(msgObj);
        }
        private class OsAgentCb extends ILocAidlIzatSubscriptionCallback.Stub {
            private OsAgent mOsAgent;

            public OsAgentCb(OsAgent osAgent) {
                mOsAgent = osAgent;
            }

            @Override
            public void requestData(int[] l) {
                IDLClientUtils.fromIDLService(TAG);
                OsAgent.this.requestData(l);
            }

            @Override
            public void updateSubscribe(int[] l, boolean subscribe) {
                IDLClientUtils.fromIDLService(TAG);
                if (subscribe) {
                    mOsAgent.subscribe(l);
                } else {
                    mOsAgent.unsubscribe(l);
                }
            }

            @Override
            public void unsubscribeAll() {
                IDLClientUtils.fromIDLService(TAG);
                mOsAgent.unsubscribeAll();
            }

            @Override
            public void turnOnModule(int di, int timeout) {
                IDLClientUtils.fromIDLService(TAG);
                mOsAgent.turnOn(di, timeout);
            }

            @Override
            public void turnOffModule(int di) {
                IDLClientUtils.fromIDLService(TAG);
                mOsAgent.turnOff(di);
            }
            @Override
            public final int getInterfaceVersion() {
                return ILocAidlIzatSubscriptionCallback.VERSION;
            }
            @Override
            public final String getInterfaceHash() {
                return ILocAidlIzatSubscriptionCallback.HASH;
            }
        }

        public void subscription_deinit() {
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.deinit();
                } catch (RemoteException e) {
                }
            }
        }

        public void bool_dataitem_update(int[] dataItemId, boolean[] updated_value) {
            LocAidlBoolDataItem[] dataItemArray = new LocAidlBoolDataItem[dataItemId.length];
            for (int i = 0; i < dataItemId.length; i++) {
                LocAidlBoolDataItem item = new LocAidlBoolDataItem();
                item.id = dataItemId[i];
                item.enabled = updated_value[i];
                dataItemArray[i] = item;
            }
            if (mOsAgentIface!= null && dataItemArray.length > 0) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.boolDataItemUpdate(dataItemArray);
                } catch (RemoteException e) {
                }
            }
        }

        public void string_dataitem_update(int dataItemId, String updated_value) {
            LocAidlStringDataItem item = new LocAidlStringDataItem();
            item.id = dataItemId;
            item.str = updated_value;
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.stringDataItemUpdate(item);
                } catch (RemoteException e) {
                }
            }
        }

        public void networkinfo_update(boolean is_connected, int type,
                                       String type_name, String subtype_name,
                                       boolean is_available, boolean is_roaming) {
            LocAidlNetworkInfoDataItem item = new LocAidlNetworkInfoDataItem();
            item.type = type;
            item.typeName = type_name;
            item.subTypeName = subtype_name;
            item.available = is_available;
            item.connected = is_connected;
            item.roaming = is_roaming;
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.networkinfoUpdate(item);
                } catch (RemoteException e) {
                }
            }
        }

        public void serviceinfo_update(int air_interface_type, String carrierName, int Mcc,
                                       int Mnc) {
            LocAidlRilServiceInfoDataItem item = new LocAidlRilServiceInfoDataItem();
            item.type = air_interface_type;
            item.mcc = Mcc;
            item.mnc = Mnc;
            item.name = carrierName;
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.serviceinfoUpdate(item);
                } catch (RemoteException e) {
                }
            }
        }

        public void cell_update_lte(int air_interface_type, int nwStatus, int iMcc,
                int iMnc, int cid, int pci, int tac, int valid_mask) {
            LocAidlCellLteDataItem item = new LocAidlCellLteDataItem();
            item.type = air_interface_type;
            item.status = nwStatus;
            item.mcc = iMcc;
            item.mnc = iMnc;
            item.cid = cid;
            item.pci = pci;
            item.tac = tac;
            item.mask = valid_mask;
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.cellLteUpdate(item);
                } catch (RemoteException e) {
                }
            }
        }

        public void cell_update_gw(int air_interface_type, int nwStatus, int iMcc, int iMnc,
                int lac, int cid, int valid_mask) {
            LocAidlCellGwDataItem item = new LocAidlCellGwDataItem();
            item.type = air_interface_type;
            item.status = nwStatus;
            item.mcc = iMcc;
            item.mnc = iMnc;
            item.lac = lac;
            item.cid = cid;
            item.mask = valid_mask;
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.cellGwUpdate(item);
                } catch (RemoteException e) {
                }
            }
        }

        public void cell_update_cdma(int air_interface_type, int nwStatus, int sid, int nid,
                int bsid, int bslat, int bslong, boolean inDST, int UTCTimeOffset,
                int valid_mask) {
            LocAidlCellCdmaDataItem item = new LocAidlCellCdmaDataItem();
            item.type = air_interface_type;
            item.status = nwStatus;
            item.sid = sid;
            item.nid = nid;
            item.bsid = bsid;
            item.bslat = bslat;
            item.bslong = bslong;
            item.timeOffset = UTCTimeOffset;
            item.mask = valid_mask;
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.cellCdmaUpdate(item);
                } catch (RemoteException e) {
                }
            }
        }

        public void cell_update_ooo(int nwStatus, int valid_mask) {
            LocAidlCellOooDataItem item = new LocAidlCellOooDataItem();
            item.status = nwStatus;
            item.mask = valid_mask;
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.cellOooUpdate(item);
                } catch (RemoteException e) {
                }
            }
        }

        public void service_state_update(int nwStatus) {
            LocAidlServiceStateDataItem item = new LocAidlServiceStateDataItem();
            item.status = nwStatus;
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.serviceStateUpdate(item);
                } catch (RemoteException e) {
                }
            }
        }

        public void screen_status_update(boolean status) {
            LocAidlScreenStatusDataItem item = new LocAidlScreenStatusDataItem();
            item.status = status;
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.screenStatusUpdate(item);
                } catch (RemoteException e) {
                }
            }
        }

        public void power_connect_status_update(boolean status) {
            LocAidlPowerConnectStatusDataItem item = new LocAidlPowerConnectStatusDataItem();
            item.status = status;
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.powerConnectStatusUpdate(item);
                } catch (RemoteException e) {
                }
            }
        }

        public void timezone_change_update(long currTimeMillis, int rawOffset, int dstOffset) {
            LocAidlTimeZoneChangeDataItem item = new LocAidlTimeZoneChangeDataItem();
            item.curTimeMillis = currTimeMillis;
            item.rawOffset = rawOffset;
            item.dstOffset = dstOffset;
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.timezoneChangeUpdate(item);
                } catch (RemoteException e) {
                }
            }
        }

        public void time_change_update(long currTimeMillis, int rawOffset, int dstOffset) {
            LocAidlTimeChangeDataItem item = new LocAidlTimeChangeDataItem();
            item.curTimeMillis = currTimeMillis;
            item.rawOffset = rawOffset;
            item.dstOffset = dstOffset;
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.timeChangeUpdate(item);
                } catch (RemoteException e) {
                }
            }

        }

        public void shutdown_update() {
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.shutdownUpdate();
                } catch (RemoteException e) {
                }
            }
        }

        public void wifi_supplicant_status_update(int state,
                                                  int ap_mac_valid,
                                                  byte ap_mac_array[],
                                                  int ssid_valid,
                                                  char ssid_array[]) {
            LocAidlWifiSupplicantStatusDataItem item =
                    new LocAidlWifiSupplicantStatusDataItem();
            item.state = state;
            item.apMacAddressValid = (ap_mac_valid != 0);
            item.apSsidValid = (ssid_valid != 0);
            item.apMacAddress = new byte[ap_mac_array.length];
            item.apSsid = new String();
            if (ap_mac_valid != 0) {
                for (int i = 0; i < ap_mac_array.length; i++) {
                    item.apMacAddress[i] = ap_mac_array[i];
                }
            }
            if (ssid_valid != 0) {
                item.apSsid = String.valueOf(ssid_array);
            }

            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.wifiSupplicantStatusUpdate(item);
                } catch (RemoteException e) {
                }
            }
        }

        public void battery_level_update(int batteryPct) {
            LocAidlBatteryLevelDataItem item = new LocAidlBatteryLevelDataItem();
            item.batteryPct = (byte)batteryPct;
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.batteryLevelUpdate(item);
                } catch (RemoteException e) {
                }
            }
        }

        public void btle_scan_data_inject(boolean is_valid,
                                          int rssi,
                                          byte btle_mac_array[],
                                          long btle_scan_req_timestamp,
                                          long scan_start_timestamp,
                                          long scan_recv_timestamp,
                                          int error_code) {
            LocAidlBtLeDeviceScanDetailsDataItem item =
                    new LocAidlBtLeDeviceScanDetailsDataItem();
            item.validSrnData = is_valid;
            item.errorCause = error_code;
            item.apSrnMacAddress = new byte[btle_mac_array.length];
            if (is_valid) {
                item.apSrnRssi = rssi;
                item.apSrnTimestamp = scan_start_timestamp;
                item.requestTimestamp = btle_scan_req_timestamp;
                item.receiveTimestamp = scan_recv_timestamp;
                for (int i = 0; i < btle_mac_array.length; i++) {
                    item.apSrnMacAddress[i] = btle_mac_array[i];
                }
            }
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.btleScanDataInject(item);
                } catch (RemoteException e) {
                }
            }
        }

        public void bt_classic_scan_data_inject(boolean is_valid,
                                                int rssi,
                                                byte bt_mac_array[],
                                                long bt_scan_req_timestamp,
                                                long scan_start_timestamp,
                                                long scan_recv_timestamp,
                                                int error_code) {
            LocAidlBtDeviceScanDetailsDataItem item = new LocAidlBtDeviceScanDetailsDataItem();
            item.validSrnData = is_valid;
            item.errorCause = error_code;
            item.apSrnMacAddress = new byte[bt_mac_array.length];
            if (is_valid) {
                item.apSrnRssi = rssi;
                item.apSrnTimestamp = scan_start_timestamp;
                item.requestTimestamp = bt_scan_req_timestamp;
                item.receiveTimestamp = scan_recv_timestamp;
                for (int i = 0; i < bt_mac_array.length; i++) {
                    item.apSrnMacAddress[i] = bt_mac_array[i];
                }
            }
            if (mOsAgentIface!= null) {
                try {
                    IDLClientUtils.toIDLService(TAG);
                    mOsAgentIface.btClassicScanDataInject(item);
                } catch (RemoteException e) {
                }
            }
        }

    }

}
