package org.apache.jmeter.samplers;


import org.apache.commons.collections4.MapUtils;
import org.apache.jmeter.JMeter;
import org.apache.jmeter.config.ENVDataSet;
import org.apache.jmeter.config.ExternalScriptDataTransfer;
import org.apache.jmeter.config.ExternalScriptResultDTO;
import org.apache.jmeter.config.SSHPortForwarding;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.exceptions.IllegalUserActionException;
import org.apache.jmeter.gui.tree.JMeterTreeModel;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.AbstractThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.ReportCollector;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.SearchByClass;
import org.slf4j.Logger;
import pers.kelvin.util.FileUtil;
import pers.kelvin.util.JMeterVarsUtil;
import pers.kelvin.util.PathUtil;
import pers.kelvin.util.StringUtil;
import pers.kelvin.util.exception.ExceptionUtil;
import pers.kelvin.util.exception.ServiceException;
import pers.kelvin.util.json.JsonUtil;
import pers.kelvin.util.log.LogUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * @author KelvinYe
 */
public class ExecuteExternalScript extends AbstractSampler implements Interruptible {

    private static final Logger logger = LogUtil.getLogger(ExecuteExternalScript.class);

    private static final String LINE_SEP = FileUtil.LINE_SEPARATOR;

    private Properties props = JMeterUtils.getJMeterProperties();

    public static final String EXTERNAL_SCRIPT_PATH = "ExecuteExternalScript.externalScriptPath";
    public static final String SCRIPT_NAME = "ExecuteExternalScript.scriptName";
    public static final String PROPS_NAME_SUFFIX = "ExecuteExternalScript.propsNameSuffix";
    public static final String IS_PRINT_TO_CONSOLE = "ExecuteExternalScript.printSampleResultToConsole";

    @Override
    public SampleResult sample(Entry entry) {
        String scriptPath = getScriptPath();
        logger.debug("开始执行外部脚本[{}]", scriptPath);

        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.setEncodingAndType(StandardCharsets.UTF_8.name());
        try {
            result.setSamplerData("执行外部脚本：" + scriptPath);
            result.sampleStart();
            // 运行外部脚本
            ExternalScriptResultDTO externalScriptResult = runExternalScript(scriptPath);
            result.setResponseData(getExecuteResult(externalScriptResult), StandardCharsets.UTF_8.name());
            result.setSuccessful(true);
            // 判断外部脚本的 sampler是否运行失败
            SampleResult errorResult = externalScriptResult.getErrorSampleResult();
            if (errorResult != null) {
                setErrorSampleResult(result, errorResult);
                result.setSuccessful(false);
            }
        } catch (Exception e) {
            // 异常后，判断是否已开始统计sample时间，没有则开始统计
            if (!result.isStampedAtStart()) {
                result.sampleStart();
            }
            result.setSuccessful(false);
            result.setResponseData(ExceptionUtil.getStackTrace(e), StandardCharsets.UTF_8.name());
        } finally {
            result.sampleEnd();
            // 清理外部脚本中设置的 JMeterProps
            clearExternalScriptProps();
        }
        logger.debug("执行外部脚本成功[{}]", scriptPath);
        return result;
    }

    private String getExternalScriptPath() {
        return getPropertyAsString(EXTERNAL_SCRIPT_PATH);
    }

    private String getScriptName() {
        return getPropertyAsString(SCRIPT_NAME);
    }

    private String getPropsNameSuffix() {
        return JMeterUtils.getPropDefault("propsNameSuffix", getPropertyAsString(PROPS_NAME_SUFFIX));
    }

    private String getIsPrintToConsole() {
        return JMeterUtils.getPropDefault("printSampleResultToConsole", getPropertyAsString(IS_PRINT_TO_CONSOLE));
    }

    private String getScriptPath() {
        String path = PathUtil.pathJoin(getExternalScriptPath(), getScriptName());
        return path.replace("\\", "/");
    }

