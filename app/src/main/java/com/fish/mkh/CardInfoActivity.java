package com.fish.mkh;

import java.util.List;

import com.fish.mkh.div.MkhTitleBar;
import com.mkh.mobilemall.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import java.util.ArrayList;

public class CardInfoActivity extends Activity {

	Context context;
	MkhTitleBar mTitleBar;

	private Button bt_button1;
	private Button bt_button2;
	private View bt_button1_bottom;
	private View bt_button2_bottom;
	private ViewPager mPager;//页卡内容

	private Button bt_cardinforecharge;
	private List<View> listViews; // Tab页面列表

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cardinfo);
		context = this;
		initTileBar();
		initControl();
	}

	public void initTileBar() {
		mTitleBar = (MkhTitleBar) findViewById(R.id.cardinfoactivity_actionbar);
		mTitleBar.setTitle(getResources().getString(R.string.title_activity_cardinfo));
		mTitleBar.setLeftClickFinish(this);

		bt_cardinforecharge = (Button)findViewById(R.id.cardinfoactivity_recharge_bt);
		bt_cardinforecharge.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(CardInfoActivity.this,ReChargeActivity.class);
				startActivity(intent);
				
			}
		});
	}


	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();

		CardInfoView tkv1 = new CardInfoView(context,1);
		CardInfoView tkv2 = new CardInfoView(context,2);

		listViews.add(tkv1);
		listViews.add(tkv2);

		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	private void initControl() {

		bt_button1 = (Button)findViewById(R.id.tta_bt1);
		bt_button2 = (Button)findViewById(R.id.tta_bt2);
		bt_button1_bottom =  (View)findViewById(R.id.tta_bt1_bottom);
		bt_button2_bottom =  (View)findViewById(R.id.tta_bt2_bottom);

		bt_button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				mPager.setCurrentItem(0);
				bt_button1.setTextColor(getResources().getColor(R.color.color_orange));
				bt_button2.setTextColor(getResources().getColor(R.color.color_txt_black));
				bt_button1_bottom.setVisibility(View.VISIBLE);
				bt_button2_bottom.setVisibility(View.INVISIBLE);
			}
		});

		bt_button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				mPager.setCurrentItem(1);
				bt_button2.setTextColor(getResources().getColor(R.color.color_orange));
				bt_button1.setTextColor(getResources().getColor(R.color.color_txt_black));
				bt_button2_bottom.setVisibility(View.VISIBLE);
				bt_button1_bottom.setVisibility(View.INVISIBLE);
			}
		});

		InitViewPager() ;
	}

	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

		@Override
		public void onPageSelected(int arg0) {

			switch (arg0) {
				case 0:
					bt_button1_bottom.setVisibility(View.VISIBLE);
					bt_button2_bottom.setVisibility(View.INVISIBLE);
					bt_button1.setTextColor(getResources().getColor(R.color.color_orange));
					bt_button2.setTextColor(getResources().getColor(R.color.color_txt_black));
					break;
				case 1:
					bt_button2_bottom.setVisibility(View.VISIBLE);
					bt_button1_bottom.setVisibility(View.INVISIBLE);
					bt_button2.setTextColor(getResources().getColor(R.color.color_orange));
					bt_button1.setTextColor(getResources().getColor(R.color.color_txt_black));
					break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}


}
