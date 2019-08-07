package pers.kelvin.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author KelvinYe
 */
public class TimeUtil {

    /**
     * Date转String
     * todo：DateUtils.parseDateStrictly("20171012 14:30:12", Locale.TRADITIONAL_CHINESE, "yyyyMMdd hh:mm:ss")
     */
    public static String dateToString(Date data, String dateFormatPattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatPattern);
        return sdf.format(data);
    }

    /**
     * long时间戳转String
     */
    public static String timeStampToString(long timeStamp, String dateFormatPattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatPattern);
        return sdf.format(timeStamp);
    }

    public static long stringToTimestamp(String time, String dateFormatPattern) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatPattern);
            Date date = simpleDateFormat.parse(time);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long stringToTimestamp(String time, SimpleDateFormat dateFormat) {
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
    public static String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    /**
     * 获取当前日期
     */
    public static String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    /**
     * 获取当前时间
     */
    public static Date currentTime() {
        return new Date();
    }

    /**
     * 获取当前时间并格式化
     */
    public static String currentTimeAsString(String dateFormatPattern) {
        return new SimpleDateFormat(dateFormatPattern).format(new Date());
    }

    /**
     * 获取当前时间戳
     */
    public static long currentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 计算时间差（毫秒）
     *
     * @param startTime 开始时间戳
     * @return 耗时ms.
     */
    public static String elapsedTimeAsMs(long startTime) {
        return elapsedTime(startTime) + "ms";
    }

    /**
     * 获取 时间差（long型）
     */
    public static long elapsedTime(long startTime) {
        return System.currentTimeMillis() - startTime;
    }


    /**
     * 格式化时间差（00h:00m:00s + 00ms）
     */
    public static String formatElapsedTimeAsHMSMs(long elapsedTime) {
        long hour = elapsedTime / (60 * 60 * 1000);
        long min = (elapsedTime / (60 * 1000)) - (hour * 60);
        long s = (elapsedTime / 1000) - (hour * 60 * 60) - (min * 60);
        long ms = elapsedTime - (hour * 60 * 60 * 1000) - (min * 60 * 1000) - (s * 1000);
        return String.format("%02dh:%02dm:%02ds + %dms", hour, min, s, ms);
    }

    /**
     * 格式化时间差（00h:00m:00s）
     */
    public static String formatElapsedTimeAsHMS(long elapsedTime) {
        long hour = elapsedTime / (60 * 60 * 1000);
        long min = (elapsedTime / (60 * 1000)) - (hour * 60);
        long s = (elapsedTime / 1000) - (hour * 60 * 60) - (min * 60);
        return String.format("%02dh:%02dm:%02ds", hour, min, s);
    }

    public static String formatElapsedTimeAsHMS(String startTime, String endTime, String dateFormatPattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatPattern);
        long startTimestamp = stringToTimestamp(startTime, sdf);
        long endTimestamp = stringToTimestamp(endTime, sdf);
        long elapsedTime = endTimestamp - startTimestamp;
        return formatElapsedTimeAsHMS(elapsedTime);
    }

    public static long formatedTimeToMs(String time) {
        time = time.replace(" ", "");
        String[] timsArray = time.split("\\+");
        String[] hms = timsArray[0].split(":");
        long h = Long.valueOf(hms[0].substring(0, 2)) * 60 * 60 * 1000;
        long m = Long.valueOf(hms[1].substring(0, 2)) * 60 * 1000;
        long s = Long.valueOf(hms[2].substring(0, 2)) * 1000;
        long ms = 0;
        if (timsArray.length == 2) {
            ms = Long.valueOf(timsArray[1].substring(0, timsArray[1].length() - 1));
        }
        return h + m + s + ms;
    }

    public static void main(String[] args) {
        System.out.println(timeStampToString(currentTimestamp(), "yyyy.MM.dd HH:mm:ss"));
    }


}
