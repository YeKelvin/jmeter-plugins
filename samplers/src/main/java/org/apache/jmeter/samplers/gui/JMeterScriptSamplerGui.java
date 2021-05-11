package org.apache.jmeter.samplers.gui;


import org.apache.jmeter.common.utils.GuiUtil;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.JMeterScriptSampler;
import org.apache.jmeter.testelement.TestElement;

import javax.swing.*;
import java.awt.*;

/**
 * @author KelvinYe
 */
public class JMeterScriptSamplerGui extends AbstractSamplerGui {

    private JTextField scriptPathField;
    private JTextField scriptNameField;
    private JComboBox<String> syncToProps;
    private JComboBox<String> syncToVars;

    public JMeterScriptSamplerGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);
        add(createBodyPanel(), BorderLayout.CENTER);
        add(createNoteArea(), BorderLayout.SOUTH);
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
        el.setProperty(JMeterScriptSampler.SCRIPT_PATH, scriptPathField.getText());
        el.setProperty(JMeterScriptSampler.SCRIPT_NAME, scriptNameField.getText());
        el.setProperty(JMeterScriptSampler.SYNC_TO_PROPS, (String) syncToProps.getSelectedItem());
        el.setProperty(JMeterScriptSampler.SYNC_TO_VARS, (String) syncToVars.getSelectedItem());
    }

    /**
     * 将数据设置到GUI元素中
     */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        scriptPathField.setText(el.getPropertyAsString(JMeterScriptSampler.SCRIPT_PATH));
        scriptNameField.setText(el.getPropertyAsString(JMeterScriptSampler.SCRIPT_NAME));
        syncToProps.setSelectedItem(el.getPropertyAsString(JMeterScriptSampler.SYNC_TO_PROPS));
        syncToVars.setSelectedItem(el.getPropertyAsString(JMeterScriptSampler.SYNC_TO_VARS));
    }

    @Override
    public void clearGui() {
        super.clearGui();
        scriptPathField.setText("");
        scriptNameField.setText("");
        syncToProps.setSelectedItem("true");
        syncToVars.setSelectedItem("false");
    }

    private Component createScriptPathTextField() {
        if (scriptPathField == null) {
            scriptPathField = GuiUtil.createTextField(JMeterScriptSampler.SCRIPT_PATH);
        }
        return scriptPathField;
    }

    private Component createScriptPathLabel() {
        return GuiUtil.createLabel("脚本目录：", createScriptPathTextField());
    }

    private Component createScriptNameTextField() {
        if (scriptNameField == null) {
            scriptNameField = GuiUtil.createTextField(JMeterScriptSampler.SCRIPT_NAME);
        }
        return scriptNameField;
    }

    private Component createScriptNameLabel() {
        return GuiUtil.createLabel("脚本名称：", createScriptNameTextField());
    }

    private Component createSyncToPropsComboBox() {
        if (syncToProps == null) {
            syncToProps = GuiUtil.createComboBox(JMeterScriptSampler.SYNC_TO_PROPS);
            syncToProps.addItem("true");
            syncToProps.addItem("false");
        }
        return syncToProps;
    }

    private Component createSyncToPropsLabel() {
        return GuiUtil.createLabel(
                "同步增量vars至props：", syncToProps);
    }

    private Component createSyncToVarsComboBox() {
        if (syncToVars == null) {
            syncToVars = GuiUtil.createComboBox(JMeterScriptSampler.SYNC_TO_VARS);
            syncToVars.addItem("false");
            syncToVars.addItem("true");
        }
        return syncToVars;
    }

    private Component createSyncToVarsLabel() {
        return GuiUtil.createLabel(
                "同步vars至目标脚本：", syncToVars);
    }

    private Component createBodyPanel() {
        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBorder(GuiUtil.createTitledBorder("配置外部脚本信息"));

        bodyPanel.add(createScriptPathLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(createScriptPathTextField(), GuiUtil.GridBag.editorConstraints);

        bodyPanel.add(createScriptNameLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(createScriptNameTextField(), GuiUtil.GridBag.editorConstraints);

        bodyPanel.add(createSyncToPropsLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(createSyncToPropsComboBox(), GuiUtil.GridBag.editorConstraints);

        bodyPanel.add(createSyncToVarsLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(createSyncToVarsComboBox(), GuiUtil.GridBag.editorConstraints);

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(bodyPanel);
        return mainPanel;
    }

    private Component createNoteArea() {
        String note =
                "1.【脚本目录】: 请使用环境变量\n" +
                        "2.【脚本名称】: 需要包含.jmx\n" +
                        "3.【增量同步vars至props】: 将目标脚本中新增的局部变量同步至全局变量中\n" +
                        "4.【同步vars至子脚本】: 将调用者的局部变量同步至目标脚本中（不会覆盖目标脚本中已存在的key），\n" +
                        "                     执行结束时将目标脚本新增的局部变量返回给调用者\n";
        return GuiUtil.createNoteArea(note, this.getBackground());
    }

}
