package com.fish.mkh;

import com.fish.mkh.client.AuthDao;
import com.fish.mkh.div.MkhTitleBar;
import com.fish.mkh.util.UIUtil;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.support.appconfig.AppManager;
import com.mkh.mobilemall.ui.activity.auth.LoginActivity;
import com.mkh.mobilemall.ui.base.BaseActivity;
import com.xiniunet.api.request.system.LoginPasswordModifyRequest;
import com.xiniunet.api.request.system.MemberUserUpdateRequest;
import com.xiniunet.api.response.system.LoginPasswordModifyResponse;
import com.xiniunet.api.response.system.MemberUserUpdateResponse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.mkh.mobilemall.R;

public class PassWordActivity extends BaseActivity {

	Context context;
	MkhTitleBar mTitleBar;
	private EditText et_password;
	private EditText et_newpassword;
	private EditText et_confirmpassword;
	private Button bt_ok;
	private  boolean isPwdSame=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pass_word);
		context = this;
		initTileBar();
		initControl();
	}

	public void initTileBar() {
		mTitleBar = (MkhTitleBar) findViewById(R.id.passwordactivity_actionbar);
		mTitleBar.setTitle(getResources().getString(R.string.title_activity_pass_word));
		//mTitleBar.setLeftClickFinish(this);
		Intent intent = new Intent();


		mTitleBar.setClickFinish(this,intent);
	}
	
	private void initControl() {
		et_password = (EditText)findViewById(R.id.passwordactivity_password_edittext);
		et_newpassword = (EditText)findViewById(R.id.passwordactivity_newpassword_edittext);
		et_confirmpassword = (EditText)findViewById(R.id.passwordactivity_confirmpassword_edittext);
		bt_ok = (Button)findViewById(R.id.passwordactivity_ok_button);

	/*	et_newpassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){
					String m_newpassword = et_newpassword.getText().toString();
					if(!m_newpassword.equals("")) {
						if (m_newpassword.equals(GlobalContext.getInstance().getSpUtil().getpassWord())) {

							isPwdSame=true;
							Toast.makeText(context, getResources().getString(R.string.newpassword_not_defficent), Toast.LENGTH_SHORT).show();
							et_newpassword.setText("");

							*//*et_newpassword.requestFocus();*//*

							return;
						}

						isPwdSame=false;
					}
				}
			}
		});*/
		
		bt_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String m_password = et_password.getText().toString();
				String m_newpassword = et_newpassword.getText().toString();
				String m_confirmpassword = et_confirmpassword.getText().toString();
				if(m_password.equals("")
						||m_newpassword.equals("") 
						||m_confirmpassword.equals("")) {
					Toast.makeText(context, getResources().getString(R.string.passwordactivity_toast_noinput), Toast.LENGTH_SHORT).show();
				} else {

					/*if(isPwdSame){
						Toast.makeText(context, getResources().getString(R.string.newpassword_not_defficent), Toast.LENGTH_SHORT).show();
						et_newpassword.setText("");
						et_confirmpassword.setText("");

						return;
					}*/

					if(!m_newpassword.equals(m_confirmpassword)) {
						Toast.makeText(context, getResources().getString(R.string.passwordactivity_toast_inconform), Toast.LENGTH_SHORT).show();
					}else {
						LoginPasswordModify(m_password, m_newpassword);
					}

				}

			}
		});
	}
	
	private void LoginPasswordModify(final String oldLoginPassword, final String loginPassword) {

		UIUtil.showWaitDialog(PassWordActivity.this);
		
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				UIUtil.dismissDlg();
				
				if (msg.what == 2){
					String s = (String) msg.obj;
					Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
					GlobalContext.getInstance().getSpUtil().saveUserInfo(null);
					GlobalContext.getInstance().getSpUtil().setDefaultReceiveAddress("");
					Intent intent=new Intent(PassWordActivity.this, LoginActivity.class);
					startActivity(intent);
					finish();

					return;
				}
				LoginPasswordModifyResponse loginPasswordModifyResponse = (LoginPasswordModifyResponse) msg.obj;
				if (msg.what == 1) {
					Toast.makeText(context, "密码修改成功", Toast.LENGTH_SHORT).show();
					GlobalContext.getInstance().getSpUtil().saveUserInfo(null);
					GlobalContext.getInstance().getSpUtil().setDefaultReceiveAddress("");
					Intent intent=new Intent(PassWordActivity.this, LoginActivity.class);
					startActivity(intent);
					PassWordActivity.this.finish();

				} else if (msg.what == 0) {
					Toast.makeText(context, loginPasswordModifyResponse.getErrors().get(0).getMessage(), Toast.LENGTH_SHORT).show();
				} 
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();

				LoginPasswordModifyRequest loginPasswordModifyRequest = new LoginPasswordModifyRequest();
				loginPasswordModifyRequest.setOldLoginPassword(oldLoginPassword);
				loginPasswordModifyRequest.setLoginPassword(loginPassword);

				try {
					LoginPasswordModifyResponse loginPasswordModifyResponse = AuthDao.client.execute(loginPasswordModifyRequest, GlobalContext.getInstance().getSpUtil().getUserInfo());
					if (loginPasswordModifyResponse.hasError()) {
						msg.what = 0;   // 登录失败
					} else {
						msg.what = 1;   // 登录成功
					}
					msg.obj = loginPasswordModifyResponse;
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
