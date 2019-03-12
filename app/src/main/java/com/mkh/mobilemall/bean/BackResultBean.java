package com.mkh.mobilemall.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015/1/9.
 */
public class BackResultBean implements Parcelable {
    private String code;
   private  boolean result;
    private String message;
    private String keyStr;
    private String orderNumber;



    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    private long orderId;

    public int getPosition() {
        return position;
    }

    public String getKeyStr() {
        return keyStr;
    }

    public void setKeyStr(String keyStr) {
        this.keyStr = keyStr;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private int position;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public boolean getResult(){
        return  result;
    }
    public void setResult(boolean result){
        this.result=result;
    }




    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;

    public BackResultBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.code);
        dest.writeString(this.message);
        dest.writeString(this.type);
        dest.writeInt(this.position);
        dest.writeLong(this.orderId);
        dest.writeString(this.keyStr);
        dest.writeString(this.orderNumber);
    }

    private BackResultBean(Parcel in) {
        this.code = in.readString();
        this.message = in.readString();
        this.type = in.readString();
        this.position=in.readInt();
        this.orderId=in.readLong();
        this.keyStr=in.readString();
        this.orderNumber=in.readString();
    }

    public static final Creator<BackResultBean> CREATOR = new Creator<BackResultBean>() {
        public BackResultBean createFromParcel(Parcel source) {
            return new BackResultBean(source);
        }

        public BackResultBean[] newArray(int size) {
            return new BackResultBean[size];
        }
    };
}
