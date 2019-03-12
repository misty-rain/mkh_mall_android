package com.mkh.mobilemall.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xiniu_wutao on 15/7/1.
 */
public class CategoryRetailItem implements Parcelable {

    private Long id;
    private Long itemId;
    private Long categoryRetailId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getCategoryRetailId() {
        return categoryRetailId;
    }

    public void setCategoryRetailId(Long categoryRetailId) {
        this.categoryRetailId = categoryRetailId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(itemId);
        dest.writeLong(categoryRetailId);

    }

    public static final Creator<CategoryRetailItem> CREATOR = new Creator<CategoryRetailItem>() {

        @Override
        public CategoryRetailItem[] newArray(int size) {
            // TODO Auto-generated method stub
            return new CategoryRetailItem[size];
        }

        @Override
        public CategoryRetailItem createFromParcel(Parcel source) {
            CategoryRetailItem entity = new CategoryRetailItem();
            entity.id = source.readLong();
            entity.itemId = source.readLong();
            entity.categoryRetailId = source.readLong();

            return entity;
        }
    };
}
