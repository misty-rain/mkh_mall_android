package com.mkh.mobilemall.dao;

import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.support.http.http.HttpUtility;
import com.xiniunet.api.ApiException;
import com.xiniunet.api.domain.master.ItemCategory;
import com.xiniunet.api.domain.master.Location;
import com.xiniunet.api.domain.master.Store;
import com.xiniunet.api.request.master.CategoryAllListGetRequest;
import com.xiniunet.api.request.master.LocationGetAllListRequest;
import com.xiniunet.api.request.master.LocationGetRequest;
import com.xiniunet.api.request.master.StoreListGetByLocationRequest;
import com.xiniunet.api.response.master.CategoryAllListGetResponse;
import com.xiniunet.api.response.master.LocationGetAllListResponse;
import com.xiniunet.api.response.master.StoreListGetByLocationResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zwd on 15/7/14.
 *    位置 数据访问
 */
public class LocationDao {


    public static List<Store> getNearStore(final double latitude,final double longitude) {

        StoreListGetByLocationRequest request = new StoreListGetByLocationRequest();
        request.setLatitude(latitude);
        request.setLongitude(longitude);
        List<Store> list = null;
        try {
            StoreListGetByLocationResponse response = HttpUtility.getInstance().client
                    .execute(request, GlobalContext.getInstance().getSpUtil().getUserInfo());
            list=response.getResult();

        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;

    }

    /**
     * 获得所有地址
     * @return
     */
    public static List<Location> getAddressList() {

        LocationGetAllListRequest request = new LocationGetAllListRequest();

        List<Location> list = null;
        try {
            LocationGetAllListResponse response = HttpUtility.getInstance().client
                      .execute(request, GlobalContext.getInstance().getSpUtil().getUserInfo());
            list=response.getResult();

        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }




}
