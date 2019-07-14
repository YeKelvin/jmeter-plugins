package org.apache.jmeter.config;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.jmeter.reporters.ResultCollector;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
            props.put("isExecuteSuccess", "false");
            props.put("errorSampleResult", result);
        }

        completedSampleCount++;
        getThreadContext().getVariables().entrySet().forEach(entry -> System.out.println(entry.getKey() + "=" + entry.getValue().toString()));

        if (threadGroupSampleCount == completedSampleCount) {
            Collection<Map.Entry> subtract = CollectionUtils.subtract(
                    getThreadContext().getVariables().entrySet(), clonedVars.entrySet());
            subtract.forEach(entry -> props.put(entry.getKey(), entry.getValue()));

            ListedHashTree testTree = getThreadContext().getThread().getTestTree();
            SearchByClass<ResultCollector> searcher = new SearchByClass<>(ResultCollector.class);
            testTree.traverse(searcher);
            searcher.getSearchResults().forEach(System.out::println);
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
        System.out.println("threadStarted");
        props.put("isExecuteSuccess", "true");
        getThreadContext().getVariables().entrySet().forEach(e -> clonedVars.put(e.getKey(), e.getValue()));
        getThreadContext().getVariables().entrySet().forEach(e -> System.out.println(e.getKey() + "=" + e.getValue().toString()));

        ListedHashTree testTree = getThreadContext().getThread().getTestTree();
        SearchByClass<Sampler> searcher = new SearchByClass<>(Sampler.class);
        testTree.traverse(searcher);
        threadGroupSampleCount = searcher.getSearchResults().size();

    }

    @Override
    public void threadFinished() {
    }
}
