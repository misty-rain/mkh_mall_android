package com.mkh.mobilemall.dao;

import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.support.http.http.HttpUtility;
import com.xiniunet.api.ApiException;
import com.xiniunet.api.domain.ecommerce.Ad;
import com.xiniunet.api.domain.master.Activities;
import com.xiniunet.api.domain.membership.Voucher;
import com.xiniunet.api.request.ecommerce.AdFindRequest;
import com.xiniunet.api.request.master.ActivitiesFindRequest;
import com.xiniunet.api.request.membership.VouchersFindRequest;
import com.xiniunet.api.response.ecommerce.AdFindResponse;
import com.xiniunet.api.response.master.ActivitiesFindResponse;
import com.xiniunet.api.response.membership.VouchersFindResponse;

import java.util.List;

/**
 * Created by zwd on 15/7/15.
 *
 */
public class ActivitiesDao {

    /**
     *  得到活动数据
     * @param pageSize
     * @return
     */
    public static List<Activities> getActivities(int pageSize){
        ActivitiesFindRequest activitiesFindRequest = new ActivitiesFindRequest();
        activitiesFindRequest.setStoreId(GlobalContext.getInstance().getSpUtil().getStoreId());
        List<Activities> activitiesList=null;
         try {
            ActivitiesFindResponse response = HttpUtility.getInstance().client
                    .execute(activitiesFindRequest, GlobalContext.getInstance().getSpUtil().getUserInfo());
            activitiesList=response.getResult();

        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return activitiesList;
    }

    /**
     *  得到广告位
     * @return
     */
    public static List<Ad> getAds(){
        AdFindRequest activitiesFindRequest = new AdFindRequest();
        activitiesFindRequest.setStoreId(GlobalContext.getInstance().getSpUtil().getStoreId());
        List<Ad> adsList=null;
        try {
            AdFindResponse response = HttpUtility.getInstance().client
                      .execute(activitiesFindRequest, GlobalContext.getInstance().getSpUtil().getUserInfo());
            adsList=response.getResult();


        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return adsList;
    }
}
