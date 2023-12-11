PREBUILT_PATH := $(call my-dir)
LOCAL_PATH         := $(PREBUILT_PATH)

include $(CLEAR_VARS)
LOCAL_MODULE        := DeviceStatisticsService
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := APPS
LOCAL_CERTIFICATE   := platform
LOCAL_MODULE_SUFFIX := .apk
LOCAL_SRC_FILES     := ../../.././target/product/qssi/product/app/DeviceStatisticsService/DeviceStatisticsService.apk
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_PRODUCT)/app
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := remotesimlockservice
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := APPS
LOCAL_CERTIFICATE   := platform
LOCAL_MODULE_SUFFIX := .apk
LOCAL_SRC_FILES     := ../../.././target/product/qssi/product/app/remotesimlockservice/remotesimlockservice.apk
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_PRODUCT)/app
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := uimgbaservice
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := APPS
LOCAL_CERTIFICATE   := platform
LOCAL_MODULE_SUFFIX := .apk
LOCAL_SRC_FILES     := ../../.././target/product/qssi/product/app/uimgbaservice/uimgbaservice.apk
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_PRODUCT)/app
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := uimlpaservice
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := APPS
LOCAL_CERTIFICATE   := platform
LOCAL_MODULE_SUFFIX := .apk
LOCAL_SRC_FILES     := ../../.././target/product/qssi/product/app/uimlpaservice/uimlpaservice.apk
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_PRODUCT)/app
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := qvirtmgr
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := EXECUTABLES
LOCAL_SHARED_LIBRARIES :=  liblog libjsoncpp libcutils libc++ libc libm libdl
LOCAL_SRC_FILES     := ../../.././target/product/qssi/product/bin/qvirtmgr
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_PRODUCT)/bin
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := DynamicDDSService
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := APPS
LOCAL_CERTIFICATE   := platform
LOCAL_MODULE_SUFFIX := .apk
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/app/DynamicDDSService/DynamicDDSService.apk
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/app
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := embms
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := APPS
LOCAL_CERTIFICATE   := platform
LOCAL_MODULE_SUFFIX := .apk
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/app/embms/embms.apk
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/app
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := MStatsSystemService
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := APPS
LOCAL_CERTIFICATE   := platform
LOCAL_MODULE_SUFFIX := .apk
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/app/MStatsSystemService/MStatsSystemService.apk
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/app
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := ODLT
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := APPS
LOCAL_CERTIFICATE   := platform
LOCAL_MODULE_SUFFIX := .apk
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/app/ODLT/ODLT.apk
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/app
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := QCC-AUTHMGR
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := APPS
LOCAL_CERTIFICATE   := platform
LOCAL_MODULE_SUFFIX := .apk
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/app/QCC-AUTHMGR/QCC-AUTHMGR.apk
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/app
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := QCC-Netstat
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := APPS
LOCAL_CERTIFICATE   := platform
LOCAL_MODULE_SUFFIX := .apk
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/app/QCC-Netstat/QCC-Netstat.apk
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/app
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := QCC
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := APPS
LOCAL_CERTIFICATE   := platform
LOCAL_MODULE_SUFFIX := .apk
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/app/QCC/QCC.apk
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/app
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := QesdkSysService
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := APPS
LOCAL_CERTIFICATE   := platform
LOCAL_MODULE_SUFFIX := .apk
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/app/QesdkSysService/QesdkSysService.apk
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/app
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := QTIDiagServices
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := APPS
LOCAL_CERTIFICATE   := platform
LOCAL_MODULE_SUFFIX := .apk
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/app/QTIDiagServices/QTIDiagServices.apk
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/app
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := workloadclassifier
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := APPS
LOCAL_CERTIFICATE   := platform
LOCAL_MODULE_SUFFIX := .apk
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/app/workloadclassifier/workloadclassifier.apk
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/app
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := aptxalsOverlayCreateProject.sh
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := EXECUTABLES
LOCAL_SHARED_LIBRARIES := 
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/bin/aptxalsOverlayCreateProject.sh
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/bin
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := dpmd
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := EXECUTABLES
LOCAL_SHARED_LIBRARIES :=  libdpmframework libdiag_system libhardware_legacy libhidlbase libcutils libutils com.qualcomm.qti.dpm.api@1.0 libc++ libc libm libdl
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/bin/dpmd
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/bin
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := qccsyshal@1.1-service
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := EXECUTABLES
LOCAL_SHARED_LIBRARIES :=  libhidlbase libutils libfmq libbinder liblog vendor.qti.hardware.qccsyshal@1.1-halimpl vendor.qti.hardware.qccsyshal@1.1 vendor.qti.hardware.qccsyshal@1.0-halimpl vendor.qti.hardware.qccsyshal@1.0 libc++ libc libm libdl
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/bin/qccsyshal@1.1-service
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/bin
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := qcrosvm
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := EXECUTABLES
LOCAL_CHECK_ELF_FILES := false
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/bin/qcrosvm
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/bin
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := qspmsvc
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := EXECUTABLES
LOCAL_SHARED_LIBRARIES :=  liblog libutils libcutils libbinder vendor.qti.qspmhal@1.0 android.hardware.thermal@1.0 android.hardware.thermal@2.0 android.hidl.memory@1.0 libhidlmemory libhidlbase libc++ libc libm libdl
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/bin/qspmsvc
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/bin
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := qxrsplitauxservice
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := EXECUTABLES
LOCAL_SHARED_LIBRARIES :=  liblog libutils libandroid_runtime libbinder_ndk libcutils libnativewindow libaudioclient libhardware_legacy libavservices_minijail vendor.qti.hardware.qxr-V1-ndk_platform libc++ libc libm libdl
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/bin/qxrsplitauxservice
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/bin
include $(BUILD_PREBUILT)

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := sigma_miracasthalservice
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := EXECUTABLES
LOCAL_32_BIT_ONLY := true
LOCAL_SHARED_LIBRARIES :=  vendor.qti.hardware.sigma_miracast@1.0 vendor.qti.hardware.sigma_miracast@1.0-halimpl libhidlbase libutils liblog libbinder libc++ libc libm libdl
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/bin/sigma_miracasthalservice
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/bin
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := sigma_miracasthalservice64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := EXECUTABLES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.hardware.sigma_miracast@1.0 vendor.qti.hardware.sigma_miracast@1.0-halimpl libhidlbase libutils liblog libbinder libc++ libc libm libdl
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/bin/sigma_miracasthalservice64
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/bin
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE        := tcmd
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := EXECUTABLES
LOCAL_SHARED_LIBRARIES :=  libbinder libcutils libutils libhidlbase libhardware libhardware_legacy liblog vendor.qti.hardware.dpmservice@1.0 libavservices_minijail libc++ libc libm libdl
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/bin/tcmd
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/bin
include $(BUILD_PREBUILT)

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := wfdservice
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := EXECUTABLES
LOCAL_32_BIT_ONLY := true
LOCAL_SHARED_LIBRARIES :=  liblog libutils libcutils libbinder libwfdservice libmmosal libwfdcommonutils libwfdconfigutils libhidlbase libavservices_minijail libc++ libc libm libdl
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/bin/wfdservice
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/bin
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := wfdservice64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := EXECUTABLES
LOCAL_SHARED_LIBRARIES :=  liblog libutils libcutils libbinder libwfdservice libmmosal libwfdcommonutils libwfdconfigutils libhidlbase libavservices_minijail libc++ libc libm libdl
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/bin/wfdservice64
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/bin
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)
endif

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.qesdsys.service.xml
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := ETC
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/etc/vintf/manifest/vendor.qti.qesdsys.service.xml
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/etc/vintf/manifest
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := com.qualcomm.qti.dpm.api@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/com.qualcomm.qti.dpm.api@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

