package org.apache.jmeter.functions;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.slf4j.Logger;
import pers.kelvin.util.Signature;
import pers.kelvin.util.log.LogUtil;

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
        DESC.add("HTTP请求报文加签");
        DESC.add("Json报文按照keyName排序后进行md5加密");
        DESC.add("目前仅支持在HTTP Sampler下的配置元件、前后置处理器中使用");
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
    public String execute(SampleResult sampleResult, Sampler sampler) {
        String signStr = "";
        if (sampler instanceof HTTPSampler) {
            String prefixStr = prefix.execute().trim();
            HTTPSampler httpSampler = (HTTPSampler) sampler;
            // 获取HTTP Sampler post body中的内容
            Arguments args = httpSampler.getArguments();
            // 报文加签
            signStr = Signature.sign(args.getArgument(0).getValue(), prefixStr);
        }
        return signStr;
    }
}
