package com.mkh.mobilemall.ui.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.widget.TextView;

import com.mkh.mobilemall.utils.Utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by xiniu_wutao on 15/8/6.
 */
public class HtmlImageGetter implements Html.ImageGetter {
    TextView textView;
    Context context;
    public HtmlImageGetter(Context contxt, TextView textView) {
        this.context = contxt;
        this.textView = textView;
    }

    @Override
    public Drawable getDrawable(String paramString) {
        URLDrawable urlDrawable = new URLDrawable(context);

        ImageGetterAsyncTask getterTask = new ImageGetterAsyncTask(urlDrawable);
        getterTask.execute(paramString);
        return urlDrawable;
    }

    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
        URLDrawable urlDrawable;

        public ImageGetterAsyncTask(URLDrawable drawable) {
            this.urlDrawable = drawable;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            if (result != null) {
                urlDrawable.drawable = result;

                HtmlImageGetter.this.textView.requestLayout();
            }
        }

        @Override
        protected Drawable doInBackground(String... params) {
            String source = params[0];
            return fetchDrawable(source);
        }

        public Drawable fetchDrawable(String url) {
            Drawable drawable = null;
            URL Url;
            try {
                Url = new URL(url);
                drawable = Drawable.createFromStream(Url.openStream(), "");
            } catch (Exception e) {
                Log.e("dd",e.getMessage());
                return null;
            }
//按比例缩放图片
            Rect bounds = getDefaultImageBounds(context);
            int newwidth = bounds.width();
            int newheight = bounds.height();
            double factor = 1;
            double fx = (double)drawable.getIntrinsicWidth() / (double)newwidth;
            double fy = (double)drawable.getIntrinsicHeight() / (double)newheight;
            factor = fx > fy ? fx : fy;
            if (factor < 1) factor = 1;
            newwidth = (int)(drawable.getIntrinsicWidth() / factor);
            newheight = (int)(drawable.getIntrinsicHeight() / factor);
            drawable.setBounds(0, 0, newwidth, newheight);
            return drawable;
        }
    }

    //预定图片宽高比例为 4:3
    @SuppressWarnings("deprecation")
    public Rect getDefaultImageBounds(Context context) {
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = (int) (width * 3 / 4);
        Rect bounds = new Rect(0, 0, width, height);
        return bounds;
    }


}

