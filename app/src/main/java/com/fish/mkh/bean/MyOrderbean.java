package com.fish.mkh.bean;

import java.io.Serializable;

public class MyOrderbean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5997145665658321487L;
	private String title;
//	private String subtitle;
//	private String content;
//	private String date;
	private String cost;
	private String imageUrl;
	private Long goodsId;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCost() {
		return cost;
	}
	public void setCost(String cost) {
		this.cost = cost;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public Long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	
}
