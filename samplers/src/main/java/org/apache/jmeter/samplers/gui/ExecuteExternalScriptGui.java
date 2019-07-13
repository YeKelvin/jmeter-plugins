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
    private static final int H_GAP = 5;
    private static final int V_GAP = 10;
    private static final int LABEL_WIDTH = 100;
    private static final int LABEL_HEIGHT = 10;

    private JTextField externalScriptPathField;
    private JTextField scriptNameField;
    private JTextField propsNameSuffixField;

    public ExecuteExternalScriptGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(getExternalScriptPathPanel());
        mainPanel.add(getScriptNamePanel());
        mainPanel.add(getPropsNameSuffixPanel());

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
    }

    @Override
    public void clearGui() {
        super.clearGui();
        externalScriptPathField.setText("");
        scriptNameField.setText("");
        propsNameSuffixField.setText("");
    }

    private JPanel getExternalScriptPathPanel() {
        externalScriptPathField = new JTextField(10);
        externalScriptPathField.setName(ExecuteExternalScript.EXTERNAL_SCRIPT_PATH);

        JLabel label = GuiUtil.createTextFieldLabel("脚本目录路径：", externalScriptPathField, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(externalScriptPathField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getScriptNamePanel() {
        scriptNameField = new JTextField(10);
        scriptNameField.setName(ExecuteExternalScript.SCRIPT_NAME);

        JLabel label = GuiUtil.createTextFieldLabel("脚本名称：", scriptNameField, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(scriptNameField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getPropsNameSuffixPanel() {
        propsNameSuffixField = new JTextField(10);
        propsNameSuffixField.setName(ExecuteExternalScript.PROPS_NAME_SUFFIX);

        JLabel label = GuiUtil.createTextFieldLabel("JMeter属性名称后缀：", propsNameSuffixField, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(propsNameSuffixField, BorderLayout.CENTER);
        return panel;
    }
}
