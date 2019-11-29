package com.qjj.cn.myredraindemo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 * Created by bob on 2018/12/28.
 */
public class DateUtils {

    /**
     * 得到当前时间
     *
     * @param dateFormat 时间格式
     * @return 转换后的时间格式
     */
    public static String getStringToday(String dateFormat) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 将字符串型日期转换成日期
     *
     * @param dateStr    字符串型日期
     * @param dateFormat 日期格式
     * @return
     */
    public static Date stringToDate(String dateStr, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 日期转字符串
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static String dateToString(Date date, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(date);
    }

    /**
     *
     * @param currentTime currentTime要转换的long类型的时间
     * @param formatType  要转换的string类型的时间格式
     * @return   yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
     * @throws ParseException
     */
    public static String longToString(long currentTime, String formatType)
            throws ParseException {
        Date date = longToDate(currentTime, formatType); // long类型转成Date类型
        String strTime = dateToString(date, formatType); // date类型转成String
        return strTime;
    }

    /**
     *
     * @param currentTime currentTime要转换的long类型的时间
     * @param formatType  要转换的string类型的时间格式
     * @return  // formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
     * @throws ParseException
     */
    public static Date longToDate(long currentTime, String formatType)
            throws ParseException {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
    }

    /**
     * 两个时间点的间隔时长（分钟）
     *
     * @param before 开始时间
     * @param after  结束时间
     * @return 两个时间点的间隔时长（分钟）
     */
    public static long compareMin(Date before, Date after) {
        if (before == null || after == null) {
            return 0l;
        }
        long dif = 0;
        if (after.getTime() >= before.getTime()) {
            dif = after.getTime() - before.getTime();
        } else if (after.getTime() < before.getTime()) {
            dif = after.getTime() + 86400000 - before.getTime();
        }
        dif = Math.abs(dif);
        return dif / 60000;
    }

    /**
     * 获取指定时间间隔分钟后的时间
     *
     * @param date 指定的时间
     * @param min  间隔分钟数
     * @return 间隔分钟数后的时间
     */
    public static Date addMinutes(Date date, int min) {
        if (date == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, min);
        return calendar.getTime();
    }

    /**
     * 根据时间返回指定术语，自娱自乐，可自行调整
     *
     * @param hourday 小时
     * @return
     */
    public static String showTimeView(int hourday) {
        if (hourday >= 22 && hourday <= 24) {
            return "晚上";
        } else if (hourday >= 0 && hourday <= 6) {
            return "凌晨";
        } else if (hourday > 6 && hourday <= 12) {
            return "上午";
        } else if (hourday > 12 && hourday < 22) {
            return "下午";
        }
        return null;
    }

    //毫秒换成00:00:00
    public static String getCountTimeByLong(long finishTime) {
        int totalTime = (int) (finishTime / 1000);//秒
        int hour = 0, minute = 0, second = 0, ms = 0;

        if (3600 <= totalTime) {
            hour = totalTime / 3600;
            totalTime = totalTime - 3600 * hour;
        }
        if (60 <= totalTime) {
            minute = totalTime / 60;
            totalTime = totalTime - 60 * minute;
        }
        if (0 <= totalTime) {
            second = totalTime;
        }
        ms = (int) (finishTime % 1000);


       return String.format("%02d:%02d:%02d:%03d", hour, minute, second,ms);

//        StringBuilder sb = new StringBuilder();
//
//        if (hour < 10) {
//            sb.append("0").append(hour).append(":");
//        } else {
//            sb.append(hour).append(":");
//        }
//        if (minute < 10) {
//            sb.append("0").append(minute).append(":");
//        } else {
//            sb.append(minute).append(":");
//        }
//        if (second < 10) {
//            sb.append("0").append(second);
//        } else {
//            sb.append(second);
//        }
//
//
//        return sb.toString();

    }


}
