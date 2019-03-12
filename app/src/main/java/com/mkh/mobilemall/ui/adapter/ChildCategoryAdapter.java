package com.mkh.mobilemall.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.bean.CategoryBean;

import java.util.List;

public class ChildCategoryAdapter extends BaseAdapter {

    private Context mContext;
    private List<CategoryBean> mCategories;
    private LayoutInflater mInflater;

    public ChildCategoryAdapter(Context context,
                                List<CategoryBean> childCategory) {
        this.mContext = context;
        this.mCategories = childCategory;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return (null == mCategories) ? 0 : mCategories.size();
    }

    @Override
    public CategoryBean getItem(int position) {
        return (null == mCategories) ? null : mCategories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (null == convertView) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.category_item, parent,
                    false);
            holder.image = (ImageView) convertView
                    .findViewById(R.id.catergory_image);
            holder.title = (TextView) convertView
                    .findViewById(R.id.catergoryitem_title);
            holder.content = (TextView) convertView
                    .findViewById(R.id.catergoryitem_content);
            // 使用tag存储数据
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mCategories.size() > 0) {
            holder.title.setText(mCategories.get(position).getName());
            holder.title.setTag(mCategories.get(position));
        } else {
            holder.title.setText("全部");
        }

        return convertView;
    }

    public static class ViewHolder {
        ImageView image;
        TextView title;
        TextView content;
    }
}
