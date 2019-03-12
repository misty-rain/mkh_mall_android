package com.mkh.mobilemall.utils;

import com.mkh.mobilemall.app.GlobalContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertiesUtils {

    public static Properties getSysProperties() {

        Properties properties = new Properties();
        try {
            int id = GlobalContext.getInstance().getResources().getIdentifier("sysconfig", "raw", GlobalContext.getInstance().getPackageName());
            properties.load(GlobalContext.getInstance().getResources().openRawResource(id));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return properties;
    }


    public static Properties getURLProperties() {

        Properties p = new Properties();

        try {
            InputStream inputStream = PropertiesUtils.class.getClassLoader().getResourceAsStream(
                    "resources/URLHelper.properties");

            p.load(inputStream);

        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return p;
    }


}
