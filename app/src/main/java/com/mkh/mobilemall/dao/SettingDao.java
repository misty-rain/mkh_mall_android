package com.mkh.mobilemall.dao;

import com.alibaba.fastjson.JSON;
import com.mkh.mobilemall.bean.UpdateBean;
import com.mkh.mobilemall.config.Constants;
import com.mkh.mobilemall.support.http.http.HttpMethod;
import com.mkh.mobilemall.support.http.http.HttpUtility;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiniu_wutao on 15/6/30.
 */
public class SettingDao {

    /**
     * 获得最新版本号
     *
     * @return
     */

    public static UpdateBean getAppVersion() {
        Map<String, String> paramsMap = new HashMap<String, String>();
        UpdateBean update = null;
        try {
            String jsonString = HttpUtility.getInstance().executeNormalTask(
                    HttpMethod.Get, Constants.GET_APP_VERSION_URL, paramsMap);
            update = JSON.parseObject(jsonString, UpdateBean.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return update;
    }
}
