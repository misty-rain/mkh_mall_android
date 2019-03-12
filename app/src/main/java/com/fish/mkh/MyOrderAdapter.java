package com.fish.mkh;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.fish.mkh.bean.MyOrderbean;
import com.fish.mkh.bean.OrdersBean;
import com.fish.mkh.util.UIUtil;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.bean.BackResultBean;
import com.mkh.mobilemall.dao.OrderDao;
import com.mkh.mobilemall.dao.PointDao;
import com.mkh.mobilemall.support.alipay.PayResult;
import com.mkh.mobilemall.support.asyncTask.MyAsyncTask;
import com.mkh.mobilemall.support.db.TempComCarDBTask;
import com.mkh.mobilemall.ui.activity.main.MainActivity;
import com.mkh.mobilemall.ui.utils.OrderUtils;
import com.mkh.mobilemall.ui.widget.TipsToast;
import com.mkh.mobilemall.utils.DialogUtil;
import com.mkh.mobilemall.utils.MathUtils;
import com.xiniunet.api.domain.ecommerce.DeliveryModeEnum;
import com.xiniunet.api.domain.ecommerce.Order;
import com.xiniunet.api.domain.ecommerce.OrderLine;
import com.xiniunet.api.domain.ecommerce.OrderStatusEnum;
import com.xiniunet.api.domain.ecommerce.PayMethodEnum;
import com.mkh.mobilemall.R;

public class MyOrderAdapter extends BaseAdapter{
	Context mContext;
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private int selectedIndex;
	
	public MyOrderAdapter(Context context,int selectIndex) {
		mContext = context;
		this.selectedIndex=selectIndex;

	}
	
	private List<Order> datas = new ArrayList<Order>();

	TipsToast tipsToast;
	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_CHECK_FLAG = 2;
	Dialog paymentDialog;
	TextView txtCardBalances;
	EditText edtPayCode;
	String payKey=null;//微笑卡支付秘钥
	Long  orderId=0L; //orderid
	double smileCardBalance; //微笑卡余额

	public void setData(List<Order> list) {
		datas = list;
	}

