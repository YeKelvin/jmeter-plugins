package org.apache.jmeter.samplers;

import com.jcraft.jsch.JSchException;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.common.jmeter.JMeterVariablesUtil;
import org.apache.jmeter.common.exceptions.ServiceException;
import org.apache.jmeter.common.ssh.SSHTelnetClient;
import org.apache.jmeter.config.SSHConfiguration;
import org.apache.jmeter.engine.util.ValueReplacer;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.utils.TelnetUtil;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author  Kelvin.Ye
 * @date    2019-02-22 11:47
 */
public class DubboTelnetSampler extends AbstractSampler {

    private static final Logger log = LoggerFactory.getLogger(DubboTelnetSampler.class);

    public static final String ADDRESS = "DubboTelnetSampler.address";
    public static final String INTERFACE_NAME = "DubboTelnetSampler.interfaceName";
    public static final String EXPECTATION = "DubboTelnetSampler.expectation";
    public static final String ENCODE = "DubboTelnetSampler.encode";
    public static final String PARAMS = "DubboTelnetSampler.params";
    public static final String THROUGH_SSH = "DubboTelnetSampler.throughSSH";

    private static final String REPLACE_VALUE = "DubboTelnetSampler.replaceValue";

    private static final int DEFAULT_TIMEOUT = 5000;

    @Override
    public SampleResult sample(Entry entry) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.setEncodingAndType(StandardCharsets.UTF_8.name());
        boolean isSuccess = false;
        String responseData = "";
        try {
            // 入参数据验证
            verifyData();
            String interfaceName = getInterfaceName();
            String requestData = getParams();
            result.setSamplerData(interfaceName + "(" + requestData + ")");
            result.sampleStart();
            responseData = invokeDubbo(getAddress(), interfaceName, requestData);
            isSuccess = getSuccessful(responseData, getExpectation());
        } catch (Exception e) {
            // 异常后，判断是否已开始统计sample时间，没有则开始统计
            if (result.isStampedAtStart()) {
                result.sampleStart();
            }
            responseData = ExceptionUtil.getStackTrace(e);
        } finally {
            result.sampleEnd();
            result.setSuccessful(isSuccess);
            result.setResponseData(responseData, StandardCharsets.UTF_8.name());
        }

        return result;
    }

    /**
     * 获取JMeter系统变量
     **/
    private void putAllProps(Map<String, String> map) {
        Properties props = JMeterUtils.getJMeterProperties();
        for (String keyName : props.stringPropertyNames()) {
            map.put(keyName, props.getProperty(keyName));
        }
    }

    /**
     * 获取JMeter线程变量
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
     * 控制TestElement的runningVersion，替换${}占位符为真值
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
            log.error(ExceptionUtil.getStackTrace(e));
        }
        return value;
    }

    /**
     * telnetDubbo invoke dubbo接口
     *
     * @param dubboAddress  地址，格式为host:port
     * @param interfaceName 接口名称
     * @param requestData   请求数据
     * @return 响应报文
     */
    private String invokeDubbo(String dubboAddress, String interfaceName, String requestData)
            throws IOException, JSchException {
        // 分割地址，格式为host:port
        String[] address = dubboAddress.split(":");
        String dubboHost = address[0];
        String dubboPort = address.length == 1 ? "0000" : address[1];

        if (throughSSH()) {
            return telnetInvokeBySSH(dubboHost, dubboPort, interfaceName, requestData);
        } else {
            return telnetInvoke(dubboHost, dubboPort, interfaceName, requestData);
        }
    }

    /**
     * telnet直连服务器
     *
     * @param dubboHost     地址
     * @param dubboPort     端口号
     * @param interfaceName 接口名称
     * @param requestData   请求数据
     * @return 响应报文
     * @throws IOException 输入输出流异常
     */
    private String telnetInvoke(String dubboHost, String dubboPort, String interfaceName, String requestData)
            throws IOException {
        TelnetUtil telnet = new TelnetUtil(dubboHost, dubboPort, getEncode(), DEFAULT_TIMEOUT);
        String response = telnet.invokeDubbo(interfaceName, requestData);
        telnet.disconnect();
        return response;
    }

    /**
     * 先ssh连接跳板机后再telnet服务器
     *
     * @param dubboHost     地址
     * @param dubboPort     端口号
     * @param interfaceName 接口名称
     * @param requestData   请求数据
     * @return 响应报文
     * @throws IOException   输入输出流异常
     * @throws JSchException ssh连接异常
     */
    private String telnetInvokeBySSH(String dubboHost, String dubboPort, String interfaceName, String requestData)
            throws IOException, JSchException {
        // 分割地址，格式为host:port
        String[] sshAddressArray = getSSHAddress().split(":");
        String sshHost = sshAddressArray[0];
        int sshPort = Integer.parseInt(sshAddressArray.length == 1 ? "22" : sshAddressArray[1]);

        SSHTelnetClient telnet = new SSHTelnetClient(sshHost, sshPort, getSSHUserName(), getSSHPassword(),
                getEncode(), DEFAULT_TIMEOUT);
        telnet.telnetDubbo(dubboHost, dubboPort);
        String response = telnet.invokeDubbo(interfaceName, requestData);
        telnet.disconnect();
        return response;
    }

    /**
     * 判断expection预期结果是否为逻辑表达式，
     * 否则预期结果判断逻辑为responseData响应报文中是否包含expection预期结果的值，包含为true，不包含为false。
     *
     * @param responseData 响应报文
     * @param expectation  预期结果
     * @return ture | false
     */
    private boolean getSuccessful(String responseData, String expectation) {
        if (StringUtils.isBlank(expectation)) {
            return true;
        }
        return responseData.contains(expectation);
    }

    /**
     * 数据验证
     */
    private void verifyData() {
        if (StringUtils.isBlank(getAddress())) {
            throw new ServiceException("服务器地址不能为空");
        }
        if (StringUtils.isBlank(getInterfaceName())) {
            throw new ServiceException("接口名称不能为空");
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

    private String getExpectation() {
        return getPropertyAsString(EXPECTATION, "");
    }

    private String getEncode() {
        return JMeterVariablesUtil.getDefault(ENCODE, StandardCharsets.UTF_8.name());
    }

    private String getSSHAddress() {
        return JMeterVariablesUtil.getDefault(SSHConfiguration.SSH_ADDRESS, "");
    }

    private String getSSHUserName() {
        return JMeterVariablesUtil.getDefault(SSHConfiguration.SSH_USER_NAME, "");
    }

    private String getSSHPassword() {
        return JMeterVariablesUtil.getDefault(SSHConfiguration.SSH_PASSWORD, "");
    }

    private boolean throughSSH() {
        return JMeterUtils.getPropDefault(
                "throughSSH", JMeterVariablesUtil.getDefaultAsBoolean(THROUGH_SSH, false));
    }
}
