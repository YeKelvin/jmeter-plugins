package org.apache.jmeter.config;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.engine.event.LoopIterationListener;
import org.slf4j.Logger;
import org.apache.jmeter.common.utils.StringUtil;
import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.common.utils.exception.ServiceException;
import org.apache.jmeter.common.utils.json.JsonFileUtil;
import org.apache.jmeter.common.utils.json.JsonPathUtil;
import org.apache.jmeter.common.utils.json.JsonUtil;
import org.apache.jmeter.common.utils.LogUtil;

import java.io.IOException;
import java.util.Iterator;

/**
 * Json报文自动遍历非空校验
 * @author Kelvin.Ye
 * Date: 2018-04-17
 * Time: 11:10
 */
public class TraversalEmptyValue extends ConfigTestElement implements LoopIterationListener {

    private static final Logger logger = LogUtil.getLogger(TraversalEmptyValue.class);

    public static final String BLANK_TYPE = "TraversalEmptyValue.blankType";
    public static final String PATAMS = "TraversalEmptyValue.patams";
    public static final String EMPTY_CHECK_EXPECTATION = "TraversalEmptyValue.emptyCheckExpectation";
    public static final String USE_TEMPLATE = "TraversalEmptyValue.useTemplate";
    public static final String INTERFACE_PATH = "TraversalEmptyValue.interfacePath";
    public static final String INTERFACE_NAME = "TraversalEmptyValue.interfaceName";

    private Iterator<String> jsonPathIterator = null;


    @Override
    public void iterationStart(LoopIterationEvent event) {
        setVariables();
    }


    /**
     * 循环获取jsonPath、expectation 和 params，并放入vars变量中
     */
    private void setVariables() {
        Iterator<String> iter = getJsonPathIterator();
        if (iter.hasNext()) {
            String jsonPath = iter.next();
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
     * 根据JsonPath更新对应的值为 null或 ""，然后将json字符串并放入变量名为params的jmeter变量中
     */
    private void putParams(String jsonPath) {
        try {
            DocumentContext ctx = JsonPathUtil.jsonParse(JsonUtil.toArrayJson(getPatams()));
            if ("null".equals(getBlankType())) {
                ctx.set(jsonPath, null);
            } else {
                ctx.set(jsonPath, "");
            }
            String jsonStr = ctx.jsonString();
            getThreadContext().getVariables().put("params", jsonStr.substring(1, jsonStr.length() - 1));
        } catch (Exception e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
    }

    /**
     * JsonPathList的迭代器
     */
    private Iterator<String> getJsonPathIterator() {
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
        return getPropertyAsString(TraversalEmptyValue.PATAMS);
    }

    /**
     * 获取脚本中的值
     */
    public String getEmptyCheckExpection() {
        //使testEL元素只读，即不能参数化
        setRunningVersion(false);
        return getPropertyAsString(TraversalEmptyValue.EMPTY_CHECK_EXPECTATION);
    }

    private boolean getUseTemplate() {
        return getPropertyAsBoolean(USE_TEMPLATE, false);
    }

    private String getInterfaceName() {
        return getPropertyAsString(INTERFACE_NAME);
    }

    private String getInterfacePath() {
        return getPropertyAsString(INTERFACE_PATH);
    }

    private String getBlankType() {
        return getPropertyAsString(BLANK_TYPE);
    }

    private String readJsonFile() throws IOException {
        String interfaceDir = getInterfacePath();
        String interfaceName = getInterfaceName();

        if (StringUtil.isBlank(interfaceDir)) {
            throw new ServiceException("接口路径不允许为空");
        }
        // 根据入參 interfacePath递归搜索获取绝对路径
        String path = JsonFileUtil.findInterfacePathByKeywords(interfaceDir, interfaceName);
        if (path == null) {
            throw new ServiceException(String.format("\"%s\" 接口模版不存在", interfaceName));
        }
        // 根据绝对路径获取json模版内容
        return JsonFileUtil.readJsonFileToString(path);
    }
}
