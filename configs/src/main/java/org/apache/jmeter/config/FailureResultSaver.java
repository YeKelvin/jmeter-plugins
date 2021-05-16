package org.apache.jmeter.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.common.utils.FileUtil;
import org.apache.jmeter.common.utils.TimeUtil;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/**
 * @author Kelvin.Ye
 */
public class FailureResultSaver extends ConfigTestElement implements SampleListener {

    private static final Logger log = LoggerFactory.getLogger(FailureResultSaver.class);

    private static final String LINE_SEP = FileUtil.LINE_SEPARATOR;

    public static final String LOG_PATH = "FailureResultSaver.logPath";

    public static final String ERROR_CLASSIFICATION = "FailureResultSaver.errorClassification";

    public static final String EXCLUDE = "FailureResultSaver.exclude";

    private File failureLog;

    private String getLogPath() {
        return getPropertyAsString(LOG_PATH);
    }

    private String getErrorClassification() {
        return getPropertyAsString(ERROR_CLASSIFICATION);
    }

    private String getExclude() {
        return getPropertyAsString(EXCLUDE);
    }

    private File getFailureLog() {
        String logPath = getLogPath();
        log.debug("LogPath:[ {} ]", logPath);
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
                // Generate parent sample（事务）
                if (transactionResults.length != 0) {
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
        if (StringUtils.isNotBlank(excludeText)) {
            String responseData = result.getResponseDataAsString();
            String[] excludes = excludeText.split(",");
            for (String exclude : excludes) {
                if (responseData.contains(exclude)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getResultContent(SampleResult result) {
        StringBuffer resultContent = new StringBuffer();
        resultContent
                .append("【Start Time】: ")
                .append(LINE_SEP)
                .append(TimeUtil.timestampToStrtime(result.getStartTime(), "yyyy.MM.dd HH:mm:ss"));

        String requestHeaders = result.getRequestHeaders();
        if (requestHeaders != null && !requestHeaders.isEmpty()) {
            resultContent.append("【Request Headers】: ").append(LINE_SEP).append(requestHeaders);
        }

        resultContent.append("【Request Data】: ").append(LINE_SEP).append(result.getSamplerData());

        String responseHeaders = result.getResponseHeaders();
        if (responseHeaders != null && !responseHeaders.isEmpty()) {
            resultContent.append("【Response Headers】: ").append(LINE_SEP).append(responseHeaders);
        }

        resultContent.append("【Response Data】: ").append(LINE_SEP).append(result.getResponseDataAsString()).append(LINE_SEP);
        resultContent.append("【elapsed】: ").append(result.getEndTime() - result.getStartTime()).append(" ms")
                .append(LINE_SEP).append(LINE_SEP).append(LINE_SEP);
        return resultContent.toString();
    }

    private void outputFile(String content) {
        // 根据错误码分类错误日志文件
        String errorClassification = getErrorClassification();
        if (StringUtils.isNotBlank(errorClassification)) {
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
