package org.apache.jmeter.functions;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.slf4j.Logger;
import org.apache.jmeter.common.utils.Signature;
import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.common.utils.LogUtil;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * 报文加签
 *
 * @author KelvinYe
 */
public class Sign extends AbstractFunction {

    private static final Logger logger = LogUtil.getLogger(Sign.class);

    /**
     * 自定义function的描述
     */
    private static final List<String> DESC = new LinkedList<>();

    static {
        DESC.add("Sign Prefix:");

    }

    /**
     * function名称
     */
    private static final String KEY = "__Sign";

    /**
     * function传入的参数
     */
    private CompoundVariable prefix;

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
        prefix = (CompoundVariable) parameters.toArray()[0];
    }

    /**
     * function的执行主体
     */
    @Override
    public synchronized String execute(SampleResult sampleResult, Sampler currentSampler) {
        String sign = "";
        if (currentSampler instanceof HTTPSamplerProxy) {
            String prefixStr = prefix.execute().trim();

            // 获取HTTP Sampler post body中的内容
            HTTPSamplerProxy httpSamplerProxy = (HTTPSamplerProxy) currentSampler;
            Arguments args = httpSamplerProxy.getArguments();

            logger.debug("current sign http url={}", getUrl(httpSamplerProxy));
            logger.debug("sign prefix={}", prefixStr);

            // 报文加签
            sign = Signature.sign(args.getArgument(0).getValue(), prefixStr);
        } else {
            logger.error("函数 __Sign目前仅支持在 HTTP Sampler下及其子组件下使用");
        }
        return sign;
    }

    private String getUrl(HTTPSamplerProxy httpSamplerProxy) {
        String url = "";
        try {
            url = httpSamplerProxy.getUrl().toString();
        } catch (MalformedURLException e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
        return url;
    }
}
