package org.apache.jmeter.visualizers.gui;


import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.visualizers.ReportCollector;
import pers.kelvin.util.GuiUtil;

import javax.swing.*;
import java.awt.*;

public class LocalHtmlReportGui extends AbstractListenerGui {
    private static final int H_GAP = 5;
    private static final int V_GAP = 10;
    private static final int LABEL_WIDTH = 100;
    private static final int LABEL_HEIGHT = 10;

    private JTextField reportNameTextField;
    private JComboBox<String> isAppendComboBox;

    public LocalHtmlReportGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);

        VerticalPanel htmlPanel = new VerticalPanel();
        htmlPanel.setBorder(GuiUtil.createTitledBorder("Configure the Data Source"));
        htmlPanel.add(getReportNamePanel());
        htmlPanel.add(getIsAppendPanel());

        VerticalPanel notePanel = new VerticalPanel();
        notePanel.add(getNoteJPanel());

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(htmlPanel);
        mainPanel.add(notePanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    @Override
    public String getStaticLabel() {
        return "Local HTML Report";
    }

    @Override
    public String getLabelResource() {
        return null;
    }

    @Override
    public TestElement createTestElement() {
        ReportCollector info = new ReportCollector();
        modifyTestElement(info);
        return info;
    }

    @Override
    public void configure(TestElement el) {
        super.configure(el);
        reportNameTextField.setText(el.getPropertyAsString(ReportCollector.REPORT_NAME));
        isAppendComboBox.setSelectedItem(el.getPropertyAsString(ReportCollector.IS_APPEND));
    }

    @Override
    public void modifyTestElement(TestElement el) {
        super.configureTestElement(el);
        el.setProperty(ReportCollector.REPORT_NAME, reportNameTextField.getText());
        el.setProperty(ReportCollector.IS_APPEND, (String) isAppendComboBox.getSelectedItem());
    }

    @Override
    public void clearGui() {
        super.clearGui();
        reportNameTextField.setText("");
        isAppendComboBox.setSelectedItem("");
    }

    private JPanel getReportNamePanel() {
        reportNameTextField = new JTextField(10);
        reportNameTextField.setName(ReportCollector.REPORT_NAME);

        JLabel label = GuiUtil.createTextFieldLabel("ReportName:", reportNameTextField, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(reportNameTextField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getIsAppendPanel() {
        isAppendComboBox = new JComboBox<>();
        isAppendComboBox.setName(ReportCollector.IS_APPEND);
        isAppendComboBox.addItem("true");
        isAppendComboBox.addItem("false");

        JLabel label = GuiUtil.createTextFieldLabel("IsAppend:", isAppendComboBox, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(isAppendComboBox, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getNoteJPanel() {
        String note = "\n说明：\n" +
                "1. reportName为测试报告名称，输出路径为 jmeterHome/htmlreport/${reportName}.html；\n" +
                "2. isAppend为是否追加模式写报告，枚举为 true|false；\n" +
                "3. 执行前必须先在 jmeterHome 下创建 htmlreport 目录；\n" +
                "4. Non-Gui模式命令解释：\n" +
                "   a. 存在 -JreportName 参数时，优先读取 ${__P(reportName)} HTML报告名称；\n" +
                "   b. 存在 -JisAppend 参数时，优先读取 ${__P(isAppend)} 追加模式；\n" +
                "   c. 存在 -JdataFileName 参数时，优先读取 ${__P(dataFileName)} 数据文件名称。";
        JTextArea textArea = new JTextArea(note);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setBackground(this.getBackground());

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(textArea, BorderLayout.CENTER);
        return panel;
    }
}
