package com.mkh.mobilemall.ui.activity.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.config.SysConstant;
import com.mkh.mobilemall.support.asyncTask.MyAsyncTask;
import com.mkh.mobilemall.ui.base.BaseActivity;
import com.xiniunet.api.domain.membership.Voucher;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiniu_wutao on 15/7/8.
 *
 * 优惠卷
 */
public class PreferentialTicket extends BaseActivity {
    @Bind(R.id.ticketlist)
    ListView scList;
    @Bind(R.id.empty_list) View emptyView;
    @Bind(R.id.txtemptyMessage)
    TextView txtEmptyMessage;
    @Bind(R.id.imgicon)
    ImageView imageIcon;



    List<Voucher> vouchersList = new ArrayList<Voucher>();
    VoucherTicketAdapter adapter;



    @Override
    protected int getLayoutId() {
        return R.layout.preferentialticket;
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.toolbar_title_ticketlist;
    }

    @Override
    protected int getActionBarCustomView() {
        return R.layout.custom_toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initView();
        vouchersList= JSON.parseArray(getIntent().getStringExtra("voucherList"), Voucher.class);
        new GetTicketListDataTask().execute();


    }

    private  void initView(){
        scList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Voucher voucher=vouchersList.get(position);
                Intent intent=new Intent(PreferentialTicket.this,CartForListview.class);
                intent.putExtra("voucher",JSON.toJSONString(voucher));
                setResult(SysConstant.BACKRESULTCODE_CART_DISCOUNT_TICKET, intent);
                finish();


            }
        });
    }

    private class GetTicketListDataTask extends
              MyAsyncTask<String, Void, List<Voucher>> {

        /*
         * (non-Javadoc)
         *
         * @see
         * com.engc.jlcollege.support.lib.MyAsyncTask#doInBackground(Params[])
         */
        @Override
        protected List<Voucher> doInBackground(String... params) {


            return vouchersList;
        }

        @Override
        protected void onPostExecute(List<Voucher> list) {
            if(vouchersList!=null) {
                if (vouchersList.size() > 0) {
                    adapter = new VoucherTicketAdapter();
                    scList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                } else {
                    scList.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    txtEmptyMessage.setText("暂无优惠劵");
                    imageIcon.setImageResource(R.mipmap.icon_no_ticket);

                }
            }


        }

    }

    /**
     * 抵用券adapter
     */

    public class VoucherTicketAdapter extends BaseAdapter {

        @Override
        public int getCount() {

            if(vouchersList != null)
                return vouchersList.size();
            else
                return 0;
        }

        @Override
        public Object getItem(int position) {
            if(vouchersList != null)
                return vouchersList.get(position);
            else
                return null;
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ListItemView listItemView = null;
            if (convertView == null) {
                listItemView = new ListItemView();
                convertView = LayoutInflater.from(GlobalContext.getInstance()).inflate(R.layout.adapter_ticket, null);
                //获取控件对象
                listItemView.ta_layout = (LinearLayout)convertView.findViewById(R.id.adt_cost_layout);
                listItemView.tv_title = (TextView)convertView.findViewById(R.id.adt_title);
                listItemView.tv_subtitle = (TextView)convertView.findViewById(R.id.adt_subtitle);
                listItemView.tv_content = (TextView)convertView.findViewById(R.id.adt_content);
                listItemView.tv_cost = (TextView)convertView.findViewById(R.id.adt_cost);

                //设置控件集到convertView
                convertView.setTag(listItemView);
            }else {
                listItemView = (ListItemView)convertView.getTag();
            }


            Voucher voucher = vouchersList.get(position);
            if( voucher != null){
                if(voucher.getName() != null)
                    listItemView.tv_title.setText(voucher.getName());
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                if(voucher.getValidDate() != null){
                    String date = df.format(voucher.getValidDate());
                    listItemView.tv_subtitle.setText(date);
                }
                if(voucher.getRange() != null)
                    listItemView.tv_content.setText(voucher.getRange());
                if(voucher.getAmount()!=null)
                    listItemView.tv_cost.setText(getString(R.string.center_mark_money)+" "+voucher.getAmount());
            }
            return convertView;
        }

        public final class ListItemView{
            public LinearLayout ta_layout;
            public TextView tv_title;
            public TextView tv_subtitle;
            public TextView tv_cost;
            public TextView tv_content;
        }

    }

}
