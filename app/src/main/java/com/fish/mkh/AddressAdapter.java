package com.fish.mkh;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.app.GlobalContext;
import com.xiniunet.api.domain.master.Location;

public class AddressAdapter extends BaseAdapter {
	Context mContext;


	public AddressAdapter(Context context) {
		mContext = context;
	}

	private List<Location> datas = new ArrayList<Location>();

	public void setData(List<Location> list) {	
		datas = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return datas.size();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final Location bean = datas.get(position);
		View view = null;
		ViewHolder holder = new ViewHolder();
		if (convertView == null) {
			view = View.inflate(mContext, R.layout.adapt_address_item, null);
			holder.cbDefault = (CheckBox)view.findViewById(R.id.cb_default);
			holder.tvAddress = (TextView)view.findViewById(R.id.tv_address);
			holder.tvName = (TextView)view.findViewById(R.id.tv_name);
			holder.tvPhone = (TextView)view.findViewById(R.id.tv_phone);
			
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		holder.cbDefault.setVisibility(bean.getIsDefault()?View.VISIBLE:View.GONE);
		String airqColor = "<font color=\"#DC0017\">";// 紫色
		String airqStr = "[默认]";
		StringBuilder adress = new StringBuilder();
		String addressDetail = "收货地址："+bean.getAreaInfo()+" "+bean.getAddress();
		adress.append(bean.getIsDefault()?airqColor + airqStr
				+ "</font>" +addressDetail:addressDetail);
		holder.tvAddress.setText(android.text.Html.fromHtml(adress
				.toString()));
		holder.tvName.setText(bean.getContactName());
		holder.tvPhone.setText(bean.getContactPhone());
		 if(bean.getIsDefault()){
			 List<String> defaultAddressList=new ArrayList<String>();
			 defaultAddressList.add(bean.getContactName());
			 defaultAddressList.add(bean.getContactPhone());
			 defaultAddressList.add(bean.getAreaInfo()+bean.getAddress());
			 defaultAddressList.add(String.valueOf(bean.getId()));
			 GlobalContext.getInstance().getSpUtil().setDefaultReceiveAddress(JSON.toJSONString(defaultAddressList));
		 }
		
		return view;
	}

	public Object getItem(int position) {
		return datas.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	class ViewHolder {
		TextView tvName, tvPhone, tvAddress;
		CheckBox cbDefault;
	}
}