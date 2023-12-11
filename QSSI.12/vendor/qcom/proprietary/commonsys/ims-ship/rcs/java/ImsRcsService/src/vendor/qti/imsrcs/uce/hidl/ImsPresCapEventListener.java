/**********************************************************************
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 **********************************************************************/


package vendor.qti.imsrcs.uce.hidl;

import android.net.Uri;
import android.telephony.ims.RcsUceAdapter;
import android.telephony.ims.stub.RcsCapabilityExchangeImplBase;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import vendor.qti.ims.rcsuce.V1_0.PresPublishTriggerType;
import vendor.qti.ims.rcsuce.V1_0.PresSubscriptionState;
import vendor.qti.ims.rcsuce.V1_0.SipResponse;
import vendor.qti.ims.rcsuce.V1_0.SubscriptionInfo;
import vendor.qti.ims.rcsuce.V1_0.SubscriptionStatus;
import vendor.qti.ims.rcsuce.V1_0.UceStatusCode;

import android.util.Log;
import vendor.qti.imsrcs.ImsRcsService;
public abstract class ImsPresCapEventListener {
    protected Executor mExecutor;

    private final String LOG_TAG = ImsRcsService.LOG_TAG + ":ImsPresCapEventListener";
    /**
     * Executor: the thread where the callback will be executed on.
     * */
    public ImsPresCapEventListener(Executor e) {
        mExecutor = e;
    }
    public final void handleCmdStatus(long userData, int i) {
        if(i == UceStatusCode.SUCCESS) {
            return;
        }
        mExecutor.execute(()-> {
            Log.d(LOG_TAG, "handleCmdStatus :: cmdStatus[" +i +"]");
            onCmdStatusError(userData, convertHidlStatusCode(i));
        });
    }
    public final void handleSipResponse(long userdata, SipResponse hidlSipResponse, int retryAfter) {
        mExecutor.execute(()-> {
            onSipResponse(userdata,hidlSipResponse.code, hidlSipResponse.reasonPhrase, hidlSipResponse.reasonHeader,
                    retryAfter);
        });
    }
    public void handleCapInfo(long userdata, SubscriptionStatus subscriptionStatus, ArrayList<SubscriptionInfo> contactSubsInfo) {
        mExecutor.execute(()-> {
            //Log.d(LOG_TAG, "handleCapInfo :: cmdStatus[" +i +"]");
            ArrayList<String> pidfXmls = new ArrayList<String >();
            ArrayList<Pair<Uri,String>> UriTerminatedReason = new ArrayList<Pair<Uri,String>>();
            for(SubscriptionInfo info : contactSubsInfo) {
                pidfXmls.add(info.pidfXml);
                if(info.subStatus.state == PresSubscriptionState.TERMINATED) {
                    UriTerminatedReason.add(new Pair<Uri, String>(Uri.parse(info.uri), info.subStatus.terminationReason));
                }
            }
            onNotifyCapabilitiesUpdate(userdata,pidfXmls);
            if(UriTerminatedReason.size() >0) {
                onResourceTerminated(userdata,UriTerminatedReason);
            }
            if(subscriptionStatus.state == PresSubscriptionState.TERMINATED) {
                onTerminated(userdata,subscriptionStatus.terminationReason, 0);
            }
        });
    }
    public final void handleServiceStatus(int s) {
        mExecutor.execute(()->{
            onServiceStatus(s);
        });
    }

    public final void handlePresenceServiceDied() {
        mExecutor.execute(()->{
            onPresenceServiceDied();
        });
    }

