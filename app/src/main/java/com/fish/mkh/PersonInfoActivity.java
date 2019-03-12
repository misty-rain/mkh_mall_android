package com.fish.mkh;

import java.util.Calendar;

import com.fish.mkh.client.AuthDao;
import com.fish.mkh.div.MkhTitleBar;
import com.fish.mkh.util.UIUtil;
import com.fish.mkh.view.BottomAlertDialog;
import com.fish.mkh.view.CommitDialog;
import com.fish.mkh.view.SelectBirthday;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.support.appconfig.AppManager;
import com.mkh.mobilemall.support.db.TempComCarDBTask;
import com.mkh.mobilemall.ui.activity.auth.LoginActivity;
import com.mkh.mobilemall.ui.base.BaseActivity;
import com.xiniunet.api.domain.master.Member;
import com.xiniunet.api.domain.system.MemberUser;
import com.xiniunet.api.domain.system.Passport;
import com.xiniunet.api.request.master.MemberGetRequest;
import com.xiniunet.api.request.master.MemberUpdateRequest;
import com.xiniunet.api.request.system.LogoutRequest;
import com.xiniunet.api.request.system.MemberUserGetRequest;
import com.xiniunet.api.request.system.MemberUserUpdateRequest;
import com.xiniunet.api.response.master.MemberGetResponse;
import com.xiniunet.api.response.master.MemberUpdateResponse;
import com.xiniunet.api.response.system.LogoutResponse;
import com.xiniunet.api.response.system.MemberUserGetResponse;
import com.xiniunet.api.response.system.MemberUserUpdateResponse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PersonInfoActivity extends BaseActivity {

	Context context;
	MkhTitleBar mTitleBar;
	EditText pia_name;
	TextView pia_phonenum;
	TextView pia_membercard;
	TextView pia_solidcard;
	EditText pia_sex;
	EditText pia_birthday;
	SelectBirthday birth;
	LinearLayout ll_pwdchange;
	LinearLayout ll_loginout;
	Passport passport;
	ImageView edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_info);
		passport= GlobalContext.getInstance().getSpUtil().getUserInfo();
		context = this;
		initTileBar();
		initControl();
	}

	public void initTileBar() {
		mTitleBar = (MkhTitleBar) findViewById(R.id.personinfoactivity_actionbar);
		mTitleBar.setTitle(getResources().getString(R.string.title_activity_person_info));
		mTitleBar.setLeftClickFinish(this);
		mTitleBar.setRightTextButton(getResources().getString(R.string.personinfoctivity_save), new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MemberUserUpdate(pia_sex.getText().toString(), pia_birthday.getText().toString(),pia_name.getText().toString());
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==1){

			pia_name.setEnabled(false);

		}

	}

	private void initControl() {
		pia_name = (EditText)findViewById(R.id.pia_name_et);
		pia_phonenum = (TextView)findViewById(R.id.pia_phonenum_tv);
		pia_membercard = (TextView)findViewById(R.id.pia_membercard_tv);
		pia_solidcard = (TextView)findViewById(R.id.pia_solidcard_tv);
        edit= (ImageView) findViewById(R.id.edit);
		pia_sex = (EditText)findViewById(R.id.pia_sex_tv);
		pia_sex.setEnabled(false);
		pia_name.setEnabled(false);
		edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mTitleBar.setkeboradClickFinish(PersonInfoActivity.this,pia_name);
				pia_name.setEnabled(true);
				pia_name.setSelection(pia_name.getText().toString().length());
				pia_name.requestFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(pia_name,InputMethodManager.SHOW_FORCED);

			}
		});

		pia_birthday = (EditText)findViewById(R.id.pia_birthday_tv);
		pia_birthday.setEnabled(false);


		String date;
		Calendar calendar = Calendar.getInstance();
		int mCurYear = calendar.get(Calendar.YEAR);
		int mCurMonth = calendar.get(Calendar.MONTH) +1;
		int mCurDay = calendar.get(Calendar.DATE);
		date = ""+ mCurYear + "-" + mCurMonth + "-"
				  + mCurDay;
		//pia_birthday.setText(date);

		/*pia_birthday.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				int inType = pia_birthday.getInputType(); // backup the input type
				pia_birthday.setInputType(InputType.TYPE_NULL); // disable soft input
				pia_birthday.onTouchEvent(arg1); // call native handler
				pia_birthday.setInputType(inType); // restore input type
				//				pia_birthday.setSelection(pia_birthday.getText().length());
				pia_birthday.setFocusable(false);

				return true;
			}
		});
		pia_birthday.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String date = pia_birthday.getText().toString();
				birth = new SelectBirthday(PersonInfoActivity.this,date);
				birth.setSourceControl(pia_birthday);
				birth.showAtLocation(PersonInfoActivity.this.findViewById(R.id.personinfo_layout),
						Gravity.BOTTOM, 0, 0);
			}
		});

		pia_sex.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				int inType = pia_sex.getInputType(); // backup the input type
				pia_sex.setInputType(InputType.TYPE_NULL); // disable soft input
				pia_sex.onTouchEvent(arg1); // call native handler
				pia_sex.setInputType(inType); // restore input type
				//				pia_sex.setSelection(pia_birthday.getText().length());
				pia_sex.setFocusable(false);

				return true;
			}
		});
		pia_sex.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String[] str = {"男","女"};
				new BottomAlertDialog(PersonInfoActivity.this,pia_sex,str).show();
			}
		});*/

		ll_pwdchange = (LinearLayout)findViewById(R.id.personinfo_pwdchange);
		ll_pwdchange.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				/*Intent intent=new Intent(PersonInfoActivity.this,PassWordActivity.class);
				startActivity(intent);*/
				startActivityForResult(new Intent(PersonInfoActivity.this, PassWordActivity.class), 1);
			}
		});

		ll_loginout = (LinearLayout)findViewById(R.id.personinfo_loginout);
		ll_loginout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new CommitDialog(PersonInfoActivity.this, new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						logOut();
					}
				}).show();
			}
		});

		memberUserGet((long) 1) ;
	}

	private void logOut() {

		UIUtil.showWaitDialog(PersonInfoActivity.this);

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				UIUtil.dismissDlg();
				if (msg.what == 2){
					String s = (String) msg.obj;
					Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
					return;
				}
				LogoutResponse logoutResponse = (LogoutResponse) msg.obj;
				if (msg.what == 1) {
					GlobalContext.getInstance().getSpUtil().saveUserInfo(null);
					GlobalContext.getInstance().getSpUtil().setDefaultReceiveAddress("");
					TempComCarDBTask.getInstance().removeTempCarCom(null);
					Intent intent=new Intent(PersonInfoActivity.this, LoginActivity.class);
					startActivity(intent);
					PersonInfoActivity.this.finish();


				} else if (msg.what == 0) {
					Toast.makeText(context, logoutResponse.getErrors().get(0).getMessage(), Toast.LENGTH_SHORT).show();
				} 
			}
		};

		new Thread() {
			public void run() {
				Message msg = new Message();

				LogoutRequest logOutRequest = new LogoutRequest();
				logOutRequest.setId(passport.getId());
				try {
					if (msg.what == 2){
						String s = (String) msg.obj;
						Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
						return;
					}

					LogoutResponse logOutResponse = AuthDao.client.execute(logOutRequest,passport);
					if (logOutResponse.hasError()) {
						msg.what = 0;   // 退出失败
					} else {
						msg.what = 1;   // 注销成功
					}
					msg.obj = logOutResponse;
					handler.sendMessage(msg);
				} catch (Exception e) {
					msg.what = 2;
					msg.obj = e.getMessage();
					handler.sendMessage(msg);
				}
			}
		}.start();
	}


	private void memberUserGet(final Long id) {

		UIUtil.showWaitDialog(PersonInfoActivity.this);

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				UIUtil.dismissDlg();
				if (msg.what == 2){
					String s = (String) msg.obj;
					Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
					return;
				}
				MemberUserGetResponse memberUserGetResponse = (MemberUserGetResponse) msg.obj;
				if (msg.what == 1) {
					MemberUser memberUser = memberUserGetResponse.getMemberUser();
					if(memberUser != null) {
						if(memberUser.getName() != null)
							pia_name.setText(memberUser.getName());
						if(memberUser.getMobilePhone() != null)
							pia_phonenum.setText(memberUser.getMobilePhone());
						if(memberUser.getMemberNumber() != null)
							pia_membercard.setText(memberUser.getMemberNumber());
						if(memberUser.getCardNumber() != null)
							pia_solidcard.setText(memberUser.getCardNumber());
						if(memberUser.getSex() != null)
							pia_sex.setText(memberUser.getSex());
						if(memberUser.getBirthYear() != null && memberUser.getBirthMonth() != null && memberUser.getBirthDate() != null)
						{
							String birthday= memberUser.getBirthYear() + "-" + memberUser.getBirthMonth() + "-"
									+ memberUser.getBirthDate();
							pia_birthday.setText(birthday);
						}

					}
				} else if (msg.what == 0) {
					Toast.makeText(context, memberUserGetResponse.getErrors().get(0).getMessage(), Toast.LENGTH_SHORT).show();
				} 
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();

				MemberUserGetRequest memberUserGetRequest = new MemberUserGetRequest();

				try {
					MemberUserGetResponse memberUserGetResponse = AuthDao.client.execute(memberUserGetRequest,passport);
					if (memberUserGetResponse.hasError()) {
						msg.what = 0;   // 登录失败
					} else {
						msg.what = 1;   // 登录成功
					}
					msg.obj = memberUserGetResponse;
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

	private void MemberUserUpdate(final String sex, final String birthday,final String userName) {

		UIUtil.showWaitDialog(PersonInfoActivity.this);

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				UIUtil.dismissDlg();

				if (msg.what == 2){
					String s = (String) msg.obj;
					Toast.makeText(context, s, Toast.LENGTH_SHORT).show();

					Intent intent=new Intent(PersonInfoActivity.this, MemberCenterActivity.class);
					startActivity(intent);
					finish();
					return;
				}
				MemberUserUpdateResponse memberUserUpdateResponse = (MemberUserUpdateResponse) msg.obj;
				if (msg.what == 1) {
					Toast.makeText(context, "更新用户信息成功", Toast.LENGTH_SHORT).show();
				} else if (msg.what == 0) {
					Toast.makeText(context, memberUserUpdateResponse.getErrors().get(0).getMessage(), Toast.LENGTH_SHORT).show();
				} 
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();

				MemberUserUpdateRequest memberUserUpdateRequest = new MemberUserUpdateRequest();
				memberUserUpdateRequest.setSex(sex);
				memberUserUpdateRequest.setName(userName);

				if (birthday != null && birthday.contains("-")) {
					String str[] = birthday.split("-");
					final int mCurYear = Integer.parseInt(str[0]);
					final int mCurMonth = Integer.parseInt(str[1]);
					final int mCurDay = Integer.parseInt(str[2]);

					memberUserUpdateRequest.setBirthYear(mCurYear);
					memberUserUpdateRequest.setBirthMonth(mCurMonth);
					memberUserUpdateRequest.setBirthDate(mCurDay);
				}

				try {
					MemberUserUpdateResponse memberUserUpdateResponse = AuthDao.client.execute(memberUserUpdateRequest,passport);
					if (memberUserUpdateResponse.hasError()) {
						msg.what = 0;   // 登录失败
					} else {
						msg.what = 1;   // 登录成功
					}
					msg.obj = memberUserUpdateResponse;
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
