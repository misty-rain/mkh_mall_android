package com.mkh.mobilemall.dao;

import android.content.ClipData;

import com.alibaba.fastjson.JSON;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.bean.CategoryBean;
import com.mkh.mobilemall.support.db.CategoryDBTask;
import com.mkh.mobilemall.support.http.http.HttpUtility;
import com.mkh.mobilemall.ui.activity.map.Location;
import com.mkh.mobilemall.utils.AppLogger;
import com.mkh.mobilemall.utils.Utility;
import com.xiniunet.api.ApiException;
import com.xiniunet.api.domain.master.ItemCategory;
import com.xiniunet.api.request.master.CategoryAllListGetRequest;
import com.xiniunet.api.response.master.CategoryAllListGetResponse;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiniu_wutao on 15/7/9.
 *
 * 商品类别dao
 */
public class CategoryDao {

    //获得所有零售主条目
    public static Map<String,List<ItemCategory>> getCategory() {
        CategoryAllListGetRequest request = new CategoryAllListGetRequest();
        request.setStoreId(GlobalContext.getInstance().getSpUtil().getStoreId());

        Map<String,List<ItemCategory>> cateMap = new HashMap<String,List<ItemCategory>>();
        List<ItemCategory> cateList=null;
        CategoryBean categoryBean=null;

        try {
            //if(CategoryDBTask.getInstance().getParentCategoryList().size()==0) {
                CategoryAllListGetResponse response = HttpUtility.getInstance().client
                          .execute(request, GlobalContext.getInstance().getSpUtil().getUserInfo());
                cateList = response.getResult();

                if (cateList != null) {
                    //for (ItemCategory itemCategory : cateList) {
                        //CategoryDBTask.getInstance().addOrUpdateCategory(itemCategory);
                    //}
                    CategoryDBTask.getInstance().removeAllCategory();
                    CategoryDBTask.getInstance().addOrUpdateCategoryForFast(cateList);

                    cateMap.put("group", CategoryDBTask.getInstance().getParentCategoryList());
                    for (int i = 0; i < cateMap.get("group").size(); i++) {
                        String paentId = String.valueOf(cateMap.get("group").get(i).getParentId());
                        cateMap.put(String.valueOf(i), CategoryDBTask.getInstance().getCategoryListByParentId(cateMap.get("group").get(i).getId()));
                    }
                }
           // }else{
            //    cateMap.put("group", CategoryDBTask.getInstance().getParentCategoryList());
            //    for (int i = 0; i < cateMap.get("group").size(); i++) {
             //       String paentId = String.valueOf(cateMap.get("group").get(i).getParentId());
             //       cateMap.put(String.valueOf(i), CategoryDBTask.getInstance().getCategoryListByParentId(cateMap.get("group").get(i).getId()));
              //  }
        //    }

        } catch (ApiException e) {
            // TODO Auto-generated catch block
        }
        return cateMap;

    }


}
