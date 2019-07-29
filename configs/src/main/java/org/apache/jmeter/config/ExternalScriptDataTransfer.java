package org.apache.jmeter.config;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.jmeter.protocol.jdbc.config.DataSourceElement;
import org.apache.jmeter.protocol.jdbc.sampler.JDBCSampler;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.testelement.ThreadListener;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.ListedHashTree;
import org.apache.jorphan.collections.SearchByClass;
import org.slf4j.Logger;
import pers.kelvin.util.FileUtil;
import pers.kelvin.util.json.JsonUtil;
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

    private int threadGroupSampleCount;

    private int completedSampleCount;

    private boolean isExecuteSuccess = true;

    private SampleResult errorSampleResult;

    public ExternalScriptDataTransfer() {
        super();
        isPrintSampleResultToConsole = Boolean.valueOf(JMeterUtils.getProperty("printSampleResultToConsole"));
        clonedVars = new HashMap<>();
    }

    @Override
    public void sampleOccurred(SampleEvent e) {
        SampleResult result = e.getResult();

        // 统计已完成的 sample的数量
        completedSampleCount++;

        // 打印 sample数据到console，用于脚本调试
        if (isPrintSampleResultToConsole) {
            printContentToConsole(result);
        }

        if (!result.isSuccessful()) {
            isExecuteSuccess = false;
            errorSampleResult = result;
        }

        // 当所有 sample执行完毕时，把增量的 JMeterVars写入 JMeterProps中，用于在线程组间传递数据
        if (threadGroupSampleCount == completedSampleCount || !result.isSuccessful()) {
            // 获取 ThreadGroup运行前后的 JMeterVars的差集
            Collection<Map.Entry<String, Object>> subtract = CollectionUtils.subtract(
                    getThreadContext().getVariables().entrySet(), clonedVars.entrySet());

            Map<String, Object> sentToPropsMap = new HashMap<>();
            if (!subtract.isEmpty()) {
                // 把差集结果放入临时 map对象中
                subtract.forEach(entry -> sentToPropsMap.put(entry.getKey(), entry.getValue()));

                // 删除不必要的key
                removeUnwantedKey(sentToPropsMap);
                logger.debug("sentToPropsMap after removeUnwantedKey=" + JsonUtil.toJson(sentToPropsMap));
            }

            // 将增量的 JMeterVars写入 ExternalScriptResultDTO中返回给调用者
            ExternalScriptResultDTO scriptResult = new ExternalScriptResultDTO();
            scriptResult.setExecuteSuccess(isExecuteSuccess);
            scriptResult.setExternalScriptData(sentToPropsMap);
            scriptResult.setErrorSampleResult(errorSampleResult);
            props.put("externalScriptResult", scriptResult);
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
        // 获取当前线程组下的 sample数量， sample总数 = 线程组 sample数 * 线程组循环数
        threadGroupSampleCount = getSampleCount(getThreadContext().getThread().getTestTree());

        // 保存 JMeterVars副本
        getThreadContext().getVariables().entrySet().forEach(e -> clonedVars.put(e.getKey(), e.getValue()));
    }

    @Override
    public void threadFinished() {
        // 线程组执行结束时清理数据
        isExecuteSuccess = true;
        errorSampleResult = null;
        isPrintSampleResultToConsole = false;
        threadGroupSampleCount = 0;
        completedSampleCount = 0;
        clonedVars.clear();
    }

    /**
     * 获取 ListedHashTree中的 sample数量
     */
    private int getSampleCount(ListedHashTree testTree) {
        SearchByClass<Sampler> searcher = new SearchByClass<>(Sampler.class);
        testTree.traverse(searcher);
        return searcher.getSearchResults().size();
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
