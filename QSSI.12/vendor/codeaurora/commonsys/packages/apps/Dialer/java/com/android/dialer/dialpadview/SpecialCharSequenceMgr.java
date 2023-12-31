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

package com.android.dialer.dialpadview;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.common.io.MoreCloseables;
import com.android.contacts.common.database.NoNullCursorAsyncQueryHandler;
import com.android.contacts.common.util.ContactDisplayUtils;
import com.android.contacts.common.widget.SelectPhoneAccountDialogFragment;
import com.android.contacts.common.widget.SelectPhoneAccountDialogFragment.SelectPhoneAccountListener;
import com.android.contacts.common.widget.SelectPhoneAccountDialogOptionsUtil;
import com.android.dialer.common.Assert;
import com.android.dialer.common.LogUtil;
import com.android.dialer.compat.telephony.TelephonyManagerCompat;
import com.android.dialer.oem.MotorolaUtils;
import com.android.dialer.oem.TranssionUtils;
import com.android.dialer.telecom.TelecomUtil;
import com.android.dialer.util.PermissionsUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import com.qti.extphone.ExtTelephonyManager;
import com.qti.extphone.QtiImeiInfo;
import com.qti.extphone.ServiceCallback;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
//andrew.hu add RPM TZ SBL version start
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.os.SystemProperties;
import android.os.Build;
import android.view.WindowManager;
//andrew.hu add RPM TZ SBL version end

/**
 * Helper class to listen for some magic character sequences that are handled specially by Dialer.
 */
public class SpecialCharSequenceMgr {
  private static final String TAG = "SpecialCharSequenceMgr";
  private static final String TAG_SELECT_ACCT_FRAGMENT = "tag_select_acct_fragment";

  @VisibleForTesting static final String MMI_IMEI_DISPLAY = "*#06#";
  private static final String MMI_REGULATORY_INFO_DISPLAY = "*#07#";
  private static final String PRL_VERSION_DISPLAY = "*#0000#";
  /** ***** This code is used to handle SIM Contact queries ***** */
  private static final String ADN_PHONE_NUMBER_COLUMN_NAME = "number";

  private static final String ADN_NAME_COLUMN_NAME = "name";
  private static final int ADN_QUERY_TOKEN = -1;

  //andrew.hu add RPM TZ SBL version start
  private static final String MMI_QUECTEL_IMAGE_VERSION_DISPLAY = "*#08#";
  private final static String cmd_0 = "echo 0 > /sys/devices/soc0/select_image";
  //private final static String cmd_1 = "echo 1 > /sys/devices/soc0/select_image";
  private final static String cmd_3 = "echo 3 > /sys/devices/soc0/select_image";
  private final static String cmd_11 = "echo 11 > /sys/devices/soc0/select_image";
  private final static String cmd_12 = "echo 12 > /sys/devices/soc0/select_image";
  private final static String cmd_cat = "cat /sys/devices/soc0/image_crm_version";
  private static final String BASEBAND_VERSION = "gsm.version.baseband";
  private static final String BASEBAND_VERSION_PERSIST = "persist.version.baseband";
  private static final String VENDOR_VERSION = "persist.vendor.version.ati";

  private static ExtTelephonyManager mExtTelephonyManager = null;
  private static boolean mIsServiceBound;

  /**
   * Remembers the previous {@link QueryHandler} and cancel the operation when needed, to prevent
   * possible crash.
   *
   * <p>QueryHandler may call {@link ProgressDialog#dismiss()} when the screen is already gone,
   * which will cause the app crash. This variable enables the class to prevent the crash on {@link
   * #cleanup()}.
   *
   * <p>TODO: Remove this and replace it (and {@link #cleanup()}) with better implementation. One
   * complication is that we have SpecialCharSequenceMgr in Phone package too, which has *slightly*
   * different implementation. Note that Phone package doesn't have this problem, so the class on
   * Phone side doesn't have this functionality. Fundamental fix would be to have one shared
   * implementation and resolve this corner case more gracefully.
   */
  private static QueryHandler previousAdnQueryHandler;

