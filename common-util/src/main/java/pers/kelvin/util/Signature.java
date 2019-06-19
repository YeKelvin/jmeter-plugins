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

    private static Type hashMapType = new TypeToken<HashMap<Object, Object>>() {
    }.getType();

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

        Map<Object, Object> resultMap = sortMapByKey(gson.fromJson(json, hashMapType));
        StringBuffer sb = new StringBuffer();
        if (resultMap != null) {
            resultMap.forEach((key, value) -> sorted(sb, key, value));
        }

        String sign = sb.substring(0, sb.length() - 1);
        logger.debug("sign after md5=" + prefix + "&" + sign);

        // md5加密
        if (StringUtil.isNotBlank(sign) && StringUtil.isNotBlank(prefix)) {
            sign = md5(prefix + "&" + sign);
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
    private static void sorted(StringBuffer sb, Object key, Object value) {
        if (value instanceof Map) {
            sb.append(key).append("=").append(sortedMap(new HashMap<Object, Object>((Map) value))).append("&");
        } else if (value instanceof List) {
            sb.append(key).append("=").append(sortedArray(new ArrayList<Object>((List) value))).append("&");
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
    private static String sortedMap(Map<Object, Object> map) {
        Map<Object, Object> resultMap = sortMapByKey(map);
        StringBuffer sb = new StringBuffer();
        if (map == null || map.size() <= 0) {
            return sb.append("{}").toString();
        }
        sb.append("{");
        resultMap.forEach((key, value) -> sorted(sb, key, value));
        return sb.substring(0, sb.length() - 1) + "}";
    }

    /**
     * 排序List对象后拼接各字段
     *
     * @param list List对象
     * @return str
     */
    private static String sortedArray(List<Object> list) {
        StringBuffer sb = new StringBuffer();
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
        // 降序排序
        Map<Object, Object> sortMap = new TreeMap<>(Comparator.comparing(Object::toString));
        sortMap.putAll(map);
        return sortMap;
    }

    /**
     * md5加密
     */
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
     *     Map mapTypes = Signature.toMap(body);
     *     String sign = Signature.sign(mapTypes, prefix);
     *     return sign;
     * }
     */
}
