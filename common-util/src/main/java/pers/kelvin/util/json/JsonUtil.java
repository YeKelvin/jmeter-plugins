package pers.kelvin.util.json;

import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;

import java.util.regex.Pattern;

/**
 * @author KelvinYe
 */
public class JsonUtil {
    private static Gson gson = new Gson();

    /**
     * 获取Gson实例
     *
     * @return gson对象
     */
    public static Gson getGsonInstance() {
        return gson;
    }

    /**
     * @param json     json报文
     * @param jsonPath json节点路径
     * @return json值
     */
    public static String readAsString(String json, String jsonPath) {
        Object obj = JsonPath.read(json, jsonPath);
        return obj == null ? null : obj.toString();
    }

    /**
     * 在原json最外层加 {} ，转换为object json
     */
    public static String toObjectJson(String json) {
        // 判断原json是否为Object
        if (isObjectJson(json)) {
            return json;
        }
        return "{" + json + "}";
    }

    /**
     * 在原json最外层加 [] ，转换为array json
     */
    public static String toArrayJson(String json) {
        // 判断原json是否为Object
        if (isArrayJson(json)) {
            return json;
        }
        return "[" + json + "]";
    }

    /**
     * 判断json是否为Object
     */
    public static Boolean isObjectJson(String json) {
        return Pattern.matches("^[{].*[}]$", json);
    }

    /**
     * 判断json是否为Array
     */
    public static Boolean isArrayJson(String json) {
        return Pattern.matches("^[\\[].*[]]$", json);
    }

    /**
     * 根据报文和类型转换为类对象
     *
     * @param json   json报文
     * @param tClass 类
     * @param <T>    泛型
     * @return 类对象
     */
    public static <T> T fromJson(String json, Class<T> tClass) {
        return gson.fromJson(json, tClass);
    }

    /**
     * 根据对象转换为json报文
     *
     * @param obj 对象
     * @return json报文
     */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }
}
