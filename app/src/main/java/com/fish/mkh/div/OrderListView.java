package com.fish.mkh.div;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.fish.mkh.MyOrderActivity;
import com.fish.mkh.MyOrderAdapter;

import com.fish.mkh.client.AuthDao;
import com.fish.mkh.util.UIUtil;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.app.GlobalContext;
import com.xiniunet.api.domain.ecommerce.Order;
import com.xiniunet.api.domain.ecommerce.OrderStatusEnum;
import com.xiniunet.api.domain.membership.Voucher;
import com.xiniunet.api.domain.membership.VoucherStateEnum;
import com.xiniunet.api.request.ecommerce.OrderFindRequest;
import com.xiniunet.api.request.membership.VouchersFindRequest;
import com.xiniunet.api.response.ecommerce.OrderCloseResponse;
import com.xiniunet.api.response.ecommerce.OrderFindResponse;
import com.xiniunet.api.response.membership.VouchersFindResponse;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class OrderListView extends LinearLayout {

	Context context;
	MyOrderAdapter adapter;
	private ListView listView;
	List<Order> myOrderList;
	private int LIST_OK=1;
	private int LIST_ERROR=0;
	
	private int visibleLastIndex = 0;   //最后的可视项索引    
	private int visibleItemCounts;       // 当前窗口可见项总数  

	private int pagesize = 20;
	private int currentpage = 0;
	private boolean isLastpage = false;
	private int index = 0;
	Handler handler;
	private boolean isOnline;
	private RelativeLayout rlError;
	
	public OrderListView(Context context) {
		super(context);
		this.context = context;
		init();
	}
	
	public OrderListView(Context context, int index,boolean isonline) {
		super(context);
		this.context = context;
		this.index = index;
		this.isOnline = isonline;
		init();
	}

	private Handler handler1 = new Handler();

	private void init() {

        View.inflate(context,R.layout.order_view_listview, this);
        
        listView = (ListView)findViewById(R.id.tkv_listview);
		rlError = (RelativeLayout)findViewById(R.id.rl_error);
        loadDatas(index, currentpage);

		adapter = new MyOrderAdapter(context,index); //创建适配器
		listView.setAdapter(adapter);   

		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				int itemsLastIndex = adapter.getCount() - 1; // 数据集最后一项的索引
				int lastIndex = itemsLastIndex;
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						&& visibleLastIndex == lastIndex) {

					handler1.postDelayed(new Runnable() {
						@Override
						public void run() {
							if (!isLastpage) {
								currentpage++;
								loadDatas(index, currentpage);
								adapter.notifyDataSetChanged(); // 数据集变化后,通知adapter
								listView.setSelection(visibleLastIndex
										- visibleItemCounts + 1); // 设置选中项
							}

						}
					}, 10);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				visibleItemCounts = visibleItemCount;
				visibleLastIndex = firstVisibleItem + visibleItemCount - 1;

			}
		});

		
		handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == LIST_OK) {
					OrderFindResponse orderListRsp = (OrderFindResponse) msg.obj;
					if (null != orderListRsp && orderListRsp.getResult() != null) {
						if (myOrderList == null) {
							myOrderList = orderListRsp.getResult();
							Log.e("---test---",myOrderList.toString());
						} else{
							myOrderList.addAll(orderListRsp.getResult());
						}
						if (myOrderList.size() >= orderListRsp.getTotalCount()) {
							isLastpage = true;
						}
						if(myOrderList != null && myOrderList.size()==0){
							rlError.setVisibility(View.VISIBLE);
						}
						adapter.setData(myOrderList);
						listView.setAdapter(adapter);
						UIUtil.dismissDlg();
					} else {
						rlError.setVisibility(View.VISIBLE);
					}
				} else if (msg.what == LIST_ERROR) {
					OrderFindResponse orderListRsp = (OrderFindResponse) msg.obj;
					Toast.makeText(context,
							orderListRsp.getErrors().get(0).getMessage(),
							Toast.LENGTH_SHORT).show();
						rlError.setVisibility(View.VISIBLE);
					UIUtil.dismissDlg();
				}
			}
		};
	}

	private void getOrderList(final int index, final int currPage) {
		Log.e("-----","getOrderList----");
		new Thread() {
			public void run() {
				Message msg = new Message();

				OrderFindRequest orderlistReq = new OrderFindRequest();

				orderlistReq.setPageNumber(currPage);
				orderlistReq.setPageSize(pagesize);
				orderlistReq.setIsOnline(isOnline);
				switch (index) {
				case 0:
					break;
				case 1:
					orderlistReq.setStatus(OrderStatusEnum.ORDER_PLACED);
					break;
				case 2:
					orderlistReq.setStatus(OrderStatusEnum.ORDER_PAID);
					break;
				case 3:
					orderlistReq.setStatus(OrderStatusEnum.GOODS_RECEIVED);
					break;
					case 4:
						orderlistReq.setStatus(OrderStatusEnum.ORDER_CLOSED);
						break;
				default:
					break;
				}
				try {
					OrderFindResponse orderListRsp = AuthDao.client.execute(
							orderlistReq, GlobalContext.getInstance().getSpUtil().getUserInfo());
					Log.e("-----","orderListRsp----");
					if (orderListRsp.hasError()) {
						msg.what = LIST_ERROR; // 获取失败
					} else {
						msg.what = LIST_OK; // 登录成功
					}
					msg.obj = orderListRsp;
					handler.sendMessage(msg);
				} catch (Exception e) {
					Log.e("dd",e.getMessage().toString());
				}
			}
		}.start();
	}
	
	private void loadDatas(int index, int curPage) {
		adapter = new MyOrderAdapter(context,index);
		myOrderList = new ArrayList<Order>();
		UIUtil.showWaitDialog((Activity)context);
		getOrderList(index, curPage);
	}

	public void reloadDatas(int index){
		adapter = new MyOrderAdapter(context,index);
		myOrderList = new ArrayList<Order>();
		UIUtil.showWaitDialog((Activity)context);
		currentpage = 0;
		getOrderList(index, currentpage);
	}
}
