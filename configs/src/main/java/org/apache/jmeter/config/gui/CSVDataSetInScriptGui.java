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
    private static final int H_GAP = 5;
    private static final int V_GAP = 10;
    private static final int LABEL_WIDTH = 100;
    private static final int LABEL_HEIGHT = 10;

    private JTextField variableNamesTextField;
    private JSyntaxTextArea dataTextArea;

    public CSVDataSetInScriptGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());

        VerticalPanel configPanel = new VerticalPanel();
        configPanel.setBorder(GuiUtil.createTitledBorder("Configure the Data Source"));
        configPanel.add(getVariableNamesPanel());

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(makeTitlePanel());
        mainPanel.add(configPanel);

        add(mainPanel, BorderLayout.NORTH);
        add(getDataPanel(), BorderLayout.CENTER);
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

    private JPanel getVariableNamesPanel() {
        variableNamesTextField = new JTextField(10);
        variableNamesTextField.setName(CSVDataSetInScript.VARIABLE_NAMES);

        JLabel label = GuiUtil.createTextFieldLabel("VariableNames:", variableNamesTextField,LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(variableNamesTextField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getDataPanel() {
        dataTextArea = JSyntaxTextArea.getInstance(20, 20);
        dataTextArea.setName(CSVDataSetInScript.DATA);

        JLabel label = new JLabel("CSV Data:");
        label.setLabelFor(dataTextArea);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.NORTH);
        panel.add(JTextScrollPane.getInstance(dataTextArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel getNotePanel() {
        String note = "说明：\n" +
                "1. 以 “，” 逗号作为引用名和数据的分隔符；\n" +
                "2. 请将线程组设置为无限循环，数据遍历完毕时线程组将自动停止循环。";
        JTextArea textArea = new JTextArea(note);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setBackground(this.getBackground());

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(textArea, BorderLayout.CENTER);
        return panel;
    }

}


