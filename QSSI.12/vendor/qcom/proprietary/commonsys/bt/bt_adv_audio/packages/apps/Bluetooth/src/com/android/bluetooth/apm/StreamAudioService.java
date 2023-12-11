/*
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 *
*/
/*
 * Copyright (c) 2020, The Linux Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
       * Redistributions of source code must retain the above copyright
         notice, this list of conditions and the following disclaimer.
       * Redistributions in binary form must reproduce the above
         copyright notice, this list of conditions and the following
         disclaimer in the documentation and/or other materials provided
         with the distribution.
       * Neither the name of The Linux Foundation nor the names of its
         contributors may be used to endorse or promote products derived
         from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **************************************************************************/

package com.android.bluetooth.apm;

import static com.android.bluetooth.Utils.enforceBluetoothPermission;
import static com.android.bluetooth.Utils.enforceBluetoothPrivilegedPermission;

import android.bluetooth.BluetoothCodecConfig;
import android.bluetooth.BluetoothCodecStatus;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.IBluetoothVcp;

import android.content.AttributionSource;
import android.os.Binder;
import android.os.HandlerThread;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;

import com.android.bluetooth.btservice.AdapterService;
import com.android.bluetooth.btservice.ProfileService;
import com.android.bluetooth.acm.AcmService;
import com.android.bluetooth.Utils;

public class StreamAudioService extends ProfileService {
    private static final boolean DBG = true;
    private static final String TAG = "APM: StreamAudioService:";
    public static final int LE_AUDIO_UNICAST = 26;

    public static final String CoordinatedAudioServiceName = "com.android.bluetooth.acm.AcmService";
    public static final int COORDINATED_AUDIO_UNICAST = AcmService.ACM_AUDIO_UNICAST;

    private static StreamAudioService sStreamAudioService;
    private ActiveDeviceManagerService mActiveDeviceManager;
    private MediaAudio mMediaAudio;
    private VolumeManager mVolumeManager;
    private final Object mVolumeManagerLock = new Object();
    @Override
    protected void create() {
        Log.i(TAG, "create()");
    }

    private static final int BAP       = 0x01;
    private static final int GCP       = 0x02;
    private static final int WMCP      = 0x04;
    private static final int VMCP      = 0x08;
    private static final int BAP_CALL  = 0x10;
    private static final int GCP_VBC   = 0x20;

    private static final int MEDIA_CONTEXT = 1;
    private static final int VOICE_CONTEXT = 2;

    @Override
    protected boolean start() {
        if(sStreamAudioService != null) {
            Log.i(TAG, "StreamAudioService already started");
            return true;
        }
        Log.i(TAG, "start()");

        ApmConst.setLeAudioEnabled(true);
        ApmConstIntf.init();

        setStreamAudioService(this);

        mActiveDeviceManager = ActiveDeviceManagerService.get(this);
        mMediaAudio = MediaAudio.init(this);

        DeviceProfileMap dpm = DeviceProfileMap.getDeviceProfileMapInstance();
        dpm.init(this);
        CallAudio mCallAudio = CallAudio.init(this);
        synchronized (mVolumeManagerLock) {
            mVolumeManager = VolumeManager.init(this);
        }

        Log.i(TAG, "start() complete");
        return true;
    }

    @Override
    protected boolean stop() {
        Log.w(TAG, "stop() called");
        if (sStreamAudioService == null) {
            Log.w(TAG, "stop() called before start()");
            return true;
        }

        if (mActiveDeviceManager != null) {
            mActiveDeviceManager.disable();
            mActiveDeviceManager.cleanup();
        }

        DeviceProfileMap dMap = DeviceProfileMap.getDeviceProfileMapInstance();
        dMap.cleanup();
        mMediaAudio.cleanup();
        return true;
    }

    @Override
    protected void cleanup() {
        Log.i(TAG, "cleanup()");
        synchronized (mVolumeManagerLock) {
            mVolumeManager.cleanup();
            mVolumeManager = null;
        }
        setStreamAudioService(null);
    }

    public boolean connectLeStream(BluetoothDevice device, int profile) {
        AcmService mAcmService = AcmService.getAcmService();
        int mContext = getContext(profile);

        if(mContext == 0) {
            Log.e(TAG, "No valid context for profiles passed");
            return false;
        }
        return mAcmService.connect(device, mContext, getAcmProfileID(profile), MEDIA_CONTEXT);
        //return mAcmService.connect(device, VOICE_CONTEXT, BAP_CALL, VOICE_CONTEXT);
        //return mAcmService.connect(device, MEDIA_CONTEXT, BAP|WMCP, MEDIA_CONTEXT);
    }

