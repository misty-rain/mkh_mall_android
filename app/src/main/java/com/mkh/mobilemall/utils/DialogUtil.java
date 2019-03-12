package com.mkh.mobilemall.utils;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import com.mkh.mobilemall.R;
import com.mkh.mobilemall.ui.widget.dialog.WaitDialog;


/**
 * 对话框工具类 ，调用时只需 DialogUtil.
 *
 * @author Administrator
 */
public class DialogUtil {


    /**
     * 黑色主题 的请求对话框 ，如无需改动 显示消息 ，传null
     *
     * @param context
     * @param displayMessage
     * @return
     */
    public static Dialog getRequestDialogForBlack(Activity context,
                                                  String displayMessage) {
        final Dialog dialog = new Dialog(context, R.style.Dialog);
        dialog.setContentView(R.layout.request_dialog_black);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (displayMessage != null) {
            TextView titleTxtv = (TextView) dialog.findViewById(R.id.tips_msg);
            titleTxtv.setText(displayMessage);
        }
        return dialog;

    }
    /**
     * @param
     * @param
     * @return 弹出客服对话框
     */
    public static Dialog getCustomServiceDialog(final Activity context,final String phoneNum){

        final Dialog dialog = new Dialog(context, R.style.Dialog);
        dialog.setContentView(R.layout.customservicedialog);
        TextView cancel=(TextView)dialog.findViewById(R.id.custom_cancel);
        TextView confrim=(TextView)dialog.findViewById(R.id.custom_confrim);
        TextView txtPhone= (TextView) dialog.findViewById(R.id.txtphone);
        txtPhone.setText("确认拨打电话: "+phoneNum+" 吗?");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        confrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phoneNum));
                context.startActivity(intent);
            }
        });

        return dialog;
    }

    /**
     * 普通对话框
     * @param context
     * @return
     */
    public static Dialog getCustomeDilogForNormal(final Activity context){

        final Dialog dialog = new Dialog(context, R.style.Dialog);
        dialog.setContentView(R.layout.is_customer_dialog);
        return dialog;
    }

    public static Dialog getCustomDialog(Activity context) {
        final Dialog dialog = new Dialog(context, R.style.Dialog);
        return dialog;
    }

    public static int getScreenWidth(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static int getScreenHeight(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }


    public static WaitDialog getWaitDialog(Activity activity, int message) {
        WaitDialog dialog = null;
        try {
            dialog = new WaitDialog(activity, R.style.dialog_waiting);
            dialog.setMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;
    }

    public static WaitDialog getWaitDialog(Activity activity, String message) {
        WaitDialog dialog = null;
        try {
            dialog = new WaitDialog(activity, R.style.dialog_waiting);
            dialog.setMessage(message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dialog;
    }

    public static WaitDialog getCancelableWaitDialog(Activity activity,
                                                     String message) {
        WaitDialog dialog = null;
        try {
            dialog = new WaitDialog(activity, R.style.dialog_waiting);
            dialog.setMessage(message);
            dialog.setCancelable(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
       return dialog;
    }



}
