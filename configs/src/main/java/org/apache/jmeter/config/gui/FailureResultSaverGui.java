package org.apache.jmeter.config.gui;

import org.apache.jmeter.common.utils.GuiUtil;
import org.apache.jmeter.config.FailureResultSaver;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;

import javax.swing.*;
import java.awt.*;


/**
 * @author KelvinYe
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
     * 将数据从GUI元素移动到TestElement
     */
    @Override
    public void modifyTestElement(TestElement el) {
        super.configureTestElement(el);
        el.setProperty(FailureResultSaver.LOG_PATH, logPathTextField.getText());
        el.setProperty(FailureResultSaver.ERROR_CLASSIFICATION, errorClassificationTextField.getText());
        el.setProperty(FailureResultSaver.EXCLUDE, excludeTextField.getText());
    }

    /**
     * 将数据设置到GUI元素中
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
            logPathTextField = GuiUtil.createTextField(FailureResultSaver.LOG_PATH);
        }
        return logPathTextField;
    }

    private Component createLogPathLabel() {
        return GuiUtil.createLabel("日志路径：", createLogPathTextField());
    }

    private Component createErrorClassificationTextField() {
        if (errorClassificationTextField == null) {
            errorClassificationTextField = GuiUtil.createTextField(FailureResultSaver.ERROR_CLASSIFICATION);
        }
        return errorClassificationTextField;
    }

    private Component createErrorClassificationLabel() {
        return GuiUtil.createLabel("错误分类：", createErrorClassificationTextField());
    }

    private Component createExcludeTextField() {
        if (excludeTextField == null) {
            excludeTextField = GuiUtil.createTextField(FailureResultSaver.EXCLUDE);
        }
        return excludeTextField;
    }

    private Component createExcludeLabel() {
        return GuiUtil.createLabel("排除指定错误（逗号分隔）：", createExcludeTextField());
    }

    private Component createBodyPanel() {
        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBorder(GuiUtil.createTitledBorder("配置错误日志信息"));
        bodyPanel.add(createLogPathLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(createLogPathTextField(), GuiUtil.GridBag.editorConstraints);
        bodyPanel.add(createErrorClassificationLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(createErrorClassificationTextField(), GuiUtil.GridBag.editorConstraints);
        bodyPanel.add(createExcludeLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(createExcludeTextField(), GuiUtil.GridBag.editorConstraints);

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(bodyPanel);
        return mainPanel;
    }

}
