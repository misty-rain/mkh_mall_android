package com.fish.mkh;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fish.mkh.client.AuthDao;
import com.fish.mkh.util.UIUtil;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.app.GlobalContext;
import com.xiniunet.api.domain.membership.BalanceHistory;
import com.xiniunet.api.domain.membership.OrderHistoryBean;
import com.xiniunet.api.request.membership.MemberBalanceHistoryFindRequest;
import com.xiniunet.api.request.membership.MemberOrderHistoryFindRequest;
import com.xiniunet.api.response.membership.MemberBalanceHistoryFindResponse;
import com.xiniunet.api.response.membership.MemberOrderHistoryFindResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CardInfoView extends LinearLayout {

	Context context;
	private ListView lv_cardinfolist;
	private scoreAdapter m_cardinfoAdapter;
	private ArrayList<BalanceHistory> cardinfo_array;
	private ArrayList<OrderHistoryBean> cardinfo_array2;

	private int visibleLastIndex = 0;   //最后的可视项索引
	private int visibleItemCounts;       // 当前窗口可见项总数

	private int pagesize = 10;
	private int currentpage = 0;
	private boolean isLastpage = false;
	private int index = 1;

	public CardInfoView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public CardInfoView(Context context, int index) {
		super(context);
		this.context = context;
		this.index = index;
		init();
	}

	private Handler handler1 = new Handler();    

	private void init() {
		
		LayoutInflater mInflater = LayoutInflater.from(context);
        View myView = mInflater.inflate(R.layout.card_view_listview, null);
        addView(myView);
		lv_cardinfolist = (ListView)myView.findViewById(R.id.tkv_listview);
		memberBalanceHistoryFind(currentpage, pagesize);

		m_cardinfoAdapter = new scoreAdapter(); //创建适配器
		lv_cardinfolist.setAdapter(m_cardinfoAdapter);
		lv_cardinfolist.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				int itemsLastIndex = m_cardinfoAdapter.getCount() - 1;    //数据集最后一项的索引
				int lastIndex = itemsLastIndex ;
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {

					handler1.postDelayed(new Runnable() {
						@Override
						public void run() {
							if(!isLastpage) {
								currentpage++;
								memberBalanceHistoryFind(currentpage, pagesize);
								m_cardinfoAdapter.notifyDataSetChanged(); //数据集变化后,通知adapter
								lv_cardinfolist.setSelection(visibleLastIndex - visibleItemCounts + 1); //设置选中项
							}
						}
					}, 10);
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				visibleItemCounts = visibleItemCount;
				visibleLastIndex = firstVisibleItem + visibleItemCount - 1;

			}
		});

	}


	public class scoreAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			if(cardinfo_array != null && index == 2)
				return cardinfo_array.size();
			else if(cardinfo_array2 != null && index == 1){
				return cardinfo_array2.size();
			}
				return 0;
		}

		@Override
		public Object getItem(int position) {
			if(cardinfo_array != null && index == 2)
				return cardinfo_array.get(position);
			else if(cardinfo_array2!= null && index==1)
				return cardinfo_array2.get(position);
			else
				return null;
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ListItemView  listItemView = null;
			if (convertView == null) {
				listItemView = new ListItemView();
				convertView = LayoutInflater.from(context).inflate(R.layout.adapter_score2, null);
				//获取控件对象
				listItemView.tv_title = (TextView)convertView.findViewById(R.id.adapter_title);
				listItemView.tv_date= (TextView)convertView.findViewById(R.id.adapter_date);
				listItemView.tv_cost = (TextView)convertView.findViewById(R.id.adapter_cost);

				//设置控件集到convertView
				convertView.setTag(listItemView);
			}else {
				listItemView = (ListItemView)convertView.getTag();
			}

			if(index == 1){
				OrderHistoryBean recharge = cardinfo_array2.get(position);
				if (recharge != null) {
					if (recharge.getCard() != null)
						listItemView.tv_title.setText("订单号：" + recharge.getCard());

					if (recharge.getTime() != null) {
						listItemView.tv_date.setText("时间："+recharge.getDate());
					}
					listItemView.tv_cost.setText("-" + recharge.getMoney() + "元");

				}
			}else {
				BalanceHistory recharge = cardinfo_array.get(position);
				if (recharge != null) {
					if (recharge.getPayment() != null)
						listItemView.tv_title.setText(recharge.getPayment());

					DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					if (recharge.getTime() != null) {
						String date = df.format(recharge.getTime());
						listItemView.tv_date.setText("时间："+ date);
					}
					listItemView.tv_cost.setTextColor(getResources().getColor(R.color.color_txt_green));
					listItemView.tv_cost.setText("+" + recharge.getAmount() + "元");

				}
			}
			return convertView;
		}
	}

	private void memberBalanceHistoryFind(final int pageNumber, final int pageSize) {

		UIUtil.showWaitDialog((Activity)context);
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				UIUtil.dismissDlg();
				if (msg.what == 2){
					String s = (String) msg.obj;
					Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
					return;
				}

				if (msg.what == 4) {
					MemberBalanceHistoryFindResponse memberBalanceHistoryFindResponse = (MemberBalanceHistoryFindResponse) msg.obj;
					if(memberBalanceHistoryFindResponse.getResult() != null) {
						if(cardinfo_array ==null){
							cardinfo_array = new ArrayList<BalanceHistory>(memberBalanceHistoryFindResponse.getResult());
						} else {
							cardinfo_array.addAll(memberBalanceHistoryFindResponse.getResult());
						}
						if(memberBalanceHistoryFindResponse.getResult().size()<10) {
							isLastpage = true;
						}
						m_cardinfoAdapter.notifyDataSetChanged();
					}
				} else if (msg.what == 3) {
					MemberBalanceHistoryFindResponse memberBalanceHistoryFindResponse = (MemberBalanceHistoryFindResponse) msg.obj;
					Toast.makeText(context, memberBalanceHistoryFindResponse.getErrors().get(0).getMessage(), Toast.LENGTH_SHORT).show();
				}else if (msg.what == 1) {
					MemberOrderHistoryFindResponse memberOrderHistoryFindResponse = (MemberOrderHistoryFindResponse) msg.obj;
					if(memberOrderHistoryFindResponse.getResult() != null) {
						if(cardinfo_array2 ==null){
							cardinfo_array2 = new ArrayList<OrderHistoryBean>(memberOrderHistoryFindResponse.getResult());
						} else {
							cardinfo_array2.addAll(memberOrderHistoryFindResponse.getResult());
						}
						if(memberOrderHistoryFindResponse.getResult().size()<10) {
							isLastpage = true;
						}
						m_cardinfoAdapter.notifyDataSetChanged();
					}
				} else if (msg.what == 0) {
					MemberOrderHistoryFindResponse memberOrderHistoryFindResponse = (MemberOrderHistoryFindResponse) msg.obj;
					Toast.makeText(context, memberOrderHistoryFindResponse.getErrors().get(0).getMessage(), Toast.LENGTH_SHORT).show();
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				if(index == 1) {

					MemberOrderHistoryFindRequest memberBalanceHistoryFindRequest = new MemberOrderHistoryFindRequest();
					memberBalanceHistoryFindRequest.setPageNumber(pageNumber);
					memberBalanceHistoryFindRequest.setPageSize(pageSize);
					try {
						MemberOrderHistoryFindResponse memberBalanceHistoryFindResponse = AuthDao.client.execute(memberBalanceHistoryFindRequest, GlobalContext.getInstance().getSpUtil().getUserInfo());
						if (memberBalanceHistoryFindResponse.hasError()) {
							msg.what = 0;   // 登录失败
						} else {
							msg.what = 1;   // 登录成功
						}
						msg.obj = memberBalanceHistoryFindResponse;
						handler.sendMessage(msg);
					} catch (Exception e) {
						msg.what = 2;
						msg.obj = e.getMessage();
						handler.sendMessage(msg);
						//                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
					}
					//                dialog.dismiss();
				}else{
					MemberBalanceHistoryFindRequest memberBalanceHistoryFindRequest = new MemberBalanceHistoryFindRequest();
					memberBalanceHistoryFindRequest.setPageNumber(pageNumber);
					memberBalanceHistoryFindRequest.setPageSize(pageSize);
					try {
						MemberBalanceHistoryFindResponse memberBalanceHistoryFindResponse = AuthDao.client.execute(memberBalanceHistoryFindRequest, GlobalContext.getInstance().getSpUtil().getUserInfo());
						if (memberBalanceHistoryFindResponse.hasError()) {
							msg.what = 3;   // 登录失败
						} else {
							msg.what = 4;   // 登录成功
						}
						msg.obj = memberBalanceHistoryFindResponse;
						handler.sendMessage(msg);
					} catch (Exception e) {
						msg.what = 2;
						msg.obj = e.getMessage();
						handler.sendMessage(msg);
						//                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
					}
					//                dialog.dismiss();
				}
			}
		}.start();
	}
	public final class ListItemView{
		public TextView tv_title;
		public TextView tv_subtitle;
		public TextView tv_content;
		public TextView tv_date;
		public TextView tv_cost;
	}

}

