package org.apache.jmeter.config.gui;

import org.apache.jmeter.config.CSVDataSetInScript;
import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.gui.util.JTextScrollPane;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;
import pers.kelvin.util.GuiUtil;

import javax.swing.*;
import java.awt.*;

/**
 * User: KelvinYe
 * Date: 2018-04-17
 * Time: 11:10
 */
public class CSVDataSetInScriptGui extends AbstractConfigGui {

    private JTextField variableNamesTextField;
    private JSyntaxTextArea dataTextArea;

    public CSVDataSetInScriptGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);

        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBorder(GuiUtil.createTitledBorder("配置CSV数据"));

        bodyPanel.add(getVariableNamesLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getVariableNamesTextField(), GuiUtil.GridBag.editorConstraints);

        bodyPanel.add(getDataLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(GuiUtil.createBlankPanel(), GuiUtil.GridBag.editorConstraints);
        bodyPanel.add(getDataPanel(), GuiUtil.GridBag.fillBottomConstraints);

        add(bodyPanel, BorderLayout.CENTER);
        add(getNotePanel(), BorderLayout.SOUTH);
    }

    @Override
    public String getStaticLabel() {
        return "CSV Date Set In Script";
    }


    @Override
    public String getLabelResource() {
        return null;
    }


    @Override
    public TestElement createTestElement() {
        CSVDataSetInScript dataSet = new CSVDataSetInScript();
        modifyTestElement(dataSet);
        return dataSet;
    }

    /**
     * 将数据从GUI元素移动到TestElement
     */
    @Override
    public void modifyTestElement(TestElement el) {
        super.configureTestElement(el);
        el.setProperty(CSVDataSetInScript.VARIABLE_NAMES, variableNamesTextField.getText());
        el.setProperty(CSVDataSetInScript.DATA, dataTextArea.getText());
    }

    /**
     * 将数据设置到GUI元素中
     */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        variableNamesTextField.setText(el.getPropertyAsString(CSVDataSetInScript.VARIABLE_NAMES));
        dataTextArea.setInitialText(el.getPropertyAsString(CSVDataSetInScript.DATA));
        dataTextArea.setCaretPosition(0);
    }

    @Override
    public void clearGui() {
        super.clearGui();
        variableNamesTextField.setText("");
        dataTextArea.setInitialText("");
    }

    private Component getVariableNamesTextField() {
        if (variableNamesTextField == null) {
            variableNamesTextField = GuiUtil.createTextField(CSVDataSetInScript.VARIABLE_NAMES);
        }
        return variableNamesTextField;
    }

    private Component getVariableNamesLabel() {
        return GuiUtil.createLabel("变量名称：", getVariableNamesTextField());
    }

    private Component getDataTextArea() {
        if (dataTextArea == null) {
            dataTextArea = GuiUtil.createTextArea(CSVDataSetInScript.DATA, 20);
        }
        return dataTextArea;
    }

    private Component getDataLabel() {
        return GuiUtil.createLabel("CSV数据：", getDataTextArea());
    }


    private Component getDataPanel() {
        return JTextScrollPane.getInstance((JSyntaxTextArea) getDataTextArea());
    }

    private Component getNotePanel() {
        String note = "说明：\n" +
                "1. 以 “，” 逗号作为引用名和数据的分隔符；\n" +
                "2. 请将线程组设置为无限循环，数据遍历完毕时线程组将自动停止循环。";
        return GuiUtil.createNotePanel(note, this.getBackground());
    }

}


