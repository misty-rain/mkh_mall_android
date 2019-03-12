package com.fish.mkh.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.mkh.mobilemall.R;
import com.fish.mkh.util.UIUtil;

public class DialAlertDialog extends Dialog {

	private Context mContext;
	private TextView tvNum;
	private Button btCancel,btOk;
	private String phone;

	public DialAlertDialog(Context context) {
		super(context, R.style.Dialog);
		mContext = context;
	}

	public DialAlertDialog(Context context, String number) {
		super(context, R.style.Dialog);
		mContext = context;
		phone = number;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_serversdial);
		setCanceledOnTouchOutside(true);
		//WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		//lp.width = UIUtil.getScreenWidth(getContext());
		//lp.gravity = Gravity.CENTER_HORIZONTAL;
		//lp.dimAmount = 0.6f;
		//this.getWindow().setAttributes(lp);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		tvNum = (TextView) findViewById(R.id.tv_number);
		btCancel = (Button) findViewById(R.id.bt_cancel);
		btOk = (Button) findViewById(R.id.bt_ok);
		tvNum.setText("确定拨打电话："+phone+"吗？");
		btOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dismiss();
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
				mContext.startActivity(intent);
			}
		});
		btCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
		});
	}

}
