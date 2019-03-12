package com.mkh.mobilemall.ui.activity.commodity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fish.mkh.CardInfoActivity;
import com.fish.mkh.util.UIUtil;
import com.jude.rollviewpager.RollPagerView;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.bean.BackResultBean;
import com.mkh.mobilemall.bean.TempComCarBean;
import com.mkh.mobilemall.dao.CommodityDao;
import com.mkh.mobilemall.dao.PointDao;
import com.mkh.mobilemall.support.asyncTask.MyAsyncTask;
import com.mkh.mobilemall.support.db.TempComCarDBTask;
import com.mkh.mobilemall.support.http.http.HttpUtility;
import com.mkh.mobilemall.ui.activity.auth.LoginActivity;
import com.mkh.mobilemall.ui.activity.cart.CartForListview;
import com.mkh.mobilemall.ui.adapter.EvaluationAdapter;
import com.mkh.mobilemall.ui.adapter.RecommendAdapter;
import com.mkh.mobilemall.ui.adapter.TestAdapter;
import com.mkh.mobilemall.ui.base.BaseActivity;
import com.mkh.mobilemall.ui.utils.ShoppingCartUtils;
import com.mkh.mobilemall.ui.utils.ShowToastUtils;
import com.mkh.mobilemall.ui.widget.AddAndSubView;
import com.mkh.mobilemall.utils.AnimUtils;
import com.mkh.mobilemall.utils.BadgeView;
import com.mkh.mobilemall.utils.ListViewUtil;
import com.mkh.mobilemall.utils.MathUtils;
import com.xiniunet.api.XiniuResponse;
import com.xiniunet.api.domain.ecommerce.ItemEvaluation;
import com.xiniunet.api.domain.ecommerce.SimpleItem;
import com.xiniunet.api.domain.master.Item;
import com.xiniunet.api.domain.system.Passport;
import com.xiniunet.api.request.ecommerce.FavoriteItemCreateRequest;
import com.xiniunet.api.request.ecommerce.FavoriteItemDeleteRequest;
import com.xiniunet.api.request.ecommerce.RecommendItemGetAllListRequest;
import com.xiniunet.api.request.master.ItemGetRequest;
import com.xiniunet.api.response.ecommerce.FavoriteItemCreateResponse;
import com.xiniunet.api.response.ecommerce.FavoriteItemDeleteResponse;
import com.xiniunet.api.response.ecommerce.RecommendItemGetAllListResponse;
import com.xiniunet.api.response.master.ItemGetResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class CommodityViewActivity extends BaseActivity {
    // 商品名称
    @Bind(R.id.tvItemName)
    TextView name;
    // 商品价格
    @Bind(R.id.tvItemPrice)
    TextView price;
    // 商品单位
    @Bind(R.id.tvItemUom)
    TextView uom;
    // 商品原价
    @Bind(R.id.tvItemOriginalPrice)
    TextView originalPrice;
    // 商品描述
    @Bind(R.id.tvItemDescText)
    WebView description;
    // 食用方法
    @Bind(R.id.tvItemEdibleMethodText)
    TextView edibleMethod;
    // 评价列表
    @Bind(R.id.evaluationList)
    ListView evaluationListView;
    // 评价标题
    @Bind(R.id.tvItemEvaluationTitle)
    TextView evaluationTitle;
    // 推荐商品列表
    @Bind(R.id.gridView)
    GridView gridView;
    // 商品主图
    @Bind(R.id.roll_view_pager)
    RollPagerView mainPicture;
    // 更多评论的标题
    @Bind(R.id.moreEvaluationLayout)
    LinearLayout moreEvaluationLayout;
    // 商品剩余库存
    @Bind(R.id.itemQuantity)
    TextView quantity;

    // 返回按钮
    @Bind(R.id.btnBack)
    ImageButton btnBack;
    // 分享按钮
    @Bind(R.id.btnShare)
    ImageButton btnShare;
    // 收藏按钮
    @Bind(R.id.btnLike)
    ImageButton btnLike;

    @Bind(R.id.llyfoodmethod)
    LinearLayout llyFoodMethod;
    @Bind(R.id.aasComEdite)
    AddAndSubView aasComEdite;
    @Bind(R.id.reload)
    Button reload;
    LinearLayout llnet, llorignal;


    @Bind(R.id.btn_go_settlement)
    Button btnSettlement;
    @Bind(R.id.main_wallet_layout)
    RelativeLayout walletLayout;
    @Bind(R.id.main_cart_layout)
    RelativeLayout cartLayout;
    @Bind(R.id.imageView_comment)
    ImageView imagecomment;
    @Bind(R.id.txtcomPrice)
    TextView txtPrice;
    private String opertionType;// 用来标记当前是 添加 or 减 的动作

    // 当前的商品信息
    Item item;
    // 当前商品的ID
    Long itemId;
    // 当前店铺的ID
    Long storeId;

    // 当前商品是否已被收藏
    boolean isLike = false;
    // 当前商品的评价列表
    List<ItemEvaluation> evaluationList;
    // 当前商品的评价总数
    Long evaluationCount = 0L;
    // 推荐商品列表
    List<SimpleItem> recommendList;

    private OnekeyShare oks;

    private List<View> views;
    Passport passport;
    TempComCarBean tempComCarBean = null;
    int[] start_location;
    private ImageView imgTrajectory = null;
    @Bind(R.id.iconCart)
    ImageView imgCart;
    private BadgeView badgeViewCount;
    @Bind(R.id.txtwalletprice)
    TextView txtWalletBalances;
    //记录当前选中的位置
    private int currentIndex;
    @Bind(R.id.view)
    View v;


    private long cartItemId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_commodity_view;
    }

    @Override
    protected boolean hasBackButton() {
        return false;
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.toolbar_title_commodity;
    }

    @Override
    protected int getActionBarCustomView() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        passport = GlobalContext.getInstance().getSpUtil().getUserInfo();
        UIUtil.showWaitDialog(CommodityViewActivity.this);
        initView();
        initData();
        if (passport != null)
            new GetSmileCardBalanceTask().execute();//获得微笑卡余额
    }

    private void initView() {
        llnet = (LinearLayout) findViewById(R.id.llnet);
        llorignal = (LinearLayout) findViewById(R.id.llorignal);
        itemId = getIntent().getLongExtra("id", 0L);
        storeId = getIntent().getLongExtra("storeId", 0L);
        getData(itemId);
        gridView.setOnItemClickListener(new RecommendListClickListener());
        moreEvaluationLayout.setOnClickListener(new EvaluationClickListener());
        btnBack.setOnClickListener(new BackListener());
        btnShare.setOnClickListener(new ShareListener());
        btnLike.setOnClickListener(new LikeListener());
        WebSettings settings = description.getSettings();
        settings.setDefaultFontSize(14);
        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        tempComCarBean = TempComCarDBTask.getInstance().getTempComByItemId(String.valueOf(itemId));
        aasComEdite.setNum(tempComCarBean != null ? tempComCarBean.getCommodTotalCount() : 0);
        aasComEdite.setOnNumChangeListener(new AddAndSubView.OnNumChangeListener() {
            @Override
            public void onNumChange(View view, int num, String type) {
                if (passport != null) {
                    opertionType = type;
                    UIUtil.showWaitDialog(CommodityViewActivity.this);
                    start_location = new int[2];
                    view.getLocationInWindow(start_location);
                    imgTrajectory = new ImageView(CommodityViewActivity.this);
                    imgTrajectory.setImageResource(R.mipmap.sign);
                    item.setQuantity(num);
                    if (tempComCarBean != null)
                        item.setId(tempComCarBean.getCartItemId());
                    if (num != 0)
                        new CreateorUpdateCartItemTask().execute(item);
                    else {
                        new removeCartItem().execute(tempComCarBean.getCartItemId());
                    }

                } else {
                    Intent intent = new Intent(CommodityViewActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }


            }
        });

        badgeViewCount = new BadgeView(CommodityViewActivity.this, v);
        badgeViewCount.setTextColor(Color.WHITE);
        badgeViewCount.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        badgeViewCount.setTextSize(12);
        int count = TempComCarDBTask.getInstance().getTempComCarTotalCount();
        if (count != 0) {
            badgeViewCount.setText(String.valueOf(count));

            badgeViewCount.show();
        }

        walletLayout.setOnClickListener(this);
        cartLayout.setOnClickListener(this);
        btnSettlement.setOnClickListener(this);

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
            UIUtil.dismissDlg();
            if(point!=null) {
                Spannable WordtoSpan = new SpannableString(getString(R.string.center_mark_money) + " " + MathUtils.formatDataForBackBigDecimal(point));
                WordtoSpan.setSpan(new TextAppearanceSpan(CommodityViewActivity.this, R.style.style02), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                WordtoSpan.setSpan(new TextAppearanceSpan(CommodityViewActivity.this, R.style.style2), 2, (getString(R.string.center_mark_money) + " " + MathUtils.formatDataForBackBigDecimal(point)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                txtWalletBalances.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
            }


        }

    }


    private void initData() {

        BigDecimal price = TempComCarDBTask.getInstance().getAllOrderPrice();
        if (price != null) {
            Spannable WordtoSpan = new SpannableString(getString(R.string.center_mark_money) + " " + MathUtils.formatDataForBackBigDecimal(price.doubleValue()));
            WordtoSpan.setSpan(new TextAppearanceSpan(CommodityViewActivity.this, R.style.style02), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            WordtoSpan.setSpan(new TextAppearanceSpan(CommodityViewActivity.this, R.style.style2), 2, (getString(R.string.center_mark_money) + " " + MathUtils.formatDataForBackBigDecimal(price.doubleValue())).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            txtPrice.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
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
            if (resultBean != null)
                resultBean.setOrderId(params[0]);

            return resultBean;

        }

        @Override
        protected void onPostExecute(BackResultBean resultBean) {
            UIUtil.dismissDlg();
            if (resultBean != null) {
                if (!resultBean.getCode().equals("1")) {
                    Toast.makeText(CommodityViewActivity.this, resultBean.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    tempComCarBean = null;
                    TempComCarDBTask.getInstance().removeTempCarComByCartItemId(String.valueOf(resultBean.getOrderId()));
                    BigDecimal price = TempComCarDBTask.getInstance().getAllOrderPrice();
                    //动画。//动画。
                    AnimUtils.setAnimForSub(CommodityViewActivity.this, Integer.MAX_VALUE, imgTrajectory, start_location, imgCart, badgeViewCount);
                    item.setId(itemId);
                    if (price != null)
                        txtPrice.setText(getString(R.string.center_mark_money) + " " + MathUtils.formatDataForBackBigDecimal(price.doubleValue()));
                    else
                        txtPrice.setText(getString(R.string.default_money_number));


                }
            }

        }

    }


    /**
     * 添加or 更新商品Item
     */
    private class CreateorUpdateCartItemTask extends
              MyAsyncTask<Item, Void, BackResultBean> {


        @Override
        protected BackResultBean doInBackground(Item... params) {
            item = params[0];
            BackResultBean resultBean = null;

            if (tempComCarBean == null)
                resultBean = CommodityDao.addOrUpdateCartItem(item.getId(), item.getQuantity());
            else
                resultBean = CommodityDao.updateCartItem(item.getId(), item.getQuantity());
            return resultBean;
        }

        @Override
        protected void onPostExecute(BackResultBean resultBean) {
            UIUtil.dismissDlg();
            if (resultBean != null) {

                if (resultBean.getCode().equals("1")) {

                  /* cartItemId=item.getId();
                    item.setId(itemId);*/
                    if (tempComCarBean == null)
                        ShoppingCartUtils.saveOrUpdateCartData(item, item.getQuantity(), resultBean.getOrderId() != 0L ? resultBean.getOrderId() : item.getId());
                    else {
                        cartItemId = item.getId();
                        item.setId(itemId);
                        ShoppingCartUtils.saveOrUpdateCartData(item, item.getQuantity(), resultBean.getOrderId() != 0L ? resultBean.getOrderId() : cartItemId);

                    }
                    List<TempComCarBean> l = TempComCarDBTask.getInstance().getTempComCarList();
                    tempComCarBean = TempComCarDBTask.getInstance().getTempComByItemId(String.valueOf(itemId));
                    BigDecimal price = TempComCarDBTask.getInstance().getAllOrderPrice();
                    //动画。//动画。
                    if (opertionType.equals("+"))
                        AnimUtils.setAnim(CommodityViewActivity.this, Integer.MAX_VALUE, imgTrajectory, start_location, imgCart, badgeViewCount);
                    else
                        AnimUtils.setAnimForSub(CommodityViewActivity.this, Integer.MAX_VALUE, imgTrajectory, start_location, imgCart, badgeViewCount);

                    if (price != null) {
                        Spannable WordtoSpan = new SpannableString(getString(R.string.center_mark_money) + " " + MathUtils.formatDataForBackBigDecimal(price.doubleValue()));
                        WordtoSpan.setSpan(new TextAppearanceSpan(CommodityViewActivity.this, R.style.style2), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        WordtoSpan.setSpan(new TextAppearanceSpan(CommodityViewActivity.this, R.style.style3), 2, (getString(R.string.center_mark_money) + " " + MathUtils.formatDataForBackBigDecimal(price.doubleValue())).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        txtPrice.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
                        //txtPrice.setText(getString(R.string.center_mark_money) + " " + MathUtils.formatDataForBackBigDecimal(price.doubleValue()));
                    }

                } else {
                    Toast.makeText(CommodityViewActivity.this, resultBean.getMessage(), Toast.LENGTH_SHORT).show();
                    aasComEdite.setNum(item.getQuantity() - 1);
                }
            } else {
                Toast.makeText(CommodityViewActivity.this, getString(R.string.interface_timeout), Toast.LENGTH_SHORT).show();
            }


        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            TempComCarBean tempComCarBean = TempComCarDBTask.getInstance().getTempComByItemId(String.valueOf(itemId));
            if (tempComCarBean != null)
                aasComEdite.setNum(tempComCarBean.getCommodTotalCount());
            else
                aasComEdite.setNum(0);
            BigDecimal bigDecimal = TempComCarDBTask.getInstance().getAllOrderPrice();
            if (bigDecimal != null) {
                int count = TempComCarDBTask.getInstance().getTempComCarTotalCount();
                badgeViewCount.setText(String.valueOf(count));
                badgeViewCount.show();
                txtPrice.setText(getString(R.string.center_mark_money) + " " + MathUtils.formatDataForBackBigDecimal(bigDecimal.doubleValue()));

            } else {
                txtPrice.setText(getString(R.string.center_mark_money) + " 0.00");
                badgeViewCount.hide();
            }
            if (passport != null) {
                UIUtil.showWaitDialog(CommodityViewActivity.this);
                new GetSmileCardBalanceTask().execute();
            }
        }
    }


    /**
     * 获取页面数据
     *
     * @param id 商品的ID
     */
    private void getData(final Long id) {
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                UIUtil.dismissDlg();
                if (msg.what == 0) {
                    XiniuResponse response = (XiniuResponse) msg.obj;
                    Toast.makeText(CommodityViewActivity.this, response.getErrors().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                } else if (msg.what == 1) {

                    name.setText(item.getName());
                    price.setText(getString(R.string.center_mark_money) + " " + MathUtils.formatDataForBackBigDecimal(item.getCurrentPrice()) + "");
                    uom.setText(item.getUom());
                    if (item.getIsFav()) {
                        btnLike.setBackgroundResource(R.drawable.item_detail_like);
                        isLike = true;
                    }


                    final List<String> imgurlList = item.getPictureUrls();
                    if (imgurlList != null) {
                        List<String> picsList = new ArrayList<String>();
                        picsList.add(item.getPictureUrl());
                        for (int i = 0; i < imgurlList.size(); i++) {
                            picsList.add(imgurlList.get(i));
                        }


                        mainPicture.setAdapter(new TestAdapter(picsList));

                    }
                    // 如果原价不为空且与现价不同
                    if (item.getOriginalPrice() != null && !item.getOriginalPrice().equals(item.getCurrentPrice())) {
                        Spannable WordtoSpan = new SpannableString(getString(R.string.center_mark_money) + " " + item.getOriginalPrice() + "");
                        WordtoSpan.setSpan(new TextAppearanceSpan(CommodityViewActivity.this, R.style.style0), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        WordtoSpan.setSpan(new TextAppearanceSpan(CommodityViewActivity.this, R.style.style1), 2, (getString(R.string.center_mark_money) + " " + item.getOriginalPrice() + "").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        originalPrice.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
                        //originalPrice.setText(getString(R.string.center_mark_money)+" "+ item.getOriginalPrice() + "");
                        originalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        originalPrice.setText("");
                    }
                    quantity.setText("库存：" + item.getQuantity());


                    // 如果原价不为空且与现价不同
                    if (item.getOriginalPrice() != null && !item.getOriginalPrice().equals(item.getCurrentPrice())) {
                        originalPrice.setText(getString(R.string.center_mark_money) + " " + item.getOriginalPrice() + "");
                        originalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        originalPrice.setText("");
                    }
                    quantity.setText("库存：" + item.getQuantity());


                    if (item.getDescription() != null) {
                        try {
                            StringBuilder head = new StringBuilder("<html><head>"
                                      + "<style type=\"text/css\">body{color:#666666;font-size:14px}"
                                      + "</style></head>"
                                      + "<body>");
                            StringBuilder sb = new StringBuilder(item.getDescription());
                            head.append(sb);
                            head.append("</body>" + "</html>");


                            description.loadDataWithBaseURL(null, head.toString(), "text/html", "UTF-8", null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }


                    if (item.getEdibleMethod() != null) {
                        if (!item.getEdibleMethod().equals(""))
                            edibleMethod.setText(item.getEdibleMethod());
                        else
                            llyFoodMethod.setVisibility(View.GONE);
                    }

                    evaluationTitle.setText("评论(" + evaluationCount + ")");
                    if (evaluationCount <= 3) {
                        imagecomment.setVisibility(View.GONE);
                    }


                    // 设置评价列表

                    if (evaluationList != null) {
                        evaluationListView.setAdapter(new EvaluationAdapter(CommodityViewActivity.this, evaluationList));
                        ListViewUtil.setListViewHeight(evaluationListView);
                    }
                    // 设置推荐商品列表
                    if (recommendList != null) {
                        gridView.setAdapter(new RecommendAdapter(CommodityViewActivity.this, recommendList));
                        ListViewUtil.setGridViewHeight(gridView);
                    }


                } else if (msg.what == -1) {
                    Toast.makeText(CommodityViewActivity.this, getString(R.string.interface_timeout), Toast.LENGTH_SHORT).show();
                    llnet.setVisibility(View.VISIBLE);
                    llorignal.setVisibility(View.GONE);
                    reload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                            if (info != null && info.isAvailable()) {
                                UIUtil.showWaitDialog(CommodityViewActivity.this);
                                llnet.setVisibility(View.GONE);
                                llorignal.setVisibility(View.VISIBLE);
                                itemId = getIntent().getLongExtra("id", 0L);
                                storeId = getIntent().getLongExtra("storeId", 0L);
                                getData(itemId);
                            } else {
                                ShowToastUtils.showToast(getString(R.string.network_not_connected), CommodityViewActivity.this);
                            }

                        }
                    });
                }
            }
        };

        new Thread() {
            public void run() {
                Message msg = new Message();

                try {
                    // 查询物料信息
                    ItemGetRequest request = new ItemGetRequest();
                    request.setId(id);
                    ItemGetResponse itemGetResponse = HttpUtility.client.execute(request, passport);
                    if (itemGetResponse.hasError()) {
                        msg.what = 0;
                        msg.obj = itemGetResponse;
                        handler.sendMessage(msg);
                        return;
                    } else {
                        // 设置物料信息
                        item = itemGetResponse.getItem();
                    }

                   /* // 查询物料评价
                    ItemEvaluationFindRequest findRequest = new ItemEvaluationFindRequest();
                    findRequest.setItemId(id);
                    findRequest.setPageSize(3);
                    ItemEvaluationFindResponse findResponse = HttpUtility.client.execute(findRequest, passport);
                    if (findResponse.hasError()) {
                        msg.what = 0;
                        msg.obj = findResponse;
                        handler.sendMessage(msg);
                        return;
                    } else {
                        evaluationList = findResponse.getResult();
                        evaluationCount = findResponse.getTotalCount();
                    }*/

                    // 查询推荐商品列表
                    RecommendItemGetAllListRequest listRequest = new RecommendItemGetAllListRequest();
                    listRequest.setItemId(id);
                    listRequest.setStoreId(itemId);
                    RecommendItemGetAllListResponse listResponse = HttpUtility.client.execute(listRequest, passport);
                    if (listResponse.hasError()) {
                        msg.what = 0;
                        msg.obj = listResponse;
                        handler.sendMessage(msg);
                        return;
                    } else {
                        recommendList = listResponse.getResult();
                    }
                    msg.what = 1;

                } catch (Exception e) {
                    msg.what = -1;

                } finally {

                    handler.sendMessage(msg);
                }

            }

        }.start();
    }


    /**
     * 评价列表的点击事件
     */
    private class EvaluationClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // 跳转至评价列表
            /*Intent intent = new Intent(CommodityViewActivity.this, EvaluationListActivity.class);
            intent.putExtra("id", itemId);
            startActivity(intent);*/
            ShowToastUtils.showToast("功能即将上线,敬请期待", CommodityViewActivity.this);
        }
    }

    /**
     * 推荐商品列表的点击事件
     */
    private class RecommendListClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 跳转至对应商品详情页面
            Intent intent = new Intent(CommodityViewActivity.this, CommodityViewActivity.class);
            intent.putExtra("id", recommendList.get(position).getId());
            startActivity(intent);
        }
    }

    /**
     * 返回按钮
     */
    private class BackListener implements ImageButton.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    /**
     * 分享按钮
     */
    private class ShareListener implements ImageButton.OnClickListener {
        @Override
        public void onClick(View v) {
            showShareDialog();


        }
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_go_settlement://结算
                if (TempComCarDBTask.getInstance().getTempComCarList().size() > 0) {
                    intent = new Intent(CommodityViewActivity.this, CartForListview.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(CommodityViewActivity.this, getString(R.string.cart_no_item), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.main_wallet_layout://钱包
                if (passport != null) {
                    intent = new Intent(CommodityViewActivity.this, CardInfoActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(CommodityViewActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.main_cart_layout://购物车
                if (passport != null) {
                    intent = new Intent(CommodityViewActivity.this, CartForListview.class);
                    startActivityForResult(intent, 1);
                    finish();
                } else {
                    intent = new Intent(CommodityViewActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;


        }

    }

    /**
     * 分享
     */
    public void showShareDialog() {
        // ShareSDK.initSDK(this);
        OnekeyShare onekeyShare = new OnekeyShare();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        onekeyShare.setTitle("分享");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        onekeyShare.setTitleUrl("http://m.mkh.cn");
        // text是分享文本，所有平台都需要这个字段
        onekeyShare.setText(item.getName() + "http://m.mkh.cn");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        onekeyShare.setImageUrl(item.getPictureUrl());
        onekeyShare.show(this);
    }

    /**
     * 收藏按钮
     */
    private class LikeListener implements ImageButton.OnClickListener {
        @Override
        public void onClick(View v) {
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 0) {
                        XiniuResponse response = (XiniuResponse) msg.obj;
                        Toast.makeText(CommodityViewActivity.this, response.getErrors().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                    } else if (msg.what == 1) {
                        btnLike.setBackgroundResource(R.drawable.item_detail_not_like);
                        Toast.makeText(CommodityViewActivity.this, "取消收藏", 200).show();
                        isLike = false;


                    } else if (msg.what == 2) {
                        btnLike.setBackgroundResource(R.drawable.item_detail_like);
                        Toast.makeText(CommodityViewActivity.this, "已加入收藏", 200).show();
                        isLike = true;
                    }
                }
            };
            new Thread() {
                public void run() {
                    Message msg = new Message();
                    try {
                        if (isLike) {
                            // 如果已经被收藏, 点击该按钮为取消收藏
                            FavoriteItemDeleteRequest request = new FavoriteItemDeleteRequest();
                            request.setItemId(item.getId());
                            FavoriteItemDeleteResponse response = HttpUtility.client.execute(request, passport);
                            if (response.hasError()) {
                                msg.what = 0;
                            } else {
                                msg.what = 1;
                            }
                            msg.obj = response;
                        } else {
                            // 如果未被收藏, 点击该按钮为收藏商品
                            FavoriteItemCreateRequest request = new FavoriteItemCreateRequest();
                            request.setItemId(itemId);
                            request.setStoreId(storeId);
                            FavoriteItemCreateResponse response = HttpUtility.client.execute(request, passport);
                            if (response.hasError()) {
                                msg.what = 0;
                            } else {
                                msg.what = 2;
                            }
                            msg.obj = response;
                        }

                        handler.sendMessage(msg);
                    } catch (Exception e) {
                        Toast.makeText(CommodityViewActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }.start();
        }
    }


}
