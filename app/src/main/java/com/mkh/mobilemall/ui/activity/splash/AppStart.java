package com.mkh.mobilemall.ui.activity.splash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.fish.mkh.util.UIUtil;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.ui.activity.guide.GuideActivity;
import com.mkh.mobilemall.ui.activity.main.MainActivity;
import com.mkh.mobilemall.ui.activity.map.Location;
import com.mkh.mobilemall.ui.activity.store.StoreList;
import com.mkh.mobilemall.ui.utils.ShowToastUtils;
import com.mkh.mobilemall.utils.SharePreferenceUtil;
import com.mkh.mobilemall.utils.Utility;

import java.util.Timer;
import java.util.TimerTask;

public class
        AppStart extends Activity {
    boolean isFirstIn = false;

    private static final int GO_HOME = 1000;
    private static final int GO_GUIDE = 1001;
    // 延迟3秒
    private static final long SPLASH_DELAY_MILLIS = 10;

    private static final String SHAREDPREFERENCES_NAME = "first_pref";
    private LinearLayout ll_net;
    private Button reload_activity;
    /**
     * Handler:跳转到不同界面
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME:
                    if (Utility.isConnected(GlobalContext.getInstance())) {
                        if(GlobalContext.getInstance().getSpUtil().getStoreId()!=0L) {

                            Intent intent = new Intent(AppStart.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Intent intent = new Intent(AppStart.this, StoreList.class);
                            startActivity(intent);
                            finish();
                        }

                   } else {
                        Utility.ToastMessage(AppStart.this, getString(R.string.network_not_connected));
                       /* new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                finish();
                            }

                        }, 1000);

*/
                        ll_net.setVisibility(View.VISIBLE);
                        reload_activity.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                                NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                                if (info != null && info.isAvailable()) {
                                    UIUtil.showWaitDialog(AppStart.this);
                                    ll_net.setVisibility(View.GONE);
                                    if(GlobalContext.getInstance().getSpUtil().getStoreId()!=0L) {

                                        Intent intent = new Intent(AppStart.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        Intent intent = new Intent(AppStart.this, StoreList.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                } else {
                                    ShowToastUtils.showToast(getString(R.string.network_not_connected), AppStart.this);
                                }

                            }
                        });

                    }



                    break;
                case GO_GUIDE:
                    loadGuide();

                    break;
            }
            super.handleMessage(msg);
        }
    };

    private SharePreferenceUtil mSpUtil;

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        final View view = View.inflate(this, R.layout.appstart, null);
        setContentView(view);
        ll_net= (LinearLayout) findViewById(R.id.llnet);
        reload_activity= (Button) findViewById(R.id.reload);


//        getInte
        // 渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
        aa.setDuration(2000);
        view.startAnimation(aa);
        aa.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {

                init();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }

        });

    }

    private void init() {
        // 读取SharedPreferences中需要的数据
        // 使用SharedPreferences来记录程序的使用次数
        SharedPreferences preferences = getSharedPreferences(
          SHAREDPREFERENCES_NAME, MODE_PRIVATE);

        // 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
        isFirstIn = preferences.getBoolean("isFirstIn", true);



        // 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
        if (!isFirstIn) {
			// 使用Handler的postDelayed方法，2秒后执行跳转到MainActivity
			mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
		} else {
			mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
		}

    }


    /**
     * 加载引导
     */
    private void loadGuide() {
		Intent intent = new Intent(AppStart.this, GuideActivity.class);
		AppStart.this.startActivity(intent);
		AppStart.this.finish();
    }


}
