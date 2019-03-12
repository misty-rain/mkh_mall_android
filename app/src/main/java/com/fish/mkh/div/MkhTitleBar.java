package com.fish.mkh.div;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mkh.mobilemall.R;


/**
 * titlebar
 * @author Wang
 */
public class MkhTitleBar extends RelativeLayout {
	
	private TextView mTvTitle,mRightTv;
	
	private ImageButton mRightButton;
	
	private LinearLayout mLeftLayout;
	
	private RelativeLayout mRootLayout;
	
	private CheckBox mRightSwitch;
	
	
	public MkhTitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		View.inflate(context, R.layout.layout_titlebar, this);
		mTvTitle = (TextView) findViewById(R.id.tv_fctitlebar_title);
		mRightButton = (ImageButton) findViewById(R.id.ib_fctitlebar_right);
		mLeftLayout = (LinearLayout) findViewById(R.id.ll_fctitlebar_left);
		mRootLayout = (RelativeLayout) findViewById(R.id.rl_titlebar_root);
		mRightTv = (TextView)findViewById(R.id.tv_fctitlebar_right);
		mRightSwitch = (CheckBox)findViewById(R.id.ib_fctitlebar_switch);
	}
	
	/**
	 * 设置左边部分是否可见
	 * @param visibility
	 */
	public void setLeftSideVisible(int visibility) {
		mLeftLayout.setVisibility(visibility);
	}
	
	/**
	 * 隐藏左边的
	 */
	public void hideLeftSide() {
		mLeftLayout.setVisibility(View.GONE);
	}

	/**
	 * 设置标题
	 * @param text
	 * @return
	 */
	public MkhTitleBar setTitle(String text) {
		mTvTitle.setText(text);
		return this;
	}
	
	/**
	 * 设置titlebar的背景色
	 * @param color
	 */
	public void setTitleBarBgColor(int color) {
		mRootLayout.setBackgroundColor(color);
	}
	
	/**
	 * 右边的图片按钮
	 * @param resId
	 * @param listener
	 * @return
	 */
	public MkhTitleBar setRightButton(int resId, OnClickListener listener) {
		mRightButton.setImageResource(resId);
		mRightButton.setBackgroundColor(Color.TRANSPARENT);
		mRightButton.setOnClickListener(listener);
		mRightButton.setVisibility(View.VISIBLE);
		return this;
	}
	
	/**
	 * 右边的文字按钮按钮
	 * @param text
	 * @param listener
	 * @return
	 */
	public MkhTitleBar setRightTextButton(String text, OnClickListener listener) {
		mRightTv.setVisibility(View.VISIBLE);
		mRightTv.setText(text);
		mRightTv.setOnClickListener(listener);
		return this;
	}
	public MkhTitleBar setRightTextButton1(String text, OnClickListener listener) {
		mRightTv.setVisibility(View.VISIBLE);
		mRightTv.setText(text);
		mRightTv.setTextColor(Color.BLACK);
		mRightTv.setOnClickListener(listener);
		return this;
	}
	
	/**
	 * 右边的switch按钮
	 * @param resId
	 * @param listener
	 * @return
	 */
	public MkhTitleBar setRightSwitch(OnCheckedChangeListener listener) {

		mRightSwitch.setOnCheckedChangeListener(listener);
		mRightSwitch.setVisibility(View.GONE);
		return this;
	}
	
	public boolean getRightSwitchChecked(){
		return mRightSwitch.isChecked();
	}
	
	public void hideRightTextButton() {
		mRightTv.setVisibility(View.GONE);
	}
	
	
	/**
	 * 设置左边返回点击finish的activity
	 * @param aty
	 * @return
	 */
	public MkhTitleBar setLeftClickFinish(final Activity aty,OnClickListener listener) {
		mLeftLayout.setOnClickListener(listener);
		return this;
	}
	
	/**
	 * 设置左边返回点击finish的activity
	 * @param aty
	 * @return
	 */
	public MkhTitleBar setLeftClickFinish(final Activity aty) {
		mLeftLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				aty.finish();
			}
		});
		return this;
	}
	public MkhTitleBar setkeboradClickFinish(final Activity aty , final View flag) {
		mLeftLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

					InputMethodManager imm = (InputMethodManager) aty.getSystemService(Activity.INPUT_METHOD_SERVICE);
				if(imm.isActive()){
					imm.hideSoftInputFromWindow(flag.getWindowToken(), 0);
				}



				aty.finish();
			}
		});
		return this;
	}
	public MkhTitleBar setClickFinish(final Activity aty, final Intent intent){
		mLeftLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				aty.setResult(1, intent);
				aty.finish();
			}
		});
		return this;

	}
	/**
	 * 设置标题
	 * @param text
	 * @return
	 */
	public MkhTitleBar setTitle(String text,int color) {
		mTvTitle.setText(text);
		mTvTitle.setTextColor(getResources().getColor(color));
		return this;
	}
}
