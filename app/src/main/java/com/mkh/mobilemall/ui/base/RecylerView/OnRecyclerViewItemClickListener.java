package com.mkh.mobilemall.ui.base.RecylerView;

import android.view.View;

import com.mkh.mobilemall.bean.PosItemBean;
import com.xiniunet.api.domain.master.Item;

/**
 * Created by xiniu_wutao on 15/7/8.
 * recyclerView 自定义 item点击事件
 */
public interface OnRecyclerViewItemClickListener {
    void onItemClick(View view , Item data);
}
