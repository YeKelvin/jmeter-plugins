package pers.kelvin.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import pers.kelvin.util.exception.ExceptionUtil;
import pers.kelvin.util.log.LogUtil;

import java.security.MessageDigest;
import java.util.*;

/**
 * 报文加签工具类，jackson
 * json格式报文按照keyName首字母排序后用MD5加密。
 *
 * @author Kelvin.Ye
 */
public class Signature2 {
    private static final Logger logger = LogUtil.getLogger(Signature2.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * A-Z排序工具类
     */
    public static String sign(Map<Object, Object> map, Object prefix) {
        Map<Object, Object> resultMap = sortMapByKey(map);
        StringBuilder sb = new StringBuilder();
        String sign = null;
        if (!map.isEmpty()) {
            resultMap.forEach((key, value) -> sorted(sb, key, value));
            sign = sb.substring(0, sb.length() - 1);
            logger.debug("sign after md5={}&{}", prefix, sign);
        }
        if (sign != null && prefix != null && prefix != "") {
            sign = md5(prefix + "&" + sign);
        }
        return sign;
    }

    private static void sorted(StringBuilder sb, Object key, Object value) {
        if (value instanceof Map) {
            sb.append(key).append("=").append(sortedMap(new HashMap<Object, Object>((Map) value))).append("&");
        } else if (value instanceof List) {
            sb.append(key).append("=").append(sortedArray(new ArrayList<Object>((List) value))).append("&");
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
                sb.append(sortedMap(new HashMap<Object, Object>((Map) item)));
            } else if (item instanceof List) {
                sb.append(sortedArray(new ArrayList<Object>((List) item)));
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
    public static Map<Object, Object> sortMapByKey(Map<Object, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<Object, Object> sortMap = new TreeMap<>(Comparator.comparing(Object::toString));
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

}
