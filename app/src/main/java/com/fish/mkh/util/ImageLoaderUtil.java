package com.fish.mkh.util;

import android.content.Context;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


public class ImageLoaderUtil {
	

	private static DisplayImageOptions getDefaultOptions() {
		return new DisplayImageOptions.Builder()
				.showImageOnLoading(android.R.color.transparent)
				.showImageForEmptyUri(android.R.color.transparent)
				.showImageOnFail(android.R.color.transparent)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.build();

	}
	
	private static DisplayImageOptions.Builder getBuilder() {
		return new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true);
	}

	/**
	 * 
	 * @param url
	 * @param iv
	 * @param defaultRes
	 */
	public static void loadImg(String url, ImageView iv, int defaultRes,Context content) {
      	if(!ImageLoader.getInstance().isInited())
    			ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(content));
      	
		DisplayImageOptions op = getBuilder().showImageOnFail(defaultRes)
				.showImageForEmptyUri(defaultRes)
				.showImageOnLoading(defaultRes)
				.build();
		ImageLoader.getInstance().displayImage(url, iv, op);
	}

	public static void loadImg(String url, ImageView iv,Context content) {
      	if(!ImageLoader.getInstance().isInited())
    			ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(content));
		ImageLoader.getInstance().displayImage(url, iv, getDefaultOptions());
	}

	public static void loadImg(String url, ImageView iv,
			ImageLoadingListener listener,Context content) {
      	if(!ImageLoader.getInstance().isInited())
    			ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(content));
		if (listener != null) {
			ImageLoader.getInstance().displayImage(url, iv,
					getDefaultOptions(), listener);
		} else {
			ImageLoader.getInstance().displayImage(url, iv,
					getDefaultOptions());
		}
	}
}
