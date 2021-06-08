package org.apache.jmeter.visualizers.gui;


import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.common.jmeter.JMeterGuiUtil;
import org.apache.jmeter.common.utils.DesktopUtil;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.ReportCollector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author Kaiwen.Ye
 */
public class LocalHtmlReportGui extends AbstractListenerGui implements ActionListener {

    /**
     * Action命令
     */
    private static final String OPEN_HTML_ACTION = "OPEN_HTML";
    private static final String OPEN_DIRECTORY_ACTION = "OPEN_DIRECTORY";

    /**
     * swing组件
     */
    private final JTextField reportNameTextField;
    private final JLabel reportNameLabel;


    private final JComboBox<String> isAppendComboBox;
    private final JLabel isAppendLabel;

    /**
     * HTML目录路径
     */
    private final String reportDirectory;

    /**
     * 插件说明
     */
    private static final String NOTE =
            "1、测试报告生成路径为${JMETER_HOME}/htmlreport/${reportName}\n" +
                    "2、执行前必须先在 ${JMETER_HOME} 下创建 htmlreport 目录\n" +
                    "3、Non-Gui命令说明：\n" +
                    "       3.1、存在 -JreportName 选项时，优先读取 ${__P(reportName)} HTML报告名称\n" +
                    "       3.2、存在 -JisAppend 选项时，优先读取 ${__P(isAppend)} 追加模式\n" +
                    "4、如中文乱码，在 ${JMETER_BIN}/jmeter[.bat|sh]里添加Java启动参数 \"-Dfile.encoding=UTF-8\"\n";

    public LocalHtmlReportGui() {
        reportDirectory = JMeterUtils.getJMeterHome() + File.separator + "htmlreport";

        reportNameTextField = createReportNameTextField();
        reportNameLabel = createReportNameLabel();

        isAppendComboBox = createIsAppendComboBox();
        isAppendLabel = createIsAppendLabel();

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

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals(OPEN_HTML_ACTION)) {
            openHtml();
        } else if (action.equals(OPEN_DIRECTORY_ACTION)) {
            openHtmlDirectory();
        }
    }

    private void openHtml() {
        String reportName = reportNameTextField.getText();
        if (StringUtils.isBlank(reportName)) {
            return;
        }

        DesktopUtil.openFile(reportDirectory + File.separator + reportName);
    }

    private void openHtmlDirectory() {
        DesktopUtil.openFile(reportDirectory);
    }

    private JTextField createReportNameTextField() {
        return JMeterGuiUtil.createTextField(ReportCollector.REPORT_NAME);
    }

    private JLabel createReportNameLabel() {
        return JMeterGuiUtil.createLabel("报告名称：", reportNameTextField);
    }

    private JComboBox<String> createIsAppendComboBox() {
        JComboBox<String> comboBox = JMeterGuiUtil.createComboBox(ReportCollector.IS_APPEND);
        comboBox.addItem("false");
        comboBox.addItem("true");
        return comboBox;
    }

    private JLabel createIsAppendLabel() {
        return JMeterGuiUtil.createLabel("追加写报告：", isAppendComboBox);
    }

    private Component createBodyPanel() {
        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBorder(JMeterGuiUtil.createTitledBorder("配置HTML"));

        bodyPanel.add(reportNameLabel, JMeterGuiUtil.GridBag.labelConstraints);
        bodyPanel.add(reportNameTextField, JMeterGuiUtil.GridBag.editorConstraints);

        bodyPanel.add(isAppendLabel, JMeterGuiUtil.GridBag.labelConstraints);
        bodyPanel.add(isAppendComboBox, JMeterGuiUtil.GridBag.editorConstraints);

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(bodyPanel);
        mainPanel.add(createButtonPanel());
        return mainPanel;
    }

    private JPanel createButtonPanel() {
        HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.setBorder(JMeterGuiUtil.createTitledBorder("操作"));
        buttonPanel.add(createOpenHtmlButton());
        buttonPanel.add(createOpenDirectoryButton());
        return buttonPanel;
    }

    private Component createOpenHtmlButton() {
        JButton button = new JButton("打开报告");
        button.setActionCommand(OPEN_HTML_ACTION);
        button.addActionListener(this);
        return button;
    }

    private Component createOpenDirectoryButton() {
        JButton button = new JButton("打开HTML目录");
        button.setActionCommand(OPEN_DIRECTORY_ACTION);
        button.addActionListener(this);
        return button;
    }

    private Component createNoteArea() {
        return JMeterGuiUtil.createNoteArea(NOTE, this.getBackground());
    }
}
