/******************************************************************************
 *
 *  Copyright (c) 2020 - 2021 Qualcomm Technologies, Inc. and/or its subsidiaries.
 *  All Rights Reserved.
 *  Qualcomm Technologies, Inc. and/or its subsidiaries. Confidential and Proprietary.
 *
 ******************************************************************************/
package com.qualcomm.qtil.aptxui;

import androidx.core.app.JobIntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

public class aptxuiNotify extends JobIntentService {

  public static final int JOB_ID = 61707458;
  private static final String TAG = "aptxuiNotify";

  public static final String ACTION_NOTIFY_QSS_SUPPORT = "NOTIFY_QSS_SUPPORT";

  private static String QSS_CHANNEL_ID = "aptxuiNotifyQss";
  private static final int QSS_NOTIFICATION_ID = 61707459;

  private static NotificationManager mNotificationManager = null;
  private static NotificationChannel mQssNotificationChannel = null;
  private static Toast toast = null;

  public static void enqueueWork(Context context, Intent work) {
    enqueueWork(context, aptxuiNotify.class, JOB_ID, work);
  }

  @Override
  protected void onHandleWork(Intent intent) {
    if (intent != null) {
      final String action = intent.getAction();
      Context context = getApplicationContext();
      int codec = aptxuiALSDefs.SOURCE_CODEC_TYPE_NONE;
      String audioProfile = "";

      switch (action) {
        case aptxuiALSDefs.ACTION_CODEC_CONFIGURED:
          codec = intent.getIntExtra(aptxuiALSDefs.CODEC, aptxuiALSDefs.SOURCE_CODEC_TYPE_NONE);
          audioProfile = intent.getStringExtra(aptxuiALSDefs.AUDIO_PROFILE);
          showCodecToast(context, codec, audioProfile);
          break;

        case ACTION_NOTIFY_QSS_SUPPORT:
          showQssNotification(context);
          break;

        default:
          Log.e(TAG, "invalid action");
          return;
      }
    }
  }

  private NotificationManager getNotificationManager() {
    if (mNotificationManager == null) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }
    return mNotificationManager;
  }

  private void showQssNotification(Context context) {
    try {
      String name = getString(R.string.notification_qss_name);
      mQssNotificationChannel = new NotificationChannel(QSS_CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
      getNotificationManager().createNotificationChannel(mQssNotificationChannel);

      int view = R.layout.qti_snapdragon_sound_layout;
      Notification notification = createQssNotification(context, view);
      getNotificationManager().notify(QSS_NOTIFICATION_ID, notification);
    } catch (Exception e) {
      Log.e(TAG, "showQssNotification Exception: " + e);
    }
  }

  private static Notification createQssNotification(Context context, int view) {
    int icon = android.R.drawable.stat_sys_headset;
    RemoteViews contentView = new RemoteViews(context.getPackageName(), view);
    Bitmap bitmap = getBitmap(context, icon);
    contentView.setImageViewBitmap(R.id.icon, bitmap);
    contentView.setImageViewResource(R.id.logo, R.drawable.qc_snp_sound);

    Notification.Builder notificationBuilder = new Notification.Builder(context, QSS_CHANNEL_ID);
    notificationBuilder
        .setSmallIcon(icon)
        .setOngoing(false)
        .setAutoCancel(false)
        .setChannelId(QSS_CHANNEL_ID)
        .setCustomContentView(contentView)
        .setCategory(Notification.CATEGORY_SYSTEM)
        .setDefaults(0)
        .setTimeoutAfter(3000)
        .setPriority(Notification.PRIORITY_MAX)
        .setVisibility(Notification.VISIBILITY_SECRET);
    return notificationBuilder.build();
  }

  private static Bitmap getBitmap(Context context, int resId) {
    int largeIconWidth = (int) context.getResources().getDimension(android.R.dimen.notification_large_icon_width);
    int largeIconHeight = (int) context.getResources().getDimension(android.R.dimen.notification_large_icon_height);
    Drawable d = context.getResources().getDrawable(resId);
    Bitmap b = Bitmap.createBitmap(largeIconWidth, largeIconHeight, Bitmap.Config.ARGB_8888);
    Canvas c = new Canvas(b);
    d.setBounds(0, 0, largeIconWidth, largeIconHeight);
    d.draw(c);
    return b;
  }

  private void showCodecToast(Context context, int codec, String audioProfile) {
    //Log.d(TAG, "Show toast codec:" + codec + " audioProfile:" + audioProfile);
    // Handler will run after 1 second.
    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
      @Override
      public void run() {
        if (codec == aptxuiALSDefs.SOURCE_CODEC_TYPE_APTX
            || codec == aptxuiALSDefs.SOURCE_CODEC_TYPE_APTX_HD
            || codec == aptxuiALSDefs.SOURCE_CODEC_TYPE_APTX_ADAPTIVE
            || codec == aptxuiALSDefs.SOURCE_CODEC_TYPE_APTX_TWSP) {
          if (toast != null) {
          toast.cancel();
          }

          // Get text for toast
          String text = getToastText(context, codec, audioProfile);
          if (!text.isEmpty()) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
          }
        }
      }
    }, 1000);
  }

  private String getToastText(Context context, int codec, String audioProfile) {
    String copyrightStr = context.getString(R.string.copyright_string);
    String trademarkStr = context.getString(R.string.trademark_string);
    String qtiStr = context.getString(R.string.qti_string);
    String aptxStr = context.getString(R.string.aptx_string);
    String spaceStr = " ";
    String codecStr = "";
    String titleStr = "";

    if (codec == aptxuiALSDefs.SOURCE_CODEC_TYPE_APTX) {
      titleStr = qtiStr + copyrightStr + spaceStr + aptxStr + trademarkStr + spaceStr + codecStr;
    } else if (codec == aptxuiALSDefs.SOURCE_CODEC_TYPE_APTX_HD) {
      codecStr = context.getString(R.string.hd_string);
      titleStr = qtiStr + copyrightStr + spaceStr + aptxStr + trademarkStr + spaceStr + codecStr;
    } else if (codec == aptxuiALSDefs.SOURCE_CODEC_TYPE_APTX_ADAPTIVE) {
      codecStr = context.getString(R.string.adaptive_string);
      if (audioProfile.equals(aptxuiALSDefs.AUDIO_PROFILE_GAMING_MODE)) {
        codecStr += spaceStr + context.getString(R.string.gaming_mode_string);
      }
      titleStr = qtiStr + copyrightStr + spaceStr + aptxStr + trademarkStr + spaceStr + codecStr;
    } else if (codec == aptxuiALSDefs.SOURCE_CODEC_TYPE_APTX_TWSP) {
      titleStr = qtiStr + copyrightStr + spaceStr + aptxStr + trademarkStr + spaceStr + codecStr;
    }
    return titleStr;
  }
}
