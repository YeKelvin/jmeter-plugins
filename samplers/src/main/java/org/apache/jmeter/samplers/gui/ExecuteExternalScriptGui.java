package org.apache.jmeter.samplers.gui;


import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.ExecuteExternalScript;
import org.apache.jmeter.testelement.TestElement;
import pers.kelvin.util.GuiUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author KelvinYe
 */
public class ExecuteExternalScriptGui extends AbstractSamplerGui {

    private JTextField externalScriptPathField;
    private JTextField scriptNameField;
    private JTextField propsNameSuffixField;
    private JComboBox<String> isPrintToConsoleComboBox;

    public ExecuteExternalScriptGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());

        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBorder(GuiUtil.createTitledBorder("配置外部脚本信息"));

        bodyPanel.add(getExternalScriptPathLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getExternalScriptPathField(), GuiUtil.GridBag.editorConstraints);

        bodyPanel.add(getScriptNameLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getScriptNameField(), GuiUtil.GridBag.editorConstraints);

        bodyPanel.add(getPropsNameSuffixLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getPropsNameSuffixField(), GuiUtil.GridBag.editorConstraints);

        bodyPanel.add(getIsPrintToConsoleLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getIsPrintToConsoleComboBox(), GuiUtil.GridBag.editorConstraints);

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(makeTitlePanel());
        mainPanel.add(bodyPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    @Override
    public String getStaticLabel() {
        return "Execute External Script";
    }


    @Override
    public String getLabelResource() {
        return null;
    }


    @Override
    public TestElement createTestElement() {
        ExecuteExternalScript el = new ExecuteExternalScript();
        modifyTestElement(el);
        return el;
    }

    /**
     * 将数据从GUI元素移动到TestElement
     */
    @Override
    public void modifyTestElement(TestElement el) {
        super.configureTestElement(el);
        el.setProperty(ExecuteExternalScript.EXTERNAL_SCRIPT_PATH, externalScriptPathField.getText());
        el.setProperty(ExecuteExternalScript.SCRIPT_NAME, scriptNameField.getText());
        el.setProperty(ExecuteExternalScript.PROPS_NAME_SUFFIX, propsNameSuffixField.getText());
        el.setProperty(ExecuteExternalScript.IS_PRINT_TO_CONSOLE, (String) isPrintToConsoleComboBox.getSelectedItem());
    }

    /**
     * 将数据设置到GUI元素中
     */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        externalScriptPathField.setText(el.getPropertyAsString(ExecuteExternalScript.EXTERNAL_SCRIPT_PATH));
        scriptNameField.setText(el.getPropertyAsString(ExecuteExternalScript.SCRIPT_NAME));
        propsNameSuffixField.setText(el.getPropertyAsString(ExecuteExternalScript.PROPS_NAME_SUFFIX));
        isPrintToConsoleComboBox.setSelectedItem(el.getPropertyAsString(ExecuteExternalScript.IS_PRINT_TO_CONSOLE));
    }

    @Override
    public void clearGui() {
        super.clearGui();
        externalScriptPathField.setText("");
        scriptNameField.setText("");
        propsNameSuffixField.setText("");
        isPrintToConsoleComboBox.setSelectedItem("");
    }

    private Component getExternalScriptPathField() {
        if (externalScriptPathField == null) {
            externalScriptPathField = new JTextField(10);
            externalScriptPathField.setName(ExecuteExternalScript.EXTERNAL_SCRIPT_PATH);
        }
        return externalScriptPathField;
    }

    private Component getExternalScriptPathLabel() {
        return GuiUtil.createTextFieldLabel("脚本目录路径：", getExternalScriptPathField());
    }

    private Component getScriptNameField() {
        if (scriptNameField == null) {
            scriptNameField = new JTextField(10);
            scriptNameField.setName(ExecuteExternalScript.SCRIPT_NAME);
        }
        return scriptNameField;
    }

    private Component getScriptNameLabel() {
        return GuiUtil.createTextFieldLabel("脚本名称：", getScriptNameField());
    }

    private Component getPropsNameSuffixField() {
        if (propsNameSuffixField == null) {
            propsNameSuffixField = new JTextField(10);
            propsNameSuffixField.setName(ExecuteExternalScript.PROPS_NAME_SUFFIX);
        }
        return propsNameSuffixField;
    }

    private Component getPropsNameSuffixLabel() {
        return GuiUtil.createTextFieldLabel("JMeter属性名称后缀：", getPropsNameSuffixField());
    }

    private Component getIsPrintToConsoleComboBox() {
        if (isPrintToConsoleComboBox == null) {
            isPrintToConsoleComboBox = new JComboBox<>();
            isPrintToConsoleComboBox.setName(ExecuteExternalScript.IS_PRINT_TO_CONSOLE);
            isPrintToConsoleComboBox.addItem("false");
            isPrintToConsoleComboBox.addItem("true");
        }
        return isPrintToConsoleComboBox;
    }

    private Component getIsPrintToConsoleLabel() {
        return GuiUtil.createTextFieldLabel(
                "是否打印 Result到控制台：", isPrintToConsoleComboBox);
    }

}
