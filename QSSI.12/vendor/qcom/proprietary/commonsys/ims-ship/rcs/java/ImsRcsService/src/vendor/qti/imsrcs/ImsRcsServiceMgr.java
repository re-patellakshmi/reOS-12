/**********************************************************************
 * Copyright (c) 2021 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 **********************************************************************/

package vendor.qti.imsrcs;

import android.os.IHwBinder;
import android.os.RemoteException;
import android.telephony.ims.stub.CapabilityExchangeEventListener;
import android.util.Log;

import vendor.qti.imsrcs.config.ImsConfigServiceWrapper;
import vendor.qti.imsrcs.siptransport.hidl.SipTransportServiceWrapper;
import vendor.qti.imsrcs.uce.hidl.OptionsServiceWrapper;
import vendor.qti.imsrcs.uce.hidl.PresenceServiceWrapper;
import vendor.qti.imsrcs.uce.ImsRcsCapabilityExchangeImpl;

import java.util.NoSuchElementException;
import java.util.concurrent.Executor;

import vendor.qti.ims.factory.V2_0.IImsFactory;
import vendor.qti.ims.configservice.V1_0.IConfigService;
import vendor.qti.ims.rcsuce.V1_0.IPresenceService;
import vendor.qti.ims.rcsuce.V1_0.IOptionsService;
import vendor.qti.ims.rcssip.V1_0.ISipTransportService;
import vendor.qti.imsrcs.config.ImsConfigServiceImpl;

//singleton
public class ImsRcsServiceMgr {
    final String LOG_TAG = "ImsRcsServiceMgr";
    static IImsFactory mImsFactoryInstance = null;
    PresenceServiceWrapper[] mPresenceService;
    OptionsServiceWrapper[] mOptionsService;
    ImsConfigServiceWrapper[] mConfigService;
    SipTransportServiceWrapper[] mSipTransportService;
    ImsRcsCapabilityExchangeImpl[] mRcsCapExchanges;
    private static ImsRcsServiceMgr mMgr;
    private ImsRcsServiceMgr() {
        mSipTransportService = new SipTransportServiceWrapper[ImsRcsService.MAX_SLOTS];
    }

    synchronized public static ImsRcsServiceMgr getInstance() {
        if(mMgr == null) {
          mMgr = new ImsRcsServiceMgr();
        }
        return mMgr;
    }

    public void dispose() {

    }

    public boolean Initialize() {
        if(mImsFactoryInstance == null) {
            //Initialize the arrays of Hidl objects required for this service
            Log.d(LOG_TAG, "initialize()");
            mPresenceService = new PresenceServiceWrapper[ImsRcsService.MAX_SLOTS];
            mOptionsService = new OptionsServiceWrapper[ImsRcsService.MAX_SLOTS];
            mConfigService = new ImsConfigServiceWrapper[ImsRcsService.MAX_SLOTS];

            try {
                mImsFactoryInstance = IImsFactory.getService("default", true);
                 Log.d(LOG_TAG, "IImsFactory.getService");
            } catch (RemoteException | NoSuchElementException e) {
                Log.e(LOG_TAG, "Unable to bind to IImsFactory instance");
            }
            if (mImsFactoryInstance == null) {
                return false;
            } else {
                try {
                    mImsFactoryInstance.linkToDeath(new FactoryDeathRecipient(), -1);
                    Log.d(LOG_TAG, "mImsFactoryInstance.linkToDeath");
                } catch (RemoteException e) {
                    Log.e(LOG_TAG, "Unable to bind to DeathRecipient");
                }
            }
        }
        if(mRcsCapExchanges == null) {
            //@Note: there is a chance that FW never delete's this object
            mRcsCapExchanges = new ImsRcsCapabilityExchangeImpl[ImsRcsService.MAX_SLOTS];
        }
        return  true;
    }

