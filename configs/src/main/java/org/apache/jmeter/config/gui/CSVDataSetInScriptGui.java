package org.apache.jmeter.config.gui;

import org.apache.jmeter.config.CSVDataSetInScript;
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
public class CSVDataSetInScriptGui extends AbstractConfigGui {
    private JTextField variableNamesTextField;
    private JSyntaxTextArea dataTextArea;

    public CSVDataSetInScriptGui() {
        super();
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());

        Box box = Box.createVerticalBox();
        box.add(makeTitlePanel());
        box.add(createVariableNamesPanel());
        add(box, BorderLayout.NORTH);

        JPanel panel = createDataPanel();
        add(panel, BorderLayout.CENTER);
        add(Box.createVerticalStrut(panel.getPreferredSize().height), BorderLayout.WEST);
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

    private JPanel createVariableNamesPanel() {
        variableNamesTextField = new JTextField(10);
        variableNamesTextField.setName(CSVDataSetInScript.VARIABLE_NAMES);

        JLabel label = new JLabel(CSVDataSetInScript.VARIABLE_NAMES);
        label.setLabelFor(variableNamesTextField);

        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(label, BorderLayout.WEST);
        panel.add(variableNamesTextField, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDataPanel() {
        dataTextArea = JSyntaxTextArea.getInstance(20, 20);

        JLabel label = new JLabel(CSVDataSetInScript.DATA);
        label.setLabelFor(dataTextArea);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(label, BorderLayout.NORTH);
        panel.add(JTextScrollPane.getInstance(dataTextArea), BorderLayout.CENTER);

        String note = "说明：\n" +
                "1. 以 “，” 逗号作为引用名和数据的分隔符\n" +
                "2. 请将线程组设置为无限循环，数据遍历完毕时线程组将自动停止循环";
        JTextArea textArea = new JTextArea(note);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setBackground(this.getBackground());
        panel.add(textArea, BorderLayout.SOUTH);

        return panel;
    }

}


