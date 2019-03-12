package com.fish.mkh;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.fish.mkh.bean.MyOrderbean;
import com.fish.mkh.bean.OrdersBean;
import com.fish.mkh.client.AuthDao;
import com.fish.mkh.div.GoodsItem;
import com.fish.mkh.div.MkhTitleBar;
import com.fish.mkh.util.UIUtil;
import com.fish.mkh.view.BottomAlertDialog;
import com.fish.mkh.view.RefundDialog;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.bean.BackResultBean;
import com.mkh.mobilemall.dao.OrderDao;
import com.mkh.mobilemall.dao.PointDao;
import com.mkh.mobilemall.support.alipay.PayResult;
import com.mkh.mobilemall.support.asyncTask.MyAsyncTask;
import com.mkh.mobilemall.support.db.TempComCarDBTask;
import com.mkh.mobilemall.ui.activity.main.MainActivity;
import com.mkh.mobilemall.ui.base.BaseActivity;
import com.mkh.mobilemall.ui.utils.OrderUtils;
import com.mkh.mobilemall.ui.utils.ShowToastUtils;
import com.mkh.mobilemall.ui.widget.TipsToast;
import com.mkh.mobilemall.utils.DialogUtil;
import com.mkh.mobilemall.utils.MathUtils;
import com.xiniunet.api.domain.ecommerce.DeliveryModeEnum;
import com.xiniunet.api.domain.ecommerce.Order;
import com.xiniunet.api.domain.ecommerce.OrderLine;
import com.xiniunet.api.domain.ecommerce.OrderStatusEnum;
import com.xiniunet.api.domain.ecommerce.PayMethodEnum;
import com.xiniunet.api.domain.system.Passport;
import com.xiniunet.api.request.ecommerce.OrderCloseRequest;
import com.xiniunet.api.request.ecommerce.OrderCommitRequest;
import com.xiniunet.api.request.ecommerce.OrderGetRequest;
import com.xiniunet.api.request.ecommerce.OrderRefundRequest;
import com.xiniunet.api.response.ecommerce.OrderCloseResponse;
import com.xiniunet.api.response.ecommerce.OrderGetResponse;
import com.xiniunet.api.response.ecommerce.OrderRefundResponse;
import com.mkh.mobilemall.R;

import butterknife.Bind;

public class OrderDetailActivity extends BaseActivity {

	MkhTitleBar mTitleBar;
	TextView tvState,tvPrice,tvPriceY,tvName,tvPhone,tvAddress,tvStoreName
		,tvCostY,tvTotalCount,tvTotalPrice,tvOrderNo,tvDeliType,tvisReceiv,tvBack,txtPaytype,tvsuumary;
	LinearLayout llContainer,llisReceive,llBack,llBackDetail,summary_linear,llnet;
	List<OrderLine> orderLineList;
	Button btSubmit,btCancel,reload;
	TextView tvBackState;
	boolean isBack = false;
	Handler handler ;
	private int GET_OK=1;
	private int GET_ERROR=0;
	private int CLOSE_OK = 2;
	private int CLOSE_ERROR = 3;
	private int REF_OK = 4;
	private int REF_ERROR = 5;
	Passport passport;
	private long orderid;

