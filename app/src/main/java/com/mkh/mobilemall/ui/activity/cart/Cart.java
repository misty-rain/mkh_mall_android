package com.mkh.mobilemall.ui.activity.cart;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.sdk.app.PayTask;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.daimajia.swipe.util.Attributes;
import com.fish.mkh.AddressManagerActivity;
import com.fish.mkh.ReChargeActivity;
import com.fish.mkh.bean.TempCartItem;
import com.fish.mkh.div.MkhTitleBar;
import com.fish.mkh.util.UIUtil;
import com.fish.mkh.view.BottomAlertDialog;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.bean.BackResultBean;
import com.mkh.mobilemall.bean.TempComCarBean;
import com.mkh.mobilemall.config.SysConstant;
import com.mkh.mobilemall.dao.CommodityDao;
import com.mkh.mobilemall.dao.LocationDao;
import com.mkh.mobilemall.dao.OrderDao;
import com.mkh.mobilemall.dao.PointDao;
import com.mkh.mobilemall.dao.PreferentialDao;
import com.mkh.mobilemall.support.alipay.PayResult;
import com.mkh.mobilemall.support.asyncTask.MyAsyncTask;
import com.mkh.mobilemall.support.db.TempComCarDBTask;
import com.mkh.mobilemall.ui.activity.main.MainActivity;
import com.mkh.mobilemall.ui.base.BaseActivity;
import com.mkh.mobilemall.ui.base.RecylerView.WrapRecyclerView;
import com.mkh.mobilemall.ui.utils.AddressUtils;
import com.mkh.mobilemall.ui.utils.OrderUtils;
import com.mkh.mobilemall.ui.widget.AddAndSubView;
import com.mkh.mobilemall.ui.widget.DividerItemDecoration;
import com.mkh.mobilemall.ui.widget.TipsToast;
import com.mkh.mobilemall.utils.DialogUtil;
import com.mkh.mobilemall.utils.ImageLoaderUtil;
import com.mkh.mobilemall.utils.MathUtils;
import com.mkh.mobilemall.utils.SharePreferenceUtil;
import com.xiniunet.api.domain.ecommerce.CartItem;
import com.xiniunet.api.domain.ecommerce.DeliveryModeEnum;
import com.xiniunet.api.domain.ecommerce.Order;
import com.xiniunet.api.domain.ecommerce.OrderStatusEnum;
import com.xiniunet.api.domain.ecommerce.OrderTypeEnum;
import com.xiniunet.api.domain.ecommerce.PayMethodEnum;
import com.xiniunet.api.domain.master.Location;
import com.xiniunet.api.domain.membership.Voucher;
import com.xiniunet.api.request.ecommerce.OrderCommitRequest;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiniu_wutao on 15/7/22.
 */
public class Cart extends BaseActivity {

    List<TempComCarBean> itemList = new ArrayList<TempComCarBean>();
    private SharePreferenceUtil mspUtil;
    DecimalFormat df;
    @Bind(R.id.cartList)
    WrapRecyclerView cartList;
    com.mkh.mobilemall.ui.widget.togglebutton.ToggleButton toggleButton;
    @Bind(R.id.empty_list)
    View emptyView;
    @Bind(R.id.cart_bottombar)
    View cartBottomBar;
    TextView txtGoodsCount;
    Button btnPay,reload;
    TextView txtTotalPrice, txtistridate, txtReiceveAddress, txtDisistributionerName, txtDisistributionerPhoneNum, txtDistritype, txtTicket, txtCurrPoint, txtTotalAmount, txtCartPayType, txtRemoveCart, title;
    LinearLayout llyPickDate, llyDistype, llyPreferentialTicket, llyReiceveAddress, llyChoosePayType, llyScore,llnet;
    EditText edtorderSummary;
    TextView txtCardBalances;
    EditText edtPayCode;

    List<Voucher> voucherList = new ArrayList<Voucher>();
    List<Long> cartItemIdList = new ArrayList<Long>();
    List<String> addressList = new ArrayList<String>();

    String[] strDistritDate; //配送时间数组
    String[] strDistritType; //配送方式数组
    String[] strPayType; //支付方式

    Dialog paymentDialog;
    TipsToast tipsToast;
    View orderSettingView; //订单底部视图
    View lltitle;          // 头部店名
    long mAddressId = 0L;
    int allCartCount = 0; //购物车总数量
    BigDecimal bdTotalprice;

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_CHECK_FLAG = 2;

