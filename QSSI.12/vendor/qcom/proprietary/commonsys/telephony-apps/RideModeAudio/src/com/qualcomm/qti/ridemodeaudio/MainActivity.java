/*
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */

package com.qualcomm.qti.ridemodeaudio;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.telephony.PreciseCallState;
import android.telephony.PhoneStateListener;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.qualcomm.qti.ridemodeaudio.R;

public class MainActivity extends AppCompatActivity {
    public static final int INVALID_PHONE_ID = -1;
    public static final String EXTRA_PHONE_ID = "phoneId";
    public static final Uri FILE_BASE_URI = Uri.parse("content://media/external/audio/media");
    private static final int ANTITHEFT_VERIFIER_PERMISSION_REQUEST = 99;
    private static final String[] RUNTIME_PERMISSIONS = {Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final String LOG_TAG = "RideModeAudio";

    private AudioManager mAudioManager;
    private ContentResolver mContentResolver;
    private List<Audio> mAudioList;
    private MediaPlayer mMediaPlayer;
    private RadioGroup mRadioGroup;
    private RadioButton mEnableRadio, mDisableRadio;
    private RecyclerAdapter mRecyclerAdapter;
    private RecyclerView mRecyclerView;
    private PreciseCallStateListener mPreciseCallStateListener;
    private TelephonyManager mBaseTelephonyManager;
    private SubscriptionManager mSubscriptionManager;
    private int mPhoneId = INVALID_PHONE_ID;
    Map<Integer, PreciseCallStateListener> mTelephonyCallbacks;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
        int[] grantResults) {

        boolean granted = false;

        if (requestCode == ANTITHEFT_VERIFIER_PERMISSION_REQUEST) {
            for (String str : permissions) {
                Log.d(LOG_TAG, "request Permission :." + str);
            }

            /** Check all permission granted */
            if (grantResults.length > 0) {
                granted = true;

                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        granted = false;
                        Log.d(LOG_TAG, "Permission not be granted");
                        break;
                    }
                }
            }

