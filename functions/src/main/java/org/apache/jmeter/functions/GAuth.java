package org.apache.jmeter.functions;

import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.slf4j.Logger;
import pers.kelvin.util.GoogleAuthenticator;
import pers.kelvin.util.exception.ExceptionUtil;
import pers.kelvin.util.log.LogUtil;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * 获取谷歌动态认证码
 *
 * @author KelvinYe
 */
public class GAuth extends AbstractFunction {
    private static final Logger logger = LogUtil.getLogger(GAuth.class);

    /**
     * 自定义function的描述
     */
    private static final List<String> DESC = new LinkedList<>();

    static {
        DESC.add("Google Secret Key:");
    }

    /**
     * function名称
     */
    private static final String KEY = "__GAuth";

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
        String googleAuthCode = "";
        String secret = secretKey.execute().trim();
        logger.debug("Google Secret Key=" + secret);
        try {
            googleAuthCode = GoogleAuthenticator.getCode(secret);
            logger.debug("Google Auth Code=" + googleAuthCode);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
        return googleAuthCode;
    }
}