    CartAdapter adapter;
    Long orderId = 0L; //orderid
    String payKey = null;//微笑卡支付秘钥
    TempCartItem tempCartItem = null;//临时购物车item
    double smileCardBalance; //微笑卡余额
    MkhTitleBar mTitleBar;
    String alipayOrderKeyStr;
    String orderNum;
    private String categoryId;


    @Override
    protected int getLayoutId() {
        return R.layout.carttwo;
    }

    @Override
    protected boolean hasBackButton() {
        return false;
    }

    @Override
    protected int getActionBarTitle() {
        return 0;
    }

    @Override
    protected int getActionBarCustomView() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        init();
        initTileBar();
        initView();
        initViewClickListener();
        UIUtil.showWaitDialog(Cart.this);
        new GetCartDataTask().execute();// 查询购物车商品数据
        new GetUserPointTask().execute();//查询用户积分
        initAddress();//初始化地址


    }


    public void initTileBar() {
        mTitleBar = (MkhTitleBar) findViewById(R.id.cart_actionbar);
        mTitleBar.setTitle(getResources().getString(R.string.toolbar_title_cart));
        mTitleBar.setLeftClickFinish(Cart.this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Cart.this,MainActivity.class);
                intent.putExtra("categoryId",categoryId);
                setResult(1,intent);
                finish();

            }
        });

    }

    /**
     * 初始化地址
     */
    private void initAddress() {
        if (!mspUtil.getdefaultReceiveAddress().equals("")) {
            addressList = JSON.parseObject(mspUtil.getdefaultReceiveAddress(), new TypeReference<List<String>>() {
            });
            txtDisistributionerName.setText(addressList.get(0));
            txtDisistributionerPhoneNum.setText(addressList.get(1));
            txtReiceveAddress.setText(addressList.get(2));
        } else {
            new GetAddressTask().execute();// 得到默认地址
        }

    }

    /**
     * 初始化
     */
    private void init() {
        mspUtil = GlobalContext.getInstance().getSpUtil();
        df = new DecimalFormat("##.00");
        categoryId=getIntent().getStringExtra("categoryId");
    }

    /**
     * 初始化视图
     */
    private void initView() {

        LinearLayoutManager llm = new LinearLayoutManager(Cart.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        cartList.setLayoutManager(llm);

        cartList.setHasFixedSize(true);
        cartList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));





        LayoutInflater layoutInflater =
                  (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        orderSettingView = layoutInflater.inflate(R.layout.order_setting, null);
        lltitle = layoutInflater.inflate(R.layout.titleline, null);

         ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
         lltitle.setLayoutParams(layoutParams);

        cartList.addHeaderView(lltitle);
        cartList.addFootView(orderSettingView);

        txtGoodsCount = (TextView) findViewById(R.id.txtgoodscount);
        btnPay = (Button) findViewById(R.id.btnpayorder);
        txtTotalPrice = (TextView) findViewById(R.id.txtcomPrice);
        llyPickDate = (LinearLayout) orderSettingView.findViewById(R.id.llydistrdate);
        llyDistype = (LinearLayout) orderSettingView.findViewById(R.id.llydistype);
        llyPreferentialTicket = (LinearLayout) orderSettingView.findViewById(R.id.llypreferentialticket);
        llyReiceveAddress = (LinearLayout) orderSettingView.findViewById(R.id.llyreiceveaddress);
        llyChoosePayType = (LinearLayout) orderSettingView.findViewById(R.id.llypaytype);
        llyScore = (LinearLayout) orderSettingView.findViewById(R.id.llyscore);
        txtistridate = (TextView) orderSettingView.findViewById(R.id.txtdistridate);
        txtReiceveAddress = (TextView) orderSettingView.findViewById(R.id.distributioner_address);
        txtDisistributionerName = (TextView) orderSettingView.findViewById(R.id.distributioner_name);
        txtDisistributionerPhoneNum = (TextView) orderSettingView.findViewById(R.id.distributioner_phonenum);
        txtDistritype = (TextView) orderSettingView.findViewById(R.id.txtdistritype);
        txtTicket = (TextView) orderSettingView.findViewById(R.id.txtpreferential_ticket);
        txtCurrPoint = (TextView) orderSettingView.findViewById(R.id.txtcurrpoint);
        txtTotalAmount = (TextView) orderSettingView.findViewById(R.id.txtprice);
        txtCartPayType = (TextView) orderSettingView.findViewById(R.id.txtcartpaytype);
        txtRemoveCart = (TextView) findViewById(R.id.txtclearcart);
        txtReiceveAddress = (TextView) orderSettingView.findViewById(R.id.distributioner_address);
        edtorderSummary = (EditText) orderSettingView.findViewById(R.id.edtorderSummary);
        llnet= (LinearLayout) findViewById(R.id.llnet);
        reload= (Button) findViewById(R.id.reload);
        title = (TextView) lltitle.findViewById(R.id.title_store_name);
        title.setText(mspUtil.getStoreName());
        //toggleButton = (ToggleButton) findViewById(R.id.toggleButton01);
       // toggleButton.setEnabled(false);

    }

    /**
     * 初始化view 单击事件
     */
    private void initViewClickListener() {

        llyPickDate.setOnClickListener(this);
        llyPreferentialTicket.setOnClickListener(this);
        llyReiceveAddress.setOnClickListener(this);
        llyDistype.setOnClickListener(this);
        llyChoosePayType.setOnClickListener(this);
        llyScore.setOnClickListener(this);
        title.setOnClickListener(this);

        txtTotalPrice.setOnClickListener(this);
        btnPay.setOnClickListener(this);
        // txtRemoveCart.setOnClickListener(this);
       /* cartList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TempComCarBean pb = null;
                if (view instanceof TextView) {
                    pb = (TempComCarBean) view.getTag();
                } else {
                    TextView ctv = (TextView) view
                              .findViewById(R.id.listitem_title);
                    pb = (TempComCarBean) ctv.getTag();
                }
                if (pb == null)
                    return;
                Intent intent = new Intent(Cart.this, CommodityViewActivity.class);
                intent.putExtra("id", Long.parseLong(pb.getId()));
                intent.putExtra("storeId", GlobalContext.getInstance().getSpUtil().getStoreId()); // FIXME 店铺ID
                startActivity(intent);
            }
        });
        */
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.llydistrdate: //选择日期
                strDistritDate = getResources().getStringArray(R.array.DistritDate);
                new BottomAlertDialog(Cart.this, txtistridate, strDistritDate).show();
                break;
            case R.id.llypreferentialticket: //抵用券
                /*if (voucherList.size() > 0) {
                    intent = new Intent(Cart.this, PreferentialTicket.class);
                    intent.putExtra("voucherList", JSON.toJSONString(voucherList));
                    startActivityForResult(intent, 1);
                }*/
                Toast.makeText(Cart.this, getString(R.string.no_support_voucher), 200).show();
                break;
            case R.id.llyscore://积分
                Toast.makeText(Cart.this, getString(R.string.no_support_score), Toast.LENGTH_SHORT).show();
                break;
            case R.id.llypaytype://支付方式
                strPayType = getResources().getStringArray(R.array.paytype);
                new BottomAlertDialog(Cart.this, txtCartPayType, strPayType).show();
                break;

            case R.id.btnpayorder: //支付订单
                if (validateOrderData()) {//验证订单数据
                    if (orderId == 0L) {
                        UIUtil.showWaitDialog(Cart.this);
                        List<Integer> hourList = new ArrayList<Integer>();
                        ;
                        if (txtistridate.getText().toString().equals(strDistritDate[0])) {
                            hourList.add(8);
                            hourList.add(9);
                            hourList.add(10);
                            hourList.add(11);
                        } else if (txtistridate.getText().toString().equals(strDistritDate[1])) {
                            hourList.add(14);
                            hourList.add(15);
                            hourList.add(16);
                            hourList.add(17);
                        } else {
                            hourList.add(18);
                            hourList.add(19);
                            hourList.add(20);

                        }
                        List<Long> vList = new ArrayList<Long>();
                        OrderCommitRequest orderCommitRequest = null;
                        if (txtCartPayType.getText().toString().equals(strPayType[1])) { //采用支付宝支fu
                            orderCommitRequest = OrderUtils.assembleOrderData(mspUtil.getStoreId(), OrderTypeEnum.STANDARD, bdTotalprice.doubleValue(), PayMethodEnum.ALIPAY, 0, vList, itemList.size(), mspUtil.getdefaultReceiveAddress().equals("") ? mAddressId : Long.parseLong(addressList.get(3)), hourList, txtDistritype.getText().toString().equals(strDistritType[0]) ? DeliveryModeEnum.DELIVERY : DeliveryModeEnum.PICKUP, edtorderSummary.getText().toString(), cartItemIdList);
                            commitOrder(orderCommitRequest);
                            // } else if (txtCartPayType.getText().toString().equals(strPayType[1])) {//采用微信支付
                        } else { //微笑卡支付
                            orderCommitRequest = OrderUtils.assembleOrderData(mspUtil.getStoreId(), OrderTypeEnum.STANDARD, bdTotalprice.doubleValue(), PayMethodEnum.SMILE_CARD, 0, vList, itemList.size(), mspUtil.getdefaultReceiveAddress().equals("") ? mAddressId : Long.parseLong(addressList.get(3)), hourList, txtDistritype.getText().toString().equals(strDistritType[0]) ? DeliveryModeEnum.DELIVERY : DeliveryModeEnum.PICKUP, edtorderSummary.getText().toString(), cartItemIdList);
                            commitOrder(orderCommitRequest);
                        }
                    } else {
                        if (txtCartPayType.getText().toString().equals(strPayType[1])) { //采用支付宝支fu
                            ToAliPay(alipayOrderKeyStr);
                        } else { //微笑卡支付
                            showPayDialog(orderNum);
                        }
                    }

                }

                break;
            case R.id.llyreiceveaddress: //地址管理
                intent = new Intent(Cart.this, AddressManagerActivity.class);
                intent.putExtra("isEditAddress", "false");
                startActivityForResult(intent, 1);
                break;
            case R.id.llydistype://配送方式
                strDistritType = getResources().getStringArray(R.array.DistritType);
                new BottomAlertDialog(Cart.this, txtDistritype, strDistritType).show();
                break;

            case R.id.txtclearcart://清空购物车

                Toast.makeText(Cart.this, "此功能暂未开放,敬请期待", 100).show();
               /* final Dialog dialog=DialogUtil.getCustomeDilogForNormal(Cart.this);
                TextView title= (TextView) dialog.findViewById(R.id.dialog_title);
                title.setText(R.string.removecart);
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
                        TempComCarDBTask.getInstance().removeTempCarCom(null);
                        cartList.setVisibility(View.GONE);
                        cartBottomBar.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                        imgRemoveCart.setVisibility(View.GONE);
                        dialog.cancel();
                    }
                });*/
            case R.id.title_store_name://title点击事件
                Intent titleintent = new Intent(Cart.this, MainActivity.class);
                startActivity(titleintent);
                break;

        }
    }

    /**
     * 验证order 数据
     */
    private boolean validateOrderData() {
        boolean result = true;
        if (TextUtils.isEmpty(txtReiceveAddress.getText())) {
            Toast.makeText(Cart.this, getString(R.string.input_reiceveaddress), Toast.LENGTH_SHORT).show();
            result = false;
        }

        if (txtDistritype.getText().equals(getString(R.string.cart_choose_dstritype))) {
            Toast.makeText(Cart.this, getString(R.string.input_cart_choose_dstritype), Toast.LENGTH_SHORT).show();
            result = false;
        }

        if (txtistridate.getText().equals(getString(R.string.cart_choose_distridate))) {
            Toast.makeText(Cart.this, getString(R.string.input_cart_choose_distridate), Toast.LENGTH_SHORT).show();
            result = false;
        }

        if (txtCartPayType.getText().equals(getString(R.string.choose_pay_type))) {
            Toast.makeText(Cart.this, getString(R.string.input_choose_pay_type), Toast.LENGTH_SHORT).show();
            result = false;
        }

      /*  if (strPayType != null) {
            if (txtCartPayType.getText().equals(strPayType[1])) {
                Toast.makeText(Cart.this, getString(R.string.no_support_weixin_pay), Toast.LENGTH_SHORT).show();
                result = false;
            }

           if (txtCartPayType.getText().equals(strPayType[2])) {
                AppMsg.makeText(Cart.this, getString(R.string.no_support_smilecard), AppMsg.STYLE_INFO).show();
                result = false;
            }
        }*/


        return result;


    }


    /**
     * 弹出付款对话框
     */
    private void showPayDialog(final String orderNum) {
        paymentDialog = DialogUtil.getCustomDialog(Cart.this);
        paymentDialog.setContentView(R.layout.pay_dialog);


        TextView txtOrderNo = (TextView) paymentDialog.findViewById(R.id.txtorderno);
        TextView txtTotalAmount = (TextView) paymentDialog.findViewById(R.id.txtamount);
        final TextView txtPayType = (TextView) paymentDialog.findViewById(R.id.txtpaytype);
        txtCardBalances = (TextView) paymentDialog.findViewById(R.id.txtbalance);
        Button btnRecharge = (Button) paymentDialog.findViewById(R.id.btnRecharge);
        edtPayCode = (EditText) paymentDialog.findViewById(R.id.edtpaycode);
        Button btnGetPayCode = (Button) paymentDialog.findViewById(R.id.btngetpaycode);
        Button btnCancle = (Button) paymentDialog.findViewById(R.id.btncancle);
        Button btnPayment = (Button) paymentDialog.findViewById(R.id.btnpayment);
        txtTotalAmount.setText(MathUtils.formatDataForBackBigDecimal(bdTotalprice.doubleValue()) + "元");
        txtOrderNo.setText("订单号:" + orderNum);
        txtPayType.setText(txtCartPayType.getText());
        new GetSmileCardBalanceTask().execute();


        //充值
        btnRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Cart.this, ReChargeActivity.class);
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
                if (smileCardBalance < bdTotalprice.doubleValue()) {
                    Toast.makeText(Cart.this, getString(R.string.smilecardbalance_no_full), Toast.LENGTH_SHORT).show();
                    return;
                }
                UIUtil.showWaitDialog(Cart.this);
                new GetSmileCardPaySmsCodeTask().execute(orderId);

            }
        });

        //支付订单
        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edtPayCode.getText())) {
                    Toast.makeText(Cart.this, getString(R.string.input_sms_code), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (smileCardBalance < bdTotalprice.doubleValue()) {
                    Toast.makeText(Cart.this, getString(R.string.smilecardbalance_no_full), Toast.LENGTH_SHORT).show();
                    return;

                }
                UIUtil.showWaitDialog(Cart.this);
                simleCardPay(orderId, edtPayCode.getEditableText().toString(), payKey);


            }
        });


    }

    /**
     * 获得当前购物车数据
     */
    private class GetCartDataTask extends
              MyAsyncTask<String, Void, List<TempComCarBean>> {
        @Override
        protected List<TempComCarBean> doInBackground(String... params) {

            List<CartItem> cartItemList = CommodityDao.getCartItemList();
            allCartCount = 0;
            if (cartItemList != null) {
                if (cartItemList.size() > 0) {
                    TempComCarDBTask.getInstance().removeTempCarCom(null);
                    TempComCarBean tempComCarBean = null;
                    for (CartItem cartItem : cartItemList) {

                        tempComCarBean = new TempComCarBean();
                        cartItemIdList.add(cartItem.getId());
                        tempComCarBean.setId(String.valueOf(cartItem.getItemId()));
                        tempComCarBean.setCartItemId(cartItem.getId());
                        tempComCarBean.setSingleComPicUrl(cartItem.getItem().getPictureUrl());
                        tempComCarBean.setSingleCommodPrice(cartItem.getItem().getPrice());
                        tempComCarBean.setCommodName(cartItem.getItem().getName());
                        tempComCarBean.setCommodTotalCount(cartItem.getQuantity().intValue());
                        tempComCarBean.setCommodTotalPrice(MathUtils.getTotalAmount(cartItem.getItem().getPrice(), cartItem.getQuantity()));

                        TempComCarDBTask.getInstance().addOrUpdateItemToTempCar(tempComCarBean);
                    }

                } else {
                    TempComCarDBTask.getInstance().removeTempCarCom(null);
                }
            }

            itemList = TempComCarDBTask.getInstance().getTempComCarList();

            bdTotalprice = TempComCarDBTask.getInstance().getAllOrderPrice();
            return itemList;
        }

        @Override
        protected void onPostExecute(List<TempComCarBean> list) {
            UIUtil.dismissDlg();
            if (list != null) {
                if (list.size() > 0) {
                   adapter = new CartAdapter(list, Cart.this, mspUtil);
                    ((CartAdapter) adapter).setMode(Attributes.Mode.Single);
                    cartList.setAdapter(adapter);
                    //adapter.notifyDataSetChanged();
                    txtTotalPrice.setText(MathUtils.formatDataForBackBigDecimal(bdTotalprice.doubleValue()).toString());
                    txtTotalAmount.setText(MathUtils.formatDataForBackBigDecimal(bdTotalprice.doubleValue()).toString());
                    allCartCount = TempComCarDBTask.getInstance().getTempComCarTotalCount();
                    txtGoodsCount.setText("共" + allCartCount + "件商品");
                    // new GetTicketTask().execute(getItemList(list));
                } else {
                    cartList.setVisibility(View.GONE);
                    cartBottomBar.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    //txtRemoveCart.setVisibility(View.GONE);


                }
            }

        }

    }


    /**
     * 删除购物车内单个cartitem
     */
    private class removeCartItem extends
              MyAsyncTask<Long, Void, BackResultBean> {


        @Override
        protected BackResultBean doInBackground(Long... params) {
            BackResultBean resultBean = CommodityDao.removeCartItemOrClear(params[0]);
            resultBean.setPosition(Integer.parseInt(String.valueOf(params[1])));

            return resultBean;

        }

        @Override
        protected void onPostExecute(BackResultBean resultBean) {
            UIUtil.dismissDlg();
            if (!resultBean.getCode().equals("1")) {
                Toast.makeText(Cart.this, resultBean.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                TempComCarDBTask.getInstance().removeTempCarCom(
                          itemList.get(resultBean.getPosition()).getId());
                double currentPrice = Double.parseDouble(df.format(bdTotalprice.doubleValue() - itemList.get(resultBean.getPosition()).getCommodTotalPrice()));

                itemList.clear();
                new GetCartDataTask().execute();

            }


        }

    }

    /**
     * 获得所有商品id list
     *
     * @param input
     * @return
     */
    private List<Long> getItemList(List<TempComCarBean> input) {
        ArrayList<Long> result = new ArrayList<Long>();
        for (TempComCarBean obj : input) {
            result.add(Long.parseLong(obj.getId()));
        }
        return result;
    }

    /**
     * 获得符合条件优惠劵
     */
    private class GetTicketTask extends
              MyAsyncTask<List<Long>, Void, List<Voucher>> {


        @Override
        protected List<Voucher> doInBackground(List<Long>... params) {
            return PreferentialDao.getVoucher(params[0]);

        }

        @Override
        protected void onPostExecute(List<Voucher> list) {
            if (list.size() > 0) {
                voucherList = list;
                txtTicket.setText(getString(R.string.cart_use_ticket) + list.size() + "张优惠券");
            } else {
                txtTicket.setText(getString(R.string.cart_no_ticket));
            }


        }

    }


    /**
     * 获得会员积分
     */
    private class GetUserPointTask extends
              MyAsyncTask<List<Long>, Void, Double> {


        @Override
        protected Double doInBackground(List<Long>... params) {
            return PointDao.getUserPoint();

        }

        @Override
        protected void onPostExecute(Double point) {
            UIUtil.dismissDlg();
            //txtCurrPoint.setText("当前积分:" + point);


        }

    }


    /**
     * 获得收货地址
     */
    private class GetAddressTask extends
              MyAsyncTask<Void, Void, List<Location>> {


        @Override
        protected List<Location> doInBackground(Void... params) {
            return LocationDao.getAddressList();

        }

        @Override
        protected void onPostExecute(List<Location> list) {
            if (list != null) {
                if (list.size() > 0) {
                    addressList = AddressUtils.getDefaultAddress(list);
                    if (addressList.size() > 0) {
                        txtDisistributionerName.setText(addressList.get(0));
                        txtDisistributionerPhoneNum.setText(addressList.get(1));
                        txtReiceveAddress.setText(addressList.get(2));
                        mspUtil.setDefaultReceiveAddress(JSON.toJSONString(addressList));
                    } else {
                        txtReiceveAddress.setText(getString(R.string.order_add_reiceveaddress));
                    }
                }

            }


        }

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
            if (backResultBean != null) {
                if (backResultBean.getCode().equals("1")) {
                    Toast.makeText(Cart.this, getString(R.string.smscode_sended), Toast.LENGTH_SHORT).show();
                    payKey = backResultBean.getKeyStr();
                } else {
                    Toast.makeText(Cart.this, backResultBean.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }


        }

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
            smileCardBalance = point;
            txtCardBalances.setText("余额:" + MathUtils.formatDataForBackBigDecimal(point));
            paymentDialog.show();


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
                        UIUtil.showWaitDialog(Cart.this);
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
                            Toast.makeText(Cart.this, "支付结果确认中",
                                      Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(Cart.this, "支付失败",
                                      Toast.LENGTH_SHORT).show();

                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    Toast.makeText(Cart.this, "检查结果为：" + msg.obj,
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
     * call alipay sdk pay. 调用SDK支付
     */
    public void ToAliPay(final String orderInfo) {


        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {

                // 构造PayTask 对象
                PayTask alipay = new PayTask(Cart.this);
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
            if (order != null) {
                if (order.getStatus() == OrderStatusEnum.ORDER_PAID) {
                    showTips(R.mipmap.tips_success, "支付成功");
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {

                            Intent intent = new Intent(Cart.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    }, 1000);
                } else {
                    Toast.makeText(Cart.this, getString(R.string.pay_error), Toast.LENGTH_SHORT).show();

                }
            }


        }

    }

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
     * 添加or 更新商品Item
     */
    private class CreateorUpdateCartItemTask extends
              MyAsyncTask<TempComCarBean, Void, BackResultBean> {
        @Override
        protected BackResultBean doInBackground(TempComCarBean... params) {
            TempComCarBean item = params[0];
            BackResultBean resultBean = CommodityDao.updateCartItem(item.getCartItemId(), tempCartItem.getCount());
            return resultBean;
        }

        @Override
        protected void onPostExecute(BackResultBean resultBean) {

            UIUtil.dismissDlg();
            if (resultBean != null) {
                if (resultBean.getCode().equals("1")) {
                    //adapter.notifyDataSetChanged();
                    TempComCarDBTask.getInstance()
                              .updateItemCarsCountAndPrice(String.valueOf(tempCartItem.getCartItemId()), tempCartItem.getCount(),
                                        new BigDecimal(tempCartItem.getAmount()));

                    new GetCartDataTask().execute();
                } else {
                    Toast.makeText(Cart.this, resultBean.getMessage(), Toast.LENGTH_SHORT).show();
                    new GetCartDataTask().execute();

                }
            }


        }

    }

    /**
     * 微笑卡支付
     *
     * @param request
     */
    private void simleCardPay(final Long orderId, final String smsCode, final String payKey) {
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                UIUtil.dismissDlg();
                BackResultBean resultBean = (BackResultBean) msg.obj;
                if (msg.what == 1) {
                    UIUtil.showWaitDialog(Cart.this);
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
                BackResultBean resultBean = OrderDao.smileCardPay(orderId, smsCode, payKey != null ? payKey : "1");
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
     * 提交订单
     *
     * @param request
     */
    private void commitOrder(final OrderCommitRequest request) {
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                UIUtil.dismissDlg();
                BackResultBean resultBean = (BackResultBean) msg.obj;
                if (msg.what == 1) {
                    TempComCarDBTask.getInstance().removeTempCarCom(null);

                    UIUtil.showWaitDialog(Cart.this);
                    orderId = resultBean.getOrderId();
                    if (request.getPayMethod() != PayMethodEnum.SMILE_CARD) { //非微笑卡支付 ，需加密订单信息
                        getSignOrderInfo(resultBean.getOrderId());
                    } else {

                        orderNum = resultBean.getOrderNumber();
                        UIUtil.dismissDlg();
                        showPayDialog(orderNum);

                    }


                } else if (msg.what == 0) {
                    showTips(R.mipmap.tips_error, resultBean.getMessage());
                }
            }
        };
        new Thread() {
            public void run() {
                Message msg = new Message();
                BackResultBean resultBean = OrderDao.commitOrder(request);
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
                    alipayOrderKeyStr = resultBean.getKeyStr();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            switch (resultCode) {
                case SysConstant.BACKRESULTCODE_CART_DISTRIBUTION_DATE://配送时间

                    txtistridate.setText(data.getStringExtra("pickDate"));
                    break;
                case SysConstant.BACKRESULTCODE_CART_REICEVEADDRESS://收货地址
                    addressList.clear();
                    String name = data.getStringExtra("name");
                    addressList.add(name);
                    String phoneNum = data.getStringExtra("phone");
                    addressList.add(phoneNum);
                    String address = data.getStringExtra("defaultAddress");
                    addressList.add(address);

                    String addressId = data.getStringExtra("addressId");
                    addressList.add(addressId);
                    mAddressId = Long.parseLong(addressId);


                    txtReiceveAddress.setText(address);
                    txtDisistributionerName.setText(name);
                    txtDisistributionerPhoneNum.setText(phoneNum);

                    break;
                case SysConstant.BACKRESULTCODE_CART_DISCOUNT_TICKET://抵用券
                    Voucher voucher = JSON.parseObject(data.getStringExtra("voucher"), Voucher.class);
                    txtTicket.setText(getString(R.string.center_mark_money) + " " + voucher.getAmount().toString());
                    break;

            }
        }
    }

    public class CartAdapter extends RecyclerSwipeAdapter<CartAdapter.SimpleViewHolder> {



        private Context mContext;
        private List<TempComCarBean> list;
        SharePreferenceUtil mspUtil;

        //protected SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);

        public CartAdapter(List<TempComCarBean> list, Context context,SharePreferenceUtil mspUtil) {
            this.list = list;
            this.mContext = context;
            this.mspUtil=mspUtil;

        }

        @Override
        public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_item_cart_layout, parent, false);
            return new SimpleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
            /*viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            viewHolder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
                @Override
                public void onOpen(SwipeLayout layout) {
                    //YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
                }
            });*/

            final TempComCarBean item = list.get(position);
            viewHolder.itemView.setTag(item);
            viewHolder.itemTitle.setText(item.getCommodName());
            viewHolder.itemTitle.setTag(item);
            viewHolder.aasItem.setNum(item.getCommodTotalCount());
            String price = MathUtils.formatDataForBackBigDecimal(item.getCommodTotalPrice()).toString();
            Spannable WordtoSpan = new SpannableString(getString(R.string.center_mark_money) + " " + price);
            WordtoSpan.setSpan(new TextAppearanceSpan(Cart.this, R.style.style1), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            WordtoSpan.setSpan(new TextAppearanceSpan(Cart.this, R.style.style2), 2, (getString(R.string.center_mark_money) + " " + price).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.unitPrice.setText(WordtoSpan,TextView.BufferType.SPANNABLE);
            viewHolder.aasItem.setNum(item.getCommodTotalCount());
            if (item.getSingleComPicUrl() != null)
                ImageLoaderUtil.getImageLoaderInstance().displayImage(item.getSingleComPicUrl(), viewHolder.imgThumbnails);


            viewHolder.aasItem.setOnNumChangeListener(new AddAndSubView.OnNumChangeListener() {

                @Override
                public void onNumChange(View view, int num, String type) {

                    tempCartItem = new TempCartItem();
                    double singelAmount = MathUtils.getTotalAmount(num, item.getSingleCommodPrice());
                    tempCartItem.setAmount(singelAmount);
                    tempCartItem.setCount(num);
                    tempCartItem.setCartItemId(item.getCartItemId());
                    UIUtil.showWaitDialog(Cart.this);
                    if (num != 0)
                        new CreateorUpdateCartItemTask().execute(item);
                    else
                        new removeCartItem().execute(item.getCartItemId(), Long.parseLong(String.valueOf(position)));

                }
            });

            viewHolder.imgRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIUtil.showWaitDialog(Cart.this);
                    new removeCartItem().execute(itemList.get(position).getCartItemId(), Long.parseLong(String.valueOf(position)));
                }
            });


        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return R.id.swipe;
        }


        public  class SimpleViewHolder extends RecyclerView.ViewHolder {
            TextView itemTitle;
            TextView count;
            ImageView imgThumbnails;
            TextView unitPrice;
            AddAndSubView aasItem;
            SwipeLayout swipeLayout;
            ImageView imgRemove;

            public SimpleViewHolder(View itemView) {
                super(itemView);
                itemTitle = (TextView) itemView.findViewById(R.id.listitem_title);
                imgThumbnails = (ImageView) itemView.findViewById(R.id.listitem_Thumbnails);
                unitPrice = (TextView)itemView.findViewById(R.id.listitem_unitprice);
                aasItem = (AddAndSubView) itemView.findViewById(R.id.aasComEdite);
                swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
                imgRemove = (ImageView) swipeLayout.findViewById(R.id.trash);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }
        }
    }

}
