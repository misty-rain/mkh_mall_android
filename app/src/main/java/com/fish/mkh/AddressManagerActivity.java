package com.fish.mkh;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.fish.mkh.client.AuthDao;
import com.fish.mkh.div.MkhTitleBar;
import com.fish.mkh.div.swipemenu.SwipeMenu;
import com.fish.mkh.div.swipemenu.SwipeMenuCreator;
import com.fish.mkh.div.swipemenu.SwipeMenuItem;
import com.fish.mkh.div.swipemenu.SwipeMenuListView;
import com.fish.mkh.div.swipemenu.SwipeMenuListView.OnMenuItemClickListener;
import com.fish.mkh.util.UIUtil;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.config.SysConstant;
import com.mkh.mobilemall.ui.activity.cart.CartForListview;
import com.mkh.mobilemall.ui.base.BaseActivity;
import com.xiniunet.api.domain.master.Location;
import com.xiniunet.api.domain.system.Passport;
import com.xiniunet.api.request.master.LocationDeleteRequest;
import com.xiniunet.api.request.master.LocationGetAllListRequest;
import com.xiniunet.api.response.master.LocationDeleteResponse;
import com.xiniunet.api.response.master.LocationGetAllListResponse;

public class AddressManagerActivity extends BaseActivity {

    MkhTitleBar mTitleBar;
    AddressAdapter adapter;
    List<Location> myAddressList;
    SwipeMenuListView mListView;
    Handler handler;
    private int LOAD_OK = 1;
    private int LOAD_ERROR = 0;
    private int DELETE_OK = 2;
    private int DELETE_ERROR = 3;
    private int request_code = 1001;
    private String isEditAddress;
    Passport passport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_manager);
        passport = GlobalContext.getInstance().getSpUtil().getUserInfo();
        isEditAddress = getIntent().getStringExtra("isEditAddress");
        initViews();
        initTileBar();
        loadDatas();
    }

    private void initViews() {
        mListView = (SwipeMenuListView) findViewById(R.id.lv_container);

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                deleteItem.setWidth(dip2px(100));
                deleteItem.setTitle("删除");
                deleteItem.setTitleColor(Color.WHITE);
                deleteItem.setTitleSize(20);
                menu.addMenuItem(deleteItem);
            }
        };
        mListView.setMenuCreator(creator);
        mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        deleteAddressList(myAddressList.get(position).getId());
                        break;
                }
                return false;
            }
        });

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent it = null;
                if (isEditAddress == null) {
                    it = new Intent(AddressManagerActivity.this, AddressEditActivity.class);
                    it.putExtra("name", myAddressList.get(arg2).getContactName());
                    it.putExtra("phone", myAddressList.get(arg2).getContactPhone());
                    if (myAddressList.get(arg2).getProvinceName() != null || myAddressList.get(arg2).getCityName() != null)
                        it.putExtra("address", myAddressList.get(arg2).getProvinceName() + myAddressList.get(arg2).getCityName() + myAddressList.get(arg2).getDistrictName());
                    else
                        it.putExtra("address", myAddressList.get(arg2).getAreaInfo() + myAddressList.get(arg2).getAddress());
                    it.putExtra("detailAddress", myAddressList.get(arg2).getAddress());
                    it.putExtra("addressTag", myAddressList.get(arg2).getProvinceName() + ";" + myAddressList.get(arg2).getCityName() + ";" + myAddressList.get(arg2).getDistrictName());
                    it.putExtra("id", myAddressList.get(arg2).getId());


                    it.putExtra("isNewAdd", false);
                    it.putExtra("isDefault", myAddressList.get(arg2).getIsDefault());

                    startActivityForResult(it, request_code);
                } else {
                    it = new Intent(AddressManagerActivity.this, CartForListview.class);
                    String address = "";
                    if (myAddressList.get(arg2).getProvinceName() != null || myAddressList.get(arg2).getCityName() != null)
                        address = myAddressList.get(arg2).getProvinceName() + myAddressList.get(arg2).getCityName() + myAddressList.get(arg2).getDistrictName() + myAddressList.get(arg2).getAddress();
                    else
                        address = myAddressList.get(arg2).getAreaInfo() + myAddressList.get(arg2).getAddress();
                    it.putExtra("addressId", myAddressList.get(arg2).getId().toString());
                    it.putExtra("name", myAddressList.get(arg2).getContactName());
                    it.putExtra("phone", myAddressList.get(arg2).getContactPhone());
                    it.putExtra("defaultAddress", address);
                    setResult(SysConstant.BACKRESULTCODE_CART_REICEVEADDRESS, it);
                    finish();

                }
            }
        });

    }

    private void loadDatas() {
        if (adapter == null) {
            adapter = new AddressAdapter(this);
        }
        myAddressList = new ArrayList<Location>();
        handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == LOAD_OK) {
                    LocationGetAllListResponse addressListRsp = (LocationGetAllListResponse) msg.obj;
                    if (null != addressListRsp) {
                        myAddressList = addressListRsp.getResult();
                        adapter.setData(myAddressList);
                        mListView.setAdapter(adapter);
                    }
                    UIUtil.dismissDlg();
                } else if (msg.what == LOAD_ERROR) {
                    LocationGetAllListResponse addressListRsp = (LocationGetAllListResponse) msg.obj;
                    Toast.makeText(AddressManagerActivity.this,
                              addressListRsp.getErrors().get(0).getMessage(),
                              Toast.LENGTH_SHORT).show();
                    UIUtil.dismissDlg();
                } else if (msg.what == DELETE_OK) {
                    UIUtil.dismissDlg();
                    loadDatas();
                } else if (msg.what == DELETE_ERROR) {
                    LocationDeleteResponse locaDelRsp = (LocationDeleteResponse) msg.obj;
                    Toast.makeText(AddressManagerActivity.this,
                              locaDelRsp.getErrors().get(0).getMessage(),
                              Toast.LENGTH_SHORT).show();
                    UIUtil.dismissDlg();
                }
            }
        };
        UIUtil.showWaitDialog(AddressManagerActivity.this);
        getAddressList();
    }

    public void initTileBar() {
        mTitleBar = (MkhTitleBar) findViewById(R.id.title_setting);
        mTitleBar.setTitle("地址管理");
        mTitleBar.setLeftClickFinish(this);
        mTitleBar.setRightTextButton("新建", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AddressManagerActivity.this, AddressEditActivity.class), request_code);
            }
        });
    }

    public int dip2px(float dipValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private void getAddressList() {
        new Thread() {
            public void run() {
                Message msg = new Message();
                LocationGetAllListRequest addresslistReq = new LocationGetAllListRequest();
                try {
                    LocationGetAllListResponse addressListRsp = AuthDao.client.execute(addresslistReq, passport);
                    if (addressListRsp.hasError()) {
                        msg.what = LOAD_ERROR; // 获取失败
                    } else {
                        msg.what = LOAD_OK; // 获取成功
                    }
                    msg.obj = addressListRsp;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                }
            }
        }.start();
    }

    private void deleteAddressList(final Long id) {
        UIUtil.showWaitDialog(AddressManagerActivity.this);
        new Thread() {
            public void run() {
                Message msg = new Message();
                LocationDeleteRequest adddeleteReq = new LocationDeleteRequest();
                adddeleteReq.setId(id);
                try {
                    LocationDeleteResponse addressdeleteRsp = AuthDao.client.execute(adddeleteReq, passport);
                    if (addressdeleteRsp.hasError()) {
                        msg.what = DELETE_ERROR; // 删除失败
                    } else {
                        msg.what = DELETE_OK; // 删除成功
                    }
                    msg.obj = addressdeleteRsp;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                }
            }
        }.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == request_code) {
            if (resultCode == 1) {
                loadDatas();
            }
        }
    }

}
