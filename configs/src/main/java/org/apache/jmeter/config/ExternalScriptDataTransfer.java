package org.apache.jmeter.config;

import org.apache.commons.collections4.CollectionUtils;
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
            String content = String.format("【Sample Name】: %s" + LINE_SEP +
                            "【Request Data】:" + LINE_SEP +
                            "%s" + LINE_SEP +
                            "【Response Data】:" + LINE_SEP +
                            "%s" + LINE_SEP + LINE_SEP,
                    result.getSampleLabel(), result.getSamplerData(), result.getResponseDataAsString());
            System.out.println(content);
        }

        if (!result.isSuccessful()) {
            props.put("isExecuteSuccess", false);
            props.put("errorSampleResult", result);
        }

        // 当所有 sample执行完毕时，把增量的 JMeterVars写入 JMeterProps中，用于在线程组间传递数据
        if (threadGroupSampleCount == completedSampleCount) {
            // 获取 ThreadGroup运行前后的 JMeterVars的差集
            Collection<Map.Entry<String, Object>> subtract = CollectionUtils.subtract(
                    getThreadContext().getVariables().entrySet(), clonedVars.entrySet());

            if (!subtract.isEmpty()) {
                HashMap<String, Object> sentToPropsMap = new HashMap<>();
                subtract.forEach(entry -> sentToPropsMap.put(entry.getKey(), entry.getValue()));

                // 删除不必要的key
                sentToPropsMap.remove("START.MS");
                sentToPropsMap.remove("START.YMD");
                sentToPropsMap.remove("START.HMS");
                sentToPropsMap.remove("TESTSTART.MS");
                sentToPropsMap.remove("JMeterThread.pack");
                sentToPropsMap.remove("JMeterThread.last_sample_ok");
                sentToPropsMap.remove("__jm__" + getThreadContext().getThreadGroup().getName() + "__idx");
                sentToPropsMap.remove("__jmeter.U_T__");
                getKeyNameInJDBCRequest(getThreadContext().getThread().getTestTree()).forEach(sentToPropsMap::remove);

                // 将增量的 JMeterVars写入 JMeterProps中
                sentToPropsMap.forEach(props::put);
            }
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
        props.put("isExecuteSuccess", true);

        // 获取当前线程组下的 sample数量
        threadGroupSampleCount = getSampleCount(getThreadContext().getThread().getTestTree());

        // 保存 JMeterVars副本
        getThreadContext().getVariables().entrySet().forEach(e -> clonedVars.put(e.getKey(), e.getValue()));
    }

    @Override
    public void threadFinished() {
        // 线程组执行结束时清理数据
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
}
