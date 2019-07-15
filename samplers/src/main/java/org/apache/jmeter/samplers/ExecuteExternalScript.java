package org.apache.jmeter.samplers;


import org.apache.commons.collections4.CollectionUtils;
import org.apache.jmeter.JMeter;
import org.apache.jmeter.config.ExternalScriptDataTransfer;
import org.apache.jmeter.config.SSHPortForwarding;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.exceptions.IllegalUserActionException;
import org.apache.jmeter.gui.tree.JMeterTreeModel;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.AbstractThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.SearchByClass;
import org.slf4j.Logger;
import pers.kelvin.util.FileUtil;
import pers.kelvin.util.JMeterVarsUtil;
import pers.kelvin.util.PathUtil;
import pers.kelvin.util.exception.ExceptionUtil;
import pers.kelvin.util.log.LogUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author KelvinYe
 */
public class ExecuteExternalScript extends AbstractSampler {

    private static final Logger logger = LogUtil.getLogger(ExecuteExternalScript.class);

    private static final String LINE_SEP = FileUtil.LINE_SEPARATOR;

    public static final String EXTERNAL_SCRIPT_PATH = "ExecuteExternalScript.ExternalScriptPath";

    public static final String SCRIPT_NAME = "ExecuteExternalScript.ScriptName";

    public static final String PROPS_NAME_SUFFIX = "ExecuteExternalScript.PropsNameSuffix";

    public static final String IS_PRINT_TO_CONSOLE = "ExecuteExternalScript.PrintSampleResultToConsole";

    @Override
    public SampleResult sample(Entry entry) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.setEncodingAndType(StandardCharsets.UTF_8.name());
        try {
            String scriptPath = getScriptPath();
            result.setSamplerData("执行外部脚本：" + scriptPath);
            result.sampleStart();
            result.setResponseData(runExternalScript(scriptPath), StandardCharsets.UTF_8.name());
            result.setSuccessful(true);
            if (JMeterUtils.getJMeterProperties().containsKey("errorSampleResult")) {
                setErrorSampleResult(result);
                result.setSuccessful(false);
            }
        } catch (Exception e) {
            // 异常后，判断是否已开始统计sample时间，没有则开始统计
            if (result.isStampedAtStart()) {
                result.sampleStart();
            }
            result.setSuccessful(false);
            result.setResponseData(ExceptionUtil.getStackTrace(e), StandardCharsets.UTF_8.name());
        } finally {
            result.sampleEnd();
        }
        return result;
    }

    private String getExternalScriptPath() {
        return getPropertyAsString(EXTERNAL_SCRIPT_PATH);
    }

    private String getScriptName() {
        return getPropertyAsString(SCRIPT_NAME);
    }

    private String getPropsNameSuffix() {
        return "_" + getPropertyAsString(PROPS_NAME_SUFFIX);
    }

    private String getIsPrintToConsole() {
        return getPropertyAsString(IS_PRINT_TO_CONSOLE);
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
    private String runExternalScript(String scriptAbsPath) throws IllegalUserActionException, IOException {
        // 加载脚本
        HashTree clonedTree = loadScriptTree(scriptAbsPath);

        // 对外部脚本添加组件，用于数据传递
        clonedTree.add(clonedTree.getArray()[0], new ExternalScriptDataTransfer());

        // 删除不必要的组件
        removeUnwantedComponents(clonedTree);

        // 设置JMeter属性，用于传递给外部脚本使用
        Properties props = JMeterUtils.getJMeterProperties();
        props.put("propsNameSuffix", getPropsNameSuffix());
        props.put("printSampleResultToConsole", getIsPrintToConsole());
        props.put("configName", JMeterVarsUtil.getDefault("ENVDataSet.ConfigName"));

        // 保存执行外部脚本前的JMeter属性的副本
        HashMap<String, String> clonedProps = new HashMap<>();
        props.forEach((key, value) -> clonedProps.put(key.toString(), value.toString()));

        // 开始执行外部脚本
        StandardJMeterEngine engine = new StandardJMeterEngine();
        engine.setProperties(props);
        engine.configure(clonedTree);
        engine.run();

        // 保存执行外部脚本后的JMeter属性的副本
        HashMap<String, String> currentProps = new HashMap<>();
        props.forEach((key, value) -> currentProps.put(key.toString(), value.toString()));

        // Json化JMeter属性的差集作为结果返回
        return getExecuteResult(currentProps, clonedProps);
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
        return JMeter.convertSubTree(tree, true);
    }

    /**
     * 序列化 JMeterProps的差集
     *
     * @param afterProps  运行后的props的HashMap对象
     * @param beforeProps 运行前的props的副本的HashMap对象
     * @return json
     */
    private String getExecuteResult(HashMap<String, String> afterProps, HashMap<String, String> beforeProps) {
        // 获取执行外部脚本前后的JMeter属性的差集
        Collection<Map.Entry> subtract = CollectionUtils.subtract(afterProps.entrySet(), beforeProps.entrySet());
        if (subtract.isEmpty()) {
            return "外部脚本没有设置新的JMeterVars";
        }

        // 序列化 JMeterProps的差集作为结果返回
        StringBuffer result = new StringBuffer();
        result.append("{");
        subtract.forEach(e -> result.append("\"").append(e.getKey()).append("\":\"").append(e.getValue()).append("\","));
        result.deleteCharAt(result.length() - 1);
        result.append("}");
        return result.toString();
    }

    /**
     * 设置失败 Sample的数据
     *
     * @param currentResult SampleResult对象
     */
    private void setErrorSampleResult(SampleResult currentResult) {
        SampleResult errorResult = (SampleResult) JMeterUtils.getJMeterProperties().get("errorSampleResult");
        currentResult.setRequestHeaders(errorResult.getRequestHeaders());
        currentResult.setResponseHeaders(errorResult.getRequestHeaders());
        currentResult.setSamplerData(currentResult.getSamplerData() + LINE_SEP + LINE_SEP +
                "外部脚本中，以下 Sample执行失败：" + LINE_SEP +
                "【Error Sample Name】: " + errorResult.getSampleLabel() + LINE_SEP +
                "【Error Request Data】:" + LINE_SEP +
                errorResult.getSamplerData());
        currentResult.setResponseData(currentResult.getResponseDataAsString() + LINE_SEP + LINE_SEP +
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
        SearchByClass<ResultCollector> rcSearcher = new SearchByClass<>(ResultCollector.class);
        testPlanTree.traverse(tgSearcher);
        testPlanTree.traverse(sshSearcher);
        testPlanTree.traverse(rcSearcher);
        Iterator<AbstractThreadGroup> tgIter = tgSearcher.getSearchResults().iterator();
        Iterator<SSHPortForwarding> sshIter = sshSearcher.getSearchResults().iterator();
        Iterator<ResultCollector> rcIter = rcSearcher.getSearchResults().iterator();

        // 逐个删除以上搜索的对象
        // 删除 TestPlan下的组件
        while (sshIter.hasNext()) {
            // 删除 ssh端口转发组件
            SSHPortForwarding sshPortForwarding = sshIter.next();
            testPlanTree.remove(sshPortForwarding);
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

}
