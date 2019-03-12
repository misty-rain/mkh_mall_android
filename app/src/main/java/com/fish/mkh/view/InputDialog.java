package com.fish.mkh.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.mkh.mobilemall.R;
import com.fish.mkh.util.UIUtil;

public class InputDialog extends Dialog {

	private Context mContext;
	private TextView tvNum, tvCan, tv_warn;
	private EditText et_input; 
	MyCallInterface callback;
	boolean touched = false;

	public interface MyCallInterface {  
		public void setamount(String amount);  
	} 
	
	public InputDialog(Context context,MyCallInterface callback) {
		super(context, R.style.commonDialog);
		mContext = context;
		this.callback = callback;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_input);
		setCanceledOnTouchOutside(true);
		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		lp.width = (int) (UIUtil.getScreenWidth(getContext())*0.8);
		lp.dimAmount = 0.6f;
		this.getWindow().setAttributes(lp);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		et_input = (EditText)findViewById(R.id.rca_input_et);
		tvNum = (TextView) findViewById(R.id.tv_ok);
		tvCan = (TextView) findViewById(R.id.tv_cancel);
		tv_warn = (TextView) findViewById(R.id.di_warning);

		tvNum.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String amount = et_input.getText().toString();
				if(Integer.valueOf(amount)%100 != 0){
					tv_warn.setVisibility(View.VISIBLE);
					return;
				}
				callback.setamount(amount);
				dismiss();
			}
		});
		tvCan.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				callback.setamount("0");
				dismiss();
			}
		});
		
		this.setCanceledOnTouchOutside(false);
		
		et_input.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				
				String temp = et_input.getText().toString();

				if(temp.equals("0")) {
					et_input.getText().delete(0, 1);
				}
				
				int posDot = temp.indexOf(".");
				if (posDot <= 0) 
					return;
				if (temp.length() - posDot - 1 > 2)
				{
					et_input.getText().delete(posDot + 3, posDot + 4);
				}
			}
		});

//		this.setOnDismissListener(new OnDismissListener() {
//
//			@Override
//			public void onDismiss(DialogInterface arg0) {
//				if(!touched){
//					callback.setamount("0");
//				}
//
//			}
//		});
	}

}
