package org.apache.jmeter.visualizers.gui;


import org.apache.jmeter.common.jmeter.JMeterGuiUtil;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.visualizers.ReportCollector;

import javax.swing.*;
import java.awt.*;

/**
 * @author Kaiwen.Ye
 */
public class LocalHtmlReportGui extends AbstractListenerGui {

    private static final String NOTE =
            "1、测试报告生成路径为 ${JMETER_HOME}/htmlreport/${reportName}\n" +
                    "2、执行前必须先在 ${JMETER_HOME} 下创建 htmlreport 目录\n" +
                    "3、Non-Gui命令说明：\n" +
                    "       3.1、存在 -JreportName 选项时，优先读取 ${__P(reportName)} HTML报告名称\n" +
                    "       3.2、存在 -JisAppend 选项时，优先读取 ${__P(isAppend)} 追加模式\n";

    private JTextField reportNameTextField;
    private JComboBox<String> isAppendComboBox;

    public LocalHtmlReportGui() {
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
        return "HTML 报告";
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

    /**
     * TestElement -> GUI
     */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        reportNameTextField.setText(el.getPropertyAsString(ReportCollector.REPORT_NAME));
        isAppendComboBox.setSelectedItem(el.getPropertyAsString(ReportCollector.IS_APPEND));
    }

    /**
     * GUI -> TestElement
     */
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

    private Component createReportNameTextField() {
        if (reportNameTextField == null) {
            reportNameTextField = JMeterGuiUtil.createTextField(ReportCollector.REPORT_NAME);
        }
        return reportNameTextField;
    }

    private Component createReportNameLabel() {
        return JMeterGuiUtil.createLabel("报告名称：", createReportNameTextField());
    }

    private Component createIsAppendComboBox() {
        if (isAppendComboBox == null) {
            isAppendComboBox = JMeterGuiUtil.createComboBox(ReportCollector.IS_APPEND);
            isAppendComboBox.addItem("false");
            isAppendComboBox.addItem("true");
        }
        return isAppendComboBox;
    }

    private Component createIsAppendLabel() {
        return JMeterGuiUtil.createLabel("追加写报告：", createIsAppendComboBox());
    }

    private Component createBodyPanel() {
        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBorder(JMeterGuiUtil.createTitledBorder("配置HTML报告"));

        bodyPanel.add(createReportNameLabel(), JMeterGuiUtil.GridBag.labelConstraints);
        bodyPanel.add(createReportNameTextField(), JMeterGuiUtil.GridBag.editorConstraints);

        bodyPanel.add(createIsAppendLabel(), JMeterGuiUtil.GridBag.labelConstraints);
        bodyPanel.add(createIsAppendComboBox(), JMeterGuiUtil.GridBag.editorConstraints);

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(bodyPanel);
        return mainPanel;
    }

    private Component createNoteArea() {
        return JMeterGuiUtil.createNoteArea(NOTE, this.getBackground());
    }
}
