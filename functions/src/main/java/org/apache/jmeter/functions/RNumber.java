package org.apache.jmeter.functions;

import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.common.utils.LogUtil;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.threads.JMeterContextService;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class RNumber extends AbstractFunction {

    private static final Logger logger = LogUtil.getLogger(RNumber.class);

    private static final List<String> DESC = new LinkedList<>();

    static {
        DESC.add("随机柬埔寨手机号");
    }

    private static final String KEY = "__RNumber";

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
            logger.error(ExceptionUtil.getStackTrace(e));
        }
        return result;
    }

    private ArrayList<Object> getParams() throws InvalidVariableException {
        ArrayList<Object> params = new ArrayList<>();
        String paramPattern = this.pattern.execute().trim();
        String[] paramPatterns = paramPattern.split(":");
        for (String pattern : paramPatterns) {
            if (!pattern.startsWith("%s") || !pattern.startsWith("%d")) {
                throw new InvalidVariableException("");
            }
            if (pattern.startsWith("%s")) {
                String param = pattern.substring(2);
                params.add(param);
            } else if (pattern.startsWith("%d")) {
                int param = Integer.parseInt(pattern.substring(2));
                params.add(param);
            }
        }
        return params;
    }

    private String getNumber() throws InvalidVariableException {
        ArrayList<Object> params = getParams();
        return "";
    }
}
