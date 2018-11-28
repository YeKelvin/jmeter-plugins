package org.apache.jmeter.samplers;

import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.engine.util.ValueReplacer;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.utils.JsonFileUtil;
import org.apache.jmeter.samplers.utils.JsonPathUtil;
import org.apache.jmeter.samplers.utils.TelnetUtil;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.util.JMeterUtils;
import pers.kelvin.util.ExceptionUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * @author KelvinYe
 */
public class DubboTelnetByFile extends AbstractJavaSamplerClient {
    private String interfaceName;
    private TelnetUtil telnet;
    private String connectErrorMessage;
    private String configFilePath = JMeterUtils.getJMeterHome() + File.separator +
            "config" + File.separator +
            "config.json";

    /**
     * 设置默认传参
     */
    @Override
    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();
        params.addArgument("address", "ip:port");
        params.addArgument("interface", "");
        params.addArgument("jsonPaths", "");
        params.addArgument("expection", "\"success\":true");
        return params;
    }

    /**
     * 测试执行初始化操作
     */
    @Override
    public void setupTest(JavaSamplerContext ctx) {
        String[] address = ctx.getParameter("address").split(":");
        String ip = address[0];
        String port = address.length == 1 ? "0" : address[1];
        interfaceName = ctx.getParameter("interface", "");
        try {
            telnet = new TelnetUtil(ip, port);
        } catch (IOException | NumberFormatException e) {
            connectErrorMessage = ExceptionUtil.getStackTrace(e);
        }
    }

    /**
     * 测试执行
     */
    @Override
    public SampleResult runTest(JavaSamplerContext ctx) {
        SampleResult result = new SampleResult();
        result.setEncodingAndType("UTF-8");
        String jsonPaths = ctx.getParameter("jsonPaths", "");
        String expection = ctx.getParameter("expection", "");
        JsonPathUtil jsonUtil = new JsonPathUtil();
        boolean isSuccess = false;
        String dubboResponse = "";
        try {
            // 根据接口名获取json模版
            String templateJson = JsonFileUtil.readJsonFile(configFilePath, interfaceName);
            // 根据jsonPaths更新json
            String requestJson = jsonUtil.updateJson(jsonPaths, templateJson);
            // 替换json中的key和function值
            requestJson = replaceValues(requestJson);
            result.setSamplerData(requestJson);
            result.sampleStart();
            if (telnet != null) {
                // telnet连接成功则开始invoke报文
                dubboResponse = telnet.invokeDubbo(interfaceName, requestJson);
                if (dubboResponse.contains(expection)) {
                    // 判断结果是否包含期望值
                    isSuccess = true;
                }
            } else {
                // telnet连接失败输出报错信息
                dubboResponse = connectErrorMessage;
            }
        } catch (Exception e) {
            // 异常后，判断是否已开始统计sample时间，没有则开始统计
            if (result.isStampedAtStart()) {
                result.sampleStart();
            }
            dubboResponse = ExceptionUtil.getStackTrace(e);
        } finally {
            result.sampleEnd();
            result.setSuccessful(isSuccess);
            result.setResponseData(dubboResponse, "UTF-8");
            result.setResponseCode(getResponseCode(dubboResponse));
        }
        return result;
    }

    /**
     * 测试执行结束释放操作
     */
    @Override
    public void teardownTest(JavaSamplerContext ctx) {
        if (telnet != null) {
            telnet.disconnect();
        }
    }

    /**
     * 将JMeterVariables转换为Map
     */
    private Map<String, String> varsToMap(JMeterVariables vars) {
        Map<String, String> varMap = new HashMap<>();
        Iterator<Map.Entry<String, Object>> iterator = vars.getIterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                varMap.put(key, (String) value);
            } else {
                varMap.put(key, value.toString());
            }
        }
        return varMap;
    }


    private TestPlan getTestPlan() {
        TestPlan testPlan = new TestPlan();
        Properties props = JMeterUtils.getJMeterProperties();
        for (String name : props.stringPropertyNames()) {
            testPlan.addParameter(name, props.getProperty(name));
        }
        return testPlan;
    }


    private String replaceValues(String values) throws InvalidVariableException {
        Argument element = new Argument("replaceValues", values);
        ValueReplacer replacer = new ValueReplacer(getTestPlan());
        replacer.addVariables(varsToMap(JMeterContextService.getContext().getVariables()));
        replacer.replaceValues(element);
        element.setRunningVersion(true);
        return element.getValue();
    }

    private String getResponseCode(String responseData) {
        if (responseData.contains("\"success\":true")) {
            return "true";
        }
        return "false";
    }

}
