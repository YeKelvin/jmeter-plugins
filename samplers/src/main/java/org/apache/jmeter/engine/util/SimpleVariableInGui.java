package org.apache.jmeter.engine.util;

import java.util.Map;

/**
 * @author Kaiwen.Ye
 */
public class SimpleVariableInGui extends SimpleVariable {

    private final Map<String, String> variables;

    public SimpleVariableInGui(Map<String, String> variables) {
        this.variables = variables;
    }

    @Override
    public String toString() {
        String ret = null;

        if (variables != null) {
            ret = variables.get(getName());
        }

        if (ret == null) {
            return "${" + getName() + "}";
        }

        return ret;
    }
}
