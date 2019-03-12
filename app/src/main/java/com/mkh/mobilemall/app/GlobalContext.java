package com.mkh.mobilemall.app;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import com.baidu.mapapi.SDKInitializer;
import com.mkh.mobilemall.broadcast.NetCheckReceiver;
import com.mkh.mobilemall.broadcast.NetworkStateService;
import com.mkh.mobilemall.utils.AppLogger;
import com.mkh.mobilemall.utils.ImageLoaderUtil;
import com.mkh.mobilemall.utils.SharePreferenceUtil;
import com.xiniunet.api.domain.master.Item;

import im.fir.sdk.FIR;


/**
 * Created by xiniu_wutao on 15/6/30.
 */
public class GlobalContext extends Application {

    //singleton
    private static GlobalContext globalContext = null;
    private SharedPreferences sharedPref = null;

    private AppLogger logger = AppLogger.getLogger(GlobalContext.class);
    public static final String SP_FILE_NAME = "mkh_mobilemall";
    private SharePreferenceUtil mSpUtil;



    /**
     * @param args
     */
    public static void main(String[] args) {
    }

    public static GlobalContext getInstance() {
        return globalContext;
    }

    @Override
    public void onCreate() {
        FIR.init(this);
        super.onCreate();
        SDKInitializer.initialize(this);
        //ImageLoaderUtil.initImageLoaderConfig(this);
        globalContext = this;
        logger.i("Application starts");
        ImageLoaderUtil.initImageLoaderConfig(getApplicationContext());

        //Intent i=new Intent(this,NetworkStateService.class);
        //startService(i);

    }


    public synchronized SharePreferenceUtil getSpUtil() {
        if (mSpUtil == null)
            mSpUtil = new SharePreferenceUtil(this, SP_FILE_NAME);
        return mSpUtil;
    }


}
