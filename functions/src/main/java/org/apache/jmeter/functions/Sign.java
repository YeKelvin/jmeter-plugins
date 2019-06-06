package org.apache.jmeter.functions;

import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.util.JMeterUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Sign extends AbstractFunction {
    /**
     * 自定义function的描述
     */
    private static final List<String> DESC = new LinkedList<>();

    static {
        DESC.add("Json报文按照keyName排序后MD5加密");
    }

    /**
     * function名称
     */
    private static final String KEY = "__Sign";

    /**
     * function传入的参数的值
     */
    private Object[] values = null;

    /**
     * function引用关键字
     */
    @Override
    public String getReferenceKey() {
        return KEY;
    }

    /**
     * function描述
     */
    @Override
    public List<String> getArgumentDesc() {
        return DESC;
    }

    /**
     * 设置function参数
     */
    @Override
    public void setParameters(Collection<CompoundVariable> parameters) {
        //将入參的实际值存入values中
        values = parameters.toArray();
    }

    /**
     * function执行
     */
    @Override
    public String execute(SampleResult sampleResult, Sampler sampler) throws InvalidVariableException {
//        try {
//            String path = JMeterUtils.getJMeterHome();
//            if (values.length != 0) {
//                for (Object value : values) {
//                    String childPath = ((CompoundVariable) value).execute().trim();
//                    path = pathJoin(path, childPath);
//                }
//            }
//            return path.replace("\\", "/");
//        } catch (Exception ex) {
//            throw new InvalidVariableException(ex);
//        }
        return "";
    }
}
