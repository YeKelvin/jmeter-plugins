package org.apache.jmeter.config;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.engine.event.LoopIterationListener;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import pers.kelvin.util.StringUtil;
import pers.kelvin.util.exception.ExceptionUtil;
import pers.kelvin.util.exception.ServiceException;
import pers.kelvin.util.json.JsonFileUtil;
import pers.kelvin.util.json.JsonPathUtil;
import pers.kelvin.util.json.JsonUtil;
import pers.kelvin.util.log.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Dubbo接口报文非空校验自动遍历
 * User: Kelvin.Ye
 * Date: 2018-04-17
 * Time: 11:10
 */
public class TraverseEmptyValue extends ConfigTestElement implements LoopIterationListener {
    private static final Logger logger = LogUtil.getLogger(TraverseEmptyValue.class);

    public static final String BLANK_TYPE = "TraverseEmptyValue.blankType";
    public static final String PATAMS = "TraverseEmptyValue.patams";
    public static final String EMPTY_CHECK_EXPECTATION = "TraverseEmptyValue.emptyCheckExpectation";
    public static final String USE_TEMPLATE = "TraverseEmptyValue.useTemplate";
    public static final String INTERFACE_PATH = "TraverseEmptyValue.interfacePath";
    public static final String INTERFACE_NAME = "TraverseEmptyValue.interfaceName";
    private Iterator jsonPathIterator = null;
    public static final String CONFIG_FILE_PATH = JMeterUtils.getJMeterHome() + File.separator + "config" +
            File.separator + "config.json";


    @Override
    public void iterationStart(LoopIterationEvent event) {
        setVariables();
    }


    /**
     * 循环获取jsonPath、expectation 和 params，并放入vars变量中
     */
    private void setVariables() {
        Iterator iter = getJsonPathIterator();
        if (iter.hasNext()) {
            String jsonPath = (String) iter.next();
            Object isSuccess = JsonPath.read(JsonUtil.toArrayJson(getEmptyCheckExpection()), jsonPath);
            if (isSuccess instanceof Boolean) {
                String expection = String.valueOf(isSuccess);
                getThreadContext().getVariables().put("jsonPath", jsonPath);
                getThreadContext().getVariables().put("expectation", expection);
                putParams(jsonPath);
            } else {
                setVariables();
            }
        } else {
            logger.info("Traverse Empty Check循环结束，线程组停止");
            getThreadContext().getThreadGroup().stop();
        }
    }

    /**
     * 根据JsonPath更新对应的值为null，然后将json字符串并放入变量名为params的jmeter变量中
     */
    private void putParams(String jsonPath) {
        try {
            DocumentContext ctx = JsonPathUtil.jsonParse(JsonUtil.toArrayJson(getPatams()));
            ctx.set(jsonPath, null);
            String jsonStr = ctx.jsonString();
            getThreadContext().getVariables().put("params", jsonStr.substring(1, jsonStr.length() - 1));
        } catch (Exception e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
    }

    /**
     * JsonPathList的迭代器
     */
    private Iterator getJsonPathIterator() {
        if (jsonPathIterator == null) {
            jsonPathIterator = JsonPathUtil.getJsonPathList(JsonUtil.toArrayJson(getEmptyCheckExpection())).iterator();
        }
        return jsonPathIterator;
    }

    /**
     * 获取脚本中的值
     */
    public String getPatams() throws IOException {
        //使testEL元素只读，即不能参数化
        setRunningVersion(false);
        if (getUseTemplate()) {
            String templateJson = readJsonFile();
            if (templateJson == null) {
                throw new ServiceException(String.format("%s json模版获取失败", getInterfaceName()));
            }
            return templateJson;
        }
        return getPropertyAsString(TraverseEmptyValue.PATAMS);
    }

    /**
     * 获取脚本中的值
     */
    public String getEmptyCheckExpection() {
        //使testEL元素只读，即不能参数化
        setRunningVersion(false);
        return getPropertyAsString(TraverseEmptyValue.EMPTY_CHECK_EXPECTATION);
    }

    private boolean getUseTemplate() {
        return getPropertyAsBoolean(USE_TEMPLATE, false);
    }

    private String getInterfaceName() {
        return getPropertyAsString(INTERFACE_NAME);
    }

    private String getInterfaceSystem() {
        return getPropertyAsString(INTERFACE_PATH);
    }

    private String readJsonFile() throws IOException {
        if (StringUtil.isNotBlank(getInterfaceSystem())) {
            return JsonFileUtil.readJsonFile(CONFIG_FILE_PATH, getInterfaceSystem(), getInterfaceName());
        } else {
            return JsonFileUtil.readJsonFile(CONFIG_FILE_PATH, getInterfaceName());
        }
    }


}
