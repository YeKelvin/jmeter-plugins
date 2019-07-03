package pers.kelvin.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.config.Argument;
import org.apache.jmeter.engine.util.ValueReplacer;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.util.JMeterUtils;

import java.util.HashMap;
import java.util.Map;

public class VariablesMap {
    private static final String TEMPORARY_REQUEST_DATA = "VariablesMap.temporaryRequestData";
    private Map<String, String> valuesMap;
    private ValueReplacer replacer;

    public VariablesMap() {
        valuesMap = new HashMap<>();
    }

    public void put(String key, String value) {
        valuesMap.put(key, value);
    }

    public String get(String key) {
        return valuesMap.get(key);
    }

    public boolean containsKey(String key) {
        return valuesMap.containsKey(key);
    }

    /**
     * 利用 StringUtils替换属性值
     *
     * @param source 需替换的源值
     * @return 替换后的真值
     */
    public String replace(String source) {
        if (StringUtils.isBlank(source)) {
            return source;
        }
        return StringUtil.replace(source, valuesMap);
    }

    /**
     * 利用 JMeter ValueReplacer替换属性值和函数值
     *
     * @param source 需替换的源值
     * @return 替换后的真值
     */
    public String replaceByJmeter(String source) {
        if (replacer == null) {
            replacer = new ValueReplacer();
        }
        replacer.setUserDefinedVariables(valuesMap);
        JMeterUtils.getJMeterProperties().forEach((key, value) -> replacer.addVariable((String) key, (String) value));
        Argument argument = new Argument(TEMPORARY_REQUEST_DATA, source);
        try {
            replacer.replaceValues(argument);
            argument.setRunningVersion(true);
            return argument.getPropertyAsString(TEMPORARY_REQUEST_DATA);
        } catch (InvalidVariableException e) {
            return source;
        }
    }
}
