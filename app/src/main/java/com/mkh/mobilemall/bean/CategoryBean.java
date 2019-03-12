package com.mkh.mobilemall.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 分类
 */
public class CategoryBean implements Parcelable {
    private String id;
    private String name;
    private String description;
    private String parent_id;
    private String order_index;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getOrder_index() {
        return order_index;
    }

    public void setOrder_index(String order_index) {
        this.order_index = order_index;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(parent_id);
        dest.writeString(order_index);

    }

    public static final Creator<CategoryBean> CREATOR = new Creator<CategoryBean>() {

        @Override
        public CategoryBean[] newArray(int size) {
            // TODO Auto-generated method stub
            return new CategoryBean[size];
        }

        @Override
        public CategoryBean createFromParcel(Parcel source) {
            CategoryBean entity = new CategoryBean();
            entity.id = source.readString();
            entity.name = source.readString();
            entity.description = source.readString();
            entity.parent_id = source.readString();
            entity.order_index = source.readString();

            return entity;
        }
    };

}
