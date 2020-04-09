package org.apache.jmeter.samplers;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.common.utils.FileUtil;
import org.apache.jmeter.common.utils.ExceptionUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DubboZK extends AbstractJavaSamplerClient {
    private static final String LINE_SEP = FileUtil.LINE_SEPARATOR;
    private String classFullName;
    private String methodName;
    private String expectation;
    private Service service;
    private String serviceInitErrorMessage;
    private File logFile;

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
        logFile = new File(JMeterUtils.getJMeterHome() + File.separator +
                "log" + File.separator +
                "error-" + (new SimpleDateFormat("MMdd-HHmmss")).format(new Date()) + ".log");
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
            result.setResponseData(serviceInitErrorMessage, StandardCharsets.UTF_8.name());
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
                result.setResponseData(responseData, StandardCharsets.UTF_8.name());
            }
            if (!isSuccess) {
                // 失败时才写日志
                errorLog(classFullName, methodName, params, responseData, result.getEndTime() - result.getStartTime());
            }
            System.out.println(isSuccess);
        }
        return result;
    }

    @Override
    public void teardownTest(JavaSamplerContext context) {
    }

    private void errorLog(String className, String methodName, String request, String response, long elapsed) {
        String content = String.format("【%s.%s】-【elapsed %s ms】 ", className, methodName, elapsed) + LINE_SEP +
                request + LINE_SEP + response + LINE_SEP + LINE_SEP;
        FileUtil.appendFile(logFile, content);
    }
}

