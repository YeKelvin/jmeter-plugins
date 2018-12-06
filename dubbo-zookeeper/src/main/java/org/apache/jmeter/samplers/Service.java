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
     * @param classFullName  类名
     * @param methodName 方法名
     */
    public Service(String classFullName, String methodName) throws ClassNotFoundException {
        this.className = getClassName(classFullName);
        this.method = ReflectUtil.getMethod(classFullName, methodName);
    }

    /**
     * json报文转换为dto对象
     * @param json 接口入参json报文
     */
    public void setParams(String json) {
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
        Object response = method.invoke(getBean(className), getParams());
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
    private String getClassName(String classFullName) {
        String[] classNames = classFullName.split("\\.");
        return classNames[classNames.length - 1];
    }

}
