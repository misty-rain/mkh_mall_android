package com.fish.mkh;

import com.fish.mkh.div.MkhTitleBar;
import com.fish.mkh.util.SharedPreferencesUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.ui.base.BaseActivity;

public class PayListActivity extends BaseActivity {

	Context context;
	MkhTitleBar mTitleBar;
	private ListView lv_paylist;
	private paylistAdapter m_paylistAdapter;
	private int selected = 0;//0,zhifubao 1.weixin
	private RelativeLayout rl_zhifubao;
	private RelativeLayout rl_weixin;
	private RelativeLayout rl_yinlian;
	private ImageView iv_zhifubao_checked;
	private ImageView iv_weixin_checked;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay_list);
		Bundle bundle = getIntent().getExtras();  
		if(bundle!=null){
			selected = bundle.getInt(SharedPreferencesUtil.PAY_INDEX, 0);
		}
		context = this;
		initTileBar();
		initControl();
	}
	
	public void initTileBar() {
		mTitleBar = (MkhTitleBar) findViewById(R.id.paylistactivity_actionbar);
		mTitleBar.setTitle(getResources().getString(R.string.title_activity_pay_list));
		mTitleBar.setLeftClickFinish(this);
	}
	
	private void initControl() {
		lv_paylist = (ListView)findViewById(R.id.paylistactivity_listview);
		
		m_paylistAdapter = new paylistAdapter(); //创建适配器   
		lv_paylist.setAdapter(m_paylistAdapter);   
		
		lv_paylist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				selected = arg2+2;
				m_paylistAdapter.notifyDataSetChanged();
				SharedPreferencesUtil.setIntValue(context, SharedPreferencesUtil.PAY_INDEX, selected);
				finish();
			}
		});
		
		rl_zhifubao = (RelativeLayout)findViewById(R.id.pl_rl_zhifubao);
		rl_weixin = (RelativeLayout)findViewById(R.id.pl_rl_weixin);
		rl_yinlian = (RelativeLayout)findViewById(R.id.pl_rl_yinlian);
		
		rl_yinlian.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(lv_paylist.getVisibility() == View.INVISIBLE){
					lv_paylist.setVisibility(View.VISIBLE);
				}else {
					lv_paylist.setVisibility(View.INVISIBLE);
				}
			}
		});
		
		rl_zhifubao.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				selected = 0;
				SharedPreferencesUtil.setIntValue(context, SharedPreferencesUtil.PAY_INDEX, selected);
				finish();
			}
		});
		
		rl_weixin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				selected = 1;
				SharedPreferencesUtil.setIntValue(context, SharedPreferencesUtil.PAY_INDEX, selected);
				finish();
			}
		});
		
		iv_zhifubao_checked = (ImageView)findViewById(R.id.pl_iv_zhifubao);
		iv_weixin_checked = (ImageView)findViewById(R.id.pl_iv_weixin);
		if(selected == 0) {
			iv_zhifubao_checked.setVisibility(View.VISIBLE);
		}else if(selected == 1){
			iv_weixin_checked.setVisibility(View.VISIBLE);
		}
	}
	
	public static int[] paymethod = {R.mipmap.ic_payment3,
		R.mipmap.ic_payment4,R.mipmap.ic_payment5,R.mipmap.ic_payment6};
	
	public final class ListItemView{   
		public ImageView iv_content;
		public ImageView iv_selected;
	}  
	public class paylistAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return paymethod.length;
		}

		@Override
		public Object getItem(int position) {

			return paymethod[position];
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
				convertView = LayoutInflater.from(context).inflate(R.layout.adapter_payment, null);   
				//获取控件对象   
				listItemView.iv_content = (ImageView)convertView.findViewById(R.id.ap_payment_iv);   
				listItemView.iv_selected = (ImageView)convertView.findViewById(R.id.ap_selected_iv);   

				//设置控件集到convertView   
				convertView.setTag(listItemView);   
			}else {   
				listItemView = (ListItemView)convertView.getTag();   
			}   
			
			if( (selected-2) == position) {
				listItemView.iv_selected.setVisibility(View.VISIBLE);
			}else{
				listItemView.iv_selected.setVisibility(View.INVISIBLE);
			}
			listItemView.iv_content.setImageResource(paymethod[position]);
			return convertView; 
		}
	}
}
