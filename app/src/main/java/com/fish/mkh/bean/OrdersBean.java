package com.fish.mkh.bean;

import java.io.Serializable;
import java.util.List;

public class OrdersBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7862605388029048513L;

	private List<MyOrderbean> data;
	private Long OrderId;

	public List<MyOrderbean> getData() {
		return data;
	}

	public void setData(List<MyOrderbean> data) {
		this.data = data;
	}

	public Long getOrderId() {
		return OrderId;
	}

	public void setOrderId(Long orderId) {
		OrderId = orderId;
	}
	
}
