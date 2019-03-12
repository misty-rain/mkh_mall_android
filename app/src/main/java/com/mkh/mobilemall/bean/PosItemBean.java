package com.mkh.mobilemall.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class PosItemBean implements Parcelable {
    private String id;
    private String name;
    private String picture_url;

    public Long getProductID() {
        return productID;
    }

    public void setProductID(Long productID) {
        this.productID = productID;
    }

    private Long productID;

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    private double unit_price;
    private String is_active;
    private String specification;

    private String shortName;

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    private String sortLetters;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    private String number;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture_url() {
        return picture_url;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

    public double getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(double unit_price) {
        this.unit_price = unit_price;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.picture_url);
        dest.writeDouble(this.unit_price);
        dest.writeString(this.is_active);
        dest.writeString(this.specification);
        dest.writeString(this.number);
        dest.writeString(this.sortLetters);
    }

    public PosItemBean() {
    }

    private PosItemBean(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.picture_url = in.readString();
        this.unit_price = in.readDouble();
        this.is_active = in.readString();
        this.specification = in.readString();
        this.number = in.readString();
        this.sortLetters = in.readString();
    }

    public static final Creator<PosItemBean> CREATOR = new Creator<PosItemBean>() {
        public PosItemBean createFromParcel(Parcel source) {
            return new PosItemBean(source);
        }

        public PosItemBean[] newArray(int size) {
            return new PosItemBean[size];
        }
    };


}