    public boolean disconnectLeStream(BluetoothDevice device, boolean callAudio, boolean mediaAudio) {
        AcmService mAcmService = AcmService.getAcmService();
        if(callAudio && mediaAudio)
            return mAcmService.disconnect(device, VOICE_CONTEXT | MEDIA_CONTEXT);
            //return mAcmService.disconnect(device, VOICE_CONTEXT);
            //return mAcmService.disconnect(device, MEDIA_CONTEXT);
        else if(mediaAudio)
            return mAcmService.disconnect(device, MEDIA_CONTEXT);
        else if(callAudio)
            return mAcmService.disconnect(device, VOICE_CONTEXT);

        return false;
    }

    public boolean startStream(BluetoothDevice device) {
        AcmService mAcmService = AcmService.getAcmService();
        return mAcmService.StartStream(device, VOICE_CONTEXT);
    }

    public boolean stopStream(BluetoothDevice device) {
        AcmService mAcmService = AcmService.getAcmService();
        return mAcmService.StopStream(device, VOICE_CONTEXT);
    }

    public int setActiveDevice(BluetoothDevice device, int profile, boolean playReq) {
        AcmService mAcmService = AcmService.getAcmService();
        if (mAcmService == null && device == null) {
            Log.w(TAG, ": device is null, fake success.");
            return mActiveDeviceManager.SHO_SUCCESS;
        }

        if(ApmConst.AudioProfiles.BAP_MEDIA == profile) {
            return mAcmService.setActiveDevice(device, MEDIA_CONTEXT, BAP, playReq);
        } else if(ApmConst.AudioProfiles.BAP_GCP == profile){
            return mAcmService.setActiveDevice(device, MEDIA_CONTEXT, GCP, playReq);
        } else if(ApmConst.AudioProfiles.BAP_RECORDING == profile){
            return mAcmService.setActiveDevice(device, MEDIA_CONTEXT, WMCP, playReq);
        } else if(ApmConst.AudioProfiles.BAP_GCP_VBC == profile){
            return mAcmService.setActiveDevice(device, MEDIA_CONTEXT, GCP_VBC, playReq);
        } else {
            return mAcmService.setActiveDevice(device, VOICE_CONTEXT, BAP_CALL, playReq);
            //return mAcmService.setActiveDevice(device, MEDIA_CONTEXT, BAP, playReq);
        }
    }

    public void setCodecConfig(BluetoothDevice device, String codecID, int channelMode) {
        AcmService mAcmService = AcmService.getAcmService();
        mAcmService.ChangeCodecConfigPreference(device, codecID);
    }

    public BluetoothDevice getDeviceGroup(BluetoothDevice device){
        AcmService mAcmService = AcmService.getAcmService();
        return mAcmService.getGroup(device);
    }

    public void onConnectionStateChange(BluetoothDevice device, int state,
                                        int audioType, boolean primeDevice) {
        Log.w(TAG, "onConnectionStateChange: state:" + state +
                   " for device " + device + " audioType: " + audioType +
                   " primeDevice: " + primeDevice);
        MediaAudio mMediaAudio = MediaAudio.get();
        CallAudio mCallAudio = CallAudio.get();
        int profile = ApmConst.AudioFeatures.MAX_AUDIO_FEATURES;
        boolean isCsipDevice = (device != null) &&
                    getDeviceGroup(device).getAddress().contains(ApmConst.groupAddress);
        Log.w(TAG, "onConnectionStateChange: isCsipDevice:" + isCsipDevice);
        if(audioType == ApmConst.AudioFeatures.CALL_AUDIO) {
            if(isCsipDevice)
                mCallAudio.onConnStateChange(device, state, ApmConst.AudioProfiles.BAP_CALL, primeDevice);
            else
                mCallAudio.onConnStateChange(device, state, ApmConst.AudioProfiles.BAP_CALL);
        } else if(audioType == ApmConst.AudioFeatures.MEDIA_AUDIO) {
            if(isCsipDevice)
                mMediaAudio.onConnStateChange(device, state, ApmConst.AudioProfiles.BAP_MEDIA, primeDevice);
            else
                mMediaAudio.onConnStateChange(device, state, ApmConst.AudioProfiles.BAP_MEDIA);
        }
    }

