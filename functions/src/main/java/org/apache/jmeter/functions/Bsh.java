package org.apache.jmeter.functions;

import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.util.BeanShellInterpreter;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import pers.kelvin.util.log.LogUtil;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


public class Bsh extends AbstractFunction {

    private static final Logger logger = LogUtil.getLogger(Bsh.class);

    private static final List<String> desc = new LinkedList<>();

    private static final String KEY = "__Bsh";

    private static final String INIT_FILE = "beanshell.function.init";

    static {
        desc.add("文件名：.e.g fileName.bsh / fileName");
        desc.add("方法名：.e.g className.methodName / methodName");
        desc.add("参数值...：");
    }

    private Object[] values;

    private BeanShellInterpreter bshInterpreter = null;

    @Override
    public synchronized String execute(SampleResult previousResult, Sampler currentSampler)
            throws InvalidVariableException {

        if (bshInterpreter == null) // did we find BeanShell?
        {
            throw new InvalidVariableException("BeanShell not found");
        }

        JMeterContext jmctx = JMeterContextService.getContext();
        JMeterVariables vars = jmctx.getVariables();

        String script = ((CompoundVariable) values[0]).execute();
        String varName = "";
        if (values.length > 1) {
            varName = ((CompoundVariable) values[1]).execute().trim();
        }

        String resultStr = "";
        try {

            // Pass in some variables
            if (currentSampler != null) {
                bshInterpreter.set("Sampler", currentSampler);
            }

            if (previousResult != null) {
                bshInterpreter.set("SampleResult", previousResult);
            }

            // Allow access to context and variables directly
            bshInterpreter.set("ctx", jmctx);
            bshInterpreter.set("vars", vars);
            bshInterpreter.set("props", JMeterUtils.getJMeterProperties());

            // Execute the script
            Object bshOut = bshInterpreter.eval(script);
            if (bshOut != null) {
                resultStr = bshOut.toString();
            }
            if (vars != null && varName.length() > 0) {
                vars.put(varName, resultStr);
            }
        } catch (Exception ex) {
            logger.warn("Error running BSH script", ex);
        }
        logger.debug("__Beanshell({},{})={}", script, varName, resultStr);
        return resultStr;

    }

    @Override
    public synchronized void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {

        checkMinParameterCount(parameters, 2);

        values = parameters.toArray();

        try {
            bshInterpreter = new BeanShellInterpreter(JMeterUtils.getProperty(INIT_FILE), logger);
        } catch (ClassNotFoundException e) {
            throw new InvalidVariableException("BeanShell not found", e);
        }
    }

    @Override
    public String getReferenceKey() {
        return KEY;
    }

    @Override
    public List<String> getArgumentDesc() {
        return desc;
    }

}
