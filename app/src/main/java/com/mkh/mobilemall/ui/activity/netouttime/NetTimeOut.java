package com.mkh.mobilemall.ui.activity.netouttime;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mkh.mobilemall.R;
import com.mkh.mobilemall.ui.utils.ShowToastUtils;

/**
 * Created by xiniu on 15/8/6.
 */
public class NetTimeOut extends Activity {
    private Button reload;
    private ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.net_out_time);
        reload= (Button) findViewById(R.id.reload);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                if(info != null && info.isAvailable()) {
                    finish();
                }else{
                    ShowToastUtils.showToast("请检查网络！",NetTimeOut.this);
                }
            }
        });

    }

}

