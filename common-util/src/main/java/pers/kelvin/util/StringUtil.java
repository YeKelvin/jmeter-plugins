package pers.kelvin.util;

/**
 * User: KelvinYe
 * Date: 2018-03-27
 * Time: 10:30
 */
public class StringUtil {
    public static boolean isBlank(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
}
