ifneq ($(TARGET_HAS_LOW_RAM),true)
ifeq ($(TARGET_FWK_SUPPORTS_FULL_VALUEADDS),true)
PRODUCT_PACKAGES += \
    PowerOffAlarm \
    SimContact \
    PerformanceMode \
    QColor

ifneq (,$(filter userdebug eng, $(TARGET_BUILD_VARIANT)))
  SMART_TRACE := binder_trace_dump.sh
  SMART_TRACE += perfetto_dump.sh
  SMART_TRACE += perfetto.cfg
  PRODUCT_PACKAGES += $(SMART_TRACE)
endif
endif
endif
