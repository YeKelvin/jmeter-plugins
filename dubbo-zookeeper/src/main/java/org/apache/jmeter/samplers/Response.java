package org.apache.jmeter.samplers;

import org.apache.jmeter.samplers.utils.ReflectUtil;
import pers.kelvin.util.json.JsonUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Description: 响应相关
 *
 * @author: KelvinYe
 * Date: 2018-05-11
 * Time: 17:13
 */
public class Response {
    private Object response;
    private Class<?> tclass;

    public Response(Object responseObj) {
        this.response = responseObj;
        this.tclass = responseObj.getClass();
    }

    public Boolean isSuccess() throws InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        Method method = ReflectUtil.getMethod(tclass, "isSuccess");
        return (Boolean) method.invoke(response);
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(response);
    }
}
