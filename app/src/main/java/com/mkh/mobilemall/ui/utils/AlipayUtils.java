package com.mkh.mobilemall.ui.utils;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.config.Constants;
import com.mkh.mobilemall.support.alipay.PayResult;
import com.mkh.mobilemall.utils.SignUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by xiniu_wutao on 15/7/20.
 */
public class AlipayUtils {




    public static String assembleOrderInfoForAlipay(final String subject, final String body, final String price) {
        // 签约合作者身份ID
        StringBuilder stringBuilder = new StringBuilder("partner=" + "\"" + Constants.ALIPAY_PARTNER + "\"");
        // 签约卖家支付宝账号
        stringBuilder.append("&seller_id=" + "\"" + Constants.ALIPAY_SELLER_ID + "\"");
        // 商户网站唯一订单号
        stringBuilder.append("&out_trade_no=" + "\"" + getOutTradeNo() + "\"");
        // 商品名称
        stringBuilder.append("&subject=" + "\"" + subject + "\"");
        // 商品详情
        stringBuilder.append("&body=" + "\"" + body + "\"");
        // 商品金额
        stringBuilder.append("&total_fee=" + "\"" + price + "\"");
        // 服务器异步通知页面路径
        stringBuilder.append("&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\"");
        // 服务接口名称， 固定值
        stringBuilder.append("&service=\"mobile.securitypay.pay\"");
        // 支付类型， 固定值
        stringBuilder.append("&payment_type=\"1\"");
        // 参数编码， 固定值
        stringBuilder.append("&_input_charset=\"utf-8\"");

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        stringBuilder.append("&it_b_pay=\"30m\"");
// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        stringBuilder.append("&return_url=\"m.alipay.com\"");

// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return stringBuilder.toString();


    }

    /**
     * 生成系统订单号
     *
     * @return
     */

    public static String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
                  Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content
     *            待签名订单信息
     */
    public static  String sign(String content) {
        return SignUtils.sign(content, Constants.ALIPAY_RSA_PRIVATE);

    }

    /**
     * get the sign type we use. 获取签名方式
     *
     */
    public static String getSignType() {
        return "sign_type=\"RSA\"";
    }



}
