package org.apache.jmeter.samplers.gui;


import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.JMeterScriptSampler;
import org.apache.jmeter.testelement.TestElement;
import pers.kelvin.util.GuiUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author KelvinYe
 */
public class JMeterScriptSamplerGui extends AbstractSamplerGui {

    private JTextField externalScriptPathField;
    private JTextField scriptNameField;
    private JTextField propsNameSuffixField;
    private JComboBox<String> syncToProps;
    private JComboBox<String> syncToVars;
    private JComboBox<String> printToConsoleComboBox;

    public JMeterScriptSamplerGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);

        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBorder(GuiUtil.createTitledBorder("配置外部脚本信息"));

        bodyPanel.add(getExternalScriptPathLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getExternalScriptPathTextField(), GuiUtil.GridBag.editorConstraints);

        bodyPanel.add(getScriptNameLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getScriptNameTextField(), GuiUtil.GridBag.editorConstraints);

        bodyPanel.add(getPropsNameSuffixLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getPropsNameSuffixTextField(), GuiUtil.GridBag.editorConstraints);

        bodyPanel.add(getSyncToPropsLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getSyncToPropsComboBox(), GuiUtil.GridBag.editorConstraints);

        bodyPanel.add(getSyncToVarsLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getSyncToVarsComboBox(), GuiUtil.GridBag.editorConstraints);

        bodyPanel.add(getPrintToConsoleLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getPrintToConsoleComboBox(), GuiUtil.GridBag.editorConstraints);

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(bodyPanel);

        add(mainPanel, BorderLayout.CENTER);
        add(getNotePanel(), BorderLayout.SOUTH);
    }

    @Override
    public String getStaticLabel() {
        return "JMeterScript取样器";
    }


    @Override
    public String getLabelResource() {
        return null;
    }


    @Override
    public TestElement createTestElement() {
        JMeterScriptSampler el = new JMeterScriptSampler();
        modifyTestElement(el);
        return el;
    }

    /**
     * 将数据从GUI元素移动到TestElement
     */
    @Override
    public void modifyTestElement(TestElement el) {
        super.configureTestElement(el);
        el.setProperty(JMeterScriptSampler.EXTERNAL_SCRIPT_PATH, externalScriptPathField.getText());
        el.setProperty(JMeterScriptSampler.SCRIPT_NAME, scriptNameField.getText());
        el.setProperty(JMeterScriptSampler.PROPS_NAME_SUFFIX, propsNameSuffixField.getText());
        el.setProperty(JMeterScriptSampler.SYNC_TO_PROPS, (String) syncToProps.getSelectedItem());
        el.setProperty(JMeterScriptSampler.SYNC_TO_VARS, (String) syncToVars.getSelectedItem());
        el.setProperty(JMeterScriptSampler.PRINT_TO_CONSOLE, (String) printToConsoleComboBox.getSelectedItem());
    }

    /**
     * 将数据设置到GUI元素中
     */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        externalScriptPathField.setText(el.getPropertyAsString(JMeterScriptSampler.EXTERNAL_SCRIPT_PATH));
        scriptNameField.setText(el.getPropertyAsString(JMeterScriptSampler.SCRIPT_NAME));
        propsNameSuffixField.setText(el.getPropertyAsString(JMeterScriptSampler.PROPS_NAME_SUFFIX));
        syncToProps.setSelectedItem(el.getPropertyAsString(JMeterScriptSampler.SYNC_TO_PROPS));
        syncToVars.setSelectedItem(el.getPropertyAsString(JMeterScriptSampler.SYNC_TO_VARS));
        printToConsoleComboBox.setSelectedItem(el.getPropertyAsString(JMeterScriptSampler.PRINT_TO_CONSOLE));
    }

    @Override
    public void clearGui() {
        super.clearGui();
        externalScriptPathField.setText("");
        scriptNameField.setText("");
        propsNameSuffixField.setText("");
        syncToProps.setSelectedItem("true");
        syncToVars.setSelectedItem("false");
        printToConsoleComboBox.setSelectedItem("false");
    }

    private Component getExternalScriptPathTextField() {
        if (externalScriptPathField == null) {
            externalScriptPathField = GuiUtil.createTextField(JMeterScriptSampler.EXTERNAL_SCRIPT_PATH);
        }
        return externalScriptPathField;
    }

    private Component getExternalScriptPathLabel() {
        return GuiUtil.createLabel("脚本目录路径：", getExternalScriptPathTextField());
    }

    private Component getScriptNameTextField() {
        if (scriptNameField == null) {
            scriptNameField = GuiUtil.createTextField(JMeterScriptSampler.SCRIPT_NAME);
        }
        return scriptNameField;
    }

    private Component getScriptNameLabel() {
        return GuiUtil.createLabel("脚本名称：", getScriptNameTextField());
    }

    private Component getPropsNameSuffixTextField() {
        if (propsNameSuffixField == null) {
            propsNameSuffixField = GuiUtil.createTextField(JMeterScriptSampler.PROPS_NAME_SUFFIX);
        }
        return propsNameSuffixField;
    }

    private Component getPropsNameSuffixLabel() {
        return GuiUtil.createLabel("增量属性名称后缀：", getPropsNameSuffixTextField());
    }

    private Component getSyncToPropsComboBox() {
        if (syncToProps == null) {
            syncToProps = GuiUtil.createComboBox(JMeterScriptSampler.SYNC_TO_PROPS);
            syncToProps.addItem("true");
            syncToProps.addItem("false");
        }
        return syncToProps;
    }

    private Component getSyncToPropsLabel() {
        return GuiUtil.createLabel(
                "同步增量 vars至 props：", syncToProps);
    }

    private Component getSyncToVarsComboBox() {
        if (syncToVars == null) {
            syncToVars = GuiUtil.createComboBox(JMeterScriptSampler.SYNC_TO_VARS);
            syncToVars.addItem("false");
            syncToVars.addItem("true");
        }
        return syncToVars;
    }

    private Component getSyncToVarsLabel() {
        return GuiUtil.createLabel(
                "同步 vars：", syncToVars);
    }

    private Component getPrintToConsoleComboBox() {
        if (printToConsoleComboBox == null) {
            printToConsoleComboBox = GuiUtil.createComboBox(JMeterScriptSampler.PRINT_TO_CONSOLE);
            printToConsoleComboBox.addItem("false");
            printToConsoleComboBox.addItem("true");
        }
        return printToConsoleComboBox;
    }

    private Component getPrintToConsoleLabel() {
        return GuiUtil.createLabel(
                "打印 Result到控制台：", printToConsoleComboBox);
    }

    private Component getNotePanel() {
        String note = "说明：\n" +
                "1. 【脚本目录路径】：请使用变量动态获取\n" +
                "2. 【脚本名称】：需要包含.jmx\n" +
                "3. 【同步增量 vars至 props】：将外部脚本中新增的 var放入 prop中\n" +
                "4. 【同步 vars】：将调用者的 vars带入外部脚本中（不会覆盖外部脚本中已存在的key），执行结束时将外部脚本新增的 var带回给调用者的 vars中\n" +
                "5. 【增量属性名称后缀】：格式为 属性名称_后缀";
        return GuiUtil.createNotePanel(note, this.getBackground());
    }

}