	public int getCount() {
		return datas.size();
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
						UIUtil.showWaitDialog((Activity)mContext);
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
		paymentDialog = DialogUtil.getCustomDialog((Activity)mContext);
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
				mContext.startActivity(intent);
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
					Toast.makeText((Activity) mContext, mContext.getString(R.string.smilecardbalance_no_full),Toast.LENGTH_SHORT).show();
					return;
				}
				UIUtil.showWaitDialog((Activity) mContext);
				new GetSmileCardPaySmsCodeTask().execute(orderId);

			}
		});

		//支付订单
		btnPayment.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(TextUtils.isEmpty(edtPayCode.getText())) {
					Toast.makeText((Activity)mContext, mContext.getString(R.string.input_sms_code), Toast.LENGTH_SHORT).show();
					return;
				}

				if(smileCardBalance<order.getTotalAmount()){
					Toast.makeText((Activity)mContext,mContext.getString(R.string.smilecardbalance_no_full),Toast.LENGTH_SHORT).show();
					return;

				}
				UIUtil.showWaitDialog((Activity)mContext);
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
					Toast.makeText(mContext,mContext.getString(R.string.smscode_sended),Toast.LENGTH_SHORT).show();
					payKey=backResultBean.getKeyStr();
				}else{
					Toast.makeText(mContext,backResultBean.getMessage(),Toast.LENGTH_SHORT).show();
				}
			}


		}

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

							Intent intent = new Intent(mContext, MyOrderActivity.class);
							((Activity)mContext).startActivity(intent);
							((Activity)mContext).finish();

						}
					}, 1000);
				}else{
					Toast.makeText(mContext, mContext.getString(R.string.pay_error), Toast.LENGTH_SHORT).show();

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
					UIUtil.showWaitDialog((Activity) mContext);
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
			txtCardBalances.setText("余额:" + MathUtils.formatDataForBackBigDecimal(point));
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
					showTips(R.mipmap.tips_error, mContext.getString(R.string.signorderexception));
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
				PayTask alipay = new PayTask((Activity) mContext);
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

	public View getView(int position, View convertView, ViewGroup parent) {
		final Order bean = datas.get(position);
		View view = null;
		ViewHolder holder = new ViewHolder();
		if (convertView == null) {
			view = View.inflate(mContext, R.layout.adapt_my_order_item,null);
			holder.llTitle = (LinearLayout)view.findViewById(R.id.ll_order_detail);
			holder.tvNO = (TextView)view.findViewById(R.id.tv_order_no);
			holder.tvTime = (TextView)view.findViewById(R.id.tv_orderTime);
			holder.tvPayType = (TextView)view.findViewById(R.id.tv_orderType);
			holder.tvTransType = (TextView)view.findViewById(R.id.tv_dispatchType);
			holder.tvState = (TextView)view.findViewById(R.id.tv_orderState);
			holder.btleft = (Button)view.findViewById(R.id.bt_item_cancel);
			holder.btright = (Button)view.findViewById(R.id.bt_item_submit);
			holder.tvBackState = (TextView)view.findViewById(R.id.tv_backState);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		holder.llTitle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent it = new Intent(mContext,OrderDetailActivity.class);
				it.putExtra("IS_BACK", false);
				it.putExtra("orderid",bean.getId());
				((Activity)mContext).startActivityForResult(it, MyOrderActivity.Request_Code);
			}
		});
		holder.tvNO.setText(bean.getOrderNumber());
		holder.tvTime.setText(formatter.format(bean.getOrderTime()!= null ?bean.getOrderTime():new Date()));
		String payMethod ="";
		if(bean.getPayMethod() == PayMethodEnum.ALIPAY){
			payMethod = "支付宝";
		}else if(bean.getPayMethod() == PayMethodEnum.SMILE_CARD){
			payMethod = "微笑卡";
		}
		holder.tvPayType.setText(payMethod);
		String deli = "";
		if(bean.getDeliveryMode() == DeliveryModeEnum.PICKUP){
			deli = "到店自提";
		}else if(bean.getDeliveryMode() == DeliveryModeEnum.DELIVERY){
			deli = "送货上门";
		}
		
		holder.tvTransType.setText(deli);

		//holder.tvState.setText(OrderDetailActivity.getStatus(bean.getStatus()));
		if(bean.getStatus() == OrderStatusEnum.ORDER_PLACED){
			holder.tvState.setText("待付款");
			holder.btleft.setVisibility(View.VISIBLE);
			holder.btright.setVisibility(View.VISIBLE);
			holder.btleft.setText("取消订单");
			holder.btright.setText("付款");
			holder.tvBackState.setVisibility(View.GONE);
			holder.btleft.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					final Dialog dialog=DialogUtil.getCustomeDilogForNormal((Activity) mContext);
                TextView title= (TextView) dialog.findViewById(R.id.dialog_title);
                title.setText(R.string.cancle_order);
                dialog.show();
                TextView cancel=(TextView)dialog.findViewById(R.id.custom_cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                TextView confrim=(TextView)dialog.findViewById(R.id.custom_confrim);
                confrim.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
						dialog.cancel();
						List <Long> list = new ArrayList<Long>();
						list.add(bean.getId());
						((MyOrderActivity)mContext).cancelOrder(list,selectedIndex);

                    }
                });





				}
			});
			holder.btright.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					orderId=bean.getId();
					if(bean.getPayMethod()==PayMethodEnum.ALIPAY)
						getSignOrderInfo(orderId);
					if(bean.getPayMethod()==PayMethodEnum.SMILE_CARD)
						showPayDialog(bean);





				}
			});
		}else if(bean.getStatus() == OrderStatusEnum.GOODS_SHIPPED){
			holder.tvState.setText("配送中");
			holder.btleft.setText("申请退款");
			holder.btright.setText("确认收货");
			holder.btleft.setVisibility(View.VISIBLE);
			holder.btright.setVisibility(View.VISIBLE);
			holder.tvBackState.setVisibility(View.GONE);
			holder.btleft.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent it = new Intent(mContext,OrderDetailActivity.class);
					it.putExtra("IS_BACK", true);
					it.putExtra("orderid",bean.getId());
					Log.e("------","id--"+bean.getId());
					((Activity) mContext).startActivityForResult(it, MyOrderActivity.Request_Code);
				}
			});
			holder.btleft.setVisibility(View.GONE);
			holder.btright.setVisibility(View.GONE);
			holder.btright.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Toast.makeText(mContext, "jump-确认收货", Toast.LENGTH_SHORT).show();
				}
			});
		}else if(bean.getStatus() == OrderStatusEnum.GOODS_RECEIVED){
			holder.tvState.setText("商品评论");
			holder.btleft.setVisibility(View.GONE);
			holder.btright.setVisibility(View.VISIBLE);
			holder.btright.setText("评论");
			holder.tvBackState.setVisibility(View.GONE);
			holder.btright.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					/*if(bean.getOrderLineList() != null){
						List<OrderLine> orderLineList = bean.getOrderLineList();
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
						ob.setOrderId(bean.getId());
						Intent it = new Intent(mContext,GoodsCommentActivity.class);
						it.putExtra("data", ob);
						mContext.startActivity(it);
					}*/
					Toast.makeText(mContext,mContext.getString(R.string.tobecontinue),Toast.LENGTH_SHORT).show();
				}
			});
		}else if(bean.getStatus() == OrderStatusEnum.ORDER_CLOSED){
			holder.tvState.setText("已取消");
			holder.btleft.setVisibility(View.GONE);
			holder.btright.setVisibility(View.GONE);
			holder.tvBackState.setVisibility(View.VISIBLE);
			holder.tvBackState.setText("");
		}else if(bean.getStatus() == OrderStatusEnum.ORDER_PAID){
			holder.tvState.setText("配送中");
			holder.btleft.setVisibility(View.GONE);
			holder.btright.setVisibility(View.GONE);
			holder.tvBackState.setVisibility(View.VISIBLE);
			holder.tvBackState.setText("");
		}
		
		return view;
	}

	public Object getItem(int position) {
		return datas.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
}

class ViewHolder {
	LinearLayout llTitle;
	TextView tvNO,tvTime,tvPayType,tvTransType,tvState,tvBackState;
	Button btleft,btright;
}