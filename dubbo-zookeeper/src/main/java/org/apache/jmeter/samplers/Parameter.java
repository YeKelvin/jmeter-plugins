package org.apache.jmeter.samplers;

import com.google.gson.Gson;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import lombok.Getter;
import pers.kelvin.util.json.JsonUtil;

import java.util.EnumSet;
import java.util.Set;

/**
 * Description: 请求参数相关
 *
 * @author: KelvinYe
 * Date: 2018-05-11
 * Time: 15:31
 */
@Getter
public class Parameter {
    private static Gson gson = new Gson();
    private static Configuration config;

    private String jsonText;

    public Parameter(String json) {
        this.jsonText = json;
    }

    /**
     * 分割json报文，根据入參的类型，将json数据逐一实例化为java对象
     */
    public Object[] getParamsFromJson(Class<?>[] tclazzs) {
        // 获取接口入參对象个数
        Object[] params = new Object[tclazzs.length];
        // 解析json报文
        DocumentContext ctx = jsonParse(JsonUtil.toArrayJson(jsonText));
        // 把json报文转换为java对象
        for (int i = 0; i < tclazzs.length; i++) {
            String jsonPath = "$.[" + i + "]";
            params[i] = ctx.read(jsonPath, tclazzs[i]);
        }
        return params;
    }

    private static Configuration getJsonPathConfig() {
        if (config == null) {
            Configuration.setDefaults(new Configuration.Defaults() {
                private final JsonProvider jsonProvider = new GsonJsonProvider(gson);
                private final MappingProvider mappingProvider = new GsonMappingProvider(gson);

                @Override
                public JsonProvider jsonProvider() {
                    return jsonProvider;
                }

                @Override
                public MappingProvider mappingProvider() {
                    return mappingProvider;
                }

                @Override
                public Set<Option> options() {
                    return EnumSet.noneOf(Option.class);
                }
            });
            config = Configuration.defaultConfiguration();
        }
        return config;
    }

    private static DocumentContext jsonParse(String json) {
        return JsonPath.using(getJsonPathConfig()).parse(json);
    }
}
