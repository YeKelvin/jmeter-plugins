package org.apache.jmeter.samplers;


import org.apache.commons.collections4.MapUtils;
import org.apache.jmeter.JMeter;
import org.apache.jmeter.config.ExternalScriptDataTransfer;
import org.apache.jmeter.config.ExternalScriptResultDTO;
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
import pers.kelvin.util.StringUtil;
import pers.kelvin.util.exception.ExceptionUtil;
import pers.kelvin.util.json.JsonUtil;
import pers.kelvin.util.log.LogUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Properties;

/**
 * @author KelvinYe
 */
public class ExecuteExternalScript extends AbstractSampler {

    private static final Logger logger = LogUtil.getLogger(ExecuteExternalScript.class);

    private Properties props = JMeterUtils.getJMeterProperties();

    private static final String LINE_SEP = FileUtil.LINE_SEPARATOR;

    public static final String EXTERNAL_SCRIPT_PATH = "ExecuteExternalScript.externalScriptPath";
    public static final String SCRIPT_NAME = "ExecuteExternalScript.scriptName";
    public static final String PROPS_NAME_SUFFIX = "ExecuteExternalScript.propsNameSuffix";
    public static final String IS_PRINT_TO_CONSOLE = "ExecuteExternalScript.printSampleResultToConsole";

    @Override
    public SampleResult sample(Entry entry) {
        String scriptPath = getScriptPath();
        logger.debug("开始执行外部脚本[" + scriptPath + "]");

        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.setEncodingAndType(StandardCharsets.UTF_8.name());
        try {
            result.setSamplerData("执行外部脚本：" + scriptPath);
            result.sampleStart();
            result.setResponseData(runExternalScript(scriptPath), StandardCharsets.UTF_8.name());
            result.setSuccessful(true);
            if (props.containsKey("errorSampleResult")) {
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
            // 重置外部脚本中设置的 isExecuteSuccess属性和 errorSampleResult属性
            clearExternalScriptProps();
        }
        logger.debug("执行外部脚本成功[" + scriptPath + "]");
        return result;
    }

    private String getExternalScriptPath() {
        return getPropertyAsString(EXTERNAL_SCRIPT_PATH);
    }

    private String getScriptName() {
        return getPropertyAsString(SCRIPT_NAME);
    }

    private String getPropsNameSuffix() {
        return getPropertyAsString(PROPS_NAME_SUFFIX);
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
    private String runExternalScript(String scriptAbsPath) throws IllegalUserActionException, IOException, InterruptedException {
        // 加载脚本
        HashTree clonedTree = loadScriptTree(scriptAbsPath);

        // 设置 JMeterProps，用于传递给外部脚本使用
        props.put("printSampleResultToConsole", getIsPrintToConsole());
        props.put("configName", JMeterVarsUtil.getDefault("ENVDataSet.configName"));

        // 开始执行外部脚本
        StandardJMeterEngine engine = new StandardJMeterEngine();
        engine.setProperties(props);
        engine.configure(clonedTree);

        // 新建一个线程运行
        Thread runningThread = new Thread(engine, "StandardJMeterEngine");
        runningThread.start();
        runningThread.join();

        // 提取外部脚本执行结果
        ExternalScriptResultDTO scriptResult = (ExternalScriptResultDTO) props.get("externalScriptResult");

        // 把外部脚本中新增的 JMeterVars变量加入 JMeterProps中
        // 如果设置了 JMeterProps属性名称后缀，则把外部脚本中获取的变量名都加上后缀
        setPropsWithSuffix(scriptResult);

        // Json化JMeter属性的差集作为结果返回
        return getExecuteResult(scriptResult);
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

        // 对外部脚本添加组件，用于数据传递
        clonedTree.add(clonedTree.getArray()[0], new ExternalScriptDataTransfer());

        // 删除不必要的组件
        removeUnwantedComponents(clonedTree);

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

    /**
     * 清空外部脚本的执行结果
     */
    private void clearExternalScriptProps() {
        props.remove("isExecuteSuccess");
        props.remove("printSampleResultToConsole");
        props.remove("errorSampleResult");
        props.remove("externalScriptResult");
    }

    private void setPropsWithSuffix(ExternalScriptResultDTO scriptResult) {
        String propsNameSuffix = getPropsNameSuffix();
        if (StringUtil.isBlank(propsNameSuffix)) {
            scriptResult.getExternalScriptData().forEach((key, value) -> props.put(key, value.toString()));
        } else {
            scriptResult.getExternalScriptData().forEach((key, value) -> props.put(key + "_" + propsNameSuffix, value.toString()));
        }
    }

}
