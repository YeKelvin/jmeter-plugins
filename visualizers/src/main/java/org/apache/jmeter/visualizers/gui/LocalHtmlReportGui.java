package org.apache.jmeter.visualizers.gui;


import org.apache.jmeter.common.utils.GuiUtil;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.visualizers.ReportCollector;

import javax.swing.*;
import java.awt.*;

/**
 * @author Kaiwen.Ye
 */
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

    private Component createReportNameTextField() {
        if (reportNameTextField == null) {
            reportNameTextField = GuiUtil.createTextField(ReportCollector.REPORT_NAME);
        }
        return reportNameTextField;
    }

    private Component createReportNameLabel() {
        return GuiUtil.createLabel("报告名称：", createReportNameTextField());
    }

    private Component createIsAppendComboBox() {
        if (isAppendComboBox == null) {
            isAppendComboBox = GuiUtil.createComboBox(ReportCollector.IS_APPEND);
            isAppendComboBox.addItem("false");
            isAppendComboBox.addItem("true");
        }
        return isAppendComboBox;
    }

    private Component createIsAppendLabel() {
        return GuiUtil.createLabel("追加写报告：", createIsAppendComboBox());
    }

    private Component createBodyPanel() {
        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBorder(GuiUtil.createTitledBorder("配置HTML报告"));

        bodyPanel.add(createReportNameLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(createReportNameTextField(), GuiUtil.GridBag.editorConstraints);

        bodyPanel.add(createIsAppendLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(createIsAppendComboBox(), GuiUtil.GridBag.editorConstraints);

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(bodyPanel);
        return mainPanel;
    }

    private Component createNoteArea() {
        String note =
                "1. 测试报告的路径为 ${JMETER_HOME}/htmlreport/${reportName}；\n" +
                "2. 执行前必须先在 ${JMETER_HOME} 下创建 htmlreport 目录；\n" +
                "3. Non-Gui命令说明：\n" +
                "      a. 存在 -JreportName 选项时，优先读取 ${__P(reportName)} HTML报告名称；\n" +
                "      b. 存在 -JisAppend 选项时，优先读取 ${__P(isAppend)} 追加模式；\n";
        return GuiUtil.createNoteArea(note, this.getBackground());
    }
}
