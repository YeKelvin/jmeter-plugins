package org.apache.jmeter.config;

import com.google.gson.reflect.TypeToken;
import org.apache.jmeter.common.CliOption;
import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.common.utils.LogUtil;
import org.apache.jmeter.common.utils.YamlUtil;
import org.apache.jmeter.engine.util.NoConfigMerge;
import org.apache.jmeter.engine.util.NoThreadClone;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: KelvinYe
 * Date: 2018-04-08
 * Time: 17:11
 */
public class ENVDataSet extends ConfigTestElement implements TestStateListener, NoThreadClone, NoConfigMerge {

    private static final Logger logger = LogUtil.getLogger(ENVDataSet.class);

    private static final Type hashMap = new TypeToken<HashMap<String, String>>() {
    }.getType();

    public static final String CONFIG_NAME = "ENVDataSet.configName";

    public ENVDataSet() {
        super();
    }

    /**
     * 获取环境变量配置文件名称
     */
    public String getFileName() {
        return JMeterUtils.getPropDefault(CliOption.CONFIG_NAME, getPropertyAsString(CONFIG_NAME));
    }

    /**
     * 获取环境变量配置文件路径
     */
    public String getFilePath() {
        return JMeterUtils.getJMeterHome() + File.separator + "config" + File.separator + getFileName();
    }

    /**
     * 读取json文件转换为HashMap
     *
     * @param filePath 文件路径
     */
    public HashMap<String, String> getEnvMap(String filePath) {
        HashMap<String, String> envMap = new HashMap<>();
        File file = new File(filePath);
        // 判断是否为 yaml文件
        if (file.isFile() && filePath.endsWith("yaml")) {
            try {
                envMap = parseYaml(file);
            } catch (Exception e) {
                logger.error(ExceptionUtil.getStackTrace(e));
            }
        } else {
            logger.error("{} 非 yaml文件", filePath);
        }
        return envMap;
    }

    private HashMap<String, String> parseYaml(File file) {
        HashMap<String, String> map = new HashMap<>();
        @SuppressWarnings("unchecked")
        Map<String, Object> yamlMap = (Map<String, Object>) YamlUtil.parseYaml(file);
        if (yamlMap != null) {
            yamlMap.forEach((key, value) -> {
                map.put(key, String.valueOf(value));
            });
        }
        return map;
    }

    @Override
    public void testStarted() {
        testStarted("localhost");
    }

    @Override
    public void testStarted(String s) {
        // 把测试环境配置文件名添加到jmeter变量中
        JMeterContextService.getContext().getVariables().put(CONFIG_NAME, getFileName());
        // 将配置文件中的所有属性逐一添加到jmeter变量中
        HashMap<String, String> envMap = getEnvMap(getFilePath());
        envMap.forEach((key, value) -> getThreadContext().getVariables().put(key, value));
    }

    @Override
    public void testEnded() {
        testEnded("localhost");
    }

    @Override
    public void testEnded(String s) {
    }

}
