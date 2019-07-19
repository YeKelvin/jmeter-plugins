package org.apache.jmeter.visualizers.gui;


import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.visualizers.ReportCollector;
import pers.kelvin.util.GuiUtil;

import javax.swing.*;
import java.awt.*;

public class LocalHtmlReportGui extends AbstractListenerGui {

    private JTextField reportNameTextField;
    private JComboBox<String> isAppendComboBox;

    public LocalHtmlReportGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);

        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBorder(GuiUtil.createTitledBorder("配置HTML报告"));

        bodyPanel.add(getReportNameLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getReportNameTextField(), GuiUtil.GridBag.editorConstraints);

        bodyPanel.add(getIsAppendLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getIsAppendComboBox(), GuiUtil.GridBag.editorConstraints);

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(bodyPanel);
        mainPanel.add(getNoteJPanel());

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

    private Component getReportNameTextField() {
        if (reportNameTextField == null) {
            reportNameTextField = GuiUtil.createTextField(ReportCollector.REPORT_NAME);
        }
        return reportNameTextField;
    }

    private Component getReportNameLabel() {
        return GuiUtil.createLabel("报告名称：", getReportNameTextField());
    }

    private Component getIsAppendComboBox() {
        if (isAppendComboBox == null) {
            isAppendComboBox = GuiUtil.createComboBox(ReportCollector.IS_APPEND);
            isAppendComboBox.addItem("false");
            isAppendComboBox.addItem("true");
        }
        return isAppendComboBox;
    }

    private Component getIsAppendLabel() {
        return GuiUtil.createLabel("是否追加写报告：", getIsAppendComboBox());
    }

    private Component getNoteJPanel() {
        String note = "\n说明：\n" +
                "1. 测试报告的路径为 ${JMETER_HOME}/htmlreport/${reportName}.html；\n" +
                "2. 执行前必须先在 ${JMETER_HOME} 下创建 htmlreport 目录；\n" +
                "3. Non-Gui模式命令解释：\n" +
                "       a. 存在 -JreportName 参数时，优先读取 ${__P(reportName)} HTML报告名称；\n" +
                "       b. 存在 -JisAppend 参数时，优先读取 ${__P(isAppend)} 追加模式；\n" +
                "       c. 存在 -JdataFileName 参数时，优先读取 ${__P(dataFileName)} 数据文件名称。";
        return GuiUtil.createNotePanel(note, this.getBackground());
    }
}
