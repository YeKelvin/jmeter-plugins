package org.apache.jmeter.functions;

import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.common.google.GoogleAuthenticator;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * 获取谷歌动态认证码
 *
 * @author Kelvin.Ye
 * @date 2019-06-19 19:42
 */
public class GoogleAuth extends AbstractFunction {

    private static final Logger log = LoggerFactory.getLogger(GoogleAuth.class);

    /**
     * 自定义function的描述
     */
    private static final List<String> DESC = new LinkedList<>();

    static {
        DESC.add("谷歌动态认证码");
    }

    /**
     * function名称
     */
    private static final String KEY = "__GoogleAuth";

    /**
     * function传入的参数
     */
    private CompoundVariable secretKey;

    /**
     * function的引用关键字
     */
    @Override
    public String getReferenceKey() {
        return KEY;
    }

    /**
     * function的描述
     */
    @Override
    public List<String> getArgumentDesc() {
        return DESC;
    }

    /**
     * 设置function的入参
     */
    @Override
    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        // 检查参数个数
        checkParameterCount(parameters, 1, 1);
        secretKey = (CompoundVariable) parameters.toArray()[0];
    }

    /**
     * function的执行主体
     */
    @Override
    public synchronized String execute(SampleResult sampleResult, Sampler sampler) {
        String result = "";
        String secret = secretKey.execute().trim();
        log.debug("Google Secret Key={}", secret);
        try {
            result = GoogleAuthenticator.getCode(secret);
        } catch (Exception e) {
            log.error(ExceptionUtil.getStackTrace(e));
        }
        return result;
    }
}
