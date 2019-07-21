package pers.kelvin.util.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author KelvinYe
 */
public class JsonUtil {

    public static Type mapType = new TypeToken<Map<Object, Object>>() {
    }.getType();

    private static Gson gson = getGson();

    private static Gson getGson() {
        return new GsonBuilder().serializeNulls()
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
    public static Gson getGsonInstance() {
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

    /**
     * 根据对象转换为json报文
     *
     * @param obj 对象
     * @return json报文
     */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * 格式化输出 json
     *
     * @param json jsonStr
     * @return str
     */
    public static String prettyJsonWithPlaceholder(String json) {
        StringBuilder indent = new StringBuilder();//缩进
        StringBuilder sb = new StringBuilder();

        char previous = '\u0000';
        boolean notAllowLineBreaks = false;
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
                    indent.delete(indent.length() - FORMAT_INDENT.length(), indent.length());
                    sb.append("\n").append(indent).append("}");
                    break;
                case '[':
                    indent.append(FORMAT_INDENT);
                    sb.append("[\n").append(indent);
                    break;
                case ']':
                    indent.delete(indent.length() - FORMAT_INDENT.length(), indent.length());
                    sb.append("\n").append(indent).append("]");
                    break;
                case ',':
                    sb.append(",\n").append(indent);
                    break;
                default:
                    sb.append(c);
            }
            previous = c;
        }
        return sb.toString();
    }

    public static String jsonFormat(String json) {
        StringBuilder indent = new StringBuilder();//缩进
        StringBuilder sb = new StringBuilder();

        for (char c : json.toCharArray()) {
            switch (c) {
                case '{':
                    indent.append(" ");
                    sb.append("{\n").append(indent);
                    break;
                case '}':
                    indent.deleteCharAt(indent.length() - 1);
                    sb.append("\n").append(indent).append("}");
                    break;
                case '[':
                    indent.append(" ");
                    sb.append("[\n").append(indent);
                    break;
                case ']':
                    indent.deleteCharAt(indent.length() - 1);
                    sb.append("\n").append(indent).append("]");
                    break;
                case ',':
                    sb.append(",\n").append(indent);
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

}
