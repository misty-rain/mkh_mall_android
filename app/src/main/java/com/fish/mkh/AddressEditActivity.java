package com.fish.mkh;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fish.mkh.client.AuthDao;
import com.fish.mkh.div.MkhTitleBar;
import com.fish.mkh.util.UIUtil;
import com.fish.mkh.view.AddressAlertDialog;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.ui.base.BaseActivity;
import com.mkh.mobilemall.utils.SharePreferenceUtil;
import com.xiniunet.api.domain.system.Passport;
import com.xiniunet.api.request.ecommerce.ItemEvaluationCreateRequest;
import com.xiniunet.api.request.master.LocationCreateRequest;
import com.xiniunet.api.request.master.LocationDeleteRequest;
import com.xiniunet.api.request.master.LocationSetDefaultRequest;
import com.xiniunet.api.request.master.LocationUpdateRequest;
import com.xiniunet.api.response.ecommerce.ItemEvaluationCreateResponse;
import com.xiniunet.api.response.master.LocationCreateResponse;
import com.xiniunet.api.response.master.LocationDeleteResponse;
import com.xiniunet.api.response.master.LocationSetDefaultResponse;
import com.xiniunet.api.response.master.LocationUpdateResponse;
import com.mkh.mobilemall.R;

public class AddressEditActivity extends BaseActivity {

