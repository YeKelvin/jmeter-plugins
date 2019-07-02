package pers.kelvin.util;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class ValuesReplacer {
    private Map<String, String> valuesMap;

    public ValuesReplacer() {
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

    public String replace(String source) {
        if (StringUtils.isBlank(source)) {
            return source;
        }
        return StringUtil.replace(source, valuesMap);
    }
}
