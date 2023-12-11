/*
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 *
*/
/******************************************************************************
 *  Copyright (c) 2020, The Linux Foundation. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are
 *  met:
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above
 *        copyright notice, this list of conditions and the following
 *        disclaimer in the documentation and/or other materials provided
 *        with the distribution.
 *      * Neither the name of The Linux Foundation nor the names of its
 *        contributors may be used to endorse or promote products derived
 *        from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
 *  ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 *  BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 *  BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 *  WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 *  OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 *  IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*****************************************************************************/

package com.android.bluetooth.apm;

import static android.Manifest.permission.BLUETOOTH_CONNECT;

import android.bluetooth.BluetoothDevice;
import com.android.bluetooth.hfp.HeadsetService;
import com.android.bluetooth.hfp.HeadsetA2dpSync;
import com.android.bluetooth.apm.ApmConst;
import com.android.bluetooth.apm.MediaAudio;
import com.android.bluetooth.apm.CallControl;
import android.media.AudioManager;
import com.android.bluetooth.apm.ActiveDeviceManagerService;
import com.android.bluetooth.btservice.ActiveDeviceManager;
import com.android.bluetooth.btservice.AdapterService;
import com.android.bluetooth.btservice.ProfileService;
import com.android.bluetooth.btservice.AdapterService;
import com.android.bluetooth.cc.CCService;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import com.android.bluetooth.Utils;
import android.content.Context;
import java.lang.Integer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.util.Log;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import android.content.Intent;
import android.os.UserHandle;

public class CallAudio {

    private static CallAudio mCallAudio;
    private static final String TAG = "APM: CallAudio";
    Map<String, CallDevice> mCallDevicesMap;
    private Context mContext;
    private AudioManager mAudioManager;
    private ActiveDeviceManagerService mActiveDeviceManager;
    private AdapterService mAdapterService;
    public static final String BLUETOOTH_PERM = android.Manifest.permission.BLUETOOTH;
    public static final String BLUETOOTH_ADMIN_PERM = android.Manifest.permission.BLUETOOTH_ADMIN;
    public static final String BLUETOOTH_PRIVILEGED =
            android.Manifest.permission.BLUETOOTH_PRIVILEGED;
    private static final int MAX_DEVICES = 200;
    public boolean mVirtualCallStarted;
    private CallControl mCallControl = null;

    private CallAudio(Context context) {
        Log.d(TAG, "Initialization");
        mContext = context;
        mCallDevicesMap = new ConcurrentHashMap<String, CallDevice>();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mActiveDeviceManager = ActiveDeviceManagerService.get();
        mAdapterService = AdapterService.getAdapterService();
        mCallControl = CallControl.get();
    }

    public static CallAudio init(Context context) {
        if(mCallAudio == null) {
            mCallAudio = new CallAudio(context);
            CallAudioIntf.init(mCallAudio);
        }
        return mCallAudio;
    }

    public static CallAudio get() {
        return mCallAudio;
    }

    public boolean connect(BluetoothDevice device) {
        Log.i(TAG, "connect: " + device);
        mContext.enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM, "Need BLUETOOTH_ADMIN permission");
        if(device == null)
            return false;
        boolean status;
        if (getConnectionPolicy(device) == BluetoothProfile.CONNECTION_POLICY_FORBIDDEN) {
            Log.e(TAG, "Cannot connect to " + device + " : CONNECTION_POLICY_FORBIDDEN");
            return false;
        }

        DeviceProfileMap dMap = DeviceProfileMap.getDeviceProfileMapInstance();
        if (dMap == null)
            return false;

        int profileID = dMap.getSupportedProfile(device, ApmConst.AudioFeatures.CALL_AUDIO);
        if (profileID == ApmConst.AudioProfiles.NONE) {
            Log.e(TAG, "Can Not connect to " + device + ". Device does not support call service.");
            return false;
        }

