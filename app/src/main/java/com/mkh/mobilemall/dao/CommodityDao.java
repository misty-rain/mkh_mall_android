package com.mkh.mobilemall.dao;

import com.alibaba.fastjson.JSON;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.bean.BackResultBean;
import com.mkh.mobilemall.support.http.http.HttpUtility;
import com.xiniunet.api.ApiException;
import com.xiniunet.api.domain.ecommerce.CartItem;
import com.xiniunet.api.domain.master.Item;
import com.xiniunet.api.request.ecommerce.CartItemCreateOrUpdateRequest;
import com.xiniunet.api.request.ecommerce.CartItemDeleteRequest;
import com.xiniunet.api.request.ecommerce.CartItemFindRequest;
import com.xiniunet.api.request.ecommerce.CartItemUpdateRequest;
import com.xiniunet.api.request.ecommerce.FavoriteItemCreateRequest;
import com.xiniunet.api.request.ecommerce.FavoriteItemDeleteRequest;
import com.xiniunet.api.request.ecommerce.FavoriteItemFindRequest;
import com.xiniunet.api.request.master.ItemSearchRequest;
import com.xiniunet.api.response.ecommerce.CartItemCreateOrUpdateResponse;
import com.xiniunet.api.response.ecommerce.CartItemDeleteResponse;
import com.xiniunet.api.response.ecommerce.CartItemFindResponse;
import com.xiniunet.api.response.ecommerce.CartItemUpdateResponse;
import com.xiniunet.api.response.ecommerce.FavoriteItemCreateResponse;
import com.xiniunet.api.response.ecommerce.FavoriteItemDeleteResponse;
import com.xiniunet.api.response.ecommerce.FavoriteItemFindResponse;
import com.xiniunet.api.response.master.ItemSearchResponse;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xiniu_wutao on 15/7/9.
 * 商品 dao
 */
