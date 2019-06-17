package pers.kelvin.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import pers.kelvin.util.exception.ExceptionUtil;
import pers.kelvin.util.json.JsonUtil;
import pers.kelvin.util.log.LogUtil;

import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.util.*;

/**
 * 报文加签工具类，json格式报文按照keyName首字母排序后用MD5加密
 *
 * @author Kelvin.Ye
 */
public class Signature {
    private static final Logger logger = LogUtil.getLogger(Signature.class);

    private static Gson gson = JsonUtil.getGsonInstance();

    /**
     * A-Z排序工具类
     */
    public static String sign(Map<Object, Object> map, Object prefix) {
        Map<Object, Object> resultMap = sortMapByKey(map);
        StringBuilder sb = new StringBuilder();
        String sign = null;
        if (!map.isEmpty()) {
            resultMap.forEach((key, value) -> {
                sorted(sb, key, value);
            });
            sign = sb.substring(0, sb.length() - 1);
            logger.debug("sign after md5=" + prefix + "&" + sign);
        }
        if (sign != null && prefix != null && prefix != "") {
            sign = md5(prefix + "&" + sign);
        }
        return sign;
    }

    private static void sorted(StringBuilder sb, Object key, Object value) {
        if (value instanceof Map) {
            sb.append(key).append("=").append(sortedMap((Map) value)).append("&");
        } else if (value instanceof List) {
            sb.append(key).append("=").append(sortedArray((List) value)).append("&");
        } else {
            sb.append(key).append("=").append(value).append("&");
        }
    }

    private static String sortedMap(Map<Object, Object> map) {
        Map<Object, Object> resultMap = sortMapByKey(map);
        StringBuilder sb = new StringBuilder();
        if (map == null || map.size() <= 0) {
            sb.append("{}");
        }
        sb.append("{");
        resultMap.forEach((key, value) -> sorted(sb, key, value));
        return sb.substring(0, sb.length() - 1) + "}";
    }

    private static String sortedArray(List<Object> list) {
        StringBuilder sb = new StringBuilder();
        if (CollectionUtils.isEmpty(list)) {
            return sb.append("[]").toString();
        }
        sb.append("[");
        list.forEach(item -> {
            if (item instanceof Map) {
                sb.append(sortedMap((Map) item));
            } else if (item instanceof List) {
                sb.append(sortedArray((List) item));
            } else {
                sb.append(item);
            }
            sb.append(", ");
        });

        return sb.substring(0, sb.length() - 2) + "]";
    }

    public static Map<Object, Object> toMap(String json) {
        Type hashMapType = new TypeToken<HashMap<Object, Object>>() {
        }.getType();
        return gson.fromJson(json, hashMapType);
    }

    /**
     * Map 根据keyName 排序
     */
    public static Map<Object, Object> sortMapByKey(Map<Object, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<Object, Object> sortMap = new TreeMap<>(new Comparator<Object>() {
            public int compare(Object obj1, Object obj2) {
                // 降序排序
                return obj1.toString().compareTo(obj2.toString());
            }
        });
        sortMap.putAll(map);
        return sortMap;
    }

    public static String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] b = md.digest();

            int i;

            StringBuffer sb = new StringBuffer();
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    sb.append("0");
                sb.append(Integer.toHexString(i));
            }
            str = sb.toString();
        } catch (Exception e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
        return str;
    }

    /**
     * main
     *
     * String sign(String prefix) {
     *     Arguments args = sampler.getArguments();
     *     Map reqMap = args.getArgumentsAsMap();
     *     String body = null;
     *     for (body:
     *     reqMap.values()) {}
     *     System.out.println(body);
     *     Map mapTypes = Signature2.toMap(body);
     *     String sign = Signature2.sign(mapTypes, prefix);
     *     return sign;
     * }
     */
}
