package org.apache.jmeter.config;

import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;

/**
 * @author Kelvin.Ye
 * @date 2021-05-13 00:20
 */
public class ScriptArgumentsDescriptor extends Arguments implements TestStateListener {
    @Override
    public void testStarted() {
        testStarted("localhost");
    }

    @Override
    public void testStarted(String host) {
        JMeterVariables variables = JMeterContextService.getContext().getVariables();

        for (JMeterProperty prop : this) {
            Argument arg = (Argument) prop.getObjectValue();
            Object value = arg.getProperty(Argument.VALUE).getObjectValue();
            if (value instanceof CompoundVariable) {
                variables.put(arg.getName(), ((CompoundVariable) value).execute());
            }
        }
    }

    @Override
    public void testEnded() {
        testEnded("localhost");
    }

    @Override
    public void testEnded(String host) {

    }
}
