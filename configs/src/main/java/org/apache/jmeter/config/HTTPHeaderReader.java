package org.apache.jmeter.config;

import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.common.utils.LogUtil;
import org.apache.jmeter.common.utils.YamlUtil;
import org.apache.jmeter.engine.util.ValueReplacer;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.samplers.SampleMonitor;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 未完成不可用
 */
public class HTTPHeaderReader extends AbstractTestElement implements SampleMonitor {

    private static final Logger logger = LogUtil.getLogger(HTTPHeaderReader.class);

    public static final String COMMON_FILE_NAME = "HTTPHeaderReader.commonFileName";
    public static final String FILE_NAME = "HTTPHeaderReader.fileName";

    private static final ValueReplacer replacer = new ValueReplacer();

    public HTTPHeaderReader() {
    }

    public String getFileName() {
        return getPropertyAsString(FILE_NAME);
    }

    public String getFilePath() {
        return JMeterUtils.getJMeterHome() + File.separator + "header" + File.separator + getFileName();
    }

    public Map<String, String> getHeaderMap(String filePath) {
        File file = new File(filePath);

        Map<String, String> headerMap = new HashMap<>();
        // 判断是否为 yaml文件
        if (file.isFile() && filePath.endsWith("yaml")) {
            try {
                headerMap = parseYaml(file);
            } catch (Exception e) {
                logger.error(ExceptionUtil.getStackTrace(e));
            }
        } else {
            logger.error("{} 非 yaml文件", filePath);
        }
        return headerMap;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> parseYaml(File file) {
        Map<String, String> map = new HashMap<>();
        Map<String, Object> yamlMap = (Map<String, Object>) YamlUtil.parseYaml(file);
        if (yamlMap != null) {
            yamlMap.forEach((key, value) -> {
                map.put(key, String.valueOf(value));
            });
        }
        return map;
    }

    @Override
    public void sampleStarting(Sampler sampler) {
        if (sampler instanceof HTTPSamplerProxy) {
            HTTPSamplerProxy httpSampler = (HTTPSamplerProxy) sampler;
            HeaderManager headerManager = httpSampler.getHeaderManager();
        }
    }

    @Override
    public void sampleEnded(Sampler sampler) {

    }
}
