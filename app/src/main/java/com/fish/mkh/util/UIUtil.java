package com.fish.mkh.util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class UIUtil {
	
	public static void showWaitDialog(Activity aty) {
		CustomProgressDialog.show(aty);
	}
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static void dismissDlg() {
		CustomProgressDialog.hidden();
	}

	public static void showToast(Activity aty,String msg) {
		Toast.makeText(aty, msg, Toast.LENGTH_LONG).show();
	}
	private static long lastClickTime = 0;
	
	// 防止按钮重复点击
	public static boolean isFastDoubleClick(float ts) {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		lastClickTime = time;
		if (0 < timeD && timeD < ts * 1000) {
			return true;
		}
		return false;
	}
	
	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	public static String isNull(String msg){
		if(null == msg || "null".equals(msg)){
			return "0";
		}else{
			return msg;
		}
	}
}
