package pers.kelvin.util;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * User: KelvinYe
 * Date: 2018-03-27
 * Time: 10:30
 */
public class StringUtil {
    public static boolean isBlank(final CharSequence cs) {
        return StringUtils.isBlank(cs);
    }

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    public static <T> String replace(String source, Map<String, T> valuesMap) {
        if (StringUtils.isNotEmpty(source)) {
            StrSubstitutor substitutor = new StrSubstitutor(valuesMap);
            return substitutor.replace(source);
        }
        return source;
    }

    public static boolean isEmpty(final CharSequence cs) {
        return StringUtils.isEmpty(cs);
    }

    public static boolean isNotEmpty(final CharSequence cs) {
        return StringUtils.isNotEmpty(cs);
    }
}
