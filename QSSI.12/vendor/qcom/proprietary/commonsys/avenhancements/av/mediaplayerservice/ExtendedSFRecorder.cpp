/*
 * Copyright (c) 2015-2019,2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 *
 * Not a Contribution.
 * Apache license notifications and license are retained
 * for attribution purposes only.
 */
/*
 * Copyright (c) 2013 - 2015, The Linux Foundation. All rights reserved.
 */
/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//#define LOG_NDEBUG 0
#define LOG_TAG "ExtendedSFRecorder"
#include <inttypes.h>
#include <common/AVLog.h>
#include <utils/Errors.h>
#include <cutils/properties.h>

#include <media/stagefright/MediaErrors.h>
#include <media/stagefright/MetaData.h>
#include <media/stagefright/MediaDefs.h>
#include <media/stagefright/MediaSource.h>
#include <media/stagefright/foundation/ADebug.h>
#include <media/stagefright/foundation/AMessage.h>
#include <media/stagefright/OMXClient.h>
#include <media/stagefright/foundation/ADebug.h>
#include <media/stagefright/AudioSource.h>
#include <media/stagefright/foundation/ABuffer.h>
#include <media/stagefright/MediaCodecList.h>
#include <media/MediaProfiles.h>

#include <StagefrightRecorder.h>

#include "mediaplayerservice/AVMediaServiceExtensions.h"
#include "stagefright/ExtendedAudioSource.h"
#include "mediaplayerservice/ExtendedSFRecorder.h"
#include "mediaplayerservice/ExtendedWriter.h"
#include "mediaplayerservice/WAVEWriter.h"

//TODO: don't need this dependency after MetaData keys are moved to separate file
#include <stagefright/AVExtensions.h>
#include <stagefright/ExtendedUtils.h>

#include <OMX_Video.h>

