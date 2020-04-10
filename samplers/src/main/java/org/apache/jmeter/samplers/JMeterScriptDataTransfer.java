package org.apache.jmeter.samplers;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.jmeter.common.utils.LogUtil;
import org.apache.jmeter.engine.util.NoThreadClone;
import org.apache.jmeter.protocol.jdbc.config.DataSourceElement;
import org.apache.jmeter.protocol.jdbc.sampler.JDBCSampler;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.ThreadListener;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.ListedHashTree;
import org.apache.jorphan.collections.SearchByClass;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * Description:
 *
 * @author: KelvinYe
 */
public class JMeterScriptDataTransfer extends AbstractTestElement
        implements ThreadListener, SampleListener, NoThreadClone {

    private static final Logger logger = LogUtil.getLogger(JMeterScriptDataTransfer.class);

    public static final String CALLER_VARIABLES = "JMeterScriptDataTransfer.callerVariables";
    public static final String SCRIPT_RESULT = "JMeterScriptDataTransfer.scriptResult";

    private Properties props = JMeterUtils.getJMeterProperties();

    private HashMap<String, Object> clonedVars;

    private Map<String, Object> scriptData;  // todo incrementalVariables

    private SampleResult parentResult;

    private JMeterScriptResultDTO scriptResult;  // todo 去掉

    public JMeterScriptDataTransfer(SampleResult parentResult) {
        super();
        this.scriptData = new HashMap<>();
        this.clonedVars = new HashMap<>();
        this.scriptResult = new JMeterScriptResultDTO();
        this.parentResult = parentResult;
    }

    @Override
    public void sampleOccurred(SampleEvent e) {
        SampleResult result = e.getResult();
        parentResult.addSubResult(result, false);

        if (!result.isSuccessful()) {
            parentResult.setSuccessful(false);
        }

        // 获取 Sampler运行前后局部变量的差集
        Collection<Map.Entry<String, Object>> differenceSet = getThreadVariablesDifferenceSet();

        if (!differenceSet.isEmpty()) {
            // 存储差集
            differenceSet.forEach(entry -> scriptData.put(entry.getKey(), entry.getValue()));
            // 删除不必要的key
            removeUnwantedKey(scriptData);
        }

    }

    @Override
    public void sampleStarted(SampleEvent e) {
    }

    @Override
    public void sampleStopped(SampleEvent e) {
    }

    @Override
    public void threadStarted() {
        if (props.containsKey(CALLER_VARIABLES)) {
            // 把调用者的局部变量复制到当前线程的局部变量中，同名 key不覆盖
            JMeterVariables callerVars = (JMeterVariables) props.get(CALLER_VARIABLES);
            JMeterVariables currentVars = getThreadContext().getVariables();

            // 获取调用者的局部变量和当前线程的局部变量的差集
            Collection<Map.Entry<String, Object>> differenceSet =
                    CollectionUtils.subtract(callerVars.entrySet(), currentVars.entrySet());

            if (!differenceSet.isEmpty()) {
                differenceSet.forEach(e -> {
                    if (e.getValue() instanceof String) {
                        currentVars.put(e.getKey(), (String) e.getValue());
                    } else {
                        currentVars.putObject(e.getKey(), e.getValue());
                    }
                });
            }
        }

        // 克隆当前线程的局部变量作为副本
        getThreadContext().getVariables().entrySet().forEach(e -> clonedVars.put(e.getKey(), e.getValue()));
    }

    @Override
    public void threadFinished() {
        scriptResult.setExternalData(scriptData);
        props.put(SCRIPT_RESULT, scriptResult);
    }

    private Collection<Map.Entry<String, Object>> getThreadVariablesDifferenceSet() {
        return CollectionUtils.subtract(getThreadContext().getVariables().entrySet(), clonedVars.entrySet());
    }

    /**
     * 获取JDBC Request中的 resultVariable变量名称，用于删除该变量
     */
    private ArrayList<String> getKeyNameInJDBCRequest(ListedHashTree testTree) {
        ArrayList<String> jdbcKeyNameList = new ArrayList<>();
        SearchByClass<JDBCSampler> searcher = new SearchByClass<>(JDBCSampler.class);
        testTree.traverse(searcher);
        for (JDBCSampler jdbcSampler : searcher.getSearchResults()) {
            jdbcKeyNameList.add(jdbcSampler.getResultVariable());
        }
        return jdbcKeyNameList;
    }

    /**
     * 获取JDBC DataSource中的 DataSource变量名称，用于删除该变量
     */
    private ArrayList<String> getKeyNameInJDBCDataSource(ListedHashTree testTree) {
        ArrayList<String> jdbcDataSourceNameList = new ArrayList<>();
        SearchByClass<DataSourceElement> searcher = new SearchByClass<>(DataSourceElement.class);
        testTree.traverse(searcher);
        for (DataSourceElement sourceElement : searcher.getSearchResults()) {
            jdbcDataSourceNameList.add(sourceElement.getDataSource());
        }
        return jdbcDataSourceNameList;
    }

    /**
     * 删除不需要的key
     */
    private void removeUnwantedKey(Map<String, Object> givenMap) {
        givenMap.remove("START.MS");
        givenMap.remove("START.YMD");
        givenMap.remove("START.HMS");
        givenMap.remove("TESTSTART.MS");
        givenMap.remove("JMeterThread.pack");
        givenMap.remove("__jm__" + getThreadContext().getThreadGroup().getName() + "__idx");
        givenMap.remove("__jmeter.U_T__");
        getKeyNameInJDBCRequest(getThreadContext().getThread().getTestTree()).forEach(givenMap::remove);
        getKeyNameInJDBCDataSource(getThreadContext().getThread().getTestTree()).forEach(givenMap::remove);
    }
}