    /**
     * 执行外部jmx脚本
     *
     * @param scriptAbsPath 脚本绝对路径
     * @return 执行结果
     */
    private ExternalScriptResultDTO runExternalScript(String scriptAbsPath) throws IllegalUserActionException, IOException, InterruptedException {
        // 加载脚本
        HashTree clonedTree = loadScriptTree(scriptAbsPath);

        // 设置 JMeterProps，用于传递给外部脚本使用
        props.put("propsNameSuffix", getPropsNameSuffix());
        props.put("printSampleResultToConsole", getIsPrintToConsole());
        props.put("configName", JMeterVarsUtil.getDefault(ENVDataSet.CONFIG_NAME));

        // 开始执行外部脚本
        StandardJMeterEngine engine = new StandardJMeterEngine();
        engine.setProperties(props);
        engine.configure(clonedTree);

        // 判断是否命令行运行，如果是临时设置 JMETER_NON_GUI为false
        boolean isNonGuiMode = false;
        if (JMeter.isNonGUI()) {
            isNonGuiMode = true;
            setNonGuiProperty(false);
        }

        // 新建一个线程运行
        Thread runningThread = new Thread(engine, "StandardJMeterEngine");
        runningThread.start();
        runningThread.join();

        // 判断是否命令行运行，如果是把 JMETER_NON_GUI恢复为true
        if (isNonGuiMode) {
            setNonGuiProperty(true);
        }

        // 提取外部脚本执行结果
        ExternalScriptResultDTO scriptResult = (ExternalScriptResultDTO) props.get("externalScriptResult");

        // 把外部脚本中新增的 JMeterVars变量加入 JMeterProps中
        // 如果设置了 JMeterProps属性名称后缀，则把外部脚本中获取的变量名都加上后缀
        setPropsWithSuffix(scriptResult);

        return scriptResult;
    }

    /**
     * 加载 jmx脚本
     *
     * @param scriptAbsPath 脚本绝对路径
     * @return 脚本的 HashTree对象
     */
    private HashTree loadScriptTree(String scriptAbsPath) throws IOException, IllegalUserActionException {
        // 加载脚本
        File file = new File(scriptAbsPath);
        HashTree tree = SaveService.loadTree(file);

        // 对脚本做一些处理
        JMeterTreeModel treeModel = new JMeterTreeModel(new TestPlan());
        JMeterTreeNode root = (JMeterTreeNode) treeModel.getRoot();
        treeModel.addSubTree(tree, root);

        // 删除已禁用的组件
        HashTree clonedTree = JMeter.convertSubTree(tree, true);

        // 强制设置线程组的错误动作、线程数和循环次数
        setThreadGroupParams(clonedTree);

        // 删除不必要的组件
        removeUnwantedComponents(clonedTree);

        // 对外部脚本添加组件，用于数据传递
        clonedTree.add(clonedTree.getArray()[0], new ExternalScriptDataTransfer());

        return clonedTree;
    }

    /**
     * 序列化 JMeterProps的差集
     *
     * @param scriptResult ExternalScriptResultDTO对象
     * @return json
     */
    private String getExecuteResult(ExternalScriptResultDTO scriptResult) {
        if (MapUtils.isEmpty(scriptResult.getExternalScriptData())) {
            return "{\"isExecuteSuccess\":" + scriptResult.isExecuteSuccess() + ",\"msg\":\"外部脚本没有设置新的变量\"}";
        }
        return fixJson(JsonUtil.toJson(scriptResult));
    }

    /**
     * 修正数据
     */
    private String fixJson(String json) {
        return json.replace("\"[", "[")
                .replace("]\"", "]")
                .replace("\\\"", "\"");
    }

    /**
     * 设置失败 Sample的数据
     *
     * @param result SampleResult对象
     */
    private void setErrorSampleResult(SampleResult result, SampleResult errorResult) {
        result.setRequestHeaders(errorResult.getRequestHeaders());
        result.setResponseHeaders(errorResult.getRequestHeaders());
        result.setSamplerData(result.getSamplerData() + LINE_SEP + LINE_SEP +
                "外部脚本中，以下 Sample执行失败：" + LINE_SEP +
                "【Error Sample Name】: " + errorResult.getSampleLabel() + LINE_SEP +
                "【Error Request Data】:" + LINE_SEP +
                errorResult.getSamplerData());
        result.setResponseData(result.getResponseDataAsString() + LINE_SEP + LINE_SEP +
                        "【Error Sample Name】: " + errorResult.getSampleLabel() + LINE_SEP +
                        "【Error Response Data】:" + LINE_SEP +
                        errorResult.getResponseDataAsString(),
                StandardCharsets.UTF_8.name());
    }

