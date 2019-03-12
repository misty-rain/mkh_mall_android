package com.mkh.mobilemall.ui.activity.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fish.mkh.CardInfoActivity;
import com.fish.mkh.MemberCenterActivity;
import com.fish.mkh.div.MkhTitleBar;
import com.fish.mkh.util.UIUtil;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.bean.BackResultBean;
import com.mkh.mobilemall.bean.TempComCarBean;
import com.mkh.mobilemall.dao.CommodityDao;
import com.mkh.mobilemall.support.asyncTask.MyAsyncTask;
import com.mkh.mobilemall.support.db.TempComCarDBTask;
import com.mkh.mobilemall.support.http.file.AsyncImageLoader;
import com.mkh.mobilemall.ui.activity.auth.LoginActivity;
import com.mkh.mobilemall.ui.activity.cart.Cart;
import com.mkh.mobilemall.ui.activity.commodity.CommodityViewActivity;
import com.mkh.mobilemall.ui.base.RecylerView.OnRcvScrollListener;
import com.mkh.mobilemall.ui.base.RecylerView.OnRecyclerViewItemClickListener;
import com.mkh.mobilemall.ui.utils.ShoppingCartUtils;
import com.mkh.mobilemall.ui.widget.AddAndSubView;
import com.mkh.mobilemall.ui.widget.DividerItemDecoration;
import com.mkh.mobilemall.utils.AnimUtils;
import com.mkh.mobilemall.utils.BadgeView;
import com.mkh.mobilemall.utils.ImageLoaderUtil;
import com.mkh.mobilemall.utils.MathUtils;
import com.mkh.mobilemall.utils.SharePreferenceUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.tendcloud.tenddata.TCAgent;
import com.xiniunet.api.domain.ecommerce.CartItem;
import com.xiniunet.api.domain.master.Item;

import java.math.BigDecimal;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zwd on 15/8/2.
 */
public class SearchDetailActivty extends Activity implements View.OnClickListener {
    @Bind(R.id.list)
    RecyclerView recyclerView;
    @Bind(R.id.empty_list)
    View emptyView;
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
    @Bind(R.id.view)
    View v;

