package org.apache.jmeter.samplers;

import org.apache.jmeter.samplers.utils.ReflectUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Description: 接口调用
 *
 * @author: KelvinYe
 * Date: 2018-05-11
 * Time: 15:53
 */
public class Service {
    private static ApplicationContext ctx = new ClassPathXmlApplicationContext("ApplicationContext.xml");
    private String className;
    private Method method;
    private Parameter params;

    /**
     * @param className  类名（含完整包路径）
     * @param methodName 方法名
     * @param json       请求json
     */
    public Service(String className, String methodName, String json) throws ClassNotFoundException {
        this.className = className;
        this.method = ReflectUtil.getMethod(className, methodName);
        this.params = new Parameter(json);
    }

    /**
     * 获取方法的参数数组
     */
    public Object[] getParams() {
        return params.getParamsFromJson(method.getParameterTypes());
    }

    /**
     * 反射调用方法名为methodName的方法
     */
    public Response invoke() throws InvocationTargetException, IllegalAccessException {
        Object response = method.invoke(getBean(getClassName()), getParams());
        return new Response(response);
    }

    /**
     * 获取javaBean
     *
     * @param beanID beanID
     * @return 对象
     */
    private Object getBean(String beanID) {
        return ctx.getBean(beanID);
    }

    /**
     * 由于入參className包含包路径名，需要分割提取真正的类名
     */
    private String getClassName() {
        String[] classNames = className.split("\\.");
        return classNames[classNames.length - 1];
    }

}