ifeq ($(wildcard vendor/qcom/proprietary/commonsys-intf/wfd-interface),)

include $(CLEAR_VARS)
LOCAL_MODULE        := com.qualcomm.qti.wifidisplayhal@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/com.qualcomm.qti.wifidisplayhal@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE        := com.quicinc.cne.api@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  com.quicinc.cne.constants@1.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/com.quicinc.cne.api@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := com.quicinc.cne.api@1.1
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  com.quicinc.cne.api@1.0 com.quicinc.cne.constants@2.1 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/com.quicinc.cne.api@1.1.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := com.quicinc.cne.constants@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/com.quicinc.cne.constants@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := com.quicinc.cne.constants@2.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/com.quicinc.cne.constants@2.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := com.quicinc.cne.constants@2.1
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/com.quicinc.cne.constants@2.1.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := com.quicinc.cne.server@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  com.quicinc.cne.constants@1.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/com.quicinc.cne.server@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := com.quicinc.cne.server@2.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  com.quicinc.cne.constants@2.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/com.quicinc.cne.server@2.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := com.quicinc.cne.server@2.1
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  com.quicinc.cne.constants@2.1 com.quicinc.cne.server@2.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/com.quicinc.cne.server@2.1.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := com.quicinc.cne.server@2.2
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  com.quicinc.cne.constants@2.0 com.quicinc.cne.constants@2.1 com.quicinc.cne.server@2.0 com.quicinc.cne.server@2.1 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/com.quicinc.cne.server@2.2.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.qccsyshal@1.0-impl
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libbase libcutils libfmq libhidlbase liblog libutils vendor.qti.hardware.qccsyshal@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/hw/vendor.qti.hardware.qccsyshal@1.0-impl.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64/hw
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.qccsyshal@1.1-impl
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libbase libcutils libfmq libhidlbase liblog libutils vendor.qti.hardware.qccsyshal@1.1 vendor.qti.hardware.qccsyshal@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/hw/vendor.qti.hardware.qccsyshal@1.1-impl.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64/hw
include $(BUILD_PREBUILT)

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.sigma_miracast@1.0-impl
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase libutils vendor.qti.hardware.sigma_miracast@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/hw/vendor.qti.hardware.sigma_miracast@1.0-impl.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64/hw
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE        := libadapter
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_CHECK_ELF_FILES := false
LOCAL_MODULE_SUFFIX := .dylib.so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libadapter.dylib.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libAdaptiveEQ_recpp
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libAdaptiveEQ_recpp.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libAGC_recpp
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libAGC_recpp.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libANS_recpp
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libANS_recpp.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libbeluga
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libbase liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libbeluga.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libbinauralrenderer_wrapper.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libvr_amb_engine liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libbinauralrenderer_wrapper.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libcomposerextn.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libcutils liblog libutils libui libsync libhidlbase libdisplayconfig.system.qti libtinyxml2 vendor.qti.hardware.limits@1.0 vendor.qti.hardware.limits@1.1 libbinder libbinder_ndk vendor.qti.hardware.display.config-V5-ndk_platform libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libcomposerextn.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libCompressor_recpp
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libCompressor_recpp.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libdashdatasource
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libmmiipstreammmihttp libembmsmmosal libmmipstreamutils libembmstinyxml liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libdashdatasource.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libdashsamplesource
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libmmiipstreammmihttp libembmsmmosal libmmipstreamutils libembmstinyxml liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libdashsamplesource.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libDiagService
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libandroid_runtime libnativehelper libutils libcutils liblog libdiag_system libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libDiagService.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libdolphin
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libcutils liblog libutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libdolphin.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libdpmctmgr
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libdiag_system libbinder libcutils libutils libdpmframework com.qualcomm.qti.dpm.api@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libdpmctmgr.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libdpmfdmgr
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libdiag_system libbinder libcutils libutils libdpmframework com.qualcomm.qti.dpm.api@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libdpmfdmgr.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libdpmframework
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libdiag_system libbinder libcutils libutils liblog libhidlbase libhardware libhardware_legacy com.qualcomm.qti.dpm.api@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libdpmframework.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libdpmtcm
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libdiag_system libbinder libcutils libutils libdpmframework com.qualcomm.qti.dpm.api@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libdpmtcm.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libembmsmmosal
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libembmsmmosal.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libembmsmmparser_lite
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libembmsmmosal liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libembmsmmparser_lite.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libembmssqlite
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libembmssqlite.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libembmstinyxml
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libembmstinyxml.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libGPQTEEC_system.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc libcutils libutils liblog libhidlbase libGPTEE_system.qti libQTEEConnector_system vendor.qti.hardware.qteeconnector@1.0 libc++ libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libGPQTEEC_system.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libGPTEE_system.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc libcutils libutils liblog libc++ libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libGPTEE_system.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libHDR_recpp
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libHDR_recpp.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libhoaeffects_csim
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libhoaeffects_csim.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libhoaeffects.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhoaeffects_csim libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libhoaeffects.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libHPFilter_recpp
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libHPFilter_recpp.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := lib-imsvtextutils
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libutils libcutils liblog libdiag_system lib-imsvtutils libGLESv2 libEGL libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/lib-imsvtextutils.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := lib-imsvt
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  lib-imsvideocodec libmediandk libnativewindow libion libdmabufheap lib-imsvtutils libandroid libhidlbase vendor.qti.imsrtpservice@3.0 libdiag_system libcutils libutils liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/lib-imsvt.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := lib-imsvtutils
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libutils libcutils liblog libdiag_system libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/lib-imsvtutils.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libjnihelpers
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libjnihelpers.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := liblayerext.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libcutils liblog libutils libtinyxml2 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/liblayerext.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libLimiter_recpp
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libLimiter_recpp.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := liblistensoundmodel2.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libcutils liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/liblistensoundmodel2.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libloc2jnibridge
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libutils libqmi_cci_system liblog libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libloc2jnibridge.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libmink-sock-native-api
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  liblog libminksocket_system libjnihelpers libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libmink-sock-native-api.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libmiracastsystem
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_CHECK_ELF_FILES := false
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libmiracastsystem.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE        := libmmhttpstack
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libembmsmmosal libcutils libmmipstreamutils libmmipstreamnetwork liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libmmhttpstack.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libmmiipstreammmihttp
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libembmsmmosal libmmipstreamutils libembmsmmparser_lite libmmipstreamsourcehttp liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libmmiipstreammmihttp.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libmmio
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_CHECK_ELF_FILES := false
LOCAL_MODULE_SUFFIX := .dylib.so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libmmio.dylib.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libmmipstreamnetwork
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libembmsmmosal liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libmmipstreamnetwork.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libmmipstreamsourcehttp
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libembmstinyxml libembmsmmosal libembmsmmparser_lite libmmipstreamnetwork libmmipstreamutils libmmhttpstack liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libmmipstreamsourcehttp.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libmmipstreamutils
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libembmsmmosal liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libmmipstreamutils.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libmmparser_lite
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libutils libcutils liblog libmmosal libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libmmparser_lite.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libmmQSM
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libembmsmmosal libembmssqlite liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libmmQSM.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/mm-rtp),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libmmrtpdecoder
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libutils liblog libcutils libmmosal libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libmmrtpdecoder.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/mm-rtp),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libmmrtpencoder
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libmmosal liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libmmrtpencoder.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE        := libmsp
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libz libjsoncpp libxml2 liblog libutils libhidlbase vendor.qti.hardware.embmssl@1.0 vendor.qti.hardware.embmssl@1.0-adapter-helper vendor.qti.hardware.embmssl@1.1 vendor.qti.hardware.embmssl@1.1-adapter-helper libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libmsp.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libmwqemiptablemgr
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  liblog libcutils libbase libutils libdpmframework libdiag_system com.qualcomm.qti.dpm.api@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libmwqemiptablemgr.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libOpenCL_system
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libcutils liblog libvndksupport libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libOpenCL_system.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libqape.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libnativehelper libcutils libutils liblog libbinder libhidlbase libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libqape.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libqccAuthMgr
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  liblog libutils libcutils libbase libhidlbase vendor.qti.hardware.qccvndhal@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libqccAuthMgr.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libqcc_file_agent_sys
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libcutils liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libqcc_file_agent_sys.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libqcc_netstats
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libcutils liblog libutils libbase libhidlbase vendor.qti.hardware.qccvndhal@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libqcc_netstats.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libqcc
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libcutils liblog libutils libbase libfmq libhidlbase vendor.qti.hardware.qccvndhal@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libqcc.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libqesdk_ndk_platform.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase libutils vendor.qti.qesdhal@1.0 vendor.qti.qesdhal@1.1 liblog libcutils libcrypto libjsoncpp libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libqesdk_ndk_platform.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libQOC.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libQOC.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libQSEEComAPI_system
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc libcutils libutils liblog libhidlbase libhidlmemory libdmabufheap vendor.qti.hardware.qseecom@1.0 android.hidl.allocator@1.0 android.hidl.memory@1.0 libc++ libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libQSEEComAPI_system.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libqspmsvc
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  liblog libutils libcutils libbinder libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libqspmsvc.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libQTEEConnector_system
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libcutils libutils vendor.qti.hardware.qteeconnector@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libQTEEConnector_system.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libqti_workloadclassifiermodel
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libnativehelper libcutils liblog libutils libbase libtflite libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libqti_workloadclassifiermodel.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := librecpp_intf
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libHDR_recpp libWNR_intf libANS_recpp libHPFilter_recpp libAGC_recpp libCompressor_recpp libAdaptiveEQ_recpp libLimiter_recpp liblog libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/librecpp_intf.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libsdm-disp-apis.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libcutils libutils liblog libhidlbase vendor.display.color@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libsdm-disp-apis.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libseccam
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  liblog libandroid libnativewindow libmediandk libcutils libseccam-ipc vendor.display.config@1.14 vendor.qti.hardware.seccam@1.0 libdisplayconfig.system.qti libhwbinder libhidlbase libhidltransport libbase libutils libQTEEConnector_system vendor.qti.hardware.qteeconnector@1.0 android.hidl.allocator@1.0 android.hidl.memory@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libseccam.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libskewknob_system
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  liblog libc libcutils libc++ libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libskewknob_system.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libsmomoconfig.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libutils libbinder libcomposerextn.qti liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libsmomoconfig.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libthermalclient.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libutils liblog libbinder libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libthermalclient.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libtrigger-handler
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libcutils libutils liblog vendor.qti.qspmhal@1.0 libhidlbase libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libtrigger-handler.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libupdateprof.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.qspmhal@1.0 libhidlbase libhidltransport liblog libutils libcutils android.hidl.allocator@1.0 android.hidl.memory@1.0 libhidlmemory libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libupdateprof.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libvr_amb_engine
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libvr_amb_engine.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libvraudio_client.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  liblog libvraudio libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libvraudio_client.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libvraudio
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  liblog libutils libcutils libstagefright libstagefright_foundation libdatasource libmedia libbinder libaudioutils libandroid libandroidfw libmemunreachable libaudioclient libaudiohal libvr_sam_wrapper libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libvraudio.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libvr_object_engine
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libvr_object_engine.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libvr_sam_wrapper
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libvr_object_engine libvr_amb_engine liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libvr_sam_wrapper.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfdclient
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_CHECK_ELF_FILES := false
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libwfdclient.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfdcommonutils
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_CHECK_ELF_FILES := false
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libwfdcommonutils.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfddisplayconfig
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase libutils libcutils liblog vendor.display.config@2.0 libdisplayconfig.system.qti libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libwfddisplayconfig.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfdmminterface
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libmmosal liblog libutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libwfdmminterface.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfdmmsink
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_CHECK_ELF_FILES := false
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libwfdmmsink.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfdmmsrc_system
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_CHECK_ELF_FILES := false
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libwfdmmsrc_system.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfdrtsp
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_CHECK_ELF_FILES := false
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libwfdrtsp.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfdservice
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_CHECK_ELF_FILES := false
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libwfdservice.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfdsinksm
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_CHECK_ELF_FILES := false
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libwfdsinksm.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfduibcinterface
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libmmosal liblog libutils libcutils libwfduibcsrcinterface libwfduibcsinkinterface libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libwfduibcinterface.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfduibcsinkinterface
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_CHECK_ELF_FILES := false
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libwfduibcsinkinterface.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfduibcsink
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libmmosal libutils liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libwfduibcsink.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfduibcsrcinterface
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_CHECK_ELF_FILES := false
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libwfduibcsrcinterface.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfduibcsrc
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_CHECK_ELF_FILES := false
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libwfduibcsrc.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE        := libWNR_intf
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libWNR liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libWNR_intf.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libWNR
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libWNR.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libxrvd.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  liblog libutils libandroid_runtime libnativedisplay android.hardware.graphics.common-V1-ndk_platform android.hardware.graphics.common@1.1 android.hardware.graphics.common@1.2 android.hardware.graphics.bufferqueue@1.0 android.hardware.graphics.bufferqueue@2.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/libxrvd.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.data.factory@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  com.quicinc.cne.api@1.0 com.quicinc.cne.api@1.1 com.quicinc.cne.constants@2.1 com.quicinc.cne.server@2.0 com.quicinc.cne.server@2.1 com.quicinc.cne.server@2.2 vendor.qti.hardware.data.dynamicdds@1.0 vendor.qti.hardware.data.qmi@1.0 vendor.qti.ims.rcsconfig@1.0 vendor.qti.latency@2.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.data.factory@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.data.factory@2.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.hardware.data.cne.internal.api@1.0 vendor.qti.hardware.data.cne.internal.constants@1.0 vendor.qti.hardware.data.cne.internal.server@1.0 vendor.qti.hardware.data.dynamicdds@1.0 vendor.qti.hardware.data.qmi@1.0 vendor.qti.ims.rcsconfig@1.0 vendor.qti.latency@2.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.data.factory@2.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.data.factory@2.1
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.data.factory@2.0 vendor.qti.data.slm@1.0 vendor.qti.hardware.data.cne.internal.api@1.0 vendor.qti.hardware.data.cne.internal.constants@1.0 vendor.qti.hardware.data.cne.internal.server@1.0 vendor.qti.hardware.data.dynamicdds@1.0 vendor.qti.hardware.data.qmi@1.0 vendor.qti.ims.rcsconfig@1.0 vendor.qti.ims.rcsconfig@1.1 vendor.qti.latency@2.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.data.factory@2.1.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.data.factory@2.2
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.data.factory@2.0 vendor.qti.data.factory@2.1 vendor.qti.data.mwqem@1.0 vendor.qti.data.slm@1.0 vendor.qti.hardware.data.cne.internal.api@1.0 vendor.qti.hardware.data.cne.internal.constants@1.0 vendor.qti.hardware.data.cne.internal.server@1.0 vendor.qti.hardware.data.dynamicdds@1.0 vendor.qti.hardware.data.qmi@1.0 vendor.qti.ims.rcsconfig@1.0 vendor.qti.ims.rcsconfig@1.1 vendor.qti.latency@2.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.data.factory@2.2.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.data.factory@2.3
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.data.factory@2.0 vendor.qti.data.factory@2.1 vendor.qti.data.factory@2.2 vendor.qti.data.mwqem@1.0 vendor.qti.data.slm@1.0 vendor.qti.hardware.data.cne.internal.api@1.0 vendor.qti.hardware.data.cne.internal.constants@1.0 vendor.qti.hardware.data.cne.internal.server@1.0 vendor.qti.hardware.data.dynamicdds@1.0 vendor.qti.hardware.data.lce@1.0 vendor.qti.hardware.data.qmi@1.0 vendor.qti.ims.rcsconfig@1.0 vendor.qti.ims.rcsconfig@1.1 vendor.qti.latency@2.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.data.factory@2.3.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.data.factory@2.4
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.data.factory@2.0 vendor.qti.data.factory@2.1 vendor.qti.data.factory@2.2 vendor.qti.data.factory@2.3 vendor.qti.data.mwqem@1.0 vendor.qti.data.slm@1.0 vendor.qti.hardware.data.cne.internal.api@1.0 vendor.qti.hardware.data.cne.internal.constants@1.0 vendor.qti.hardware.data.cne.internal.server@1.0 vendor.qti.hardware.data.cne.internal.server@1.1 vendor.qti.hardware.data.dynamicdds@1.0 vendor.qti.hardware.data.lce@1.0 vendor.qti.hardware.data.qmi@1.0 vendor.qti.ims.rcsconfig@1.0 vendor.qti.ims.rcsconfig@1.1 vendor.qti.latency@2.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.data.factory@2.4.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.data.factory@2.5
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.data.factory@2.0 vendor.qti.data.factory@2.1 vendor.qti.data.factory@2.2 vendor.qti.data.factory@2.3 vendor.qti.data.factory@2.4 vendor.qti.data.mwqem@1.0 vendor.qti.data.slm@1.0 vendor.qti.hardware.data.cne.internal.api@1.0 vendor.qti.hardware.data.cne.internal.constants@1.0 vendor.qti.hardware.data.cne.internal.server@1.0 vendor.qti.hardware.data.cne.internal.server@1.1 vendor.qti.hardware.data.cne.internal.server@1.2 vendor.qti.hardware.data.dynamicdds@1.0 vendor.qti.hardware.data.flow@1.0 vendor.qti.hardware.data.lce@1.0 vendor.qti.hardware.data.qmi@1.0 vendor.qti.ims.rcsconfig@1.0 vendor.qti.ims.rcsconfig@1.1 vendor.qti.latency@2.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.data.factory@2.5.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.data.cne.internal.api@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.hardware.data.cne.internal.constants@1.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.hardware.data.cne.internal.api@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.data.cne.internal.constants@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.hardware.data.cne.internal.constants@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.data.cne.internal.server@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.hardware.data.cne.internal.constants@1.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.hardware.data.cne.internal.server@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.data.cne.internal.server@1.1
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.hardware.data.cne.internal.constants@1.0 vendor.qti.hardware.data.cne.internal.server@1.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.hardware.data.cne.internal.server@1.1.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.data.cne.internal.server@1.2
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.hardware.data.cne.internal.constants@1.0 vendor.qti.hardware.data.cne.internal.server@1.0 vendor.qti.hardware.data.cne.internal.server@1.1 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.hardware.data.cne.internal.server@1.2.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.data.qmi@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.hardware.data.qmi@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.dpmservice@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.hardware.dpmservice@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.dpmservice@1.1
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.hardware.dpmservice@1.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.hardware.dpmservice@1.1.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.embmssl@1.0-adapter-helper
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libbase libcutils libhidlbase liblog libutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.hardware.embmssl@1.0-adapter-helper.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.embmssl@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.hardware.embmssl@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.embmssl@1.1-adapter-helper
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libbase libcutils libhidlbase liblog libutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.hardware.embmssl@1.1-adapter-helper.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.embmssl@1.1
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.hardware.embmssl@1.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.hardware.embmssl@1.1.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.mwqemadapter@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.hardware.mwqemadapter@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.qccsyshal@1.0-halimpl
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libcutils liblog libhidlbase libutils libfmq libqcc_file_agent_sys vendor.qti.hardware.qccsyshal@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.hardware.qccsyshal@1.0-halimpl.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.qccsyshal@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.hardware.qccsyshal@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.qccsyshal@1.1-halimpl
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libprotobuf-cpp-full libcutils liblog libhidlbase libutils libfmq libqcc_file_agent_sys vendor.qti.hardware.qccsyshal@1.1 vendor.qti.hardware.qccsyshal@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.hardware.qccsyshal@1.1-halimpl.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.qccsyshal@1.1
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.hardware.qccsyshal@1.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.hardware.qccsyshal@1.1.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.qccvndhal@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.hardware.qccvndhal@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.sigma_miracast@1.0-halimpl
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase vendor.qti.hardware.sigma_miracast@1.0 libutils libmmosal libmiracastsystem libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.hardware.sigma_miracast@1.0-halimpl.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys-intf/wfd-interface),)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.sigma_miracast@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.hardware.sigma_miracast@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.slmadapter@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.hardware.slmadapter@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

