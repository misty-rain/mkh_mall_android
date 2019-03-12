package com.mkh.mobilemall.ui.utils;

import com.xiniunet.api.domain.master.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiniu_wutao on 15/7/29.
 */


public class AddressUtils {

    /**
     * 得到默认收货地址
     * @param list
     * @return
     */
    public static List<String> getDefaultAddress(List<Location> list) {
        List<String> addressList = new ArrayList<String>();
        if (list != null || list.size() > 0) {
            for (Location location : list) {
                if (location.getIsDefault()) {
                    addressList.add(location.getContactName());
                    addressList.add(location.getContactPhone());
                    addressList.add(location.getAreaInfo()+location.getAddress());
                    addressList.add(String.valueOf(location.getId()));

                }

            }
        }
        return addressList;
    }
}
