/*
 * Copyright (c) 2013 - 2018, The Linux Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *     * Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *     * Neither the name of The Linux Foundation nor the names of its
 *      contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
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
 */

#define LOG_TAG "AVFactory"
#include <utils/Log.h>

#include <media/stagefright/foundation/ADebug.h>
#include <media/stagefright/foundation/AMessage.h>
#include <media/stagefright/foundation/ABuffer.h>
#include <media/stagefright/MediaDefs.h>
#include <media/stagefright/MediaCodecList.h>
#include <media/stagefright/MetaData.h>
#include <media/stagefright/ACodec.h>
#include <media/stagefright/MediaFilter.h>
#include <media/stagefright/MediaDefs.h>
#include <media/stagefright/CameraSourceTimeLapse.h>
#include <media/stagefright/MPEG4Writer.h>
#include <media/stagefright/AudioSource.h>

#include "common/ExtensionsLoader.hpp"
#include "stagefright/AVExtensions.h"

namespace android {

sp<CodecBase> AVFactory::createMediaFilter() {
    return new MediaFilter;
}

sp<ACodec> AVFactory::createACodec() {
    return new ACodec;
}

CameraSource* AVFactory::CreateCameraSourceFromCamera(
            const sp<hardware::ICamera> &camera,
            const sp<ICameraRecordingProxy> &proxy,
            int32_t cameraId,
            const String16& clientName,
            uid_t clientUid,
            pid_t clientPid,
            Size videoSize,
            int32_t frameRate,
            const sp<IGraphicBufferProducer>& surface,
            // TODO(b/168051781) review removal of storeMetaDataInVideoBuffers
            // parameter from CreateFromCamera API.
            bool /*storeMetaDataInVideoBuffers*/) {
    return CameraSource::CreateFromCamera(camera, proxy, cameraId,
            clientName, clientUid, clientPid, videoSize, frameRate, surface);
}

CameraSourceTimeLapse* AVFactory::CreateCameraSourceTimeLapseFromCamera(
        const sp<hardware::ICamera> &camera,
        const sp<ICameraRecordingProxy> &proxy,
        int32_t cameraId,
        const String16& clientName,
        uid_t clientUid,
        pid_t clientPid,
        Size videoSize,
        int32_t videoFrameRate,
        const sp<IGraphicBufferProducer>& surface,
        int64_t timeBetweenFrameCaptureUs,
        // TODO(b/168051781) review removal of storeMetaDataInVideoBuffers
        // parameter from CreateFromCamera API.
        bool /*storeMetaDataInVideoBuffers*/) {
    return CameraSourceTimeLapse::CreateFromCamera(camera, proxy, cameraId,
            clientName, clientUid, clientPid, videoSize, videoFrameRate, surface,
            timeBetweenFrameCaptureUs);
}

MPEG4Writer* AVFactory::CreateMPEG4Writer(int fd) {
    return new MPEG4Writer(fd);
}

ElementaryStreamQueue* AVFactory::createESQueue(
         ElementaryStreamQueue::Mode , uint32_t ) {
    return NULL;
}

AudioSource* AVFactory::createAudioSource(
            const audio_attributes_t *attr,
            const content::AttributionSourceState& attributionSource,
            uint32_t sampleRate,
            uint32_t channels,
            uint32_t outSampleRate,
            audio_port_handle_t selectedDeviceId,
            audio_microphone_direction_t /*selectedMicDirection*/,
            float /*selectedMicFieldDimension*/) {
    // TODO(b/129493645): use new selectedMicDirection and selectedMicFieldDimension params
    return new AudioSource(attr, attributionSource, sampleRate,
                            channels, outSampleRate, selectedDeviceId);
}
// ----- NO TRESSPASSING BEYOND THIS LINE ------
AVFactory::AVFactory() {
}

AVFactory::~AVFactory() {
}

//static
AVFactory *AVFactory::sInst =
        ExtensionsLoader<AVFactory>::createInstance("createExtendedFactory");

} //namespace android

