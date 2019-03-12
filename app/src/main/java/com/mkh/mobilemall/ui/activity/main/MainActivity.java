package com.mkh.mobilemall.ui.activity.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.fish.mkh.CardInfoActivity;
import com.fish.mkh.MemberCenterActivity;
import com.fish.mkh.MyOrderActivity;
import com.fish.mkh.util.UIUtil;
import com.jude.rollviewpager.RollPagerView;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.bean.BackResultBean;
import com.mkh.mobilemall.bean.PosItemBean;
import com.mkh.mobilemall.bean.TempComCarBean;
import com.mkh.mobilemall.dao.ActivitiesDao;
import com.mkh.mobilemall.dao.CategoryDao;
import com.mkh.mobilemall.dao.CommodityDao;
import com.mkh.mobilemall.dao.PointDao;
import com.mkh.mobilemall.support.appconfig.AppManager;
import com.mkh.mobilemall.support.asyncTask.MyAsyncTask;
import com.mkh.mobilemall.support.db.TempComCarDBTask;
import com.mkh.mobilemall.support.http.http.HttpUtility;
import com.mkh.mobilemall.support.update.UpdateManager;
import com.mkh.mobilemall.ui.activity.auth.LoginActivity;
import com.mkh.mobilemall.ui.activity.cart.Cart;
import com.mkh.mobilemall.ui.activity.test.RecylerTest;
import com.mkh.mobilemall.ui.activity.commodity.CommodityViewActivity;
import com.mkh.mobilemall.ui.activity.map.Location;
import com.mkh.mobilemall.ui.activity.search.SearchActivity;
import com.mkh.mobilemall.ui.activity.webweidian.WebWeidian;
import com.mkh.mobilemall.ui.adapter.ActivityAdapter;
import com.mkh.mobilemall.support.http.file.AsyncImageLoader;
import com.mkh.mobilemall.ui.adapter.AdsAdapter;
import com.mkh.mobilemall.ui.adapter.BaseNodeAdapter;
import com.mkh.mobilemall.ui.adapter.CategoryAdapter;
import com.mkh.mobilemall.ui.base.BaseActivity;
import com.mkh.mobilemall.ui.base.RecylerView.OnRcvScrollListener;
import com.mkh.mobilemall.ui.base.RecylerView.OnRecyclerViewItemClickListener;
import com.mkh.mobilemall.ui.utils.ShoppingCartUtils;
import com.mkh.mobilemall.ui.widget.AddAndSubView;
import com.mkh.mobilemall.ui.widget.DividerItemDecoration;
import com.mkh.mobilemall.utils.AnimUtils;
import com.mkh.mobilemall.utils.BadgeView;
import com.mkh.mobilemall.utils.DialogUtil;
import com.mkh.mobilemall.utils.ImageLoaderUtil;
import com.mkh.mobilemall.utils.MathUtils;
import com.mkh.mobilemall.utils.SharePreferenceUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.xiniunet.api.domain.ecommerce.Ad;
import com.xiniunet.api.domain.ecommerce.CartItem;
import com.xiniunet.api.domain.master.Activities;
import com.xiniunet.api.domain.master.Item;
import com.xiniunet.api.domain.master.ItemCategory;
import com.xiniunet.api.request.ecommerce.FavoriteItemFindRequest;
import com.xiniunet.api.request.master.ItemSearchRequest;
import com.xiniunet.api.response.ecommerce.FavoriteItemFindResponse;
import com.xiniunet.api.response.master.ItemSearchResponse;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 主页
 */

public class MainActivity extends BaseActivity {

    @Bind(R.id.catergory_listview)
    ExpandableListView mCateListView;

    @Bind(R.id.layout_commodityorCollect)
    View commodityOrCollectView;// 商品视图 or 收藏视图

    @Bind(R.id.layout_activities)
    View activitiesView;//活动视图

    @Bind(R.id.list)
    RecyclerView recyclerView;

    @Bind(R.id.banner_main_default)
    RollPagerView bgaBanner;
    @Bind(R.id.txtwalletprice)
    TextView txtWalletBalances;
    @Bind(R.id.iconCart)
    ImageView imgCart;
    @Bind(R.id.txtcomPrice)
    TextView txtPrice;
    @Bind(R.id.btn_go_settlement)
    Button btnSettlement;
    @Bind(R.id.main_wallet_layout)
    RelativeLayout walletLayout;
    @Bind(R.id.main_cart_layout)
    RelativeLayout cartLayout;

    @Bind(R.id.base_node_listview)
    ListView baseNodeList;
    @Bind(R.id.activitieslist)
    ListView activitiesList;
    @Bind(R.id.txtstoreName)
    TextView txtStoreName;
    @Bind(R.id.img_search)
    ImageView main_search;

