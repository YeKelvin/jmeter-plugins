package org.apache.jmeter.common.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * @author Kelvin.Ye
 */
public class JsonConfigUtil {
    /**
     * 读取本地Json配置文件
     *
     * @return HashMap
     */
    public static HashMap<String, String> get(String configFilePath) throws FileNotFoundException {
        InputStreamReader reader = new InputStreamReader(new FileInputStream(configFilePath), StandardCharsets.UTF_8);
        Type hashMap = new TypeToken<HashMap<String, String>>() {
        }.getType();
        return new Gson().fromJson(reader, hashMap);
    }

}
