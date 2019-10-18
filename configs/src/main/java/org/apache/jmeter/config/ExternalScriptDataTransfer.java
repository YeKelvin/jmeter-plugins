package org.apache.jmeter.config;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.jmeter.protocol.jdbc.config.DataSourceElement;
import org.apache.jmeter.protocol.jdbc.sampler.JDBCSampler;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.ThreadListener;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.ListedHashTree;
import org.apache.jorphan.collections.SearchByClass;
import org.slf4j.Logger;
import pers.kelvin.util.FileUtil;
import pers.kelvin.util.log.LogUtil;

import java.util.*;

/**
 * Description:
 *
 * @author: KelvinYe
 */
public class ExternalScriptDataTransfer extends ConfigTestElement implements ThreadListener, SampleListener {

    private static final Logger logger = LogUtil.getLogger(ExternalScriptDataTransfer.class);

    private static final String LINE_SEP = FileUtil.LINE_SEPARATOR;

    private Properties props = JMeterUtils.getJMeterProperties();

    private boolean isPrintSampleResultToConsole;

    private HashMap<String, Object> clonedVars;

    private Map<String, Object> scriptData;

    private boolean isSuccess = true;

    private ExternalScriptResultDTO scriptResult;

    private SampleResult errorSampleResult;

    public ExternalScriptDataTransfer() {
        super();
        isPrintSampleResultToConsole = Boolean.valueOf(JMeterUtils.getProperty("printSampleResultToConsole"));
        scriptResult = new ExternalScriptResultDTO();
        scriptData = new HashMap<>();
        clonedVars = new HashMap<>();
    }

    @Override
    public void sampleOccurred(SampleEvent e) {
        SampleResult result = e.getResult();

        // 打印 sample数据到console，用于脚本调试
        if (isPrintSampleResultToConsole) {
            printContentToConsole(result);
        }

        if (!result.isSuccessful()) {
            isSuccess = false;
            errorSampleResult = result;
        }

        // 获取 Sampler运行前后的 JMeterVars的差集
        Collection<Map.Entry<String, Object>> subtract = CollectionUtils.subtract(
                getThreadContext().getVariables().entrySet(), clonedVars.entrySet());

        if (!subtract.isEmpty()) {
            // 把差集结果放入 map对象中
            subtract.forEach(entry -> scriptData.put(entry.getKey(), entry.getValue()));
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
        if (props.containsKey("callerVars")) {
            // 把调用者线程的 vars复制一份到当前线程的 vars中，同名key不覆盖
            JMeterVariables callerVars = (JMeterVariables) props.get("callerVars");
            JMeterVariables currentVars = getThreadContext().getVariables();

            // 获取 callerVars 和 currentVars的差集
            Collection<Map.Entry<String, Object>> subtract = CollectionUtils.subtract(callerVars.entrySet(), currentVars.entrySet());

            if (!subtract.isEmpty()) {
                subtract.forEach(e -> {
                    if (e.getValue() instanceof String) {
                        currentVars.put(e.getKey(), (String) e.getValue());
                    } else {
                        currentVars.putObject(e.getKey(), e.getValue());
                    }
                });
            }
        }

        // 保存 JMeterVars副本
        getThreadContext().getVariables().entrySet().forEach(e -> clonedVars.put(e.getKey(), e.getValue()));
    }

    @Override
    public void threadFinished() {
        // 将增量的 JMeterVars写入 ExternalScriptResultDTO中返回给调用者
        scriptResult.setSuccess(isSuccess);
        scriptResult.setExternalData(scriptData);
        scriptResult.setErrorSampleResult(errorSampleResult);
        props.put("externalScriptResult", scriptResult);
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
     * 打印 sample数据到console
     *
     * @param result SampleResult对象
     */
    private void printContentToConsole(SampleResult result) {
        String content = String.format("【Sample Name】: %s" + LINE_SEP +
                        "【Request Data】:" + LINE_SEP +
                        "%s" + LINE_SEP +
                        "【Response Data】:" + LINE_SEP +
                        "%s" + LINE_SEP + LINE_SEP,
                result.getSampleLabel(), result.getSamplerData(), result.getResponseDataAsString());
        System.out.println(content);
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
        givenMap.remove("JMeterThread.last_sample_ok");
        givenMap.remove("__jm__" + getThreadContext().getThreadGroup().getName() + "__idx");
        givenMap.remove("__jmeter.U_T__");
        getKeyNameInJDBCRequest(getThreadContext().getThread().getTestTree()).forEach(givenMap::remove);
        getKeyNameInJDBCDataSource(getThreadContext().getThread().getTestTree()).forEach(givenMap::remove);
    }
}
