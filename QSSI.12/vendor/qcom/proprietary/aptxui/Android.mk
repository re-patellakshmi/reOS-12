ifneq ($(TARGET_HAS_LOW_RAM),true)
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
LOCAL_DEX_PREOPT := false
LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_PROGUARD_ENABLED := disabled
LOCAL_CERTIFICATE := platform
LOCAL_PROTOC_OPTIMIZE_TYPE := micro
LOCAL_PACKAGE_NAME := aptxui
LOCAL_USE_AAPT2 := true
LOCAL_SDK_VERSION  := current
LOCAL_SYSTEM_EXT_MODULE := true

LOCAL_STATIC_ANDROID_LIBRARIES := \
     androidx.preference_preference \

include $(BUILD_PACKAGE)
endif #TARGET_HAS_LOW_RAM
