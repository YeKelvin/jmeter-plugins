package org.apache.jmeter.functions;

import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.common.utils.LogUtil;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Kelvin.Ye
 * @date 2020-04-20 19:38
 */
public class ExtractSQLValue extends AbstractFunction {

    private static final Logger logger = LogUtil.getLogger(ExtractSQLValue.class);

    private static final List<String> DESC = new LinkedList<>();

    static {
        DESC.add("根据列名提取数据库表第一行的值");
    }

    private static final String KEY = "__ExtractSQLValue";

    private CompoundVariable tableName = null;
    private CompoundVariable columnName = null;
    private CompoundVariable defaultValue = null;
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
        checkParameterCount(parameters, 3, 4);

        Object[] params = parameters.toArray();
        int count = params.length;

        tableName = (CompoundVariable) params[0];
        columnName = (CompoundVariable) params[1];
        defaultValue = (CompoundVariable) params[2];


        if (count > 3) {
            variable = (CompoundVariable) params[3];
        }
    }

    @Override
    public String execute(SampleResult sampleResult, Sampler sampler) {
        String result = "";
        try {
            String tableName = this.tableName.execute().trim();
            String columnName = this.columnName.execute().trim();
            String defaultValue = this.defaultValue.execute().trim();
            JMeterVariables variables = JMeterContextService.getContext().getVariables();
            @SuppressWarnings("unchecked")
            String columnValue = String.valueOf(
                    ((List<Map<String, Object>>) variables.getObject(tableName)).get(0).get(columnName)
            );
            if (columnValue == null || columnValue.isEmpty()) {
                result = defaultValue;
            }
            result = columnValue;

            if (this.variable != null) {
                String variable = this.variable.execute().trim();
                variables.put(variable, result);
            }
        } catch (Exception e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
        return result;
    }
}
