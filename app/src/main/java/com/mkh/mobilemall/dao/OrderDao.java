package com.mkh.mobilemall.dao;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.bean.BackResultBean;
import com.mkh.mobilemall.support.http.http.HttpUtility;
import com.mkh.mobilemall.utils.AppLogger;
import com.xiniunet.api.ApiException;

import com.xiniunet.api.domain.ecommerce.AlipayParam;
import com.xiniunet.api.domain.ecommerce.Order;
import com.xiniunet.api.domain.ecommerce.PayMethodEnum;
import com.xiniunet.api.request.ecommerce.OrderCommitRequest;
import com.xiniunet.api.request.ecommerce.OrderGetRequest;
import com.xiniunet.api.request.ecommerce.OrderPayRequest;
import com.xiniunet.api.request.ecommerce.OrderPrepayRequest;
import com.xiniunet.api.request.ecommerce.OrderReceiveRequest;
import com.xiniunet.api.request.ecommerce.OrderSmsSendRequest;
import com.xiniunet.api.request.membership.MemberBalanceGetRequest;
import com.xiniunet.api.request.membership.MemberCardRechargeRequest;
import com.xiniunet.api.response.ecommerce.OrderCommitResponse;
import com.xiniunet.api.response.ecommerce.OrderGetResponse;
import com.xiniunet.api.response.ecommerce.OrderPayResponse;
import com.xiniunet.api.response.ecommerce.OrderPrepayResponse;
import com.xiniunet.api.response.ecommerce.OrderReceiveResponse;
import com.xiniunet.api.response.ecommerce.OrderSmsSendResponse;
import com.xiniunet.api.response.membership.MemberBalanceGetResponse;
import com.xiniunet.api.response.membership.MemberCardRechargeResponse;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

/**
 * Created by xiniu_wutao on 15/7/14.
 * 订单操作 dao
 */
public class OrderDao {

    /**
     * 提交订单数据
     * @param request
     * @return
     */
    public static BackResultBean commitOrder(OrderCommitRequest request){


        BackResultBean  resultBean = new BackResultBean();
        try {
            OrderCommitResponse response = HttpUtility.getInstance().client
                      .execute(request, GlobalContext.getInstance().getSpUtil().getUserInfo());
            if(response.hasError()){
                resultBean.setCode("0");
                resultBean.setMessage(response.getErrors().get(0).getMessage());

            }else{
                resultBean.setCode("1");
                resultBean.setOrderId(response.getOrderId());
                resultBean.setOrderNumber(response.getOrderSn());

            }


        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resultBean;


    }


    public static BackResultBean getSignOrderInfoByOrderId(final Long orderId){
        AppLogger.getLogger(OrderDao.class).e("返回的orderid**********"+orderId);
        OrderPayRequest request=new OrderPayRequest();
        request.setId(orderId);
        BackResultBean backResultBean=new BackResultBean();
        OrderPayResponse response=HttpUtility.getInstance().client
                  .execute(request, GlobalContext.getInstance().getSpUtil().getUserInfo());
        if(response.hasError()){
            backResultBean.setCode("0");
            backResultBean.setMessage(response.getErrors().get(0).getMessage());

        }else{
            backResultBean.setCode("1");
            backResultBean.setKeyStr(response.getOrderString());
        }

        return backResultBean;
    }

    /**
     * 充值 加密 订单
     * @param amount
     * @param methodEnum
     * @return
     */
    public static BackResultBean  assembleRechargeSignInfo(final double amount,PayMethodEnum methodEnum){
        MemberCardRechargeRequest rechargeRequest=new MemberCardRechargeRequest();
        rechargeRequest.setRechargeAmount(amount);
        rechargeRequest.setPayMethod(methodEnum);
        BackResultBean backResultBean=new BackResultBean();
        MemberCardRechargeResponse response=HttpUtility.getInstance().client
                  .execute(rechargeRequest, GlobalContext.getInstance().getSpUtil().getUserInfo());
        if(response.hasError()){
            backResultBean.setCode("0");
            backResultBean.setMessage(response.getErrors().get(0).getMessage());

        }else {
            backResultBean.setCode("1");
            backResultBean.setKeyStr(response.getOrderString());
        }
         return backResultBean;

    }


    /**
     * 获取 支付码
     * @param id
     * @return
     */
    public static BackResultBean  getSmileCardPaySmsCode(final long id){
        OrderSmsSendRequest request=new OrderSmsSendRequest();
        request.setId(id);
        BackResultBean backResultBean=new BackResultBean();
        OrderSmsSendResponse response=HttpUtility.getInstance().client
                  .execute(request, GlobalContext.getInstance().getSpUtil().getUserInfo());
        if(response.hasError()){
            backResultBean.setCode("0");
            backResultBean.setMessage(response.getErrors().get(0).getMessage());

        }else {
            backResultBean.setCode("1");
            backResultBean.setKeyStr(response.getPayKey());
        }
        return backResultBean;

    }

    /**
     * 微笑卡 支付
     * @param id
     * @param code
     * @param paykey
     * @return
     */
    public static BackResultBean  smileCardPay(final long orderId,final String smsCode,final String payKey){
        OrderPrepayRequest request=new OrderPrepayRequest();
        request.setId(orderId);
        request.setCode(smsCode);
        request.setPayKey(payKey);
        BackResultBean backResultBean=new BackResultBean();
        OrderPrepayResponse response=HttpUtility.getInstance().client
                  .execute(request, GlobalContext.getInstance().getSpUtil().getUserInfo());
        if(response.hasError()){
            backResultBean.setCode("0");
            backResultBean.setMessage(response.getErrors().get(0).getMessage());

        }else {
            backResultBean.setCode("1");
            backResultBean.setKeyStr(response.getMsg());
        }
        return backResultBean;

    }


    /**
     *  根究orderid 获得 订单信息
     * @param orderId
     * @return
     */
    public static Order getOrderInfoByOrderId(final Long orderId){
        AppLogger.getLogger(OrderDao.class).e("返回的orderid**********"+orderId);
        OrderGetRequest request=new OrderGetRequest();
        request.setId(orderId);
        Order order=null;
        BackResultBean backResultBean=new BackResultBean();
        OrderGetResponse response=HttpUtility.getInstance().client
                  .execute(request, GlobalContext.getInstance().getSpUtil().getUserInfo());
        if(response.hasError()){
            backResultBean.setCode("0");
            backResultBean.setMessage(response.getErrors().get(0).getMessage());

        }else{
             order=response.getOrder();
        }

        return order;
    }

    /**
     * 确认收货
     * @param orderId
     * @return
     */
    public static BackResultBean  isReiceveGoods(final long orderId){
        OrderReceiveRequest request=new OrderReceiveRequest();
        request.setId(orderId);
        BackResultBean backResultBean=new BackResultBean();
        OrderReceiveResponse response=HttpUtility.getInstance().client
                  .execute(request, GlobalContext.getInstance().getSpUtil().getUserInfo());
        if(response.hasError()){
            backResultBean.setCode("0");
            backResultBean.setMessage(response.getErrors().get(0).getMessage());

        }else {
            backResultBean.setCode("1");
            backResultBean.setMessage(response.getMsg());
        }
        return backResultBean;
    }


}
