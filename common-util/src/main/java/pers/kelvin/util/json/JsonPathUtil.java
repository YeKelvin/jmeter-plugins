package pers.kelvin.util.json;

import com.google.gson.Gson;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Description
 *
 * @author KelvinYe
 * Date     2019-02-14
 * Time     10:16
 */
public class JsonPathUtil {

    private static Gson gson = new Gson();

    private static Configuration config;

    /**
     * 获取 JsonPath配置对象
     */
    public static Configuration getJsonPathConfigWithGson() {
        if (config == null) {
            config = Configuration.builder()
                    .jsonProvider(new GsonJsonProvider(gson))
                    .mappingProvider(new GsonMappingProvider(gson))
                    .options(EnumSet.noneOf(Option.class))
                    .build();
        }
        return config;
    }

    /**
     * 解析json串
     */
    public static DocumentContext jsonParse(String json) {
        return JsonPath.using(getJsonPathConfigWithGson()).parse(json);
    }

    /**
     * 获取json报文所有可遍历的JsonPath地址
     */
    public static List<String> getJsonPathList(String json) {
        Configuration conf = Configuration.builder().options(Option.AS_PATH_LIST).build();
        return JsonPath.using(conf).parse(json).read("$..*");
    }

    public static void main(String[] args) {
        String json = "[{\"customerType\":\"ORG\",\"loginName\":\"${mobile}\"}]";
        List<String> jsonPathList = getJsonPathList(json);
        for (String jsonPath : jsonPathList) {
            System.out.println(jsonPath);
        }
    }
}