ifeq ($(wildcard vendor/qcom/proprietary/commonsys-intf/wfd-interface),)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.wifidisplaysession@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.hardware.wifidisplaysession@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.ims.connection@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.ims.connection@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.ims.factory@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.ims.callcapability@1.0 vendor.qti.ims.rcsconfig@2.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.ims.factory@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.ims.factory@1.1
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.ims.callcapability@1.0 vendor.qti.ims.factory@1.0 vendor.qti.ims.rcsconfig@2.0 vendor.qti.ims.rcsconfig@2.1 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.ims.factory@1.1.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.ims.factory@2.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.ims.connection@1.0 vendor.qti.ims.callcapability@1.0 vendor.qti.ims.rcsuce@1.0 vendor.qti.ims.rcssip@1.0 vendor.qti.ims.configservice@1.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.ims.factory@2.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.ims.factory@2.1
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.ims.callcapability@1.0 vendor.qti.ims.configservice@1.0 vendor.qti.ims.configservice@1.1 vendor.qti.ims.connection@1.0 vendor.qti.ims.factory@2.0 vendor.qti.ims.rcssip@1.0 vendor.qti.ims.rcssip@1.1 vendor.qti.ims.rcsuce@1.0 vendor.qti.ims.rcsuce@1.1 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.ims.factory@2.1.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.ims.rcssip@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.ims.rcssip@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.ims.rcssip@1.1
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.ims.rcssip@1.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.ims.rcssip@1.1.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.ims.rcsuce@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.ims.rcsuce@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.ims.rcsuce@1.1
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.ims.rcsuce@1.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.ims.rcsuce@1.1.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.imsrtpservice@3.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.imsrtpservice@3.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.mstatservice@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.mstatservice@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.qesdhal@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.qesdhal@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.qesdhal@1.1
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.qesdhal@1.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 64
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib64/vendor.qti.qesdhal@1.1.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib64
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := com.qualcomm.qti.dpm.api@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/com.qualcomm.qti.dpm.api@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

