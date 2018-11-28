package pers.kelvin.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author KelvinYe
 */
public class TimeUtil {

    /**
     * Date转String
     */
    public static String dateToString(Date data, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(data);
    }

    /**
     * long转String
     */
    public static String longToString(long time, String dateFormat) {
        Date date = longToDate(time);
        return dateToString(date, dateFormat);
    }

    /**
     * long转Date
     */
    public static Date longToDate(long time) {
        return new Date(time);
    }

}
