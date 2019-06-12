package org.apache.jmeter.samplers;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import pers.kelvin.util.exception.ExceptionUtil;
import pers.kelvin.util.log.JLog;

public class DubboZK extends AbstractJavaSamplerClient {
    private String classFullName;
    private String methodName;
    private String expectation;
    private Service service;
    private String serviceInitErrorMessage;

    @Override
    public Arguments getDefaultParameters() {
        Arguments arguments = new Arguments();
        arguments.addArgument("classFullName", "");
        arguments.addArgument("methodName", "");
        arguments.addArgument("params", "");
        arguments.addArgument("expectation", "");
        return arguments;
    }

    @Override
    public void setupTest(JavaSamplerContext args) {
        classFullName = args.getParameter("classFullName");
        methodName = args.getParameter("methodName");
        expectation = args.getParameter("expectation");
        try {
            service = new Service(classFullName, methodName);
        } catch (Exception e) {
            serviceInitErrorMessage = ExceptionUtil.getStackTrace(e);
        }

    }

    @Override
    public SampleResult runTest(JavaSamplerContext args) {
        String params = args.getParameter("params");
        Response response;
        String responseData = "null";
        boolean isSuccess = false;
        SampleResult result = new SampleResult();
        // 如Service类初始化失败则sample结束并置为失败
        if (serviceInitErrorMessage != null) {
            result.setSuccessful(false);
            result.setResponseData(serviceInitErrorMessage, "UTF-8");
            // 因jmeter同一线程会循环执行，需重置该属性
            serviceInitErrorMessage = null;
            System.out.println(false);
            return result;
        }
        try {
            // json报文转换为dto对象
            service.setParams(params);
            result.sampleStart();
            // 接口调用
            response = service.invoke();
            result.sampleEnd();
            if (response != null) {
                responseData = response.toString();
                isSuccess = responseData.contains(expectation);
            }
        } catch (Exception e) {
            responseData = ExceptionUtil.getStackTrace(e);
        } finally {
            result.setSuccessful(isSuccess);
            result.setSamplerData(params);
            if (responseData != null) {
                result.setResponseData(responseData, "UTF-8");
            }
            if (!isSuccess) {
                // 失败时才写日志
                JLog.error(classFullName, methodName, params, responseData, result.getEndTime() - result.getStartTime());
            }
            System.out.println(isSuccess);
        }
        return result;
    }

    @Override
    public void teardownTest(JavaSamplerContext context) {
    }
}

