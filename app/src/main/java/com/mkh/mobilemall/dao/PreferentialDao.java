package com.mkh.mobilemall.dao;

import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.support.http.http.HttpUtility;
import com.xiniunet.api.ApiException;
import com.xiniunet.api.domain.membership.Voucher;
import com.xiniunet.api.request.membership.VouchersFindRequest;
import com.xiniunet.api.response.master.CategoryAllListGetResponse;
import com.xiniunet.api.response.membership.VouchersFindResponse;

import java.util.List;

/**
 * Created by xiniu_wutao on 15/7/14.
 * 优惠劵dao
 */

public class PreferentialDao {
    /**
     * 获得符合条件的抵用券
     * @return
     */
    public static List<Voucher> getVoucher(final List<Long> idList){
        VouchersFindRequest vouchersFindRequest = new VouchersFindRequest();
        vouchersFindRequest.setCartItemIdList(idList);
        vouchersFindRequest.setPageSize(0);
        List<Voucher> voucherList=null;
        try {
            VouchersFindResponse response = HttpUtility.getInstance().client
                      .execute(vouchersFindRequest, GlobalContext.getInstance().getSpUtil().getUserInfo());
            voucherList=response.getResult();


        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return voucherList;




    }
}
