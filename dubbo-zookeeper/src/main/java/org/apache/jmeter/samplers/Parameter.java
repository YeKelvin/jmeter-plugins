package org.apache.jmeter.samplers;

import com.jayway.jsonpath.DocumentContext;
import lombok.Getter;
import pers.kelvin.util.json.JsonPathUtil;
import pers.kelvin.util.json.JsonUtil;

/**
 * Description: 请求参数相关
 *
 * @author: KelvinYe
 * Date: 2018-05-11
 * Time: 15:31
 */
@Getter
public class Parameter {

    private String jsonText;

    Parameter(String json) {
        this.jsonText = json;
    }

    /**
     * 分割json报文，根据入參的类型，将json数据逐一实例化为java对象
     */
    public Object[] getParamsFromJson(Class<?>[] tclazzs) {
        // 获取接口入參对象个数
        Object[] params = new Object[tclazzs.length];
        // 解析json报文
        DocumentContext ctx = JsonPathUtil.jsonParse(JsonUtil.toArrayJson(jsonText));
        // 把json报文转换为java对象
        for (int i = 0; i < tclazzs.length; i++) {
            String jsonPath = "$.[" + i + "]";
            params[i] = ctx.read(jsonPath, tclazzs[i]);
        }
        return params;
    }

}
