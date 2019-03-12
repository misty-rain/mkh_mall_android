package com.mkh.mobilemall.dao;


import android.util.Log;

import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.support.http.http.HttpUtility;
import com.mkh.mobilemall.utils.AppLogger;
import com.xiniunet.api.domain.master.Store;
import com.xiniunet.api.request.master.StoreGetAllListRequest;
import com.xiniunet.api.response.master.StoreGetAllListResponse;

import java.util.List;

/**
 * Created by Administrator on 2015/7/15.
 * 门点 dao
 */
public class StoreDao {

    public static List<Store> getStoreList() {
        StoreGetAllListRequest request = new StoreGetAllListRequest();
        List<Store> list = null;
        try {
            StoreGetAllListResponse response = HttpUtility.getInstance().client
                    .execute(request, GlobalContext.getInstance().getSpUtil().getUserInfo());
            if(response!=null)
            list = response.getResult();
        }catch (Exception e){
            Log.e("mkh",e.getMessage());
        }
        return list;

    }

}
