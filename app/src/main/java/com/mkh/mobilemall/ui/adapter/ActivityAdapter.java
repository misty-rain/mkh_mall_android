package com.mkh.mobilemall.ui.adapter;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mkh.mobilemall.R;
import com.mkh.mobilemall.support.http.file.AsyncImageLoader;
import com.mkh.mobilemall.ui.activity.commodity.CommodityViewActivity;
import com.mkh.mobilemall.ui.widget.ActivitywidgetA;
import com.mkh.mobilemall.ui.widget.ActivitywidgetB;
import com.mkh.mobilemall.ui.widget.ActivitywidgetC;
import com.mkh.mobilemall.ui.widget.ActivitywidgetD;
import com.mkh.mobilemall.ui.widget.ActivitywidgetE;
import com.mkh.mobilemall.utils.ImageLoaderUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiniunet.api.domain.ecommerce.SimpleItem;
import com.xiniunet.api.domain.master.Activities;
import com.xiniunet.api.domain.master.ActivityItem;

import java.util.List;

/**
 * Created by zwd on 15/7/15.
 */
public class ActivityAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Activities>activitiesList;
    private AsyncImageLoader imageLoader;
    DisplayImageOptions options;
    int Type_one=1;
    int Type_two=2;
    int Type_three=3;
    int Type_four=4;
    int Type_five=5;


    public ActivityAdapter(Context context,List<Activities> activitiesList) {
        super();
        this.mContext = context;
        this.activitiesList=activitiesList;
        this.mInflater = LayoutInflater.from(context);
        imageLoader=new AsyncImageLoader(mContext);
        options=new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
    }
    @Override
    public int getCount() {
        return (null == activitiesList) ? 0 : activitiesList.size();
    }

    @Override
    public Object getItem(int position) {
        return (null == activitiesList) ? null : activitiesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        // TODO Auto-generated method stub
        return 5;
    }

    @Override
    public int getItemViewType(int position) {
        String dataType=activitiesList.get(position).getDataType();
        if("R1".equals(dataType)){
            return Type_one;
        }else if("R2".equals(dataType)){
            return Type_two;
        }else if("R3".equals(dataType)){
            return Type_three;
        }else if("R4".equals(dataType)){
            return Type_four;
        }else if("R5".equals(dataType)){
            return Type_five;
        }
        return super.getItemViewType(position);


    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (null == mContext) {
            return null;
        }
        final ViewHolder viewHolder;
        // if (null == convertView) {
             viewHolder = new ViewHolder();
             convertView = mInflater.inflate(R.layout.list_activity_item_dynamic, parent,
                    false);
            viewHolder.textTitle=(TextView)convertView.findViewById(R.id.item_title);
            /*viewHolder.one = (ImageView)convertView.findViewById(R.id.imageView_one);
            viewHolder.two = (ImageView)convertView.findViewById(R.id.imageView_two);
            viewHolder.three = (ImageView)convertView.findViewById(R.id.imageView_three);*/
            viewHolder.dynamicLayout= (LinearLayout) convertView.findViewById(R.id.dynamic_layout);
            convertView.setTag(viewHolder);
           //  } else {
             //  viewHolder = (ViewHolder) convertView.getTag();
     //   }
        if(activitiesList.get(position).getName()!=null)
        viewHolder.textTitle.setText(activitiesList.get(position).getName());
        else
        viewHolder.textTitle.setVisibility(View.GONE);

        final  List<ActivityItem> simpleItemList=activitiesList.get(position).getItemList();

        int a=getItemViewType(position);
        if(simpleItemList!=null||simpleItemList.size()>0) {
            if(a==1){
             for( int i=0;i<simpleItemList.size();i++) {
                 ActivitywidgetA imgActiiies=null;
                 imgActiiies = new ActivitywidgetA(mContext,null);
                 imgActiiies.getImageView().setScaleType(ImageView.ScaleType.FIT_XY);
                 imgActiiies.getImageView().setId(position);   //注意这点 设置id
              /*   //  RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                 RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imgActiiies.getLayoutParams();
                 params.height = 24;
                 params.width = ViewGroup.LayoutParams.MATCH_PARENT;
*/
                // imgActiiies.setLayoutParams(params);
                 imgActiiies.setLeftClickFinish(mContext,simpleItemList.get(i));
                 imgActiiies.getImageView().setImageResource(R.mipmap.dedault);
                 ImageLoaderUtil.getImageLoaderInstance().displayImage(simpleItemList.get(i).getPictureUrl(), imgActiiies.getImageView(), options);
                 viewHolder.dynamicLayout.addView(imgActiiies);
                   }
             }else if(a==2){
                for(int i=0;i<simpleItemList.size();i++) {
                    ActivitywidgetB imgActiiies=null;
                    imgActiiies = new ActivitywidgetB(mContext,null);
                    imgActiiies.getImageView().setScaleType(ImageView.ScaleType.FIT_XY);
                    imgActiiies.setId(position);   //注意这点 设置id
                    imgActiiies.setLeftClickFinish(mContext,simpleItemList.get(i));
                    imgActiiies.getImageView().setImageResource(R.mipmap.dedault);
                    ImageLoaderUtil.getImageLoaderInstance().displayImage(simpleItemList.get(i).getPictureUrl(), imgActiiies.getImageView(), options);
                    viewHolder.dynamicLayout.addView(imgActiiies);
                }
            }
            else if(a==3){
                for(int i=0;i<simpleItemList.size();i++) {
                    ActivitywidgetC imgActiiies=null;
                    imgActiiies = new ActivitywidgetC(mContext,null);
                    imgActiiies.getImageView().setScaleType(ImageView.ScaleType.FIT_XY);
                    imgActiiies.setId(position);   //注意这点 设置id
                    imgActiiies.setLeftClickFinish(mContext,simpleItemList.get(i));
                    imgActiiies.getImageView().setImageResource(R.mipmap.dedault);
                    ImageLoaderUtil.getImageLoaderInstance().displayImage(simpleItemList.get(i).getPictureUrl(), imgActiiies.getImageView(), options);
                    viewHolder.dynamicLayout.addView(imgActiiies);
                }
            }

            else if(a==4){
                for(int i=0;i<simpleItemList.size();i++) {
                    ActivitywidgetD imgActiiies=null;
                    imgActiiies = new ActivitywidgetD(mContext,null);
                    imgActiiies.getLeftImageView().setScaleType(ImageView.ScaleType.FIT_XY);
                    imgActiiies.getRightImageView().setScaleType(ImageView.ScaleType.FIT_XY);
                    imgActiiies.setId(position);   //注意这点 设置id
                    imgActiiies.setLeftClickFinish(mContext,simpleItemList.get(i));
                    imgActiiies.setRightClickFinish(mContext,simpleItemList.get(i));
                    imgActiiies.getLeftImageView().setImageResource(R.mipmap.dedault);
                    imgActiiies.getRightImageView().setImageResource(R.mipmap.dedault);
                    ImageLoaderUtil.getImageLoaderInstance().displayImage(simpleItemList.get(i).getPictureUrl(), imgActiiies.getLeftImageView(), options);
                    ImageLoaderUtil.getImageLoaderInstance().displayImage(simpleItemList.get(i).getPictureUrl(), imgActiiies.getRightImageView(), options);
                    viewHolder.dynamicLayout.addView(imgActiiies);
                }
            }else if(a==5){
                for(int i=0;i<simpleItemList.size();i++) {
                    ActivitywidgetE imgActiiies=null;
                    imgActiiies = new ActivitywidgetE(mContext,null);
                    imgActiiies.getImageView(1).setScaleType(ImageView.ScaleType.FIT_XY);
                    imgActiiies.getImageView(2).setScaleType(ImageView.ScaleType.FIT_XY);
                    imgActiiies.getImageView(3).setScaleType(ImageView.ScaleType.FIT_XY);
                    imgActiiies.setId(position);   //注意这点 设置id
                    imgActiiies.setLeftClickFinish(mContext,simpleItemList.get(i));
                    imgActiiies.setMidClickFinish(mContext,simpleItemList.get(i));
                    imgActiiies.setRightClickFinish(mContext,simpleItemList.get(i));
                    imgActiiies.getImageView(1).setImageResource(R.mipmap.dedault);
                    imgActiiies.getImageView(2).setImageResource(R.mipmap.dedault);
                    imgActiiies.getImageView(3).setImageResource(R.mipmap.dedault);
                    ImageLoaderUtil.getImageLoaderInstance().displayImage(simpleItemList.get(i).getPictureUrl(), imgActiiies.getImageView(1), options);
                    ImageLoaderUtil.getImageLoaderInstance().displayImage(simpleItemList.get(i).getPictureUrl(), imgActiiies.getImageView(2), options);
                    ImageLoaderUtil.getImageLoaderInstance().displayImage(simpleItemList.get(i).getPictureUrl(), imgActiiies.getImageView(3), options);
                    viewHolder.dynamicLayout.addView(imgActiiies);
                }
            }



        }
            return convertView;

    }
    class ViewHolder {
        TextView textTitle;
        ImageView one,two,three;
        LinearLayout dynamicLayout;

    }


}
