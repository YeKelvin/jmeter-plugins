package org.apache.jmeter.common.utils.json;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.jmeter.common.utils.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * Description
 *
 * @author KelvinYe
 * Date     2019-02-14
 * Time     10:16
 */
public class JsonPathUtil {

    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

    private static final Configuration config = Configuration.builder()
            .jsonProvider(new GsonJsonProvider(JsonUtil.getGson()))
            .mappingProvider(new GsonMappingProvider(JsonUtil.getGson()))
            .options(EnumSet.noneOf(Option.class))
            .build();

    /**
     * @param json     json报文
     * @param jsonPath json节点路径
     * @return json值
     */
    @SuppressWarnings("unchecked")
    public static String extractAsString(String json, String jsonPath) {
        String result = "";
        try {
            Object obj = JsonPath.read(json, jsonPath);
            if (obj instanceof Map) {
                return new JSONObject((Map<String, ?>) obj).toJSONString();
            }
            if (obj instanceof JSONArray) {
                return ((JSONArray) obj).toJSONString();
            }
            result = obj == null ? "null" : String.valueOf(obj);
        } catch (Exception e) {
            log.error(ExceptionUtil.getStackTrace(e));
        }
        return result;
    }

    public static int extractAsInt(String json, String jsonPath) {
        int result = 0;
        try {
            Object obj = JsonPath.read(json, jsonPath);
            result = obj == null ? 0 : Integer.parseInt(String.valueOf(obj));
        } catch (Exception e) {
            log.error(ExceptionUtil.getStackTrace(e));
        }
        return result;
    }

    public static float extractAsFloat(String json, String jsonPath) {
        float result = 0;
        try {
            Object obj = JsonPath.read(json, jsonPath);
            result = obj == null ? 0 : Float.parseFloat(obj.toString());
        } catch (Exception e) {
            log.error(ExceptionUtil.getStackTrace(e));
        }
        return result;
    }

    /**
     * 解析json串
     */
    public static DocumentContext jsonParse(String json) {
        return JsonPath.using(config).parse(json);
    }

    /**
     * 获取json报文所有可遍历的JsonPath地址
     */
    public static List<String> getJsonPathList(String json) {
        Configuration conf = Configuration.builder().options(Option.AS_PATH_LIST).build();
        return JsonPath.using(conf).parse(json).read("$..*");
    }

    /**
     * 获取json list的长度
     *
     * @param json     json字符串
     * @param jsonPath e.g.: $..book.length()
     * @return length
     */
    public static int getArrayLength(String json, String jsonPath) {
        try {
            List<Integer> size = JsonPath.read(json, jsonPath);
            return size.get(0);
        } catch (Exception e) {
            log.error(ExceptionUtil.getStackTrace(e));
            return 0;
        }
    }

}
