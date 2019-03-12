package com.mkh.mobilemall.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mkh.mobilemall.R;
import com.mkh.mobilemall.app.GlobalContext;
import com.mkh.mobilemall.ui.activity.commodity.CommodityViewActivity;
import com.mkh.mobilemall.ui.activity.webweidian.WebWeidianActivity;
import com.mkh.mobilemall.utils.SharePreferenceUtil;
import com.xiniunet.api.domain.master.ActivityItem;

/**
 * Created by xiniu on 15/8/25.
 */
public class ActivitywidgetB extends LinearLayout {
    private ImageView one;
    private SharePreferenceUtil sUtil;
    public ActivitywidgetB(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.activity_item_type_two, this);
        one= (ImageView) findViewById(R.id.two_image);
        sUtil = GlobalContext.getInstance().getSpUtil();
    }
    public ImageView getImageView(){

        return one;

    }
    public void  setLeftClickFinish(final Context mContext,final ActivityItem activityItem) {
        one.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(mContext, activityItem.getSource(), Toast.LENGTH_SHORT).show();
                Long sourceid = activityItem.getSource_id();
                if (sourceid != null && sourceid > 0) {
                    Intent i = new Intent(mContext, CommodityViewActivity.class);
                    i.putExtra("id",sourceid);
                    i.putExtra("storeId", sUtil.getStoreId()); // FIXME 店铺ID
                    mContext.startActivity(i);

                } else {

                    Intent i = new Intent(mContext, WebWeidianActivity.class);
                    i.putExtra("url", activityItem.getSource());
                    mContext.startActivity(i);
                }
            }
        });

    }

}
