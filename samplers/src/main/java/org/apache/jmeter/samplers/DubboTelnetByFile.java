package org.apache.jmeter.samplers;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jayway.jsonpath.DocumentContext;
import com.jcraft.jsch.JSchException;
import groovy.lang.Binding;
import org.apache.jmeter.engine.util.ValueReplacer;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.utils.GroovyUtil;
import org.apache.jmeter.samplers.utils.TelnetUtil;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import pers.kelvin.util.StringUtil;
import pers.kelvin.util.exception.ExceptionUtil;
import pers.kelvin.util.exception.ServiceException;
import pers.kelvin.util.json.JsonFileUtil;
import pers.kelvin.util.json.JsonPathUtil;
import pers.kelvin.util.json.JsonUtil;
import pers.kelvin.util.log.LogUtil;
import pers.kelvin.util.ssh.SSHTelnetClient;

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

    public static final String ADDRESS = "DubboTelnetByFile.Address";
    public static final String INTERFACE_NAME = "DubboTelnetByFile.InterfaceName";
    public static final String PARAMS = "DubboTelnetByFile.Params";
    public static final String JSON_PATHS = "DubboTelnetByFile.JsonPaths";
    public static final String EXPECTATION = "DubboTelnetByFile.Expectation";
    public static final String ENCODE = "DubboTelnetByFile.Encode";
    public static final String USE_TEMPLATE = "DubboTelnetByFile.UseTemplate";
    public static final String INTERFACE_SYSTEM = "DubboTelnetByFile.InterfaceSystem";
    public static final String TEMPLATE_CONTENT = "DubboTelnetByFile.TemplateContent";
    private static final String REPLACE_VALUE = "DubboTelnetByFile.ReplaceValue";
    public static final String CONFIG_FILE_PATH = JMeterUtils.getJMeterHome() + File.separator + "config" +
            File.separator + "config.json";
    private static final int defaultTimeout = 5000;

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
            result.setSamplerData(getInterfaceName() + "(" + requestData + ")");
            result.sampleStart();
            responseData = invokeDubbo(getAddress(), getInterfaceName(), requestData);
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
     * 获取请求报文
     *
     * @param params      参数
     * @param useTemplate 是否使用模版
     * @return 请求报文
     */
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

    /**
     * 根据JsonPaths替换模版json的值
     *
     * @param data json模版
     */
    private String transformDataByJsonPaths(String data) {
        String jsonPaths = getJsonPaths();
        if (StringUtil.isNotBlank(jsonPaths)) {
            try {
                Type hashMapType = new TypeToken<HashMap<String, Object>>() {
                }.getType();
                HashMap<String, Object> jsonPathMap = JsonUtil.getGsonInstance().fromJson(jsonPaths, hashMapType);
                DocumentContext ctx = JsonPathUtil.jsonParse(JsonUtil.toArrayJson(data));
                for (Map.Entry<String, Object> entry : jsonPathMap.entrySet()) {
                    ctx.set(entry.getKey(), entry.getValue());
                }
                data = ctx.jsonString();
                data = data.substring(1, data.length() - 1);
            } catch (Exception e) {
                logger.error("jsonPaths格式必须为 {\"jsonPath\":\"newValue\",\"jsonPath\":\"newValue\"...}");
            }
        }
        // jsonPaths为空时原值返回
        return data;
    }

    /**
     * 根据 Params替换模版 json的值，且将 jmeter的${}占位符替换为真值
     *
     * @param params jmeter Params入参
     */
    private String transformParams(String params) throws IOException {
        String requestData = "";
        // 根据接口名获取json模版
        String templateJson = readJsonFile();
        if (templateJson == null) {
            throw new ServiceException(String.format("%s json模版获取失败", getInterfaceName()));
        }
        if (StringUtil.isNotBlank(params)) {
            // 根据params转换为如要替换json模版的jsonPath列表
            List<String> jsonPathList = JsonPathUtil.getJsonPathList(JsonUtil.toArrayJson(params));
            // 解析json
            DocumentContext paramsDCtx = JsonPathUtil.jsonParse(JsonUtil.toArrayJson(params));
            DocumentContext templateJsonDCtx = JsonPathUtil.jsonParse(JsonUtil.toArrayJson(templateJson));
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

    private String readJsonFile() throws IOException {
        if (StringUtil.isNotBlank(getInterfaceSystem())) {
            return JsonFileUtil.readJsonFile(CONFIG_FILE_PATH, getInterfaceSystem(), getInterfaceName());
        } else {
            return JsonFileUtil.readJsonFile(CONFIG_FILE_PATH, getInterfaceName());
        }
    }

    /**
     * 获取JMeter系统变量
     *
     * @param map
     */
    private void putAllProps(Map<String, String> map) {
        Properties props = JMeterUtils.getJMeterProperties();
        for (String keyName : props.stringPropertyNames()) {
            map.put(keyName, props.getProperty(keyName));
        }
    }

    /**
     * 获取JMeter线程变量
     *
     * @param map
     */
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
     * telnetDubbo invoke dubbo接口
     *
     * @param address       地址，格式为host:port
     * @param interfaceName 接口名称
     * @param requestData   请求数据
     * @return 响应报文
     */
    private String invokeDubbo(String address, String interfaceName, String requestData) throws IOException, JSchException {
        // 分割地址，格式为host:port
        String[] addressArray = address.split(":");
        String host = addressArray[0];
        String port = addressArray.length == 1 ? "0000" : addressArray[1];

        if (isSSHTelnet()) {
            return sshTelnetInvoke(host, port, interfaceName, requestData);
        } else {
            return telnetInvoke(host, port, interfaceName, requestData);
        }
    }

    /**
     * telnet直连服务器
     *
     * @param host          地址
     * @param port          端口号
     * @param interfaceName 接口名称
     * @param requestData   请求数据
     * @return 响应报文
     * @throws IOException 输入输出流异常
     */
    private String telnetInvoke(String host, String port, String interfaceName, String requestData) throws IOException {
        TelnetUtil telnet = new TelnetUtil(host, port, getEncode());
        String response = telnet.invokeDubbo(interfaceName, requestData);
        telnet.disconnect();
        return response;
    }

    /**
     * 先ssh连接跳板机后再telnet服务器
     *
     * @param host          地址
     * @param port          端口号
     * @param interfaceName 接口名称
     * @param requestData   请求数据
     * @return 响应报文
     * @throws IOException   输入输出流异常
     * @throws JSchException ssh连接异常
     */
    private String sshTelnetInvoke(String host, String port, String interfaceName, String requestData)
            throws IOException, JSchException {
        // 分割地址，格式为host:port
        String[] sshAddressArray = getSSHAddress().split(":");
        String sshHost = sshAddressArray[0];
        String sshPort = sshAddressArray.length == 1 ? "22" : sshAddressArray[1];

        SSHTelnetClient telnet = new SSHTelnetClient(sshHost, Integer.valueOf(sshPort),
                getSSHUserName(), getSSHPassword(), getEncode(), defaultTimeout);
        telnet.telnetDubbo(host, port);
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

    /**
     * 数据验证
     */
    private void verifyData() {
        if (StringUtil.isBlank(getAddress())) {
            throw new ServiceException("address 服务器地址不能为空");
        }
        if (StringUtil.isBlank(getInterfaceName())) {
            throw new ServiceException("interfaceName 接口名称不能为空");
        }
        if (!getUseTemplate() && StringUtil.isBlank(getParams())) {
            throw new ServiceException("不使用json模版时，params 请求参数不能为空");
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
        return getPropertyAsString(EXPECTATION);
    }

    private String getEncode() {
        return StringUtil.isBlank(getPropertyAsString(ENCODE)) ? "UTF-8" : getPropertyAsString(ENCODE);
    }

    private boolean getUseTemplate() {
        return getPropertyAsBoolean(USE_TEMPLATE, false);
    }

    private String getInterfaceSystem() {
        return getPropertyAsString(INTERFACE_SYSTEM);
    }

    private String getSSHAddress() {
        return JMeterUtils.getProperty("sshAddress");
    }

    private String getSSHUserName() {
        return JMeterUtils.getProperty("sshUserName");
    }

    private String getSSHPassword() {
        return JMeterUtils.getProperty("sshPassword");
    }

    private boolean isSSHTelnet() {
        return Boolean.valueOf(JMeterUtils.getPropDefault("isSSHConnect", "false"));
    }
}
