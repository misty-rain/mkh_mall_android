package com.mkh.mobilemall.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mkh.mobilemall.R;
import com.mkh.mobilemall.bean.CategoryBean;

import java.util.List;

/**
 * Created by xiniu_wutao on 15/7/9.
 *
 * 首页基本节点适配器
 */
public class BaseNodeAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mNodesList;
    private LayoutInflater mInflater;
    // default selected item
    private int mSelectedPos = 0;

    public BaseNodeAdapter(Context context, List<String> mNodesList) {
        super();
        this.mContext = context;
        this.mNodesList = mNodesList;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return (null == mNodesList) ? 0 : mNodesList.size();
    }

    @Override
    public String getItem(int position) {
        return (null == mNodesList) ? null : mNodesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (null == mContext) {
            return null;
        }

        if (null == mNodesList || mNodesList.size() == 0
                  || mNodesList.size() <= position) {
            return null;
        }

        final ViewHolder viewHolder;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.basenodes_list_item, parent,
                      false);

            viewHolder.cateCheckedTextView = (CheckedTextView) convertView
                      .findViewById(R.id.cate_tv);
            viewHolder.imgCateIcon= (ImageView) convertView.findViewById(R.id.imgicon);
            viewHolder.cateitemLayout= (LinearLayout) convertView.findViewById(R.id.cateitemLayout);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.cateCheckedTextView.setText(mNodesList.get(position));
        if(mNodesList.get(position).equals("活动"))
            viewHolder.imgCateIcon.setImageResource(R.mipmap.icon_activity);
        if(mNodesList.get(position).equals("订单"))
            viewHolder.imgCateIcon.setImageResource(R.mipmap.icon_order);
        if(mNodesList.get(position).equals("收藏"))
            viewHolder.imgCateIcon.setImageResource(R.mipmap.index_leftbar_icon_collect);
        if(mNodesList.get(position).equals("客服"))
            viewHolder.imgCateIcon.setImageResource(R.mipmap.icon_cs);


        if (mSelectedPos == position) {
            viewHolder.cateitemLayout.setBackgroundColor(Color.parseColor("#E6B53C"));
        } else {
            viewHolder.cateitemLayout.setBackgroundColor(Color.TRANSPARENT);
        }
        return convertView;
    }

    class ViewHolder {
        CheckedTextView cateCheckedTextView;
        ImageView imgCateIcon;
        LinearLayout cateitemLayout;
    }

    public void setSelectedPos(int position) {
        this.mSelectedPos = position;
        notifyDataSetChanged();
    }

}
