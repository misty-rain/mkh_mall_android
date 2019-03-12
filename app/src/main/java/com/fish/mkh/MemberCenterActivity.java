package com.fish.mkh;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.alibaba.fastjson.JSON;
import com.fish.mkh.client.AuthDao;
import com.fish.mkh.div.MkhTitleBar;
import com.fish.mkh.util.UIUtil;
import com.fish.mkh.view.DialAlertDialog;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.config.SysConstant;
import com.mkh.mobilemall.support.appconfig.AppManager;
import com.mkh.mobilemall.ui.activity.main.MainActivity;
import com.mkh.mobilemall.ui.base.BaseActivity;
import com.mkh.mobilemall.ui.utils.ShowToastUtils;
import com.mkh.mobilemall.utils.MathUtils;
import com.mkh.mobilemall.utils.SharePreferenceUtil;
import com.xiniunet.api.domain.membership.Voucher;
import com.xiniunet.api.domain.system.Passport;
import com.xiniunet.api.request.ecommerce.MemberInfoGetRequest;
import com.xiniunet.api.request.system.LogoutRequest;
import com.xiniunet.api.request.system.MemberUserGetRequest;
import com.xiniunet.api.response.ecommerce.MemberInfoGetResponse;
import com.xiniunet.api.response.system.LogoutResponse;
import com.xiniunet.api.response.system.MemberUserGetResponse;

public class MemberCenterActivity extends BaseActivity implements OnClickListener {