    CommodityAdapter adapter;
    String search = null;
    private SharePreferenceUtil sUtil;
    MkhTitleBar mTitleBar;
    private String opertionType;// 用来标记当前是 添加 or 减 的动作
    int[] start_location;
    private ImageView imgTrajectory = null;
    private BadgeView badgeViewCount;
    TempComCarBean tempComCarBean = null;
    Item item = new Item(); //添加商品时商品全局对象


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchdeatail);
        sUtil = GlobalContext.getInstance().getSpUtil();
        search = getIntent().getStringExtra("search");
        ButterKnife.bind(this);
        initTileBar();
        initView();

        UIUtil.showWaitDialog(SearchDetailActivty.this);
        new GetDataTask().execute(search);
        new GetCartDataTask().execute();
       /* mCateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Item pb = null;
                if (view instanceof TextView) {
                    pb = (Item) view.getTag();
                } else {
                    TextView ctv = (TextView) view
                              .findViewById(R.id.listitem_title);
                    pb = (Item) ctv.getTag();
                }
                if (pb == null)
                    return;
                Intent intent = new Intent(SearchDetailActivty.this, CommodityViewActivity.class);
                intent.putExtra("id", pb.getId());
                intent.putExtra("storeId", GlobalContext.getInstance().getSpUtil().getStoreId()); // FIXME 店铺ID
                startActivity(intent);
            }
        });*/
    }


    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onResume(this);

    }

    public void initTileBar() {
        mTitleBar = (MkhTitleBar) findViewById(R.id.search_actionbar);
        mTitleBar.setTitle("商品列表");
        mTitleBar.setLeftClickFinish(this);

    }


    private void initView() {

        badgeViewCount = new BadgeView(SearchDetailActivty.this, v);
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

        LinearLayoutManager llm = new LinearLayoutManager(SearchDetailActivty.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));


    }

    @Override
    public void onClick(View v) {

        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_go_settlement://结算
                if (TempComCarDBTask.getInstance().getTempComCarList().size() > 0) {
                    intent = new Intent(SearchDetailActivty.this, Cart.class);
                    // intent.putExtra("categoryId", String.valueOf(categoryId));
                    startActivityForResult(intent, 1);
                } else {
                    Toast.makeText(SearchDetailActivty.this, getString(R.string.cart_no_item), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.main_wallet_layout://钱包
                if (sUtil.getUserInfo() != null) {
                    intent = new Intent(SearchDetailActivty.this, CardInfoActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(SearchDetailActivty.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.main_cart_layout://购物车
                if (sUtil.getUserInfo() != null) {
                    intent = new Intent(SearchDetailActivty.this, Cart.class);
                    //  intent.putExtra("categoryId", String.valueOf(categoryId));
                    startActivityForResult(intent, 1);
                } else {
                    intent = new Intent(SearchDetailActivty.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
        }

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
     * 格式化 价钱  和yang 符号
     */

    private void formatPriceAndRMB(BigDecimal bd, TextView txtContent) {
        Spannable WordtoSpan = new SpannableString(getString(R.string.center_mark_money) + " " + MathUtils.formatDataForBackBigDecimal(bd.doubleValue()));
        WordtoSpan.setSpan(new TextAppearanceSpan(SearchDetailActivty.this, R.style.style02), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        WordtoSpan.setSpan(new TextAppearanceSpan(SearchDetailActivty.this, R.style.style2), 2, (getString(R.string.center_mark_money) + " " + MathUtils.formatDataForBackBigDecimal(bd.doubleValue())).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtContent.setText(WordtoSpan, TextView.BufferType.SPANNABLE);

    }


    /**
     * 格式化 价钱  和yang 符号
     */
    private void formatPriceAndRMBForDefault(TextView txtContent) {
        Spannable WordtoSpan = new SpannableString(getString(R.string.center_mark_money) + " 0.00");
        WordtoSpan.setSpan(new TextAppearanceSpan(SearchDetailActivty.this, R.style.style02), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        WordtoSpan.setSpan(new TextAppearanceSpan(SearchDetailActivty.this, R.style.style2), 2, (getString(R.string.center_mark_money) + " 0.00").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtContent.setText(WordtoSpan, TextView.BufferType.SPANNABLE);

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
                        AnimUtils.setAnim(SearchDetailActivty.this, Integer.MAX_VALUE, imgTrajectory, start_location, imgCart, badgeViewCount);
                    else
                        AnimUtils.setAnimForSub(SearchDetailActivty.this, Integer.MAX_VALUE, imgTrajectory, start_location, imgCart, badgeViewCount);

                    formatPriceAndRMB(price, txtPrice);

                } else {
                    Toast.makeText(SearchDetailActivty.this, resultBean.getMessage(), Toast.LENGTH_SHORT).show();
                    new GetDataTask().execute(search);

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
                    Toast.makeText(SearchDetailActivty.this, resultBean.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    TempComCarDBTask.getInstance().removeTempCarCom(String.valueOf(resultBean.getOrderId()));
                    BigDecimal price = TempComCarDBTask.getInstance().getAllOrderPrice();
                    //动画。//动画。
                    AnimUtils.setAnimForSub(SearchDetailActivty.this, Integer.MAX_VALUE, imgTrajectory, start_location, imgCart, badgeViewCount);
                    if (price != null)
                        formatPriceAndRMB(price, txtPrice);
                    else
                        formatPriceAndRMBForDefault(txtPrice);


                }
            }

        }

    }

    class GetDataTask extends AsyncTask<String, Void, List<Item>> {

        @Override
        protected List<Item> doInBackground(String... params) {

            List<Item> SourceDateList = CommodityDao.getItemByCategoryId(0L, params[0]);


            return SourceDateList;
        }

        @Override
        protected void onPostExecute(List<Item> result) {
            super.onPostExecute(result);
            UIUtil.dismissDlg();
            if (result != null) {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                adapter = new CommodityAdapter(result, SearchDetailActivty.this, false);
                recyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, Item data) {
                        Intent intent = new Intent(SearchDetailActivty.this, CommodityViewActivity.class);
                        intent.putExtra("id", data.getId());
                        intent.putExtra("storeId", sUtil.getStoreId()); // FIXME 店铺ID
                        startActivityForResult(intent, 1);
                    }
                });
            } else {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);


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
            options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
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
            viewHolder.collectPic.setVisibility(View.GONE);
            if (isCollect)
                viewHolder.collectPic.setImageResource(R.mipmap.index_icon_collect);


            if (item.getIsFav())
                viewHolder.collectPic.setImageResource(R.mipmap.index_icon_collect);
            viewHolder.goodsOldPrice.setText(getString(R.string.center_mark_money) + " " + MathUtils.formatDataForBackBigDecimal(item.getOriginalPrice()));
            viewHolder.goodsOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.goodsQuantity.setText(String.valueOf(item.getQuantity()));
            // if(item.getPictureUrl()!=null)

            ImageLoaderUtil.getImageLoaderInstance().displayImage(item.getPictureUrl(), viewHolder.imgPic, options);
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
                        imgTrajectory = new ImageView(SearchDetailActivty.this);
                        imgTrajectory.setImageResource(R.mipmap.sign);
                        item.setQuantity(num);
                        UIUtil.showWaitDialog(SearchDetailActivty.this);
                        if (num != 0)
                            new CreateorCartItemTask().execute(item);
                        else {
                            TempComCarBean tempComCarBean = TempComCarDBTask.getInstance().getTempComByItemId(String.valueOf(item.getId()));
                            if (tempComCarBean != null)
                                new removeCartItem().execute(tempComCarBean.getCartItemId(), item.getId());
                        }

                    } else {
                        Intent intent = new Intent(SearchDetailActivty.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });

            viewHolder.collectPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                /*    imgGloCollect = (ImageView) v;

                    if (isMyCollect || currentCollectPannel) {
                        UIUtil.showWaitDialog(SearchDetailActivty.this);
                        new cancleCollectItemTask().execute(item);
                    } else {
                        new collectItemTask().execute(item);
                    }*/


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