ifneq ($(ENABLE_HYP), true)

IMS_SHIP_RCS += ImsRcsService
IMS_SHIP_RCS += whitelist_com.qualcomm.qti.uceShimService
IMS_SHIP_RCS += uceShimService
IMS_SHIP_RCS += vendor.qti.ims.rcsservice.xml

#IMS_SHIP_VT += lib-imscamera
IMS_SHIP_VT += lib-imsvideocodec

PRODUCT_PACKAGES += $(IMS_SHIP_RCS)
PRODUCT_PACKAGES += $(IMS_SHIP_VT)

endif
