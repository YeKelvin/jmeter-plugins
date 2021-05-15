package org.apache.jmeter.common.jmeter;

import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.engine.util.SimpleValueReplacer;
import org.apache.jmeter.functions.InvalidVariableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Kelvin.Ye
 * @date 2021-05-15 14:08
 */
public class ValueReplaceUtil {

    private static final Logger log = LoggerFactory.getLogger(ValueReplaceUtil.class);

    public static String replace(String value, Map<String, String> variables) {
        try {
            SimpleValueReplacer replacer = new SimpleValueReplacer(variables);
            replacer.setParameters(value);
            return replacer.replace();
        } catch (InvalidVariableException e) {
            log.error(ExceptionUtil.getStackTrace(e));
        }
        return value;
    }
}