    public final void handlePublishTrigger(int hidlTrigger) {
        mExecutor.execute(()->{
           int aospTriggerType = RcsUceAdapter.CAPABILITY_UPDATE_TRIGGER_UNKNOWN;
           if(hidlTrigger == PresPublishTriggerType.ETAG_EXPIRED) {
               aospTriggerType = RcsUceAdapter.CAPABILITY_UPDATE_TRIGGER_ETAG_EXPIRED;
           } else if(hidlTrigger == PresPublishTriggerType.MOVE_TO_2G) {
               aospTriggerType = RcsUceAdapter.CAPABILITY_UPDATE_TRIGGER_MOVE_TO_2G;
           } else if(hidlTrigger == PresPublishTriggerType.MOVE_TO_3G) {
               aospTriggerType = RcsUceAdapter.CAPABILITY_UPDATE_TRIGGER_MOVE_TO_3G;
           } else if(hidlTrigger == PresPublishTriggerType.MOVE_TO_EHRPD) {
               aospTriggerType = RcsUceAdapter.CAPABILITY_UPDATE_TRIGGER_MOVE_TO_EHRPD;
           } else if(hidlTrigger == PresPublishTriggerType.MOVE_TO_HSPAPLUS) {
               aospTriggerType = RcsUceAdapter.CAPABILITY_UPDATE_TRIGGER_MOVE_TO_HSPAPLUS;
           } else if(hidlTrigger == PresPublishTriggerType.MOVE_TO_IWLAN) {
               aospTriggerType = RcsUceAdapter.CAPABILITY_UPDATE_TRIGGER_MOVE_TO_IWLAN;
           } else if(hidlTrigger == PresPublishTriggerType.MOVE_TO_LTE_VOPS_DISABLED) {
               aospTriggerType = RcsUceAdapter.CAPABILITY_UPDATE_TRIGGER_MOVE_TO_LTE_VOPS_DISABLED;
           } else if(hidlTrigger == PresPublishTriggerType.MOVE_TO_LTE_VOPS_ENABLED) {
               aospTriggerType = RcsUceAdapter.CAPABILITY_UPDATE_TRIGGER_MOVE_TO_LTE_VOPS_ENABLED;
           } else if(hidlTrigger == PresPublishTriggerType.MOVE_TO_NR5G_VOPS_DISABLED) {
               aospTriggerType = RcsUceAdapter.CAPABILITY_UPDATE_TRIGGER_MOVE_TO_NR5G_VOPS_DISABLED;
           } else if(hidlTrigger == PresPublishTriggerType.MOVE_TO_NR5G_VOPS_ENABLED) {
               aospTriggerType = RcsUceAdapter.CAPABILITY_UPDATE_TRIGGER_MOVE_TO_NR5G_VOPS_ENABLED;
           }else if(hidlTrigger == PresPublishTriggerType.MOVE_TO_WLAN) {
               aospTriggerType = RcsUceAdapter.CAPABILITY_UPDATE_TRIGGER_MOVE_TO_WLAN;
           }
            onRequestPublishCapabilities(aospTriggerType);
        });
    }

    public final void handlUnPublish() {
        mExecutor.execute(() -> {
            onUnpublish();
        });
    }

    /*overriding functions */
    public void onServiceStatus(int s) {}
    public void onPresenceServiceDied(){}
    public void onRequestPublishCapabilities(int aospPublishTriggerType) {}
    public void onUnpublish(){}


    public void onCmdStatusError(long userdata, int i) {}
    public void onSipResponse(long userdata, int code, String ReasonPhrase, String reasonHeader, int retryAfter) {}

    public void onNotifyCapabilitiesUpdate(long userdata, List<String> PidfXmls) {}
    public void onResourceTerminated(long userdata, List<Pair<Uri, String>> uriTerminatedReason) {}
    public void onTerminated(long userdata, String reason, long retryAfterMilliseconds){}

    public static int convertHidlStatusCode(int uceStatusCode) {
        if(UceStatusCode.SUCCESS == uceStatusCode) {
            return -1;
        }
        if(UceStatusCode.FAILURE == uceStatusCode) {
            return RcsCapabilityExchangeImplBase.COMMAND_CODE_GENERIC_FAILURE;
        } else if(UceStatusCode.SERVICE_UNKNOWN == uceStatusCode) {
            return RcsCapabilityExchangeImplBase.COMMAND_CODE_SERVICE_UNKNOWN;
        } else if(UceStatusCode.FETCH_ERROR == uceStatusCode) {
            return RcsCapabilityExchangeImplBase.COMMAND_CODE_FETCH_ERROR;
        } else if(UceStatusCode.INSUFFICIENT_MEMORY == uceStatusCode) {
            return RcsCapabilityExchangeImplBase.COMMAND_CODE_INSUFFICIENT_MEMORY;
        } else if(UceStatusCode.INVALID_PARAM == uceStatusCode) {
            return RcsCapabilityExchangeImplBase.COMMAND_CODE_INVALID_PARAM;
        } else if(UceStatusCode.LOST_NET == uceStatusCode) {
            return RcsCapabilityExchangeImplBase.COMMAND_CODE_LOST_NETWORK_CONNECTION;
        } else if(UceStatusCode.NO_CHANGE_IN_CAP == uceStatusCode) {
            return RcsCapabilityExchangeImplBase.COMMAND_CODE_NO_CHANGE;
        } else if(UceStatusCode.NOT_FOUND == uceStatusCode) {
            return RcsCapabilityExchangeImplBase.COMMAND_CODE_NOT_FOUND;
        } else if(UceStatusCode.NOT_SUPPORTED == uceStatusCode) {
            return RcsCapabilityExchangeImplBase.COMMAND_CODE_NOT_SUPPORTED;
        } else if(UceStatusCode.REQUEST_TIMEOUT == uceStatusCode) {
            return RcsCapabilityExchangeImplBase.COMMAND_CODE_REQUEST_TIMEOUT;
        } else if(UceStatusCode.SERVICE_UNAVAILABLE == uceStatusCode) {
            return RcsCapabilityExchangeImplBase.COMMAND_CODE_SERVICE_UNAVAILABLE;
        } else {
            return RcsCapabilityExchangeImplBase.COMMAND_CODE_SERVICE_UNKNOWN;
        }
    }
}
