package com.mkh.mobilemall.ui.utils;

import com.mkh.mobilemall.bean.PosItemBean;
import com.mkh.mobilemall.bean.TempComCarBean;
import com.mkh.mobilemall.support.db.TempComCarDBTask;
import com.mkh.mobilemall.utils.DateUtil;
import com.xiniunet.api.domain.master.Item;

import java.math.BigDecimal;

/**
 * Created by xiniu_wutao on 15/7/2.
 * 购物车相关运算类
 */
public class ShoppingCartUtils {

    /**
     * 保存 或 更新 购物车数据
     */
    public static void saveOrUpdateCartData(Item item, int count,final long cartItemId) {
        TempComCarBean cb = new TempComCarBean();
        cb.setCommodName(item.getName());
        cb.setId(String.valueOf(item.getId()));
        cb.setCartItemId(cartItemId);
        cb.setCommodTotalCount(count);
        cb.setSingleCommodPrice(item.getCurrentPrice());
        cb.setSingleCommodTotalCount(count);
        cb.setNumber(item.getNumber());
        cb.setShortName(item.getShortName());
        cb.setSingleComPicUrl(item.getPictureUrl());

        BigDecimal bdCount = new BigDecimal(cb.getCommodTotalCount());
        BigDecimal bdTotal = new BigDecimal(item.getCurrentPrice());
        cb.setCommodTotalPrice(bdCount.multiply(bdTotal).doubleValue());
        cb.setOperationTime(DateUtil.getCurrentTime());
        TempComCarDBTask.getInstance().addOrUpdateItemToTempCar(cb);
    }

}
