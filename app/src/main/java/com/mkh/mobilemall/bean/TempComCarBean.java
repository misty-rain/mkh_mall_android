package com.mkh.mobilemall.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 临时购物单 bean
 *
 * @author misty-rain
 * @ClassName: TempComCarBean
 * @Description: 临时购物单 bean
 * @date 2014-12-11 上午11:24:18
 */
public class TempComCarBean implements Parcelable {
    private String id;
    private String commodName;
    private Integer singleCommodTotalCount;
    private Integer commodTotalCount;
    private double singleCommodPrice;
    private double commodTotalPrice;
    private String singleComPicUrl;

    public Long getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(Long cartItemId) {
        this.cartItemId = cartItemId;
    }

    private String operationTime;
    private String flag;
    private String shortName;
    private Long cartItemId;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    private Long productId;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    private String number;


    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getSingleComPicUrl() {
        return singleComPicUrl;
    }

    public void setSingleComPicUrl(String singleComPicUrl) {
        this.singleComPicUrl = singleComPicUrl;
    }

    public String getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(String operationTime) {
        this.operationTime = operationTime;
    }

    public String getCommodName() {
        return commodName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCommodName(String commodName) {
        this.commodName = commodName;
    }

    public Integer getSingleCommodTotalCount() {
        return singleCommodTotalCount;
    }

    public void setSingleCommodTotalCount(Integer singleCommodTotalCount) {
        this.singleCommodTotalCount = singleCommodTotalCount;
    }

    public Integer getCommodTotalCount() {
        return commodTotalCount;
    }

    public void setCommodTotalCount(Integer commodTotalCount) {
        this.commodTotalCount = commodTotalCount;
    }

    public double getSingleCommodPrice() {
        return singleCommodPrice;
    }

    public void setSingleCommodPrice(double singleCommodPrice) {
        this.singleCommodPrice = singleCommodPrice;
    }

    public double getCommodTotalPrice() {
        return commodTotalPrice;
    }

    public void setCommodTotalPrice(double commodTotalPrice) {
        this.commodTotalPrice = commodTotalPrice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.commodName);
        dest.writeValue(this.singleCommodTotalCount);
        dest.writeValue(this.commodTotalCount);
        dest.writeDouble(this.singleCommodPrice);
        dest.writeDouble(this.commodTotalPrice);
        dest.writeString(this.singleComPicUrl);
        dest.writeString(this.operationTime);
        dest.writeString(this.flag);
        dest.writeLong(this.cartItemId);

    }

    public TempComCarBean() {
    }

    private TempComCarBean(Parcel in) {
        this.id = in.readString();
        this.commodName = in.readString();
        this.singleCommodTotalCount = (Integer) in.readValue(Integer.class
                .getClassLoader());
        this.commodTotalCount = (Integer) in.readValue(Integer.class
                .getClassLoader());
        this.singleCommodPrice = in.readDouble();
        this.commodTotalPrice = in.readDouble();
        this.singleComPicUrl = in.readString();
        this.operationTime = in.readString();
        this.flag = in.readString();
        this.cartItemId=in.readLong();

    }

    public static final Creator<TempComCarBean> CREATOR = new Creator<TempComCarBean>() {
        public TempComCarBean createFromParcel(Parcel source) {
            return new TempComCarBean(source);
        }

        public TempComCarBean[] newArray(int size) {
            return new TempComCarBean[size];
        }
    };

}
