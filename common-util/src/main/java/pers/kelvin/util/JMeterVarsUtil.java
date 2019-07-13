package pers.kelvin.util;

import org.apache.jmeter.threads.JMeterContextService;

public class JMeterVarsUtil {

    public static String getDefault(String varName) {
        return getDefault(varName, "");
    }

    public static String getDefault(String varName, String defaultVal) {
        String value = JMeterContextService.getContext().getVariables().get(varName);
        return StringUtil.isNotEmpty(value) ? value : defaultVal;
    }

}
