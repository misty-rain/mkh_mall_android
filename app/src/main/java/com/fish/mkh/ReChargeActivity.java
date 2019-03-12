package com.fish.mkh;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.alipay.sdk.app.PayTask;
import com.fish.mkh.div.MkhTitleBar;
import com.fish.mkh.util.SharedPreferencesUtil;
import com.fish.mkh.util.UIUtil;
import com.fish.mkh.view.CommitDialog;
import com.fish.mkh.view.InputDialog;
import com.fish.mkh.view.InputDialog.MyCallInterface;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.bean.BackResultBean;
import com.mkh.mobilemall.dao.OrderDao;
import com.mkh.mobilemall.support.alipay.PayResult;
import com.mkh.mobilemall.ui.activity.main.MainActivity;
import com.mkh.mobilemall.ui.base.BaseActivity;
import com.mkh.mobilemall.ui.widget.TipsToast;
import com.xiniunet.api.domain.ecommerce.PayMethodEnum;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.test.UiThreadTest;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ReChargeActivity extends BaseActivity {

	Context context;
	MkhTitleBar mTitleBar;
	private ArrayList<Button> buttonlist;
	private LinearLayout costnumlayout;
	private RelativeLayout costlayout;
	private TextView tv_cost;
	private int buttonselected = 0;
	private int payselected = 0;

	private TextView tv_recharge;
	private ImageView iv_recharge,imgArrow;
	private Button bt_recharge;

	private double selectedAmount = 100.00;
	TipsToast tipsToast;
	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_CHECK_FLAG = 2;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_re_charge);
		context = this;
		initTileBar();
		initControl();
	}

	public void initTileBar() {
		mTitleBar = (MkhTitleBar) findViewById(R.id.rechargeactivity_actionbar);
		mTitleBar.setTitle(getResources().getString(R.string.title_activity_re_charge));
		mTitleBar.setLeftClickFinish(this);
	}

	public static Double[] selectedamounts = {100.00,200.00,500.00,1000.00};

	public static int[] buttonId = {R.id.btnone,R.id.btntwo,R.id.btnfive,R.id.btnoneqian,
		R.id.rca_button5};

	public static int[] paymethod = {R.mipmap.ic_small_payment1,R.mipmap.ic_small_payment2,R.mipmap.ic_small_payment3,
		R.mipmap.ic_small_payment4,R.mipmap.ic_small_payment5,R.mipmap.ic_small_payment6};

	public static String[] paymethod_text = {"支付宝","微信支付","招商银行","中国银行","中国工商银行","中国建设银行"};

	private void initControl() {
		tv_cost =  (TextView)findViewById(R.id.rca_cost_tv);
		imgArrow= (ImageView) findViewById(R.id.rca_more_img);

		buttonlist = new ArrayList<Button>();
		for(int i=0 ; i<buttonId.length ;i++) {
			Button button = (Button)findViewById(buttonId[i]);
			buttonlist.add(button);
			button.setOnClickListener(buttonclick);
		}

		buttonlist.get(buttonselected).setBackgroundResource(R.drawable.cost_btn_seldown);
		tv_cost.setText(buttonlist.get(buttonselected).getText());
		setAmount(0);

		RelativeLayout paymentlayout = (RelativeLayout)findViewById(R.id.rechargeactivity_recharge);
		paymentlayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				/*Intent intent=new Intent(ReChargeActivity.this,PayListActivity.class);
				Bundle bundle=new Bundle();
				bundle.putInt(SharedPreferencesUtil.PAY_INDEX, payselected);
				intent.putExtras(bundle);
				startActivity(intent);*/
			Toast.makeText(ReChargeActivity.this,getString(R.string.support_alipay),Toast.LENGTH_SHORT).show();
			}
		});

		costlayout = (RelativeLayout)findViewById(R.id.rechargeactivity_cost);
		costnumlayout = (LinearLayout)findViewById(R.id.rechargeactivity_costnum);

		costlayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(costnumlayout.getVisibility() == View.VISIBLE) {
					costnumlayout.setVisibility(View.GONE);
					imgArrow.setImageResource(R.mipmap.icon_down);
				}else {
					costnumlayout.setVisibility(View.VISIBLE);
					imgArrow.setImageResource(R.mipmap.icon_up);
				}

			}
		});

		tv_recharge = (TextView)findViewById(R.id.rca_recharge_tv);
		iv_recharge = (ImageView)findViewById(R.id.rca_recharge_img);

		payselected = SharedPreferencesUtil.getIntValue(this, SharedPreferencesUtil.PAY_INDEX, 0);
		tv_recharge.setText(paymethod_text[payselected]);
		iv_recharge.setImageResource(paymethod[payselected]);

		bt_recharge = (Button)findViewById(R.id.rca_recharge_bt);
		bt_recharge.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				UIUtil.showWaitDialog(ReChargeActivity.this);
				getSignInfo(selectedAmount, PayMethodEnum.ALIPAY);
			}
		});
	}

	protected void onResume() {
		super.onResume();

		payselected = SharedPreferencesUtil.getIntValue(this, SharedPreferencesUtil.PAY_INDEX, 0);
		tv_recharge.setText(paymethod_text[payselected]);
		iv_recharge.setImageResource(paymethod[payselected]);
	};

	OnClickListener buttonclick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {

			for(int i = 0; i<buttonId.length; i++){
				if(buttonId[i] == arg0.getId()){
					buttonselected = i;
					buttonlist.get(i).setBackgroundResource(R.drawable.cost_btn_seldown);

					if(buttonselected == 4){
						new InputDialog(context, new MyCallInterface() {

							@Override
							public void setamount(String amount) {
								if(amount.equals("0")){
									setDefault();
								}else{
									tv_cost.setText(amount+"元");
									setAmount(Double.parseDouble(amount));
								}
							}
						}).show();
					}else {
						tv_cost.setText(buttonlist.get(buttonselected).getText());
						setAmount(0);
					}
				}else {
					buttonlist.get(i).setBackgroundResource(R.drawable.cost_btn_sel);
				}
			}
		}
	};

	private void setDefault(){
		buttonselected = 0;
		for(int i = 0; i<buttonId.length; i++){
			if(i == buttonselected){
				buttonlist.get(i).setBackgroundResource(R.drawable.cost_btn_seldown);
				tv_cost.setText(buttonlist.get(i).getText());
			}else {
				buttonlist.get(i).setBackgroundResource(R.drawable.cost_btn_sel);
			}
		}
		setAmount(0);
	}

	private void setAmount(double amount){
		switch (buttonselected) {
		case 0:
			selectedAmount = 100.00;
			break;
		case 1:
			selectedAmount = 200.00;
			break;
		case 2:
			selectedAmount = 500.00;
			break;
		case 3:
			selectedAmount = 1000.00;
			break;
		case 4:
			selectedAmount = amount;
			break;

		default:
			break;
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SDK_PAY_FLAG: {
					PayResult payResult = new PayResult((String) msg.obj);

					// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
					String resultInfo = payResult.getResult();

					String resultStatus = payResult.getResultStatus();

					// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
					if (TextUtils.equals(resultStatus, "9000")) {
						showTips(R.mipmap.tips_success, "充值成功");
						new Timer().schedule(new TimerTask() {
							@Override
							public void run() {

								Intent intent = new Intent(ReChargeActivity.this, MainActivity.class);
								startActivity(intent);
								finish();

							}
						}, 1000);
					} else {
						// 判断resultStatus 为非“9000”则代表可能支付失败
						// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
						if (TextUtils.equals(resultStatus, "8000")) {
							Toast.makeText(ReChargeActivity.this, "支付结果确认中",
									  Toast.LENGTH_SHORT).show();

						} else {
							// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
							Toast.makeText(ReChargeActivity.this, "充值失败",
									  Toast.LENGTH_SHORT).show();

						}
					}
					break;
				}
				case SDK_CHECK_FLAG: {
					Toast.makeText(ReChargeActivity.this, "检查结果为：" + msg.obj,
							  Toast.LENGTH_SHORT).show();
					break;
				}
				default:
					break;
			}
		}

		;
	};


	/**
	 * 提示信息
	 *
	 * @param iconResId
	 * @param
	 */
	private void showTips(int iconResId, String msg) {
		if (tipsToast != null) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				tipsToast.cancel();
			}
		} else {
			tipsToast = TipsToast.makeText(getApplication().getBaseContext(),
					  msg, TipsToast.LENGTH_SHORT);
		}
		tipsToast.show();
		tipsToast.setIcon(iconResId);
		tipsToast.setText(msg);
	}


	/**
	 *  得到签名信息
	 *
	 * @param
	 */
	private void getSignInfo(final double amount,final PayMethodEnum methodEnum) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {

				BackResultBean resultBean = (BackResultBean) msg.obj;
				if (msg.what == 1) {
					UIUtil.showWaitDialog(ReChargeActivity.this);
					ToAliPay(resultBean.getKeyStr());
				} else if (msg.what == 0) {
					UIUtil.dismissDlg();
					showTips(R.mipmap.tips_error, resultBean.getMessage());
				} else {
					UIUtil.dismissDlg();
					showTips(R.mipmap.tips_error, getString(R.string.signorderexception));
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				BackResultBean resultBean = OrderDao.assembleRechargeSignInfo(amount,methodEnum);
				try {
					if (resultBean.getCode().equals("1")) {
						msg.what = 1;
						msg.obj = resultBean;
					} else {
						msg.what = 0;
						msg.obj = resultBean;
					}
					handler.sendMessage(msg);
				} catch (Exception e) {
				}

			}
		}.start();
	}

	/**
	 * call alipay sdk pay. 调用SDK支付
	 */
	public void ToAliPay(final String orderInfo) {


		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {

				// 构造PayTask 对象
				PayTask alipay = new PayTask(ReChargeActivity.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(orderInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};
		UIUtil.dismissDlg();

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();


	}
}
