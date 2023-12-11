# Legacy secure camera binaries
ifneq ($(TARGET_BOARD_AUTO),true)
SECUREMSM_SECCAM_DBG := seccamsample
SECUREMSM_SECCAM += seccamservice
endif
SECUREMSM_SECCAM := libseccamservice
SECUREMSM_SECCAM += libseccam-ipc
SECUREMSM_SECCAM += libseccam
SECUREMSM_SECCAM += vendor.qti.hardware.seccam@1.0

# Secure Camera 2.0 binaries
#ifneq ($(TARGET_BOARD_AUTO),true)
#SECUREMSM_SECCAM_DBG += Cam2test
#endif
SECUREMSM_SECCAM += com.qti.media.secureprocessor
SECUREMSM_SECCAM += libmediasp_jni
SECUREMSM_SECCAM += vendor.qti.hardware.secureprocessor.common@1.0
SECUREMSM_SECCAM += vendor.qti.hardware.secureprocessor.config@1.0
SECUREMSM_SECCAM += vendor.qti.hardware.secureprocessor.device@1.0
SECUREMSM_SECCAM += vendor.qti.hardware.secureprocessor.common@1.0-helper
SECUREMSM_SECCAM += com.qti.media.secureprocessor.xml

# Legacy secure display/ui binaries
ifneq ($(TARGET_BOARD_AUTO),true)
SECUREMSM_SECDISP := com.qualcomm.qti.services.secureui
endif
SECUREMSM_SECDISP += libsecureuisvc_jni
SECUREMSM_SECDISP += libsecureui_svcsock_system
SECUREMSM_SECDISP += vendor.qti.hardware.tui_comm@1.0
SECUREMSM_SECDISP += vendor.qti.hardware.qdutils_disp@1.0

# TrustedUI 2.0 binaries
ifneq ($(TARGET_BOARD_AUTO),true)
SECUREMSM_SECDISP += com.qualcomm.qti.services.systemhelper
endif
SECUREMSM_SECDISP += libsystemhelper_jni
SECUREMSM_SECDISP += vendor.qti.hardware.systemhelper@1.0

# Add to PRODUCT_PACKAGES
PRODUCT_PACKAGES += $(SECUREMSM_SECCAM)
PRODUCT_PACKAGES += $(SECUREMSM_SECDISP)
PRODUCT_PACKAGES_DEBUG += $(SECUREMSM_SECCAM_DBG)