    public ImsRcsCapabilityExchangeImpl getRcsCapExchangeImpl(
            CapabilityExchangeEventListener l, int SlotId, Executor localexec) {
        //@NOTE: We will create new CapExImpl if fw Requests it
        mRcsCapExchanges[SlotId] = new ImsRcsCapabilityExchangeImpl(localexec, l, SlotId);
        return mRcsCapExchanges[SlotId];
    }
    public ImsConfigServiceWrapper getConfigService(int slotId) {
        if(mImsFactoryInstance != null && mConfigService[slotId] == null) {
            //Create the hidl service object only if needed
            mConfigService[slotId] = new ImsConfigServiceWrapper();
            IImsFactory.createConfigServiceCallback hidl_config_cb = new IImsFactory.createConfigServiceCallback() {
                @Override
                public void onValues(int status, IConfigService iImsConfigService) {
                    Log.d(LOG_TAG,IImsFactory.StatusCode.toString(status));
                    if(status == IImsFactory.StatusCode.OK) {
                        mConfigService[slotId].setHidlConfigService(iImsConfigService);
                    }
                    else{
                        /* handle scenario when iconfigservice creation fails */
                    }
                }
            };
            try {
                mImsFactoryInstance.createConfigService(slotId+1, mConfigService[slotId].gethidlConfigListener(), hidl_config_cb);
            } catch (RemoteException | NoSuchElementException e) {
                Log.e(LOG_TAG, "Unable to get to IConfigService instance");
            }
        }
        return mConfigService[slotId];
    }

    public SipTransportServiceWrapper getSipTransportService(int slotId) {
        return mSipTransportService[slotId];
    }

    public SipTransportServiceWrapper getSipTransportService(
        int slotId,
        SipTransportServiceWrapper.ImsSipTransportEventListener listener)
    {
        if(mImsFactoryInstance != null && mSipTransportService[slotId] == null) {
            //Create the hidl service object only if needed
            mSipTransportService[slotId] = new
              SipTransportServiceWrapper(slotId, listener);
            IImsFactory.createRcsSipTransportServiceCallback hidl_sipTransport_cb =
              new IImsFactory.createRcsSipTransportServiceCallback() {
                @Override
                public void onValues(int i,
                                     ISipTransportService iSipTransportService) {
                    mSipTransportService[slotId].setHidlSipTransportService(
                        iSipTransportService);
                }
            };
            try {
                mImsFactoryInstance.createRcsSipTransportService(
                        slotId+1,
                        mSipTransportService[slotId].getHidlSipTransportListener(),
                        hidl_sipTransport_cb);
            } catch (RemoteException | NoSuchElementException e) {
                Log.e(LOG_TAG, "Unable to get to ISipTransportService instance");
            }
        }
        return mSipTransportService[slotId];
    }

    public PresenceServiceWrapper getImsPresenceService(int slotId) {
        if(mImsFactoryInstance != null && mPresenceService[slotId] == null) {
            //Create the hidl service object only if needed
            mPresenceService[slotId] = new PresenceServiceWrapper(slotId);
            IImsFactory.createPresenceServiceCallback hidl_presence_cb = new IImsFactory.createPresenceServiceCallback() {
                @Override
                public void onValues(int i, IPresenceService iPresenceService) {
                    mPresenceService[slotId].setHidlPresenceService(iPresenceService);
                }
            };
            try {
                mImsFactoryInstance.createPresenceService(slotId+1, mPresenceService[slotId].getHidlPresenceListener(), hidl_presence_cb);
            } catch (RemoteException | NoSuchElementException e) {
                Log.e(LOG_TAG, "Unable to get to IPresenceService instance");
            }
        }
        return mPresenceService[slotId];
    }

