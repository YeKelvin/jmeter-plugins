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
        return JMeterUtils.getPropDefault(CliOptions.CONFIG_NAME, getPropertyAsString(CONFIG_NAME));
    }

    /**
     * 获取环境变量配置文件路径
     */
    public String getConfigPath() {
        return JMeterUtils.getJMeterHome() + File.separator + "config" + File.separator + getConfigName();
    }

    /**
     * 读取json文件转换为HashMap
     *
     * @param filePath 文件路径
     */
//    public HashMap<String, String> getEnvironmentVariables(String filePath) {
//        HashMap<String, String> envMap = new HashMap<>();
//        File file = new File(filePath);
//        // 判断是否为 yaml文件
//        if (file.isFile() && filePath.endsWith("yaml")) {
//            try {
//                envMap = parseYaml(file);
//            } catch (Exception e) {
//                log.error(ExceptionUtil.getStackTrace(e));
//            }
//        } else {
//            log.error("{} 非 yaml文件", filePath);
//        }
//        return envMap;
//    }

//    private HashMap<String, String> parseYaml(File file) {
//        HashMap<String, String> map = new HashMap<>();
//        @SuppressWarnings("unchecked")
//        Map<String, Object> yamlMap = (Map<String, Object>) YamlUtil.parseYaml(file);
//        if (yamlMap != null) {
//            yamlMap.forEach((key, value) -> {
//                map.put(key, String.valueOf(value));
//            });
//        }
//        return map;
//    }

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