    @Bind(R.id.empty_list)
    View empty;
    @Bind(R.id.txtemptyMessage)
    TextView txtEmptyMessage;
    @Bind(R.id.imgicon)
    ImageView imageIcon;
    @Bind(R.id.btn_location)
    Button btnLocation;
    @Bind(R.id.view)
    View v;
    @Bind(R.id.imggoweidian)
    ImageView imgGoWeidian;

    private LayoutInflater layoutInflater;
    private CategoryAdapter mCateListAdapter;
    private CommodityAdapter commodityAdapter;
    private int mFromY = 0;
    private SharePreferenceUtil sUtil;
    boolean isNormal = true;
    AlertDialog alg;
    List<PosItemBean> itemList = new ArrayList<PosItemBean>();
    private BadgeView badgeViewCount;
    private ImageView imgTrajectory = null;
    private BaseNodeAdapter baseNodeAdapter;
    private boolean isMyCollect, currentCollectPannel;
    private List<View> mDefaultBgaViews;

    Item item = new Item(); //添加商品时商品全局对象
    int[] start_location;

    ImageView imgGloCollect;
    private long categoryId = 0L;//单个类别 id
    TempComCarBean tempComCarBean = null;
    private int visibleLastIndex = 0;   //最后的可视项索引
    private int visibleItemCounts;     // 当前窗口可见项总数
    List<Item> collectItemList, goodsItemList;

    private boolean isLastpage = false;
    private boolean isLastPageForGoods=false;
    private Handler handlerPage = new Handler();
    private int pagesize = 30;
    private int currentpage = 1;
    private int  currentPageForGoods=1;
    private String opertionType;// 用来标记当前是 添加 or 减 的动作


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        sUtil = GlobalContext.getInstance().getSpUtil();

        //在线检查更新
        UpdateManager.getUpdateManager().checkAppUpdate(MainActivity.this, false);
        initView();//初始化视图
        initData();
        initBaseNodeData(); //初始化基本节点视图
        UIUtil.showWaitDialog(MainActivity.this);
        new ActivitiesDaoTask().execute();
        new GetCategoryDataTask().execute();
        initCategoryData();//初始化分类数据
        if (sUtil.getUserInfo() != null)
            new GetCartDataTask().execute();//初始化购物车数字
        new GetAdsData().execute();// 加载广告栏
        if (sUtil.getUserInfo() != null)
            new GetSmileCardBalanceTask().execute();//获得微笑卡余额
        isMyCollect = getIntent().getBooleanExtra("isMyCollect", false);

