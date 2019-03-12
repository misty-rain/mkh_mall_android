package com.mkh.mobilemall.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.xiniunet.api.domain.master.Member;
import com.xiniunet.api.domain.system.Passport;


public class SharePreferenceUtil {
    public static final String MESSAGE_NOTIFY_KEY = "message_notify";
    public static final String MESSAGE_SOUND_KEY = "message_sound";
    public static final String SHOW_HEAD_KEY = "show_head";
    public static final String PULLREFRESH_SOUND_KEY = "pullrefresh_sound";
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public SharePreferenceUtil(Context context, String file) {
        sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    /**
     * 清除
     */
    public void clearData() {
        sp.edit().clear().commit();
    }


    // 设置初次绘制的解锁图案
    public void setFirstGesturePwd(String firstGesturePwd) {
        editor.putString("firstGesturePwd", firstGesturePwd);
        editor.commit();

    }

    //保存用户名
    public void setUserName(String userName) {
        editor.putString("userName", userName);
        editor.commit();
    }

    public String getUserName() {
        return sp.getString("userName", "");
    }

    public String getFirstGesturePwd() {
        return sp.getString("firstGesturePwd", "");

    }


    //保存密码
    public void setPassword(String passWord) {
        editor.putString("passWord", passWord);
        editor.commit();
    }

    public String getpassWord() {
        return sp.getString("passWord", "");
    }


    // 存储设置绘制的解锁图案,用来判断是否使用解锁方案
    public void setGesturePwd(String GesturePwd) {
        editor.putString("GesturePwd", GesturePwd);
        editor.commit();

    }

    public String getGesturePwd() {
        return sp.getString("GesturePwd", "");

    }


    public String getAppId() {
        return sp.getString("appid", "");
    }

    // 头像图标
    public String getHeadIcon() {
        return sp.getString("headIcon", "");
    }

    public void setHeadIcon(String icon) {
        editor.putString("headIcon", icon);
        editor.commit();
    }

    // 设置Tag
    public void setTag(String tag) {
        editor.putString("tag", tag);
        editor.commit();
    }

    public String getTag() {
        return sp.getString("tag", "");
    }

    // 是否通知
    public boolean getMsgNotify() {
        return sp.getBoolean(MESSAGE_NOTIFY_KEY, true);
    }

    public void setMsgNotify(boolean isChecked) {
        editor.putBoolean(MESSAGE_NOTIFY_KEY, isChecked);
        editor.commit();
    }

    // 新消息是否有声音
    public boolean getMsgSound() {
        return sp.getBoolean(MESSAGE_SOUND_KEY, true);
    }

    public void setMsgSound(boolean isChecked) {
        editor.putBoolean(MESSAGE_SOUND_KEY, isChecked);
        editor.commit();
    }

    // 刷新是否有声音
    public boolean getPullRefreshSound() {
        return sp.getBoolean(PULLREFRESH_SOUND_KEY, true);
    }

    public void setPullRefreshSound(boolean isChecked) {
        editor.putBoolean(PULLREFRESH_SOUND_KEY, isChecked);
        editor.commit();
    }

    // 是否显示自己头像
    public boolean getShowHead() {
        return sp.getBoolean(SHOW_HEAD_KEY, true);
    }

    public void setShowHead(boolean isChecked) {
        editor.putBoolean(SHOW_HEAD_KEY, isChecked);
        editor.commit();
    }

    // 表情翻页效果
    public int getFaceEffect() {
        return sp.getInt("face_effects", 3);
    }

    public void setFaceEffect(int effect) {
        if (effect < 0 || effect > 11)
            effect = 3;
        editor.putInt("face_effects", effect);
        editor.commit();
    }

    /**
     * 设置默认收货地址
     *
     * @param defaultReceiveAddress
     */
    public void setDefaultReceiveAddress(String defaultReceiveAddress) {
        editor.putString("defaultReceiveAddress", defaultReceiveAddress);
        editor.commit();

    }

    /**
     * 获取默认收货地址
     *
     * @return
     */
    public String getdefaultReceiveAddress() {
        return sp.getString("defaultReceiveAddress", "");

    }

    /**
     * 保存用户信息
     *
     * @param
     */
    public void saveUserInfo(Passport passport) {
        editor.putString("passPort", JSON.toJSONString(passport));
        editor.commit();

    }

    /**
     * 获取默认收货地址
     *
     * @return
     */
    public Passport getUserInfo() {
        String json = sp.getString("passPort", "");
        if (!json.equals(""))
            return JSON.parseObject(json, Passport.class);
        return null;


    }


    /**
     * 保存门店Id
     *
     * @param
     */
    public void saveStoreId(Long storeId) {
        editor.putLong("storeId", storeId);
        editor.commit();

    }

    /**
     * 获取门店Id
     *
     * @return
     */
    public Long getStoreId() {
        long storeId = sp.getLong("storeId", 0L);
        return storeId;


    }


    /**
     * 保存门店名称
     *
     * @param
     */
    public void saveStoreName(String storeName) {
        editor.putString("storeName", storeName);
        editor.commit();

    }

    /**
     * 获取门店名称
     *
     * @return
     */
    public String getStoreName() {
        String storeName = sp.getString("storeName", "");
        return storeName;
    }

    /**
     * 保存城市名称
     *
     * @param
     */
    public void saveCityName(String cityName) {
        editor.putString("cityName", cityName);
        editor.commit();

    }

    /**
     * 获取城市名称
     *
     * @return
     */
    public String getCityName() {
        String cityName = sp.getString("cityName", "上海");
        return cityName;
    }

    /**
     * 保存门店客服电话
     *
     * @param
     */
    public void saveStorePhone(String storePhone) {
        editor.putString("storePhone", storePhone);
        editor.commit();

    }

    /**
     * 获取门店客服电话
     *
     * @return
     */
    public String getStorePhone() {
        String storePhone = sp.getString("storePhone", "400-177-0666");
        return storePhone;
    }
}
