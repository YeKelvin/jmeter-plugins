package pers.kelvin.util.http;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author KelvinYe
 */
public class HttpHeader {
    private static Map<String, HttpHeader> defaultHeaders;

    private Map<String, String> headers;

    /**
     * 初始化空请求头部
     */
    public HttpHeader() {
        headers = new HashMap<>();
    }

    private HttpHeader(boolean isUseDefaultHeaders) {
        this();
        if (isUseDefaultHeaders && defaultHeaders.containsKey("default")) {
            headers.putAll(defaultHeaders.get("default").toMap());
        }
    }

    /**
     * 初始化带默认头部的请求头部
     *
     * @param defaultHeadersName 默认请求的分类名称
     */
    private HttpHeader(String defaultHeadersName) {
        this();
        if (MapUtils.isNotEmpty(defaultHeaders)) {
            headers.putAll(defaultHeaders.get(defaultHeadersName).toMap());
        }
    }

    public static HttpHeader useDefaultHeaders() {
        return new HttpHeader(true);
    }

    public static HttpHeader getDefaultHeaders(String defaultHeadersName) {
        return new HttpHeader(defaultHeadersName);
    }

    /**
     * 配置默认请求头部键值对
     *
     * @param key   键
     * @param value 值
     */
    public static void setDefaultHeaders(String key, String value) {
        setDefaultHeaders(key, value, "default");
    }

    /**
     * 配置默认请求头部键值对
     *
     * @param key               键
     * @param value             值
     * @param defaultHeaderName 默认请求的分类名称
     */
    public static void setDefaultHeaders(String key, String value, String defaultHeaderName) {
        if (MapUtils.isEmpty(defaultHeaders)) {
            defaultHeaders = new HashMap<>();
        }
        if (!defaultHeaders.containsKey(defaultHeaderName)) {
            defaultHeaders.put(defaultHeaderName, new HttpHeader());
        }
        if (StringUtils.isEmpty(defaultHeaderName)) {
            defaultHeaderName = "default";
        }
        defaultHeaders.get(defaultHeaderName).put(key, value);
    }

    /**
     * 设置请求头部键值对
     *
     * @param key   键
     * @param value 值
     * @return this
     */
    public HttpHeader put(String key, String value) {
        headers.put(key, value);
        return this;
    }

    /**
     * 将 HttpHeader对象转换为 map对象
     *
     * @return Map<String, String>对象
     */
    public Map<String, String> toMap() {
        return headers;
    }
}
