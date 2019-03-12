package com.mkh.mobilemall.support.http.http;


import com.squareup.okhttp.*;

import java.util.Map;

/**
 * @author misty-rain
 */
public class JavaHttpUtility {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    public static OkHttpClient httpClient = new OkHttpClient();

    public String executeNormalTask(HttpMethod httpMethod, String url, Map<String, String> param) throws Exception {
        switch (httpMethod) {
            case Post:
                return doPost(url, param);
            case Get:
                return doGet(url, param);
        }
        return "";
    }


    public String doPost(String urlAddress, Map<String, String> param) throws Exception {
        RequestBody body = RequestBody.create(JSON, com.alibaba.fastjson.JSON.toJSONString(param));
        Request request = new Request.Builder()
                .url(urlAddress)
                .post(body)
                .build();
        Response response = httpClient.newCall(request).execute();
        return response.body().string();

    }


    public String doGet(String urlStr, Map<String, String> param) throws Exception {
        Request request = new Request.Builder()
                .url(urlStr)
                .build();
        Response response = httpClient.newCall(request).execute();
        return response.body().string();
    }


}



