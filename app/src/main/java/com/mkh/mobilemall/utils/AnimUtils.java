package com.mkh.mobilemall.utils;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;

import com.mkh.mobilemall.support.db.TempComCarDBTask;

/**
 * Created by xiniu_wutao on 15/7/3.
 * 常用动画工具类
 */
public class AnimUtils {
    /**
     * @Description: 创建动画层
     * @param
     * @return void
     * @throws
     */

    private static int buyNum = 0;//购买数量
    private static ViewGroup anim_mask_layout;


    /**
     * 创建动画层
     *
     * @param context
     * @param value
     * @return
     */

    public static ViewGroup createAnimLayout(Activity context, int value) {
        ViewGroup rootView = (ViewGroup) context.getWindow().getDecorView();
        LinearLayout animLayout = new LinearLayout(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                  LinearLayout.LayoutParams.MATCH_PARENT,
                  LinearLayout.LayoutParams.MATCH_PARENT);
        animLayout.setLayoutParams(lp);
        animLayout.setId(value);
        animLayout.setBackgroundResource(android.R.color.transparent);
        rootView.addView(animLayout);
        return animLayout;

    }

    public static View addViewToAnimLayout(final ViewGroup vg, final View view,
                                           int[] location) {
        int x = location[0];
        int y = location[1];
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                  LinearLayout.LayoutParams.WRAP_CONTENT,
                  LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = x;
        lp.topMargin = y;
        view.setLayoutParams(lp);
        return view;
    }

    // 减的动画
    public static void setAnimForSub(Activity context, int value, final View v, int[] start_location, View shopCart, final BadgeView buyNumView) {
        int[] end_location = new int[2];// 这是用来存储动画结束位置的X、Y坐标
        shopCart.getLocationInWindow(end_location);// shopCart是那个购物车
        anim_mask_layout = null;
        anim_mask_layout = createAnimLayout(context, value);
         View view=null;
        try {


            if (anim_mask_layout.getChildCount() != 0)
                anim_mask_layout.removeAllViews();
            anim_mask_layout.addView(v);//把动画小球添加到动画层
             view= addViewToAnimLayout(anim_mask_layout, v,
                      end_location);

        } catch (Exception e) {

        }

        // 计算位移
        int endX = -(end_location[0] - start_location[0] + 40);// 动画位移的X坐标
        int endY = -(end_location[1] - start_location[1]);// 动画位移的y坐标

        TranslateAnimation translateAnimationX = new TranslateAnimation(0,
                  endX, 0, 0);
        translateAnimationX.setInterpolator(new LinearInterpolator());
        translateAnimationX.setRepeatCount(0);// 动画重复执行的次数
        translateAnimationX.setFillAfter(true);

        TranslateAnimation translateAnimationY = new TranslateAnimation(0, 0,
                  0, endY);
        translateAnimationY.setInterpolator(new AccelerateInterpolator());
        translateAnimationY.setRepeatCount(0);// 动画重复执行的次数
        translateAnimationX.setFillAfter(true);

        AnimationSet set = new AnimationSet(false);
        set.setFillAfter(false);
        set.addAnimation(translateAnimationY);
        set.addAnimation(translateAnimationX);
        set.setDuration(800);// 动画的执行时间
        view.startAnimation(set);
        // 动画监听事件
        set.setAnimationListener(new AnimationListener() {
            // 动画的开始
            @Override
            public void onAnimationStart(Animation animation) {
                v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            // 动画的结束
            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
                buyNum = TempComCarDBTask.getInstance().getTempComCarTotalCount();
                buyNumView.setText(buyNum + "");//
                if (buyNum != 0) {
                    buyNumView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                    buyNumView.show();
                } else {
                    buyNumView.hide();
                }
            }
        });

    }

    // 加的动画
    public static void setAnim(Activity context, int value, final View v, int[] start_location, View shopCart, final BadgeView buyNumView) {
        int[] end_location = new int[2];// 这是用来存储动画结束位置的X、Y坐标
        shopCart.getLocationInWindow(end_location);// shopCart是那个购物车
        anim_mask_layout = null;
        anim_mask_layout = createAnimLayout(context, value);
         View view=null;
        try {


            if (anim_mask_layout.getChildCount() != 0)
                anim_mask_layout.removeAllViews();
            anim_mask_layout.addView(v);//把动画小球添加到动画层
             view= addViewToAnimLayout(anim_mask_layout, v,
                      start_location);

        } catch (Exception e) {
        }
        // 计算位移
        int endX = end_location[0] - start_location[0] + 40;// 动画位移的X坐标
        int endY = end_location[1] - start_location[1];// 动画位移的y坐标


        TranslateAnimation translateAnimationX = new TranslateAnimation(0,
                  endX, 0, 0);
        translateAnimationX.setInterpolator(new LinearInterpolator());
        translateAnimationX.setRepeatCount(0);// 动画重复执行的次数
        translateAnimationX.setFillAfter(true);

        TranslateAnimation translateAnimationY = new TranslateAnimation(0, 0,
                  0, endY);
        translateAnimationY.setInterpolator(new AccelerateInterpolator());
        translateAnimationY.setRepeatCount(0);// 动画重复执行的次数
        translateAnimationX.setFillAfter(true);

        AnimationSet set = new AnimationSet(false);
        set.setFillAfter(false);
        set.addAnimation(translateAnimationY);
        set.addAnimation(translateAnimationX);
        set.setDuration(800);// 动画的执行时间
        view.startAnimation(set);
        // 动画监听事件
        set.setAnimationListener(new AnimationListener() {
            // 动画的开始
            @Override
            public void onAnimationStart(Animation animation) {
                v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            // 动画的结束
            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
                buyNum = TempComCarDBTask.getInstance().getTempComCarTotalCount();
                buyNumView.setText(buyNum + "");//
                buyNumView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                buyNumView.show();
            }
        });

    }
}
