package com.mkh.mobilemall.support.http.http;


import com.mkh.mobilemall.config.Constants;
import com.xiniunet.api.DefaultXiniuClient;

import java.util.Map;


public class HttpUtility {


    private static HttpUtility httpUtility = new HttpUtility();

    private HttpUtility() {
    }

    public static HttpUtility getInstance() {
        return httpUtility;
    }

    public String executeNormalTask(HttpMethod httpMethod, String url,
                                    Map<String, String> param) throws Exception {
        return new JavaHttpUtility().executeNormalTask(httpMethod, url, param);
    }

    public static final DefaultXiniuClient client = new DefaultXiniuClient(
              Constants.API_BASE_URL, Constants.API_APP_KEY,
              Constants.API_SECRET);

}