    public void onStreamStateChange(BluetoothDevice device, int state, int audioType) {
        MediaAudio mMediaAudio = MediaAudio.get();
        CallAudio mCallAudio = CallAudio.get();
        if(audioType == ApmConst.AudioFeatures.MEDIA_AUDIO)
            mMediaAudio.onStreamStateChange(device, state);
        else if(audioType == ApmConst.AudioFeatures.CALL_AUDIO)
             mCallAudio.onAudioStateChange(device, state);
    }

    public void onActiveDeviceChange(BluetoothDevice device, int audioType) {
        if (mActiveDeviceManager != null)
            mActiveDeviceManager.onActiveDeviceChange(device, audioType);
    }

    public void onMediaCodecConfigChange(BluetoothDevice device, BluetoothCodecStatus codecStatus, int audioType) {
        MediaAudio mMediaAudio = MediaAudio.get();
        mMediaAudio.onCodecConfigChange(device, codecStatus, ApmConst.AudioProfiles.BAP_MEDIA);
    }

    public void onMediaCodecConfigChange(BluetoothDevice device, BluetoothCodecStatus codecStatus, int audioType, boolean updateAudio) {
        MediaAudio mMediaAudio = MediaAudio.get();
        mMediaAudio.onCodecConfigChange(device, codecStatus, ApmConst.AudioProfiles.BAP_MEDIA, updateAudio);
    }

    public void setCallAudioParam(String param) {
        CallAudio mCallAudio = CallAudio.get();
        mCallAudio.setAudioParam(param);
    }

    public void setCallAudioOn(boolean on) {
        CallAudio mCallAudio = CallAudio.get();
        mCallAudio.setBluetoothScoOn(on);
    }

    public int getVcpConnState(BluetoothDevice device) {
        synchronized (mVolumeManagerLock) {
            if (mVolumeManager == null)
                return BluetoothProfile.STATE_DISCONNECTED;
            return mVolumeManager.getConnectionState(device);
        }
    }

    public int getConnectionMode(BluetoothDevice device) {
        synchronized (mVolumeManagerLock) {
            if (mVolumeManager == null)
                return BluetoothProfile.STATE_DISCONNECTED;
            return mVolumeManager.getConnectionMode(device);
        }
    }

    public void setAbsoluteVolume(BluetoothDevice device, int volume) {
        synchronized (mVolumeManagerLock) {
            if (mVolumeManager != null)
                mVolumeManager.updateBroadcastVolume(device, volume);
        }
    }

    public int getAbsoluteVolume(BluetoothDevice device) {
        synchronized (mVolumeManagerLock) {
            if (mVolumeManager == null)
                return 7;
            return mVolumeManager.getBassVolume(device);
        }
    }

    public void setMute(BluetoothDevice device, boolean muteStatus) {
        synchronized (mVolumeManagerLock) {
            if (mVolumeManager != null)
                mVolumeManager.setMute(device, muteStatus);
        }
    }

    public boolean isMute(BluetoothDevice device) {
        synchronized (mVolumeManagerLock) {
            if (mVolumeManager == null)
                return false;
            return mVolumeManager.getMuteStatus(device);
        }
    }

    boolean setActiveProfile(BluetoothDevice device, int audioType, int profile) {
        MediaAudio mMediaAudio = MediaAudio.get();
        CallAudio mCallAudio = CallAudio.get();
        if(audioType == ApmConst.AudioFeatures.MEDIA_AUDIO)
            return mMediaAudio.setActiveProfile(device, profile);
        else if(audioType == ApmConst.AudioFeatures.CALL_AUDIO)
             return mCallAudio.setActiveProfile(device, profile);
        return false;
    }

    int getActiveProfile(int audioType) {
        return mActiveDeviceManager.getActiveProfile(audioType);
    }

    private int getContext(int profileID) {
        int context = 0;
        if((DeviceProfileMap.getLeMediaProfiles() & profileID) > 0) {
            context = (context|MEDIA_CONTEXT);
        }

        if((DeviceProfileMap.getLeCallProfiles() & profileID) > 0) {
            context = (context|VOICE_CONTEXT);
        }
        return context;
    }

    private int getAcmProfileID (int ProfileID) {
        int AcmProfileID = 0;
        if((ApmConst.AudioProfiles.BAP_MEDIA & ProfileID) == ApmConst.AudioProfiles.BAP_MEDIA)
            AcmProfileID = BAP;
        if((ApmConst.AudioProfiles.BAP_CALL & ProfileID) == ApmConst.AudioProfiles.BAP_CALL)
            AcmProfileID = AcmProfileID | BAP_CALL;
        if((ApmConst.AudioProfiles.BAP_GCP & ProfileID) == ApmConst.AudioProfiles.BAP_GCP)
            AcmProfileID = AcmProfileID | GCP;
        if((ApmConst.AudioProfiles.BAP_RECORDING & ProfileID) == ApmConst.AudioProfiles.BAP_RECORDING)
            AcmProfileID = AcmProfileID | WMCP;
        return AcmProfileID;
    }

