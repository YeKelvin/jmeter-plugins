package org.apache.jmeter.common.utils;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author KelvinYe
 */
public class TimeUtil {

    /**
     * Date转String
     */
    public static String dateToStrtime(Date data, String dateFormatPattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatPattern);
        return sdf.format(data);
    }

    /**
     * long时间戳转String
     */
    public static String timestampToStrtime(long timeStamp, String dateFormatPattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatPattern);
        return sdf.format(timeStamp);
    }

    public static long strtimeToTimestamp(String time, String dateFormatPattern) {
        try {
            Date date = DateUtils.parseDateStrictly(time, dateFormatPattern);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long strtimeToTimestamp(String time, SimpleDateFormat dateFormat) {
        try {
            Date date = dateFormat.parse(time);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取当前时间
     */
    public static String currentTimeAsString() {
        return currentTimeAsString("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 获取当前时间并格式化
     */
    public static String currentTimeAsString(String dateFormatPattern) {
        return new SimpleDateFormat(dateFormatPattern).format(new Date());
    }

    /**
     * 获取当前日期，并根据 pattern格式化时间
     */
    public static String currentDate(String pattern) {
        return new SimpleDateFormat(pattern).format(new Date());
    }

    public static String currentDate(String pattern, int number) {
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, number);
        Date newTime = calendar.getTime();
        return new SimpleDateFormat(pattern).format(newTime);
    }

    /**
     * 获取当前日期
     */
    public static String currentDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    /**
     * 获取当前日期，可加减日期
     *
     * @param number 加减日期的天数
     */
    public static String currentDate(int number) {
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, number);
        Date newTime = calendar.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(newTime);
    }

    /**
     * 获取当前时间戳
     */
    public static long currentTimestamp() {
        return System.currentTimeMillis();
    }

    public static String currentTimestampAsString() {
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * 计算时间差（毫秒）
     *
     * @param startTime 开始时间戳
     * @return 耗时ms.
     */
    public static String elapsedTimeAsString(long startTime) {
        return String.valueOf(elapsedTime(startTime));
    }

    /**
     * 获取 时间差（long型）
     */
    public static long elapsedTime(long startTime) {
        return System.currentTimeMillis() - startTime;
    }


    /**
     * 格式化时间差（00h:00m:00s + 00ms）
     *
     * @param elapsedTime 毫秒级耗时
     */
    public static String formatElapsedTimeAsHMSMs(long elapsedTime) {
        long hour = elapsedTime / (60 * 60 * 1000);
        long min = (elapsedTime / (60 * 1000)) - (hour * 60);
        long s = (elapsedTime / 1000) - (hour * 60 * 60) - (min * 60);
        long ms = elapsedTime - (hour * 60 * 60 * 1000) - (min * 60 * 1000) - (s * 1000);
        return String.format("%02dh:%02dm:%02ds + %dms", hour, min, s, ms);
    }

    /**
     * 格式化毫秒时长（00h:00m:00s）
     *
     * @param elapsedTime 毫秒级耗时
     */
    public static String formatElapsedTimeAsHMS(long elapsedTime) {
        long hour = elapsedTime / (60 * 60 * 1000);
        long min = (elapsedTime / (60 * 1000)) - (hour * 60);
        long s = (elapsedTime / 1000) - (hour * 60 * 60) - (min * 60);
        return String.format("%02dh:%02dm:%02ds", hour, min, s);
    }

    /**
     * 格式化毫秒时长（00m:00s）
     *
     * @param elapsedTime 毫秒级耗时
     */
    public static String formatElapsedTimeAsMS(long elapsedTime) {
        long hour = elapsedTime / (60 * 60 * 1000);
        long min = (elapsedTime / (60 * 1000)) - (hour * 60);
        long s = (elapsedTime / 1000) - (hour * 60 * 60) - (min * 60);
        return String.format("%02dm:%02ds", min, s);
    }

    /**
     * 格式化时间差（00h:00m:00s）
     *
     * @param startTime         开始时间
     * @param endTime           结束时间
     * @param dateFormatPattern 时间格式化规则
     */
    public static String formatElapsedTimeAsHMS(String startTime, String endTime, String dateFormatPattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatPattern);
        long startTimestamp = strtimeToTimestamp(startTime, sdf);
        long endTimestamp = strtimeToTimestamp(endTime, sdf);
        long elapsedTime = endTimestamp - startTimestamp;
        return formatElapsedTimeAsHMS(elapsedTime);
    }

    /**
     * 格式化时间差（00m:00s）
     *
     * @param startTime         开始时间
     * @param endTime           结束时间
     * @param dateFormatPattern 时间格式化规则
     */
    public static String formatElapsedTimeAsMS(String startTime, String endTime, String dateFormatPattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatPattern);
        long startTimestamp = strtimeToTimestamp(startTime, sdf);
        long endTimestamp = strtimeToTimestamp(endTime, sdf);
        long elapsedTime = endTimestamp - startTimestamp;
        return formatElapsedTimeAsMS(elapsedTime);
    }

    /**
     * String型耗时转换为 long型的毫秒级耗时
     *
     * @param elapsedTime String型耗时（00h:00m:00s + 00ms）
     */
    public static long hmsMsElapsedTimeToLong(String elapsedTime) {
        elapsedTime = elapsedTime.replace(" ", "");
        String[] timsArray = elapsedTime.split("\\+");
        String[] hms = timsArray[0].split(":");
        long h = Long.parseLong(hms[0].substring(0, 2)) * 60 * 60 * 1000;
        long m = Long.parseLong(hms[1].substring(0, 2)) * 60 * 1000;
        long s = Long.parseLong(hms[2].substring(0, 2)) * 1000;
        long ms = Long.parseLong(timsArray[1].substring(0, timsArray[1].length() - 2));
        return h + m + s + ms;
    }

    /**
     * String型耗时转换为 long型的毫秒级耗时
     *
     * @param elapsedTime String型耗时（00h:00m:00s）
     */
    public static long hmsElapsedTimeToLong(String elapsedTime) {
        String[] hms = elapsedTime.split(":");
        long h = Long.parseLong(hms[0].substring(0, 2)) * 60 * 60 * 1000;
        long m = Long.parseLong(hms[1].substring(0, 2)) * 60 * 1000;
        long s = Long.parseLong(hms[2].substring(0, 2)) * 1000;
        return h + m + s;
    }

    /**
     * String型耗时转换为 long型的毫秒级耗时
     *
     * @param elapsedTime String型耗时（00m:00s）
     */
    public static long msElapsedTimeToLong(String elapsedTime) {
        String[] hms = elapsedTime.split(":");
        long m = Long.parseLong(hms[0].substring(0, 2)) * 60 * 1000;
        long s = Long.parseLong(hms[1].substring(0, 2)) * 1000;
        return m + s;
    }
}
