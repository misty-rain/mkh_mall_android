package com.mkh.mobilemall.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by xiniu_wutao on 15/6/30.
 * 时间操作 工具类
 */
public class DateUtil {
    /**
     * 新版时间展示 聊天页面
     *
     * @param mTimeStamp
     * @return 【备注】注意时间单位是毫秒
     */
    public static String getSessionTime(int mTimeStamp) {
        if (mTimeStamp <= 0) {
            return null;
        }
        String[] weekDays = {
                "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"
        };
        String strDesc = null;
        SimpleDateFormat formatYear = new SimpleDateFormat("yy/MM/dd");
        SimpleDateFormat formatToday = new SimpleDateFormat("HH:mm");
        /**消息时间戳*/
        long changeTime = (long) mTimeStamp;
        long messageTimeStamp = changeTime * 1000;
        /**当前的时间戳*/
        long currentTimeStamp = System.currentTimeMillis();
        /**获取今天的 0 点时间戳*/
        long todayTimeStamp = getTimesmorning();
        /**获取 上一周 0点时间戳*/
        long rangeWeekStamp = todayTimeStamp - 86400000 * 6;

        /**今天的显示 hh:mm   (今天星期三)
         * 昨天
         * 星期一
         * 星期日 、 星期六、 星期五、星期四
         * yy-hh-mm
         * */
        do {
            long diff = currentTimeStamp - messageTimeStamp;
            long diffToday = currentTimeStamp - todayTimeStamp;
            /**今天之内的*/
            if (diff < diffToday) {
                strDesc = formatToday.format(messageTimeStamp);
                break;
            }

            long diffWeek = currentTimeStamp - rangeWeekStamp;
            /**最近一周的判断*/
            if (diff < diffWeek) {
                /**昨天零点的时间*/
                long yesterday = todayTimeStamp - 86400000;
                long diffYesterday = currentTimeStamp - yesterday;
                if (diff < diffYesterday) {
                    strDesc = "昨天";
                } else {
                    Calendar weekCal = Calendar.getInstance();
                    weekCal.setTimeInMillis(messageTimeStamp);
                    int w = weekCal.get(Calendar.DAY_OF_WEEK) - 1;
                    w = w < 0 ? 0 : w;
                    strDesc = weekDays[w];
                }
                break;
            }
            /**年月日显示*/
            strDesc = formatYear.format(messageTimeStamp);
        } while (false);
        return strDesc;
    }

    /**
     * 获取当天 零点的时间戳【linux】
     *
     * @return
     */
    public static long getTimesmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static boolean needDisplayTime(int predateTime, int curdateTime) {
        long timediff = (curdateTime - predateTime);
        return (timediff >= 5 * 60);
    }