ifeq ($(wildcard vendor/qcom/proprietary/commonsys-intf/wfd-interface),)

include $(CLEAR_VARS)
LOCAL_MODULE        := com.qualcomm.qti.wifidisplayhal@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/com.qualcomm.qti.wifidisplayhal@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE        := com.quicinc.cne.api@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  com.quicinc.cne.constants@1.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/com.quicinc.cne.api@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := com.quicinc.cne.api@1.1
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  com.quicinc.cne.api@1.0 com.quicinc.cne.constants@2.1 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/com.quicinc.cne.api@1.1.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := com.quicinc.cne.constants@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/com.quicinc.cne.constants@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := com.quicinc.cne.constants@2.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/com.quicinc.cne.constants@2.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := com.quicinc.cne.constants@2.1
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/com.quicinc.cne.constants@2.1.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := com.quicinc.cne.server@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  com.quicinc.cne.constants@1.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/com.quicinc.cne.server@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := com.quicinc.cne.server@2.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  com.quicinc.cne.constants@2.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/com.quicinc.cne.server@2.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := com.quicinc.cne.server@2.1
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  com.quicinc.cne.constants@2.1 com.quicinc.cne.server@2.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/com.quicinc.cne.server@2.1.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := com.quicinc.cne.server@2.2
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  com.quicinc.cne.constants@2.0 com.quicinc.cne.constants@2.1 com.quicinc.cne.server@2.0 com.quicinc.cne.server@2.1 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/com.quicinc.cne.server@2.2.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.qccsyshal@1.0-impl
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libbase libcutils libfmq libhidlbase liblog libutils vendor.qti.hardware.qccsyshal@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/hw/vendor.qti.hardware.qccsyshal@1.0-impl.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib/hw
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.qccsyshal@1.1-impl
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libbase libcutils libfmq libhidlbase liblog libutils vendor.qti.hardware.qccsyshal@1.1 vendor.qti.hardware.qccsyshal@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/hw/vendor.qti.hardware.qccsyshal@1.1-impl.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib/hw
include $(BUILD_PREBUILT)

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.sigma_miracast@1.0-impl
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase libutils vendor.qti.hardware.sigma_miracast@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/hw/vendor.qti.hardware.sigma_miracast@1.0-impl.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib/hw
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE        := libAdaptiveEQ_recpp
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libAdaptiveEQ_recpp.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libAGC_recpp
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libAGC_recpp.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libANS_recpp
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libANS_recpp.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libbeluga
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libbase liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libbeluga.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libbinauralrenderer_wrapper.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libvr_amb_engine liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libbinauralrenderer_wrapper.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libcomposerextn.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libcutils liblog libutils libui libsync libhidlbase libdisplayconfig.system.qti libtinyxml2 vendor.qti.hardware.limits@1.0 vendor.qti.hardware.limits@1.1 libbinder libbinder_ndk vendor.qti.hardware.display.config-V5-ndk_platform libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libcomposerextn.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libCompressor_recpp
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libCompressor_recpp.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libDiagService
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libandroid_runtime libnativehelper libutils libcutils liblog libdiag_system libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libDiagService.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libdolphin
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libcutils liblog libutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libdolphin.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libdpmctmgr
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libdiag_system libbinder libcutils libutils libdpmframework com.qualcomm.qti.dpm.api@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libdpmctmgr.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libdpmfdmgr
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libdiag_system libbinder libcutils libutils libdpmframework com.qualcomm.qti.dpm.api@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libdpmfdmgr.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libdpmframework
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libdiag_system libbinder libcutils libutils liblog libhidlbase libhardware libhardware_legacy com.qualcomm.qti.dpm.api@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libdpmframework.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libdpmtcm
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libdiag_system libbinder libcutils libutils libdpmframework com.qualcomm.qti.dpm.api@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libdpmtcm.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libGPQTEEC_system.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc libcutils libutils liblog libhidlbase libGPTEE_system.qti libQTEEConnector_system vendor.qti.hardware.qteeconnector@1.0 libc++ libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libGPQTEEC_system.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libGPTEE_system.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc libcutils libutils liblog libc++ libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libGPTEE_system.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libHDR_recpp
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libHDR_recpp.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libhoaeffects_csim
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libhoaeffects_csim.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libhoaeffects.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhoaeffects_csim libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libhoaeffects.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libHPFilter_recpp
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libHPFilter_recpp.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := lib-imsvtextutils
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libutils libcutils liblog libdiag_system lib-imsvtutils libGLESv2 libEGL libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/lib-imsvtextutils.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := lib-imsvt
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  lib-imsvideocodec libmediandk libnativewindow libion libdmabufheap lib-imsvtutils libandroid libhidlbase vendor.qti.imsrtpservice@3.0 libdiag_system libcutils libutils liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/lib-imsvt.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := lib-imsvtutils
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libutils libcutils liblog libdiag_system libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/lib-imsvtutils.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libjnihelpers
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libjnihelpers.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := liblayerext.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libcutils liblog libutils libtinyxml2 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/liblayerext.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libLimiter_recpp
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libLimiter_recpp.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := liblistensoundmodel2.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libcutils liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/liblistensoundmodel2.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libloc2jnibridge
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libutils libqmi_cci_system liblog libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libloc2jnibridge.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libmink-sock-native-api
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  liblog libminksocket_system libjnihelpers libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libmink-sock-native-api.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libmiracastsystem
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_CHECK_ELF_FILES := false
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libmiracastsystem.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE        := libmmparser_lite
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libutils libcutils liblog libmmosal libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libmmparser_lite.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/mm-rtp),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libmmrtpdecoder
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libutils liblog libcutils libmmosal libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libmmrtpdecoder.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/mm-rtp),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libmmrtpencoder
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libmmosal liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libmmrtpencoder.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE        := libmwqemiptablemgr
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  liblog libcutils libbase libutils libdpmframework libdiag_system com.qualcomm.qti.dpm.api@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libmwqemiptablemgr.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libOpenCL_system
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libcutils liblog libvndksupport libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libOpenCL_system.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libqape.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libnativehelper libcutils libutils liblog libbinder libhidlbase libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libqape.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libqccAuthMgr
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  liblog libutils libcutils libbase libhidlbase vendor.qti.hardware.qccvndhal@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libqccAuthMgr.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libqcc_file_agent_sys
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libcutils liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libqcc_file_agent_sys.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libqcc_netstats
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libcutils liblog libutils libbase libhidlbase vendor.qti.hardware.qccvndhal@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libqcc_netstats.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libqcc
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libcutils liblog libutils libbase libfmq libhidlbase vendor.qti.hardware.qccvndhal@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libqcc.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libqesdk_ndk_platform.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase libutils vendor.qti.qesdhal@1.0 vendor.qti.qesdhal@1.1 liblog libcutils libcrypto libjsoncpp libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libqesdk_ndk_platform.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libQSEEComAPI_system
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc libcutils libutils liblog libhidlbase libhidlmemory libdmabufheap vendor.qti.hardware.qseecom@1.0 android.hidl.allocator@1.0 android.hidl.memory@1.0 libc++ libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libQSEEComAPI_system.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libqspmsvc
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  liblog libutils libcutils libbinder libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libqspmsvc.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libQTEEConnector_system
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libcutils libutils vendor.qti.hardware.qteeconnector@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libQTEEConnector_system.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libqti_workloadclassifiermodel
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libnativehelper libcutils liblog libutils libbase libtflite libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libqti_workloadclassifiermodel.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := librecpp_intf
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libHDR_recpp libWNR_intf libANS_recpp libHPFilter_recpp libAGC_recpp libCompressor_recpp libAdaptiveEQ_recpp libLimiter_recpp liblog libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/librecpp_intf.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libsdm-disp-apis.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libcutils libutils liblog libhidlbase vendor.display.color@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libsdm-disp-apis.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libseccam
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  liblog libandroid libnativewindow libmediandk libcutils libseccam-ipc vendor.display.config@1.14 vendor.qti.hardware.seccam@1.0 libdisplayconfig.system.qti libhwbinder libhidlbase libhidltransport libbase libutils libQTEEConnector_system vendor.qti.hardware.qteeconnector@1.0 android.hidl.allocator@1.0 android.hidl.memory@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libseccam.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libskewknob_system
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  liblog libc libcutils libc++ libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libskewknob_system.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libsmomoconfig.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libutils libbinder libcomposerextn.qti liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libsmomoconfig.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libthermalclient.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libutils liblog libbinder libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libthermalclient.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libtrigger-handler
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libcutils libutils liblog vendor.qti.qspmhal@1.0 libhidlbase libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libtrigger-handler.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libupdateprof.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.qspmhal@1.0 libhidlbase libhidltransport liblog libutils libcutils android.hidl.allocator@1.0 android.hidl.memory@1.0 libhidlmemory libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libupdateprof.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libvr_amb_engine
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libvr_amb_engine.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libvraudio_client.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  liblog libvraudio libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libvraudio_client.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libvraudio
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  liblog libutils libcutils libstagefright libstagefright_foundation libdatasource libmedia libbinder libaudioutils libandroid libandroidfw libmemunreachable libaudioclient libaudiohal libvr_sam_wrapper libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libvraudio.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libvr_object_engine
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libvr_object_engine.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libvr_sam_wrapper
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libvr_object_engine libvr_amb_engine liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libvr_sam_wrapper.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfdclient
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_CHECK_ELF_FILES := false
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libwfdclient.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfdcommonutils
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_CHECK_ELF_FILES := false
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libwfdcommonutils.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfddisplayconfig
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase libutils libcutils liblog vendor.display.config@2.0 libdisplayconfig.system.qti libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libwfddisplayconfig.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfdmminterface
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libmmosal liblog libutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libwfdmminterface.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfdmmsink
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_CHECK_ELF_FILES := false
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libwfdmmsink.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfdmmsrc_system
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_CHECK_ELF_FILES := false
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libwfdmmsrc_system.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfdrtsp
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_CHECK_ELF_FILES := false
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libwfdrtsp.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfdservice
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_CHECK_ELF_FILES := false
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libwfdservice.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfdsinksm
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_CHECK_ELF_FILES := false
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libwfdsinksm.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfduibcinterface
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libmmosal liblog libutils libcutils libwfduibcsrcinterface libwfduibcsinkinterface libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libwfduibcinterface.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfduibcsinkinterface
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_CHECK_ELF_FILES := false
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libwfduibcsinkinterface.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfduibcsink
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libmmosal libutils liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libwfduibcsink.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfduibcsrcinterface
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_CHECK_ELF_FILES := false
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libwfduibcsrcinterface.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := libwfduibcsrc
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_CHECK_ELF_FILES := false
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libwfduibcsrc.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE        := libWNR_intf
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libWNR liblog libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libWNR_intf.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libWNR
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libWNR.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := libxrvd.qti
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  liblog libutils libandroid_runtime libnativedisplay android.hardware.graphics.common-V1-ndk_platform android.hardware.graphics.common@1.1 android.hardware.graphics.common@1.2 android.hardware.graphics.bufferqueue@1.0 android.hardware.graphics.bufferqueue@2.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/libxrvd.qti.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.data.factory@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  com.quicinc.cne.api@1.0 com.quicinc.cne.api@1.1 com.quicinc.cne.constants@2.1 com.quicinc.cne.server@2.0 com.quicinc.cne.server@2.1 com.quicinc.cne.server@2.2 vendor.qti.hardware.data.dynamicdds@1.0 vendor.qti.hardware.data.qmi@1.0 vendor.qti.ims.rcsconfig@1.0 vendor.qti.latency@2.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.data.factory@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.data.factory@2.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.hardware.data.cne.internal.api@1.0 vendor.qti.hardware.data.cne.internal.constants@1.0 vendor.qti.hardware.data.cne.internal.server@1.0 vendor.qti.hardware.data.dynamicdds@1.0 vendor.qti.hardware.data.qmi@1.0 vendor.qti.ims.rcsconfig@1.0 vendor.qti.latency@2.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.data.factory@2.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.data.factory@2.1
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.data.factory@2.0 vendor.qti.data.slm@1.0 vendor.qti.hardware.data.cne.internal.api@1.0 vendor.qti.hardware.data.cne.internal.constants@1.0 vendor.qti.hardware.data.cne.internal.server@1.0 vendor.qti.hardware.data.dynamicdds@1.0 vendor.qti.hardware.data.qmi@1.0 vendor.qti.ims.rcsconfig@1.0 vendor.qti.ims.rcsconfig@1.1 vendor.qti.latency@2.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.data.factory@2.1.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.data.factory@2.2
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.data.factory@2.0 vendor.qti.data.factory@2.1 vendor.qti.data.mwqem@1.0 vendor.qti.data.slm@1.0 vendor.qti.hardware.data.cne.internal.api@1.0 vendor.qti.hardware.data.cne.internal.constants@1.0 vendor.qti.hardware.data.cne.internal.server@1.0 vendor.qti.hardware.data.dynamicdds@1.0 vendor.qti.hardware.data.qmi@1.0 vendor.qti.ims.rcsconfig@1.0 vendor.qti.ims.rcsconfig@1.1 vendor.qti.latency@2.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.data.factory@2.2.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.data.factory@2.3
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.data.factory@2.0 vendor.qti.data.factory@2.1 vendor.qti.data.factory@2.2 vendor.qti.data.mwqem@1.0 vendor.qti.data.slm@1.0 vendor.qti.hardware.data.cne.internal.api@1.0 vendor.qti.hardware.data.cne.internal.constants@1.0 vendor.qti.hardware.data.cne.internal.server@1.0 vendor.qti.hardware.data.dynamicdds@1.0 vendor.qti.hardware.data.lce@1.0 vendor.qti.hardware.data.qmi@1.0 vendor.qti.ims.rcsconfig@1.0 vendor.qti.ims.rcsconfig@1.1 vendor.qti.latency@2.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.data.factory@2.3.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.data.factory@2.4
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.data.factory@2.0 vendor.qti.data.factory@2.1 vendor.qti.data.factory@2.2 vendor.qti.data.factory@2.3 vendor.qti.data.mwqem@1.0 vendor.qti.data.slm@1.0 vendor.qti.hardware.data.cne.internal.api@1.0 vendor.qti.hardware.data.cne.internal.constants@1.0 vendor.qti.hardware.data.cne.internal.server@1.0 vendor.qti.hardware.data.cne.internal.server@1.1 vendor.qti.hardware.data.dynamicdds@1.0 vendor.qti.hardware.data.lce@1.0 vendor.qti.hardware.data.qmi@1.0 vendor.qti.ims.rcsconfig@1.0 vendor.qti.ims.rcsconfig@1.1 vendor.qti.latency@2.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.data.factory@2.4.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.data.factory@2.5
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.data.factory@2.0 vendor.qti.data.factory@2.1 vendor.qti.data.factory@2.2 vendor.qti.data.factory@2.3 vendor.qti.data.factory@2.4 vendor.qti.data.mwqem@1.0 vendor.qti.data.slm@1.0 vendor.qti.hardware.data.cne.internal.api@1.0 vendor.qti.hardware.data.cne.internal.constants@1.0 vendor.qti.hardware.data.cne.internal.server@1.0 vendor.qti.hardware.data.cne.internal.server@1.1 vendor.qti.hardware.data.cne.internal.server@1.2 vendor.qti.hardware.data.dynamicdds@1.0 vendor.qti.hardware.data.flow@1.0 vendor.qti.hardware.data.lce@1.0 vendor.qti.hardware.data.qmi@1.0 vendor.qti.ims.rcsconfig@1.0 vendor.qti.ims.rcsconfig@1.1 vendor.qti.latency@2.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.data.factory@2.5.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.data.cne.internal.api@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.hardware.data.cne.internal.constants@1.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.hardware.data.cne.internal.api@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.data.cne.internal.constants@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.hardware.data.cne.internal.constants@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.data.cne.internal.server@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.hardware.data.cne.internal.constants@1.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.hardware.data.cne.internal.server@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.data.cne.internal.server@1.1
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.hardware.data.cne.internal.constants@1.0 vendor.qti.hardware.data.cne.internal.server@1.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.hardware.data.cne.internal.server@1.1.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.data.cne.internal.server@1.2
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.hardware.data.cne.internal.constants@1.0 vendor.qti.hardware.data.cne.internal.server@1.0 vendor.qti.hardware.data.cne.internal.server@1.1 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.hardware.data.cne.internal.server@1.2.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.data.qmi@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.hardware.data.qmi@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.dpmservice@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.hardware.dpmservice@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.dpmservice@1.1
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.hardware.dpmservice@1.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.hardware.dpmservice@1.1.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.mwqemadapter@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.hardware.mwqemadapter@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.qccsyshal@1.0-halimpl
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libcutils liblog libhidlbase libutils libfmq libqcc_file_agent_sys vendor.qti.hardware.qccsyshal@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.hardware.qccsyshal@1.0-halimpl.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.qccsyshal@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.hardware.qccsyshal@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.qccsyshal@1.1-halimpl
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libprotobuf-cpp-full libcutils liblog libhidlbase libutils libfmq libqcc_file_agent_sys vendor.qti.hardware.qccsyshal@1.1 vendor.qti.hardware.qccsyshal@1.0 libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.hardware.qccsyshal@1.1-halimpl.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.qccsyshal@1.1
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.hardware.qccsyshal@1.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.hardware.qccsyshal@1.1.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.qccvndhal@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.hardware.qccvndhal@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

