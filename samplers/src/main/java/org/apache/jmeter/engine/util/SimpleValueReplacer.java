package org.apache.jmeter.engine.util;

import org.apache.jmeter.functions.InvalidVariableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Map;

/**
 * @author Kaiwen.Ye
 */
public class SimpleValueReplacer {

    private static final Logger log = LoggerFactory.getLogger(SimpleValueReplacer.class);

    private static final FunctionParser FUNCTION_PARSER = new FunctionParser();

    private final Map<String, String> variables;

    private LinkedList<Object> compiledComponents;

    public SimpleValueReplacer(Map<String, String> variables) {
        this.variables = variables;
    }

    public String replace() {
        if (compiledComponents == null || compiledComponents.isEmpty()) {
            return "";
        }

        StringBuilder results = new StringBuilder();
        for (Object item : compiledComponents) {
            results.append(item);
        }
        return results.toString();
    }

    public void setParameters(String parameters) throws InvalidVariableException {
        if (parameters == null || parameters.length() == 0) {
            return;
        }

        LinkedList<Object> newCompiledComponents = new LinkedList<>();
        compiledComponents = FUNCTION_PARSER.compileString(parameters);
        for (Object item : compiledComponents) {
            if (item instanceof SimpleVariable) {
                SimpleVariable simpleVar = (SimpleVariable) item;
                SimpleVariableNoContext newSimpleVar = new SimpleVariableNoContext(this.variables);
                newSimpleVar.setName(simpleVar.getName());
                newCompiledComponents.add(newSimpleVar);
            } else {
                newCompiledComponents.add(item);
            }
        }
        compiledComponents = newCompiledComponents;
    }

}
