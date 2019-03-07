package org.apache.jmeter.config;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.engine.event.LoopIterationListener;
import org.slf4j.Logger;
import pers.kelvin.util.json.JsonPathUtil;
import pers.kelvin.util.json.JsonUtil;
import pers.kelvin.util.log.LogUtil;

import java.util.Iterator;

/**
 * dubbo接口报文非空校验自动遍历
 * User: KelvinYe
 * Date: 2018-04-17
 * Time: 11:10
 */
public class TraverseEmptyValue extends ConfigTestElement implements LoopIterationListener {
    private static final Logger logger = LogUtil.getLogger(TraverseEmptyValue.class);

    public static final String USE_TEMPLATE = "TraverseEmptyValue.UseTemplate";
    public static final String INTERFACE_SYSTEM = "TraverseEmptyValue.InterfaceSystem";
    public static final String PATAMS = "Patams";
    public static final String EMPTY_CHECK_EXPECTION = "EmptyCheckExpection";
    private Iterator jsonPathIterator = null;


    @Override
    public void iterationStart(LoopIterationEvent event) {
        setVariables();
    }


    /**
     * 循环获取jsonPath、expection 和 params，并放入vars变量中
     */
    private void setVariables() {
        Iterator iter = getJsonPathIterator();
        if (iter.hasNext()) {
            String jsonPath = (String) iter.next();
            Object isSuccess = JsonPath.read(JsonUtil.toArrayJson(getEmptyCheckExpection()), jsonPath);
            if (isSuccess instanceof Boolean) {
                String expection = String.valueOf(isSuccess);
                getThreadContext().getVariables().put("jsonPath", jsonPath);
                getThreadContext().getVariables().put("expection", expection);
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
        DocumentContext ctx = JsonPathUtil.jsonParse(JsonUtil.toArrayJson(getPatams()));
        ctx.set(jsonPath, null);
        String jsonStr = ctx.jsonString();
        getThreadContext().getVariables().put("params", jsonStr.substring(1, jsonStr.length() - 1));
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
    public String getPatams() {
        //使testEL元素只读，即不能参数化
        setRunningVersion(false);
        return getPropertyAsString(TraverseEmptyValue.PATAMS);
    }

    /**
     * 获取脚本中的值
     */
    public String getEmptyCheckExpection() {
        //使testEL元素只读，即不能参数化
        setRunningVersion(false);
        return getPropertyAsString(TraverseEmptyValue.EMPTY_CHECK_EXPECTION);
    }

}
