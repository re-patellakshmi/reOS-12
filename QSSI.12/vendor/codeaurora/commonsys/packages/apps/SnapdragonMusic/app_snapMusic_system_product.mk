ifeq ($(TARGET_FWK_SUPPORTS_FULL_VALUEADDS),true)
ifneq ($(TARGET_HAS_LOW_RAM),true)
PRODUCT_PACKAGES += \
    SnapdragonMusic

endif
endif