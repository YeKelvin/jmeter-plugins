package org.apache.jmeter.config;

import org.apache.commons.lang3.StringUtils;
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
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * HTTP请求头文件读取器
 *
 * @author Kelvin.Ye
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
                Map<String, String> headerMap = getHeaderVariables(getHeaderFilePath());
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

    public String getHeaderFilePath() {
        return JMeterUtils.getJMeterHome() + File.separator + "header" + File.separator + getHeadersFileName();
    }

    /**
     * 反序列化配置文件
     */
    private Map<String, String> getHeaderVariables(String filePath) {
        Map<String, String> variables = new HashMap<>();
        try {
            YamlUtil.parseYamlAsMap(filePath).forEach((key, value) -> {
                if (StringUtils.isBlank(key)) {
                    return;
                }
                if (value != null) {
                    variables.put(key, value.toString());
                } else {
                    variables.put(key, "");
                }
            });
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
    public void testStarted(String host) {
        if (!alreadyAddedGui) {
            try {
                modifyAppliableConfigClassesField();
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

    /**
     * 修改HTTPSampler的APPLIABLE_CONFIG_CLASSES属性
     */
    @SuppressWarnings("unchecked")
    private void modifyAppliableConfigClassesField() throws NoSuchFieldException, IllegalAccessException {
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
    }
}
