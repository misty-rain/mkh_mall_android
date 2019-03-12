package com.mkh.mobilemall.utils;

import com.mkh.mobilemall.support.T;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiniu_wutao on 15/7/14.
 * 集合操作类
 */
public class CollectionUtils {


    public static  List<T> map(List<T> input) {
        ArrayList<T> result = new ArrayList<T>();
        for (T obj : input) {
            result.add(obj);
        }
        return result;
    }
}
