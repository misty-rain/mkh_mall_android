package com.mkh.mobilemall.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import com.mkh.mobilemall.app.GlobalContext;

import java.io.*;

/**
 * Created by xiniu_wutao on 15/6/30.
 * 图片帮助类
 */
public class ImageUtil {

    private static AppLogger logger = AppLogger.getLogger(ImageUtil.class);

    public static Bitmap getBigBitmapForDisplay(String imagePath,
                                                Context context) {
        if (null == imagePath || !new File(imagePath).exists())
            return null;
        try {
            int degeree = readPictureDegree(imagePath);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            if (bitmap == null)
                return null;
            DisplayMetrics dm = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
            float scale = bitmap.getWidth() / (float) dm.widthPixels;
            Bitmap newBitMap = null;
            if (scale > 1) {
                newBitMap = zoomBitmap(bitmap, (int) (bitmap.getWidth() / scale), (int) (bitmap.getHeight() / scale));
                bitmap.recycle();
                Bitmap resultBitmap = rotaingImageView(degeree, newBitMap);
                return resultBitmap;
            }
            Bitmap resultBitmap = rotaingImageView(degeree, bitmap);
            return resultBitmap;
        } catch (Exception e) {
            logger.e(e.getMessage());
            return null;
        }
    }

    private static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        if (null == bitmap) {
            return null;
        }
        try {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            Matrix matrix = new Matrix();
            float scaleWidth = ((float) width / w);
            float scaleHeight = ((float) height / h);
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
            return newbmp;
        } catch (Exception e) {
            logger.e(e.getMessage());
            return null;
        }
    }


    public Intent getPhotoPickIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        return intent;
    }


    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);// 压缩位图
            byte[] bytes = baos.toByteArray();// 创建分配字节数组
            return bytes;
        } catch (Exception e) {
            AppLogger.getLogger(ImageUtil.class).e(e.getMessage());
            return null;
        } finally {
            if (null != baos) {
                try {
                    baos.flush();
                    baos.close();
                } catch (IOException e) {
                    AppLogger.getLogger(ImageUtil.class).e(e.getMessage());
                }
            }
        }
    }

    /**
     * @param path
     * @return
     * @throws IOException
     * @Description 上传服务器前调用该方法进行压缩
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap revitionImage(String path) throws IOException {
        if (null == path || TextUtils.isEmpty(path) || !new File(path).exists())
            return null;
        BufferedInputStream in = null;
        try {
            int degree = readPictureDegree(path);
            in = new BufferedInputStream(new FileInputStream(new File(path)));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            options.inSampleSize = calculateInSampleSize(options, 400, 600);

            in.close();
            in = new BufferedInputStream(new FileInputStream(new File(path)));
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
            Bitmap newbitmap = rotaingImageView(degree, bitmap);
            return newbitmap;
        } catch (Exception e) {
            AppLogger.getLogger(ImageUtil.class).e(e.getMessage());
            return null;
        } finally {
            if (null != in) {
                in.close();
                in = null;
            }
        }
    }

    public String getImagePathFromUri(Uri uri) {
        // 如果是file，直接拿
        if (uri.getScheme().equalsIgnoreCase("file")) {
            return uri.getPath();
        }

        String[] projection = {
                MediaStore.Images.Media.DATA
        };
        Cursor cursor = GlobalContext.getInstance().getContentResolver().query(uri, projection,
                null, null, null);
        int column_index = cursor.getColumnIndex(projection[0]);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();

        return path;
    }


    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            AppLogger.getLogger(ImageUtil.class).e(e.getMessage());
        }
        return degree;
    }

    /*
     * 旋转图片
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        if (null == bitmap) {
            return null;
        }
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

}
