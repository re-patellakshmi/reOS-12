/*
 * Copyright (c) 2018 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */

package com.qualcomm.qti.sva.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;

import com.qualcomm.listen.ListenSoundModel;
import com.qualcomm.listen.ListenTypes;

import com.qualcomm.qti.sva.controller.SMLParametersManager;
import com.qualcomm.qti.sva.R;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    public static boolean isAtLeastN() {
        return Build.VERSION.SDK_INT >= 24;
    }

    public static void openAlertDialog(Context context, String title, String message) {
        if (null == context || null == message) {
            LogUtils.e(TAG, "openAlertDialog: invalid input params");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (null == title) {
            title = context.getResources().getString(R.string.app_name);
        }
        LogUtils.d(TAG, "openAlertDialog: title= " + title);
        LogUtils.d(TAG, "openAlertDialog: message= " + message);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setNegativeButton(R.string.cancel, null);

        if (!((Activity) context).isFinishing()) {
            builder.show();
        }
    }

    public static String getListenErrorMsg(Context context, int error) {
        if (context != null) {
            switch (error) {
                case ListenTypes.STATUS_EBAD_PARAM:
                    return context.getString(R.string.sm_error_bad_param);
                case ListenTypes.STATUS_EKEYWORD_NOT_IN_SOUNDMODEL:
                    return context.getString(R.string.sm_error_keyword_not_found);
                case ListenTypes.STATUS_EUSER_NOT_IN_SOUNDMODEL:
                    return context.getString(R.string.sm_error_user_not_found);
                case ListenTypes.STATUS_EKEYWORD_USER_PAIR_NOT_IN_SOUNDMODEL:
                    return context.getString(R.string.sm_error_user_kw_not_active);
                case ListenTypes.STATUS_ENOT_SUPPORTED_FOR_SOUNDMODEL_VERSION:
                    return context.getString(R.string.sm_error_version_unsupported);
                case ListenTypes.STATUS_EUSER_KEYWORD_PAIRING_ALREADY_PRESENT:
                    return context.getString(R.string.sm_error_user_data_present);
                case ListenTypes.STATUS_ESOUNDMODELS_WITH_SAME_KEYWORD_CANNOT_BE_MERGED:
                    return context.getString(R.string.sm_error_duplicate_keyword);
                case ListenTypes.STATUS_ESOUNDMODELS_WITH_SAME_USER_KEYWORD_PAIR_CANNOT_BE_MERGED:
                    return context.getString(R.string.sm_error_duplicate_user_kw_air);
                case ListenTypes.STATUS_EMAX_KEYWORDS_EXCEEDED:
                    return context.getString(R.string.sm_error_max_keywords_exceeded);
                case ListenTypes.STATUS_EMAX_USERS_EXCEEDED:
                    return context.getString(R.string.sm_error_max_users_exceeded);
                case ListenTypes.STATUS_ECANNOT_DELETE_LAST_KEYWORD:
                    return context.getString(R.string.sm_error_last_keyword);
                case ListenTypes.STATUS_ENO_SPEACH_IN_RECORDING:
                    return context.getString(R.string.sm_error_no_signal);
                case ListenTypes.STATUS_ETOO_MUCH_NOISE_IN_RECORDING:
                    return context.getString(R.string.sm_error_low_snr);
                case ListenTypes.STATUS_ERECORDING_TOO_SHORT:
                    return context.getString(R.string.sm_error_recording_too_short);
                case ListenTypes.STATUS_ERECORDING_TOO_LONG:
                    return context.getString(R.string.sm_error_recording_too_long);
                case ListenTypes.STATUS_ECHOPPED_SAMPLE:
                    return context.getString(R.string.sm_error_chopped_sample);
                case ListenTypes.STATUS_ECLIPPED_SAMPLE:
                    return context.getString(R.string.sm_error_clipped_sample);
            }
        } else {
            LogUtils.e(TAG, "getListenErrorMsg: invalid input params");
        }
        return context.getString(R.string.sm_error_failed);
    }

    public static boolean isSupportImprovedTraining() {
        int result = ListenTypes.STATUS_EFUNCTION_NOT_IMPLEMENTED;
        boolean isAvalibleVersion = false;
        ListenTypes.SMLVersion smlVersion = new ListenTypes.SMLVersion();
        try {
            result = ListenSoundModel.getSMLVersion(smlVersion);
            isAvalibleVersion =
                    Long.toHexString(smlVersion.version).compareToIgnoreCase(
                            "601010000000000") >= 0;
            LogUtils.d(TAG, "isSupportImprovedTraining: SML version: ret "
                        + result + ",version 0x" + Long.toHexString(smlVersion.version));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result == ListenTypes.STATUS_SUCCESS && isAvalibleVersion;
    }
}
