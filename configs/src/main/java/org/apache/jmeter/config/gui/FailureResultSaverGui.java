package org.apache.jmeter.config.gui;

import org.apache.jmeter.config.FailureResultSaver;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.common.utils.GuiUtil;

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

        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBorder(GuiUtil.createTitledBorder("配置错误日志信息"));
        bodyPanel.add(getLogPathLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getLogPathTextField(), GuiUtil.GridBag.editorConstraints);
        bodyPanel.add(getErrorClassificationLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getErrorClassificationTextField(), GuiUtil.GridBag.editorConstraints);
        bodyPanel.add(getExcludeLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getExcludeTextField(), GuiUtil.GridBag.editorConstraints);

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(bodyPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    @Override
    public String getStaticLabel() {
        return "错误日志保存器";
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

    private Component getLogPathTextField() {
        if (logPathTextField == null) {
            logPathTextField = GuiUtil.createTextField(FailureResultSaver.LOG_PATH);
        }
        return logPathTextField;
    }

    private Component getLogPathLabel() {
        return GuiUtil.createLabel("日志路径：", getLogPathTextField());
    }

    private Component getErrorClassificationTextField() {
        if (errorClassificationTextField == null) {
            errorClassificationTextField = GuiUtil.createTextField(FailureResultSaver.ERROR_CLASSIFICATION);
        }
        return errorClassificationTextField;
    }

    private Component getErrorClassificationLabel() {
        return GuiUtil.createLabel("错误分类：", getErrorClassificationTextField());
    }

    private Component getExcludeTextField() {
        if (excludeTextField == null) {
            excludeTextField = GuiUtil.createTextField(FailureResultSaver.EXCLUDE);
        }
        return excludeTextField;
    }

    private Component getExcludeLabel() {
        return GuiUtil.createLabel("排除指定错误（逗号分隔）：", getExcludeTextField());
    }

}
