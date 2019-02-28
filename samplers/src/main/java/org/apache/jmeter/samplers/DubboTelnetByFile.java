package org.apache.jmeter.samplers;

import com.jayway.jsonpath.DocumentContext;
import groovy.lang.Binding;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.samplers.utils.GroovyUtil;
import org.apache.jmeter.samplers.utils.JsonFileUtil;
import org.apache.jmeter.samplers.utils.TelnetUtil;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import pers.kelvin.util.exception.ExceptionUtil;
import pers.kelvin.util.exception.ServiceException;
import pers.kelvin.util.json.JsonPathUtil;
import pers.kelvin.util.json.JsonUtil;
import pers.kelvin.util.log.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description
 *
 * @author KelvinYe
 * Date     2019-02-22
 * Time     11:47
 */
public class DubboTelnetByFile extends AbstractSampler {

    private static final Logger logger = LogUtil.getLogger(DubboTelnetByFile.class);

    public static final String ADDRESS = "address";
    public static final String INTERFACE_NAME = "interfaceName";
    public static final String PARAMS = "params";
    public static final String EXPECTION = "expection";
    public static final String USE_TEMPLATE = "useTemplate";
    public static final String INTERFACE_SYSTEM = "interfaceSystem";
    public static final String TEMPLATE_CONTENT = "templateContent";
    public static final String CONFIG_FILE_PATH = JMeterUtils.getJMeterHome() + File.separator + "config" +
            File.separator + "config.json";
    private static final String REPLACE_VALUE = "replaceValue";

    @Override
    public SampleResult sample(Entry entry) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.setEncodingAndType("UTF-8");
        boolean isSuccess = false;
        String responseData = "";
        try {
            String requestData = transformParams(getParams(), getUseTemplate());
            result.setSamplerData(requestData);
            result.sampleStart();
            responseData = invokeMethod(getAddress(), getInterfaceName(), requestData);
            isSuccess = getSuccessful(responseData, getExpection());
        } catch (Exception e) {
            // 异常后，判断是否已开始统计sample时间，没有则开始统计
            if (result.isStampedAtStart()) {
                result.sampleStart();
            }
            responseData = ExceptionUtil.getStackTrace(e);
        } finally {
            result.sampleEnd();
            result.setSuccessful(isSuccess);
            result.setResponseData(responseData, "UTF-8");
        }

        return result;
    }

    /**
     * 参数化${}占位符
     *
     * @param params      json报文
     * @param useTemplate 是否使用json模版
     * @return 参数化替换后的json报文
     */
    private String transformParams(String params, boolean useTemplate) throws IOException {
        if (useTemplate) {
            // 根据接口名获取json模版
            String templateJson = JsonFileUtil.readJsonFile(CONFIG_FILE_PATH, getInterfaceName());
            if (templateJson == null) {
                throw new ServiceException(String.format("%s json模版获取失败", getInterfaceName()));
            }
            // 根据params转换为如要替换json模版的jsonPath列表
            List<String> jsonPathList = JsonPathUtil.getJsonPathList(toArrayJson(params));
            System.out.println(JsonUtil.toJson(jsonPathList));
            DocumentContext paramsDCtx = JsonPathUtil.jsonParse(toArrayJson(params));
            DocumentContext templateJsonDCtx = JsonPathUtil.jsonParse(toArrayJson(templateJson));
            for (String jsonPath : jsonPathList) {
                Object newValue = paramsDCtx.read(jsonPath);
                System.out.println(JsonUtil.toJson(newValue));
                if (newValue instanceof HashMap) {
                    System.out.println("continue - " + JsonUtil.toJson(newValue));
                    continue;
                }
                templateJsonDCtx.set(jsonPath, newValue);
            }
            String requestData = templateJsonDCtx.jsonString();
            requestData = requestData.substring(1, requestData.length() - 2);
            return replaceValue(requestData);
        }
        // 不使用json模版时原值返回
        return params;
    }


    /**
     * 在json字符串最外层添加“[]”，转换为ArrayJson
     */
    public String toArrayJson(String json) {
        return "[" + json + "]";
    }

    /**
     * 控制TestElement的runningVersion，替换为${}占位符真值
     */
    private String replaceValue(String value) {
        setProperty(REPLACE_VALUE, value);
        setRunningVersion(true);
        String afterReplaceValue = getPropertyAsString(REPLACE_VALUE);
        removeProperty(REPLACE_VALUE);
        return afterReplaceValue;
    }

    /**
     * telnet invoke dubbo接口
     *
     * @param address       dubbo接口地址
     * @param interfaceName dubbo接口名称
     * @param requestData   json报文
     * @return 响应报文
     */
    private String invokeMethod(String address, String interfaceName, String requestData) throws IOException {
        String[] addressArray = address.split(":");
        String ip = addressArray[0];
        String port = addressArray.length == 1 ? "0000" : addressArray[1];

        TelnetUtil telnet = new TelnetUtil(ip, port);
        return telnet.invokeDubbo(interfaceName, requestData);
    }

    /**
     * 判断expection预期结果是否为逻辑表达式，
     * 如是则转换为groovy表达式，交由groovy执行并返回boolean结果，
     * 否则预期结果判断逻辑为responseData响应报文中是否包含expection预期结果的值，包含为true，不包含为false。
     *
     * @param responseData 响应报文
     * @param expection    预期结果
     * @return ture | false
     */
    private boolean getSuccessful(String responseData, String expection) {
        if (GroovyUtil.isExpression(expection)) {
            if (GroovyUtil.verifyExpression(expection) && GroovyUtil.verifyBrackets(expection)) {
                try {
                    String expression = GroovyUtil.transformExpression(expection);
                    Binding binding = new Binding();
                    binding.setVariable("response", responseData);
                    return (boolean) GroovyUtil.eval(binding, expression);
                } catch (Exception e) {
                    logger.error(ExceptionUtil.getStackTrace(e));
                    return false;
                }
            } else {
                logger.error("预期结果表达式语法有误");
                return false;
            }
        }
        return responseData.contains(expection);
    }

    private String getAddress() {
        return getPropertyAsString(ADDRESS);
    }

    private String getInterfaceName() {
        return getPropertyAsString(INTERFACE_NAME);
    }

    private String getParams() {
        return getPropertyAsString(PARAMS);
    }

    private String getExpection() {
        return getPropertyAsString(EXPECTION);
    }

    private boolean getUseTemplate() {
        return getPropertyAsBoolean(USE_TEMPLATE, false);
    }

    private String getInterfaceSystem() {
        return getPropertyAsString(INTERFACE_SYSTEM);
    }
}
