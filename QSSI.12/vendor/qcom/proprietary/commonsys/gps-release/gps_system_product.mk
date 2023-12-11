ifneq ($(BOARD_VENDOR_QCOM_GPS_LOC_API_HARDWARE),)

PRODUCT_PACKAGES += vendor.qti.gnss

ifeq ($(TARGET_DEVICE),apq8026_lw)
LW_FEATURE_SET := true
endif

ifeq ($(TARGET_SUPPORTS_WEAR_OS),true)
LW_FEATURE_SET := true
endif

ifneq ($(LW_FEATURE_SET),true)

PRODUCT_PACKAGES += com.qualcomm.location
PRODUCT_PACKAGES += com.qualcomm.location.xml

PRODUCT_PACKAGES += privapp-permissions-com.qualcomm.location.xml
PRODUCT_PACKAGES += xtra_t_app
PRODUCT_PACKAGES += xtra_t_app_setup
PRODUCT_PACKAGES += izat.xt.srv
PRODUCT_PACKAGES += izat.xt.srv.xml
PRODUCT_PACKAGES += com.qti.location.sdk
PRODUCT_PACKAGES += com.qti.location.sdk.xml
PRODUCT_PACKAGES += liblocationservice_jni
PRODUCT_PACKAGES += liblocsdk_diag_jni
PRODUCT_PACKAGES += libxt_native

# GPS_DBG
ifneq ($(TARGET_HAS_LOW_RAM),true)
PRODUCT_PACKAGES_DEBUG += com.qualcomm.qti.qlogcat
endif

PRODUCT_PACKAGES_DEBUG += com.qualcomm.qmapbridge.xml
PRODUCT_PACKAGES_DEBUG += com.qualcomm.qti.izattools.xml
PRODUCT_PACKAGES_DEBUG += ODLT
PRODUCT_PACKAGES_DEBUG += qmapbridge
PRODUCT_PACKAGES_DEBUG += libdiagbridge
PRODUCT_PACKAGES_DEBUG += libloc2jnibridge
PRODUCT_PACKAGES_DEBUG += SampleLocationAttribution

endif # ifneq ($(LW_FEATURE_SET),true)
endif # BOARD_VENDOR_QCOM_GPS_LOC_API_HARDWARE
