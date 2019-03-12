package com.fish.mkh.div;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fish.mkh.GoodsCommentActivity;
import com.mkh.mobilemall.R;
import com.fish.mkh.util.ImageLoaderUtil;
import com.mkh.mobilemall.utils.MathUtils;
import com.xiniunet.api.domain.ecommerce.OrderLine;

public class GoodsItem extends RelativeLayout {

	private Context context;
	private OrderLine orderLine;
	ImageView ivIcon;
	TextView tvDesc,tvPrice,tvCount;
	
	
	public GoodsItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public GoodsItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public GoodsItem(Context context,OrderLine orderLine) {
		super(context);
		this.context = context;
		this.orderLine = orderLine;
		init(context);
		setDate();
	}
	/**
	 * 初始化
	 **/
	private void init(Context ct) {
		View.inflate(ct, R.layout.layout_goods_item, this);
		ivIcon = (ImageView) findViewById(R.id.ic_icon);
		tvDesc = (TextView) findViewById(R.id.tv_desc);
		tvPrice = (TextView) findViewById(R.id.tv_price);
		tvCount = (TextView) findViewById(R.id.tv_count);
		
	}
	
	private void setDate(){
//		ivIcon.setImageResource(resId)
		ImageLoaderUtil.loadImg(orderLine.getPicture(),
				ivIcon, context);
		tvDesc.setText(orderLine.getItemName());
		tvPrice.setText("¥ "+orderLine.getUnitPrice()+"/"+orderLine.getItemUom());
		tvCount.setText("x"+ MathUtils.formatDataForBackBigDecimal(orderLine.getQuantity()));
	}
	
}
