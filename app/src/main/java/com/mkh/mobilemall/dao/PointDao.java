package com.mkh.mobilemall.dao;

import android.app.DownloadManager;

import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.support.http.http.HttpUtility;
import com.xiniunet.api.ApiException;
import com.xiniunet.api.request.membership.MemberBalanceGetRequest;
import com.xiniunet.api.request.membership.MemberPointGetRequest;
import com.xiniunet.api.response.master.CategoryAllListGetResponse;
import com.xiniunet.api.response.membership.MemberBalanceGetResponse;
import com.xiniunet.api.response.membership.MemberPointGetResponse;

/**
 * Created by xiniu_wutao on 15/7/14.
 * 积分dao
 */
public class PointDao {

    /**
     * 获得会员积分
     *
     * @return
     */
    public  static double getUserPoint(){
        MemberPointGetRequest request=new MemberPointGetRequest();
        double point = 0;
        try {
            MemberPointGetResponse response = HttpUtility.getInstance().client
                      .execute(request, GlobalContext.getInstance().getSpUtil().getUserInfo());
            if(response!=null) {
                if (response.getPoint() != null) {
                    point = response.getPoint();
                }
            }
        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return point;


    }

    /**
     * 获得微笑卡余额
     * @return
     */
    public  static double getSmileCardBalance(){
        MemberBalanceGetRequest request=new MemberBalanceGetRequest();
        double banlance = 0;
        try {
            MemberBalanceGetResponse response = HttpUtility.getInstance().client
                      .execute(request, GlobalContext.getInstance().getSpUtil().getUserInfo());
            if(response!=null)
                if(response.getBalance()!=null)
            banlance=response.getBalance();
        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return banlance;


    }


}
