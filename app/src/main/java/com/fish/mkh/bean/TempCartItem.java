package com.fish.mkh.bean;

/**
 * Created by xiniu_wutao on 15/7/27.
 */
public class TempCartItem {
    private  long cartItemId;
    private int count;
    private double amount;

    public long getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(long cartItemId) {
        this.cartItemId = cartItemId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
