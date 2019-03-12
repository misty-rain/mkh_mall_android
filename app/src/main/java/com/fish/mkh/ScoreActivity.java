package com.fish.mkh;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.fish.mkh.client.AuthDao;
import com.fish.mkh.div.MkhTitleBar;
import com.fish.mkh.util.UIUtil;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.ui.base.BaseActivity;
import com.xiniunet.api.domain.membership.InOutEnum;
import com.xiniunet.api.domain.membership.PointHistory;
import com.xiniunet.api.request.membership.MemberPointHistoryFindRequest;
import com.xiniunet.api.response.membership.MemberPointHistoryFindResponse;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class ScoreActivity extends BaseActivity {

	Context context;
	MkhTitleBar mTitleBar;
	private ListView lv_scorelist;
	private ArrayList<PointHistory> score_array;
	private scoreAdapter m_scoreAdapter;

	private int visibleLastIndex = 0;   //最后的可视项索引    
	private int visibleItemCounts;       // 当前窗口可见项总数  

	private int pagesize = 20;
	private int currentpage = 0;
	private boolean isLastpage = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score);
		context = this;
		initTileBar();
		initControl();
	}

	public void initTileBar() {
		mTitleBar = (MkhTitleBar) findViewById(R.id.scoreactivity_actionbar);
		mTitleBar.setTitle(getResources().getString(R.string.title_activity_score));
		mTitleBar.setLeftClickFinish(this);
	}

	private Handler handler1 = new Handler(); 

	private void initControl() {
		lv_scorelist = (ListView)findViewById(R.id.scoreactivity_listview);
		memberPointHistoryFind(currentpage, pagesize);
		//		score_array = new ArrayList<scorebean>();
		//		for(int i=0; i<30; i++) {
		//			scorebean bean= new scorebean();
		//			score_array.add(bean);
		//		}
		m_scoreAdapter = new scoreAdapter(); //创建适配器   
		lv_scorelist.setAdapter(m_scoreAdapter);   

		lv_scorelist.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				int itemsLastIndex = m_scoreAdapter.getCount() - 1;    //数据集最后一项的索引    
				int lastIndex = itemsLastIndex ;  
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {    

					handler1.postDelayed(new Runnable() {    
						@Override    
						public void run() {    

							if(!isLastpage) {
								currentpage++;
								memberPointHistoryFind(currentpage, pagesize);
								m_scoreAdapter.notifyDataSetChanged(); //数据集变化后,通知adapter    
								lv_scorelist.setSelection(visibleLastIndex - visibleItemCounts + 1); //设置选中项     
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
		public TextView tv_title;     
		public TextView tv_subtitle;     
		public TextView tv_content;   
		public TextView tv_date;   
		public TextView tv_cost;          
	}  

	public class scoreAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			if( score_array != null)
				return score_array.size();
			else
				return 0;
		}

		@Override
		public Object getItem(int position) {
			if( score_array != null)
				return score_array.get(position);
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
				convertView = LayoutInflater.from(context).inflate(R.layout.adapter_score, null);   
				//获取控件对象   
				listItemView.tv_title = (TextView)convertView.findViewById(R.id.adapter_title);   
				listItemView.tv_subtitle = (TextView)convertView.findViewById(R.id.adapter_subtitle);   
				listItemView.tv_content = (TextView)convertView.findViewById(R.id.adapter_content);   
				listItemView.tv_date= (TextView)convertView.findViewById(R.id.adapter_date);   
				listItemView.tv_cost = (TextView)convertView.findViewById(R.id.adapter_cost); 

				//设置控件集到convertView   
				convertView.setTag(listItemView);   
			}else {   
				listItemView = (ListItemView)convertView.getTag();   
			}   
			
	
			PointHistory pointHistory = score_array.get(position);
			if( pointHistory != null){
				if(pointHistory.getSummary() != null)
					listItemView.tv_title.setText(pointHistory.getSummary());
				if(pointHistory.getBalance()!= null)
					listItemView.tv_subtitle.setText("积分："+pointHistory.getAmount());
				if(pointHistory.getAmount()!= null) {
					if(pointHistory.getInOut() == InOutEnum.IN) {
						listItemView.tv_content.setTextColor(getResources().getColor(R.color.color_txt_green));
						listItemView.tv_content.setText("+"+pointHistory.getAmount()+"积分");
					}else {
						listItemView.tv_content.setTextColor(getResources().getColor(R.color.color_txt_red));
						listItemView.tv_content.setText("-"+pointHistory.getAmount()+"积分");
					}
					
				}
				
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				if(pointHistory.getTime() != null){
					String date = df.format(pointHistory.getTime()); 
					listItemView.tv_date.setText(date);
				}
				
				if(pointHistory.getMoneyAmount()!=null)
					listItemView.tv_cost.setTextColor(getResources().getColor(R.color.color_red));
					listItemView.tv_cost.setText("-"+pointHistory.getMoneyAmount()+"元");
			}
			return convertView; 
		}

	}

	private void memberPointHistoryFind(final int pageNumber, final int pageSize) {
		
		UIUtil.showWaitDialog(ScoreActivity.this);
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				UIUtil.dismissDlg();
				if (msg.what == 2){
					String s = (String) msg.obj;
					Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
					return;
				}
				MemberPointHistoryFindResponse memberPointHistoryFindResponse = (MemberPointHistoryFindResponse) msg.obj;
				if (msg.what == 1) {
					
					if(memberPointHistoryFindResponse.getResult() != null){
						if(score_array ==null){
							score_array = new ArrayList<PointHistory>(memberPointHistoryFindResponse.getResult())  ;
						} else {
							score_array.addAll(memberPointHistoryFindResponse.getResult());
						}
						if( score_array.size() >= memberPointHistoryFindResponse.getTotalCount() ) {
							isLastpage = true;
						}
						m_scoreAdapter.notifyDataSetChanged();
					}
				} else if (msg.what == 0) {
					Toast.makeText(context, memberPointHistoryFindResponse.getErrors().get(0).getMessage(), Toast.LENGTH_SHORT).show();
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();

				MemberPointHistoryFindRequest memberPointHistoryFindRequest = new MemberPointHistoryFindRequest();
				memberPointHistoryFindRequest.setPageNumber(pageNumber);
				memberPointHistoryFindRequest.setPageSize(pageSize);
				try {
					MemberPointHistoryFindResponse memberPointHistoryFindResponse = AuthDao.client.execute(memberPointHistoryFindRequest, GlobalContext.getInstance().getSpUtil().getUserInfo());
					if (memberPointHistoryFindResponse.hasError()) {
						msg.what = 0;   // 登录失败
					} else {
						msg.what = 1;   // 登录成功
					}
					msg.obj = memberPointHistoryFindResponse;
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
