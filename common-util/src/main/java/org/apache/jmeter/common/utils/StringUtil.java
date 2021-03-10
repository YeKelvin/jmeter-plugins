package org.apache.jmeter.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Kelvin.Ye
 * @date 2018-03-27 10:30
 */
public class StringUtil {

    private static final Pattern SPACES_AND_LINE_BREAKS_PATTERN = Pattern.compile("\\s*|\t|\r|\n");

    private static final Pattern LINE_BREAKS_PATTERN = Pattern.compile("[\r\n]");

    public static <T> String replace(String source, Map<String, T> valuesMap) {
        if (StringUtils.isNotEmpty(source)) {
            StringSubstitutor substitutor = new StringSubstitutor(valuesMap);
            return substitutor.replace(source);
        }
        return source;
    }

    /**
     * 去除空格和换行符
     */
    public static String removeSpacesAndLineBreaks(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        return SPACES_AND_LINE_BREAKS_PATTERN.matcher(str).replaceAll("");
    }

    /**
     * 去除换行符
     */
    public static String removeLineBreaks(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        return LINE_BREAKS_PATTERN.matcher(str).replaceAll("");
    }

    public static String joinNewline(String... lines) {
        StringBuffer sb = new StringBuffer();
        for (String line : lines) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }
}
