package com.mkh.mobilemall.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.mkh.mobilemall.R;
import com.xiniunet.api.domain.system.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2015-07-13.
 *
 * @author 吕浩
 * @since 1.0.0
 */
public class MessageAdapter extends BaseAdapter {
    List<Message> list = new ArrayList<>();
    Context context;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public MessageAdapter(List<Message> list, Context context) {
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
        MessageViewHolder holder;
        if (convertView == null) {
            holder = new MessageViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.message_item, null);
            holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
            convertView.setTag(holder);
        } else {
            holder = (MessageViewHolder) convertView.getTag();
        }

        Message message = list.get(position);
        if(message.getSendTime() != null) {
            holder.tvDate.setText(format.format(message.getSendTime()));
        } else {
            holder.tvDate.setText("未知时间");
        }
        holder.tvTitle.setText(message.getMessageTitle());
        holder.tvContent.setText(message.getMessageContent());
        if(message.getBusinessType() == null) {
            message.setBusinessType("客服答疑");
        }
        switch (message.getBusinessType()) {
            case "客服答疑":
                holder.ivIcon.setBackgroundResource(R.drawable.icon_answer);
                break;
            case "优惠活动":
                holder.ivIcon.setBackgroundResource(R.drawable.icon_gift);
                break;
        }

        return convertView;
    }
    final static class MessageViewHolder{
        TextView tvDate;
        ImageView ivIcon;
        TextView tvTitle;
        TextView tvContent;
    }
}
