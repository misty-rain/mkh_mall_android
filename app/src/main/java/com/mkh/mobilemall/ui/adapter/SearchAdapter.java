package com.mkh.mobilemall.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mkh.mobilemall.R;
import com.mkh.mobilemall.utils.ImageLoaderUtil;
import com.xiniunet.api.domain.master.Item;

import java.util.List;

/**
 * C
 * 搜索结果 adapter.
 */
public class SearchAdapter extends BaseAdapter  {
    private List<Item> list = null;
    private Context mContext;
    public SearchAdapter(Context mContext, List<Item> list) {
        this.mContext = mContext;
        this.list = list;
    }
    public void updateListView(List<Item> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final Item mContent = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_search_layout, null);
            viewHolder.txtDisplayText=(TextView)view.findViewById(R.id.listitem_title);
            viewHolder.txtTemp = (TextView) view.findViewById(R.id.temp);
            viewHolder.imageView= (ImageView) view.findViewById(R.id.item_image);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }




        viewHolder.txtDisplayText.setText(mContent.getName());
        viewHolder.txtDisplayText.setTag(mContent);
        viewHolder.txtTemp.setText(String.valueOf(mContent.getCurrentPrice())+"元");
        ImageLoaderUtil.getImageLoaderInstance().displayImage(mContent.getPictureUrl(),viewHolder.imageView);
        return view;

    }



    final static class ViewHolder {
        TextView txtDisplayText;
        TextView txtTemp;
        ImageView imageView;
    }



}
