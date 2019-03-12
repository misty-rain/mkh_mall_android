package com.mkh.mobilemall.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.utils.ImageLoaderUtil;
import com.xiniunet.api.domain.ecommerce.SimpleItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 推荐列表的适配器.
 * Created on 2015-07-08.
 * mkh-app-android
 *
 * @author 小昊
 * @since 1.0.0
 */
public class RecommendAdapter extends BaseAdapter {
    List<SimpleItem> list = new ArrayList<>();
    Context context;

    public RecommendAdapter(Context context, List<SimpleItem> list) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.commodity_recommend_widget_item, null);
            holder.txtName = (TextView) convertView.findViewById(R.id.tvRecommendItemName);
            holder.txtPrice = (TextView) convertView.findViewById(R.id.tvRecommendItemPrice);
            holder.ivImage = (ImageView) convertView.findViewById(R.id.ivRecommendItem);
            convertView.setTag(holder);
        } else {
            holder = (EvaluationViewHolder) convertView.getTag();
        }
        SimpleItem simpleItem = list.get(position);
        holder.txtName.setText(simpleItem.getName());
        holder.txtPrice.setText(simpleItem.getPrice()+"");
        ImageLoaderUtil.getImageLoaderInstance().displayImage(simpleItem.getPictureUrl(), holder.ivImage);

        return convertView;
    }
    final static class EvaluationViewHolder{
        TextView txtName;
        TextView txtPrice;
        ImageView ivImage;
    }
}
