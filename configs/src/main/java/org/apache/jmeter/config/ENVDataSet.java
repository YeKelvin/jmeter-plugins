package org.apache.jmeter.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import pers.kelvin.util.ExceptionUtil;
import pers.kelvin.util.log.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * User: KelvinYe
 * Date: 2018-04-08
 * Time: 17:11
 */
public class ENVDataSet extends ConfigTestElement implements TestStateListener {
    private static final Logger logger = LogUtil.getLogger(ENVDataSet.class);
    public static final String CONFIG_NAME = "ConfigName";

    public ENVDataSet() {
        super();
    }

    public String getFileName() {
        return JMeterUtils.getPropDefault("configName", getPropertyAsString(ENVDataSet.CONFIG_NAME));
    }

    public String getFilePath() {
        return JMeterUtils.getJMeterHome() + File.separator + "config" + File.separator + getFileName();
    }

    public HashMap<String, String> getEnvMap(String filePath) {
        HashMap<String, String> envMap = new HashMap<>();
        try {
            if (isEnvFile(filePath)) {
                FileInputStream input = new FileInputStream(filePath);
                InputStreamReader reader = new InputStreamReader(input, Charset.forName("UTF-8"));
                Type hashMap = new TypeToken<HashMap<String, String>>() {
                }.getType();
                envMap = new Gson().fromJson(reader, hashMap);
                reader.close();
                input.close();
            } else {
                logger.error("{}非\".env\"后缀配置文件", filePath);
            }
        } catch (IOException e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
        return envMap;
    }

    private Boolean isEnvFile(String filePath) {
        File file = new File(filePath);
        return file.isFile() && filePath.endsWith("env");
    }

    @Override
    public void testStarted() {
        testStarted("local");
    }

    @Override
    public void testStarted(String s) {
        // 把测试环境配置文件名添加到jmeter线程变量中
        JMeterContextService.getContext().getVariables().put(CONFIG_NAME, getFileName());
        // 将配置文件中的所有属性逐一添加到jmeter线程变量中
        HashMap<String, String> envMap = getEnvMap(getFilePath());
        for (Map.Entry<String, String> entry : envMap.entrySet()) {
            JMeterContextService.getContext().getVariables().put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void testEnded() {
        testEnded("local");
    }

    @Override
    public void testEnded(String s) {

    }
}
