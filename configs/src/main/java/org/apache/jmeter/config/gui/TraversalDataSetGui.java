package org.apache.jmeter.config.gui;

import org.apache.jmeter.common.utils.GuiUtil;
import org.apache.jmeter.config.TraversalDataSet;
import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.gui.util.JTextScrollPane;
import org.apache.jmeter.testelement.TestElement;

import javax.swing.*;
import java.awt.*;

/**
 * User: KelvinYe
 * Date: 2018-04-17
 * Time: 11:10
 */
public class TraversalDataSetGui extends AbstractConfigGui {

    private JTextField variableNamesTextField;
    private JSyntaxTextArea dataTextArea;

    public TraversalDataSetGui() {
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
        return "数据遍历配置器";
    }


    @Override
    public String getLabelResource() {
        return null;
    }


    @Override
    public TestElement createTestElement() {
        TraversalDataSet dataSet = new TraversalDataSet();
        modifyTestElement(dataSet);
        return dataSet;
    }

    /**
     * 将数据从GUI元素移动到TestElement
     */
    @Override
    public void modifyTestElement(TestElement el) {
        super.configureTestElement(el);
        el.setProperty(TraversalDataSet.VARIABLE_NAMES, variableNamesTextField.getText());
        el.setProperty(TraversalDataSet.DATA_SET, dataTextArea.getText());
    }

    /**
     * 将数据设置到GUI元素中
     */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        variableNamesTextField.setText(el.getPropertyAsString(TraversalDataSet.VARIABLE_NAMES));
        dataTextArea.setInitialText(el.getPropertyAsString(TraversalDataSet.DATA_SET));
        dataTextArea.setCaretPosition(0);
    }

    @Override
    public void clearGui() {
        super.clearGui();
        variableNamesTextField.setText("");
        dataTextArea.setInitialText("");
    }

    private Component createVariableNamesTextField() {
        if (variableNamesTextField == null) {
            variableNamesTextField = GuiUtil.createTextField(TraversalDataSet.VARIABLE_NAMES);
        }
        return variableNamesTextField;
    }

    private Component createVariableNamesLabel() {
        return GuiUtil.createLabel("变量名称：", createVariableNamesTextField());
    }

    private Component createDataTextArea() {
        if (dataTextArea == null) {
            dataTextArea = GuiUtil.createTextArea(TraversalDataSet.DATA_SET, 20);
        }
        return dataTextArea;
    }

    private Component createDataLabel() {
        return GuiUtil.createLabel("数据集：", createDataTextArea());
    }


    private Component createDataPanel() {
        return JTextScrollPane.getInstance((JSyntaxTextArea) createDataTextArea());
    }

    private Component createBodyPanel() {
        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBorder(GuiUtil.createTitledBorder("配置CSV数据"));

        bodyPanel.add(createVariableNamesLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(createVariableNamesTextField(), GuiUtil.GridBag.editorConstraints);

        bodyPanel.add(createDataLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(GuiUtil.createBlankPanel(), GuiUtil.GridBag.editorConstraints);
        bodyPanel.add(createDataPanel(), GuiUtil.GridBag.fillBottomConstraints);
        return bodyPanel;
    }

    private Component createNoteArea() {
        String note =
                "1. 以 “,” 逗号作为引用名和数据的分隔符\n" +
                "2. 请将线程组设置为无限循环，数据遍历完毕时线程组将自动停止循环";
        return GuiUtil.createNoteArea(note, this.getBackground());
    }

}


