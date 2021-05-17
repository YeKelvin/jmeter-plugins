package org.apache.jmeter.config;

import org.apache.jmeter.common.cli.CliOptions;
import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.common.utils.YamlUtil;
import org.apache.jmeter.engine.util.NoConfigMerge;
import org.apache.jmeter.engine.util.NoThreadClone;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author  Kelvin.Ye
 * @date    2018-04-08 17:11
 */
public class EnvDataSet extends ConfigTestElement implements TestStateListener, NoThreadClone, NoConfigMerge {

    private static final Logger log = LoggerFactory.getLogger(EnvDataSet.class);

    public static final String CONFIG_NAME = "EnvDataSet.configName";

    public EnvDataSet() {
        super();
    }

    /**
     * 获取环境变量配置文件名称
     */
    public String getConfigName() {
        String configName = JMeterUtils.getPropDefault(CliOptions.CONFIG_NAME, getPropertyAsString(CONFIG_NAME));
        log.debug("configName:[ {} ]", configName);
        return configName;
    }

    /**
     * 获取环境变量配置文件路径
     */
    public String getConfigPath() {
        String configPath = JMeterUtils.getJMeterHome() + File.separator + "config" + File.separator + getConfigName();
        log.debug("configPath:[ {} ]", configPath);
        return configPath;
    }

    /**
     * 反序列化配置文件
     */
    private Map<String, String> getEnvironmentVariables(String filePath) {
        Map<String, String> variables = new HashMap<>();
        try {
            YamlUtil.parseYamlAsMap(filePath).forEach((key, value) -> variables.put(key, value.toString()));
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            log.error(ExceptionUtil.getStackTrace(e));
        }
        return variables;
    }

    @Override
    public void testStarted() {
        testStarted("localhost");
    }

    @Override
    public void testStarted(String s) {
        // 添加配置文件名到JMeter变量中
        JMeterContextService.getContext().getVariables().put(CONFIG_NAME, getConfigName());
        // 反序列化配置文件，添加所有配置变量到JMeter变量中
        Map<String, String> envVariables = getEnvironmentVariables(getConfigPath());
        envVariables.forEach((key, value) -> getThreadContext().getVariables().put(key, value));
    }

    @Override
    public void testEnded() {
        testEnded("localhost");
    }

    @Override
    public void testEnded(String s) {
    }

}
