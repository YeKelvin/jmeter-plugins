package pers.kelvin.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import pers.kelvin.util.log.LogUtil;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.*;

/**
 * 报文加签工具类，json格式报文按照keyName首字母排序后用MD5加密
 *
 * @author Kelvin.Ye
 */
public class Signature {
    private static final Logger logger = LogUtil.getLogger(Signature.class);

    // 登录前，使用A-Z排序工具类
    public static String signBeforLogin(Map<Object, Object> map, String Key) {
        Map<Object, Object> resultMap = sortMapByKey(map);
        StringBuilder sb = new StringBuilder();
        String sign = null;
        if (!map.isEmpty()) {
            resultMap.forEach((key, value) -> {
                sorted(sb, key, value);
            });
            sign = sb.substring(0, sb.length() - 1);
            System.out.println("signBeforLogin:" + Key + "&" + sign);
        }
        sign = md5(Key + "&" + sign);
        return sign;
    }

    // 登录后，使用A-Z排序工具类
    public static String signAfterLogin(Map<Object, Object> map, Object mobileToken) {
        Map<Object, Object> resultMap = sortMapByKey(map);
        StringBuilder sb = new StringBuilder();
        String sign = null;
        if (!map.isEmpty()) {
            resultMap.forEach((key, value) -> {
                sorted(sb, key, value);
            });
            sign = sb.substring(0, sb.length() - 1);
            System.out.println("signAfterLogin" + mobileToken + "&" + sign);
        }
        if (sign != null && mobileToken != null && mobileToken != "") {
            sign = md5(mobileToken + "&" + sign);
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

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<Object, Object> toMap(String body) throws IOException {
        ObjectMapper objectMapper2 = new ObjectMapper();
        Map<Object, Object> mapTypes = objectMapper2.readValue(body, HashMap.class);
        return mapTypes;
    }

    /**
     * 使用Map 按key 进行排序
     *
     * @param map
     * @return
     */
    public static Map<Object, Object> sortMapByKey(Map<Object, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<Object, Object> sortMap = new TreeMap<Object, Object>(new Comparator<Object>() {
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

            StringBuffer buf = new StringBuffer();
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            str = buf.toString();
        } catch (Exception e) {
            e.printStackTrace();

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
     *     Map mapTypes = Signature.toMap(body);
     *     String sign = Signature.signAfterLogin(mapTypes, prefix);
     *     return sign;
     * }
     */
}
