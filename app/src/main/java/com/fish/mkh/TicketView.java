package com.fish.mkh;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.fish.mkh.client.AuthDao;
import com.fish.mkh.util.UIUtil;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.app.GlobalContext;
import com.xiniunet.api.domain.membership.Voucher;
import com.xiniunet.api.domain.membership.VoucherStateEnum;
import com.xiniunet.api.request.membership.VouchersFindRequest;
import com.xiniunet.api.response.membership.VouchersFindResponse;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class TicketView extends LinearLayout {

	Context context;
	private ListView lv_ticketlist;
	private List<Voucher> ticket_array;
	private ticketAdapter m_ticketAdapter;
	
	private int visibleLastIndex = 0;   //最后的可视项索引    
	private int visibleItemCounts;       // 当前窗口可见项总数  

	private int pagesize = 20;
	private int currentpage = 0;
	private boolean isLastpage = false;
	private boolean isexpire = false;
	
	public TicketView(Context context) {
		super(context);
		this.context = context;
		init();
	}
	
	public TicketView(Context context, boolean isexpire) {
		super(context);
		this.context = context;
		this.isexpire = isexpire;
		init();
	}

	private Handler handler1 = new Handler();    

	private void init() {
		
		LayoutInflater mInflater = LayoutInflater.from(context);
        View myView = mInflater.inflate(R.layout.ticket_view_listview, null);
        addView(myView);
        
		lv_ticketlist = (ListView)myView.findViewById(R.id.tkv_listview);
        
		VouchersFind(currentpage, pagesize);

		m_ticketAdapter = new ticketAdapter(); //创建适配器   
		lv_ticketlist.setAdapter(m_ticketAdapter);   

		lv_ticketlist.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				int itemsLastIndex = m_ticketAdapter.getCount() - 1;    //数据集最后一项的索引    
				int lastIndex = itemsLastIndex ;  
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {    
					//如果是自动加载,可以在这里放置异步加载数据的代码    
					//	                Log.i("LOADMORE", "loading...");    
					handler1.postDelayed(new Runnable() {    
						@Override    
						public void run() {    

							if(!isLastpage) {
								currentpage++;
								VouchersFind(currentpage, pagesize);
								m_ticketAdapter.notifyDataSetChanged(); //数据集变化后,通知adapter    
								lv_ticketlist.setSelection(visibleLastIndex - visibleItemCounts + 1); //设置选中项     
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

	public final class ListItemView{   
		public LinearLayout ta_layout;
		public TextView tv_title;
		public TextView tv_subtitle;
		public TextView tv_cost;      
		public TextView tv_content;
	}  

	public class ticketAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			if(ticket_array != null)
				return ticket_array.size(); 
			else
				return 0;
		}

		@Override
		public Object getItem(int position) {
			if(ticket_array != null)
				return ticket_array.get(position);
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
				convertView = LayoutInflater.from(context).inflate(R.layout.adapter_ticket, null);
				
				//获取控件对象   
				listItemView.ta_layout = (LinearLayout)convertView.findViewById(R.id.adt_cost_layout);
				listItemView.tv_title = (TextView)convertView.findViewById(R.id.adt_title);   
				listItemView.tv_subtitle = (TextView)convertView.findViewById(R.id.adt_subtitle);   
				listItemView.tv_content = (TextView)convertView.findViewById(R.id.adt_content);   
				listItemView.tv_cost = (TextView)convertView.findViewById(R.id.adt_cost); 

				//设置控件集到convertView   
				convertView.setTag(listItemView);   
			}else {   
				listItemView = (ListItemView)convertView.getTag();   
			}

			//			if(isexpire) {
			//				listItemView.ta_layout.setBackgroundResource(R.drawable.ticket_item_over);
			//			}

			Voucher voucher = ticket_array.get(position);

			if(voucher.getState() == VoucherStateEnum.EXPIRED){
				listItemView.ta_layout.setBackgroundResource(R.mipmap.ticket_item_over);
			}else if(voucher.getState() == VoucherStateEnum.ABOUT_TO_EXPIRE){
				listItemView.ta_layout.setBackgroundResource(R.mipmap.ticket_item_expire);
			}else{
				listItemView.ta_layout.setBackgroundResource(R.mipmap.ticket_item);
			}

			if( voucher != null){
				if(voucher.getName() != null)
					listItemView.tv_title.setText(voucher.getName());
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				if(voucher.getValidDate() != null){
					String date = df.format(voucher.getValidDate()); 
					listItemView.tv_subtitle.setText(date);
				}
				if(voucher.getRange() != null)
					listItemView.tv_content.setText(voucher.getRange());
				if(voucher.getAmount()!=null)
					listItemView.tv_cost.setText(context.getString(R.string.center_mark_money)+" "+voucher.getAmount());
			}
			return convertView; 
		}

	}

	private void VouchersFind(final int pageNumber, final int pageSize) {
		UIUtil.showWaitDialog((Activity)context);
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				UIUtil.dismissDlg();
				if (msg.what == 2){
					String s = (String) msg.obj;
					Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
					return;
				}
				VouchersFindResponse vouchersFindResponse = (VouchersFindResponse) msg.obj;
				if (msg.what == 1) {

					//                	ticket_array = vouchersFindResponse.getResult();
					if(vouchersFindResponse.getResult()!=null){
						if(ticket_array ==null){
							ticket_array = new ArrayList<Voucher>(vouchersFindResponse.getResult());
						} else {
							ticket_array.addAll(vouchersFindResponse.getResult());
						}
						if(vouchersFindResponse.getTotalCount() <= ticket_array.size()) {
							isLastpage = true;
						}
						m_ticketAdapter.notifyDataSetChanged();
					}
				} else if (msg.what == 0) {
					Toast.makeText(context, vouchersFindResponse.getErrors().get(0).getMessage(), Toast.LENGTH_SHORT).show();
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();

				VouchersFindRequest vouchersFindRequest = new VouchersFindRequest();
				vouchersFindRequest.setPageNumber(pageNumber);
				vouchersFindRequest.setPageSize(pageSize);
				try {
					VouchersFindResponse vouchersFindResponse = AuthDao.client.execute(vouchersFindRequest, GlobalContext.getInstance().getSpUtil().getUserInfo());
					if (vouchersFindResponse.hasError()) {
						msg.what = 0;   // 登录失败
					} else {
						msg.what = 1;   // 登录成功
					}
					msg.obj = vouchersFindResponse;
					handler.sendMessage(msg);
				} catch (Exception e) {
					msg.what = 2;
					msg.obj = e.getMessage();
					handler.sendMessage(msg);
					//                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
				}
				//                dialog.dismiss();
			}
		}.start();
	}
}
