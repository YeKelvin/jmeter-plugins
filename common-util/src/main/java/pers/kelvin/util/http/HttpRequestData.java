package pers.kelvin.util.http;

import com.google.gson.reflect.TypeToken;
import pers.kelvin.util.json.JsonUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author KelvinYe
 */
public class HttpRequestData {
    public static Type hashMapType = new TypeToken<HashMap<String, String>>() {
    }.getType();

    private Map<String, String> requestData;

    private String requestJson;

    public HttpRequestData() {
        requestData = new HashMap<>();
    }

    public HttpRequestData(String requestJson) {
        this.requestJson = requestJson;
    }

    public HttpRequestData put(String key, String value) {
        requestData.put(key, value);
        return this;
    }

    public Map<String, String> toMap() {
        if (requestData == null) {
            return JsonUtil.fromJson(requestJson, hashMapType);
        }
        return requestData;
    }

    @Override
    public String toString() {
        if (requestJson == null) {
            return JsonUtil.toJson(requestData);
        }
        return requestJson;
    }
}