    private void cleanup() {
        //Note: this will occur on HwBinderThread
        Log.d(LOG_TAG, "Native serviceDied cleanup");
        mImsFactoryInstance = null;
        for(ImsConfigServiceWrapper icsWrapper : mConfigService) {
            try {
                icsWrapper.clear();
                //icsWrapper = null;
                Log.d(LOG_TAG, " ImsConfigServiceWrapper cleanup");
            }
            catch(Exception e){
                Log.d(LOG_TAG, " ImsConfigServiceWrapper cleanup " + e.toString());
            }
        }

        for(PresenceServiceWrapper p : mPresenceService) {
            if(p == null) {
                //Presence Object is null for unsupported sub.
                continue;
            }
            try {
                p.presenceDied();
                Log.d(LOG_TAG, " presenceDied");
            }
            catch(Exception e){
                Log.d(LOG_TAG, "presenceDied caught Excpetion:");
                e.printStackTrace();
             }
        }



        for(OptionsServiceWrapper o : mOptionsService) {
            if(o == null) {
                //Options Object is null for unsupported sub.
                continue;
            }
            try {
                o.optionsDied();
                Log.d(LOG_TAG, " optionsDied");
            }
            catch(Exception e){
              Log.d(LOG_TAG, "optionsDied caught Exception:");
              e.printStackTrace();
            }
        }



        for(SipTransportServiceWrapper p : mSipTransportService) {
            try {
                p.sipTransportDied();
                Log.d(LOG_TAG, " sipTransportDied");
            }
            catch(Exception e){
                Log.d(LOG_TAG, "sipTransportDied " + e.toString());
            }
        }


        reInitHalServices();
    }

    public void reInitHalServices()
    {
        Initialize();
        for(ImsConfigServiceImpl configImpl : ImsRcsService.getImsConfigService()){
             Log.d(LOG_TAG, "configImpl call initwrapper");
             configImpl.initConfigWrapper();
        }
        for(SipTransportServiceWrapper p : mSipTransportService) {
            Log.d(LOG_TAG, "SipTransportService reinitialize");
            IImsFactory.createRcsSipTransportServiceCallback hidl_sipTransport_cb =
              new IImsFactory.createRcsSipTransportServiceCallback() {
                @Override
                public void onValues(int i,
                                     ISipTransportService iSipTransportService) {
                    p.setHidlSipTransportService(iSipTransportService);
                }
            };
            try {
                mImsFactoryInstance.createRcsSipTransportService(
                        p.getSlotId()+1,
                        p.getHidlSipTransportListener(),
                        hidl_sipTransport_cb);
            } catch (RemoteException | NoSuchElementException e) {
                Log.e(LOG_TAG, "Unable to get to ISipTransportService instance");
            }
        }
    }

    public void destroyPresenceSevice(int slotId) {
        //@TODO need to check how we can clean-up
        mPresenceService[slotId].close();
        mPresenceService[slotId] = null;
    }

    public void destroyOptionsService(int slotId) {
        //@TODO need to check how we can clean-up
        mOptionsService[slotId].close();
        mOptionsService[slotId] = null;
    }

    public OptionsServiceWrapper getImsOptionsService(int slotId) {
        if(mOptionsService[slotId] == null) {
            //Create the hidl service object only if needed
            mOptionsService[slotId] = new OptionsServiceWrapper(slotId);
            IImsFactory.createOptionsServiceCallback hidl_options_cb = new IImsFactory.createOptionsServiceCallback() {
                @Override
                public void onValues(int i, IOptionsService iOptionsService) {
                    mOptionsService[slotId].setHidlOptionsService(iOptionsService);
                }
            };
            try {
                mImsFactoryInstance.createOptionsService(slotId+1, mOptionsService[slotId].getHidlOptionsListener(), hidl_options_cb);
            } catch (RemoteException | NoSuchElementException e) {
                Log.e(LOG_TAG, "Unable to get to IOptionsService instance");
            }
        }
        return mOptionsService[slotId];
    }

    private class FactoryDeathRecipient implements IHwBinder.DeathRecipient {
        @Override
        public void serviceDied(long l) {
            ImsRcsServiceMgr.getInstance().cleanup();
        }
    }
}