	MkhTitleBar mTitleBar;
	String sheng,shi,qu;
	EditText etName,etPhone,etAddress;
	TextView tvDelete,tvDefult,tvAddressSelect,txtdisplayprovonce;
	LinearLayout lyAddress;
	boolean isNewAdd,isDefault;
	private Handler handler;
	private Long id;
	Passport passport;
	SharePreferenceUtil sharePreferenceUtil;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_edit);
		passport=GlobalContext.getInstance().getSpUtil().getUserInfo();
		isNewAdd = getIntent().getBooleanExtra("isNewAdd", true);
		isDefault = getIntent().getBooleanExtra("isDefault", false);
		sharePreferenceUtil=GlobalContext.getInstance().getSpUtil();
	
		id = getIntent().getLongExtra("id", 0);
		initViews();
		initTileBar();
	}

	private void initViews() {
		tvAddressSelect = (TextView)findViewById(R.id.tv_address);
		tvDelete = (TextView)findViewById(R.id.tv_delete);
		tvDefult = (TextView)findViewById(R.id.tv_default);
		etName = (EditText)findViewById(R.id.et_name);
		etPhone = (EditText)findViewById(R.id.et_phone);
		etAddress = (EditText)findViewById(R.id.et_address);
		lyAddress = (LinearLayout)findViewById(R.id.ly_address);
		txtdisplayprovonce= (TextView) findViewById(R.id.txtdisplayprovonce);
 
		
		lyAddress.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new AddressAlertDialog(AddressEditActivity.this, sheng, shi, qu,tvAddressSelect).show();
			}
		});
		if(!isNewAdd){
			String name = getIntent().getStringExtra("name");
			String phone = getIntent().getStringExtra("phone");
			String address = getIntent().getStringExtra("address");
			String addressTag = getIntent().getStringExtra("addressTag");
			String detailAddress = getIntent().getStringExtra("detailAddress");
			
			tvAddressSelect.setText(address);
			etName.setText(name);
			etPhone.setText(phone);
			etAddress.setText(detailAddress);
			tvAddressSelect.setTag(addressTag);
			txtdisplayprovonce.setText("所在地区");
			txtdisplayprovonce.setTextColor(getResources().getColor(R.color.color_txt_grey));
		}else{
			txtdisplayprovonce.setText("省、市、区");
		}
		
		tvDefult.setVisibility(isNewAdd?View.GONE:isDefault?View.GONE:View.VISIBLE);
		tvDelete.setVisibility(isNewAdd?View.GONE:View.VISIBLE);

		tvDelete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setDelete();
			}
		});
		
		tvDefult.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setDefault();
			}
		});
		
	}

	public void initTileBar() {
		mTitleBar = (MkhTitleBar) findViewById(R.id.title_setting);
		mTitleBar.setTitle(getResources().getString(isNewAdd?R.string.addressEdit_title_new:R.string.addressEdit_title_update));

		mTitleBar.setRightTextButton(isNewAdd?"保存":"修改", new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				if(etName.getText().toString() == null || etName.getText().toString().trim().equals("")){
                	Toast.makeText(AddressEditActivity.this, "请输入收货人姓名", Toast.LENGTH_SHORT).show();
                	return;
				}
				
				if(etPhone.getText().toString() == null || etPhone.getText().toString().trim().equals("")){
                	Toast.makeText(AddressEditActivity.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                	return;
				}
				
				if(tvAddressSelect.getText().toString() == null || tvAddressSelect.getText().toString().trim().equals("")){
                	Toast.makeText(AddressEditActivity.this, "请输入省、市、区", Toast.LENGTH_SHORT).show();
                	return;
				}
				
				if(etAddress.getText().toString() == null || etAddress.getText().toString().trim().equals("")){
                	Toast.makeText(AddressEditActivity.this, "请输入详细地址", Toast.LENGTH_SHORT).show();
                	return;
				}

				if(etPhone.getText().toString().length()<11){
					Toast.makeText(AddressEditActivity.this, "您输入的手机号码格式不正确", Toast.LENGTH_SHORT).show();
					return;
				}
				
				
				UIUtil.showWaitDialog(AddressEditActivity.this);
				handler = new Handler() {
			        public void handleMessage(Message msg) {
			        	UIUtil.dismissDlg();
			        	if(msg.what == 2 || msg.what == 4){
							Toast.makeText(AddressEditActivity.this, isNewAdd?"保存成功":"修改成功",
									Toast.LENGTH_SHORT).show();
							setResult(1);
							AddressEditActivity.this.finish();
			        	}else if(msg.what == 1){
			        		LocationCreateResponse memRsp = (LocationCreateResponse)msg.obj;
		                	Toast.makeText(AddressEditActivity.this, memRsp.getErrors().get(0).getMessage(), Toast.LENGTH_SHORT).show();
			        	}else if(msg.what == 3){
			        		LocationUpdateResponse memRsp = (LocationUpdateResponse)msg.obj;
		                	Toast.makeText(AddressEditActivity.this, memRsp.getErrors().get(0).getMessage(), Toast.LENGTH_SHORT).show();
			        	}
			        }
				};
				
				new Thread() {
		            public void run() {
		                Message msg = new Message();
		                
		                if(isNewAdd){
		                	LocationCreateRequest msgReq = new LocationCreateRequest();
							msgReq.setId(passport.getId());
		                	msgReq.setContactName(etName.getText().toString());
		                	msgReq.setContactPhone(etPhone.getText().toString());
		                	
		                	String[] address = ((String)tvAddressSelect.getTag()).split(";");
		                	msgReq.setProvinceName(address[0]);
		                	msgReq.setCityName(address[1]);
		                	msgReq.setDistrictName(address[2]);
//							msgReq.setAreaInfo(address[0]+" "+address[1]+" "+address[2]);
		                	msgReq.setAddress(etAddress.getText().toString());
		                	
			                try {
			                	LocationCreateResponse msgRsp = AuthDao.client.execute(msgReq, passport);
			                    if (msgRsp.hasError()) {
			                        msg.what = 1;   // 获取失败
			                    } else {
			                        msg.what = 2;   // 成功
			                    }
			                    msg.obj = msgRsp;
			                    handler.sendMessage(msg);
			                } catch (Exception e) {
			                }
		                }else{
		                	LocationUpdateRequest msgReq = new LocationUpdateRequest();
		                	msgReq.setId(id);
		                	msgReq.setContactName(etName.getText().toString());
		                	msgReq.setContactPhone(etPhone.getText().toString());
		                	
		                	String[] address = ((String)tvAddressSelect.getTag()).split(";");
							if(null == address || address.length<2){

							}else{
								msgReq.setProvinceName(address[0]);
								msgReq.setCityName(address[1]);
								msgReq.setDistrictName(address[2]);
							}
		                	msgReq.setAddress(etAddress.getText().toString());   
			                try {
			                	LocationUpdateResponse msgRsp = AuthDao.client.execute(msgReq,passport);
			                    if (msgRsp.hasError()) {
			                        msg.what = 3;   // 获取失败
			                    } else {
			                        msg.what = 4;   // 成功
			                    }
			                    msg.obj = msgRsp;
			                    handler.sendMessage(msg);
			                } catch (Exception e) {
			                }
		                }
		                
		            }
		        }.start();

			}

			
		});
		
		mTitleBar.setLeftClickFinish(this);
		
	}
	
	private void setDefault(){
		UIUtil.showWaitDialog(AddressEditActivity.this);
		handler = new Handler() {
	        public void handleMessage(Message msg) {
	        	UIUtil.dismissDlg();
	        	if(msg.what == 2){
					Toast.makeText(AddressEditActivity.this, "设置成功",
							Toast.LENGTH_SHORT).show();


					setResult(1);
					AddressEditActivity.this.finish();
	        	}else if(msg.what == 1){
	        		LocationSetDefaultResponse memRsp = (LocationSetDefaultResponse)msg.obj;
                	Toast.makeText(AddressEditActivity.this, memRsp.getErrors().get(0).getMessage(), Toast.LENGTH_SHORT).show();
	        	}
	        }
		};
		
		new Thread() {
            public void run() {
                Message msg = new Message(); 

                LocationSetDefaultRequest msgReq = new LocationSetDefaultRequest();
                msgReq.setId(id);
                try {
                	LocationSetDefaultResponse msgRsp = AuthDao.client.execute(msgReq,passport);
                    if (msgRsp.hasError()) {
                        msg.what = 1;   // 获取失败
                    } else {
                        msg.what = 2;   // 成功
                    }
                    msg.obj = msgRsp;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                }
            
                
            }
        }.start();
	}
	
	private void setDelete(){
		UIUtil.showWaitDialog(AddressEditActivity.this);
		handler = new Handler() {
	        public void handleMessage(Message msg) {
	        	UIUtil.dismissDlg();
	        	if(msg.what == 2){
					Toast.makeText(AddressEditActivity.this, "删除成功",
							Toast.LENGTH_SHORT).show();
					if(isDefault)
						sharePreferenceUtil.setDefaultReceiveAddress("");
					setResult(1);
					AddressEditActivity.this.finish();
	        	}else if(msg.what == 1){
	        		LocationDeleteResponse memRsp = (LocationDeleteResponse)msg.obj;
                	Toast.makeText(AddressEditActivity.this, memRsp.getErrors().get(0).getMessage(), Toast.LENGTH_SHORT).show();
	        	}
	        }
		};
		
		new Thread() {
            public void run() {
                Message msg = new Message(); 

                LocationDeleteRequest msgReq = new LocationDeleteRequest();
                msgReq.setId(id);
                try {
                	LocationDeleteResponse msgRsp = AuthDao.client.execute(msgReq,passport);
                    if (msgRsp.hasError()) {
                        msg.what = 1;   // 获取失败
                    } else {
                        msg.what = 2;   // 成功
                    }
                    msg.obj = msgRsp;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                }
            
                
            }
        }.start();
	}
}
