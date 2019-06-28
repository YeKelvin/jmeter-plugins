package pers.kelvin.util;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * User: KelvinYe
 * Date: 2018-03-27
 * Time: 10:30
 */
public class StringUtil {
    public static boolean isBlank(final String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNotBlank(final String str) {
        return !isBlank(str);
    }

    public static <T> String replace(String source, Map<String, T> valuesMap) {
        if (StringUtils.isNotEmpty(source)) {
            StrSubstitutor substitutor = new StrSubstitutor(valuesMap);
            return substitutor.replace(source);
        }
        return source;
    }
}
