package com.mkh.mobilemall.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.mkh.mobilemall.R;
import com.xiniunet.api.domain.ecommerce.ItemEvaluation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 评论列表的适配器.
 * Created on 2015-07-07.
 * mkh-app-android
 *
 * @author 小昊
 * @since 1.0.0
 */
public class EvaluationAdapter extends BaseAdapter {
    List<ItemEvaluation> list = new ArrayList<>();
    Context context;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public EvaluationAdapter(Context context, List<ItemEvaluation> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EvaluationViewHolder holder;
        if (convertView == null) {
            holder = new EvaluationViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.commodity_evaluation_widget_item, null);
            holder.txtAccount = (TextView) convertView.findViewById(R.id.txtAccount);
            holder.txtContent = (TextView) convertView.findViewById(R.id.txtContent);
            holder.txtTime = (TextView) convertView.findViewById(R.id.txtTime);
            convertView.setTag(holder);
        } else {
            holder = (EvaluationViewHolder) convertView.getTag();
        }

        holder.txtAccount.setText(list.get(position).getUserName());
        holder.txtContent.setText(list.get(position).getEvaluation());
        holder.txtTime.setText(format.format(list.get(position).getTime()));

        return convertView;
    }
    final static class EvaluationViewHolder{
        TextView txtAccount;
        TextView txtContent;
        TextView txtTime;
    }
}
