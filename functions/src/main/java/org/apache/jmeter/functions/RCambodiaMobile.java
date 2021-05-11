package org.apache.jmeter.functions;

import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.common.utils.Randoms;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.threads.JMeterContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Kelvin.Ye
 * @date 2020-04-20 19:38
 */
public class RCambodiaMobile extends AbstractFunction {

    private static final Logger log = LoggerFactory.getLogger(RCambodiaMobile.class);

    private static final List<String> DESC = new LinkedList<>();

    static {
        DESC.add("随机柬埔寨手机号");
    }

    private static final String KEY = "__RCambodiaMobile";

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
        checkParameterCount(parameters, 0, 1);

        Object[] params = parameters.toArray();
        int count = params.length;

        if (count > 0) {
            variable = (CompoundVariable) params[0];
        }
    }

    @Override
    public String execute(SampleResult sampleResult, Sampler sampler) {
        String result = "";
        try {
            result = Randoms.getCambodiaMobileNumber();

            if (this.variable != null) {
                String variable = this.variable.execute().trim();
                JMeterContextService.getContext().getVariables().put(variable, result);
            }
        } catch (Exception e) {
            log.error(ExceptionUtil.getStackTrace(e));
        }
        return result;
    }
}