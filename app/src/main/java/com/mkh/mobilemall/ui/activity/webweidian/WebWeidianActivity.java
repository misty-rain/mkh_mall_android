package com.mkh.mobilemall.ui.activity.webweidian;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fish.mkh.div.MkhTitleBar;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.ui.base.BaseActivity;
import com.mkh.mobilemall.ui.widget.ProgressWebView;

/**
 * Created by xiniu_wutao on 15/8/4.
 */
public class WebWeidianActivity extends BaseActivity {

    MkhTitleBar mTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weidian);
        initTileBar();
        ProgressWebView webView = (ProgressWebView)findViewById(R.id.webview);
        webView.setWebViewClient(new myWebClient());
        webView.getSettings().setJavaScriptEnabled(true);
        Intent i=getIntent();
        String url=i.getStringExtra("url");
        webView.loadUrl(url);
    }

    public class myWebClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub

            view.loadUrl(url);
            return true;

        }



    }

    public void initTileBar() {
        mTitleBar = (MkhTitleBar) findViewById(R.id.mkh_actionbar);
        mTitleBar.setTitle(getString(R.string.toolbar_title_webweidian));
        mTitleBar.setLeftClickFinish(this);
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

}
