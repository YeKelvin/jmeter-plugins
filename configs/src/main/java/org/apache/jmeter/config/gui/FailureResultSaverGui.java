package org.apache.jmeter.config.gui;

import org.apache.jmeter.config.FailureResultSaver;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;
import pers.kelvin.util.GuiUtil;

import javax.swing.*;
import java.awt.*;


/**
 * @author KelvinYe
 */
public class FailureResultSaverGui extends AbstractConfigGui {

    private JTextField logPathTextField;

    private JComboBox<String> formatTypeComboBox;

    public FailureResultSaverGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);

        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBorder(GuiUtil.createTitledBorder("配置错误日志信息"));
        bodyPanel.add(getLogPathLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getLogPathTextField(), GuiUtil.GridBag.editorConstraints);
        bodyPanel.add(getFormatTypeLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getFormatTypeComboBox(), GuiUtil.GridBag.editorConstraints);

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(bodyPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    @Override
    public String getStaticLabel() {
        return "Failure Result Saver";
    }


    @Override
    public String getLabelResource() {
        return null;
    }


    @Override
    public TestElement createTestElement() {
        FailureResultSaver el = new FailureResultSaver();
        modifyTestElement(el);
        return el;
    }

    /**
     * 将数据从GUI元素移动到TestElement
     */
    @Override
    public void modifyTestElement(TestElement el) {
        super.configureTestElement(el);
        el.setProperty(FailureResultSaver.LOG_PATH, logPathTextField.getText());
        el.setProperty(FailureResultSaver.FORMAT_TYPE, (String) formatTypeComboBox.getSelectedItem());
    }

    /**
     * 将数据设置到GUI元素中
     */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        logPathTextField.setText(el.getPropertyAsString(FailureResultSaver.LOG_PATH));
        formatTypeComboBox.setSelectedItem(el.getPropertyAsString(FailureResultSaver.FORMAT_TYPE));
    }

    @Override
    public void clearGui() {
        super.clearGui();
        logPathTextField.setText("");
        formatTypeComboBox.setSelectedItem("");
    }

    private Component getLogPathTextField() {
        if (logPathTextField == null) {
            logPathTextField = GuiUtil.createTextField(FailureResultSaver.LOG_PATH);
        }
        return logPathTextField;
    }

    private Component getLogPathLabel() {
        return GuiUtil.createLabel("日志路径：", getLogPathTextField());
    }

    private Component getFormatTypeComboBox() {
        if (formatTypeComboBox == null) {
            formatTypeComboBox = GuiUtil.createComboBox(FailureResultSaver.LOG_PATH);
            formatTypeComboBox.addItem("HTTP");
            formatTypeComboBox.addItem("Dubbo");
        }
        return formatTypeComboBox;
    }

    private Component getFormatTypeLabel() {
        return GuiUtil.createLabel("日志路径：", getFormatTypeComboBox());
    }

}
