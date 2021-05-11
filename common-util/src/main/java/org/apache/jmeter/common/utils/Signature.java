package org.apache.jmeter.common.utils;

import com.google.gson.JsonSyntaxException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.common.utils.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 报文加签工具类
 * Json报文按照 key首字母排序后用MD5加密
 *
 * @author Kelvin.Ye
 */
public class Signature {
    private static final Logger log = LoggerFactory.getLogger(Signature.class);

    /**
     * 报文加签
     *
     * @param json   json报文
     * @param prefix 加签前缀
     * @return 报文加签md5密文
     */
    public static String sign(String json, String prefix) {
        try {
            if (StringUtils.isBlank(json)) {
                return "";
            }

            // 排序Json
            Map<Object, Object> resultMap = sortMapByKey(JsonUtil.fromJson(json, JsonUtil.mapType));
            StringBuffer orderedSB = new StringBuffer();
            if (resultMap != null) {
                resultMap.forEach((key, value) -> traverse(orderedSB, key, value));
            }
            String sign = orderedSB.substring(0, orderedSB.length() - 1);

            // 拼接前缀
            if (StringUtils.isNotBlank(prefix)) {
                sign = prefix + "&" + sign;
            }
            log.debug("sign={}", sign);

            // md5加密
            if (StringUtils.isNotBlank(sign)) {
                sign = md5(sign);
            }
            return sign;
        } catch (JsonSyntaxException e) {
            log.error(ExceptionUtil.getStackTrace(e));
            log.error("Sign函数目前仅支持Json格式报文");
            return "";
        }
    }

    /**
     * 递归遍历报文并排序，排序完成后拼接字段
     */
    @SuppressWarnings("unchecked")
    private static void traverse(StringBuffer sb, Object key, Object value) {
        if (value instanceof Map) {
            sb.append(key).append("=").append(traverseMap((Map<Object, Object>) value)).append("&");
        } else if (value instanceof List) {
            sb.append(key).append("=").append(traverseList((List<Object>) value)).append("&");
        } else {
            sb.append(key).append("=").append(value).append("&");
        }
    }

    /**
     * 排序 Map并拼接字段
     */
    private static String traverseMap(Map<Object, Object> map) {
        StringBuffer sb = new StringBuffer();
        if (MapUtils.isEmpty(map)) {
            return sb.append("{}").toString();
        }
        Map<Object, Object> sortedMap = sortMapByKey(map);
        if (MapUtils.isEmpty(sortedMap)) {
            return sb.append("{}").toString();
        }
        sb.append("{");
        sortedMap.forEach((key, value) -> traverse(sb, key, value));
        return sb.substring(0, sb.length() - 1) + "}";
    }

    /**
     * 排序 List并拼接字段
     */
    @SuppressWarnings("unchecked")
    private static String traverseList(List<Object> list) {
        StringBuffer sb = new StringBuffer();
        if (CollectionUtils.isEmpty(list)) {
            return sb.append("[]").toString();
        }
        sb.append("[");
        list.forEach(item -> {
            if (item instanceof Map) {
                sb.append(traverseMap((Map<Object, Object>) item));
            } else if (item instanceof List) {
                sb.append(traverseList((List<Object>) item));
            } else {
                sb.append(item);
            }
            sb.append(",");
        });
        return sb.substring(0, sb.length() - 1) + "]";
    }

    /**
     * 根据 key排序 Map
     */
    private static Map<Object, Object> sortMapByKey(Map<Object, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        // 降序排序
        Map<Object, Object> sortMap = new TreeMap<>(Comparator.comparing(Object::toString));
        sortMap.putAll(map);
        return sortMap;
    }

    /**
     * md5加密
     */
    private static String md5(String str) {
        String strDigest = "";
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] data;
            data = md5.digest(str.getBytes(StandardCharsets.UTF_8));
            strDigest = bytesToHexString(data).toLowerCase();
        } catch (Exception e) {
            log.error(ExceptionUtil.getStackTrace(e));
        }
        return strDigest;
    }

    private static String bytesToHexString(byte[] src) {
        StringBuffer sb = new StringBuffer();
        if (src == null || src.length <= 0) {
            return "";
        }
        for (byte b : src) {
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                sb.append(0);
            }
            sb.append(hv);
        }
        return sb.toString();
    }
}
