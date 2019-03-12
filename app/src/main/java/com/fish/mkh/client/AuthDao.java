package com.fish.mkh.client;

import com.mkh.mobilemall.config.Constants;
import com.xiniunet.api.DefaultXiniuClient;
import com.xiniunet.api.XiniuClient;

public class AuthDao {
	
	public static XiniuClient client = new DefaultXiniuClient(Constants.API_BASE_URL, Constants.API_APP_KEY, Constants.API_SECRET);
}
