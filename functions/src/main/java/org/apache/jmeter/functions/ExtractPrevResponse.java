package org.apache.jmeter.functions;

import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.common.utils.LogUtil;
import org.apache.jmeter.common.utils.json.JsonPathUtil;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * 根据 jsonpath提取上一个 Sampler Response
 *
 * @author Kelvin.Ye
 * @date 2020-04-20 10:08
 */
public class ExtractPrevResponse extends AbstractFunction {

    private static final Logger logger = LogUtil.getLogger(ExtractPrevResponse.class);

    private static final List<String> DESC = new LinkedList<>();

    static {
        DESC.add("根据JsonPath表达式提取上一个SamplerResponse的Json值");
    }

    private static final String KEY = "__ExtractPrevResponse";

    private CompoundVariable jsonPath = null;
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
        checkParameterCount(parameters, 1, 2);

        Object[] params = parameters.toArray();
        int count = params.length;

        if (count > 0) {
            jsonPath = (CompoundVariable) params[0];
        }

        if (count > 1) {
            variable = (CompoundVariable) params[1];
        }
    }

    @Override
    public String execute(SampleResult sampleResult, Sampler sampler) {
        String result = "";
        try {
            JMeterContext context = JMeterContextService.getContext();
            String previousResponse = context.getPreviousResult().getResponseDataAsString();
            String jsonPath = this.jsonPath.execute().trim();
            result = JsonPathUtil.extractAsString(previousResponse, jsonPath);

            if (this.variable != null) {
                String variable = this.variable.execute().trim();
                context.getVariables().put(variable, result);
            }
        } catch (Exception e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
        return result;
    }
}
