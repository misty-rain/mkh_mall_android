package com.mkh.mobilemall.ui.activity.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.mkh.mobilemall.R;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.dao.CommodityDao;
import com.mkh.mobilemall.support.appconfig.AppManager;
import com.mkh.mobilemall.ui.activity.commodity.CommodityViewActivity;
import com.mkh.mobilemall.ui.adapter.SearchAdapter;
import com.mkh.mobilemall.ui.base.BaseActivity;
import com.tendcloud.tenddata.TCAgent;
import com.xiniunet.api.domain.master.Item;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zwd on 15/7/17.
 */
public class SearchActivity extends Activity implements View.OnClickListener {
    @Bind(R.id.btn_search)
    Button btnSearch;
    @Bind(R.id.edtsearch)
    EditText edtSearch;
    @Bind(R.id.btn_back)
    ImageButton btnBack;
    @Bind(R.id.searchList)
    ListView listView;
    @Bind(R.id.empty_list)
    View emptyView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        AppManager.getAppManager().addActivity(this);
        initView();

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


    private void initView() {

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
                }

                // setResult(1,intent);
                finish();
            }
        });

        //搜索框改变监听
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                // filterData("l");
                new GetDataTask().execute(edtSearch.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(edtSearch.getText().toString())) {
                    Intent intent = new Intent(SearchActivity.this, SearchDetailActivty.class);
                    intent.putExtra("search", edtSearch.getText().toString());
                    startActivity(intent);
                }

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                Intent intent = new Intent(SearchActivity.this, CommodityViewActivity.class);
                intent.putExtra("id", pb.getId());
                intent.putExtra("storeId", GlobalContext.getInstance().getSpUtil().getStoreId()); // FIXME 店铺ID
                startActivity(intent);
            }
        });


    }

    @Override
    public void onClick(View v) {

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
            if (result != null) {
                listView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                SearchAdapter adapter = new SearchAdapter(SearchActivity.this, result);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } else {
                listView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }


        }

    }


}
