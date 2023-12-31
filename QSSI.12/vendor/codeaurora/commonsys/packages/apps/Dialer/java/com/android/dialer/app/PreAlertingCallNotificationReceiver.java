/* Copyright (c) 2020, The Linux Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *     * Neither the name of The Linux Foundation nor the names of its
 *       contributors may be used to endorse or promote products derived
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
 *
 *  Changes from Qualcomm Innovation Center are provided under the following license:
 *
 *  Copyright (c) 2021 Qualcomm Innovation Center, Inc. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted (subject to the limitations in the
 *  disclaimer below) provided that the following conditions are met:
 *
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *
 *      * Redistributions in binary form must reproduce the above
 *        copyright notice, this list of conditions and the following
 *        disclaimer in the documentation and/or other materials provided
 *        with the distribution.
 *
 *      * Neither the name of Qualcomm Innovation Center, Inc. nor the names of its
 *        contributors may be used to endorse or promote products derived
 *        from this software without specific prior written permission.
 *
 *  NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE
 *  GRANTED BY THIS LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT
 *  HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE WHETHER
 *  IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 *  OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 *  IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.android.dialer.app;

import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.android.dialer.common.LogUtil;

import org.codeaurora.ims.CallComposerInfo;
import org.codeaurora.ims.QtiCallConstants;

/**
 * Receives broadcasts that notify of call composer elements.
 */
public class PreAlertingCallNotificationReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "PreAlertingCallNotificationReceiver";

    // TODO: When receiving this intent, there are two actions that can be taken
    //       depending on whether the call has ended or not
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(QtiCallConstants.ACTION_PRE_ALERTING_CALL_INFO)) {
            int phoneId = intent.getIntExtra(QtiCallConstants.EXTRA_PRE_ALERTING_CALL_PHONE_ID,
                    QtiCallConstants.INVALID_PHONE_ID);
            LogUtil.v(LOG_TAG, "onReceive: received pre alerting call notification for phoneId: "
                    + phoneId);
            boolean isCallEnded = intent.getBooleanExtra(
                    QtiCallConstants.EXTRA_PRE_ALERTING_CALL_ENDED, false);
            LogUtil.v(LOG_TAG, "onReceive: pre alerting call ended? " + isCallEnded);
            if (isCallEnded) {
                // The call has ended and any necessary clean up can be initiated
                // e.g. stop download, delete the image from the storage. etc
                return;
            }
            // The pre alerting call data is available. Authenticate, and download the data.
            // Then use the call composer/ecnam info for the incoming/waiting call.
            Bundle ccExtras = intent.getBundleExtra(
                    QtiCallConstants.EXTRA_CALL_COMPOSER_INFO);
            Bundle ecnamExtras = intent.getBundleExtra(QtiCallConstants.EXTRA_CALL_ECNAM);
            LogUtil.v(LOG_TAG, "CallComposerInfo: " + toCallComposer(ccExtras) + "EcnamInfo: "
                    + toEcnamInfo(ecnamExtras));
        }
    }

    private static CallComposerInfo toCallComposer(Bundle extras) {
        if (extras == null) {
            return null;
        }
        CallComposerInfo.Location location = CallComposerInfo.Location.UNKNOWN;
        String subject = extras.getString(
                QtiCallConstants.EXTRA_CALL_COMPOSER_SUBJECT, null);
        int priority = extras.getInt(
                QtiCallConstants.EXTRA_CALL_COMPOSER_PRIORITY,
                CallComposerInfo.PRIORITY_NORMAL);
        Uri imageUrl = extras.getParcelable(
                QtiCallConstants.EXTRA_CALL_COMPOSER_IMAGE);
        if (extras.containsKey(QtiCallConstants.EXTRA_CALL_COMPOSER_LOCATION)) {
            float radius = extras.getFloat(
                    QtiCallConstants.EXTRA_CALL_COMPOSER_LOCATION_RADIUS,
                    CallComposerInfo.Location.DEFAULT_RADIUS);
            double latitude = extras.getDouble(
                    QtiCallConstants.EXTRA_CALL_COMPOSER_LOCATION_LATITUDE);
            double longitude = extras.getDouble(
                    QtiCallConstants.EXTRA_CALL_COMPOSER_LOCATION_LONGITUDE);
            location = new CallComposerInfo.Location(radius, latitude, longitude);
        }
        return new CallComposerInfo(priority, subject, imageUrl, location);
    }

    private static String toEcnamInfo(Bundle extras) {
        if (extras == null) {
            return null;
        }
        String name = extras.getString(QtiCallConstants.EXTRA_CALL_ECNAM_DISPLAY_NAME, null);
        Uri iconUrl = extras.getParcelable(QtiCallConstants.EXTRA_CALL_ECNAM_ICON);
        Uri infoUrl = extras.getParcelable(QtiCallConstants.EXTRA_CALL_ECNAM_INFO);
        Uri cardUrl = extras.getParcelable(QtiCallConstants.EXTRA_CALL_ECNAM_CARD);
        return "DisplayName: " + name + " Icon URL: " + iconUrl + " Info URL: " + infoUrl +
                " Card URL: " + cardUrl;
    }
}
