package org.apache.jmeter.samplers;


import org.apache.jmeter.JMeter;
import org.apache.jmeter.engine.JMeterEngine;
import org.apache.jmeter.engine.JMeterEngineException;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.exceptions.IllegalUserActionException;
import org.apache.jmeter.gui.tree.JMeterTreeModel;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jorphan.collections.HashTree;
import org.slf4j.Logger;
import pers.kelvin.util.PathUtil;
import pers.kelvin.util.exception.ExceptionUtil;
import pers.kelvin.util.log.LogUtil;

import java.io.File;
import java.io.IOException;

/**
 * @author KelvinYe
 */
public class ExecuteExternalScript extends AbstractSampler {

    private static final Logger logger = LogUtil.getLogger(ExecuteExternalScript.class);

    public static final String EXTERNAL_SCRIPT_PATH = "ExecuteExternalScript.ExternalScriptPath";

    public static final String SCRIPT_NAME = "ExecuteExternalScript.ScriptName";

    @Override
    public SampleResult sample(Entry entry) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.setEncodingAndType("UTF-8");
        boolean isSuccess = false;
        String responseData = "";
        try {
            String scriptPath = getScriptPath();
            result.setSamplerData("执行外部脚本：" + scriptPath);
            result.sampleStart();
            responseData = runExternalScript(scriptPath);
            isSuccess = "success".equals(responseData);
        } catch (Exception e) {
            // 异常后，判断是否已开始统计sample时间，没有则开始统计
            if (result.isStampedAtStart()) {
                result.sampleStart();
            }
            responseData = ExceptionUtil.getStackTrace(e);
        } finally {
            result.sampleEnd();
            result.setSuccessful(isSuccess);
            result.setResponseData(responseData, "UTF-8");
        }
        return result;
    }

    private String getExternalScriptPath() {
        return getPropertyAsString(EXTERNAL_SCRIPT_PATH);
    }

    private String getScriptName() {
        return getPropertyAsString(SCRIPT_NAME);
    }

    private String getScriptPath() {
        String path = PathUtil.pathJoin(getExternalScriptPath(), getScriptName());
        return path.replace("\\", "/");
    }

    private String runExternalScript(String scriptAbsPath)
            throws IllegalUserActionException, JMeterEngineException, IOException {
        File file = new File(scriptAbsPath);
        HashTree tree = SaveService.loadTree(file);

        JMeterTreeModel treeModel = new JMeterTreeModel(new TestPlan());
        JMeterTreeNode root = (JMeterTreeNode) treeModel.getRoot();
        treeModel.addSubTree(tree, root);

        HashTree clonedTree = JMeter.convertSubTree(tree, true);

        JMeterEngine engine = new StandardJMeterEngine();
        engine.configure(clonedTree);
        engine.runTest();
        return "success";
    }


}
