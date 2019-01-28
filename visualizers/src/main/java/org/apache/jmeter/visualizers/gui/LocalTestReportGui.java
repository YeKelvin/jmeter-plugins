package org.apache.jmeter.visualizers.gui;


import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.visualizers.ReportCollector;
import org.apache.jmeter.visualizers.ReportCollector2;

import javax.swing.*;
import java.awt.*;

public class LocalTestReportGui extends AbstractListenerGui {
    private JTextField reportNameTextField;
    private JComboBox<String> isAppendComboBox;

    public LocalTestReportGui() {
        super();
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        Box box = Box.createVerticalBox();
        box.add(makeTitlePanel());
        box.add(createReportNamePanel());
        box.add(createIsAppendPanel());
        box.add(createNote());
        add(box, BorderLayout.NORTH);
    }

    @Override
    public String getStaticLabel() {
        return "Local Test Report";
    }

    @Override
    public String getLabelResource() {
        return null;
    }

    @Override
    public TestElement createTestElement() {
        ReportCollector2 info = new ReportCollector2();
        modifyTestElement(info);
        return info;
    }

    @Override
    public void configure(TestElement el) {
        super.configure(el);
        reportNameTextField.setText(el.getPropertyAsString(ReportCollector.REPORTNAME));
        isAppendComboBox.setSelectedItem(el.getPropertyAsString(ReportCollector.ISAPPEND));
    }

    @Override
    public void modifyTestElement(TestElement el) {
        super.configureTestElement(el);
        el.setProperty(ReportCollector.REPORTNAME, reportNameTextField.getText());
        el.setProperty(ReportCollector.ISAPPEND, (String) isAppendComboBox.getSelectedItem());
    }

    @Override
    public void clearGui() {
        super.clearGui();
        reportNameTextField.setText("");
        isAppendComboBox.setSelectedItem("");
    }

    private JPanel createReportNamePanel() {
        reportNameTextField = new JTextField(10);
        reportNameTextField.setName(ReportCollector.REPORTNAME);
        JLabel label = new JLabel(ReportCollector.REPORTNAME);
        label.setLabelFor(reportNameTextField);
        JPanel jPanel = new JPanel(new BorderLayout(5, 0));
        jPanel.add(label, BorderLayout.WEST);
        jPanel.add(reportNameTextField, BorderLayout.CENTER);
        return jPanel;
    }

    private JPanel createIsAppendPanel() {
        isAppendComboBox = new JComboBox<>();
        isAppendComboBox.setName(ReportCollector.ISAPPEND);
        isAppendComboBox.addItem("true");
        isAppendComboBox.addItem("false");
        JLabel label = new JLabel(ReportCollector.ISAPPEND);
        label.setLabelFor(isAppendComboBox);
        JPanel jPanel = new JPanel(new BorderLayout(5, 0));
        jPanel.add(label, BorderLayout.WEST);
        jPanel.add(isAppendComboBox, BorderLayout.CENTER);
        return jPanel;
    }

    private JTextArea createNote() {
        String note = "\n说明：\n" +
                "1. reportName为测试报告名称，输出路径为 jmeterHome/htmlreport/${reportName}.html\n" +
                "2. isAppend为是否追加模式写报告，枚举为 true|false\n" +
                "3. 执行前必须先在 jmeterHome 下创建 htmlreport 目录\n" +
                "4. Non-Gui模式命令解释：\n" +
                "   a. 存在 -JreportName 参数时，优先读取 ${__P(reportName)} HTML报告名称\n" +
                "   b. 存在 -JisAppend 参数时，优先读取 ${__P(isAppend)} 追加模式\n";
        JTextArea textArea = new JTextArea(note);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setBackground(this.getBackground());
        return textArea;
    }
}
