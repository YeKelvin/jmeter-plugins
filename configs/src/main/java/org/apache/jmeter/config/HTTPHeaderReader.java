package org.apache.jmeter.config;

import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.common.utils.YamlUtil;
import org.apache.jmeter.config.gui.HTTPHeaderReaderGui;
import org.apache.jmeter.engine.util.ValueReplacer;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author KelvinYe
 *
 * HTTP请求头文件读取器
 */
public class HTTPHeaderReader extends HeaderManager implements TestStateListener {

    private static final Logger log = LoggerFactory.getLogger(HTTPHeaderReader.class);

    public static final String HEADER_FILE_NAME = "HTTPHeaderReader.headerFileName";

    private static final ValueReplacer REPLACER = new ValueReplacer();

    private static boolean alreadyAddedGui;

    private boolean alreadyRead;

    public HTTPHeaderReader() {
        super();
    }

    public void init() {
        if (!alreadyRead) {
            try {
                Map<String, String> headerMap = getHeaderMap(getHeadersFilePath());
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    Header header = new Header(entry.getKey(), entry.getValue());
                    REPLACER.replaceValues(header);
                    header.setRunningVersion(true);
                    super.getHeaders().addItem(header);
                }
                alreadyRead = true;
            } catch (Exception e) {
                log.error(ExceptionUtil.getStackTrace(e));
            }
        }
    }

    @Override
    public Object clone() {
        HTTPHeaderReader clone = (HTTPHeaderReader) super.clone();
        clone.alreadyRead = alreadyRead;
        return clone;
    }

    @Override
    public CollectionProperty getHeaders() {
        init();
        return super.getHeaders();
    }

    public String getHeadersFileName() {
        return getPropertyAsString(HEADER_FILE_NAME);
    }

    public String getHeadersFilePath() {
        return JMeterUtils.getJMeterHome() + File.separator + "header" + File.separator + getHeadersFileName();
    }

    public Map<String, String> getHeaderMap(String filePath) {
        File file = new File(filePath);

        Map<String, String> headerMap = new HashMap<>();
        // 判断是否为 yaml文件
        if (file.isFile() && filePath.endsWith("yaml")) {
            try {
                headerMap = parseYaml(file);
            } catch (Exception e) {
                log.error(ExceptionUtil.getStackTrace(e));
            }
        } else {
            log.error("{} 非 yaml文件", filePath);
        }
        return headerMap;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> parseYaml(File file) {
        Map<String, String> map = new HashMap<>();
        Map<String, Object> yamlMap = (Map<String, Object>) YamlUtil.parseYaml(file);
        if (yamlMap != null) {
            yamlMap.forEach((key, value) -> map.put(key, String.valueOf(value)));
        }
        return map;
    }

    @Override
    public void testStarted() {
        testStarted("localhost");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void testStarted(String host) {
        if (!alreadyAddedGui) {
            try {
                Class<?> clazz = HTTPSamplerBase.class;
                HTTPSamplerProxy httpSampler = new HTTPSamplerProxy();

                Field appliableConfigClassesField = clazz.getDeclaredField("APPLIABLE_CONFIG_CLASSES");
                appliableConfigClassesField.setAccessible(true);

                Field modifiers = Field.class.getDeclaredField("modifiers");
                modifiers.setAccessible(true);
                modifiers.setInt(appliableConfigClassesField, appliableConfigClassesField.getModifiers() & ~Modifier.FINAL);

                Set<String> APPLIABLE_CONFIG_CLASSES = (Set<String>) appliableConfigClassesField.get(httpSampler);
                APPLIABLE_CONFIG_CLASSES.add(HTTPHeaderReaderGui.class.getName());
                appliableConfigClassesField.set(httpSampler, APPLIABLE_CONFIG_CLASSES);

                modifiers.setInt(appliableConfigClassesField, appliableConfigClassesField.getModifiers() & ~Modifier.FINAL);

                alreadyAddedGui = true;
            } catch (Exception e) {
                log.error(ExceptionUtil.getStackTrace(e));
            }
        }
    }

    @Override
    public void testEnded() {
        testEnded("localhost");
    }

    @Override
    public void testEnded(String host) {
        alreadyAddedGui = false;
    }

    @Override
    public int replace(String regex, String replaceBy, boolean caseSensitive) {
        return 0;
    }
}