        CallDevice mCallDevice = mCallDevicesMap.get(device.getAddress());
        if(mCallDevice == null) {
            if(mCallDevicesMap.size() >= MAX_DEVICES)
                return false;
            mCallDevice = new CallDevice(device, profileID);
            mCallDevicesMap.put(device.getAddress(), mCallDevice);
        }
        if (mCallDevice.profileConnStatus[CallDevice.SCO_STREAM] != BluetoothProfile.STATE_CONNECTED) {
          if((ApmConst.AudioProfiles.HFP & profileID) == ApmConst.AudioProfiles.HFP) {
            HeadsetService service = HeadsetService.getHeadsetService();
            if (service == null) {
                return false;
            }
            service.connectHfp(device);
         }
       }
       if (mCallDevice.profileConnStatus[CallDevice.LE_STREAM] == BluetoothProfile.STATE_CONNECTED) {
          Log.i(TAG, "LE call already connected");
          return false;
       }
        StreamAudioService mStreamService = StreamAudioService.getStreamAudioService();
        if(mStreamService != null &&
                (ApmConst.AudioProfiles.BAP_CALL & profileID) == ApmConst.AudioProfiles.BAP_CALL) {
            BluetoothDevice groupDevice = mStreamService.getDeviceGroup(device);
            mStreamService.connectLeStream(groupDevice, profileID);
        }
        return true;
    }

    public boolean connect(BluetoothDevice device, Boolean allProfiles) {
        if(allProfiles) {
            DeviceProfileMap dMap = DeviceProfileMap.getDeviceProfileMapInstance();
            if (dMap == null)
                return false;

            int profileID = dMap.getSupportedProfile(device, ApmConst.AudioFeatures.CALL_AUDIO);
            if((ApmConst.AudioProfiles.HFP & profileID) == ApmConst.AudioProfiles.HFP) {
                HeadsetService service = HeadsetService.getHeadsetService();
                if (service == null) {
                    return false;
                }
                return service.connectHfp(device);
            } else {
                /*Common connect for LE Media and Call handled from StreamAudioService*/
                return true;
            }
        }

        return connect(device);
    }

    public boolean disconnect(BluetoothDevice device) {
        Log.i(TAG, " disconnect: " + device);
        mContext.enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM, "Need BLUETOOTH ADMIN permission");
        CallDevice mCallDevice;

        if(device == null)
            return false;

        mCallDevice = mCallDevicesMap.get(device.getAddress());
        if(mCallDevice == null) {
            Log.e(TAG, "Ignore: Device " + device + " not present in list");
            return false;
        }

        if (mCallDevice.profileConnStatus[CallDevice.SCO_STREAM] != BluetoothProfile.STATE_DISCONNECTED) {
            HeadsetService service = HeadsetService.getHeadsetService();
            if(service != null) {
                service.disconnectHfp(device);
            }
        }

        if (mCallDevice.profileConnStatus[CallDevice.LE_STREAM] != BluetoothProfile.STATE_DISCONNECTED) {
            StreamAudioService service = StreamAudioService.getStreamAudioService();
            if(service != null) {
                service.disconnectLeStream(device, true, false);
            }
        }

        return true;
    }

    public boolean disconnect(BluetoothDevice device, Boolean allProfiles) {
        if(allProfiles) {
            CallDevice mCallDevice = mCallDevicesMap.get(device.getAddress());
            if(mCallDevice == null) {
                Log.e(TAG, "Ignore: Device " + device + " not present in list");
                return false;
            }
            if(mCallDevice.profileConnStatus[CallDevice.SCO_STREAM] != BluetoothProfile.STATE_DISCONNECTED) {
                return disconnect(device);
            } else {
                /*Common connect for LE Media and Call handled from StreamAudioService*/
                return true;
            }
        }

        return disconnect(device);
    }

    public boolean startScoUsingVirtualVoiceCall() {
        mContext.enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM, "Need BLUETOOTH_ADMIN permission");
        Log.d(TAG, "startScoUsingVirtualVoiceCall");
        BluetoothDevice mActivedevice = null;
        int profile;
        mActiveDeviceManager = ActiveDeviceManagerService.get();
        if(mActiveDeviceManager != null) {
            mActivedevice = mActiveDeviceManager.getActiveDevice(ApmConst.AudioFeatures.CALL_AUDIO);
            if(mActivedevice == null) {
                Log.e(TAG, "startScoUsingVirtualVoiceCall failed. Active Device is null");
                return false;
            }
        } else {
            return false;
        }

        mVirtualCallStarted = true;
        checkA2dpState();

        profile = mActiveDeviceManager.getActiveProfile(ApmConst.AudioFeatures.CALL_AUDIO);
        switch(profile) {
            case ApmConst.AudioProfiles.HFP:
                HeadsetService headsetService = HeadsetService.getHeadsetService();
                if(headsetService != null) {
                    if(headsetService.startScoUsingVirtualVoiceCall()) {
                        return true;
                    }
                }
                break;
            case ApmConst.AudioProfiles.BAP_CALL:
            case ApmConst.AudioProfiles.TMAP_CALL:
                StreamAudioService mStreamAudioService = StreamAudioService.getStreamAudioService();
                if(mStreamAudioService != null) {
                    if(mStreamAudioService.startStream(mActivedevice)) {
                        mCallControl = CallControl.get();
                        if (mCallControl != null) {
                            mCallControl.setVirtualCallActive(true);
                        }
                        return true;
                    }
                }
                break;
            default:
                Log.e(TAG, "Unhandled profile");
                break;
        }

        Log.e(TAG, "startScoUsingVirtualVoiceCall failed. Device: " + mActivedevice);
        mVirtualCallStarted = false;
        if(ApmConst.AudioProfiles.HFP != profile) {
            HeadsetService service = HeadsetService.getHeadsetService();
            if(service != null) {
                service.getHfpA2DPSyncInterface().releaseA2DP(null);
            }
        }
        return false;
    }

    public boolean stopScoUsingVirtualVoiceCall() {
        mContext.enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM, "Need BLUETOOTH_ADMIN permission");
        Log.d(TAG, "stopScoUsingVirtualVoiceCall");
        BluetoothDevice mActivedevice = null;
        int profile;
        mActiveDeviceManager = ActiveDeviceManagerService.get();
        if(mActiveDeviceManager != null) {
            mActivedevice = mActiveDeviceManager.getActiveDevice(ApmConst.AudioFeatures.CALL_AUDIO);
            if(mActivedevice == null) {
                if(mVirtualCallStarted) {
                    mVirtualCallStarted = false;
                    mCallControl = CallControl.get();
                    if (mCallControl != null) {
                        mCallControl.setVirtualCallActive(false);
                    }
                    HeadsetService headsetService = HeadsetService.getHeadsetService();
                    if(headsetService != null) {
                        headsetService.stopScoUsingVirtualVoiceCall();
                    }
                }
                Log.e(TAG, "stopScoUsingVirtualVoiceCall failed. Active Device is null");
                return false;
            }
        } else {
            return false;
        }

        profile = mActiveDeviceManager.getActiveProfile(ApmConst.AudioFeatures.CALL_AUDIO);
        switch(profile) {
            case ApmConst.AudioProfiles.HFP:
                HeadsetService headsetService = HeadsetService.getHeadsetService();
                if(headsetService != null) {
                    mVirtualCallStarted = false;
                    return headsetService.stopScoUsingVirtualVoiceCall();
                }
                break;
            case ApmConst.AudioProfiles.BAP_CALL:
            case ApmConst.AudioProfiles.TMAP_CALL:
                StreamAudioService mStreamAudioService = StreamAudioService.getStreamAudioService();
                if(mStreamAudioService != null) {
                    mVirtualCallStarted = false;
                    mCallControl = CallControl.get();
                    if (mCallControl != null) {
                        mCallControl.setVirtualCallActive(false);
                    }
                    return mStreamAudioService.stopStream(mActivedevice);
                }
                break;
            default:
                Log.e(TAG, "Unhandled profile");
                break;
        }

        Log.e(TAG, "stopScoUsingVirtualVoiceCall failed. Device: " + mActivedevice);
        return false;
    }

    void remoteDisconnectVirtualVoiceCall(BluetoothDevice device) {
        if(device == null)
            return;
        ActiveDeviceManagerService mActiveDeviceManager = ActiveDeviceManagerService.get();
        if(device.equals(mActiveDeviceManager.getActiveDevice(ApmConst.AudioFeatures.CALL_AUDIO)) &&
                        mActiveDeviceManager.isStableState(ApmConst.AudioFeatures.CALL_AUDIO)) {
            stopScoUsingVirtualVoiceCall();
        }
    }

    private int getProfileConnectionState(BluetoothDevice device, int profile) {
        CallDevice mCallDevice = mCallDevicesMap.get(device.getAddress());
        if(mCallDevice == null)
            return BluetoothProfile.STATE_DISCONNECTED;

        return mCallDevice.profileConnStatus[mCallDevice.getProfileIndex(profile)];
    }

    boolean setActiveProfile(BluetoothDevice device, int profile) {
        if(getProfileConnectionState(device, profile) == BluetoothProfile.STATE_DISCONNECTED) {
            return false;
        }

        DeviceProfileMap dMap = DeviceProfileMap.getDeviceProfileMapInstance();
        dMap.setActiveProfile(device, ApmConst.AudioFeatures.CALL_AUDIO, profile);
        mActiveDeviceManager.setActiveDevice(device, ApmConst.AudioFeatures.CALL_AUDIO, false);
        return true;
    }

    int getProfile(BluetoothDevice mDevice) {
        DeviceProfileMap dMap = DeviceProfileMap.getDeviceProfileMapInstance();
        int profileID = dMap.getProfile(mDevice, ApmConst.AudioFeatures.CALL_AUDIO);
        Log.d(TAG," getProfile for device " + mDevice + " profileID " + profileID);
        return profileID;
    }

    void checkA2dpState() {
        MediaAudio sMediaAudio = MediaAudio.get();
        BluetoothDevice sMediaActivedevice =
        mActiveDeviceManager.getActiveDevice(ApmConst.AudioFeatures.MEDIA_AUDIO);
        //if(sMediaAudio.isA2dpPlaying(sMediaActivedevice)) {
            Log.d(TAG," suspendA2DP isA2dpPlaying true " + " for device " + sMediaActivedevice);
            int profileID = mActiveDeviceManager.getActiveProfile(
                                ApmConst.AudioFeatures.CALL_AUDIO);
            if(ApmConst.AudioProfiles.HFP != profileID) {
                HeadsetService service = HeadsetService.getHeadsetService();
                if(service != null) {
                    service.getHfpA2DPSyncInterface().suspendA2DP(
                    HeadsetA2dpSync.A2DP_SUSPENDED_BY_CS_CALL, null);
                }
            }
        //}
    }

    public boolean connectAudio() {
        mContext.enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM, "Need BLUETOOTH_ADMIN permission");
        BluetoothDevice mActivedevice = null;
        boolean status = false;

        mActiveDeviceManager = ActiveDeviceManagerService.get();
        if(mActiveDeviceManager != null) {
                mActivedevice = mActiveDeviceManager.getActiveDevice(ApmConst.AudioFeatures.CALL_AUDIO);
        }
        Log.i(TAG, "connectAudio: device=" + mActivedevice + ", " + Utils.getUidPidString());

        int profileID = mActiveDeviceManager.getActiveProfile(ApmConst.AudioFeatures.CALL_AUDIO);
        checkA2dpState();

        if(ApmConst.AudioProfiles.HFP == profileID) {
            HeadsetService service = HeadsetService.getHeadsetService();
            if (service == null) {
                status = false;
            }
            status = service.connectAudio(mActivedevice);
        } else if(ApmConst.AudioProfiles.BAP_CALL == profileID) {
            boolean isCallActive = false;
            HeadsetService hsService = HeadsetService.getHeadsetService();
            if (hsService != null) {
                isCallActive = (hsService.shouldCallAudioBeActive() || mVirtualCallStarted);
            }
            if (isCallActive) {
                StreamAudioService service = StreamAudioService.getStreamAudioService();
                if(service != null) {
                    status = service.startStream(mActivedevice);
                }
            } else {
                Log.i(TAG, "ignoring connectAudio for BAP as no active call");
            }
        } else {
            Log.e(TAG, "Unhandled connect audio request for profile: " + profileID);
            status = false;
        }

        if(status == false) {
            Log.e(TAG, "failed connect audio request for device: " + mActivedevice);
            if(ApmConst.AudioProfiles.HFP != profileID) {
                HeadsetService service = HeadsetService.getHeadsetService();
                if(service != null) {
                    service.getHfpA2DPSyncInterface().releaseA2DP(null);
                }
            }
        }

        return status;
    }

    public boolean disconnectAudio() {
        mContext.enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM, "Need BLUETOOTH_ADMIN permission");
        BluetoothDevice mActivedevice = null;
        boolean mStatus = false;

        if(mActiveDeviceManager != null) {
                mActivedevice = mActiveDeviceManager.getActiveDevice(ApmConst.AudioFeatures.CALL_AUDIO);
        }
        Log.i(TAG, "disconnectAudio: device=" + mActivedevice + ", " + Utils.getUidPidString());

        int profileID = mActiveDeviceManager.getActiveProfile(ApmConst.AudioFeatures.CALL_AUDIO);

        if(ApmConst.AudioProfiles.HFP == profileID) {
            HeadsetService service = HeadsetService.getHeadsetService();
            if (service == null) {
                    mStatus = false;
            }
            mStatus = service.disconnectAudio();
        } else if(ApmConst.AudioProfiles.BAP_CALL == profileID) {
            StreamAudioService service = StreamAudioService.getStreamAudioService();
            if(service != null) {
                mStatus = service.stopStream(mActivedevice);
            }
        } else {
            Log.e(TAG, "Unhandled disconnectAudio request for profile: " + profileID);
            mStatus = true;
        }

        if(ApmConst.AudioProfiles.HFP != profileID) {
            HeadsetService service = HeadsetService.getHeadsetService();
            if(service != null) {
                service.getHfpA2DPSyncInterface().releaseA2DP(null);
            }
        }
        return mStatus;
    }

    public boolean setConnectionPolicy(BluetoothDevice device, Integer connectionPolicy) {
        mContext.enforceCallingOrSelfPermission(BLUETOOTH_PRIVILEGED,
                "Need BLUETOOTH_PRIVILEGED permission");
        boolean mStatus;

        Log.d(TAG, "setConnectionPolicy: device=" + device
            + ", connectionPolicy=" + connectionPolicy + ", " + Utils.getUidPidString());

        mStatus = mAdapterService.getDatabase()
            .setProfileConnectionPolicy(device, BluetoothProfile.HEADSET, connectionPolicy);

        if (mStatus &&
                connectionPolicy == BluetoothProfile.CONNECTION_POLICY_ALLOWED) {
            connect(device);
        } else if (mStatus &&
                connectionPolicy == BluetoothProfile.CONNECTION_POLICY_FORBIDDEN) {
            disconnect(device);
        }
        return mStatus;
    }

    public int getConnectionPolicy(BluetoothDevice device) {
        if(mAdapterService != null) {
            int connPolicy;
            connPolicy = mAdapterService.getDatabase()
                .getProfileConnectionPolicy(device, BluetoothProfile.HEADSET);
            Log.d(TAG, "getConnectionPolicy: device=" + device
                    + ", connectionPolicy=" + connPolicy);
            return connPolicy;
        } else {
            return BluetoothProfile.CONNECTION_POLICY_UNKNOWN;

        }
    }

    public int getAudioState(BluetoothDevice device) {
        if(device == null)
            return BluetoothHeadset.STATE_AUDIO_DISCONNECTED;
        CallDevice mCallDevice;
        mCallDevice = mCallDevicesMap.get(device.getAddress());
        if (mCallDevice == null) {
            Log.w(TAG, "getAudioState: device " + device + " was never connected/connecting");
            return BluetoothHeadset.STATE_AUDIO_DISCONNECTED;
        }
        return mCallDevice.scoStatus;
    }

    private List<BluetoothDevice> getNonIdleAudioDevices() {
        if(mCallDevicesMap.size() == 0) {
            return new ArrayList<>(0);
        }

        ArrayList<BluetoothDevice> devices = new ArrayList<>();
        for (CallDevice mCallDevice : mCallDevicesMap.values()) {
            if (mCallDevice.scoStatus != BluetoothHeadset.STATE_AUDIO_DISCONNECTED) {
                devices.add(mCallDevice.mDevice);
            }
        }
        return devices;
    }

    public boolean isAudioOn() {
        mContext.enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        int numConnectedAudioDevices = getNonIdleAudioDevices().size();
        Log.d(TAG," isAudioOn: The number of audio connected devices "
                      + numConnectedAudioDevices);
             return numConnectedAudioDevices > 0;
    }

    public List<BluetoothDevice> getConnectedDevices() {
        mContext.enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        Log.i(TAG, "getConnectedDevices: ");
        if(mCallDevicesMap.size() == 0) {
            Log.i(TAG, "no device is Connected:");
            return new ArrayList<>(0);
        }

        List<BluetoothDevice> connectedDevices = new ArrayList<>();
        for(CallDevice mCallDevice : mCallDevicesMap.values()) {
            if(mCallDevice.deviceConnStatus == BluetoothProfile.STATE_CONNECTED) {
                connectedDevices.add(mCallDevice.mDevice);
            }
        }
        Log.i(TAG, "ConnectedDevices: = " + connectedDevices.size());
        return connectedDevices;
    }

    public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] states) {
        mContext.enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");

        Log.i(TAG, "getDevicesMatchingConnectionStates: ");
        List<BluetoothDevice> devices = new ArrayList<>();
        if (states == null) {
            return devices;
        }

        BluetoothDevice [] bondedDevices = null;
        bondedDevices = mAdapterService.getBondedDevices();
        if(bondedDevices == null) {
            return devices;
        }

        for (BluetoothDevice device : bondedDevices) {
            CallDevice mCallDevice;
            int state = BluetoothProfile.STATE_DISCONNECTED;

            DeviceProfileMap dMap = DeviceProfileMap.getDeviceProfileMapInstance();
            if(dMap == null) {
                return new ArrayList<>(0);
            }
            if(dMap.getProfile(device, ApmConst.AudioFeatures.CALL_AUDIO) == ApmConst.AudioProfiles.NONE) {
                continue;
            }

            mCallDevice = mCallDevicesMap.get(device.getAddress());
            if(mCallDevice != null)
                state = mCallDevice.deviceConnStatus;

            for(int s: states) {
                if(s == state) {
                    devices.add(device);
                    break;
                }
            }
        }
        return devices;
    }

    public int getConnectionState(BluetoothDevice device) {
        mContext.enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");

        if(device == null)
            return BluetoothProfile.STATE_DISCONNECTED;
        CallDevice mCallDevice;
        mCallDevice = mCallDevicesMap.get(device.getAddress());
        if(mCallDevice != null)
            return mCallDevice.deviceConnStatus;

        return BluetoothProfile.STATE_DISCONNECTED;
    }

    public boolean isVoiceOrCallActive() {
        boolean isVoiceActive = isAudioOn() || mVirtualCallStarted;
        HeadsetService mHeadsetService = HeadsetService.getHeadsetService();
        if(mHeadsetService != null) {
            isVoiceActive = isVoiceActive || mHeadsetService.isScoOrCallActive();
        }
        return isVoiceActive;
    }

    private void broadcastConnStateChange(BluetoothDevice device, int fromState, int toState) {
        Log.d(TAG,"broadcastConnectionState " + device + ": " + fromState + "->" + toState);
        HeadsetService mHeadsetService = HeadsetService.getHeadsetService();
        if(mHeadsetService == null) {
            Log.w(TAG,"broadcastConnectionState: HeadsetService not initialized. Return!");
            return;
        }

        Intent intent = new Intent(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        intent.putExtra(BluetoothProfile.EXTRA_PREVIOUS_STATE, fromState);
        intent.putExtra(BluetoothProfile.EXTRA_STATE, toState);
        intent.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
        intent.addFlags(Intent.FLAG_RECEIVER_INCLUDE_BACKGROUND);
        mHeadsetService.sendBroadcastAsUser(intent, UserHandle.ALL,
                BLUETOOTH_CONNECT, Utils.getTempAllowlistBroadcastOptions());
    }

    private void broadcastAudioState(BluetoothDevice device, int fromState, int toState) {
         Log.d(TAG,"broadcastAudioState " + device + ": " + fromState + "->" + toState);
        HeadsetService mHeadsetService = HeadsetService.getHeadsetService();
        if(mHeadsetService == null) {
            Log.d(TAG,"broadcastAudioState: HeadsetService not initialized. Return!");
            return;
        }

        Intent intent = new Intent(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED);
        intent.putExtra(BluetoothProfile.EXTRA_PREVIOUS_STATE, fromState);
        intent.putExtra(BluetoothProfile.EXTRA_STATE, toState);
        intent.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
        mHeadsetService.sendBroadcastAsUser(intent, UserHandle.ALL,
                BLUETOOTH_CONNECT, Utils.getTempAllowlistBroadcastOptions());
    }

    public void onConnStateChange(BluetoothDevice device, int state, int profile, boolean isFirstMember) {
        Log.w(TAG, "onConnStateChange: state:" + state + " for device " + device + " new group: " + isFirstMember);
        if((state == BluetoothProfile.STATE_CONNECTED || state == BluetoothProfile.STATE_CONNECTING)
                        && isFirstMember) {
            StreamAudioService mStreamAudioService = StreamAudioService.getStreamAudioService();
            BluetoothDevice groupDevice = mStreamAudioService.getDeviceGroup(device);
            if(groupDevice != null) {
                CallDevice mCallDevice = mCallDevicesMap.get(groupDevice.getAddress());
                if(mCallDevice == null) {
                    mCallDevice = new CallDevice(groupDevice, profile, BluetoothProfile.STATE_CONNECTED);
                    mCallDevicesMap.put(groupDevice.getAddress(), mCallDevice);
                } else {
                    int profileIndex = mCallDevice.getProfileIndex(profile);
                    mCallDevice.profileConnStatus[profileIndex] = BluetoothProfile.STATE_CONNECTED;
                    mCallDevice.deviceConnStatus = state;
                }
            }
        } else if(isFirstMember && (state == BluetoothProfile.STATE_DISCONNECTING ||
                        state == BluetoothProfile.STATE_DISCONNECTED)) {
            StreamAudioService mStreamAudioService = StreamAudioService.getStreamAudioService();
            BluetoothDevice groupDevice = mStreamAudioService.getDeviceGroup(device);
            CallDevice mCallDevice = mCallDevicesMap.get(groupDevice.getAddress());
            int prevState = BluetoothProfile.STATE_CONNECTED;
            Log.w(TAG, "onConnStateChange: mCallDevice: " + mCallDevice);
            if(mCallDevice != null) {
                prevState = mCallDevice.deviceConnStatus;
                int profileIndex = mCallDevice.getProfileIndex(profile);
                mCallDevice.profileConnStatus[profileIndex] = state;
                mCallDevice.deviceConnStatus = state;
                Log.w(TAG, "onConnStateChange: device: " + groupDevice + " state = " +  mCallDevice.deviceConnStatus);
            }
            ActiveDeviceManager mDeviceManager = AdapterService.getAdapterService().getActiveDeviceManager();
            mDeviceManager.onDeviceConnStateChange(groupDevice, state, prevState,
                            ApmConst.AudioFeatures.CALL_AUDIO);
        }
        onConnStateChange(device, state, profile);
    }

    public void onConnStateChange(BluetoothDevice device, Integer state, Integer profile) {
        int prevState;
        Log.w(TAG, "onConnStateChange: profile: " + profile + " state: "
                                                  + state + " for device " + device);
        if(device == null)
            return;
        CallDevice mCallDevice = mCallDevicesMap.get(device.getAddress());

        Log.w(TAG, "onConnStateChange: mCallDevice:" + mCallDevice);
        if(mCallDevice == null) {
            if (state == BluetoothProfile.STATE_DISCONNECTED)
                return;
            if (mCallDevicesMap.size() >= MAX_DEVICES) {
                Log.w(TAG, "onConnStateChange: Reached max devices.");
                return;
            }
            mCallDevice = new CallDevice(device, profile, state);
            mCallDevicesMap.put(device.getAddress(), mCallDevice);
            broadcastConnStateChange(device, BluetoothProfile.STATE_DISCONNECTED, state);
            return;
        }

        int profileIndex = mCallDevice.getProfileIndex(profile);
        DeviceProfileMap dMap = DeviceProfileMap.getDeviceProfileMapInstance();
        prevState = mCallDevice.deviceConnStatus;
        mCallDevice.profileConnStatus[profileIndex] = state;

        if(state == BluetoothProfile.STATE_DISCONNECTED) {
            dMap.profileConnectionUpdate(device, ApmConst.AudioFeatures.CALL_AUDIO, profile, false);
        }

        int otherProfileConnectionState = mCallDevice.profileConnStatus[(profileIndex+1)%2];
        Log.w(TAG, " otherProfileConnectionState: " + otherProfileConnectionState);

        switch(otherProfileConnectionState) {
        /*Send Broadcast based on state of other profile*/
            case BluetoothProfile.STATE_DISCONNECTED:
                broadcastConnStateChange(device, prevState, state);
                mCallDevice.deviceConnStatus = state;
                if(state == BluetoothProfile.STATE_CONNECTED) {
                    int supportedProfiles = dMap.getSupportedProfile(device, ApmConst.AudioFeatures.CALL_AUDIO);
                    if(profile == ApmConst.AudioProfiles.HFP &&
                            (supportedProfiles & ApmConst.AudioProfiles.BAP_CALL) == ApmConst.AudioProfiles.BAP_CALL) {
                        Log.w(TAG, "Connect LE Voice after HFP auto connect from remote");
                        StreamAudioService mStreamService = StreamAudioService.getStreamAudioService();
                        if(mStreamService != null) {
                            mStreamService.connectLeStream(device, ApmConst.AudioProfiles.BAP_CALL);
                        }
                    } else {
                        ActiveDeviceManagerService mActiveDeviceManager = ActiveDeviceManagerService.get();
                        mActiveDeviceManager.setActiveDevice(device, ApmConst.AudioFeatures.CALL_AUDIO);
                    }
                }
                break;
            case BluetoothProfile.STATE_CONNECTING:
                int preferredProfile = dMap.getProfile(device, ApmConst.AudioFeatures.CALL_AUDIO);
                boolean isPreferredProfile = (preferredProfile == profile);
                if(state == BluetoothProfile.STATE_CONNECTED && isPreferredProfile) {
                    broadcastConnStateChange(device, prevState, state);
                    mCallDevice.deviceConnStatus = state;
                }
                break;
            case BluetoothProfile.STATE_DISCONNECTING:
                if(state == BluetoothProfile.STATE_CONNECTING ||
                        state == BluetoothProfile.STATE_CONNECTED) {
                    broadcastConnStateChange(device, prevState, state);
                    mCallDevice.deviceConnStatus = state;
                }
                break;
            case BluetoothProfile.STATE_CONNECTED:
                if(state == BluetoothProfile.STATE_CONNECTED) {
                    dMap.profileConnectionUpdate(device, ApmConst.AudioFeatures.CALL_AUDIO, profile, true);
                    if(prevState != state) {
                        broadcastConnStateChange(device, prevState, state);
                        mCallDevice.deviceConnStatus = state;
                    }
                    ActiveDeviceManagerService mActiveDeviceManager =
                                     ActiveDeviceManagerService.get();
                    mActiveDeviceManager.setActiveDevice(device, ApmConst.AudioFeatures.CALL_AUDIO);
                } else if(state == BluetoothProfile.STATE_DISCONNECTED) {
                    if(prevState != BluetoothProfile.STATE_CONNECTED) {
                        broadcastConnStateChange(device, prevState, BluetoothProfile.STATE_CONNECTED);
                        mCallDevice.deviceConnStatus = BluetoothProfile.STATE_CONNECTED;
                    } else {
                        ActiveDeviceManagerService mActiveDeviceManager =
                                     ActiveDeviceManagerService.get();
                        if(device.equals(mActiveDeviceManager.getActiveDevice(ApmConst.AudioFeatures.CALL_AUDIO))) {
                            mActiveDeviceManager.setActiveDevice(device, ApmConst.AudioFeatures.CALL_AUDIO);
                        }
                    }
                }
                break;
        }

        if (state == BluetoothProfile.STATE_CONNECTED) {
            dMap.profileConnectionUpdate(device, ApmConst.AudioFeatures.CALL_AUDIO, profile, true);
        }
    }

    public void onAudioStateChange(BluetoothDevice device, Integer state) {
        int prevStatus;
        if(device == null)
            return;
        CallDevice mCallDevice = mCallDevicesMap.get(device.getAddress());
        if(mCallDevice == null) {
            return;
        }

        if(mCallDevice.scoStatus == state)
            return;

        HeadsetService service = HeadsetService.getHeadsetService();
        CCService ccService = CCService.getCCService();
        int profileID = mActiveDeviceManager.getActiveProfile(
                               ApmConst.AudioFeatures.CALL_AUDIO);
        BluetoothDevice mActivedevice = mActiveDeviceManager.getActiveDevice(
                               ApmConst.AudioFeatures.CALL_AUDIO);
        if (state == BluetoothHeadset.STATE_AUDIO_CONNECTED) {
            if (ccService != null) {
                if(!(ccService.shouldCallAudioBeActive() || mVirtualCallStarted)) {
                    if(ApmConst.AudioProfiles.BAP_CALL  == profileID) {
                        StreamAudioService mStreamAudioService =
                                    StreamAudioService.getStreamAudioService();
                        if(mStreamAudioService != null) {
                            Log.w(TAG, "Call not active, disconnect stream");
                            mStreamAudioService.stopStream(mActivedevice);
                        }
                    }
                }
            }
        }

        prevStatus = mCallDevice.scoStatus;
        mCallDevice.scoStatus = state;
        VolumeManager mVolumeManager = VolumeManager.get();
        mVolumeManager.updateStreamState(device, state, ApmConst.AudioFeatures.CALL_AUDIO);
        broadcastAudioState(device, prevStatus, state);
        if(state == BluetoothHeadset.STATE_AUDIO_DISCONNECTED) {
            if(ApmConst.AudioProfiles.HFP != profileID) {
                if(service != null) {
                    service.getHfpA2DPSyncInterface().releaseA2DP(null);
                }
            }
          //mAudioManager.setBluetoothScoOn(false);
        } /*else {
          mAudioManager.setBluetoothScoOn(true);
        }*/
    }

    public void setAudioParam(String param) {
        mAudioManager.setParameters(param);
    }

    public void setBluetoothScoOn(boolean on) {
        mAudioManager.setBluetoothScoOn(on);
    }

    public AudioManager getAudioManager() {
        return mAudioManager;
    }

    class CallDevice {
        BluetoothDevice mDevice;
        int[] profileConnStatus = new int[2];
        int deviceConnStatus;
        int scoStatus;

        public static final int SCO_STREAM = 0;
        public static final int LE_STREAM = 1;

        CallDevice(BluetoothDevice device, int profile, int state) {
            mDevice = device;
            if(profile == ApmConst.AudioProfiles.HFP) {
                profileConnStatus[SCO_STREAM] = state;
                profileConnStatus[LE_STREAM] = BluetoothProfile.STATE_DISCONNECTED;
            } else {
                profileConnStatus[LE_STREAM] = state;
                profileConnStatus[SCO_STREAM] = BluetoothProfile.STATE_DISCONNECTED;
            }
            deviceConnStatus = state;
            scoStatus = BluetoothHeadset.STATE_AUDIO_DISCONNECTED;;
        }

        CallDevice(BluetoothDevice device, int profile) {
            this(device, profile, BluetoothProfile.STATE_DISCONNECTED);
        }

        public int getProfileIndex(int profile) {
            if(profile == ApmConst.AudioProfiles.HFP)
                return SCO_STREAM;
            else
                return LE_STREAM;
        }
    }
}