            if (granted) {
                continueCreate();
                return;
            } else {
                Log.d(LOG_TAG, "Permission not granted.");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        for (String runtimePermission : RUNTIME_PERMISSIONS) {
            Log.d(LOG_TAG, "Checking permissions for: " + runtimePermission);
            if (checkSelfPermission(runtimePermission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(RUNTIME_PERMISSIONS, ANTITHEFT_VERIFIER_PERMISSION_REQUEST);
                return;
            }
        }
        continueCreate();
    }

    private void continueCreate() {
        Log.d(LOG_TAG, "continueCreated.");
        setContentView(R.layout.activity_main);

        mRadioGroup = (RadioGroup)findViewById(R.id.radio_group);
        mEnableRadio = (RadioButton)findViewById(R.id.enable_ridemode);
        mDisableRadio = (RadioButton)findViewById(R.id.disable_ridemode);
        mRadioGroup.setOnCheckedChangeListener(new RideModeRadioButtonListener());

        mBaseTelephonyManager = getSystemService(TelephonyManager.class);
        mSubscriptionManager = getSystemService(SubscriptionManager.class);
        mContentResolver = getContentResolver();
        mAudioManager = getSystemService(AudioManager.class);

        retrieveAvailableAudio();
        initView();
        mTelephonyCallbacks = new TreeMap<>();
    }

    private int getSubscriptionId() {
        if (mSubscriptionManager == null) {
            return mSubscriptionManager.INVALID_SUBSCRIPTION_ID;
        }

        SubscriptionInfo subInfo = mSubscriptionManager.
        getActiveSubscriptionInfoForSimSlotIndex(mPhoneId);
        if (subInfo == null) {
            return mSubscriptionManager.INVALID_SUBSCRIPTION_ID;
        }
        return subInfo.getSubscriptionId();
    }

    class RideModeRadioButtonListener implements OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.enable_ridemode) {
                updatePhoneListeners(true);
            } else if (checkedId == R.id.disable_ridemode) {
                updatePhoneListeners(false);
            }
        }
    };

    /**
     * retrieve available audio name and uris from Media database
     * and set to audiolist
     */
    private void retrieveAvailableAudio() {
        Cursor cursor = getFolderCursor(mContentResolver);
        if (cursor == null) {
            return;
        }

        mAudioList = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
                String displayName = cursor.getString(cursor.getColumnIndex(
                        MediaStore.Audio.Media.DISPLAY_NAME));
                String uriString = FILE_BASE_URI + "/" + id;
                Log.d(LOG_TAG, "audio name is: " + displayName +
                        " uriString is: " + uriString);
                mAudioList.add(new Audio(displayName, uriString));
            }
        } finally {
            cursor.close();
        }
    }

    /**
     * Initialize the UI with list of available audio resources on the device.
     */
    private void initView() {
        mRecyclerView = findViewById(R.id.audio_item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        if (!mAudioList.isEmpty())  {
            mRecyclerAdapter = new RecyclerAdapter(mAudioList);
            mRecyclerView.setAdapter(mRecyclerAdapter);
        }
    }

    private Cursor getFolderCursor(ContentResolver resolver) {
        String[] projection = {MediaStore.Files.FileColumns._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATE_MODIFIED};
        String selection = MediaStore.Audio.Media.ALBUM + " = 'Music'";
        return query(resolver, FILE_BASE_URI, projection, selection.toString(),null, null);
    }

    private Cursor query(ContentResolver resolver, Uri uri, String[] projection,
            String selection, String[] selectionArgs, String sortOrder) {
        try {
            if (resolver == null) {
                return null;
            }
            return resolver.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (UnsupportedOperationException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private AudioDeviceInfo getTelephonyDevice() {
        AudioDeviceInfo[] deviceList = mAudioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
        for (AudioDeviceInfo device: deviceList) {
            if (device.getType() == AudioDeviceInfo.TYPE_TELEPHONY) {
                return device;
            }
        }
        return null;
    }

    private void playRecording() {
        mMediaPlayer = new MediaPlayer();
        try {
            mAudioManager.requestAudioFocus(null,AudioManager.STREAM_MUSIC,
                  AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            Uri contentUri = Uri.parse(mRecyclerAdapter.mUriString);
            Log.d(LOG_TAG, "playRecording uriString is: " + contentUri);

            mMediaPlayer.setDataSource(this, contentUri);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        AudioDeviceInfo telephonyDevice = getTelephonyDevice();
            mMediaPlayer.setPreferredDevice(telephonyDevice);
            mMediaPlayer.prepare();
            mMediaPlayer.start();

        } catch (IOException e) {
            Log.w(LOG_TAG, "Unable to play track");
        } catch (NullPointerException e) {
            Log.w(LOG_TAG, "The mSelectedUri is invalid");
        }
    }

    private class PreciseCallStateListener extends TelephonyCallback
            implements TelephonyCallback.PreciseCallStateListener {

        @Override
        public void onPreciseCallStateChanged(PreciseCallState preciseCallState) {
            Log.d(LOG_TAG, "call state is:" + preciseCallState.getForegroundCallState());
            if (preciseCallState.getForegroundCallState() ==
                    preciseCallState.PRECISE_CALL_STATE_ACTIVE) {
                playRecording();
            }
        }
    }

    /**
     * Enable/Disable ride mode feature.
     */
    private void updatePhoneListeners(boolean enabled) {
        final int numPhones = mBaseTelephonyManager.getActiveModemCount();
        for (int i = 0; i < numPhones; i++) {
            final SubscriptionInfo subInfo =
            mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(i);
            if (subInfo != null) {
                final int subId = subInfo.getSubscriptionId();
                TelephonyManager mgr = mBaseTelephonyManager.createForSubscriptionId(subId);
                if (enabled) {
                    //enable ride mode
                    PreciseCallStateListener telephonyCallback = new PreciseCallStateListener();
                    mTelephonyCallbacks.put(Integer.valueOf(subId), telephonyCallback);
                    mgr.registerTelephonyCallback(this.getMainExecutor(),
                    mTelephonyCallbacks.get(subId));
                } else {
                    //disable ride mode
                    if (mTelephonyCallbacks != null){
                        mgr.unregisterTelephonyCallback(mTelephonyCallbacks.get(subId));
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "onDestroy()");
        updatePhoneListeners(false);
    }
}