        if (isMyCollect) {
            activitiesView.setVisibility(View.GONE);
            commodityOrCollectView.setVisibility(View.VISIBLE);
            UIUtil.showWaitDialog(MainActivity.this);
            initCollectData(currentpage);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_go_settlement://结算
                if (TempComCarDBTask.getInstance().getTempComCarList().size() > 0) {
                    intent = new Intent(MainActivity.this, Cart.class);
                    intent.putExtra("categoryId", String.valueOf(categoryId));
                    startActivityForResult(intent, 1);
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.cart_no_item), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.main_wallet_layout://钱包
                if (sUtil.getUserInfo() != null) {
                    intent = new Intent(MainActivity.this, CardInfoActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.main_cart_layout://购物车
                if (sUtil.getUserInfo() != null) {
                    intent = new Intent(MainActivity.this, Cart.class);
                    intent.putExtra("categoryId", String.valueOf(categoryId));
                    startActivityForResult(intent, 1);
                } else {
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.btn_location: //位置
                intent = new Intent(MainActivity.this, Location.class);
                startActivity(intent);
                break;
            case R.id.img_search://搜索s
                intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.imggoweidian://去微店
                intent = new Intent(MainActivity.this, WebWeidian.class);
                startActivity(intent);
                break;

        }
    }

    /**
     * 格式化 价钱  和yang 符号
     */
    private void formatPriceAndRMB(BigDecimal bd, TextView txtContent) {
        Spannable WordtoSpan = new SpannableString(getString(R.string.center_mark_money) + " " + MathUtils.formatDataForBackBigDecimal(bd.doubleValue()));
        WordtoSpan.setSpan(new TextAppearanceSpan(MainActivity.this, R.style.style02), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        WordtoSpan.setSpan(new TextAppearanceSpan(MainActivity.this, R.style.style2), 2, (getString(R.string.center_mark_money) + " " + MathUtils.formatDataForBackBigDecimal(bd.doubleValue())).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtContent.setText(WordtoSpan, TextView.BufferType.SPANNABLE);

    }

    /**
     * 格式化 价钱  和yang 符号
     */
    private void formatPriceAndRMBForDefault(TextView txtContent) {
        Spannable WordtoSpan = new SpannableString(getString(R.string.center_mark_money) + " 0.00");
        WordtoSpan.setSpan(new TextAppearanceSpan(MainActivity.this, R.style.style02), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        WordtoSpan.setSpan(new TextAppearanceSpan(MainActivity.this, R.style.style2), 2, (getString(R.string.center_mark_money) + " 0.00").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtContent.setText(WordtoSpan, TextView.BufferType.SPANNABLE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            BigDecimal bigDecimal = TempComCarDBTask.getInstance().getAllOrderPrice();
            if (bigDecimal != null) {
                int count = TempComCarDBTask.getInstance().getTempComCarTotalCount();
                badgeViewCount.setText(String.valueOf(count));
                badgeViewCount.show();
                formatPriceAndRMB(bigDecimal, txtPrice);


            } else {
                formatPriceAndRMBForDefault(txtPrice);
                badgeViewCount.hide();
            }
            if (sUtil.getUserInfo() != null) {
                // UIUtil.showWaitDialog(MainActivity.this);
                new GetSmileCardBalanceTask().execute();
            }
            if (data != null) {
                String cId = data.getStringExtra("categoryId");
                if (cId != null) {
                    if (!cId.equals("0")) {
                        categoryId = Long.parseLong(cId);
                        UIUtil.showWaitDialog(MainActivity.this);
                        new GetItemByCategoryIdDataTask().execute(categoryId);
                    }
                }
            }

        }
    }

    /**
     * 监听返回键事件
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AppManager.getAppManager().AppExit(MainActivity.this);
        }
        return false;

    }

    private void initView() {
        btnLocation.setText(sUtil.getCityName());

        badgeViewCount = new BadgeView(MainActivity.this, v);
        badgeViewCount.setTextColor(Color.WHITE);
        badgeViewCount.setTextSize(12);
        badgeViewCount.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        int count = TempComCarDBTask.getInstance().getTempComCarTotalCount();
        if (count != 0) {
            badgeViewCount.setText(String.valueOf(count));

            badgeViewCount.show();
        }

        walletLayout.setOnClickListener(this);
        cartLayout.setOnClickListener(this);
        btnSettlement.setOnClickListener(this);
        btnLocation.setOnClickListener(this);
        main_search.setOnClickListener(this);
        imgGoWeidian.setOnClickListener(this);


        ImageButton button = (ImageButton) findViewById(R.id.btnUser);
        if (sUtil.getUserInfo() != null) {
            button.setImageResource(R.mipmap.icon_user);
        } else {
            button.setImageResource(R.mipmap.logout_title_icon);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sUtil.getUserInfo() != null) {

                    Intent intent = new Intent(MainActivity.this, MemberCenterActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        recyclerView.setHasFixedSize(true);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setOnScrollListener(new OnRcvScrollListener() {
            @Override
            public void onBottom() {
                super.onBottom();


                    handlerPage.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (currentCollectPannel) {
                                if (!isLastpage) {
                                    currentpage++;
                                    getCollectData(currentpage, pagesize);
                                    commodityAdapter.notifyDataSetChanged(); //数据集变化后,通知adapter

                                }
                            }else {
                                if (!isLastPageForGoods) {
                                    currentPageForGoods++;
                                    getGoodsDataByCategoryId(currentPageForGoods, pagesize,categoryId);
                                    commodityAdapter.notifyDataSetChanged(); //数据集变化后,通知adapter
                                }
                            }
                        }
                    }, 10);


            }
        });
        txtStoreName.setText(sUtil.getStoreName());
        mCateListView.setGroupIndicator(null);


    }


    /**
     * 获得当前购物车件数
     */
    private class GetCartDataTask extends
              MyAsyncTask<String, Void, List<CartItem>> {
        @Override
        protected List<CartItem> doInBackground(String... params) {

            List<CartItem> cartItemList = CommodityDao.getCartItemList();
            if (cartItemList != null) {

                if (cartItemList.size() > 0) {
                    TempComCarDBTask.getInstance().removeTempCarCom(null);
                    TempComCarBean tempComCarBean = null;
                    for (CartItem cartItem : cartItemList) {

                        tempComCarBean = new TempComCarBean();
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


            return cartItemList;
        }

        @Override
        protected void onPostExecute(List<CartItem> list) {
            if (list != null) {

                if (TempComCarDBTask.getInstance().getTempComCarTotalCount() == 0) {
                    badgeViewCount.hide();
                } else {
                    badgeViewCount.setText(String.valueOf(TempComCarDBTask.getInstance().getTempComCarTotalCount()));
                    badgeViewCount.show();
                }

                BigDecimal price = TempComCarDBTask.getInstance().getAllOrderPrice();
                if (price != null)
                    formatPriceAndRMB(price, txtPrice);

            }

        }

    }


    /**
     * 根据分类Id 获得 商品
     *
     * @param pageNumber
     * @param pageSize
     */
    private void getGoodsDataByCategoryId(final int pageNumber, final int pageSize,final long categoryId) {

        UIUtil.showWaitDialog(MainActivity.this);
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                UIUtil.dismissDlg();
                if (msg.what == 2) {
                    String s = (String) msg.obj;
                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (msg.what == 4) {

                } else if (msg.what == 3) {

                } else if (msg.what == 1) {
                    ItemSearchResponse itemSearchResponse = (ItemSearchResponse) msg.obj;

                    if (itemSearchResponse.getResult() != null) {
                        if (goodsItemList == null) {
                            goodsItemList = new ArrayList<Item>(itemSearchResponse.getResult());
                        } else {
                            goodsItemList.addAll(itemSearchResponse.getResult());
                        }
                        if (goodsItemList.size() >= itemSearchResponse.getTotalCount()) {
                            isLastPageForGoods = true;
                        }
                        commodityAdapter.notifyDataSetChanged();
                    }
                } else if (msg.what == 0) {
                    ItemSearchResponse itemSearchResponse = (ItemSearchResponse) msg.obj;
                    Toast.makeText(MainActivity.this, itemSearchResponse.getErrors().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        new Thread() {
            public void run() {
                Message msg = new Message();


                ItemSearchRequest request = new ItemSearchRequest();
                request.setStoreId(sUtil.getStoreId());
                request.setPageNumber(pageNumber);
                request.setPageSize(pageSize);
                request.setCategoryId(categoryId);
                try {
                    ItemSearchResponse itemSearchResponse = HttpUtility.client.execute(request, GlobalContext.getInstance().getSpUtil().getUserInfo());
                    if (itemSearchResponse.hasError()) {
                        msg.what = 0;   // 登录失败
                    } else {
                        msg.what = 1;   // 登录成功
                    }
                    msg.obj = itemSearchResponse;
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


    private void getCollectData(final int pageNumber, final int pageSize) {

        UIUtil.showWaitDialog(MainActivity.this);
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                UIUtil.dismissDlg();
                if (msg.what == 2) {
                    String s = (String) msg.obj;
                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (msg.what == 4) {

                } else if (msg.what == 3) {

                } else if (msg.what == 1) {
                    FavoriteItemFindResponse favoriteItemFindResponse = (FavoriteItemFindResponse) msg.obj;
                    if (favoriteItemFindResponse.getResult() != null) {
                        if (collectItemList == null) {
                            collectItemList = new ArrayList<Item>(favoriteItemFindResponse.getResult());
                        } else {
                            collectItemList.addAll(favoriteItemFindResponse.getResult());
                        }
                        if (collectItemList.size() >= favoriteItemFindResponse.getTotalCount()) {
                            isLastpage = true;
                        }
                        commodityAdapter.notifyDataSetChanged();
                    }
                } else if (msg.what == 0) {
                    FavoriteItemFindResponse favoriteItemFindResponse = (FavoriteItemFindResponse) msg.obj;
                    Toast.makeText(MainActivity.this, favoriteItemFindResponse.getErrors().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        new Thread() {
            public void run() {
                Message msg = new Message();


                FavoriteItemFindRequest favoriteItemFindRequest = new FavoriteItemFindRequest();
                favoriteItemFindRequest.setStoreId(sUtil.getStoreId());
                favoriteItemFindRequest.setPageNumber(pageNumber);
                favoriteItemFindRequest.setPageSize(pageSize);
                try {
                    FavoriteItemFindResponse favoriteItemFindResponse = HttpUtility.client.execute(favoriteItemFindRequest, GlobalContext.getInstance().getSpUtil().getUserInfo());
                    if (favoriteItemFindResponse.hasError()) {
                        msg.what = 0;   // 登录失败
                    } else {
                        msg.what = 1;   // 登录成功
                    }
                    msg.obj = favoriteItemFindResponse;
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


    /**
     * 初始化收藏夹数据
     */
    private void initCollectData(final int pageNum) {
        new FavoriteDataTask().execute(pageNum);
    }

    private void initData() {

        BigDecimal price = TempComCarDBTask.getInstance().getAllOrderPrice();
        if (price != null)
            formatPriceAndRMB(price, txtPrice);

    }

    /**
     * 初始化基本节点
     */
    private void initBaseNodeData() {
        final List<String> nodesList = new ArrayList<String>();
        nodesList.add("活动");
        nodesList.add("订单");
        nodesList.add("收藏");
        nodesList.add("客服");

        baseNodeAdapter = new BaseNodeAdapter(MainActivity.this, nodesList);
        baseNodeList.setAdapter(baseNodeAdapter);

        baseNodeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                switch (nodesList.get(position)) {
                    case "活动":
                        commodityOrCollectView.setVisibility(View.GONE);
                        activitiesView.setVisibility(View.VISIBLE);
                        currentCollectPannel = false;
                        isLastpage = false;
                        isLastPageForGoods=false;

                        break;
                    case "订单":

                        if (sUtil.getUserInfo() != null) {
                            currentCollectPannel = false;
                            isLastpage = false;
                            isLastPageForGoods=false;
                            intent = new Intent(MainActivity.this, MyOrderActivity.class);
                            startActivity(intent);
                        } else {
                            intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }

                        break;
                    case "收藏":
                        currentpage = 1;
                        if (sUtil.getUserInfo() != null) {
                            activitiesView.setVisibility(View.GONE);
                            commodityOrCollectView.setVisibility(View.VISIBLE);
                            currentCollectPannel = true;// 当前收藏面板 被激活
                            isLastpage = false;
                            isLastPageForGoods=false;
                            initCollectData(currentpage);
                        } else {
                            intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }


                        break;
                    default:// 客服

                        currentCollectPannel = false;
                        isLastpage = false;
                        isLastPageForGoods=false;
                        if (!sUtil.getStorePhone().equals(""))
                            DialogUtil.getCustomServiceDialog(MainActivity.this, sUtil.getStorePhone()).show();
                        else
                            //Toast.makeText(MainActivity.this, getString(R.string.store_not_open), 200).show();
                            DialogUtil.getCustomServiceDialog(MainActivity.this, getString(R.string.default_cs_phone)).show();

                        break;

                }

                baseNodeAdapter.setSelectedPos(position);
                if (mCateListAdapter != null) {
                    mCateListAdapter.setSelectedPos(-1);
                    mCateListAdapter.setChildSelectedPos(-1);

                }


            }
        });
    }

    /**
     * -
     * 获得商品类别数据
     */
    private class GetCategoryDataTask extends
              MyAsyncTask<String, Void, Map<String, List<ItemCategory>>> {


        @Override
        protected Map<String, List<ItemCategory>> doInBackground(String... params) {

            Map<String, List<ItemCategory>> categoryMap = CategoryDao.getCategory();


            return categoryMap;
        }

        @Override
        protected void onPostExecute(Map<String, List<ItemCategory>> categoryMap) {
            UIUtil.dismissDlg();
            if (categoryMap != null) {
                if (categoryMap.size() > 0) {
                    mCateListAdapter = new CategoryAdapter(MainActivity.this, categoryMap);
                    mCateListView.setAdapter(mCateListAdapter);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                }
            }

        }

    }
    /**
     * 获取广告位
     */
    private class GetAdsData extends MyAsyncTask<Void, Void, List<Ad>> {
        @Override
        protected List<Ad> doInBackground(Void... params) {
            return ActivitiesDao.getAds();
        }

        @Override
        protected void onPostExecute(List<Ad> adsList) {
            if (adsList == null || adsList.isEmpty()) {

            } else {
                /*mDefaultBgaViews = getViews(adsList.size());
                bgaBanner.setViews(mDefaultBgaViews);
                ImageView imageView;
                for (int i = 0; i < mDefaultBgaViews.size(); i++) {
                    imageView = (ImageView) mDefaultBgaViews.get(i).findViewById(R.id.my_image_view);
                    ImageLoaderUtil.getImageLoaderInstance().displayImage("https://raw.githubusercontent.com/bingoogolapple/BGABanner-Android/server/imgs/7.png", imageView);


                }*/
                List<String> picUrl = new ArrayList<>();
                for (int i = 0; i < adsList.size(); i++) {

                    picUrl.add(adsList.get(i).getPictureUrl());
                }
                bgaBanner.setAdapter(new AdsAdapter(picUrl));
            }

        }
    }

    /**
     * 获得活动数据
     */
    private class ActivitiesDaoTask extends
              MyAsyncTask<String, Void, List<Activities>> {

        @Override
        protected List<Activities> doInBackground(String... params) {

            List<Activities> activitiesList = ActivitiesDao.getActivities(0);

            return activitiesList;
        }

        @Override
        protected void onPostExecute(List<Activities> aList) {
            // UIUtil.dismissDlg();

                if (aList != null) {

                    ActivityAdapter activityAdapter = new ActivityAdapter(MainActivity.this, aList);
                    activitiesList.setAdapter(activityAdapter);
                } else {
                    activitiesView.setVisibility(View.VISIBLE);
                    commodityOrCollectView.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }


        }

    }

    /**
     * 添加商品Item
     */
    private class CreateorCartItemTask extends
              MyAsyncTask<Item, Void, BackResultBean> {


        @Override
        protected BackResultBean doInBackground(Item... params) {
            BackResultBean resultBean;
            item = params[0];
            tempComCarBean = TempComCarDBTask.getInstance().getTempComByItemId(String.valueOf(item.getId()));
            if (tempComCarBean != null)
                tempComCarBean.setCommodTotalCount(item.getQuantity());
            if (tempComCarBean == null)
                resultBean = CommodityDao.addOrUpdateCartItem(item.getId(), item.getQuantity());
            else
                resultBean = CommodityDao.updateCartItem(tempComCarBean.getCartItemId(), tempComCarBean.getCommodTotalCount());
            return resultBean;
        }

        @Override
        protected void onPostExecute(BackResultBean resultBean) {
            UIUtil.dismissDlg();
            if (resultBean != null) {
                if (resultBean.getCode().equals("1")) {
                    if (tempComCarBean == null) {

                        ShoppingCartUtils.saveOrUpdateCartData(item, item.getQuantity(), resultBean.getOrderId());
                    } else {
                        tempComCarBean.setCommodTotalCount(tempComCarBean.getCommodTotalCount());
                        tempComCarBean.setCommodTotalPrice(tempComCarBean.getSingleCommodPrice() * tempComCarBean.getCommodTotalCount());
                        TempComCarDBTask.getInstance().addOrUpdateItemToTempCar(tempComCarBean);
                    }

                    BigDecimal price = TempComCarDBTask.getInstance().getAllOrderPrice();
                    //动画。//动画。
                    if (opertionType.equals("+"))
                        AnimUtils.setAnim(MainActivity.this, Integer.MAX_VALUE, imgTrajectory, start_location, imgCart, badgeViewCount);
                    else
                        AnimUtils.setAnimForSub(MainActivity.this, Integer.MAX_VALUE, imgTrajectory, start_location, imgCart, badgeViewCount);

                    formatPriceAndRMB(price, txtPrice);

                } else {
                    Toast.makeText(MainActivity.this, resultBean.getMessage(), Toast.LENGTH_SHORT).show();
                    new GetItemByCategoryIdDataTask().execute(categoryId);

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
            resultBean.setOrderId(params[1]);

            return resultBean;

        }

        @Override
        protected void onPostExecute(BackResultBean resultBean) {
            UIUtil.dismissDlg();
            if (resultBean != null) {
                if (!resultBean.getCode().equals("1")) {
                    Toast.makeText(MainActivity.this, resultBean.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    TempComCarDBTask.getInstance().removeTempCarCom(String.valueOf(resultBean.getOrderId()));
                    BigDecimal price = TempComCarDBTask.getInstance().getAllOrderPrice();
                    //动画。//动画。
                    AnimUtils.setAnimForSub(MainActivity.this, Integer.MAX_VALUE, imgTrajectory, start_location, imgCart, badgeViewCount);
                    if (price != null)
                        formatPriceAndRMB(price, txtPrice);
                    else
                        formatPriceAndRMBForDefault(txtPrice);


                }
            }

        }

    }

    /**
     * 取消收藏商品
     */
    private class cancleCollectItemTask extends
              MyAsyncTask<Item, Void, BackResultBean> {


        @Override
        protected BackResultBean doInBackground(Item... params) {
            item = params[0];
            BackResultBean resultBean = CommodityDao.cancleCollectItem(item.getId());
            return resultBean;
        }

        @Override
        protected void onPostExecute(BackResultBean resultBean) {
            UIUtil.dismissDlg();
            if (resultBean.getCode().equals("1")) {
                Toast.makeText(MainActivity.this, "取消收藏成功", 200).show();
                currentpage = 1;
                if (isMyCollect || currentCollectPannel)
                    initCollectData(currentpage);
                else
                    imgGloCollect.setImageResource(R.mipmap.index_icon_no_collect);


            } else {
                UIUtil.showWaitDialog(MainActivity.this);
                new collectItemTask().execute(item);
                //Toast.makeText(MainActivity.this, resultBean.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }

    }

    /**
     * 收藏商品
     */
    private class collectItemTask extends
              MyAsyncTask<Item, Void, BackResultBean> {


        @Override
        protected BackResultBean doInBackground(Item... params) {
            item = params[0];
            BackResultBean resultBean = CommodityDao.collectItem(item.getId());
            return resultBean;
        }

        @Override
        protected void onPostExecute(BackResultBean resultBean) {
            UIUtil.dismissDlg();
            if (resultBean.getCode().equals("1")) {
                Toast.makeText(MainActivity.this, "收藏成功", 200).show();
                imgGloCollect.setImageResource(R.mipmap.index_icon_collect);
            } else {

                UIUtil.showWaitDialog(MainActivity.this);
                new cancleCollectItemTask().execute(item);
                //Toast.makeText(MainActivity.this, resultBean.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }

    }

    /**
     * 根据商品类别Id获得商品数据
     */
    private class GetItemByCategoryIdDataTask extends
              MyAsyncTask<Long, Void, ItemSearchResponse> {


        @Override
        protected ItemSearchResponse doInBackground(Long... params) {

            ItemSearchResponse response = CommodityDao.getItemByCategoryIdForBackRes(params[0], null);

            return response;
        }

        @Override
        protected void onPostExecute(ItemSearchResponse response) {
            UIUtil.dismissDlg();
            if (response != null) {
                if (response.getResult()!=null) {
                    goodsItemList = response.getResult();
                    if (goodsItemList.size() >= response.getTotalCount()) {
                        isLastPageForGoods = true;
                    }


                    activitiesView.setVisibility(View.GONE);
                    commodityOrCollectView.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    commodityAdapter = new CommodityAdapter(goodsItemList, MainActivity.this, false);
                    recyclerView.setAdapter(commodityAdapter);
                    commodityAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClick(View view, Item data) {
                            Intent intent = new Intent(MainActivity.this, CommodityViewActivity.class);
                            intent.putExtra("id", data.getId());
                            intent.putExtra("storeId", sUtil.getStoreId()); // FIXME 店铺ID
                            startActivityForResult(intent, 1);
                        }
                    });
                }else{
                    activitiesView.setVisibility(View.GONE);
                    commodityOrCollectView.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            } else {
               Toast.makeText(MainActivity.this,response.getErrors().get(0).getMessage(),Toast.LENGTH_SHORT).show();
            }

        }

    }

    /**
     * 初始化分类数据
     */
    private void initCategoryData() {

        mCateListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                UIUtil.showWaitDialog(MainActivity.this);
                mCateListAdapter.setSelectedPos(groupPosition);
                mCateListAdapter.setChildSelectedPos(-1);
                baseNodeAdapter.setSelectedPos(-1);
                currentCollectPannel = false;
                isLastpage = false;
                isLastPageForGoods=false;

                ItemCategory cb = (ItemCategory) mCateListAdapter.getGroup(groupPosition);
                categoryId = cb.getId();


                new GetItemByCategoryIdDataTask().execute(cb.getId());
                for (int i = 0; i < mCateListAdapter.getGroupCount(); i++) {
                    if (groupPosition != i)
                        mCateListView.collapseGroup(i);

                }


            }
        });


        mCateListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                UIUtil.showWaitDialog(MainActivity.this);
                ItemCategory cb = (ItemCategory) mCateListAdapter.getChild(groupPosition, childPosition);
                categoryId = cb.getId();

                new GetItemByCategoryIdDataTask().execute(cb.getId());


                mCateListAdapter.setSelectedPos(-1);
                mCateListAdapter.setChildSelectedPos(childPosition);
                baseNodeAdapter.setSelectedPos(-1);
                return false;
            }
        });


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
            if (point != null) {
                Spannable WordtoSpan = new SpannableString(getString(R.string.center_mark_money) + " " + MathUtils.formatDataForBackBigDecimal(point));
                WordtoSpan.setSpan(new TextAppearanceSpan(MainActivity.this, R.style.style02), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                WordtoSpan.setSpan(new TextAppearanceSpan(MainActivity.this, R.style.style2), 2, (getString(R.string.center_mark_money) + " " + MathUtils.formatDataForBackBigDecimal(point)).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                txtWalletBalances.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
                //txtWalletBalances.setText(getString(R.string.center_mark_money) + " " + MathUtils.formatDataForBackBigDecimal(point));
            } else
                Toast.makeText(MainActivity.this, getString(R.string.interface_timeout), Toast.LENGTH_SHORT).show();


        }

    }


    /**
     * 获取收藏商品数据
     */
    private class FavoriteDataTask extends MyAsyncTask<Integer, Void, FavoriteItemFindResponse> {
        @Override
        protected FavoriteItemFindResponse doInBackground(Integer... params) {
            return CommodityDao.findFavoriteItem(params[0]);
        }

        @Override
        protected void onPostExecute(FavoriteItemFindResponse response) {
            UIUtil.dismissDlg();
            if (response != null) {
                List<Item> favoriteList = response.getResult();
                if (favoriteList == null || favoriteList.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                    txtEmptyMessage.setText("暂无数据");
                    imageIcon.setImageResource(R.mipmap.icon_no_ticket);
                } else {

                    collectItemList = favoriteList;
                    if (collectItemList.size() >= response.getTotalCount()) {
                        isLastpage = true;
                    }


                    activitiesView.setVisibility(View.GONE);
                    commodityOrCollectView.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    baseNodeAdapter.setSelectedPos(2);

                    commodityAdapter = new CommodityAdapter(collectItemList, MainActivity.this, true);
                    recyclerView.setAdapter(commodityAdapter);
                    commodityAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
                        @Override
                        public void onItemClick(View view, Item data) {
                            Intent intent = new Intent(MainActivity.this, CommodityViewActivity.class);
                            intent.putExtra("id", data.getId());
                            intent.putExtra("storeId", sUtil.getStoreId()); // FIXME 店铺ID
                            startActivityForResult(intent, 1);
                        }
                    });


                }
            }
        }
    }

    public class CommodityAdapter extends RecyclerView.Adapter<CommodityAdapter.ViewHolder> implements View.OnClickListener, OnRecyclerViewItemClickListener {

        private List<Item> list;
        private Context context;
        private AsyncImageLoader imageLoader;
        public OnRecyclerViewItemClickListener mOnItemClickListener = null;
        boolean isCollect;
        DisplayImageOptions options;


        public CommodityAdapter(List<Item> list, Context context, boolean isCollect) {
            this.list = list;
            this.context = context;

            this.isCollect = isCollect;
            options=new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        }


        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, int i) {
            final Item item = list.get(i);
            viewHolder.itemView.setTag(item);
            viewHolder.goodsName.setText(item.getName());
            item.getQuantity();

            if (TextUtils.isEmpty(item.getUom())) {
                viewHolder.Uom.setVisibility(View.GONE);
                viewHolder.line.setVisibility(View.GONE);
            } else {
                viewHolder.Uom.setText(item.getUom());
            }
            viewHolder.goodsPrice.setText(getString(R.string.center_mark_money) + " " + MathUtils.formatDataForBackBigDecimal(item.getCurrentPrice()));
            if (isCollect)
                viewHolder.collectPic.setImageResource(R.mipmap.index_icon_collect);


            if (item.getIsFav())
                viewHolder.collectPic.setImageResource(R.mipmap.index_icon_collect);
            viewHolder.goodsOldPrice.setText(getString(R.string.center_mark_money) + " " + MathUtils.formatDataForBackBigDecimal(item.getOriginalPrice()));
            viewHolder.goodsOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.goodsQuantity.setText(String.valueOf(item.getQuantity()));
            // if(item.getPictureUrl()!=null)

            ImageLoaderUtil.getImageLoaderInstance().displayImage(item.getPictureUrl(), viewHolder.imgPic,options);
            TempComCarBean tcB = TempComCarDBTask.getInstance().getTempComByItemId(String.valueOf(item.getId()));
            if (tcB != null) {
                viewHolder.aasComEdite.setNum(tcB.getCommodTotalCount());
            }


            viewHolder.aasComEdite.setOnNumChangeListener(new AddAndSubView.OnNumChangeListener() {
                @Override
                public void onNumChange(View view, int num, String type) {

                    if (sUtil.getUserInfo() != null) {
                        opertionType = type;

                        BigDecimal price = TempComCarDBTask.getInstance().getAllOrderPrice();
                        start_location = new int[2];
                        view.getLocationInWindow(start_location);
                        imgTrajectory = new ImageView(MainActivity.this);
                        imgTrajectory.setImageResource(R.mipmap.sign);
                        item.setQuantity(num);
                        UIUtil.showWaitDialog(MainActivity.this);
                        if (num != 0)
                            new CreateorCartItemTask().execute(item);
                        else {
                            TempComCarBean tempComCarBean = TempComCarDBTask.getInstance().getTempComByItemId(String.valueOf(item.getId()));
                            if (tempComCarBean != null)
                                new removeCartItem().execute(tempComCarBean.getCartItemId(), item.getId());
                        }

                    } else {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });

            viewHolder.collectPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imgGloCollect = (ImageView) v;

                    if (isMyCollect || currentCollectPannel) {
                        UIUtil.showWaitDialog(MainActivity.this);
                        new cancleCollectItemTask().execute(item);
                    } else {
                        new collectItemTask().execute(item);
                    }


                }
            });


        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                      from(viewGroup.getContext()).
                      inflate(R.layout.listview_item_universal_layout, viewGroup, false);
            itemView.setOnClickListener(this);

            return new ViewHolder(itemView);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                //注意这里使用getTag方法获取数据
                mOnItemClickListener.onItemClick(v, (Item) v.getTag());
            }
        }

        public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
            this.mOnItemClickListener = listener;
        }

        @Override
        public void onItemClick(View view, Item data) {

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            protected TextView goodsName;
            protected TextView goodsPrice, goodsOldPrice;
            protected TextView goodsQuantity, Uom, line;//库存
            protected AddAndSubView aasComEdite;
            protected ImageView imgPic;
            protected ImageButton collectPic;

            public ViewHolder(View v) {
                super(v);
                goodsName = (TextView) v.findViewById(R.id.listitem_title);
                goodsPrice = (TextView) v.findViewById(R.id.listitem_unitprice);
                goodsOldPrice = (TextView) v.findViewById(R.id.item_originalPrice);
                goodsQuantity = (TextView) v.findViewById(R.id.listitem_quantity);
                aasComEdite = (AddAndSubView) v.findViewById(R.id.aasComEdite);
                imgPic = (ImageView) v.findViewById(R.id.listitem_Thumbnails);
                collectPic = (ImageButton) v.findViewById(R.id.imgcollectcom);
                Uom = (TextView) v.findViewById(R.id.tvItemUom_two);
                line = (TextView) v.findViewById(R.id.line);
                //txtcomStock= (TextView) v.findViewById(R.id.txtcomStock);


            }

        }
    }

}
