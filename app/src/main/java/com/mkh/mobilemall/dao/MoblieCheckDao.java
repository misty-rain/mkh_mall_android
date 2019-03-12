package com.mkh.mobilemall.dao;

import android.util.Log;

import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.support.http.http.HttpUtility;
import com.xiniunet.api.domain.master.Store;
import com.xiniunet.api.request.master.StoreGetAllListRequest;
import com.xiniunet.api.request.system.MobileCheckRequest;
import com.xiniunet.api.response.master.StoreGetAllListResponse;
import com.xiniunet.api.response.system.MobileCheckResponse;

import java.util.List;

/**
 * Created by xiniu on 15/8/7.
 */
public class MoblieCheckDao {
    public static Long getCheckMobile(String str) {
        MobileCheckRequest request = new MobileCheckRequest();

        request.setAccount(str);
        Long list=null;
        try {
            MobileCheckResponse response = HttpUtility.getInstance().client
                    .execute(request, GlobalContext.getInstance().getSpUtil().getUserInfo());
            if (response != null)
                if(response.getResult()!=null)
                list = response.getResult();
        } catch (Exception e) {

            Log.e("mkh", e.getMessage());
        }
        return list;
    }

}


