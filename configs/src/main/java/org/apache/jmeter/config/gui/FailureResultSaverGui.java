package org.apache.jmeter.config.gui;

import org.apache.jmeter.common.jmeter.JMeterGuiUtil;
import org.apache.jmeter.config.FailureResultSaver;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;

import javax.swing.*;
import java.awt.*;


/**
 * @author Kelvin.Ye
 */
public class FailureResultSaverGui extends AbstractConfigGui {

    private JTextField logPathTextField;
    private JTextField errorClassificationTextField;
    private JTextField excludeTextField;

    public FailureResultSaverGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);
        add(createBodyPanel(), BorderLayout.CENTER);
    }

    @Override
    public String getStaticLabel() {
        return "失败请求保存器";
    }


    @Override
    public String getLabelResource() {
        return null;
    }


    @Override
    public TestElement createTestElement() {
        FailureResultSaver el = new FailureResultSaver();
        modifyTestElement(el);
        return el;
    }

    /**
     * GUI -> TestElement
     */
    @Override
    public void modifyTestElement(TestElement el) {
        super.configureTestElement(el);
        el.setProperty(FailureResultSaver.LOG_PATH, logPathTextField.getText());
        el.setProperty(FailureResultSaver.ERROR_CLASSIFICATION, errorClassificationTextField.getText());
        el.setProperty(FailureResultSaver.EXCLUDE, excludeTextField.getText());
    }

    /**
     * TestElement -> GUI
     */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        logPathTextField.setText(el.getPropertyAsString(FailureResultSaver.LOG_PATH));
        errorClassificationTextField.setText(el.getPropertyAsString(FailureResultSaver.ERROR_CLASSIFICATION));
        excludeTextField.setText(el.getPropertyAsString(FailureResultSaver.EXCLUDE));
    }

    @Override
    public void clearGui() {
        super.clearGui();
        logPathTextField.setText("");
        errorClassificationTextField.setText("");
        excludeTextField.setText("");
    }

    private Component createLogPathTextField() {
        if (logPathTextField == null) {
            logPathTextField = JMeterGuiUtil.createTextField(FailureResultSaver.LOG_PATH);
        }
        return logPathTextField;
    }

    private Component createLogPathLabel() {
        return JMeterGuiUtil.createLabel("日志路径：", createLogPathTextField());
    }

    private Component createErrorClassificationTextField() {
        if (errorClassificationTextField == null) {
            errorClassificationTextField = JMeterGuiUtil.createTextField(FailureResultSaver.ERROR_CLASSIFICATION);
        }
        return errorClassificationTextField;
    }

    private Component createErrorClassificationLabel() {
        return JMeterGuiUtil.createLabel("错误分类：", createErrorClassificationTextField());
    }

    private Component createExcludeTextField() {
        if (excludeTextField == null) {
            excludeTextField = JMeterGuiUtil.createTextField(FailureResultSaver.EXCLUDE);
        }
        return excludeTextField;
    }

    private Component createExcludeLabel() {
        return JMeterGuiUtil.createLabel("排除指定错误（逗号分隔）：", createExcludeTextField());
    }

    private Component createBodyPanel() {
        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBorder(JMeterGuiUtil.createTitledBorder("配置错误日志信息"));
        bodyPanel.add(createLogPathLabel(), JMeterGuiUtil.GridBag.labelConstraints);
        bodyPanel.add(createLogPathTextField(), JMeterGuiUtil.GridBag.editorConstraints);
        bodyPanel.add(createErrorClassificationLabel(), JMeterGuiUtil.GridBag.labelConstraints);
        bodyPanel.add(createErrorClassificationTextField(), JMeterGuiUtil.GridBag.editorConstraints);
        bodyPanel.add(createExcludeLabel(), JMeterGuiUtil.GridBag.labelConstraints);
        bodyPanel.add(createExcludeTextField(), JMeterGuiUtil.GridBag.editorConstraints);

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(bodyPanel);
        return mainPanel;
    }

}
