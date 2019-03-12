package com.mkh.mobilemall.ui.activity.pickdate;

import android.os.Bundle;

import com.mkh.mobilemall.R;
import com.mkh.mobilemall.ui.base.BaseActivity;


import butterknife.ButterKnife;

/**
 * Created by xiniu_wutao on 15/7/7.
 */
public class PickDate extends BaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.pickdate;
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



    }


}
