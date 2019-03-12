package com.fish.mkh.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.mkh.mobilemall.R;
import com.fish.mkh.div.wheel.widget.OnWheelChangedListener;
import com.fish.mkh.div.wheel.widget.WheelView;
import com.fish.mkh.div.wheel.widget.adapters.ArrayWheelAdapter;
import com.fish.mkh.util.UIUtil;

public class BottomAlertDialog extends Dialog implements OnWheelChangedListener {
	
	private Button btNegative, btPositive;
	private WheelView mViewCity;
	private String[] source ;
	private Context mContext;
	private View tv;
	
	public BottomAlertDialog(Context context) {
		super(context, R.style.commonDialog);
		mContext = context;
	}
	
	public BottomAlertDialog(Context context, View view,String[] str) {
		super(context, R.style.commonDialog);
		mContext = context;
		source = str;
		tv = view;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_bottom_pop);
		setCanceledOnTouchOutside(true);
		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		lp.width = UIUtil.getScreenWidth(getContext());
		lp.gravity = Gravity.BOTTOM;
		lp.dimAmount = 0.6f;
		this.getWindow().setAttributes(lp);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		btNegative = (Button) findViewById(R.id.cancel);
		btPositive = (Button) findViewById(R.id.submit);
		
		btNegative.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		btPositive.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				((TextView)tv).setText(source[mViewCity.getCurrentItem()]);
				dismiss();
			}
		});
		
		mViewCity = (WheelView) findViewById(R.id.id_center);
		mViewCity.addChangingListener(this);
		mViewCity.setVisibleItems(7);
		
		mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(mContext, source));
		mViewCity.setCurrentItem(0);
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		// TODO Auto-generated method stub
		
	}
	
}
