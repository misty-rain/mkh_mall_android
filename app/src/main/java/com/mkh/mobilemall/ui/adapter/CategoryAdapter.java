package com.mkh.mobilemall.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.mkh.mobilemall.R;
import com.mkh.mobilemall.bean.CategoryBean;
import com.xiniunet.api.domain.master.ItemCategory;

import java.util.List;
import java.util.Map;


/**
 * 主分类 适配器
 */
public class CategoryAdapter extends BaseExpandableListAdapter {


    private Context mContext;

    private LayoutInflater mInflater;
    // default selected item
    private int mSelectedPos = -1;
    private int mChildSelectedPos=-1;
    Map<String,List<ItemCategory>> categories;

    public CategoryAdapter(Context context, Map<String,List<ItemCategory>> categories) {
        super();
        this.mContext = context;
        this.categories=categories;

        this.mInflater = LayoutInflater.from(context);

    }

    @Override
    public int getGroupCount() {
        return categories.get("group").size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return categories.get(String.valueOf(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return categories.get("group").get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return categories.get(String.valueOf(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (null == mContext) {
            return null;
        }

        if (null == categories ||categories.size() == 0
                  || categories.size() <= groupPosition) {
            return null;
        }

        final ViewHolder viewHolder;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.category_list_item, parent,
                      false);

            viewHolder.cateCheckedTextView = (CheckedTextView) convertView
                      .findViewById(R.id.cate_tv);
            viewHolder.imgCateIcon= (ImageView) convertView.findViewById(R.id.imgcateicon);
            viewHolder.cateitemLayout= (RelativeLayout) convertView.findViewById(R.id.cateitemLayout);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


            viewHolder.cateCheckedTextView.setText(categories.get("group").get(groupPosition).getName());
            viewHolder.cateCheckedTextView.setTag(categories.get("group").get(groupPosition));

        if (mSelectedPos == groupPosition)
            viewHolder.cateitemLayout.setBackgroundColor(Color.parseColor("#E6B53C"));
        else
            viewHolder.cateitemLayout.setBackgroundColor(Color.TRANSPARENT);



        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (null == mContext) {
            return null;
        }

        if (null == categories || categories.size() == 0
                  || categories.size() <= childPosition) {
            return null;
        }

        final ViewHolder viewHolder;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.category_list_child_item, parent,
                      false);

            viewHolder.cateCheckedTextView = (CheckedTextView) convertView
                      .findViewById(R.id.txt_catetitle_child);
            viewHolder.imgCateIcon= (ImageView) convertView.findViewById(R.id.imgcatechildicon);
            viewHolder.cateitemLayout= (RelativeLayout) convertView.findViewById(R.id.cateitemchildLayout);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.cateCheckedTextView.setText(categories.get(String.valueOf(groupPosition)).get(childPosition).getName());
        viewHolder.cateCheckedTextView.setTag(categories.get(String.valueOf(groupPosition)).get(childPosition));


       if (mChildSelectedPos == childPosition) {
            viewHolder.cateitemLayout.setBackgroundColor(Color.parseColor("#E6B53C"));
        } else {
            viewHolder.cateitemLayout.setBackgroundColor(Color.TRANSPARENT);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ViewHolder {
        CheckedTextView cateCheckedTextView;
        ImageView imgCateIcon;
        RelativeLayout cateitemLayout;

    }

    public void setSelectedPos(int position) {
        this.mSelectedPos = position;
        notifyDataSetChanged();
    }

    public void setChildSelectedPos(int childSelectedPos){
        this.mChildSelectedPos = childSelectedPos;
        notifyDataSetChanged();

    }
}
