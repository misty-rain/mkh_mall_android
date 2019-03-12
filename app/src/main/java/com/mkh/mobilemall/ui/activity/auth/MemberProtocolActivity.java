package com.mkh.mobilemall.ui.activity.auth;

import android.os.Bundle;

import com.fish.mkh.div.MkhTitleBar;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.ui.base.BaseActivity;
import com.mkh.mobilemall.ui.widget.ProgressWebView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MemberProtocolActivity extends BaseActivity {
    @Bind(R.id.webView)
    ProgressWebView webView;
    MkhTitleBar mTitleBar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_member_protocol;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initTileBar();
        initData();



    }

    public void initTileBar() {
        mTitleBar = (MkhTitleBar) findViewById(R.id.mkh_actionbar);
        mTitleBar.setTitle(getResources().getString(R.string.toolbar_title_register));
        mTitleBar.setLeftClickFinish(this);

    }

    private void initData(){
        webView.loadUrl("file:///android_asset/protocol.html");
    }
}
