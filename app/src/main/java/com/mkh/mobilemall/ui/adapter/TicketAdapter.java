package com.mkh.mobilemall.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.mkh.mobilemall.R;
import com.mkh.mobilemall.bean.TicketBean;
import com.mkh.mobilemall.utils.SharePreferenceUtil;
import com.xiniunet.api.domain.membership.Voucher;

import java.util.List;

/**
 *
 *  多张 抵用券  adapter
 */
public class TicketAdapter extends BaseAdapter{
    List<Voucher> list;
    protected Context context;
    protected LayoutInflater inflater;
    protected ListView listview;
    private SharePreferenceUtil sp;

    public TicketAdapter(Context context, List<Voucher> list) {
        this.list = list;
        // this.inflater = LayoutInflater.from(context);
        this.inflater = LayoutInflater.from(context);
        // this.context = context;


    }

    private List<Voucher> getList() {
        return list;
    }

    @Override
    public int getCount() {
        if (getList() != null && getList().size() > 0) {
            return getList().size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return getList().get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(
                    R.layout.list_ticket_item_layout, parent, false);
            holder.amoumnt = (TextView) convertView
                    .findViewById(R.id.txtticketamount);
            holder.code = (TextView) convertView
                    .findViewById(R.id.txtticketcode);
            holder.productName = (TextView) convertView
                    .findViewById(R.id.txtticketpname);


        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        final Voucher item = list.get(position);
        holder.amoumnt.setText(String.valueOf(item.getAmount())+" 元");
        holder.productName.setText("厂商:"+item.getName());
        holder.code.setText(item.getAmount().toString());


        return convertView;
    }

    private class ViewHolder {

        TextView productName;

        TextView code;

        TextView amoumnt;
        RelativeLayout delLayout;




    }

}
