package org.apache.jmeter.config;

import com.google.gson.reflect.TypeToken;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import pers.kelvin.util.exception.ExceptionUtil;
import pers.kelvin.util.json.JsonUtil;
import pers.kelvin.util.log.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * @author: KelvinYe
 * Date: 2018-04-08
 * Time: 17:11
 */
public class ENVDataSet extends ConfigTestElement implements TestStateListener {

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
        return JMeterUtils.getPropDefault("configName", getPropertyAsString(CONFIG_NAME));
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
        if (isEnvFile(filePath)) {
            try (
                    FileInputStream input = new FileInputStream(filePath);
                    InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8.name())
            ) {
                envMap = JsonUtil.getGsonInstance().fromJson(reader, hashMap);
            } catch (Exception e) {
                logger.error(ExceptionUtil.getStackTrace(e));
            }
        } else {
            logger.error("{}非 .env文件", filePath);
        }
        return envMap;
    }

    /**
     * 判断文件后缀是否为env
     *
     * @param filePath 文件路径
     */
    private Boolean isEnvFile(String filePath) {
        File file = new File(filePath);
        return file.isFile() && filePath.endsWith("env");
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