public class CommodityDao {
    /**
     * 根据 物料类别Id 获取 商品集合
     * @param categoryId
     * @return
     */
    public static List<Item> getItemByCategoryId(final long categoryId,final String keyWord) {
        ItemSearchRequest request = new ItemSearchRequest();


        if(categoryId!=0L)
        request.setCategoryId(categoryId);
        if(keyWord!=null&&!keyWord.equals(""))
        request.setKeyword(keyWord);

        request.setStoreId(GlobalContext.getInstance().getSpUtil().getStoreId());

        List<Item> list = null;
        try {
            ItemSearchResponse response = HttpUtility.getInstance().client
                      .execute(request, GlobalContext.getInstance().getSpUtil().getUserInfo());
            list = response.getResult();

        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;

    }


    public static ItemSearchResponse getItemByCategoryIdForBackRes(final long categoryId,final String keyWord) {
        ItemSearchRequest request = new ItemSearchRequest();


        if(categoryId!=0L)
            request.setCategoryId(categoryId);
        if(keyWord!=null&&!keyWord.equals(""))
            request.setKeyword(keyWord);

        request.setStoreId(GlobalContext.getInstance().getSpUtil().getStoreId());
        ItemSearchResponse response=null;
        try {
             response = HttpUtility.getInstance().client
                      .execute(request, GlobalContext.getInstance().getSpUtil().getUserInfo());

        } catch (ApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return response;

    }

    /**
     * 查询收藏商品列表
     * @return  收藏列表
     */
    public static FavoriteItemFindResponse findFavoriteItem(final int pageNum) {
        FavoriteItemFindRequest request = new FavoriteItemFindRequest();
        request.setStoreId(GlobalContext.getInstance().getSpUtil().getStoreId());
        request.setPageSize(10);
        request.setPageNumber(pageNum);
        FavoriteItemFindResponse response=null;
        try {
             response = HttpUtility.client.execute(request, GlobalContext.getInstance().getSpUtil().getUserInfo());
        } catch (ApiException e) {
            e.printStackTrace();
        }

        return response;
    }

    /**
     * 添加or修改 商品Item
     * @param storeId
     * @return
     */
    public static BackResultBean addOrUpdateCartItem(final long itemId,final double quantity) {

        CartItemCreateOrUpdateRequest request = new CartItemCreateOrUpdateRequest();
        request.setStoreId(GlobalContext.getInstance().getSpUtil().getStoreId());
        request.setItemId(itemId);
        request.setQuantity(quantity);

        BackResultBean  resultBean = new BackResultBean();
        try {
            CartItemCreateOrUpdateResponse response = HttpUtility.client.execute(request, GlobalContext.getInstance().getSpUtil().getUserInfo());


            if (!response.hasError()) {
                    resultBean = new BackResultBean();
                    resultBean.setCode("1");
                    resultBean.setOrderId(response.getId());

            }else {

                resultBean.setCode("0");
                resultBean.setMessage(response.getErrors().get(0).getMessage());
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return resultBean;
    }

    /**
     * 修改 商品数量
     * @param itemId
     * @param quantity
     * @return
     */
    public static BackResultBean updateCartItem(final long itemId,final double quantity) {

        CartItemUpdateRequest request = new CartItemUpdateRequest();
        request.setId(itemId);
        request.setQuantity(quantity);

        BackResultBean  resultBean = new BackResultBean();
        try {
            CartItemUpdateResponse response = HttpUtility.client.execute(request, GlobalContext.getInstance().getSpUtil().getUserInfo());


            if (!response.hasError()) {
                resultBean = new BackResultBean();
                resultBean.setCode("1");
            }else {

                resultBean.setCode("0");
                resultBean.setMessage(response.getErrors().get(0).getMessage());
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return resultBean;
    }


    /**
     * 拉取购物车数据
     * @return
     */
    public static List<CartItem> getCartItemList() {
        CartItemFindRequest request = new CartItemFindRequest();
        request.setStoreId(GlobalContext.getInstance().getSpUtil().getStoreId());
        List<CartItem> cartItemsList = null;
        try {
            CartItemFindResponse response = HttpUtility.client.execute(request, GlobalContext.getInstance().getSpUtil().getUserInfo());
            cartItemsList = response.getResult();

        } catch (ApiException e) {
            e.printStackTrace();
        }
        return cartItemsList;
    }

    /**
     * 删除购物车数据
     * @param cartItemId
     * @return
     */
    public static BackResultBean removeCartItemOrClear(final long cartItemId) {
        CartItemDeleteRequest request = new CartItemDeleteRequest();
        request.setId(cartItemId);

        BackResultBean  resultBean = new BackResultBean();
        try {
            CartItemDeleteResponse response = HttpUtility.client.execute(request, GlobalContext.getInstance().getSpUtil().getUserInfo());
            if(!response.hasError()){
                    resultBean = new BackResultBean();
                    resultBean.setCode("1");
                }else{
                resultBean.setCode("0");
                resultBean.setMessage(response.getErrors().get(0).getMessage());


                }

        } catch (ApiException e) {
            e.printStackTrace();
        }
        return resultBean;
    }

    /**
     * 收藏商品
     * @param itemId
     * @return
     */
    public static  BackResultBean collectItem(long itemId){
        FavoriteItemCreateRequest request = new FavoriteItemCreateRequest();
        request.setItemId(itemId);
        request.setStoreId(GlobalContext.getInstance().getSpUtil().getStoreId());
        BackResultBean resultBean=new BackResultBean();
        FavoriteItemCreateResponse response = HttpUtility.client.execute(request, GlobalContext.getInstance().getSpUtil().getUserInfo());
        if(response.hasError()){
            resultBean.setCode("0");
            resultBean.setMessage(response.getErrors().get(0).getMessage());
        }else{
            resultBean.setCode("1");


        }

        return resultBean;

    }

    /**
     *  取消收藏 商品
     * @param itemId
     * @return
     */
    public static  BackResultBean cancleCollectItem(long itemId){
        FavoriteItemDeleteRequest request = new FavoriteItemDeleteRequest();
        request.setItemId(itemId);
        BackResultBean resultBean=new BackResultBean();
        FavoriteItemDeleteResponse response = HttpUtility.client.execute(request, GlobalContext.getInstance().getSpUtil().getUserInfo());
        if(response.hasError()){
            resultBean.setCode("0");
            resultBean.setMessage(response.getErrors().get(0).getMessage());
        }else{
            resultBean.setCode("1");


        }

        return resultBean;

    }



    
}   
