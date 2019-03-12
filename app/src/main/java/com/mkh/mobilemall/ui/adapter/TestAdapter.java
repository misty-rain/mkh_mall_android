package com.mkh.mobilemall.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jude.rollviewpager.adapter.DynamicPagerAdapter;
import com.mkh.mobilemall.utils.ImageLoaderUtil;

import java.util.List;

/**
 * Created by zwd on 15/8/3.
 */
public class TestAdapter extends DynamicPagerAdapter
{
    private List<String> urls;

    public TestAdapter(List<String> urls) {
        this.urls = urls;
    }

    /*private int[] imgs = {
            R.drawable.img1,
            R.drawable.img2,
            R.drawable.img3,
            R.drawable.img4,
    };*/
       /* private String[] url = {
                "http://i1.xiachufang.com/image/620/1c9fc2989ec411e3b4a6e0db5512b209.jpg",
                "http://i3.xiachufang.com/image/600/84e69438a28111e3b4a6e0db5512b209.jpg",
                "http://img.ddmeishi.com/uploads/allimg/140414/160_140414083532_1.jpg",
                "http://static4.orstatic.com.cn/userphoto/photo/2/1SR/00CSLX7D8B6A0557996FD7l.jpg"

        };*/
    @Override
    public View getView(ViewGroup container, final int position) {
        ImageView view = new ImageView(container.getContext());

       // view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //view.setImageResource(imgs[position]);
        ImageLoaderUtil.getImageLoaderInstance().displayImage(urls.get(position), view);
        return view;
    }

    @Override
    public int getCount() {
        return urls.size();
    }
}