  /** This class is never instantiated. */
  private SpecialCharSequenceMgr() {}

  public static boolean handleChars(Context context, String input, EditText textField) {
    // get rid of the separators so that the string gets parsed correctly
    String dialString = PhoneNumberUtils.stripSeparators(input);

    if (handleDeviceIdDisplay(context, dialString)
        || handlePRLVersion(context, dialString)
        || handleRegulatoryInfoDisplay(context, dialString)
        || handlePinEntry(context, dialString)
        || handleAdnEntry(context, dialString, textField)
        || handleSecretCode(context, dialString)
        || handleQuectelImageVersion(context,dialString)) {
      return true;
    }

    if (MotorolaUtils.handleSpecialCharSequence(context, input)) {
      return true;
    }

    return false;
  }

   //andrew.hu add RPM TZ SBL version start
  static boolean handleQuectelImageVersion(Context context, String input){
    TelephonyManager telephonyManager =
            (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    if (telephonyManager != null && input.equals(MMI_QUECTEL_IMAGE_VERSION_DISPLAY)) {
      android.util.Log.i(TAG,"handleQuectelImageVersion");
      showQuectelImagePanel(context,telephonyManager);
      return true;
    }
    return false;
  }

  private static void showQuectelImagePanel(Context context,TelephonyManager telephonyManager) {
    resetModemCl(cmd_0,false);
    String select_image_0 = resetModemCl(cmd_cat,true);
    //resetModemCl(cmd_1,false);
    //String select_image_1 = resetModemCl(cmd_cat,true);
    resetModemCl(cmd_3,false);
    String select_image_3 = resetModemCl(cmd_cat,true);
    resetModemCl(cmd_12,false);
    String select_image_12 = resetModemCl(cmd_cat,true);
    resetModemCl(cmd_11,false);
    String select_image_11 = resetModemCl(cmd_cat,true);
    String baseVersion = "";
    try {
      baseVersion = SystemProperties.get(BASEBAND_VERSION,
              context.getResources().getString(R.string.unknown));
    } catch (RuntimeException e) {
      // No recovery
    }
    //baseband_version
    String baseband_version = "Unknown";
    try {
      baseband_version = SystemProperties.get(baseVersion.equals(context.getResources().getString(R.string.unknown)) ? BASEBAND_VERSION_PERSIST : BASEBAND_VERSION,
              context.getResources().getString(R.string.unknown));
    } catch (RuntimeException e) {
      // No recovery
    }
    //build version
    String Build_verison = getBuildVersion(context);

    AlertDialog alert = new AlertDialog.Builder(context)
            .setTitle(R.string.quectel_image_version)
            .setMessage(Build_verison+"\n\n"+baseband_version+"\n\n"+select_image_0/*+"\n"+select_image_1*/+"\n"+select_image_3+"\n"+select_image_12+"\n"+select_image_11)
            .setPositiveButton(android.R.string.ok, null)
            .setCancelable(false)
            .show();
    WindowManager.LayoutParams params = alert.getWindow().getAttributes();
    params.width = 720;
    alert.getWindow().setAttributes(params);
  }

  private static String getBuildVersion(Context mContext) {
    try {
      String Build_verison = Build.DISPLAY;
      String Build_TAGS = Build.TAGS;
      android.util.Log.i(TAG,"Build_TAGS ="+Build.TAGS );
      if(Build_verison.contains(Build_TAGS)){
        Build_verison = Build_verison.replace(Build_TAGS, "");
      }
      return Build_verison;
    } catch (RuntimeException e) {
      android.util.Log.i(TAG,"build vertion unknown!");
      String Build_unknown = mContext.getResources().getString(R.string.unknown);
      return Build_unknown;
    }
  }


  /*The core of the sending terminal command code
   * three return values
   * 0:normal;
   * 1:permission denied;
   * other:null
   */
  private static String resetModemCl(String cmd,boolean isRead){
    BufferedReader reader = null;
    try {
      ProcessBuilder execBuild = new ProcessBuilder("/system/bin/sh", "-");
      execBuild.redirectErrorStream(true);
      Process process = null;
      try {
        process = execBuild.start();
      } catch (Exception e) {
        //      log("Could not start terminal process.", e);
      }
      DataOutputStream os = new DataOutputStream(process.getOutputStream());
      os.writeBytes(cmd + "\n");
      os.flush();
      os.writeBytes("exit\n");
      os.flush();
      process.getOutputStream();
      int value = process.waitFor();
      android.util.Log.i(TAG,"value="+value);
      if(value == 0 && isRead){
        StringBuffer result = new StringBuffer();
        InputStream in = process.getInputStream();
        String line;
        reader = new BufferedReader(new InputStreamReader(in));
        while ((line = reader.readLine())!=null){
          result.append(line);
        }
        if(in != null){
          in.close();
          os.close();
        }
        android.util.Log.i(TAG,"result="+result);
        String replace_result = result.toString();
        if(result.toString().contains(":")){
          replace_result = replace_result.replaceFirst(":","");
        }
        return replace_result;
      }else if(value == 1){
        return "Operation not permitted";
      }
    } catch (Exception e) {
      e.printStackTrace();
    }finally {
      try {
        if(reader != null) {
          reader.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return null;
  }
  //andrew.hu add RPM TZ SBL version end

  static private boolean handlePRLVersion(Context context, String input) {
    if (input.equals(PRL_VERSION_DISPLAY)) {
      try {
        Intent intent = new Intent("org.codeaurora.intent.action.ACTION_DEVICEINFO");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return true;
      } catch (ActivityNotFoundException e) {
        LogUtil.d(
            "SpecialCharSequenceMgr.handlePRLVersion", "no activity to handle showing device info");
      }
    }
    return false;
  }

  /**
   * Cleanup everything around this class. Must be run inside the main thread.
   *
   * <p>This should be called when the screen becomes background.
   */
  public static void cleanup() {
    Assert.isMainThread();

    if (previousAdnQueryHandler != null) {
      previousAdnQueryHandler.cancel();
      previousAdnQueryHandler = null;
    }
  }

  /**
   * Handles secret codes to launch arbitrary activities in the form of
   * *#*#<code>#*#* or *#<code_starting_with_number>#.
   *
   * @param context the context to use
   * @param input the text to check for a secret code in
   * @return true if a secret code was encountered and handled
   */
  static boolean handleSecretCode(Context context, String input) {
    // Secret code specific to OEMs should be handled first.
    if (TranssionUtils.isTranssionSecretCode(input)) {
      TranssionUtils.handleTranssionSecretCode(context, input);
      return true;
    }

    // Secret codes are accessed by dialing *#*#<code>#*#* or "*#<code_starting_with_number>#"
    if (input.length() > 8 && input.startsWith("*#*#") && input.endsWith("#*#*")) {
      String secretCode = input.substring(4, input.length() - 4);
      TelephonyManagerCompat.handleSecretCode(context, secretCode);
      return true;
    }

    return false;
  }

  /**
   * Handle ADN requests by filling in the SIM contact number into the requested EditText.
   *
   * <p>This code works alongside the Asynchronous query handler {@link QueryHandler} and query
   * cancel handler implemented in {@link SimContactQueryCookie}.
   */
  static boolean handleAdnEntry(Context context, String input, EditText textField) {
    /* ADN entries are of the form "N(N)(N)#" */
    TelephonyManager telephonyManager =
        (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    if (telephonyManager == null
        || telephonyManager.getPhoneType() != TelephonyManager.PHONE_TYPE_GSM) {
      return false;
    }

    // if the phone is keyguard-restricted, then just ignore this
    // input.  We want to make sure that sim card contacts are NOT
    // exposed unless the phone is unlocked, and this code can be
    // accessed from the emergency dialer.
    KeyguardManager keyguardManager =
        (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
    if (keyguardManager.inKeyguardRestrictedInputMode()) {
      return false;
    }

    int len = input.length();
    if ((len > 1) && (len < 5) && (input.endsWith("#"))) {
      try {
        // get the ordinal number of the sim contact
        final int index = Integer.parseInt(input.substring(0, len - 1));

        // The original code that navigated to a SIM Contacts list view did not
        // highlight the requested contact correctly, a requirement for PTCRB
        // certification.  This behaviour is consistent with the UI paradigm
        // for touch-enabled lists, so it does not make sense to try to work
        // around it.  Instead we fill in the the requested phone number into
        // the dialer text field.

        // create the async query handler
        final QueryHandler handler = new QueryHandler(context.getContentResolver());

        // create the cookie object
        final SimContactQueryCookie sc =
            new SimContactQueryCookie(index - 1, handler, ADN_QUERY_TOKEN);

        // setup the cookie fields
        sc.contactNum = index - 1;
        sc.setTextField(textField);

        // create the progress dialog
        sc.progressDialog = new ProgressDialog(context);
        sc.progressDialog.setTitle(R.string.simContacts_title);
        sc.progressDialog.setMessage(context.getText(R.string.simContacts_emptyLoading));
        sc.progressDialog.setIndeterminate(true);
        sc.progressDialog.setCancelable(true);
        sc.progressDialog.setOnCancelListener(sc);
        sc.progressDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        List<PhoneAccountHandle> subscriptionAccountHandles =
            TelecomUtil.getSubscriptionPhoneAccounts(context);
        Context applicationContext = context.getApplicationContext();
        boolean hasUserSelectedDefault =
            subscriptionAccountHandles.contains(
                TelecomUtil.getDefaultOutgoingPhoneAccount(
                    applicationContext, PhoneAccount.SCHEME_TEL));

        if (subscriptionAccountHandles.size() <= 1 || hasUserSelectedDefault) {
          Uri uri = TelecomUtil.getAdnUriForPhoneAccount(applicationContext, null);
          handleAdnQuery(handler, sc, uri);
        } else {
          SelectPhoneAccountListener callback =
              new HandleAdnEntryAccountSelectedCallback(applicationContext, handler, sc);
          DialogFragment dialogFragment =
              SelectPhoneAccountDialogFragment.newInstance(
                  SelectPhoneAccountDialogOptionsUtil.builderWithAccounts(
                          subscriptionAccountHandles)
                      .build(),
                  callback);
          dialogFragment.show(((Activity) context).getFragmentManager(), TAG_SELECT_ACCT_FRAGMENT);
        }

        return true;
      } catch (NumberFormatException ex) {
        // Ignore
      }
    }
    return false;
  }

  private static void handleAdnQuery(QueryHandler handler, SimContactQueryCookie cookie, Uri uri) {
    if (handler == null || cookie == null || uri == null) {
      LogUtil.w("SpecialCharSequenceMgr.handleAdnQuery", "queryAdn parameters incorrect");
      return;
    }

    // display the progress dialog
    cookie.progressDialog.show();

    // run the query.
    handler.startQuery(
        ADN_QUERY_TOKEN,
        cookie,
        uri,
        new String[] {ADN_PHONE_NUMBER_COLUMN_NAME},
        null,
        null,
        null);

    if (previousAdnQueryHandler != null) {
      // It is harmless to call cancel() even after the handler's gone.
      previousAdnQueryHandler.cancel();
    }
    previousAdnQueryHandler = handler;
  }

  static boolean handlePinEntry(final Context context, final String input) {
    if ((input.startsWith("**04") || input.startsWith("**05")) && input.endsWith("#")) {
      List<PhoneAccountHandle> subscriptionAccountHandles =
          TelecomUtil.getSubscriptionPhoneAccounts(context);
      boolean hasUserSelectedDefault =
          subscriptionAccountHandles.contains(
              TelecomUtil.getDefaultOutgoingPhoneAccount(context, PhoneAccount.SCHEME_TEL));

      if (subscriptionAccountHandles.size() <= 1 || hasUserSelectedDefault) {
        // Don't bring up the dialog for single-SIM or if the default outgoing account is
        // a subscription account.
        return TelecomUtil.handleMmi(context, input, null);
      } else {
        SelectPhoneAccountListener listener = new HandleMmiAccountSelectedCallback(context, input);

        DialogFragment dialogFragment =
            SelectPhoneAccountDialogFragment.newInstance(
                SelectPhoneAccountDialogOptionsUtil.builderWithAccounts(subscriptionAccountHandles)
                    .build(),
                listener);
        dialogFragment.show(((Activity) context).getFragmentManager(), TAG_SELECT_ACCT_FRAGMENT);
      }
      return true;
    }
    return false;
  }

  private static ServiceCallback mServiceCallback = new ServiceCallback() {
      @Override
      public void onConnected() {
          LogUtil.d("SpecialCharSequenceMgr", "ExtTelephony Service connected");
          mIsServiceBound = true;
      }
      @Override
      public void onDisconnected() {
          LogUtil.d("SpecialCharSequenceMgr", "ExtTelephony Service disconnected...");
          mIsServiceBound = false;
      }
  };

  public static boolean isServiceConnected() {
      return mIsServiceBound;
  }

  // TODO: Use TelephonyCapabilities.getDeviceIdLabel() to get the device id label instead of a
  // hard-coded string.
  @SuppressLint("HardwareIds")
  static boolean handleDeviceIdDisplay(Context context, String input) {
    if (!PermissionsUtil.hasPermission(context, Manifest.permission.READ_PHONE_STATE)) {
      return false;
    }
    TelephonyManager telephonyManager =
        (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

    if (TelephonyManagerCompat.getPhoneCount(telephonyManager) > 1 &&
            mExtTelephonyManager == null) {
        mExtTelephonyManager = ExtTelephonyManager.getInstance(context);
        mExtTelephonyManager.connectService(mServiceCallback);
        LogUtil.d("SpecialCharSequenceMgr", "Connect to ExtTelephony bound service...");
    }

    if (telephonyManager != null && input.equals(MMI_IMEI_DISPLAY)) {
      final String label = context.getResources().getString(R.string.meid) + " & " +
          context.getResources().getString(R.string.imei);
      View customView = LayoutInflater.from(context).inflate(R.layout.dialog_deviceids, null);
      ViewGroup holder = customView.findViewById(R.id.deviceids_holder);

      if (TelephonyManagerCompat.getPhoneCount(telephonyManager) > 1) {
        QtiImeiInfo[] qtiImeiInfo = null;
        if (isServiceConnected()) {
          qtiImeiInfo = mExtTelephonyManager.getImeiInfo();
        }
        String deviceId = null;
        for (int slot = 0; slot < telephonyManager.getPhoneCount(); slot++) {
          // Add MEID
          final String meid = telephonyManager.getMeid(slot);
          if ((deviceId == null && isValidMeid(meid))
              || (deviceId != null && !deviceId.equals(meid)
              && isValidMeid(meid))) {
            addDeviceIdRow(
                holder,
                meid,
                /* showDecimal */
                context.getResources().getBoolean(R.bool.show_device_id_in_hex_and_decimal),
                /* showBarcode */ false);
          }
          deviceId = meid;

          // Add IMEI
          String imei = null;
          boolean isPrimary = false;
          if (qtiImeiInfo != null) {
              for (int i = 0; i < qtiImeiInfo.length; i++) {
                  if (null != qtiImeiInfo[i] && qtiImeiInfo[i].getSlotId() == slot) {
                      imei = qtiImeiInfo[i].getImei();
                      if (qtiImeiInfo[i].getImeiType() == QtiImeiInfo.IMEI_TYPE_PRIMARY) {
                          isPrimary = true;
                          break;
                      }
                  }
              }
          }
          if (TextUtils.isEmpty(imei)) {
              imei = telephonyManager.getImei(slot);
          }
          if (isPrimary) {
              imei += " (Primary)";
          }
          if (!TextUtils.isEmpty(imei)) {
            addDeviceIdRow(
                holder,
                imei,
                /* showDecimal */
                context.getResources().getBoolean(R.bool.show_device_id_in_hex_and_decimal),
                /* showBarcode */ false);
          }
        }
      } else {
        final String meid = telephonyManager.getMeid();
        if (isValidMeid(meid)) {
          addDeviceIdRow(
              holder,
              meid,
              /* showDecimal */
              context.getResources().getBoolean(R.bool.show_device_id_in_hex_and_decimal),
              /* showBarcode */
              context.getResources().getBoolean(R.bool.show_device_id_as_barcode));
        }
        final String imei = telephonyManager.getImei();
        if (!TextUtils.isEmpty(imei)) {
          addDeviceIdRow(
              holder,
              imei,
              /* showDecimal */
              context.getResources().getBoolean(R.bool.show_device_id_in_hex_and_decimal),
              /* showBarcode */
              context.getResources().getBoolean(R.bool.show_device_id_as_barcode));
        }
      }

      new AlertDialog.Builder(context)
          .setTitle(label)
          .setView(customView)
          .setPositiveButton(android.R.string.ok, null)
          .setCancelable(false)
          .show()
          .getWindow()
          .setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
      return true;
    }
    return false;
  }

  private static void addDeviceIdRow(
      ViewGroup holder, String deviceId, boolean showDecimal, boolean showBarcode) {
    if (TextUtils.isEmpty(deviceId)) {
      return;
    }

    ViewGroup row =
        (ViewGroup)
            LayoutInflater.from(holder.getContext()).inflate(R.layout.row_deviceid, holder, false);
    holder.addView(row);

    // Remove the check digit, if exists. This digit is a checksum of the ID.
    // See https://en.wikipedia.org/wiki/International_Mobile_Equipment_Identity
    // and https://en.wikipedia.org/wiki/Mobile_equipment_identifier
    String hex = deviceId.length() == 15 ? deviceId.substring(0, 14) : deviceId;

    // If this is the valid length IMEI or MEID (14 digits), show it in all formats, otherwise fall
    // back to just showing the raw hex
    if (hex.length() == 14 && showDecimal) {
      ((TextView) row.findViewById(R.id.deviceid_hex)).setText(hex);
      ((TextView) row.findViewById(R.id.deviceid_dec)).setText(getDecimalFromHex(hex));
      row.findViewById(R.id.deviceid_dec_label).setVisibility(View.VISIBLE);
    } else {
      row.findViewById(R.id.deviceid_hex_label).setVisibility(View.GONE);
      ((TextView) row.findViewById(R.id.deviceid_hex)).setText(deviceId);
    }

    final ImageView barcode = row.findViewById(R.id.deviceid_barcode);
    if (showBarcode) {
      // Wait until the layout pass has completed so we the barcode is measured before drawing. We
      // do this by adding a layout listener and setting the bitmap after getting the callback.
      barcode
          .getViewTreeObserver()
          .addOnGlobalLayoutListener(
              new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                  barcode.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                  Bitmap barcodeBitmap =
                      generateBarcode(hex, barcode.getWidth(), barcode.getHeight());
                  if (barcodeBitmap != null) {
                    barcode.setImageBitmap(barcodeBitmap);
                  }
                }
              });
    } else {
      barcode.setVisibility(View.GONE);
    }
  }

  private static boolean isValidMeid(String meid) {
    if (!TextUtils.isEmpty(meid) && !meid.equals("0")
        && !meid.startsWith("000000")) {
      return true;
    }
    return false;
  }

  private static String getDecimalFromHex(String hex) {
    final String part1 = hex.substring(0, 8);
    final String part2 = hex.substring(8);

    long dec1;
    try {
      dec1 = Long.parseLong(part1, 16);
    } catch (NumberFormatException e) {
      LogUtil.e("SpecialCharSequenceMgr.getDecimalFromHex", "unable to parse hex", e);
      return "";
    }

    final String manufacturerCode = String.format(Locale.US, "%010d", dec1);

    long dec2;
    try {
      dec2 = Long.parseLong(part2, 16);
    } catch (NumberFormatException e) {
      LogUtil.e("SpecialCharSequenceMgr.getDecimalFromHex", "unable to parse hex", e);
      return "";
    }

    final String serialNum = String.format(Locale.US, "%08d", dec2);

    StringBuilder builder = new StringBuilder(22);
    builder
        .append(manufacturerCode, 0, 5)
        .append(' ')
        .append(manufacturerCode, 5, manufacturerCode.length())
        .append(' ')
        .append(serialNum, 0, 4)
        .append(' ')
        .append(serialNum, 4, serialNum.length());
    return builder.toString();
  }

  /**
   * This method generates a 2d barcode using the zxing library. Each pixel of the bitmap is either
   * black or white painted vertically. We determine which color using the BitMatrix.get(x, y)
   * method.
   */
  private static Bitmap generateBarcode(String hex, int width, int height) {
    MultiFormatWriter writer = new MultiFormatWriter();
    String data = Uri.encode(hex);

    try {
      BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.CODE_128, width, 1);
      Bitmap bitmap = Bitmap.createBitmap(bitMatrix.getWidth(), height, Config.RGB_565);

      for (int i = 0; i < bitMatrix.getWidth(); i++) {
        // Paint columns of width 1
        int[] column = new int[height];
        Arrays.fill(column, bitMatrix.get(i, 0) ? Color.BLACK : Color.WHITE);
        bitmap.setPixels(column, 0, 1, i, 0, 1, height);
      }
      return bitmap;
    } catch (WriterException e) {
      LogUtil.e("SpecialCharSequenceMgr.generateBarcode", "error generating barcode", e);
    }
    return null;
  }

  private static boolean handleRegulatoryInfoDisplay(Context context, String input) {
    if (input.equals(MMI_REGULATORY_INFO_DISPLAY)) {
      LogUtil.i(
          "SpecialCharSequenceMgr.handleRegulatoryInfoDisplay", "sending intent to settings app");
      Intent showRegInfoIntent = new Intent(Settings.ACTION_SHOW_REGULATORY_INFO);
      try {
        context.startActivity(showRegInfoIntent);
      } catch (ActivityNotFoundException e) {
        LogUtil.e(
            "SpecialCharSequenceMgr.handleRegulatoryInfoDisplay", "startActivity() failed: ", e);
      }
      return true;
    }
    return false;
  }

  public static class HandleAdnEntryAccountSelectedCallback extends SelectPhoneAccountListener {

    private final Context context;
    private final QueryHandler queryHandler;
    private final SimContactQueryCookie cookie;

    public HandleAdnEntryAccountSelectedCallback(
        Context context, QueryHandler queryHandler, SimContactQueryCookie cookie) {
      this.context = context;
      this.queryHandler = queryHandler;
      this.cookie = cookie;
    }

    @Override
    public void onPhoneAccountSelected(
        PhoneAccountHandle selectedAccountHandle, boolean setDefault, @Nullable String callId) {
      Uri uri = TelecomUtil.getAdnUriForPhoneAccount(context, selectedAccountHandle);
      handleAdnQuery(queryHandler, cookie, uri);
      // TODO: Show error dialog if result isn't valid.
    }
  }

  public static class HandleMmiAccountSelectedCallback extends SelectPhoneAccountListener {

    private final Context context;
    private final String input;

    public HandleMmiAccountSelectedCallback(Context context, String input) {
      this.context = context.getApplicationContext();
      this.input = input;
    }

    @Override
    public void onPhoneAccountSelected(
        PhoneAccountHandle selectedAccountHandle, boolean setDefault, @Nullable String callId) {
      TelecomUtil.handleMmi(context, input, selectedAccountHandle);
    }
  }

  /**
   * Cookie object that contains everything we need to communicate to the handler's onQuery
   * Complete, as well as what we need in order to cancel the query (if requested).
   *
   * <p>Note, access to the textField field is going to be synchronized, because the user can
   * request a cancel at any time through the UI.
   */
  private static class SimContactQueryCookie implements DialogInterface.OnCancelListener {

    public ProgressDialog progressDialog;
    public int contactNum;

    // Used to identify the query request.
    private int token;
    private QueryHandler handler;

    // The text field we're going to update
    private EditText textField;

    public SimContactQueryCookie(int number, QueryHandler handler, int token) {
      contactNum = number;
      this.handler = handler;
      this.token = token;
    }

    /** Synchronized getter for the EditText. */
    public synchronized EditText getTextField() {
      return textField;
    }

    /** Synchronized setter for the EditText. */
    public synchronized void setTextField(EditText text) {
      textField = text;
    }

    /**
     * Cancel the ADN query by stopping the operation and signaling the cookie that a cancel request
     * is made.
     */
    @Override
    public synchronized void onCancel(DialogInterface dialog) {
      // close the progress dialog
      if (progressDialog != null) {
        progressDialog.dismiss();
      }

      // setting the textfield to null ensures that the UI does NOT get
      // updated.
      textField = null;

      // Cancel the operation if possible.
      handler.cancelOperation(token);
    }
  }

  /**
   * Asynchronous query handler that services requests to look up ADNs
   *
   * <p>Queries originate from {@link #handleAdnEntry}.
   */
  private static class QueryHandler extends NoNullCursorAsyncQueryHandler {

    private boolean canceled;

    public QueryHandler(ContentResolver cr) {
      super(cr);
    }

    /** Override basic onQueryComplete to fill in the textfield when we're handed the ADN cursor. */
    @Override
    protected void onNotNullableQueryComplete(int token, Object cookie, Cursor c) {
      try {
        previousAdnQueryHandler = null;
        if (canceled) {
          return;
        }

        SimContactQueryCookie sc = (SimContactQueryCookie) cookie;

        // close the progress dialog.
        sc.progressDialog.dismiss();

        // get the EditText to update or see if the request was cancelled.
        EditText text = sc.getTextField();

        // if the TextView is valid, and the cursor is valid and positionable on the
        // Nth number, then we update the text field and display a toast indicating the
        // caller name.
        if ((c != null) && (text != null) && (c.moveToPosition(sc.contactNum))) {
          String name = c.getString(c.getColumnIndexOrThrow(ADN_NAME_COLUMN_NAME));
          String number = c.getString(c.getColumnIndexOrThrow(ADN_PHONE_NUMBER_COLUMN_NAME));

          // fill the text in.
          text.getText().replace(0, 0, number);

          // display the name as a toast
          Context context = sc.progressDialog.getContext();
          CharSequence msg =
              ContactDisplayUtils.getTtsSpannedPhoneNumber(
                  context.getResources(), R.string.menu_callNumber, name);
          Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
      } finally {
        MoreCloseables.closeQuietly(c);
      }
    }

    public void cancel() {
      canceled = true;
      // Ask AsyncQueryHandler to cancel the whole request. This will fail when the query is
      // already started.
      cancelOperation(ADN_QUERY_TOKEN);
    }
  }
}
