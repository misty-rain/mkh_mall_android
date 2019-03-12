package com.mkh.mobilemall.ui.activity;

import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.support.http.http.HttpUtility;
import com.xiniunet.api.request.ecommerce.FavoriteItemFindRequest;
import com.xiniunet.api.response.ecommerce.FavoriteItemFindResponse;

/**
 * Created by zwd on 15/7/16.
 */
public class GetFavoriterID {

    public static long GetFavorteID(Long storeId,Long itemId){

        long favoriteId=0;
        FavoriteItemFindRequest favoriteItemFindRequest = new FavoriteItemFindRequest();
        favoriteItemFindRequest.setStoreId(storeId);
        favoriteItemFindRequest.setItemId(itemId);
        FavoriteItemFindResponse favoriteItemFindResponse = HttpUtility.client.execute(favoriteItemFindRequest, GlobalContext.getInstance().getSpUtil().getUserInfo());
        if(favoriteItemFindResponse.getTotalCount() > 0) {
            favoriteId = favoriteItemFindResponse.getResult().get(0).getId();
        }

     return favoriteId;
    }

}