namespace android {
ExtendedSFRecorder::ExtendedSFRecorder(const AttributionSourceState& attributionSource)
  : StagefrightRecorder(attributionSource),
    mRecPaused(false) {
    mAttributionSource = attributionSource;
    updateLogLevel();
    pConfigsIns = AVConfigHelper::getInstance();
    AVLOGV("ExtendedSFRecorder()");
}

ExtendedSFRecorder::~ExtendedSFRecorder() {
    AVLOGV("~ExtendedSFRecorder()");
}

status_t ExtendedSFRecorder::setAudioSource(audio_source_t as) {
    if (!isAudioDisabled()) {
        return StagefrightRecorder::setAudioSource(as);
    }
    return OK;
}

status_t ExtendedSFRecorder::setAudioEncoder(audio_encoder ae) {
    if (!isAudioDisabled()) {
        // Do more QC stuff here if required
        return StagefrightRecorder::setAudioEncoder(ae);
    }
    return OK;
}

void ExtendedSFRecorder::setupCustomVideoEncoderParams(sp<MediaSource> cameraSource,
        sp<AMessage> &format) {
    AVLOGV("setupCustomVideoEncoderParams");

    if (cameraSource != NULL) {
        sp<MetaData> meta = cameraSource->getFormat();
        int32_t batchSize;
        if (meta->findInt32(kKeyLocalBatchSize, &batchSize)) {
            AVLOGV("Setting batch size = %d", batchSize);
            format->setInt32("batch-size", batchSize);
        }
    }
    setEncoderProfile();
}

bool ExtendedSFRecorder::isAudioDisabled() {
    bool bAudioDisabled = false;

    bAudioDisabled = pConfigsIns->isSFRecorderDisabled();
    AVLOGD("Audio disabled %d", bAudioDisabled);

    return bAudioDisabled;
}

void ExtendedSFRecorder::setEncoderProfile() {

    char value[PROPERTY_VALUE_MAX];
    if (property_get("vendor.encoder.video.profile", value, NULL) <= 0) {
        return;
    }

    AVLOGI("Setting encoder profile : %s", value);

    int32_t profile = mVideoEncoderProfile;
    int32_t level = mVideoEncoderLevel;

    switch (mVideoEncoder) {
        case VIDEO_ENCODER_H264:
            // Set the minimum valid level if the level was undefined;
            // encoder will choose the right level anyways
            level = (level < 0) ? OMX_VIDEO_AVCLevel1 : level;
            if (strncmp("base", value, 4) == 0) {
                profile = OMX_VIDEO_AVCProfileBaseline;
                AVLOGI("H264 Baseline Profile");
            } else if (strncmp("main", value, 4) == 0) {
                profile = OMX_VIDEO_AVCProfileMain;
                AVLOGI("H264 Main Profile");
            } else if (strncmp("high", value, 4) == 0) {
                profile = OMX_VIDEO_AVCProfileHigh;
                AVLOGI("H264 High Profile");
            } else {
                AVLOGW("Unsupported H264 Profile");
            }
            break;
        case VIDEO_ENCODER_MPEG_4_SP:
            level = (level < 0) ? OMX_VIDEO_MPEG4Level0 : level;
            if (strncmp("simple", value, 5) == 0 ) {
                profile = OMX_VIDEO_MPEG4ProfileSimple;
                AVLOGI("MPEG4 Simple profile");
            } else if (strncmp("asp", value, 3) == 0 ) {
                profile = OMX_VIDEO_MPEG4ProfileAdvancedSimple;
                AVLOGI("MPEG4 Advanced Simple Profile");
            } else {
                AVLOGW("Unsupported MPEG4 Profile");
            }
            break;
        default:
            AVLOGW("No custom profile support for other codecs");
            break;
    }
    // Override _both_ profile and level, only if they are valid
    if (profile && level) {
        mVideoEncoderProfile = profile;
        mVideoEncoderLevel = level;
    }
}

status_t ExtendedSFRecorder::handleCustomOutputFormats() {
    status_t status = OK;
    switch (mOutputFormat) {
        case OUTPUT_FORMAT_QCP:
        case OUTPUT_FORMAT_WAVE:
          status = mWriter->start();
          break;

        default:
           status = UNKNOWN_ERROR;
           break;
    }
    return status;
}

status_t ExtendedSFRecorder::handleCustomRecording() {
    status_t status = OK;
    switch (mOutputFormat) {
        case OUTPUT_FORMAT_QCP:
            status = setupExtendedRecording();
            break;
        case OUTPUT_FORMAT_WAVE:
            status = setupWAVERecording();
            break;

        default:
            status = UNKNOWN_ERROR;
            break;
    }
    return status;
}

status_t ExtendedSFRecorder::handleCustomAudioSource(sp<AMessage> format) {
    status_t status = OK;
    switch (mAudioEncoder) {
        case AUDIO_ENCODER_LPCM:
            format->setString("mime", MEDIA_MIMETYPE_AUDIO_RAW);
            break;
        case AUDIO_ENCODER_EVRC:
            format->setString("mime", MEDIA_MIMETYPE_AUDIO_EVRC);
            break;
        case AUDIO_ENCODER_QCELP:
            format->setString("mime", MEDIA_MIMETYPE_AUDIO_QCELP);
            break;
        case AUDIO_ENCODER_MPEGH:
            format->setString("mime", MEDIA_MIMETYPE_AUDIO_MHAS);
            break;
        default:
            status = UNKNOWN_ERROR;
            break;
    }
    return status;
}

status_t ExtendedSFRecorder::handleCustomAudioEncoder() {
    status_t status = OK;
    switch (mAudioEncoder) {
        case AUDIO_ENCODER_LPCM:
        case AUDIO_ENCODER_EVRC:
        case AUDIO_ENCODER_QCELP:
        case AUDIO_ENCODER_MPEGH:
            break;

        default:
            status = UNKNOWN_ERROR;
            break;
    }
    return status;
}

status_t ExtendedSFRecorder::setupWAVERecording() {
    CHECK(mOutputFormat == OUTPUT_FORMAT_WAVE);
    CHECK(mAudioEncoder == AUDIO_ENCODER_LPCM);
    CHECK(mAudioSource != AUDIO_SOURCE_CNT);

    mWriter = new WAVEWriter(mOutputFd);
    return setupRawAudioRecording();
}

status_t ExtendedSFRecorder::setupExtendedRecording() {
    CHECK(mOutputFormat == OUTPUT_FORMAT_QCP);

    if (mSampleRate != 8000) {
        AVLOGE("Invalid sampling rate %d used for recording",
             mSampleRate);
        return BAD_VALUE;
    }
    if (mAudioChannels != 1) {
        AVLOGE("Invalid number of audio channels %d used for recording",
                mAudioChannels);
        return BAD_VALUE;
    }

    if (mAudioSource >= AUDIO_SOURCE_CNT) {
        AVLOGE("Invalid audio source: %d", mAudioSource);
        return BAD_VALUE;
    }

    mWriter = new ExtendedWriter(mOutputFd);
    return setupRawAudioRecording();
}

status_t ExtendedSFRecorder::pause() {
    if (mOutputFormat == OUTPUT_FORMAT_WAVE) {
        if (mWriter == NULL) {
            return UNKNOWN_ERROR;
        }
        status_t err = mWriter->pause();
        if (err != OK) {
            AVLOGE("Writer pause in StagefrightRecorder failed");
            return err;
        }
    }

    return StagefrightRecorder::pause();
}

status_t ExtendedSFRecorder::resume() {
    if (mOutputFormat == OUTPUT_FORMAT_WAVE) {
        if (mWriter == NULL) {
            return UNKNOWN_ERROR;
        }
        status_t err = mWriter->start();
        if (err != OK) {
            AVLOGE("Writer start in StagefrightRecorder failed");
            return err;
        }
    }

    return StagefrightRecorder::resume();
}

sp<MediaSource> ExtendedSFRecorder::setPCMRecording() {
    audio_attributes_t attr = AUDIO_ATTRIBUTES_INITIALIZER;
    attr.source = mAudioSource;

    sp<AudioSource> audioSource =
        new ExtendedAudioSource(
                &attr,
                mAttributionSource,
                mSampleRate,
                mAudioChannels,
                mSampleRate);

    status_t err = audioSource->initCheck();

    if (err != OK) {
        AVLOGE("audio source is not initialized");
        return NULL;
    }
    if (mAudioEncoder == AUDIO_ENCODER_LPCM) {
        AVLOGI("No encoder is needed for linear PCM format");
        return audioSource;

    }
    return NULL;
}
} // namespace android
