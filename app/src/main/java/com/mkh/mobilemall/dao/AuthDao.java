package com.mkh.mobilemall.dao;

import com.xiniunet.api.DefaultXiniuClient;
import com.xiniunet.api.XiniuClient;

/**
 * Created on 2015-06-30.
 *
 * @author 吕浩
 * @since 1.0.0
 */
public class AuthDao {
    public static XiniuClient client = new DefaultXiniuClient("http://58.210.99.99:8080/router", "0617CA8376F9901F28FF46B69BF9CF47", "28570C9D069ED51226DD9F028BD5E6DC");
//    public static XiniuClient client = new DefaultXiniuClient("http://192.168.1.54/router", "0617CA8376F9901F28FF46B69BF9CF47", "28570C9D069ED51226DD9F028BD5E6DC");
}
