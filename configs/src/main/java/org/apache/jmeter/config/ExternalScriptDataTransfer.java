package org.apache.jmeter.config;

import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.ThreadListener;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import pers.kelvin.util.FileUtil;
import pers.kelvin.util.log.LogUtil;

import java.util.HashMap;
import java.util.Properties;

/**
 * Description:
 *
 * @author: KelvinYe
 */
public class ExternalScriptDataTransfer extends ConfigTestElement implements ThreadListener, SampleListener {

    private static final Logger logger = LogUtil.getLogger(ExternalScriptDataTransfer.class);

    private static final String LINE_SEP = FileUtil.LINE_SEPARATOR;

    private boolean isPrintSampleResultToConsole;

    private Properties props = JMeterUtils.getJMeterProperties();

    private HashMap<String, Object> clonedVars;

    public ExternalScriptDataTransfer() {
        super();
        isPrintSampleResultToConsole = Boolean.valueOf(JMeterUtils.getProperty("printSampleResultToConsole"));
    }

    @Override
    public void sampleOccurred(SampleEvent e) {
        SampleResult result = e.getResult();

        if (isPrintSampleResultToConsole) {
            String content = String.format("【Sample Name】:%s" + LINE_SEP +
                            "【Request Data】:" + LINE_SEP +
                            "%s" +
                            "【Response Data】:" + LINE_SEP +
                            "%s" + LINE_SEP + LINE_SEP,
                    result.getSampleLabel(), result.getSamplerData(), result.getResponseDataAsString());
            System.out.println(content);
        }

        if (!result.isSuccessful()) {
            props.put("isExecuteSuccess", "false");
            props.put("errorSampleResult", result);
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
        props.put("isExecuteSuccess", "true");
        getThreadContext().getVariables().entrySet().forEach(e -> clonedVars.put(e.getKey(), e.getValue()));
    }

    @Override
    public void threadFinished() {
    }
}
