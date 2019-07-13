package pers.kelvin.util;

import org.slf4j.Logger;
import pers.kelvin.util.log.LogUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectUtil {

    private static final Logger logger = LogUtil.getLogger(ReflectUtil.class);

    /**
     * 获取对象的属性名数组
     */
    public static String[] getFiledName(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        String[] fieldNames = new String[fields.length];

        for (int i = 0; i < fields.length; i++) {
            fieldNames[i] = fields[i].getName();
        }
        return fieldNames;
    }

    /**
     * 获取属性类型(type)，属性名(name)的map组成的list
     */
    public static List getFiledsInfo(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> infoMap;

        for (Field field : fields) {
            infoMap = new HashMap<>();
            infoMap.put("type", field.getType().toString());
            infoMap.put("name", field.getName());
            list.add(infoMap);
        }
        return list;
    }

    /**
     * 获取方法
     *
     * @param className  类名
     * @param methodName 方法名
     * @return Method对象
     * @throws ClassNotFoundException 异常
     */
    public static Method getMethod(String className, String methodName) throws ClassNotFoundException {
        return getMethod(Class.forName(className), methodName);
    }

    /**
     * 获取方法
     *
     * @param tclass     类对象
     * @param methodName 方法名
     * @return Method对象
     */
    public static Method getMethod(Class<?> tclass, String methodName) {
        Method method = null;
        for (Method m : tclass.getDeclaredMethods()) {
            if (methodName.equals(m.getName())) {
                method = m;
            }
        }
        return method;
    }
}