    public static String getTimeDiffDesc(Date date) {

        if (date == null) {
            return null;
        }

        String strDesc = null;
        Calendar curCalendar = Calendar.getInstance();
        Date curDate = new Date();
        curCalendar.setTime(curDate);
        Calendar thenCalendar = Calendar.getInstance();
        thenCalendar.setTime(date);

        String[] weekDays = {
                "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"
        };
        int w = thenCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        // SimpleDateFormat format = new
        // SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar current = Calendar.getInstance();
        Calendar today = Calendar.getInstance(); // 今天
        today.set(Calendar.YEAR, current.get(Calendar.YEAR));
        today.set(Calendar.MONTH, current.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        // Date datetoday = today.getTime();
        // System.out.println(format.format(datetoday));

        Calendar yesterday = Calendar.getInstance(); // 昨天
        yesterday.setTime(curDate);
        yesterday.add(Calendar.DATE, -1);
        yesterday.set(Calendar.HOUR_OF_DAY, 0);
        yesterday.set(Calendar.MINUTE, 0);
        yesterday.set(Calendar.SECOND, 0);
        // Date dateyestoday = yesterday.getTime();
        // System.out.println(format.format(dateyestoday));

        Calendar sevendaysago = Calendar.getInstance(); // 7天
        sevendaysago.setTime(curDate);
        sevendaysago.add(Calendar.DATE, -7);
        sevendaysago.set(Calendar.HOUR_OF_DAY, 0);
        sevendaysago.set(Calendar.MINUTE, 0);
        sevendaysago.set(Calendar.SECOND, 0);
        // Date datesevenago = sevendaysago.getTime();
        // System.out.println(format.format(datesevenago));

        /*
         * Date tasktime = yesterday.getTime(); SimpleDateFormat df=new
         * SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         * System.out.println(df.format(tasktime));
         */

        int thenMonth = thenCalendar.get(Calendar.MONTH);
        int thenDay = thenCalendar.get(Calendar.DAY_OF_MONTH);
        int h = thenCalendar.get(Calendar.HOUR_OF_DAY);
        int m = thenCalendar.get(Calendar.MINUTE);
        String sh = "", sm = "";
        if (h < 10)
            sh = "0";

        if (m < 10)
            sm = "0";
        if (thenCalendar.after(today))// today
        {
            if (h < 6) {
                strDesc = "凌晨 " + sh + h + ":" + sm + m;
            } else if (h < 12) {
                strDesc = "上午 " + sh + h + ":" + sm + m;
            } else if (h < 13) {
                strDesc = "下午 " + h + ":" + sm + m;
            } else if (h < 19) {
                strDesc = "下午 " + (h - 12) + ":" + sm + m;
            } else {
                strDesc = "晚上 " + (h - 12) + ":" + sm + m;
            }
        } else if (thenCalendar.before(today) && thenCalendar.after(yesterday)) {// yestoday
            // System.out.println("yestoday");
            if (h < 6) {
                strDesc = "昨天凌晨 " + sh + h + ":" + sm + m;
            } else if (h < 12) {
                strDesc = "昨天上午 " + sh + h + ":" + sm + m;
            } else if (h < 13) {
                strDesc = "昨天下午 " + h + ":" + sm + m;
            } else if (h < 19) {
                strDesc = "昨天下午 " + (h - 12) + ":" + sm + m;
            } else {
                strDesc = "昨天晚上 " + (h - 12) + ":" + sm + m;
            }
        } else if (thenCalendar.before(yesterday)
                && thenCalendar.after(sevendaysago)) {// 2 ~ 7days ago
            // System.out.println("2~7");
            if (h < 6) {
                strDesc = weekDays[w] + "凌晨 " + sh + h + ":" + sm + m;
            } else if (h < 12) {
                strDesc = weekDays[w] + "上午 " + sh + h + ":" + sm + m;
            } else if (h < 13) {
                strDesc = weekDays[w] + "下午 " + h + ":" + sm + m;
            } else if (h < 19) {
                strDesc = weekDays[w] + "下午 " + (h - 12) + ":" + sm + m;
            } else {
                strDesc = weekDays[w] + "晚上 " + (h - 12) + ":" + sm + m;
            }
        } else {
            // System.out.println("7~");
            if (h < 6) {
                strDesc = (thenMonth + 1) + "月" + thenDay + "日" + "凌晨 " + sh
                        + h + ":" + sm + m;
            } else if (h < 12) {
                strDesc = (thenMonth + 1) + "月" + thenDay + "日" + "上午 " + sh
                        + h + ":" + sm + m;
            } else if (h < 13) {
                strDesc = (thenMonth + 1) + "月" + thenDay + "日" + "下午 " + h
                        + ":" + sm + m;
            } else if (h < 19) {
                strDesc = (thenMonth + 1) + "月" + thenDay + "日" + "下午 "
                        + (h - 12) + ":" + sm + m;
            } else {
                strDesc = (thenMonth + 1) + "月" + thenDay + "日" + "晚上 "
                        + (h - 12) + ":" + sm + m;
            }
        }
        // System.out.println(strDesc);
        return strDesc;
    }

    /**
     * 年月日时分秒(无下划线) yyyyMMddHHmmss
     */
    public static final String dtLong = "HHmmss";

    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    /**
     * 获取的时间格式为 yyyy-MM-dd HH:mm
     *
     * @return
     */

    public static String getCurrentTimeForDateId() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        return format.format(cal.getTime());

    }

    public static String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        return format.format(cal.getTime());

    }

    public static String getTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
        return format.format(new Date(time));
    }

    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }

    public static String getChatTime(long timesamp) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        Date today = new Date(System.currentTimeMillis());
        Date otherDay = new Date(timesamp);
        int temp = Integer.parseInt(sdf.format(today))
                - Integer.parseInt(sdf.format(otherDay));

        switch (temp) {
            case 0:
                result = "今天 " + getHourAndMin(timesamp);
                break;
            case 1:
                result = "昨天 " + getHourAndMin(timesamp);
                break;
            case 2:
                result = "前天 " + getHourAndMin(timesamp);
                break;

            default:
                // result = temp + "天前 ";
                result = getTime(timesamp);
                break;
        }

        return result;
    }

    /**
     * 将字符串转位日期类型
     *
     * @param sdate
     * @return
     */
    public static Date toDate(String sdate) {
        try {

            return dateFormater2.get().parse(sdate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将date 转为string
     * @param date
     * @return
     */
    public static String dateToString(Date date){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        return df.format(date);

    }


    /**
     * 返回系统当前时间(精确到毫秒),作为一个唯一的订单编号
     *
     * @return 以yyyyMMddHHmmss为格式的当前系统时间
     */
    public static String getOrderNumTime() {

        Date date = new Date();
        DateFormat df = new SimpleDateFormat(dtLong);
        return df.format(date);
    }

    /**
     * 产生随机的三位数
     *
     * @return
     */
    public static String getThreeRandoms() {
        Random rad = new Random();
        return rad.nextInt(1000) + "";
    }

    /**
     * 产生订单编号
     *
     * @return
     */
    public static String getOrderNo() {
        return getOrderNumTime() + getThreeRandoms();
    }

}
