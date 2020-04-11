package org.apache.jmeter.samplers;


import org.apache.commons.collections4.MapUtils;
import org.apache.jmeter.JMeter;
import org.apache.jmeter.common.CliOption;
import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.common.utils.JMeterVarsUtil;
import org.apache.jmeter.common.utils.LogUtil;
import org.apache.jmeter.common.utils.PathUtil;
import org.apache.jmeter.common.utils.exception.ServiceException;
import org.apache.jmeter.common.utils.json.JsonUtil;
import org.apache.jmeter.config.ENVDataSet;
import org.apache.jmeter.config.SSHConfiguration;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.exceptions.IllegalUserActionException;
import org.apache.jmeter.gui.tree.JMeterTreeModel;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.AbstractThreadGroup;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.ReportCollector;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;
import org.apache.jorphan.collections.SearchByClass;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author KelvinYe
 */
public class JMeterScriptSampler extends AbstractSampler implements Interruptible {

    private static final Logger logger = LogUtil.getLogger(JMeterScriptSampler.class);

    private Properties props = JMeterUtils.getJMeterProperties();

    public static final String SCRIPT_PATH = "JMeterScriptSampler.scriptPath";
    public static final String SCRIPT_NAME = "JMeterScriptSampler.scriptName";
    public static final String SYNC_TO_PROPS = "JMeterScriptSampler.syncToProps";
    public static final String SYNC_TO_VARS = "JMeterScriptSampler.syncToVars";

    private Thread runningThread;

