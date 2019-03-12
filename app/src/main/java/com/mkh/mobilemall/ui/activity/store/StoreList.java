package com.mkh.mobilemall.ui.activity.store;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fish.mkh.div.MkhTitleBar;
import com.fish.mkh.util.UIUtil;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.dao.StoreDao;
import com.mkh.mobilemall.support.asyncTask.MyAsyncTask;
import com.mkh.mobilemall.ui.activity.main.MainActivity;
import com.mkh.mobilemall.ui.base.BaseActivity;
import com.mkh.mobilemall.ui.utils.ShowToastUtils;
import com.xiniunet.api.domain.master.Store;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/7/15.
 * 门店列表
 */
public class StoreList extends BaseActivity {




    /*@Override
    protected boolean hasBackButton() {
        return true;
    }
*/
    /*@Override
    protected int getActionBarTitle() {
        return R.string.toolbar_title_storelist;
    }

    @Override
    protected int getActionBarCustomView() {
        return R.layout.custom_toolbar;
    }*/

    //构建门店列表list
    List<Store> storeList=new ArrayList<Store>();
    StoreAdapter storeAdapter;

    @Bind(R.id.storeListView)
    ListView storeListView;
    @Bind(R.id.empty_list)
    View emptyView;
    MkhTitleBar titleBar;
    LinearLayout llnet;
    @Bind(R.id.reload)
    Button reload;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storelist);
        ButterKnife.bind(this);
        UIUtil.showWaitDialog(StoreList.this);
        new getStoreListDataTask().execute();
        itemOnClicker();
        titleBar= (MkhTitleBar) findViewById(R.id.shop_actionbar);
        titleBar.setLeftClickFinish(this);
        titleBar.setTitle("门店列表");
        llnet= (LinearLayout) findViewById(R.id.llnet);
    }



    /**
     * 元素点击事件
     */
    public void itemOnClicker() {
        storeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(StoreList.this, MainActivity.class);
                intent.putExtra("storeName",storeList.get(position).getName());
                GlobalContext.getInstance().getSpUtil().saveStoreId(storeList.get(position).getId());
                GlobalContext.getInstance().getSpUtil().saveStoreName(storeList.get(position).getName());
                GlobalContext.getInstance().getSpUtil().saveStorePhone(storeList.get(position).getContactPhone());
                startActivity(intent);
                finish();

            }
        });
    }


    private class getStoreListDataTask extends MyAsyncTask<String, Void, List<Store>>{
        @Override
        protected List<Store> doInBackground(String... params) {
            storeList= StoreDao.getStoreList();
            return storeList;
        }
        protected void onPostExecute(List<Store> list) {
            UIUtil.dismissDlg();
            if(storeList!=null) {
                if (storeList.size() > 0) {
                    storeAdapter = new StoreAdapter();
                   storeListView.setAdapter(storeAdapter);
                    storeAdapter.notifyDataSetChanged();
                } else {
                    storeListView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }
            }else{
                Toast.makeText(StoreList.this,getString(R.string.interface_timeout),Toast.LENGTH_SHORT).show();
                llnet.setVisibility(View.VISIBLE);
                reload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      // Toast.makeText(StoreList.this,"aa",100).show();
                         ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                        if(info != null && info.isAvailable()) {
                            UIUtil.showWaitDialog(StoreList.this);
                            new getStoreListDataTask().execute();
                            llnet.setVisibility(View.GONE);
                        }else{
                            ShowToastUtils.showToast(getString(R.string.network_not_connected), StoreList.this);
                        }

                    }
                });

            }


        }
    }


    /**
     * store适配器
     */
    public class StoreAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (storeList != null) {
                return storeList.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {

            if (storeList != null) {
                return storeList.get(position);
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            if (convertView == null || convertView.getTag() == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(GlobalContext.getInstance()).inflate(
                        R.layout.storelist_item, parent, false);
                holder.storeName = (TextView) convertView
                        .findViewById(R.id.storeListView_item_textView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();

            }

            final Store item = storeList.get(position);
            holder.storeName.setText(item.getName());
            return convertView;
        }
    }

    private class ViewHolder {
        LinearLayout linearLayout;
        TextView storeName;
    }


}