ifeq ($(wildcard vendor/qcom/proprietary/commonsys/wfd-framework),)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.sigma_miracast@1.0-halimpl
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase vendor.qti.hardware.sigma_miracast@1.0 libutils libmmosal libmiracastsystem libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.hardware.sigma_miracast@1.0-halimpl.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)
endif

ifeq ($(wildcard vendor/qcom/proprietary/commonsys-intf/wfd-interface),)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.sigma_miracast@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.hardware.sigma_miracast@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.slmadapter@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.hardware.slmadapter@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

ifeq ($(wildcard vendor/qcom/proprietary/commonsys-intf/wfd-interface),)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.hardware.wifidisplaysession@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.hardware.wifidisplaysession@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.ims.connection@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.ims.connection@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.ims.factory@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.ims.callcapability@1.0 vendor.qti.ims.rcsconfig@2.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.ims.factory@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.ims.factory@1.1
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.ims.callcapability@1.0 vendor.qti.ims.factory@1.0 vendor.qti.ims.rcsconfig@2.0 vendor.qti.ims.rcsconfig@2.1 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.ims.factory@1.1.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.ims.factory@2.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.ims.connection@1.0 vendor.qti.ims.callcapability@1.0 vendor.qti.ims.rcsuce@1.0 vendor.qti.ims.rcssip@1.0 vendor.qti.ims.configservice@1.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.ims.factory@2.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.ims.factory@2.1
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.ims.callcapability@1.0 vendor.qti.ims.configservice@1.0 vendor.qti.ims.configservice@1.1 vendor.qti.ims.connection@1.0 vendor.qti.ims.factory@2.0 vendor.qti.ims.rcssip@1.0 vendor.qti.ims.rcssip@1.1 vendor.qti.ims.rcsuce@1.0 vendor.qti.ims.rcsuce@1.1 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.ims.factory@2.1.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.ims.rcssip@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.ims.rcssip@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.ims.rcssip@1.1
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.ims.rcssip@1.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.ims.rcssip@1.1.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.ims.rcsuce@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.ims.rcsuce@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.ims.rcsuce@1.1
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.ims.rcsuce@1.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.ims.rcsuce@1.1.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.imsrtpservice@3.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.imsrtpservice@3.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.mstatservice@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.mstatservice@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.qesdhal@1.0
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.qesdhal@1.0.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := vendor.qti.qesdhal@1.1
LOCAL_MODULE_CLASS  := SHARED_LIBRARIES
LOCAL_SHARED_LIBRARIES :=  vendor.qti.qesdhal@1.0 libhidlbase liblog libutils libcutils libc++ libc libm libdl
LOCAL_MODULE_SUFFIX := .so
LOCAL_STRIP_MODULE  := false
LOCAL_MULTILIB      := 32
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/lib/vendor.qti.qesdhal@1.1.so
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/lib
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := aptxals
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := APPS
LOCAL_CERTIFICATE   := platform
LOCAL_MODULE_SUFFIX := .apk
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/priv-app/aptxals/aptxals.apk
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/priv-app
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := dpmserviceapp
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := APPS
LOCAL_CERTIFICATE   := platform
LOCAL_MODULE_SUFFIX := .apk
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/priv-app/dpmserviceapp/dpmserviceapp.apk
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/priv-app
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := MSDC_UI
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := APPS
LOCAL_CERTIFICATE   := platform
LOCAL_MODULE_SUFFIX := .apk
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/priv-app/MSDC_UI/MSDC_UI.apk
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/priv-app
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := QAS_DVC_MSP
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := APPS
LOCAL_CERTIFICATE   := platform
LOCAL_MODULE_SUFFIX := .apk
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/priv-app/QAS_DVC_MSP/QAS_DVC_MSP.apk
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/priv-app
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := xrcbservice
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := APPS
LOCAL_CERTIFICATE   := platform
LOCAL_MODULE_SUFFIX := .apk
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/priv-app/xrcbservice/xrcbservice.apk
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/priv-app
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := xrvdservice
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := APPS
LOCAL_CERTIFICATE   := platform
LOCAL_MODULE_SUFFIX := .apk
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/priv-app/xrvdservice/xrvdservice.apk
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/priv-app
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := xtra_t_app_setup
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := APPS
LOCAL_CERTIFICATE   := platform
LOCAL_MODULE_SUFFIX := .apk
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/priv-app/xtra_t_app_setup/xtra_t_app_setup.apk
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/priv-app
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE        := xtra_t_app
LOCAL_MODULE_OWNER  := qcom
LOCAL_MODULE_TAGS   := optional
LOCAL_MODULE_CLASS  := APPS
LOCAL_CERTIFICATE   := platform
LOCAL_MODULE_SUFFIX := .apk
LOCAL_SRC_FILES     := ../../.././target/product/qssi/system_ext/priv-app/xtra_t_app/xtra_t_app.apk
LOCAL_MODULE_PATH   := $(PRODUCT_OUT)/$(TARGET_COPY_OUT_SYSTEM_EXT)/priv-app
include $(BUILD_PREBUILT)
LOCAL_PATH         := $(PREBUILT_PATH)