    @Override
    public SampleResult sample(Entry entry) {
        String scriptPath = getScriptPath();

        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.setEncodingAndType(StandardCharsets.UTF_8.name());
        try {
            result.setSamplerData("脚本路径: " + scriptPath);
            result.sampleStart();
            result.setSuccessful(true);
            // 运行JMeter脚本
            String response = runJMeterScript(scriptPath, result);
            result.setResponseData(response, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            // 异常后，判断是否已记录开始时间，没有则记录
            if (!result.isStampedAtStart()) {
                result.sampleStart();
            }
            result.setSuccessful(false);
            result.setResponseData(ExceptionUtil.getStackTrace(e), StandardCharsets.UTF_8.name());
        } finally {
            result.setEndTime(result.currentTimeInMillis());
            // 清理外部脚本中设置的 JMeterProps
            clearScriptProps();
        }
        return result;
    }

    private String getScriptName() {
        return getPropertyAsString(SCRIPT_NAME);
    }

    private boolean isSyncToProps() {
        return getPropertyAsBoolean(SYNC_TO_PROPS);
    }

    private boolean isSyncToVars() {
        return getPropertyAsBoolean(SYNC_TO_VARS);
    }

    private String getScriptPath() {
        String scriptPath = getPropertyAsString(SCRIPT_PATH);
        String path = PathUtil.join(scriptPath, getScriptName());
        return path.replace("\\", "/");
    }

    /**
     * 执行外部jmx脚本
     *
     * @param scriptAbsPath 脚本绝对路径
     * @return 执行结果
     */
    private String runJMeterScript(String scriptAbsPath, SampleResult result)
            throws IllegalUserActionException, IOException, InterruptedException {
        // 加载子脚本
        HashTree testTree = loadScriptTree(scriptAbsPath, result);

        // 设置全局变量，用于传递给子脚本使用
        props.put(CliOption.CONFIG_NAME, JMeterVarsUtil.getDefault(ENVDataSet.CONFIG_NAME));

        // 判断是否需要把当前线程的局部变量同步至子脚本
        if (isSyncToVars()) {
            props.put(JMeterScriptDataTransfer.CALLER_VARIABLES, getThreadContext().getVariables());
        }

        // 开始执行子脚本
        StandardJMeterEngine engine = new StandardJMeterEngine();
        engine.setProperties(props);
        engine.configure(testTree);

        // 判断是否命令行运行，如果是命令行模式，则临时设置 JMETER_NON_GUI为false
        boolean isNonGuiMode = false;
        if (JMeter.isNonGUI()) {
            isNonGuiMode = true;
            setNonGui(false);
        }

        // 新建一个线程运行
        Thread runningThread = new Thread(engine, "StandardJMeterEngine");
        this.runningThread = runningThread;
        runningThread.start();
        runningThread.join();

        // 判断是否命令行运行，如果是命令行模式，则把 JMETER_NON_GUI恢复为true
        if (isNonGuiMode) {
            setNonGui(true);
        }

        // 提取子脚本的执行结果
        @SuppressWarnings("unchecked")
        Map<String, Object> incrementalVariables = (Map<String, Object>) props.get(JMeterScriptDataTransfer.INCREMENTAL_VARIABLES);

        // 把子脚本中的增量局部变量同步至当前线程的局部变量中
        if (isSyncToVars()) {
            incrementalVariables.forEach((key, value) -> {
                if (value instanceof String) {
                    getThreadContext().getVariables().put(key, (String) value);
                } else {
                    getThreadContext().getVariables().putObject(key, value);
                }
            });
        }

        // 把子脚本中新增的局部变量同步至全局变量中
        setProps(incrementalVariables);

        return formatResponse(incrementalVariables);
    }

    /**
     * 加载 jmx脚本
     *
     * @param scriptAbsPath 脚本绝对路径
     * @return 脚本的 HashTree对象
     */
    private HashTree loadScriptTree(String scriptAbsPath, SampleResult result) throws IOException, IllegalUserActionException {
        // 加载脚本
        File file = new File(scriptAbsPath);
        HashTree tree = SaveService.loadTree(file);

        // 对脚本做一些处理
        JMeterTreeModel treeModel = new JMeterTreeModel(new TestPlan());
        JMeterTreeNode root = (JMeterTreeNode) treeModel.getRoot();
        treeModel.addSubTree(tree, root);

        // 删除已禁用的组件
        HashTree testTree = JMeter.convertSubTree(tree, false);

        // 校验脚本中是否仅存在一个线程组
        // 设置该线程组配置，修改如下
        // 错误动作=ON_SAMPLE_ERROR_START_NEXT_LOOP
        // 线程数=1
        // 循环次数=1
        setThreadGroupParams(testTree);

        // 删除不必要的组件
        removeUnwantedComponents(testTree);

        // 添加必须的组件
        addComponents(testTree, result);

        return testTree;
    }

    private String formatResponse(Map<String, Object> incrementalVariables) {
        StringBuffer response = new StringBuffer("子脚本新增变量：\n");
        incrementalVariables.forEach((key, value) -> {
            if (value == null) {
                response.append(key).append(": ").append("\n");
            } else if (value instanceof String) {
                response.append(key).append(": ").append((String) value).append("\n");
            } else {
                response.append(key).append(": ").append(JsonUtil.toJson(value)).append("\n");
            }
        });
        return response.toString();
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
        SearchByClass<SSHConfiguration> sshSearcher = new SearchByClass<>(SSHConfiguration.class);
        SearchByClass<ReportCollector> reportSearcher = new SearchByClass<>(ReportCollector.class);
        SearchByClass<ResultCollector> rcSearcher = new SearchByClass<>(ResultCollector.class);
        testPlanTree.traverse(tgSearcher);
        testPlanTree.traverse(sshSearcher);
        testPlanTree.traverse(reportSearcher);
        testPlanTree.traverse(rcSearcher);
        Iterator<AbstractThreadGroup> tgIter = tgSearcher.getSearchResults().iterator();
        Iterator<SSHConfiguration> sshIter = sshSearcher.getSearchResults().iterator();
        Iterator<ReportCollector> reportIter = reportSearcher.getSearchResults().iterator();
        Iterator<ResultCollector> rcIter = rcSearcher.getSearchResults().iterator();

        // 遍历删除以上搜索的对象
        // 删除 TestPlan下的组件
        while (sshIter.hasNext()) {
            // 删除 SSH Configuration组件
            SSHConfiguration sshPortForwarding = sshIter.next();
            testPlanTree.remove(sshPortForwarding);
        }

        while (reportIter.hasNext()) {
            // 删除 HTML Report组件
            ReportCollector reportCollector = reportIter.next();
            testPlanTree.remove(reportCollector);
        }

        while (rcIter.hasNext()) {
            // 删除 TestPlan下的查看结果树组件
            ResultCollector resultCollector = rcIter.next();
            testPlanTree.remove(resultCollector);
        }

        // 删除 ThreadGroup下的查看结果树组件
        while (tgIter.hasNext()) {
            AbstractThreadGroup threadGroup = tgIter.next();
            HashTree threadGroupTree = testPlanTree.get(threadGroup);
            for (ResultCollector resultCollector : rcSearcher.getSearchResults()) {
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
        // 获取 TestPlan的subTree
        HashTree testPlanTree = hashTree.get(hashTree.getArray()[0]);

        // 从 TestPlan中搜索 ThreadGroup
        SearchByClass<AbstractThreadGroup> searcher = new SearchByClass<>(AbstractThreadGroup.class);
        testPlanTree.traverse(searcher);

        if (searcher.getSearchResults().size() != 1) {
            throw new ServiceException("JMeter脚本中仅支持一个线程组，请修改脚本");
        }

        for (AbstractThreadGroup threadGroup : searcher.getSearchResults()) {
            // 将子脚本的线程组名称更改为调用者线程组的名称
            threadGroup.setName(getRawThreadName());

            if (!threadGroup.getOnErrorStartNextLoop()) {
                logger.info("JMeter脚本中的线程组仅支持错误时启动下一进程循环，已强制修改为错误时启动下一进程循环");
                threadGroup.setProperty(AbstractThreadGroup.ON_SAMPLE_ERROR, AbstractThreadGroup.ON_SAMPLE_ERROR_START_NEXT_LOOP);
            }
            if (threadGroup.getNumThreads() != 1) {
                logger.info("JMeter脚本的线程组仅支持单次执行，已强制修改线程数为1");
                threadGroup.setNumThreads(1);
            }
            LoopController loopController = (LoopController) threadGroup.getSamplerController();
            if (loopController.getLoops() != 1) {
                logger.info("JMeter脚本的线程组仅支持单次执行，已强制修改循环次数为1");
                loopController.setLoops(1);
            }
        }
    }

    /**
     * 清空外部脚本的执行结果
     */
    private void clearScriptProps() {
        props.remove(CliOption.CONFIG_NAME);
        props.remove(JMeterScriptDataTransfer.INCREMENTAL_VARIABLES);
        props.remove(JMeterScriptDataTransfer.CALLER_VARIABLES);
    }

    private void setProps(Map<String, Object> incrementalVariables) {
        if (!isSyncToProps()) {
            return;
        }

        if (MapUtils.isEmpty(incrementalVariables)) {
            return;
        }

        incrementalVariables.forEach((key, value) -> props.put(key, value));
    }

    private void setNonGui(boolean isNonGui) {
        System.setProperty(JMeter.JMETER_NON_GUI, String.valueOf(isNonGui));
    }

    @Override
    public boolean interrupt() {
        clearScriptProps();
        runningThread.interrupt();
        return true;
    }

    /**
     * 获取当前线程下的HTML报告组件
     */
    private Collection<ReportCollector> getReportCollectorIter() {
        ListedHashTree tree = JMeterContextService.getContext().getThread().getTestTree();
        SearchByClass<ReportCollector> searcher = new SearchByClass<>(ReportCollector.class);
        tree.traverse(searcher);
        return searcher.getSearchResults();
    }

    /**
     * 添加各种必须的组件
     */
    private void addComponents(HashTree hashTree, SampleResult result) {
        Object testPlan = hashTree.getArray()[0];

        // 添加 JMeter脚本数据传递组件
        hashTree.add(testPlan, new JMeterScriptDataTransfer(result));

        // 添加调用者的 HTML报告组件
        for (ReportCollector reportCollector : getReportCollectorIter()) {
            hashTree.add(testPlan, reportCollector);
        }
    }

    /**
     * 获取线程名称（删除线程名称后 JMeter自动添加的序号）
     */
    private String getRawThreadName() {
        String threadName = JMeterContextService.getContext().getThread().getThreadName();
        String pattern = "[ ][\\d]+[-][\\d]+$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(threadName);
        threadName = m.replaceAll("");
        return threadName;
    }
}
