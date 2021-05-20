package org.apache.jmeter.common.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.common.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Kelvin.Ye
 */
public class JsonUtil {

    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

    public static Type mapType = new TypeToken<Map<Object, Object>>() {
    }.getType();

    private static final Gson gson = newGson();
    private static final Gson prettyGson = newPrettyGson();

    private static Gson newGson() {
        return new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(mapType, new MapTypeAdapter())
                .registerTypeAdapter(Double.class, new DoubleTypeAdapter())
                .create();
    }

    private static Gson newPrettyGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(mapType, new MapTypeAdapter())
                .registerTypeAdapter(Double.class, new DoubleTypeAdapter())
                .create();
    }

    private static final String FORMAT_INDENT = "    ";

    /**
     * 获取Gson实例
     *
     * @return gson对象
     */
    public static Gson getGson() {
        return gson;
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
     * @param <T>    类型
     * @return 类对象
     */
    public static <T> T fromJson(String json, Class<T> tClass) {
        return gson.fromJson(json, tClass);
    }

    /**
     * 根据报文和类型转换为类对象
     *
     * @param json json报文
     * @param type 类型
     * @param <T>  类型
     * @return 类对象
     */
    public static <T> T fromJson(String json, Type type) {
        return gson.fromJson(json, type);
    }

    public static <T> T fromJson(File file, Type type) {
        try (
                FileInputStream input = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8.name())
        ) {
            return gson.fromJson(reader, type);
        } catch (Exception e) {
            log.error(ExceptionUtil.getStackTrace(e));
            return null;
        }
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

    public static String prettyJson(String json) {
        return prettyGson.toJson(JsonParser.parseString(json).getAsJsonObject());
    }

    /**
     * 格式化输出 json（忽略占位符）
     */
    public static String prettyJsonIgnorePlaceholder(String json) {
        //缩进
        StringBuffer indent = new StringBuffer();
        StringBuffer sb = new StringBuffer();

        // 上一个字符
        char previous = '\u0000';
        // 不允许换行标识
        boolean notAllowLineBreaks = false;
        // 是否在双引号内
        boolean isInsideQuotes = false;

        for (char c : json.toCharArray()) {
            switch (c) {
                case '{':
                    if (previous == '$') {
                        sb.append("{");
                        notAllowLineBreaks = true;
                        break;
                    }
                    indent.append(FORMAT_INDENT);
                    sb.append("{\n").append(indent);
                    break;
                case '}':
                    if (notAllowLineBreaks) {
                        sb.append("}");
                        notAllowLineBreaks = false;
                        break;
                    }
                    if (isInsideQuotes) {
                        sb.append("}");
                        break;
                    }
                    indent.delete(indent.length() - FORMAT_INDENT.length(), indent.length());
                    sb.append("\n").append(indent).append("}");
                    break;
                case '[':
                    if (isInsideQuotes) {
                        sb.append("[");
                        break;
                    }
                    indent.append(FORMAT_INDENT);
                    sb.append("[\n").append(indent);
                    break;
                case ']':
                    if (isInsideQuotes) {
                        sb.append("]");
                        break;
                    }
                    indent.delete(indent.length() - FORMAT_INDENT.length(), indent.length());
                    sb.append("\n").append(indent).append("]");
                    break;
                case ',':
                    if (isInsideQuotes) {
                        sb.append(",");
                        break;
                    }
                    sb.append(",\n").append(indent);
                    break;
                case '\"':
                    // 非转义符引号才标记
                    if (previous != '\\') {
                        isInsideQuotes = !isInsideQuotes;
                    }
                    sb.append(c);
                    break;
                default:
                    sb.append(c);
            }
            previous = c;
        }
        return sb.toString();
    }

    /**
     * 去除json中的空格和换行符
     */
    public static String removeSpacesAndLineBreaks(String json) {
        StringBuffer sb = new StringBuffer();
        // 上一个字符
        char previous = '\u0000';
        // 是否在双引号内
        boolean isInsideQuotes = false;
        for (char c : json.toCharArray()) {
            switch (c) {
                case ' ':
                    if (isInsideQuotes) {
                        sb.append(c);
                    }
                    break;
                case '\"':
                    // 非转义符双引号才标记
                    if (previous != '\\') {
                        isInsideQuotes = !isInsideQuotes;
                    }
                    sb.append(c);
                    break;
                default:
                    sb.append(c);
            }
            previous = c;
        }
        return StringUtil.removeLineBreaks(sb.toString());
    }

    public static void main(String[] args) {
        System.out.println(prettyJson("aaa"));
    }

}
