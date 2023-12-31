/*
 * Copyright (c) 2015, 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 *
 * Not a Contribution.
 * Apache license notifications and license are retained
 * for attribution purposes only.
 */

/*
 * Copyright (C) 2006 The Android Open Source Project
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
package org.codeaurora.ims;

/**
 * See also RIL_CallForwardInfo in include/telephony/ril.h
 *
 * {@hide}
 */
public class ImsCallForwardTimerInfo {
    public int             status;      /*1 = active, 0 = not active */
    public int             reason;      /* from TS 27.007 7.11 "reason" */
    public int             serviceClass; /* Sum of CommandsInterface.SERVICE_CLASS */
    public int             toa;         /* "type" from TS 27.007 7.11 */
    public String          number;      /* "number" from TS 27.007 7.11 */
    public int             timeSeconds; /* for CF no reply only */
    public int             startHour;
    public int             startMinute;
    public int             endHour;
    public int             endMinute;

    @Override
    public String toString() {
        return super.toString() + (status == 0 ? " not active " : " active ")
            + " reason: " + reason  + " serviceClass: " + serviceClass + " toa: " + toa
            + " number:" + number + " timeSeconds: " + timeSeconds + " startHour:" + startHour
            + " startMinute: " + startMinute + " endHour: " + endHour + " endMinute: " + endMinute ;

    }
}
