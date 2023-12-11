LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE        := MangoLwm2mApp
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := APPS
LOCAL_CERTIFICATE   := platform
LOCAL_MODULE_SUFFIX := .apk
LOCAL_SRC_FILES     := MangoLwm2mApp.apk
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/system/app
include $(BUILD_PREBUILT)

#Remove gps apk due to power consumption.
#include $(CLEAR_VARS)
#LOCAL_MODULE        := gpsprovider
#LOCAL_MODULE_OWNER  := qcom
#LOCAL_MODULE_TAGS   := optional
#LOCAL_MODULE_CLASS  := APPS
#LOCAL_CERTIFICATE   := platform
#LOCAL_MODULE_SUFFIX := .apk
#LOCAL_SRC_FILES     := gpsprovider.apk
#LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/system/app
#include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := FotaUpdater
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := APPS
LOCAL_CERTIFICATE   := platform
LOCAL_MODULE_SUFFIX := .apk
LOCAL_SRC_FILES     := FotaUpdater.apk
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/system/app
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_SHARED_LIBRARIES := \
        libcutils \
        libdl \
        liblog \
        libcurl \
        libbinder \
        android.hardware.quectelat@1.0 \
        libbase \
        libhidlbase

###### add dependency for fota netmanager
LOCAL_SHARED_LIBRARIES += libutils
LOCAL_SHARED_LIBRARIES += libutilscallstack

LOCAL_MODULE        := libquecteltel
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_MODULE_SUFFIX := .so
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := libquecteltel.so
LOCAL_SRC_FILES     := libquecteltel/lib64/libquecteltel.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/system/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_SHARED_LIBRARIES := \
        libcutils \
        libdl \
        liblog \
        libcurl \
        libbinder \
        android.hardware.quectelat@1.0 \
        libbase \
        libhidlbase

###### add dependency for fota netmanager
LOCAL_SHARED_LIBRARIES += libutils
LOCAL_SHARED_LIBRARIES += libutilscallstack

LOCAL_MODULE        := libquecteltel
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_MODULE_SUFFIX := .so
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := libquecteltel.so
LOCAL_SRC_FILES     := libquecteltel/lib/libquecteltel.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/system/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_SHARED_LIBRARIES := \
        libcutils \
        libdl \
        liblog \
        libcurl \
        libbinder \
        android.hardware.quectelat@1.0 \
        libbase \
        libhidlbase

###### add dependency for fota netmanager
LOCAL_SHARED_LIBRARIES += libquecteltel
LOCAL_SHARED_LIBRARIES += libutils
LOCAL_SHARED_LIBRARIES += libutilscallstack
LOCAL_MULTILIB      := 32
LOCAL_MODULE        := mango-lwm2m
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := EXECUTABLES
LOCAL_SRC_FILES     := mango-lwm2m/bin/mango-lwm2m
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/system/bin
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_SHARED_LIBRARIES := \
        libcutils \
        libdl \
        liblog \
        libcurl \
        libbinder \
        android.hardware.quectelat@1.0 \
        libbase \
        libhidlbase

###### add dependency for fota netmanager
LOCAL_SHARED_LIBRARIES += libquecteltel
LOCAL_SHARED_LIBRARIES += libutils
LOCAL_SHARED_LIBRARIES += libutilscallstack
LOCAL_MULTILIB      := 64
LOCAL_MODULE        := mango-lwm2m
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := EXECUTABLES
LOCAL_SRC_FILES     := mango-lwm2m/bin64/mango-lwm2m
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/system/bin
include $(BUILD_PREBUILT)

#include $(CLEAR_VARS)
#LOCAL_MODULE        := libqlmodem
#LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
#LOCAL_MODULE_SUFFIX := .so
#LOCAL_MULTILIB      := 64
#LOCAL_MODULE_OWNER  := qcom
#LOCAL_MODULE_TAGS   := optional
#LOCAL_SRC_FILES     := libqlmodem.so
#LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/vendor/lib64
#LOCAL_PROPRIETARY_MODULE := true
#include $(BUILD_PREBUILT)
