/******************************************************************************
 *
 *  Copyright (c) 2020 - 2021 Qualcomm Technologies, Inc. and/or its subsidiaries.
 *  All Rights Reserved.
 *  Qualcomm Technologies, Inc. and/or its subsidiaries. Confidential and Proprietary.
 *
 ******************************************************************************/
package com.qualcomm.qtil.aptxui;

public class aptxuiALSDefs {
  // Codec ID definitions
  public static int SOURCE_CODEC_TYPE_APTX = -1;
  public static int SOURCE_CODEC_TYPE_APTX_HD = -1;
  public static int SOURCE_CODEC_TYPE_APTX_ADAPTIVE = -1;
  public static int SOURCE_CODEC_TYPE_APTX_TWSP = -1;
  public static int SOURCE_CODEC_TYPE_MAX = -1;
  public static int SOURCE_QVA_CODEC_TYPE_MAX = -1;
  public static int SOURCE_CODEC_TYPE_LC3 = -1;
  public static int SOURCE_CODEC_TYPE_NONE = -1;

  //
  // Message defintions
  //
  // Message name from ALS to indicate when a codec has been configured
  // Intent provides all the codec details needed for notifications
  public static final String ACTION_CODEC_CONFIGURED = "ACTION_CODEC_CONFIGURED";

  //
  // Message Parameter definitions
  //
  // String to identify Codec ID in message from ALS in ACTION_CODEC_CONFIGURED.
  public static final String CODEC = "codec";

  // String to identify the audio profile name in ACTION_CODEC_CONFIGURED.
  // Null is aptxals monitor not in use.
  public static final String AUDIO_PROFILE = "audio_profile";

  // Strings values available for AUDIO_PROFILE
  public static final String AUDIO_PROFILE_GAMING_MODE = "GAMING_MODE";
  public static final String AUDIO_PROFILE_HIGH_QUALITY = "HIGH_QUALITY";

  // String to identify the connected device address in ACTION_CODEC_CONFIGURED
  public static final String CONNECTED_DEVICE_ADDRESS = "connected_device_address";

  // String to identify the Snapdragon Sound support in ACTION_CODEC_CONFIGURED
  public static final String QSS_SUPPORT = "qss_support";

}

