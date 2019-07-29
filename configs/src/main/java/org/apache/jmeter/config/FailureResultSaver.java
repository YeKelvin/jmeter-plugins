package org.apache.jmeter.config;

import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import pers.kelvin.util.FileUtil;
import pers.kelvin.util.TimeUtil;
import pers.kelvin.util.log.LogUtil;

import java.io.File;


/**
 * @author KelvinYe
 */
public class FailureResultSaver extends ConfigTestElement implements SampleListener {

    private static final Logger logger = LogUtil.getLogger(FailureResultSaver.class);

    private static final String LINE_SEP = FileUtil.LINE_SEPARATOR;

    public static final String LOG_PATH = "FailureResultSaver.logPath";

    public static final String FORMAT_TYPE = "FailureResultSaver.formatType";

    private File failureLog;

    private String getLogPath() {
        return getPropertyAsString(LOG_PATH);
    }

    private String getFormatType() {
        return getPropertyAsString(FORMAT_TYPE);
    }

    private File getFailureLog() {
        String logPath = getLogPath();
        logger.debug("LogPath={}", logPath);
        if (failureLog == null) {
            failureLog = new File(logPath);
        }
        return failureLog;
    }

    @Override
    public void sampleOccurred(SampleEvent e) {
        SampleResult result = e.getResult();
        // sampler失败时记录测试数据到日志文件
        if (!result.isSuccessful()) {
            String content = getResultContent(result);
            FileUtil.appendFile(getFailureLog(), content);
        }
    }

    @Override
    public void sampleStarted(SampleEvent e) {
    }

    @Override
    public void sampleStopped(SampleEvent e) {
    }

    private String getResultContent(SampleResult result) {
        if ("Dubbo".equals(getFormatType())) {
            return getDubboResult(result);
        }
        return getHttpResult(result);
    }

    private String getHttpResult(SampleResult result) {
        return String.format("【Start Time】: %s" + LINE_SEP +
                        "【Request Header】:" + LINE_SEP +
                        "%s" +
                        "【Request Data】:" + LINE_SEP +
                        "%s" +
                        "【Response Header】:" + LINE_SEP +
                        "%s" +
                        "【Response Data】:" + LINE_SEP +
                        "%s" + LINE_SEP +
                        "【elapsed】: %s ms" + LINE_SEP + LINE_SEP + LINE_SEP,
                TimeUtil.timeStampToString(result.getStartTime(), "yyyy.MM.dd HH:mm:ss"),
                result.getRequestHeaders(), result.getSamplerData(),
                result.getResponseHeaders(), result.getResponseDataAsString(),
                result.getEndTime() - result.getStartTime());
    }

    private String getDubboResult(SampleResult result) {
        return String.format("【Start Time】: %s" + LINE_SEP +
                        "【Request Data】: %s" + LINE_SEP +
                        "【Response Data】: %s" + LINE_SEP +
                        "【elapsed】:%s ms" + LINE_SEP + LINE_SEP + LINE_SEP,
                TimeUtil.timeStampToString(result.getStartTime(), "yyyy.MM.dd HH:mm:ss"),
                result.getSamplerData(),
                result.getResponseDataAsString(),
                result.getEndTime() - result.getStartTime());
    }
}
