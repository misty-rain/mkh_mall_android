package com.mkh.mobilemall.ui.utils;

import com.mkh.mobilemall.bean.TempComCarBean;
import com.mkh.mobilemall.config.SysConstant;
import com.mkh.mobilemall.ui.activity.auth.LoginActivity;
import com.mkh.mobilemall.utils.DateUtil;
import com.xiniunet.api.domain.ecommerce.DeliveryModeEnum;

import com.xiniunet.api.domain.ecommerce.OrderTypeEnum;
import com.xiniunet.api.domain.ecommerce.PayMethodEnum;
import com.xiniunet.api.request.ecommerce.OrderCommitRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiniu_wutao on 15/7/14.
 * 订单数据组装 帮助类
 */
public class OrderUtils {

    /**
     * 组装订单数据
     * @return
     */
    public static OrderCommitRequest assembleOrderData(Long storeId,OrderTypeEnum orderTypeEnum,double totalAmount,PayMethodEnum payMethodEnum,Integer pointAmount,List<Long> voucherList,Integer totalCount,Long locationId,List<Integer> hourList,DeliveryModeEnum deliveryModeEnum,String summary,List<Long> cardList){
        OrderCommitRequest orderCommitRequest=new OrderCommitRequest();
        //orderCommitRequest.setId(Long.parseLong(DateUtil.getOrderNo()));
        orderCommitRequest.setStoreId(storeId);
        orderCommitRequest.setType(orderTypeEnum);
        orderCommitRequest.setTotalAmount(totalAmount);
        orderCommitRequest.setPayMethod(payMethodEnum);
        orderCommitRequest.setPointAmount(pointAmount);
        orderCommitRequest.setVoucherIdList(voucherList);
        orderCommitRequest.setTotalCount(totalCount);
        orderCommitRequest.setLocationId(locationId);
        orderCommitRequest.setHourList(hourList);
        orderCommitRequest.setDeliveryMode(deliveryModeEnum);
        orderCommitRequest.setSummary(summary);

        orderCommitRequest.setCartIds(cardList);
        return orderCommitRequest;
    }


    /**
     * 组装订单商品数据
     * @param list
     * @return
     */
   /* public static List<OrderLineCreateBean> assembleOrderLineData(List<TempComCarBean> list){
        List<OrderLineCreateBean> orderLineCreateBeanList=new ArrayList<OrderLineCreateBean>();
        OrderLineCreateBean orderLineCreateBean=null;

        for(TempComCarBean cartBean: list) {
            orderLineCreateBean=new OrderLineCreateBean();
            orderLineCreateBean.setItemId(Long.parseLong(cartBean.getId()));
            orderLineCreateBean.setQuantity(Double.parseDouble(cartBean.getCommodTotalCount().toString()));
            orderLineCreateBeanList.add(orderLineCreateBean);



        }
        return orderLineCreateBeanList;

    }
    */

    /**
     * 得到支付名称
     * @param payMethodEnum
     * @return
     */
    public static String getPayName(PayMethodEnum payMethodEnum){
        String name = null;
        switch (payMethodEnum){
            case SMILE_CARD:
                name= SysConstant.PAYNAME_SMILECARD;
                break;
            case ALIPAY:
                name=SysConstant.PAYNAME_ALIPAY;
                break;
            case WECHAT:
                name=SysConstant.PAYNAME_WECHAT;
                break;
        }
        return name;
    }
}
