package org.apache.jmeter.common.utils;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * User: KelvinYe
 * Date: 2018-03-27
 * Time: 10:30
 */
public class StringUtil {

    private static Pattern spacesAndLineBreaksPattern = Pattern.compile("\\s*|\t|\r|\n");

    private static Pattern lineBreaksPattern = Pattern.compile("[\r\n]");

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


    /**
     * 去除空格和换行符
     */
    public static String removeSpacesAndLineBreaks(String str) {
        if (isBlank(str)) {
            return str;
        }
        return spacesAndLineBreaksPattern.matcher(str).replaceAll("");
    }

    /**
     * 去除换行符
     */
    public static String removeLineBreaks(String str) {
        if (isBlank(str)) {
            return str;
        }
        return lineBreaksPattern.matcher(str).replaceAll("");
    }

    public static String joinNewline(String... lines) {
        StringBuffer sb = new StringBuffer();
        for (String line : lines) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }
}