include $(CLEAR_VARS)
LOCAL_MODULE               := com.qti.dpmframework
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/com.qti.dpmframework_intermediates/classes.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := dpmapi
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/dpmapi_intermediates/classes.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := dpmapi
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/dpmapi_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := izat.xt.srv
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/izat.xt.srv_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := tcmclient
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/tcmclient_intermediates/classes.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.hardware.slmadapter-V1.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.hardware.slmadapter-V1.0-java_intermediates/classes.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.hardware.slmadapter-V1.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.hardware.slmadapter-V1.0-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.hardware.mwqemadapter-V1.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.hardware.mwqemadapter-V1.0-java_intermediates/classes.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.hardware.mwqemadapter-V1.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.hardware.mwqemadapter-V1.0-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.hardware.dpmservice-V1.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.hardware.dpmservice-V1.0-java_intermediates/classes.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.hardware.dpmservice-V1.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.hardware.dpmservice-V1.0-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.hardware.dpmservice-V1.1-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.hardware.dpmservice-V1.1-java_intermediates/classes.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.hardware.dpmservice-V1.1-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.hardware.dpmservice-V1.1-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := com.quicinc.cne.api-V1.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/com.quicinc.cne.api-V1.0-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := com.quicinc.cne.api-V1.1-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/com.quicinc.cne.api-V1.1-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.hardware.data.cne.internal.api-V1.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.hardware.data.cne.internal.api-V1.0-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := com.quicinc.cne.server-V1.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/com.quicinc.cne.server-V1.0-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := com.quicinc.cne.server-V2.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/com.quicinc.cne.server-V2.0-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := com.quicinc.cne.server-V2.1-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/com.quicinc.cne.server-V2.1-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := com.quicinc.cne.server-V2.2-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/com.quicinc.cne.server-V2.2-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.hardware.data.cne.internal.server-V1.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.hardware.data.cne.internal.server-V1.0-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.hardware.data.cne.internal.server-V1.1-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.hardware.data.cne.internal.server-V1.1-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := com.quicinc.cne.constants-V1.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/com.quicinc.cne.constants-V1.0-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := com.quicinc.cne.constants-V2.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/com.quicinc.cne.constants-V2.0-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := com.quicinc.cne.constants-V2.1-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/com.quicinc.cne.constants-V2.1-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.hardware.data.cne.internal.constants-V1.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.hardware.data.cne.internal.constants-V1.0-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.data.factory-V1.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.data.factory-V1.0-java_intermediates/classes.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.data.factory-V1.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.data.factory-V1.0-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.data.factory-V2.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.data.factory-V2.0-java_intermediates/classes.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.data.factory-V2.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.data.factory-V2.0-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.data.factory-V2.1-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.data.factory-V2.1-java_intermediates/classes.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.data.factory-V2.1-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.data.factory-V2.1-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.data.factory-V2.2-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.data.factory-V2.2-java_intermediates/classes.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.data.factory-V2.3-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.data.factory-V2.3-java_intermediates/classes.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.data.factory-V2.4-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.data.factory-V2.4-java_intermediates/classes.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.data.factory-V2.4-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.data.factory-V2.4-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.data.factory-V2.5-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.data.factory-V2.5-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.hardware.data.qmi-V1.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.hardware.data.qmi-V1.0-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.ims.factory-V1.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.ims.factory-V1.0-java_intermediates/classes.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.ims.factory-V1.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.ims.factory-V1.0-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.ims.factory-V1.1-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.ims.factory-V1.1-java_intermediates/classes.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.ims.factory-V1.1-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.ims.factory-V1.1-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.ims.factory-V2.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.ims.factory-V2.0-java_intermediates/classes.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.ims.factory-V2.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.ims.factory-V2.0-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.ims.factory-V2.1-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.ims.factory-V2.1-java_intermediates/classes.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.ims.factory-V2.1-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.ims.factory-V2.1-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.ims.rcsuce-V1.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.ims.rcsuce-V1.0-java_intermediates/classes.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.ims.rcsuce-V1.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.ims.rcsuce-V1.0-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.ims.rcsuce-V1.1-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.ims.rcsuce-V1.1-java_intermediates/classes.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.ims.rcsuce-V1.1-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.ims.rcsuce-V1.1-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.ims.connection-V1.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.ims.connection-V1.0-java_intermediates/classes.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.ims.connection-V1.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.ims.connection-V1.0-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.ims.rcssip-V1.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.ims.rcssip-V1.0-java_intermediates/classes.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.ims.rcssip-V1.0-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.ims.rcssip-V1.0-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.ims.rcssip-V1.1-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.ims.rcssip-V1.1-java_intermediates/classes.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif

include $(CLEAR_VARS)
LOCAL_MODULE               := vendor.qti.ims.rcssip-V1.1-java
LOCAL_MODULE_OWNER         := qcom
LOCAL_MODULE_TAGS          := optional
LOCAL_MODULE_CLASS         := JAVA_LIBRARIES
LOCAL_MODULE_SUFFIX        := $(COMMON_JAVA_PACKAGE_SUFFIX)
LOCAL_SRC_FILES            := ../../.././target/common/obj/JAVA_LIBRARIES/vendor.qti.ims.rcssip-V1.1-java_intermediates/classes-header.jar
LOCAL_UNINSTALLABLE_MODULE := true
ifeq ($(MODULE.TARGET.$(LOCAL_MODULE_CLASS).$(LOCAL_MODULE)),)
include $(BUILD_PREBUILT)
endif
