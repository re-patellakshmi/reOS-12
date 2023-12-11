LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE    := libjni_uart
LOCAL_SRC_FILES := UART.cpp
LOCAL_LDFLAGS   := -llog
LOCAL_CFLAGS += -Wall -Werror -Wunused-parameter
LOCAL_ARM_MODE := arm
LOCAL_SDK_VERSION := current
include $(BUILD_SHARED_LIBRARY)
