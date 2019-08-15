package org.apache.jmeter.config;

import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import pers.kelvin.util.FileUtil;
import pers.kelvin.util.StringUtil;
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

    public static final String ERROR_CLASSIFICATION = "FailureResultSaver.errorClassification";

    public static final String EXCLUDE = "FailureResultSaver.exclude";

    private File failureLog;

    private String getLogPath() {
        return getPropertyAsString(LOG_PATH);
    }

    private String getFormatType() {
        return getPropertyAsString(FORMAT_TYPE);
    }

    private String getErrorClassification() {
        return getPropertyAsString(ERROR_CLASSIFICATION);
    }

    private String getExclude() {
        return getPropertyAsString(EXCLUDE);
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
            // 排除指定的错误请求数据
            if (isExclude(result)) {
                return;
            }
            String content = "";
            // 判断是否在事务控制器下
            if (e.isTransactionSampleEvent()) {
                // 排除 not Generate parent sample（事务）的数据（事务的空数据）
                return;
            } else {
                // 判断 sampler是否为 Generate parent sample（事务）
                // 根据 SampleResult下是否存在 subResults
                SampleResult[] transactionResults = result.getSubResults();
                if (transactionResults.length != 0) { // Generate parent sample（事务）
                    for (SampleResult transactionResult : transactionResults) {
                        if (!transactionResult.isSuccessful()) {
                            content = getResultContent(transactionResult);
                            break; // 跳出for循环
                        }
                    }
                } else { // 一般sampler
                    content = getResultContent(result);
                }
            }
            outputFile(content);
        }
    }

    @Override
    public void sampleStarted(SampleEvent e) {
    }

    @Override
    public void sampleStopped(SampleEvent e) {
    }

    /**
     * 当前错误的请求是否为需要排除的指定请求
     */
    private boolean isExclude(SampleResult result) {
        String excludeText = getExclude();
        if (StringUtil.isNotBlank(excludeText)) {
            String responseData = result.getResponseDataAsString();
            String[] excludes = excludeText.split(",");
            for (String exclude : excludes) {
                if (responseData.contains(exclude)) return true;
            }
        }
        return false;
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

    private void outputFile(String content) {
        // 根据错误码分类错误日志文件
        String errorClassification = getErrorClassification();
        if (StringUtil.isNotBlank(errorClassification)) {
            File failureLog = getFailureLog();
            String[] failureLogs = failureLog.getName().split("\\.");
            String logName = failureLogs[0] + "-" + errorClassification + "." + failureLogs[1];
            String logPath = failureLog.getParent() + File.separator + logName;
            FileUtil.appendFile(logPath, content);
        } else {
            FileUtil.appendFile(getFailureLog(), content);
        }
    }

}
