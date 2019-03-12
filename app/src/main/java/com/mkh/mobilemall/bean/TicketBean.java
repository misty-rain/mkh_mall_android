package com.mkh.mobilemall.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015/1/16.
 */
public class TicketBean implements Parcelable {
    private String productName;
    private String code;
    private double amount;

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    private double totalAmount;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private  String id;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public TicketBean() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.productName);
        dest.writeString(this.code);
        dest.writeDouble(this.amount);
        dest.writeString(this.id);
        dest.writeDouble(this.totalAmount);
    }

    private TicketBean(Parcel in) {
        this.productName = in.readString();
        this.code = in.readString();
        this.amount = in.readDouble();
        this.id = in.readString();
        this.totalAmount=in.readDouble();
    }

    public static final Creator<TicketBean> CREATOR = new Creator<TicketBean>() {
        public TicketBean createFromParcel(Parcel source) {
            return new TicketBean(source);
        }

        public TicketBean[] newArray(int size) {
            return new TicketBean[size];
        }
    };
}
