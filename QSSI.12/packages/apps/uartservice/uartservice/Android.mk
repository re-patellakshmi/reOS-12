LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := $(call all-java-files-under,src)
LOCAL_RESOURCE_DIR := $(addprefix $(LOCAL_PATH)/,res)
LOCAL_STATIC_ANDROID_LIBRARIES := android-support-v7-appcompat
LOCAL_PACKAGE_NAME := uartservice
LOCAL_JNI_SHARED_LIBRARIES := libjni_uart
LOCAL_SDK_VERSION := current
LOCAL_CERTIFICATE := platform
include $(BUILD_PACKAGE)
include $(call all-makefiles-under,$(LOCAL_PATH))
