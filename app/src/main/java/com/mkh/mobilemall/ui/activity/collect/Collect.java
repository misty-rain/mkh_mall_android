package com.mkh.mobilemall.ui.activity.collect;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mkh.mobilemall.R;
import com.mkh.mobilemall.ui.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiniu_wutao on 15/7/9.
 * 收藏夹
 */
public class Collect extends BaseActivity {
    @Bind(R.id.empty_list)
    View empty;
    @Bind(R.id.list)
    RecyclerView list;
    @Bind(R.id.txtemptyMessage)
    TextView txtEmptyMessage;
    @Bind(R.id.imgicon)
    ImageView imageIcon;



    @Override
    protected int getLayoutId() {
        return R.layout.collect_list;
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.toolbar_title_pickdate;
    }

    @Override
    protected int getActionBarCustomView() {
        return R.layout.custom_toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initView();







    }

    private void initView(){

        list.setVisibility(View.GONE);
        empty.setVisibility(View.VISIBLE);

        txtEmptyMessage.setText("暂无数据");
        imageIcon.setImageResource(R.mipmap.icon_no_ticket);
    }

}
