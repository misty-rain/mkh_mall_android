package com.mkh.mobilemall.ui.activity.guide;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.audiofx.BassBoost;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mkh.mobilemall.R;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.ui.activity.auth.LoginActivity;
import com.mkh.mobilemall.ui.activity.main.MainActivity;
import com.mkh.mobilemall.ui.activity.map.Location;
import com.mkh.mobilemall.ui.adapter.GuideAdapter;
import com.mkh.mobilemall.ui.base.BaseActivity;
import com.xiniunet.api.domain.master.Store;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import m.framework.ui.widget.viewpager.ViewPagerAdapter;

/**
 * 首次安装应到界面
 */
public class GuideActivity extends Activity implements ViewPager.OnPageChangeListener {
    private ViewPager viewPager;

    private GuideAdapter guideAdapter;

    private List<View> views;

    //底部小点图片
    private ImageView[] dots;

    //记录当前选中的位置
    private int currentIndex;
    private boolean isSetting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isSetting = Boolean.parseBoolean(getIntent()
                .getStringExtra("isSetting"));
        if (!isSetting) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            ActionBar ab = getActionBar();
            ab.setTitle("功能介绍");
            ab.setDisplayHomeAsUpEnabled(true);
        }

        setContentView(R.layout.activity_guide);
        //初始化页面
        initViews();
        //初始化底部小点
      //  initDots();
    }


    private void initViews() {
        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        //初始化引导图片列表
        views.add(inflater.inflate(R.layout.loading_app_one, null));
        views.add(inflater.inflate(R.layout.loading_app_two, null));
        views.add(inflater.inflate(R.layout.loading_app_three, null));
        views.add(inflater.inflate(R.layout.loading_app_four, null));

        //初始化Adapter
        guideAdapter = new GuideAdapter(views, this, isSetting);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(guideAdapter);
        //绑定回调
        viewPager.setOnPageChangeListener(this);
    }

//    private void initDots() {
 //   LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
//        dots = new ImageView[views.size()];
//        for (int i = 0; i < views.size(); i++) {
//            dots[i] = (ImageView) ll.getChildAt(i);
//            dots[i].setEnabled(true);//设为灰色
//        }
//        currentIndex = 0;
//        dots[currentIndex].setEnabled(false);//设置为白色，即为选中颜色
//    }

    //设置当前底部小点
//    private void setCurrentDot(int position) {
//        if (position < 0 || position > views.size() - 1 || currentIndex == position) {
//            return;
//        }
//        dots[position].setEnabled(false);
//        dots[currentIndex].setEnabled(true);
//
//        currentIndex = position;
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    //当新的页面被选中时调用
    @Override
    public void onPageSelected(int position) {
        //设置底部小点选中状态
     //      setCurrentDot(position);
    }

    //当滑动状态改变时调用
    @Override
    public void onPageScrollStateChanged(int state) {

    }





}
