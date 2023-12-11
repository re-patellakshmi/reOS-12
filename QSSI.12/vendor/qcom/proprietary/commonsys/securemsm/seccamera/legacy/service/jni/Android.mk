ifneq ($(BUILD_TINY_ANDROID),true)
ifneq ($(TARGET_BOARD_AUTO),true)

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE        := libseccamservice
LOCAL_MODULE_TAGS    := optional

LOCAL_CFLAGS        := $(COMMON_CFLAGS) \
                        -fno-short-enums \
                        -g -fdiagnostics-show-option -Wno-format \
                        -Wno-missing-braces -Wno-missing-field-initializers \
                        -fpermissive -Wno-unused-parameter

LOCAL_SYSTEM_EXT_MODULE := true
LOCAL_PRELINK_MODULE := false
LOCAL_MODULE_OWNER := qti
LOCAL_PROPRIETARY_MODULE := false
LOCAL_SANITIZE := cfi integer_overflow
LOCAL_HEADER_LIBRARIES  := jni_headers display_intf_headers display_proprietary_intf_headers

SECUREMSM_SHIP_PATH :=  $(QC_PROP_ROOT)/commonsys/securemsm

LOCAL_C_INCLUDES += \
  $(LOCAL_PATH) \
  $(LOCAL_PATH)/../../lib \
  $(SECUREMSM_SHIP_PATH)/QSEEComAPI \
  $(JNI_H_INCLUDE) \
  $(SECUREMSM_SHIP_PATH)/../../../../../libnativehelper/include_jni/ \

LOCAL_HEADER_LIBRARIES := vendor_common_inc

LOCAL_SHARED_LIBRARIES := \
    libc \
    liblog \
    libandroid \
    libnativewindow \
    libseccam

LOCAL_SRC_FILES := \
    jni_if.cpp \
    jni_vendor_if.cpp

include $(BUILD_SHARED_LIBRARY)

endif # not TARGET_BOARD_AUTO
endif # not BUILD_TINY_ANDROID
