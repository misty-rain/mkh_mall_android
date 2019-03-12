package com.mkh.mobilemall.config;

import com.mkh.mobilemall.utils.PropertiesUtils;

/**
 * 得到接口
 */
public interface Constants {

    public static String API_APP_KEY = PropertiesUtils.getSysProperties().getProperty("API_APP_KEY");
    public static String API_SECRET = PropertiesUtils.getSysProperties().getProperty("API_SECRET");
    public static String API_BASE_URL = PropertiesUtils.getSysProperties().getProperty("API_BASE_URL");
    public static String GET_APP_VERSION_URL = PropertiesUtils.getSysProperties().getProperty("GET_APP_VERSION_URL");
    public static String LOGIN_URL = PropertiesUtils.getSysProperties().getProperty("LOGIN_URL");
    public static String ALIPAY_PARTNER = PropertiesUtils.getSysProperties().getProperty("ALIPAY_PARTNER");
    public static String ALIPAY_SELLER_ID = PropertiesUtils.getSysProperties().getProperty("ALIPAY_SELLER_ID");
    public static String ALIPAY_RSA_PRIVATE = PropertiesUtils.getSysProperties().getProperty("ALIPAY_RSA_PRIVATE");
    public static String ALIPAY_RSA_PUBLIC = PropertiesUtils.getSysProperties().getProperty("ALIPAY_RSA_PUBLIC");


    Long SMS_WAIT_TIME = Long.parseLong(PropertiesUtils.getSysProperties().getProperty("SMS_WAIT_TIME"));

}