	TipsToast tipsToast;
	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_CHECK_FLAG = 2;
	Dialog paymentDialog;
	TextView txtCardBalances;
	EditText edtPayCode;
	String payKey=null;//微笑卡支付秘钥
	Long  orderId=0L; //orderid
	double smileCardBalance; //微笑卡余额

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_detail);
		passport= GlobalContext.getInstance().getSpUtil().getUserInfo();
		isBack = getIntent().getBooleanExtra("IS_BACK", false);
		orderid = getIntent().getLongExtra("orderid",0l);
		initViews();
		initTileBar();
		loadDatas();
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
						UIUtil.showWaitDialog(OrderDetailActivity.this);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						new getOrderStateByOrderId().execute(orderId);
					} else {
						// 判断resultStatus 为非“9000”则代表可能支付失败
						// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
						if (TextUtils.equals(resultStatus, "8000")) {
							Toast.makeText(GlobalContext.getInstance(), "支付结果确认中",
									  Toast.LENGTH_SHORT).show();

						} else {
							// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
							Toast.makeText(GlobalContext.getInstance(), "支付失败",
									  Toast.LENGTH_SHORT).show();

						}
					}
					break;
				}
				case SDK_CHECK_FLAG: {
					Toast.makeText(GlobalContext.getInstance(), "检查结果为：" + msg.obj,
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
			tipsToast = TipsToast.makeText(GlobalContext.getInstance(),
					  msg, TipsToast.LENGTH_SHORT);
		}
		tipsToast.show();
		tipsToast.setIcon(iconResId);
		tipsToast.setText(msg);
	}

	/**
	 * 弹出付款对话框
	 */
	private void showPayDialog(final Order order) {
		paymentDialog = DialogUtil.getCustomDialog(OrderDetailActivity.this);
		paymentDialog.setContentView(R.layout.pay_dialog);
		orderId=order.getId();

		TextView txtOrderNo = (TextView) paymentDialog.findViewById(R.id.txtorderno);
		TextView txtTotalAmount = (TextView) paymentDialog.findViewById(R.id.txtamount);
		final TextView txtPayType = (TextView) paymentDialog.findViewById(R.id.txtpaytype);
		txtCardBalances = (TextView) paymentDialog.findViewById(R.id.txtbalance);
		Button btnRecharge = (Button) paymentDialog.findViewById(R.id.btnRecharge);
		edtPayCode = (EditText) paymentDialog.findViewById(R.id.edtpaycode);
		Button btnGetPayCode = (Button) paymentDialog.findViewById(R.id.btngetpaycode);
		Button btnCancle = (Button) paymentDialog.findViewById(R.id.btncancle);
		Button btnPayment = (Button) paymentDialog.findViewById(R.id.btnpayment);
		txtTotalAmount.setText(MathUtils.formatDataForBackBigDecimal(order.getTotalAmount())+"元");
		txtOrderNo.setText("订单号:" + order.getOrderNumber());
		txtPayType.setText(OrderUtils.getPayName(order.getPayMethod()));
		new GetSmileCardBalanceTask().execute();


		//充值
		btnRecharge.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GlobalContext.getInstance(), ReChargeActivity.class);
				startActivity(intent);
			}
		});

		//取消
		btnCancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				paymentDialog.cancel();
				UIUtil.dismissDlg();

			}
		});

		//获得支付码
		btnGetPayCode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(smileCardBalance<order.getTotalAmount()){
					Toast.makeText(OrderDetailActivity.this, getString(R.string.smilecardbalance_no_full),Toast.LENGTH_SHORT).show();
					return;
				}
				UIUtil.showWaitDialog(OrderDetailActivity.this);
				new GetSmileCardPaySmsCodeTask().execute(orderId);

			}
		});

		//支付订单
		btnPayment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(TextUtils.isEmpty(edtPayCode.getText())) {
					Toast.makeText(OrderDetailActivity.this, getString(R.string.input_sms_code), Toast.LENGTH_SHORT).show();
					return;
				}

				if(smileCardBalance<order.getTotalAmount()){
					Toast.makeText(OrderDetailActivity.this,getString(R.string.smilecardbalance_no_full),Toast.LENGTH_SHORT).show();
					return;

				}
				UIUtil.showWaitDialog(OrderDetailActivity.this);
				simleCardPay(orderId,edtPayCode.getEditableText().toString(),payKey);


			}
		});


	}

	/**
	 * 获得微笑卡支付码
	 */
	private class GetSmileCardPaySmsCodeTask extends
			  MyAsyncTask<Long, Void, BackResultBean> {


		@Override
		protected BackResultBean doInBackground(Long... params) {
			return OrderDao.getSmileCardPaySmsCode(params[0]);

		}

		@Override
		protected void onPostExecute(BackResultBean backResultBean) {
			UIUtil.dismissDlg();
			if(backResultBean!=null){
				if(backResultBean.getCode().equals("1")){
					Toast.makeText(OrderDetailActivity.this,getString(R.string.smscode_sended),Toast.LENGTH_SHORT).show();
					payKey=backResultBean.getKeyStr();
				}else{
					Toast.makeText(OrderDetailActivity.this,backResultBean.getMessage(),Toast.LENGTH_SHORT).show();
				}
			}


		}

	}

	/**
	 * 微笑卡支付
	 *
	 * @param request
	 */
	private void simleCardPay(final Long  orderId,final String smsCode,final String payKey) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				UIUtil.dismissDlg();
				BackResultBean resultBean= (BackResultBean) msg.obj;
				if (msg.what == 1) {
					UIUtil.showWaitDialog(OrderDetailActivity.this);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					new getOrderStateByOrderId().execute(orderId);

                   /* if(order.getStatus()== OrderStatusEnum.ORDER_PAID) {

                        showTips(R.mipmap.tips_success, getString(R.string.pay_success));
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(Cart.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            }
                        }, 1000);
                    }else{
                        showTips(R.mipmap.tips_error, getString(R.string.interface_error));
                    }
*/

				} else if (msg.what == 0) {
					showTips(R.mipmap.tips_error, resultBean.getMessage());
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				BackResultBean resultBean = OrderDao.smileCardPay(orderId,smsCode,payKey!=null?payKey:"1");
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
	 * 获得微笑卡余额
	 */
	private class GetSmileCardBalanceTask extends
			  MyAsyncTask<List<Long>, Void, Double> {


		@Override
		protected Double doInBackground(List<Long>... params) {
			return PointDao.getSmileCardBalance();

		}

		@Override
		protected void onPostExecute(Double point) {
			txtCardBalances.setText("余额"+ MathUtils.formatDataForBackBigDecimal(point));
			smileCardBalance=point;
			paymentDialog.show();


		}

	}


	/**
	 * 根据OrderId 得到签名信息
	 *
	 * @param orderId
	 */
	private void getSignOrderInfo(final long orderId) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				UIUtil.dismissDlg();
				BackResultBean resultBean = (BackResultBean) msg.obj;
				if (msg.what == 1) {
					ToAliPay(resultBean.getKeyStr());
				} else if (msg.what == 0) {
					showTips(R.mipmap.tips_error, resultBean.getMessage());
				} else {
					showTips(R.mipmap.tips_error, getString(R.string.signorderexception));
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				BackResultBean resultBean = OrderDao.getSignOrderInfoByOrderId(orderId);
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
	 *  得到签名信息
	 *
	 * @param
	 */
	private void getSignInfo(final double amount,final PayMethodEnum methodEnum) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				UIUtil.dismissDlg();
				BackResultBean resultBean = (BackResultBean) msg.obj;
				if (msg.what == 1) {
					ToAliPay(resultBean.getKeyStr());
				} else if (msg.what == 0) {
					showTips(R.mipmap.tips_error, resultBean.getMessage());
				} else {
					showTips(R.mipmap.tips_error, GlobalContext.getInstance().getString(R.string.signorderexception));
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				BackResultBean resultBean = OrderDao.assembleRechargeSignInfo(amount, methodEnum);
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
				PayTask alipay = new PayTask(OrderDetailActivity.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(orderInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();


	}

	/**
	 * 根据订单号得到订单状态
	 */
	private class getOrderStateByOrderId extends
			  MyAsyncTask<Long, Void, Order> {
		@Override
		protected Order doInBackground(Long... params) {
			Order order = OrderDao.getOrderInfoByOrderId(params[0]);
			return order;
		}

		@Override
		protected void onPostExecute(Order order) {

			UIUtil.dismissDlg();
			if(order!=null){
				if(order.getStatus()==OrderStatusEnum.ORDER_PAID) {
					showTips(R.mipmap.tips_success, "支付成功");
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {

							Intent intent = new Intent(OrderDetailActivity.this, MyOrderActivity.class);
							startActivity(intent);
							finish();

						}
					}, 1000);
				}else{
					Toast.makeText(OrderDetailActivity.this, getString(R.string.pay_error), Toast.LENGTH_SHORT).show();

				}
			}



		}

	}

	private void initViews() {
		tvState = (TextView) findViewById(R.id.tv_order_state);
		tvPrice = (TextView) findViewById(R.id.tv_price);
		tvPriceY = (TextView) findViewById(R.id.tv_price_y);
		tvName = (TextView) findViewById(R.id.tv_name);
		tvPhone = (TextView) findViewById(R.id.tv_phone);
		tvAddress = (TextView) findViewById(R.id.tv_address);
		tvStoreName = (TextView) findViewById(R.id.tv_store_name);
		llContainer = (LinearLayout) findViewById(R.id.ll_list_container);
		tvCostY = (TextView)findViewById(R.id.tv_cost_y);
		tvTotalCount = (TextView)findViewById(R.id.tv_total_count);
		tvTotalPrice = (TextView)findViewById(R.id.tv_total_price);
		tvOrderNo = (TextView)findViewById(R.id.tv_orderNo);
		tvDeliType = (TextView)findViewById(R.id.tv_deli_type);
		tvisReceiv = (TextView)findViewById(R.id.tv_is_receive);
		llisReceive = (LinearLayout)findViewById(R.id.ll_is_receive);
		summary_linear= (LinearLayout) findViewById(R.id.summary_linear);
		llBack = (LinearLayout)findViewById(R.id.ll_back);
		tvBack = (TextView)findViewById(R.id.tv_back);
		btSubmit = (Button)findViewById(R.id.bt_item_submit);
		btCancel = (Button)findViewById(R.id.bt_item_cancel);
		llBackDetail = (LinearLayout)findViewById(R.id.ll_back_detail);
		tvBackState =(TextView)findViewById(R.id.tv_backState);
		txtPaytype= (TextView) findViewById(R.id.txtpaytype);
		tvsuumary= (TextView) findViewById(R.id.summary);
		llnet= (LinearLayout) findViewById(R.id.llnet);
		reload= (Button) findViewById(R.id.reload);
		llisReceive.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String[] str = {"是","否"};
				new BottomAlertDialog(OrderDetailActivity.this,tvisReceiv,str).show();
			}
		});
		
		llBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String[] str = {"未付款","发错货"};
				new BottomAlertDialog(OrderDetailActivity.this,tvBack,str).show();
			}
		});
		
		if(isBack){
			llBackDetail.setVisibility(View.VISIBLE);
		}else{
			llBackDetail.setVisibility(View.GONE);
		}
	}

	private void loadDatas() {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == GET_OK) {
                	OrderGetResponse orderResponse = (OrderGetResponse) msg.obj;
                	if(null == orderResponse) return;
                	final Order order = orderResponse.getOrder();
                	if(null == order) return;
                	tvState.setText(getStatus(order.getStatus()));
					Spannable WordtoSpan = new SpannableString("¥ "+order.getTotalAmount());
					WordtoSpan.setSpan(new TextAppearanceSpan(OrderDetailActivity.this, R.style.style0), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					WordtoSpan.setSpan(new TextAppearanceSpan(OrderDetailActivity.this, R.style.style01), 2, ("¥ "+order.getTotalAmount()).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                	tvPrice.setText(WordtoSpan,TextView.BufferType.SPANNABLE);
					txtPaytype.setText(OrderUtils.getPayName(order.getPayMethod()));
                	//tvPriceY.setText("¥"+order.getFreightFee()
					if(TextUtils.isEmpty(order.getSummary())){
						summary_linear.setVisibility(View.GONE);
					}else{
						tvsuumary.setText(order.getSummary());
					}
                	tvName.setText(order.getLocation().getContactName());
                	tvPhone.setText(order.getLocation().getContactPhone());
					String address = order.getLocation() == null ?"":order.getLocation().getFullAddress();
					if(address!=null)
					address = address.replaceAll("&nbsp;"," ");
					tvAddress.setText(address);
                	tvStoreName.setText(order.getStoreName());
                	orderLineList = order.getOrderLineList();
                	if(orderLineList != null && orderLineList.size()>0){
                		for(int i=0;i<orderLineList.size();i++){
                			llContainer.addView(new GoodsItem(OrderDetailActivity.this,orderLineList.get(i)));
                		}
                	}
                	tvCostY.setText("¥ 0.00");
					double totalCount=0.0;
					for(int i =0;i<orderLineList.size();i++){
						totalCount=totalCount+orderLineList.get(i).getQuantity();

					}
					int totalintCount=(int)totalCount;

						tvTotalCount.setText("共" + totalintCount + "件商品");

                	tvTotalPrice.setText("¥ "+MathUtils.formatDataForBackBigDecimal(order.getTotalAmount()));
                	tvOrderNo.setText(order.getOrderNumber());
            		String deli = "";
            		if(order.getDeliveryMode() == DeliveryModeEnum.PICKUP){
            			deli = "到店自提";
            		}else if(order.getDeliveryMode() == DeliveryModeEnum.DELIVERY){
            			deli = "送货上门";
            		}
                	tvDeliType.setText(deli);
                	
            		if(order.getStatus() == OrderStatusEnum.ORDER_PLACED) {
						btCancel.setVisibility(View.VISIBLE);
						btSubmit.setVisibility(View.VISIBLE);
						btCancel.setText("取消订单");
						btSubmit.setText("付款");
						tvBackState.setVisibility(View.GONE);
						btCancel.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								cancelOrder(order.getId());
							}
						});
						btSubmit.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								orderId = order.getId();
								if (order.getPayMethod() == PayMethodEnum.ALIPAY)
									getSignOrderInfo(orderId);
								if (order.getPayMethod() == PayMethodEnum.SMILE_CARD)
									showPayDialog(order);

							}
						});
					}else if(order.getStatus() == OrderStatusEnum.GOODS_SHIPPED){
						btCancel.setText("申请退款");
						btSubmit.setText("确认收货");
						btCancel.setVisibility(View.GONE);
						btSubmit.setVisibility(View.VISIBLE);
						tvBackState.setVisibility(View.GONE);
						btCancel.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								//refundOrder(order.getId()); //退款操作
							}
						});
						btSubmit.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								UIUtil.showWaitDialog(OrderDetailActivity.this);
								isReiceveGoods(order.getId());
							}
						});

            		}else if(order.getStatus() == OrderStatusEnum.ORDER_PAID){
						btCancel.setText("申请退款");
            			btSubmit.setText("确认收货");
            			btCancel.setVisibility(View.GONE);
            			btSubmit.setVisibility(View.GONE);
            			tvBackState.setVisibility(View.GONE);
            			btCancel.setOnClickListener(new View.OnClickListener() {
            				@Override
            				public void onClick(View arg0) {
            					//refundOrder(order.getId()); //退款操作
            				}
            			});
            			btSubmit.setOnClickListener(new View.OnClickListener() {
            				@Override
            				public void onClick(View arg0) {
								UIUtil.showWaitDialog(OrderDetailActivity.this);
								isReiceveGoods(order.getId());
            				}
            			});
            		}else if(order.getStatus() == OrderStatusEnum.GOODS_RECEIVED){
            			btCancel.setVisibility(View.GONE);
            			btSubmit.setVisibility(View.VISIBLE);
            			btSubmit.setText("评论");
            			tvBackState.setVisibility(View.GONE);
            			btSubmit.setOnClickListener(new View.OnClickListener() {
    						@Override
    						public void onClick(View arg0) {
    							/*if(orderLineList != null){
    								OrdersBean ob = new OrdersBean();
    								List<MyOrderbean> myOrders = new ArrayList<MyOrderbean>();
    								for(int i = 0;i<orderLineList.size();i++){
    									MyOrderbean myOrder= new MyOrderbean();
    									myOrder.setCost(orderLineList.get(i).getAmount()+"");
    									myOrder.setTitle(orderLineList.get(i).getItemName());
    									myOrder.setImageUrl(orderLineList.get(i).getPicture());
    									myOrder.setGoodsId(orderLineList.get(i).getId());
    									myOrders.add(myOrder);
    								}
    								ob.setData(myOrders);
    						 		ob.setOrderId(order.getId());
    								Intent it = new Intent(OrderDetailActivity.this,GoodsCommentActivity.class);
    								it.putExtra("data", ob);
    								startActivity(it);
    							}*/
								Toast.makeText(OrderDetailActivity.this,getString(R.string.tobecontinue),Toast.LENGTH_SHORT).show();

    							
    						}
    					});
            		}else if(order.getStatus()==OrderStatusEnum.ORDER_CLOSED){ //已取消的订单
						btCancel.setVisibility(View.GONE);
						btSubmit.setVisibility(View.GONE);
						tvBackState.setVisibility(View.GONE);
					}
                	
                	UIUtil.dismissDlg();
                	
                } else if (msg.what == GET_ERROR) {
                	OrderGetResponse orderResponse = (OrderGetResponse) msg.obj;
                    Toast.makeText(OrderDetailActivity.this, orderResponse.getErrors().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                    UIUtil.dismissDlg();
					llnet.setVisibility(View.VISIBLE);
					reload.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// Toast.makeText(StoreList.this,"aa",100).show();
							ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
							NetworkInfo info = connectivityManager.getActiveNetworkInfo();
							if(info != null && info.isAvailable()) {
								UIUtil.showWaitDialog(OrderDetailActivity.this);
								loadDatas();
								llnet.setVisibility(View.GONE);
							}else{
								ShowToastUtils.showToast(getString(R.string.network_not_connected), OrderDetailActivity.this);
							}

						}
					});
                }else if(msg.what == -1){
					UIUtil.dismissDlg();
					llnet.setVisibility(View.VISIBLE);
					reload.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// Toast.makeText(StoreList.this,"aa",100).show();
							ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
							NetworkInfo info = connectivityManager.getActiveNetworkInfo();
							if(info != null && info.isAvailable()) {
								UIUtil.showWaitDialog(OrderDetailActivity.this);
								loadDatas();
								llnet.setVisibility(View.GONE);
							}else{
								ShowToastUtils.showToast(getString(R.string.network_not_connected), OrderDetailActivity.this);
							}

						}
					});



				}else if(msg.what == CLOSE_OK){
					UIUtil.dismissDlg();
					Toast.makeText(OrderDetailActivity.this, "取消成功", Toast.LENGTH_SHORT).show();
					setResult(RESULT_OK);
					finish();
                } else if(msg.what == CLOSE_ERROR){
					OrderCloseResponse ordercloseRSP = (OrderCloseResponse)msg.obj;
					Toast.makeText(OrderDetailActivity.this,
							ordercloseRSP.getErrors().get(0).getMessage(),
							Toast.LENGTH_SHORT).show();
					UIUtil.dismissDlg();
				}else if(msg.what == REF_OK){
					UIUtil.dismissDlg();
					RefundDialog refdlg = new RefundDialog(OrderDetailActivity.this);
					refdlg.setOnCancelListener(new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							Toast.makeText(OrderDetailActivity.this, "--todo--", Toast.LENGTH_SHORT).show();
							setResult(RESULT_OK);
							finish();
						}
					});
					refdlg.show();
				} else if(msg.what == REF_ERROR){
					OrderCloseResponse ordercloseRSP = (OrderCloseResponse)msg.obj;
					Toast.makeText(OrderDetailActivity.this,
							ordercloseRSP.getErrors().get(0).getMessage(),
							Toast.LENGTH_SHORT).show();
					UIUtil.dismissDlg();
				}
            }
        };
		getOrder();
	}




	public void initTileBar() {
		mTitleBar = (MkhTitleBar) findViewById(R.id.title_setting);
		if(isBack){
			mTitleBar.setTitle("退款");
		}else{
			mTitleBar.setTitle("订单详情");
		}
		
		mTitleBar.setLeftClickFinish(OrderDetailActivity.this, new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent=new Intent(OrderDetailActivity.this,MyOrderActivity.class);
				setResult(1001,intent);
				finish();


			}
		});
	}
	
    private void getOrder() {
    	UIUtil.showWaitDialog(OrderDetailActivity.this);

        new Thread() {
            public void run() {
                Message msg = new Message();

                OrderGetRequest orderRequest = new OrderGetRequest();
                orderRequest.setId(orderid);
                try {
                    OrderGetResponse orderResponse = AuthDao.client.execute(orderRequest,passport);
                    if (orderResponse.hasError()) {
                        msg.what = GET_ERROR;   // 获取失败
                    } else {
                        msg.what = GET_OK;   // 获取成功
                    }
                    msg.obj = orderResponse;

                } catch (Exception e) {
					msg.what=-1;
                }
				finally {
					handler.sendMessage(msg);
				}
			}
        }.start();
    }


	/**
	 * 确认收货
	 *
	 * @param request
	 */
	private void isReiceveGoods(final long orderId) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				UIUtil.dismissDlg();
				BackResultBean resultBean = (BackResultBean) msg.obj;
				if (msg.what == 1) {
					Toast.makeText(OrderDetailActivity.this,getString(R.string.receive_goods_success),Toast.LENGTH_SHORT).show();
					Intent intent=new Intent(OrderDetailActivity.this,MyOrderActivity.class);
					startActivity(intent);
					finish();

				} else if (msg.what == 0) {
					Toast.makeText(OrderDetailActivity.this,resultBean.getMessage(),Toast.LENGTH_SHORT).show();
				}
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				BackResultBean resultBean = OrderDao.isReiceveGoods(orderId);
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
     * 取消订单
     */
	public void cancelOrder(final Long id) {
		UIUtil.showWaitDialog(OrderDetailActivity.this);
		new Thread() {
			public void run() {
				Message msg = new Message();

				OrderCloseRequest ordercloseReq = new OrderCloseRequest();
				ordercloseReq.setId(id);
				try {
					OrderCloseResponse ordercloseRsp = AuthDao.client.execute(
							ordercloseReq, passport);
					if (ordercloseRsp.hasError()) {
						msg.what = CLOSE_ERROR; // 获取失败
					} else {
						msg.what = CLOSE_OK; // 登录成功
					}
					msg.obj = ordercloseRsp;
					handler.sendMessage(msg);
				} catch (Exception e) {
				}
			}
		}.start();
	}
	
	/**
	 * 退款
	 */
	public void refundOrder(final Long id) {
		UIUtil.showWaitDialog(OrderDetailActivity.this);
		new Thread() {
			public void run() {
				Message msg = new Message();

				OrderRefundRequest orderrefReq = new OrderRefundRequest();
				orderrefReq.setId(id);
				orderrefReq.setReason(tvBack.getText().toString());
				orderrefReq.setHasReceived(tvisReceiv.getText().equals("是"));
				try {
					OrderRefundResponse orderrefRsp = AuthDao.client.execute(
							orderrefReq, passport);
					if (orderrefRsp.hasError()) {
						msg.what = REF_ERROR; // 获取失败
					} else {
						msg.what = REF_OK; // 登录成功
					}
					msg.obj = orderrefRsp;
					handler.sendMessage(msg);
				} catch (Exception e) {
				}
			}
		}.start();
	}
	
	public static String getStatus(OrderStatusEnum staenu){
		if(staenu == null){
			return "";
		}
		switch (staenu) {
		case ORDER_PLACED:
			return "待付款";
		case ORDER_PAID:
			return "配送中";
		case GOODS_SHIPPED:
			return "已发货";
		case GOODS_RECEIVED:
			return "已收货";
		case ORDER_CLOSED:
			return "已取消";
		default:
			return "";
		}
	}
}
