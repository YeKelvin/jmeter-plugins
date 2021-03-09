package org.apache.jmeter.config.gui;

import org.apache.jmeter.common.utils.GuiUtil;
import org.apache.jmeter.config.TraversalEmptyValue;
import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.gui.util.JTextScrollPane;
import org.apache.jmeter.testelement.TestElement;

import javax.swing.*;
import java.awt.*;

/**
 * @author KelvinYe
 * Date: 2018-04-17
 * Time: 11:10
 */
public class TraversalEmptyValueGui extends AbstractConfigGui {

    private JComboBox<String> blankTypeComboBox;
    private JSyntaxTextArea paramsTextArea;
    private JSyntaxTextArea emptyCheckExpressionTextArea;

    public TraversalEmptyValueGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);
        add(createJTabbedPane(), BorderLayout.CENTER);
    }

    @Override
    public String getStaticLabel() {
        return "空值遍历配置器";
    }


    @Override
    public String getLabelResource() {
        return null;
    }


    @Override
    public TestElement createTestElement() {
        TraversalEmptyValue dataSet = new TraversalEmptyValue();
        modifyTestElement(dataSet);
        return dataSet;
    }

    /**
     * 将数据从GUI元素移动到TestElement
     */
    @Override
    public void modifyTestElement(TestElement el) {
        super.configureTestElement(el);
        el.setProperty(TraversalEmptyValue.BLANK_TYPE, (String) blankTypeComboBox.getSelectedItem());
        el.setProperty(TraversalEmptyValue.PATAMS, paramsTextArea.getText());
        el.setProperty(TraversalEmptyValue.EMPTY_CHECK_EXPRESSION, emptyCheckExpressionTextArea.getText());
    }

    /**
     * 将数据设置到GUI元素中
     */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        blankTypeComboBox.setSelectedItem(el.getPropertyAsString(TraversalEmptyValue.BLANK_TYPE));
        paramsTextArea.setInitialText(el.getPropertyAsString(TraversalEmptyValue.PATAMS));
        paramsTextArea.setCaretPosition(0);
        emptyCheckExpressionTextArea.setInitialText(el.getPropertyAsString(TraversalEmptyValue.EMPTY_CHECK_EXPRESSION));
        emptyCheckExpressionTextArea.setCaretPosition(0);
    }

    @Override
    public void clearGui() {
        super.clearGui();
        blankTypeComboBox.setSelectedItem("");
        paramsTextArea.setInitialText("");
        emptyCheckExpressionTextArea.setInitialText("");
    }

    private Component createBlankTypeComboBox() {
        if (blankTypeComboBox == null) {
            blankTypeComboBox = GuiUtil.createComboBox(TraversalEmptyValue.BLANK_TYPE);
            blankTypeComboBox.addItem("null");
            blankTypeComboBox.addItem("\"\"");
        }
        return blankTypeComboBox;
    }

    private Component createBlankTypeLabel() {
        return GuiUtil.createLabel("空类型：", createBlankTypeComboBox());
    }

    private Component createParamsTextArea() {
        if (paramsTextArea == null) {
            paramsTextArea = GuiUtil.createTextArea(TraversalEmptyValue.PATAMS, 20);
        }
        return paramsTextArea;
    }

    private Component createParamsLabel() {
        return GuiUtil.createLabel("请求报文：", createParamsTextArea());
    }

    private Component createParamsPanel() {
        return JTextScrollPane.getInstance((JSyntaxTextArea) createParamsTextArea());
    }

    private Component createEmptyCheckExpressionTextArea() {
        if (emptyCheckExpressionTextArea == null) {
            emptyCheckExpressionTextArea = GuiUtil.createTextArea(TraversalEmptyValue.EMPTY_CHECK_EXPRESSION, 20);
        }
        return emptyCheckExpressionTextArea;

    }

    private Component createEmptyCheckExpressionLabel() {
        return GuiUtil.createLabel("预期结果：", createEmptyCheckExpressionTextArea());
    }

    private Component createEmptyCheckExpressionPanel() {
        return JTextScrollPane.getInstance((JSyntaxTextArea) createEmptyCheckExpressionTextArea());
    }

    private Component createJTabbedPane() {
        JPanel interfacePanel = new JPanel(new GridBagLayout());
        interfacePanel.setBorder(GuiUtil.createTitledBorder("配置非空校验信息"));
        interfacePanel.add(createBlankTypeLabel(), GuiUtil.GridBag.labelConstraints);
        interfacePanel.add(createBlankTypeComboBox(), GuiUtil.GridBag.editorConstraints);
        interfacePanel.add(createParamsLabel(), GuiUtil.GridBag.labelConstraints);
        interfacePanel.add(GuiUtil.createBlankPanel(), GuiUtil.GridBag.editorConstraints);
        interfacePanel.add(createParamsPanel(), GuiUtil.GridBag.multiLineEditorConstraints);
        interfacePanel.add(createEmptyCheckExpressionLabel(), GuiUtil.GridBag.labelConstraints);
        interfacePanel.add(GuiUtil.createBlankPanel(), GuiUtil.GridBag.editorConstraints);
        interfacePanel.add(createEmptyCheckExpressionPanel(), GuiUtil.GridBag.multiLineEditorConstraints);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("非空校验配置", interfacePanel);
        tabbedPane.add("说明", createNoteArea());

        return tabbedPane;
    }

    private Component createNoteArea() {
        String note =
                "1. 请将线程组设置为无限循环，数据遍历完毕时线程组将自动停止循环\n" +
                        "2. 请求报文变量名=params，预期结果变量名=expression，当前 JsonPath变量名=jsonPath\n" +
                        "3. 该插件中数据引用变量或函数不会替换为具体的值，请在使用的位置利用 ${__eval(${params})} 函数替换";
        return GuiUtil.createNoteArea(note, this.getBackground());
    }

}


