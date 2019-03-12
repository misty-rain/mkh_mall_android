package com.mkh.mobilemall.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 更新实体bean
 */
public class UpdateBean implements Parcelable {

    private int versioncode;
    private String versionname;
    private String downloadurl;
    private String updatelog;
    private String appname;
    private String apkName;
    private String operationTime;

    public String getIsForce() {
        return isForce;
    }

    public void setIsForce(String isForce) {
        this.isForce = isForce;
    }

    private String isForce;// 是否必须更新

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public String getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(String operationTime) {
        this.operationTime = operationTime;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public int getVersioncode() {
        return versioncode;
    }

    public void setVersioncode(int versioncode) {
        this.versioncode = versioncode;
    }

    public String getVersionname() {
        return versionname;
    }

    public void setVersionname(String versionname) {
        this.versionname = versionname;
    }

    public String getDownloadurl() {
        return downloadurl;
    }

    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }

    public String getUpdatelog() {
        return updatelog;
    }

    public void setUpdatelog(String updatelog) {
        this.updatelog = updatelog;
    }

    @Override
    public int describeContents() {

        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.versioncode);
        dest.writeString(this.versionname);
        dest.writeString(this.downloadurl);
        dest.writeString(this.updatelog);
        dest.writeString(this.appname);
        dest.writeString(this.apkName);
        dest.writeString(this.operationTime);
        dest.writeString(this.isForce);

    }

    private UpdateBean() {

    }

    private UpdateBean(Parcel in) {
        this.versioncode = in.readInt();
        this.versionname = in.readString();
        this.downloadurl = in.readString();
        this.updatelog = in.readString();
        this.appname = in.readString();
        this.apkName = in.readString();
        this.operationTime = in.readString();
        this.isForce = in.readString();
    }

    public static Creator<UpdateBean> CREATOR = new Creator<UpdateBean>() {
        public UpdateBean createFromParcel(Parcel source) {
            return new UpdateBean(source);
        }


        public UpdateBean[] newArray(int size) {
            return new UpdateBean[size];
        }
    };

}
