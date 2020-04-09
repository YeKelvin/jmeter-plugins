package org.apache.jmeter.common.utils;

import com.google.gson.Gson;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.apache.jmeter.common.utils.json.JsonUtil;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

/**
 * 报文加签工具类，Gson
 * json格式报文按照keyName首字母排序后用MD5加密。
 *
 * @author Kelvin.Ye
 */
public class Signature {

    private static final Logger logger = LogUtil.getLogger(Signature.class);

    private static Gson gson = JsonUtil.getGsonInstance();

    /**
     * 报文加签
     *
     * @param json   json报文
     * @param prefix 加签前缀
     * @return 报文加签md5密文
     */
    public static String sign(String json, String prefix) {
        if (StringUtil.isBlank(json)) {
            return "";
        }

        // json排序
        Map<Object, Object> resultMap = sortMapByKey(gson.fromJson(json, JsonUtil.mapType));
        StringBuffer orderedSB = new StringBuffer();
        if (resultMap != null) {
            resultMap.forEach((key, value) -> traverse(orderedSB, key, value));
        }
        String sign = orderedSB.substring(0, orderedSB.length() - 1);

        // 拼接前缀
        if (StringUtil.isNotBlank(prefix)) {
            sign = prefix + "&" + sign;
        }
        logger.debug("sign={}", sign);

        // md5加密
        if (StringUtil.isNotBlank(sign)) {
            sign = md5(sign);
        }
        return sign;
    }

    /**
     * 报文排序后拼接各字段
     *
     * @param sb    StringBuffer对象
     * @param key   key对象
     * @param value value对象
     */
    private static void traverse(StringBuffer sb, Object key, Object value) {
        if (value instanceof Map) {
            sb.append(key).append("=").append(traverseMap(new HashMap<Object, Object>((Map) value))).append("&");
        } else if (value instanceof List) {
            sb.append(key).append("=").append(traverseList(new ArrayList<Object>((List) value))).append("&");
        } else {
            sb.append(key).append("=").append(value).append("&");
        }
    }

    /**
     * 排序Map对象后拼接各字段
     *
     * @param map Map对象
     * @return str
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
     * 排序List对象后拼接各字段
     *
     * @param list List对象
     * @return str
     */
    private static String traverseList(List<Object> list) {
        StringBuffer sb = new StringBuffer();
        if (CollectionUtils.isEmpty(list)) {
            return sb.append("[]").toString();
        }
        sb.append("[");
        list.forEach(item -> {
            if (item instanceof Map) {
                sb.append(traverseMap(new HashMap<Object, Object>((Map) item)));
            } else if (item instanceof List) {
                sb.append(traverseList(new ArrayList<Object>((List) item)));
            } else {
                sb.append(item);
            }
            sb.append(", ");
        });
        return sb.substring(0, sb.length() - 2) + "]";
    }

    /**
     * Map 根据keyName 排序
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
            data= md5.digest(str.getBytes(StandardCharsets.UTF_8));
            strDigest = bytesToHexString(data).toLowerCase();
        } catch (Exception e) {
            logger.error(ExceptionUtil.getStackTrace(e));
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
