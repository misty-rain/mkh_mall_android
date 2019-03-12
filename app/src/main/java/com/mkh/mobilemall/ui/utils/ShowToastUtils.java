package com.mkh.mobilemall.ui.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by zwd on 15/7/17.
 */
public class ShowToastUtils {
    private static Toast mToast;
    public static void  showToast(String text,Context context) {
        if(mToast == null) {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }
}
