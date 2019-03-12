package com.fish.mkh;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.fish.mkh.client.AuthDao;
import com.fish.mkh.div.MkhTitleBar;
import com.fish.mkh.div.OrderListView;
import com.fish.mkh.div.TopTablet;
import com.fish.mkh.div.TopTablet.SelectChangeListener;
import com.fish.mkh.util.UIUtil;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.support.db.TempComCarDBTask;
import com.mkh.mobilemall.ui.base.BaseActivity;
import com.xiniunet.api.request.ecommerce.OrderCloseRequest;
import com.xiniunet.api.response.ecommerce.OrderCloseResponse;

public class MyOrderActivity extends BaseActivity {

	MkhTitleBar mTitleBar;

	TopTablet topTable;
	int index = 0;

	private Handler handler1 = new Handler();
	Handler handler;
	private int CLOSE_OK = 2;
	private int CLOSE_ERROR = 3;
	public static final int Request_Code = 1001;

	private ViewPager mPager;// 页卡内容
	private List<View> listViews; // Tab页面列表
	Context context = this;
	OrderListView tkv1,tkv2,tkv3,tkv4,tkv5;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_order);
		index = getIntent().getIntExtra("INDEX", 0);
		initTileBar();
		InitViewPager();
		initViews();

	}

	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();

		 tkv1 = new OrderListView(context, 0,
				mTitleBar.getRightSwitchChecked());
		 tkv2 = new OrderListView(context, 1,
				mTitleBar.getRightSwitchChecked());
		 tkv3 = new OrderListView(context, 2,
				mTitleBar.getRightSwitchChecked());
		 tkv4 = new OrderListView(context, 3,
				mTitleBar.getRightSwitchChecked());
		 tkv5 = new OrderListView(context, 4,
				mTitleBar.getRightSwitchChecked());

		listViews.add(tkv1);
		listViews.add(tkv2);
		listViews.add(tkv3);
		listViews.add(tkv4);
		listViews.add(tkv5);

		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	private void initViews() {
		topTable = (TopTablet) findViewById(R.id.top_table);
		topTable.setChangeListener(new SelectChangeListener() {
			@Override
			public void onSelectChanged(int index) {
				mPager.setCurrentItem(index);
				reflush(index);
			}
		});
		topTable.setSelected(index);

		handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == CLOSE_OK) {
					Toast.makeText(MyOrderActivity.this, "取消订单成功",
							Toast.LENGTH_SHORT).show();
				      reflush(index);
					UIUtil.dismissDlg();
				} else if (msg.what == CLOSE_ERROR) {
					OrderCloseResponse ordercloseRSP = (OrderCloseResponse) msg.obj;
					Toast.makeText(MyOrderActivity.this,
							ordercloseRSP.getErrors().get(0).getMessage(),
							Toast.LENGTH_SHORT).show();
					UIUtil.dismissDlg();
				}
			}
		};
	}

	public void reflush(int index){
		switch (index){
			case 0:
				tkv1 = new OrderListView(context, 0,
						  mTitleBar.getRightSwitchChecked());
				break;
			case 1:
				tkv2 = new OrderListView(context, 1,
						  mTitleBar.getRightSwitchChecked());
				break;
			case 2:
				tkv3 = new OrderListView(context, 2,
						  mTitleBar.getRightSwitchChecked());
				break;
			case 3:
				tkv4 = new OrderListView(context, 3,
						  mTitleBar.getRightSwitchChecked());
				break;
			case 4:
				tkv5 = new OrderListView(context, 4,
						  mTitleBar.getRightSwitchChecked());
				break;
			default:
				break;
		}
		listViews.clear();
		listViews.add(tkv1);
		listViews.add(tkv2);
		listViews.add(tkv3);
		listViews.add(tkv4);
		listViews.add(tkv5);

		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(index);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}





	public void initTileBar() {
		mTitleBar = (MkhTitleBar) findViewById(R.id.title_setting);
		mTitleBar.setTitle(getResources().getString(R.string.myorder_title));
		mTitleBar.setLeftClickFinish(this, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it= new Intent(MyOrderActivity.this,MemberCenterActivity.class);
				setResult(1,it);
				finish();

			}
		});
		mTitleBar.setRightSwitch(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				mPager.setCurrentItem(topTable.getIndex());

			}
		});
	}

	public void cancelOrder(final List<Long> id,final int selectedIndex) {
		UIUtil.showWaitDialog(MyOrderActivity.this);
		new Thread() {
			public void run() {
				Message msg = new Message();

				OrderCloseRequest ordercloseReq = new OrderCloseRequest();
				ordercloseReq.setId(id.get(0));
				try {
					OrderCloseResponse ordercloseRsp = AuthDao.client.execute(
							ordercloseReq, GlobalContext.getInstance().getSpUtil().getUserInfo());
					if (ordercloseRsp.hasError()) {
						msg.what = CLOSE_ERROR; // 获取失败
					} else {
						msg.what = CLOSE_OK; //成功
						index=selectedIndex;
					}
					msg.obj = ordercloseRsp;
					handler.sendMessage(msg);
				} catch (Exception e) {
				}
			}
		}.start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Request_Code && resultCode == RESULT_OK) {
			mPager.setCurrentItem(topTable.getIndex());
			reflush(topTable.getIndex());

		}

		super.onActivityResult(requestCode, resultCode, data);
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

	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int arg0) {

			switch (arg0) {
			case 0:
				topTable.setSelected(0);
				break;
			case 1:
				topTable.setSelected(1);
				break;
			case 2:
				topTable.setSelected(2);
				break;
			case 3:
				topTable.setSelected(3);
				break;
				case 4:
					topTable.setSelected(4);
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
