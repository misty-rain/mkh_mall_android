package com.mkh.mobilemall.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created on 2015-07-08.
 *
 * @author 吕浩
 * @since 1.0.0
 */
public class ListViewUtil {

    /**
     * 设置列表的高度
     * @param listView
     */
    public static void setListViewHeight(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        if (adapter == null) {
            return;
        }
        int totalHeight = 0;
        for(int i=0; i<adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        listView.setLayoutParams(params);
    }

    public static void setGridViewHeight(GridView listView) {
        ListAdapter adapter = listView.getAdapter();
        if (adapter == null) {
            return;
        }
        int totalHeight = 0;
        int maxColumns = listView.getNumColumns();
        int lines = (adapter.getCount() - 1) / maxColumns + 1;
        int maxHeight = 0;
        for(int i=0; i<lines; i++) {

            int columns;
            if(i == lines -1) {
                columns = adapter.getCount() % maxColumns;
            } else {
                columns = maxColumns;
            }
            for(int j=0; j<columns; j++) {
                View listItem = adapter.getView((i*columns + j), null, listView);
                listItem.measure(0, 0);
                int height = listItem.getMeasuredHeight();
                if(height > maxHeight) {
                    maxHeight = height;
                }
            }
            totalHeight += (maxHeight + 40);
        }
        listView.getLayoutParams().height = totalHeight + 100;
    }
}
