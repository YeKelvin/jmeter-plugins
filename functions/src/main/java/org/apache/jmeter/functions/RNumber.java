package org.apache.jmeter.functions;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.common.random.Randoms;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.threads.JMeterContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Kelvin.Ye
 * @date 2020-04-20 19:38
 */
public class RNumber extends AbstractFunction {

    private static final Logger log = LoggerFactory.getLogger(RNumber.class);

    private static final List<String> DESC = new LinkedList<>();

    static {
        DESC.add("随机生成数字");
    }

    private static final String KEY = "__RNumber";

    private static final String ERROR_MSG = (
            "格式错误，例如: ${__RNumber(8)}, ${__RNumber(str:8)}, ${__RNumber(8:str)}, ${__RNumber(str:8:str)}, " +
                    "${__RNumber(8:str:8)}, ${__RNumber(\\8:8)}, ${__RNumber(8, variable)}"
    );

    private CompoundVariable pattern = null;
    private CompoundVariable variable = null;


    @Override
    public String getReferenceKey() {
        return KEY;
    }

    @Override
    public List<String> getArgumentDesc() {
        return DESC;
    }

    @Override
    public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
        checkParameterCount(parameters, 0, 2);

        Object[] params = parameters.toArray();
        int count = params.length;

        if (count > 0) {
            pattern = (CompoundVariable) params[0];
        }
        if (count > 1) {
            variable = (CompoundVariable) params[1];
        }
    }

    @Override
    public String execute(SampleResult sampleResult, Sampler sampler) {
        String result = "";
        try {
            result = getNumber();

            if (this.variable != null) {
                String variable = this.variable.execute().trim();
                JMeterContextService.getContext().getVariables().put(variable, result);
            }
        } catch (Exception e) {
            log.error(ExceptionUtil.getStackTrace(e));
        }
        return result;
    }

    private List<Object> getParams() throws InvalidVariableException {
        List<Object> params = new ArrayList<>();
        String paramPattern = this.pattern.execute().trim();
        String[] paramPatterns = paramPattern.split(":");
        int parameterSize = paramPatterns.length;
        for (String pattern : paramPatterns) {
            if (parameterSize == 1) {
                if (StringUtils.isNumeric(pattern)) {
                    params.add(Integer.parseInt(pattern));
                    break;
                } else {
                    throw new InvalidVariableException(ERROR_MSG);
                }
            }

            if (StringUtils.isNumeric(pattern)) {
                if (pattern.startsWith("\\")) {
                    params.add(pattern.substring(1));
                } else {
                    params.add(Integer.parseInt(pattern));
                }
            } else {
                params.add(pattern);
            }
        }
        return params;
    }

    private String getNumber() throws InvalidVariableException {
        List<Object> params = getParams();
        int parameterSize = params.size();

        if (parameterSize > 3) {
            throw new InvalidVariableException(ERROR_MSG);
        }

        int intTypeCount = 0;
        for (Object param : params) {
            if (param instanceof Integer) {
                intTypeCount++;
            }
        }
        if (intTypeCount == 0) {
            throw new InvalidVariableException(ERROR_MSG);
        }

        if (parameterSize == 3) {
            if (params.get(0) instanceof String) {
                return Randoms.getNumber((String) params.get(0), (int) params.get(1), (String) params.get(2));
            } else {
                return Randoms.getNumber((int) params.get(0), (String) params.get(1), (int) params.get(2));
            }
        }

        if (parameterSize == 2) {
            if (params.get(0) instanceof String) {
                return Randoms.getNumber((String) params.get(0), (int) params.get(1));
            } else {
                return Randoms.getNumber((int) params.get(0), (String) params.get(1));
            }
        }

        return Randoms.getNumber((int) params.get(0));
    }
}
