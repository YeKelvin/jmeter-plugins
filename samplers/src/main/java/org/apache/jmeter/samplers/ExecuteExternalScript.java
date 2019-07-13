package org.apache.jmeter.samplers;


import org.apache.commons.collections4.CollectionUtils;
import org.apache.jmeter.JMeter;
import org.apache.jmeter.config.ExternalScriptDataTransfer;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.exceptions.IllegalUserActionException;
import org.apache.jmeter.gui.tree.JMeterTreeModel;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.slf4j.Logger;
import pers.kelvin.util.JMeterVarsUtil;
import pers.kelvin.util.PathUtil;
import pers.kelvin.util.exception.ExceptionUtil;
import pers.kelvin.util.log.LogUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author KelvinYe
 */
public class ExecuteExternalScript extends AbstractSampler {

    private static final Logger logger = LogUtil.getLogger(ExecuteExternalScript.class);

    public static final String EXTERNAL_SCRIPT_PATH = "ExecuteExternalScript.ExternalScriptPath";

    public static final String SCRIPT_NAME = "ExecuteExternalScript.ScriptName";

    public static final String PROPS_NAME_SUFFIX = "ExecuteExternalScript.PropsNameSuffix";

    public static final String IS_PRINT_TO_CONSOLE = "ExecuteExternalScript.PrintSampleResultToConsole";

    @Override
    public SampleResult sample(Entry entry) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.setEncodingAndType(StandardCharsets.UTF_8.name());
        boolean isSuccess = true;
        String responseData = "";
        try {
            String scriptPath = getScriptPath();
            result.setSamplerData("执行外部脚本：" + scriptPath);
            result.sampleStart();
            responseData = runExternalScript(scriptPath);
        } catch (Exception e) {
            // 异常后，判断是否已开始统计sample时间，没有则开始统计
            if (result.isStampedAtStart()) {
                result.sampleStart();
            }
            isSuccess = false;
            responseData = ExceptionUtil.getStackTrace(e);
        } finally {
            result.sampleEnd();
            result.setSuccessful(isSuccess);
            result.setResponseData(responseData, StandardCharsets.UTF_8.name());
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
     * @return
     * @throws IllegalUserActionException 异常
     * @throws IOException                异常
     */
    private String runExternalScript(String scriptAbsPath) throws IllegalUserActionException, IOException {
        // 加载脚本
        File file = new File(scriptAbsPath);
        HashTree tree = SaveService.loadTree(file);

        // 对脚本做一些处理
        JMeterTreeModel treeModel = new JMeterTreeModel(new TestPlan());
        JMeterTreeNode root = (JMeterTreeNode) treeModel.getRoot();
        treeModel.addSubTree(tree, root);
        HashTree clonedTree = JMeter.convertSubTree(tree, true);

        // 对外部脚本添加组件，用于数据传递
        clonedTree.add(clonedTree.getArray()[0], new ExternalScriptDataTransfer());

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

        // 获取执行外部脚本前后的JMeter属性的差集
        Collection<Map.Entry> subtract = CollectionUtils.subtract(currentProps.entrySet(), clonedProps.entrySet());
        if (subtract.isEmpty()) {
            return "外部脚本没有设置新的JMeter属性";
        }

        // Json化JMeter属性的差集作为结果返回
        StringBuffer result = new StringBuffer();
        result.append("{");
        subtract.forEach(e -> result.append("\"").append(e.getKey()).append("\":\"").append(e.getValue()).append("\","));
        result.deleteCharAt(result.length() - 1);
        result.append("}");
        return result.toString();
    }

    private void setErrorSampleResult(SampleResult currentResult) {
        SampleResult errorResult = (SampleResult) JMeterUtils.getJMeterProperties().get("errorSampleResult");
        currentResult.setRequestHeaders(errorResult.getRequestHeaders());
        currentResult.setSamplerData(
                currentResult.getSamplerData() + "\n外部脚本中，以下Sample执行失败\n" + errorResult.getSamplerData());
        currentResult.setResponseHeaders(errorResult.getRequestHeaders());
    }

}
