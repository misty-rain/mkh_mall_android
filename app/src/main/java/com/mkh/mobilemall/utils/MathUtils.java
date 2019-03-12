package com.mkh.mobilemall.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by Administrator on 2015/1/21.
 * 数学运算帮助类
 */
public class MathUtils {

    /**
     * 将 doule 值精确到两位
     * @param value
     * @return
     */
   public static double convertTwoBit(double   value){
        long   l1   =   Math.round(value*100);   //四舍五入
        double   ret   =   l1/100.0;               //注意：使用   100.0   而不是   100
        return   ret;
    }

    public static double formatData(double value){
        DecimalFormat df = new DecimalFormat("##.00");
        return Double.parseDouble(df.format(value));

    }

    public static BigDecimal formatDataForBackBigDecimal(double value){
        DecimalFormat df = new DecimalFormat("##.00");
        return new BigDecimal(df.format(value));

    }

    public static String formatDataForBackString(double value){
        DecimalFormat df = new DecimalFormat("##.00");
        return df.format(value).toString();

    }

    public static double getTotalAmount(double count,double price){
        DecimalFormat df = new DecimalFormat("##.00");
        return count*price;

    }

}
