package pers.kelvin.util.http;

import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import pers.kelvin.util.json.JsonUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author KelvinYe
 */
public class HttpRequestData {
    private static Type hashMapType = new TypeToken<HashMap<String, String>>() {
    }.getType();

    private Map<String, String> dataMap;

    private String jsonData;

    public HttpRequestData() {
        dataMap = new HashMap<>();
    }

    public HttpRequestData(String jsonData) {
        this.jsonData = jsonData;
    }


    public HttpRequestData addData(String key, String value) {
        dataMap.put(key, value);
        return this;
    }

    public Map<String, String> toMap() {
        if (dataMap == null) {
            return JsonUtil.fromJson(jsonData, hashMapType);
        }
        return dataMap;
    }

    public HttpEntity createStringEntity(String charset) {
        return new StringEntity(dataMap.toString(), charset);
    }

    @Override
    public String toString() {
        if (jsonData == null) {
            return JsonUtil.toJson(dataMap);
        }
        return jsonData;
    }
}
