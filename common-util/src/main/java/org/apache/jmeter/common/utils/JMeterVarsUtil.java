package org.apache.jmeter.common.utils;

import org.apache.jmeter.threads.JMeterContextService;

public class JMeterVarsUtil {

    public static String get(String varName) {
        return JMeterContextService.getContext().getVariables().get(varName);
    }

    public static String getDefault(String varName) {
        return getDefault(varName, "");
    }

    public static String getDefault(String varName, String defaultValue) {
        String value = get(varName);
        return StringUtil.isNotBlank(value) ? value : defaultValue;
    }

    public static boolean getDefaultAsBoolean(String varName) {
        String value = get(varName);
        return StringUtil.isNotBlank(value) ? Boolean.valueOf(value) : false;
    }

    public static boolean getDefaultAsBoolean(String varName, boolean defaultValue) {
        String value = get(varName);
        return StringUtil.isNotBlank(value) ? Boolean.valueOf(value) : defaultValue;
    }
}