    @Override
    protected IProfileServiceBinder initBinder() {
        return new LeAudioUnicastBinder(this);
    }

    private static class LeAudioUnicastBinder extends IBluetoothVcp.Stub implements IProfileServiceBinder {
        StreamAudioService mService;

        private StreamAudioService getService() {
            if (mService != null && mService.isAvailable()) {
                return mService;
            }
            return null;
        }

        LeAudioUnicastBinder(StreamAudioService service) {
            mService = service;
        }

        @Override
        public void cleanup() {
        }

        @Override
        public int getConnectionState(BluetoothDevice device, AttributionSource source) {
            if (DBG) {
                Log.d(TAG, "getConnectionState(): device: " + device);
            }
            StreamAudioService service = getService();
            if(service == null || !Utils.checkConnectPermissionForDataDelivery(
                    service, source, "getConnectionState")) {
                return BluetoothProfile.STATE_DISCONNECTED;
            }
            return service.getVcpConnState(device);
        }

        @Override
        public int getConnectionMode(BluetoothDevice device, AttributionSource source) {
            if (DBG) {
                Log.d(TAG, "getConnectionMode(): device: " + device);
            }
            StreamAudioService service = getService();
            if(service == null || !Utils.checkConnectPermissionForDataDelivery(
                    service, source, "getConnectionMode")) {
                return 0;
            }
            return service.getConnectionMode(device);
        }

        @Override
        public void setAbsoluteVolume(BluetoothDevice device, int volume, AttributionSource source) {
            if (DBG) {
                Log.d(TAG, "setAbsoluteVolume(): device: " + device + " volume: " + volume);
            }
            StreamAudioService service = getService();
            if(service == null || !Utils.checkConnectPermissionForDataDelivery(
                    service, source, "setAbsoluteVolume")) {
                return;
            }
            service.setAbsoluteVolume(device, volume);
        }

        @Override
        public int getAbsoluteVolume(BluetoothDevice device, AttributionSource source) {
            if (DBG) {
                Log.d(TAG, "getAbsoluteVolume(): device: " + device);
            }
            StreamAudioService service = getService();
            if(service == null || !Utils.checkConnectPermissionForDataDelivery(
                    service, source, "getAbsoluteVolume")) {
                return 7;
            }
            return service.getAbsoluteVolume(device);
        }

        @Override
        public void setMute (BluetoothDevice device, boolean enableMute, AttributionSource source) {
            if (DBG) {
                Log.d(TAG, "setMute(): device: " + device);
            }
            StreamAudioService service = getService();
            if(service == null || !Utils.checkConnectPermissionForDataDelivery(
                    service, source, "setMute")) {
                return;
            }
            service.setMute(device, enableMute);
        }

        @Override
        public boolean isMute(BluetoothDevice device, AttributionSource source) {
            if (DBG) {
                Log.d(TAG, "isMute(): device: " + device);
            }
            StreamAudioService service = getService();
            if(service == null || !Utils.checkConnectPermissionForDataDelivery(
                    service, source, "isMute")) {
                return false;
            }
            return service.isMute(device);
        }

        public boolean setActiveProfile(BluetoothDevice device, int audioType, int profile,
                                       AttributionSource source) {
            StreamAudioService service = getService();
            if((service != null || !Utils.checkConnectPermissionForDataDelivery(
                service, source,"setActiveProfile")) && device != null) {
                return mService.setActiveProfile(device, audioType, profile);
            }
            return false;
        }

        public int getActiveProfile(int audioType, AttributionSource source) {
            StreamAudioService service = getService();
            if(service != null || !Utils.checkConnectPermissionForDataDelivery(
                    service, source,"getActiveProfile")) {
                return mService.getActiveProfile(audioType);
            }
            return -1;
        }
    }

    public static StreamAudioService getStreamAudioService() {
        return sStreamAudioService;
    }

    private static synchronized void setStreamAudioService(StreamAudioService instance) {
        if (DBG) {
            Log.d(TAG, "setStreamAudioService(): set to: " + instance);
        }
        sStreamAudioService = instance;
    }
}
