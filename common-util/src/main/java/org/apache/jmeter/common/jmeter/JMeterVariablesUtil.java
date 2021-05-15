package org.apache.jmeter.common.jmeter;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.threads.JMeterContextService;

/**
 * @author Kaiwen.Ye
 */
public class JMeterVariablesUtil {

    public static String get(String varName) {
        return JMeterContextService.getContext().getVariables().get(varName);
    }

    public static String getDefault(String varName) {
        return getDefault(varName, "");
    }

    public static String getDefault(String varName, String defaultValue) {
        String value = get(varName);
        return StringUtils.isNotBlank(value) ? value : defaultValue;
    }

    public static boolean getDefaultAsBoolean(String varName) {
        String value = get(varName);
        return StringUtils.isNotBlank(value) && Boolean.parseBoolean(value);
    }

    public static boolean getDefaultAsBoolean(String varName, boolean defaultValue) {
        String value = get(varName);
        return StringUtils.isNotBlank(value) ? Boolean.parseBoolean(value) : defaultValue;
    }
}
