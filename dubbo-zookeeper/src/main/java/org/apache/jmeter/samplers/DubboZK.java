package org.apache.jmeter.samplers;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import pers.kelvin.util.log.JLog;

public class DubboZK extends AbstractJavaSamplerClient {
    private String className;
    private String methodName;
    private boolean expection;

    @Override
    public Arguments getDefaultParameters() {
        Arguments arguments = new Arguments();
        arguments.addArgument("className", "include package");
        arguments.addArgument("methodName", "");
        arguments.addArgument("params", "");
        arguments.addArgument("expection", "true");
        return arguments;
    }

    @Override
    public void setupTest(JavaSamplerContext args) {
        className = args.getParameter("className");
        methodName = args.getParameter("methodName");
        expection = Boolean.parseBoolean(args.getParameter("expection"));

    }

    @Override
    public SampleResult runTest(JavaSamplerContext args) {
        String params = args.getParameter("params");
        Response response;
        String responseData = "null";
        boolean isSuccess = false;
        SampleResult result = new SampleResult();
        try {
            Service service = new Service(className, methodName, params);
            result.sampleStart();
            // 接口调用
            response = service.invoke();
            result.sampleEnd();
            if (response != null) {
                isSuccess = response.isSuccess() == expection;
                responseData = response.toString();
            }
        } catch (Exception e) {
            responseData = e.getMessage();
            e.printStackTrace();
        } finally {
            result.setSuccessful(isSuccess);
            result.setSamplerData(params);
            if (responseData != null) {
                result.setResponseData(responseData, "UTF-8");
            }
            if (!isSuccess) {
                // 失败时才写日志
                JLog.error(className, methodName, params, responseData, result.getEndTime() - result.getStartTime());
            }
            System.out.println(isSuccess);
        }
        return result;
    }

    @Override
    public void teardownTest(JavaSamplerContext context) {
    }
}

