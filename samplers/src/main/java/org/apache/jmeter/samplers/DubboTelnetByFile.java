package org.apache.jmeter.samplers;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jayway.jsonpath.DocumentContext;
import groovy.lang.Binding;
import org.apache.jmeter.engine.util.ValueReplacer;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.utils.GroovyUtil;
import org.apache.jmeter.samplers.utils.JsonFileUtil;
import org.apache.jmeter.samplers.utils.TelnetUtil;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import pers.kelvin.util.StringUtil;
import pers.kelvin.util.exception.ExceptionUtil;
import pers.kelvin.util.exception.ServiceException;
import pers.kelvin.util.json.JsonPathUtil;
import pers.kelvin.util.json.JsonUtil;
import pers.kelvin.util.log.LogUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
    public static final String JSON_PATHS = "jsonPaths";
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
            // 入参数据验证
            verifyData();
            String requestData = getRequestData(getParams(), getUseTemplate());
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

    private String getRequestData(String params, boolean useTemplate) throws IOException {
        // 用户设置使用模版且params不为空时才取json模版
        if (useTemplate) {
            String requestData = transformParams(params);
            requestData = transformDataByJsonPaths(requestData);
            return requestData;
        }
        // 不使用json模版时原值返回
        return params;
    }

    private String transformDataByJsonPaths(String data) {
        String jsonPaths = getJsonPaths();
        if (StringUtil.isNotBlank(jsonPaths)) {
            try {
                Type hashMapType = new TypeToken<HashMap<String, Object>>() {
                }.getType();
                HashMap<String, Object> jsonPathMap = JsonUtil.getGsonInstance().fromJson(jsonPaths, hashMapType);
                DocumentContext ctx = JsonPathUtil.jsonParse(toArrayJson(data));
                for (Map.Entry<String, Object> entry : jsonPathMap.entrySet()) {
                    ctx.set(entry.getKey(), entry.getValue());
                }
                data = ctx.jsonString();
                data = data.substring(1, data.length() - 1);
            } catch (Exception e) {
                logger.error("jsonPaths格式必须为 {\"jsonPath\",\"newValue\"...}");
            }
        }
        // jsonPaths为空时原值返回
        return data;
    }

    /**
     * 参数化${}占位符
     *
     * @param params json报文
     * @return 参数化替换后的json报文
     */
    private String transformParams(String params) throws IOException {
        String requestData = "";
        // 根据接口名获取json模版
        String templateJson = JsonFileUtil.readJsonFile(CONFIG_FILE_PATH, getInterfaceName());
        if (templateJson == null) {
            throw new ServiceException(String.format("%s json模版获取失败", getInterfaceName()));
        }
        if (StringUtil.isNotBlank(params)) {
            // 根据params转换为如要替换json模版的jsonPath列表
            List<String> jsonPathList = JsonPathUtil.getJsonPathList(toArrayJson(params));
            // 解析json
            DocumentContext paramsDCtx = JsonPathUtil.jsonParse(toArrayJson(params));
            DocumentContext templateJsonDCtx = JsonPathUtil.jsonParse(toArrayJson(templateJson));
            // 根据params替换json模版
            for (String jsonPath : jsonPathList) {
                Object newValue = paramsDCtx.read(jsonPath);
                if (newValue instanceof JsonObject) {
                    continue;
                }
                templateJsonDCtx.set(jsonPath, newValue);
            }
            requestData = templateJsonDCtx.jsonString();
            requestData = requestData.substring(1, requestData.length() - 1);
        } else {
            requestData = templateJson;
        }
        return replaceValue(requestData);
    }


    /**
     * 在json字符串最外层添加“[]”，转换为ArrayJson
     */
    public String toArrayJson(String json) {
        return "[" + json + "]";
    }

    private void putAllProps(Map<String, String> map) {
        Properties props = JMeterUtils.getJMeterProperties();
        for (String keyName : props.stringPropertyNames()) {
            map.put(keyName, props.getProperty(keyName));
        }
    }

    private void putAllVars(Map<String, String> map) {
        JMeterVariables vars = JMeterContextService.getContext().getVariables();
        for (Map.Entry<String, Object> var : vars.entrySet()) {
            map.put(var.getKey(), String.valueOf(var.getValue()));
        }
    }

    private Map<String, String> getUserVariables() {
        Map<String, String> map = new HashMap<>();
        putAllProps(map);
        putAllVars(map);
        return map;
    }

    /**
     * 控制TestElement的runningVersion，替换为${}占位符真值
     */
    private String replaceValue(String value) {
        setProperty(REPLACE_VALUE, value);
        ValueReplacer replacer = new ValueReplacer();
        replacer.setUserDefinedVariables(getUserVariables());
        try {
            replacer.replaceValues(this);
            setRunningVersion(true);
            return getPropertyAsString(REPLACE_VALUE);
        } catch (InvalidVariableException e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
        return value;
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
        String response = telnet.invokeDubbo(interfaceName, requestData);
        telnet.disconnect();
        return response;
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

    private void verifyData() {
        if (StringUtil.isBlank(getAddress())) {
            throw new ServiceException("address 服务地址不能为空");
        }
        if (StringUtil.isBlank(getInterfaceName())) {
            throw new ServiceException("interfaceName 接口名不能为空");
        }
        if (!getUseTemplate() && StringUtil.isBlank(getParams())) {
            throw new ServiceException("不使用json模版时，params 参数不能为空");
        }
        if (StringUtil.isBlank(getExpection())) {
            throw new ServiceException("expection 预期结果不能为空");
        }
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

    private String getJsonPaths() {
        return getPropertyAsString(JSON_PATHS);
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
