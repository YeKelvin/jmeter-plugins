package org.apache.jmeter.config;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.common.utils.LogUtil;
import org.apache.jmeter.common.utils.json.JsonPathUtil;
import org.apache.jmeter.common.utils.json.JsonUtil;
import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.engine.event.LoopIterationListener;
import org.slf4j.Logger;

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
    public static final String EMPTY_CHECK_EXPRESSION = "TraversalEmptyValue.emptyCheckExpression";

    private Iterator<String> jsonPathIterator = null;


    @Override
    public void iterationStart(LoopIterationEvent event) {
        setVariables();
    }


    /**
     * 循环获取jsonPath、expression 和 params，并放入vars变量中
     */
    private void setVariables() {
        Iterator<String> iter = getJsonPathIterator();
        if (iter.hasNext()) {
            String jsonPath = iter.next();
            Object isSuccess = JsonPath.read(JsonUtil.toArrayJson(getEmptyCheckExpection()), jsonPath);
            if (isSuccess instanceof Boolean) {
                String expection = String.valueOf(isSuccess);
                getThreadContext().getVariables().put("jsonPath", jsonPath);
                getThreadContext().getVariables().put("expression", expection);
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
        return getPropertyAsString(TraversalEmptyValue.PATAMS);
    }

    /**
     * 获取脚本中的值
     */
    public String getEmptyCheckExpection() {
        //使testEL元素只读，即不能参数化
        setRunningVersion(false);
        return getPropertyAsString(TraversalEmptyValue.EMPTY_CHECK_EXPRESSION);
    }

    private String getBlankType() {
        return getPropertyAsString(BLANK_TYPE);
    }

}
