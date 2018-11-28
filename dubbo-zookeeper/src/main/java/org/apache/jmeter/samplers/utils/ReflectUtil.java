package org.apache.jmeter.samplers.utils;

import java.lang.reflect.Method;

/**
 * @author: KelvinYe
 * Date: 2018-05-11
 * Time: 17:25
 */
public class ReflectUtil {
    public static Method getMethod(String className, String methodName) throws ClassNotFoundException {
        Method method = null;
        Class<?> tclass = Class.forName(className);
        for (Method m : tclass.getDeclaredMethods()) {
            if (methodName.equals(m.getName())) {
                method = m;
            }
        }
        return method;
    }

    public static Method getMethod(Class<?> tclass, String methodName) throws ClassNotFoundException {
        Method method = null;
        for (Method m : tclass.getDeclaredMethods()) {
            if (methodName.equals(m.getName())) {
                method = m;
            }
        }
        return method;
    }
}