    /**
     * 删除外部脚本中不需要的组件
     *
     * @param hashTree jmx脚本的 HashTree对象
     */
    private void removeUnwantedComponents(HashTree hashTree) {
        // 获取 TestPlan的HashTree对象
        HashTree testPlanTree = hashTree.get(hashTree.getArray()[0]);

        // 从 HashTree中搜索对应的组件对象
        SearchByClass<AbstractThreadGroup> tgSearcher = new SearchByClass<>(AbstractThreadGroup.class);
        SearchByClass<SSHPortForwarding> sshSearcher = new SearchByClass<>(SSHPortForwarding.class);
        SearchByClass<ReportCollector> reportSearcher = new SearchByClass<>(ReportCollector.class);
        SearchByClass<ResultCollector> rcSearcher = new SearchByClass<>(ResultCollector.class);
        testPlanTree.traverse(tgSearcher);
        testPlanTree.traverse(sshSearcher);
        testPlanTree.traverse(reportSearcher);
        testPlanTree.traverse(rcSearcher);
        Iterator<AbstractThreadGroup> tgIter = tgSearcher.getSearchResults().iterator();
        Iterator<SSHPortForwarding> sshIter = sshSearcher.getSearchResults().iterator();
        Iterator<ReportCollector> reportIter = reportSearcher.getSearchResults().iterator();
        Iterator<ResultCollector> rcIter = rcSearcher.getSearchResults().iterator();

        // 逐个删除以上搜索的对象
        // 删除 TestPlan下的组件
        while (sshIter.hasNext()) {
            // 删除 ssh端口转发组件
            SSHPortForwarding sshPortForwarding = sshIter.next();
            testPlanTree.remove(sshPortForwarding);
        }

        while (reportIter.hasNext()) {
            // 删除 Local Html Report组件
            ReportCollector reportCollector = reportIter.next();
            testPlanTree.remove(reportCollector);
        }

        // 删除 ThreadGroup下的组件
        while (tgIter.hasNext()) {
            AbstractThreadGroup threadGroup = tgIter.next();
            HashTree threadGroupTree = testPlanTree.get(threadGroup);
            while (rcIter.hasNext()) {
                // 删除 查看结果树组件
                ResultCollector resultCollector = rcIter.next();
                threadGroupTree.remove(resultCollector);
            }
        }
    }

    /**
     * 设置线程组的 sample错误时的动作强制设为错误时启动下一进程循环，线程数强制设为1，循环次数强制设为1
     *
     * @param hashTree jmx脚本的 HashTree对象
     */
    private void setThreadGroupParams(HashTree hashTree) {
        // 获取 TestPlan的HashTree对象
        HashTree testPlanTree = hashTree.get(hashTree.getArray()[0]);

        // 从 HashTree中搜索对应的组件对象
        SearchByClass<AbstractThreadGroup> searcher = new SearchByClass<>(AbstractThreadGroup.class);
        testPlanTree.traverse(searcher);

        if (searcher.getSearchResults().size() != 1) {
            throw new ServiceException("外部脚本中只支持存在一个线程组，请修改后重试");
        }

        for (AbstractThreadGroup threadGroup : searcher.getSearchResults()) {
            if (!threadGroup.getOnErrorStartNextLoop()) {
                logger.info("外部脚本中的线程组只支持错误时启动下一进程循环，已强制修改为错误时启动下一进程循环");
                threadGroup.setProperty(AbstractThreadGroup.ON_SAMPLE_ERROR, AbstractThreadGroup.ON_SAMPLE_ERROR_START_NEXT_LOOP);
            }
            if (threadGroup.getNumThreads() != 1) {
                logger.info("外部脚本的线程组只支持单次执行，已强制修改线程数为1");
                threadGroup.setNumThreads(1);
            }
            LoopController loopController = (LoopController) threadGroup.getSamplerController();
            if (loopController.getLoops() != 1) {
                logger.info("外部脚本的线程组只支持单次执行，已强制修改次数为1");
                loopController.setLoops(1);
            }
        }
    }

    /**
     * 清空外部脚本的执行结果
     */
    private void clearExternalScriptProps() {
        props.remove("configName");
        props.remove("propsNameSuffix");
        props.remove("printSampleResultToConsole");
        props.remove("externalScriptResult");
    }

    private void setPropsWithSuffix(ExternalScriptResultDTO scriptResult) {
        Map<String, Object> scriptData = scriptResult.getExternalScriptData();

        if (MapUtils.isEmpty(scriptData)) {
            return;
        }

        String propsNameSuffix = getPropsNameSuffix();
        if (StringUtil.isBlank(propsNameSuffix)) {
            scriptResult.getExternalScriptData().forEach((key, value) -> props.put(key, value.toString()));
        } else {
            scriptResult.getExternalScriptData().forEach((key, value) -> props.put(key + "_" + propsNameSuffix, value.toString()));
        }
    }

    private void setNonGuiProperty(boolean isNonGui) {
        System.setProperty(JMeter.JMETER_NON_GUI, String.valueOf(isNonGui));
    }

    @Override
    public boolean interrupt() {
        clearExternalScriptProps();
        return true;
    }
}
