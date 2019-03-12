package com.fish.mkh.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.mkh.mobilemall.R;
import com.fish.mkh.util.UIUtil;

public class CommitDialog extends Dialog {

	private Context mContext;
	private TextView tvNum, tvCan;
	private View.OnClickListener listener;

	public CommitDialog(Context context,View.OnClickListener listener) {
		super(context, R.style.commonDialog);
		mContext = context;
		this.listener = listener;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_commit);
		setCanceledOnTouchOutside(true);
		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		lp.width = (int) (UIUtil.getScreenWidth(getContext())*0.8);
		lp.dimAmount = 0.6f;
		this.getWindow().setAttributes(lp);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		tvNum = (TextView) findViewById(R.id.tv_ok);
		tvCan = (TextView) findViewById(R.id.tv_cancel);

		tvNum.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dismiss();
				listener.onClick(arg0);
			}
		});
		tvCan.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
		});
	}

}
