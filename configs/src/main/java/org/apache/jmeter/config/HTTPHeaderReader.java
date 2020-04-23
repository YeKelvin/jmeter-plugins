package org.apache.jmeter.config;

import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.common.utils.LogUtil;
import org.apache.jmeter.common.utils.YamlUtil;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class HTTPHeaderReader extends HeaderManager {

    private static final long serialVersionUID = 2303087080392055094L;

    private static final Logger logger = LogUtil.getLogger(HTTPHeaderReader.class);

    public static final String FILE_NAME = "HTTPHeaderReader.fileName";

    private boolean init = false;

    public HTTPHeaderReader() {
        super();
    }

    @Override
    public CollectionProperty getHeaders() {
        init();
        return super.getHeaders();
    }

    @Override
    public Header getHeader(int row) {
        init();
        return super.getHeader(row);
    }

    @Override
    public Header get(int i) {
        init();
        return super.get(i);
    }

    @Override
    public HeaderManager merge(TestElement element) {
        init();
        return super.merge(element);
    }

    private void init() {
        if (!init) {
            CollectionProperty headers = getHeaders();
            Map<String, String> headerMap = getHeaderMap(getFilePath());
            headerMap.forEach((name, value) -> {
                Header header = new Header(name, value);
                headers.addItem(header);
            });
            init = true;
        }
    }

    public String getFileName() {
        return getPropertyAsString(FILE_NAME);
    }

    public String getFilePath() {
        return JMeterUtils.getJMeterHome() + File.separator + "header" + File.separator + getFileName();
    }

    public Map<String, String> getHeaderMap(String filePath) {
        File file = new File(filePath);

        Map<String, String> envMap = new HashMap<>();
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
}