    MkhTitleBar mTitleBar;
    RelativeLayout rlAllOrder;
    LinearLayout llScore, llPrice, llTicekt, llMyWallet, llAddressMgr,
              llMyCollect, lldfk, lldsh, lldpj, llMemberCenter, llCancle, llnet;
    Handler handler;
    TextView tvScore, tvCount, tvTickCount, tvTopCommment, tvUnPay, tvPeisong, tvCancle, tvPhone, btPhone2, txtLevel;
    ImageView ivMsg, ivHome, imgWallet, imgCollect;
    LinearLayout btPhone;
    Button reload;
    private static final int LOGOUT_ERROR = 2;
    private static final int LOGOUT_OK = 3;
    private static final int GET_MEMBER_OK = 0;
    private static final int GET_MEMBER_ERROR = 1;
    private static final int GET_USER_OK = 4;
    private static final int GET_USER_ERROR = 5;
    private static final int GET_MSG_OK = 6;
    private static final int GET_MSG_ERROR = 7;
    private Passport passport;
    private SharePreferenceUtil sharePreferenceUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_center);
        AppManager.getAppManager().addActivity(this);
        sharePreferenceUtil = GlobalContext.getInstance().getSpUtil();
        passport = sharePreferenceUtil.getUserInfo();
        initViews();
        setDate();
    }

    private void initViews() {
        rlAllOrder = (RelativeLayout) findViewById(R.id.rechargeactivity_cost);
        btPhone = (LinearLayout) findViewById(R.id.ll_phone);
        llScore = (LinearLayout) findViewById(R.id.ll_score);
        llPrice = (LinearLayout) findViewById(R.id.ll_price);
        llTicekt = (LinearLayout) findViewById(R.id.ll_ticket);
        llnet = (LinearLayout) findViewById(R.id.llnet);
        tvScore = (TextView) findViewById(R.id.tv_score);
        tvCount = (TextView) findViewById(R.id.tv_count);
        tvTickCount = (TextView) findViewById(R.id.tv_ticket_count);
        tvTopCommment = (TextView) findViewById(R.id.tv_top_comment);
        tvUnPay = (TextView) findViewById(R.id.tv_top_unpay);
        tvPeisong = (TextView) findViewById(R.id.tv_top_peisong);
        tvCancle = (TextView) findViewById(R.id.tv_top_cancle);
        tvPhone = (TextView) findViewById(R.id.tv_phone);
        btPhone2 = (TextView) findViewById(R.id.bt_phone);
        txtLevel = (TextView) findViewById(R.id.txtlevel);
        reload = (Button) findViewById(R.id.reload);
        if (sharePreferenceUtil.getStorePhone().equals(""))
            btPhone2.setText("客服电话：" + getString(R.string.default_cs_phone));
        else
            btPhone2.setText("客服电话:" + sharePreferenceUtil.getStorePhone());

        ivMsg = (ImageView) findViewById(R.id.iv_msg);
        ivHome = (ImageView) findViewById(R.id.iv_home);
        llMyWallet = (LinearLayout) findViewById(R.id.ll_mywallet);
        llAddressMgr = (LinearLayout) findViewById(R.id.ll_addressmgr);
        llMyCollect = (LinearLayout) findViewById(R.id.ll_mycollect);
        llMemberCenter = (LinearLayout) findViewById(R.id.ll_member_center);
        lldfk = (LinearLayout) findViewById(R.id.ll_dfk);
        lldsh = (LinearLayout) findViewById(R.id.ll_dsh);
        lldpj = (LinearLayout) findViewById(R.id.ll_dpj);
        llCancle = (LinearLayout) findViewById(R.id.ll_cancel);


        rlAllOrder.setOnClickListener(this);
        llScore.setOnClickListener(this);
        llCancle.setOnClickListener(this);
        llPrice.setOnClickListener(this);
        llTicekt.setOnClickListener(this);
        btPhone.setOnClickListener(this);
        ivHome.setOnClickListener(this);
        llMyWallet.setOnClickListener(this);
        llAddressMgr.setOnClickListener(this);
        llMyCollect.setOnClickListener(this);
        llMemberCenter.setOnClickListener(this);
        lldfk.setOnClickListener(this);
        lldsh.setOnClickListener(this);
        lldpj.setOnClickListener(this);
    }

    private void setDate() {
        UIUtil.showWaitDialog(MemberCenterActivity.this);
        handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == GET_MEMBER_OK) {
                    UIUtil.dismissDlg();
                    MemberInfoGetResponse memRsp = (MemberInfoGetResponse) msg.obj;
                    if (null == memRsp)
                        return;
                    try {
                        tvCount.setText(UIUtil.isNull(" " + MathUtils.formatDataForBackBigDecimal(memRsp.getBalance())));
                        tvScore.setText(UIUtil.isNull("" + MathUtils.formatDataForBackBigDecimal(memRsp.getPoint())));
                        tvTickCount.setText(UIUtil.isNull("" + memRsp.getVoucherCount()));
                    } catch (Exception e) {
                        Log.e("mkh", e.getMessage());
                    }
                    if (memRsp.getPlacedQuantity() == 0) {
                        tvUnPay.setVisibility(View.GONE);
                    } else {
                        if (String.valueOf(memRsp.getPlacedQuantity()).length() <= 2)
                            tvUnPay.setText("" + memRsp.getPlacedQuantity());
                        else
                            tvUnPay.setText("...");
                    }
                    if (memRsp.getReceivedQuantity() == 0) {
                        tvTopCommment.setVisibility(View.GONE);

                    } else {
                        if (String.valueOf(memRsp.getReceivedQuantity()).length() <= 2)
                            tvTopCommment.setText("" + memRsp.getReceivedQuantity());
                        else
                            tvTopCommment.setText("...");
                    }
                    if (memRsp.getPaidQuantity() == 0) {
                        tvPeisong.setVisibility(View.GONE);

                    } else {
                        if (String.valueOf(memRsp.getPaidQuantity()).length() <= 2)
                            tvPeisong.setText("" + memRsp.getPaidQuantity());
                        else
                            tvPeisong.setText("...");
                    }

                    if (memRsp.getClosedQuantity() == 0) {
                        tvCancle.setVisibility(View.GONE);

                    } else {
                        if (String.valueOf(memRsp.getClosedQuantity()).length() <= 2)
                            tvCancle.setText("" + memRsp.getClosedQuantity());
                        else
                            tvCancle.setText("...");
                    }

                } else if (msg.what == GET_MEMBER_ERROR) {
                    UIUtil.dismissDlg();
                    MemberInfoGetResponse memRsp = (MemberInfoGetResponse) msg.obj;
                    Toast.makeText(MemberCenterActivity.this,
                              memRsp.getErrors().get(0).getMessage(),
                              Toast.LENGTH_SHORT).show();
                    llnet.setVisibility(View.VISIBLE);
                    reload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Toast.makeText(StoreList.this,"aa",100).show();
                            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                            if (info != null && info.isAvailable()) {
                                UIUtil.showWaitDialog(MemberCenterActivity.this);
                                setDate();
                                llnet.setVisibility(View.GONE);
                            } else {
                                ShowToastUtils.showToast(getString(R.string.network_not_connected), MemberCenterActivity.this);
                            }

                        }
                    });
                } else if (msg.what == -1) {
                    UIUtil.dismissDlg();
                    llnet.setVisibility(View.VISIBLE);
                    reload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Toast.makeText(StoreList.this,"aa",100).show();
                            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                            if (info != null && info.isAvailable()) {
                                UIUtil.showWaitDialog(MemberCenterActivity.this);
                                setDate();
                                llnet.setVisibility(View.GONE);
                            } else {
                                ShowToastUtils.showToast(getString(R.string.network_not_connected), MemberCenterActivity.this);
                            }

                        }
                    });

                } else if (msg.what == LOGOUT_OK) {
                    UIUtil.dismissDlg();
                    LogoutResponse outRsp = (LogoutResponse) msg.obj;

                } else if (msg.what == LOGOUT_ERROR) {
                    UIUtil.dismissDlg();
                    LogoutResponse outRsp = (LogoutResponse) msg.obj;
                    Toast.makeText(MemberCenterActivity.this,
                              outRsp.getErrors().get(0).getMessage(),
                              Toast.LENGTH_SHORT).show();
                } else if (msg.what == GET_USER_OK) {
                    UIUtil.dismissDlg();
                    MemberUserGetResponse userRsp = (MemberUserGetResponse) msg.obj;
                    if (null == userRsp)
                        return;
                    if (null == userRsp.getMemberUser())
                        return;
                    tvPhone.setText(userRsp.getMemberUser().getMobilePhone());
                    if (userRsp.getMemberUser().getLevelId() != null)
                        txtLevel.setText("Lv." + userRsp.getMemberUser().getLevelId());
                    else
                        txtLevel.setText("Lv.0");

                } else if (msg.what == GET_USER_ERROR) {
                    UIUtil.dismissDlg();
                    MemberUserGetResponse userRsp = (MemberUserGetResponse) msg.obj;
                    Toast.makeText(MemberCenterActivity.this,
                              userRsp.getErrors().get(0).getMessage(),
                              Toast.LENGTH_SHORT).show();
                } else if (msg.what == GET_MSG_OK) {
                    UIUtil.dismissDlg();
//					MessageFindResponse msgRsp = (MessageFindResponse) msg.obj;
//					if (null == msgRsp)
//						return;
//					if (msgRsp.getTotalCount() > 0) {
//						ivMsg.setImageResource(R.mipmap.ic_message_new);
//					}

                } else if (msg.what == GET_MSG_ERROR) {
                    UIUtil.dismissDlg();
//					MessageFindResponse msgRsp = (MessageFindResponse) msg.obj;
//					Toast.makeText(MemberCenterActivity.this,
//							msgRsp.getErrors().get(0).getMessage(),
//							Toast.LENGTH_SHORT).show();
                }

            }
        };

        getMemberInfo();
        getUserInfo();
        getMsgCount();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.rechargeactivity_cost: {
                Intent it = new Intent(MemberCenterActivity.this,
                          MyOrderActivity.class);
                it.putExtra("INDEX", 0);
                startActivityForResult(it, 0);
            }
            break;
            case R.id.ll_dfk: {
                Intent it = new Intent(MemberCenterActivity.this,
                          MyOrderActivity.class);
                it.putExtra("INDEX", 1);
                startActivityForResult(it, 0);
            }
            break;
            case R.id.ll_dsh: {
                Intent it = new Intent(MemberCenterActivity.this,
                          MyOrderActivity.class);
                it.putExtra("INDEX", 2);
                startActivityForResult(it, 0);
            }

            break;
            case R.id.ll_cancel: {
                Intent it = new Intent(MemberCenterActivity.this,
                          MyOrderActivity.class);
                it.putExtra("INDEX", 4);
                startActivityForResult(it, 0);
            }

            break;
            case R.id.ll_dpj: {
                Intent it = new Intent(MemberCenterActivity.this,
                          MyOrderActivity.class);
                it.putExtra("INDEX", 3);
                startActivityForResult(it, 0);
            }
            break;
            case R.id.ll_phone:
                if (!sharePreferenceUtil.getStorePhone().equals("")) {
                    new DialAlertDialog(MemberCenterActivity.this, sharePreferenceUtil.getStorePhone())
                              .show();
                } else {
                    //Toast.makeText(MemberCenterActivity.this,getString(R.string.store_not_open),200).show();
                    new DialAlertDialog(MemberCenterActivity.this, getString(R.string.default_cs_phone))
                              .show();
                }
                break;
            case R.id.ll_price: {
                Intent intent = new Intent(MemberCenterActivity.this,
                          CardInfoActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.ll_score: {
            /*Intent intent = new Intent(MemberCenterActivity.this,
					ScoreActivity.class);
			startActivity(intent);*/
                Toast.makeText(MemberCenterActivity.this, getString(R.string.tobecontinue), 200).show();
            }
            break;
            case R.id.ll_ticket: {
		/*	Intent intent = new Intent(MemberCenterActivity.this,
					TicketActivity.class);
			startActivity(intent);*/
                Toast.makeText(MemberCenterActivity.this, getString(R.string.tobecontinue), 200).show();
            }
            break;
            case R.id.iv_home:
                Intent intent = new Intent(MemberCenterActivity.this,
                          MainActivity.class);
                startActivity(intent);


                break;
            case R.id.ll_addressmgr:
                startActivity(new Intent(MemberCenterActivity.this,
                          AddressManagerActivity.class));
                break;
            case R.id.ll_mycollect:
                Intent i = new Intent(MemberCenterActivity.this, MainActivity.class);
                i.putExtra("isMyCollect", true);
                startActivity(i);
                finish();
                break;
            case R.id.ll_mywallet:
			/*Intent it=new Intent(MemberCenterActivity.this,CardInfoActivity.class);
			startActivity(it);*/
                Toast.makeText(MemberCenterActivity.this, getString(R.string.tobecontinue), Toast.LENGTH_SHORT).show();

                break;
            case R.id.ll_member_center:
                startActivity(new Intent(MemberCenterActivity.this,
                          PersonInfoActivity.class));
                break;
            default:
                break;
        }
    }

    private void logOut() {
        new Thread() {
            public void run() {
                Message msg = new Message();

                LogoutRequest logOutRequest = new LogoutRequest();
                try {
                    LogoutResponse logOutResponse = AuthDao.client.execute(
                              logOutRequest, passport);
                    if (logOutResponse.hasError()) {
                        msg.what = LOGOUT_ERROR; // 退出失败
                    } else {
                        msg.what = LOGOUT_OK; // 注销成功
                    }
                    msg.obj = logOutResponse;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                }
            }
        }.start();
    }

    private void getMemberInfo() {
        new Thread() {
            public void run() {
                Message msg = new Message();

                MemberInfoGetRequest memberInfoReq = new MemberInfoGetRequest();
                try {
                    MemberInfoGetResponse memberInfoRsp = AuthDao.client
                              .execute(memberInfoReq, passport);
                    if (memberInfoRsp.hasError()) {
                        msg.what = GET_MEMBER_ERROR; // 获取失败
                    } else {
                        msg.what = GET_MEMBER_OK; // 登录成功
                    }
                    msg.obj = memberInfoRsp;

                } catch (Exception e) {
                    msg.what = -1;
                } finally {
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }

    private void getUserInfo() {
        new Thread() {
            public void run() {
                Message msg = new Message();

                MemberUserGetRequest userInfoReq = new MemberUserGetRequest();
                try {
                    MemberUserGetResponse userInfoRsp = AuthDao.client.execute(
                              userInfoReq, passport);
                    if (userInfoRsp.hasError()) {
                        msg.what = GET_USER_ERROR; // 获取失败
                    } else {
                        msg.what = GET_USER_OK; // 登录成功
                    }
                    msg.obj = userInfoRsp;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                }
            }
        }.start();
    }

    private void getMsgCount() {
        new Thread() {
            public void run() {
                Message msg = new Message();

//				MessageFindRequest msgReq = new MessageFindRequest();
//				try {
//					MessageFindResponse msgRsp = AuthDao.client.execute(msgReq, passport);
//					if (msgRsp.hasError()) {
//						msg.what = GET_MSG_ERROR; // 获取失败
//					} else {
//						msg.what = GET_MSG_OK; // 登录成功
//					}
//					msg.obj = msgRsp;
//					handler.sendMessage(msg);
//				} catch (Exception e) {
//				}
            }
        }.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            setDate();
        }
    }

}
