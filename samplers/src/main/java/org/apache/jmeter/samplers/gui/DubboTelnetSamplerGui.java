package org.apache.jmeter.samplers.gui;

import org.apache.jmeter.common.jmeter.JMeterGuiUtil;
import org.apache.jmeter.common.json.JsonUtil;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.gui.util.JTextScrollPane;
import org.apache.jmeter.samplers.DubboTelnetSampler;
import org.apache.jmeter.testelement.TestElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author  Kelvin.Ye
 * @date    2019-02-22 11:47
 */
public class DubboTelnetSamplerGui extends AbstractSamplerGui implements ActionListener {

    private static final Logger log = LoggerFactory.getLogger(DubboTelnetSamplerGui.class);

    private static final String JSON_ACTION = "json";
    private static final String TEXT_ACTION = "text";

    private JTextField addressTextField;
    private JTextField interfaceNameTextField;
    private JTextField expectationTextField;
    private JTextField encodeTextField;
    private JComboBox<String> throughSSHComboBox;
    private JSyntaxTextArea paramsTextArea;

    private String currentParamsContentType = TEXT_ACTION;

    public DubboTelnetSamplerGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout(0, 10));
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
    }

    private Component createMainPanel() {
        JPanel interfacePanel = new JPanel(new GridBagLayout());
        interfacePanel.setBorder(JMeterGuiUtil.createTitledBorder("配置接口信息"));
        interfacePanel.add(createAddressLabel(), JMeterGuiUtil.GridBag.labelConstraints);
        interfacePanel.add(createAddressTextField(), JMeterGuiUtil.GridBag.editorConstraints);
        interfacePanel.add(createInterfaceNameLabel(), JMeterGuiUtil.GridBag.labelConstraints);
        interfacePanel.add(createInterfaceNameTextField(), JMeterGuiUtil.GridBag.editorConstraints);
        interfacePanel.add(createExpectationLabel(), JMeterGuiUtil.GridBag.labelConstraints);
        interfacePanel.add(createExpectationTextField(), JMeterGuiUtil.GridBag.editorConstraints);
        interfacePanel.add(createEncodeLabel(), JMeterGuiUtil.GridBag.labelConstraints);
        interfacePanel.add(createEncodeTextField(), JMeterGuiUtil.GridBag.editorConstraints);
        interfacePanel.add(createParamsLabel(), JMeterGuiUtil.GridBag.labelConstraints);
        interfacePanel.add(createButtonPanel(), JMeterGuiUtil.GridBag.editorConstraints);
        interfacePanel.add(createParamsPanel(), JMeterGuiUtil.GridBag.fillBottomConstraints);

        JPanel sshPanel = new JPanel(new GridBagLayout());
        sshPanel.setBorder(JMeterGuiUtil.createTitledBorder("配置SSH"));
        sshPanel.add(createThroughSSHLabel(), JMeterGuiUtil.GridBag.labelConstraints);
        sshPanel.add(createThroughSSHComboBox(), JMeterGuiUtil.GridBag.editorConstraints);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("接口配置", interfacePanel);
        tabbedPane.add("SSH配置", sshPanel);

        return tabbedPane;
    }

    @Override
    public String getStaticLabel() {
        return "DubboTelnet 取样器";
    }

    @Override
    public String getLabelResource() {
        return getStaticLabel();
    }

    @Override
    public TestElement createTestElement() {
        DubboTelnetSampler dubboTelnet = new DubboTelnetSampler();
        modifyTestElement(dubboTelnet);
        return dubboTelnet;
    }

    /**
     * GUI -> TestElement
     */
    @Override
    public void modifyTestElement(TestElement element) {
        super.configureTestElement(element);
        element.setProperty(DubboTelnetSampler.ADDRESS, addressTextField.getText());
        element.setProperty(DubboTelnetSampler.INTERFACE_NAME, interfaceNameTextField.getText());
        element.setProperty(DubboTelnetSampler.EXPECTATION, expectationTextField.getText());
        element.setProperty(DubboTelnetSampler.ENCODE, encodeTextField.getText());
        element.setProperty(DubboTelnetSampler.THROUGH_SSH, (String) throughSSHComboBox.getSelectedItem());
        element.setProperty(DubboTelnetSampler.PARAMS, getParamsText());
    }

    /**
     * TestElement -> GUI
     */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        addressTextField.setText(el.getPropertyAsString(DubboTelnetSampler.ADDRESS));
        interfaceNameTextField.setText(el.getPropertyAsString(DubboTelnetSampler.INTERFACE_NAME));
        expectationTextField.setText(el.getPropertyAsString(DubboTelnetSampler.EXPECTATION));
        encodeTextField.setText(el.getPropertyAsString(DubboTelnetSampler.ENCODE));
        throughSSHComboBox.setSelectedItem(el.getPropertyAsString(DubboTelnetSampler.THROUGH_SSH));
        paramsTextArea.setInitialText(prettyParams(el.getPropertyAsString(DubboTelnetSampler.PARAMS)));
        paramsTextArea.setCaretPosition(0);
    }

    @Override
    public void clearGui() {
        super.clearGui();
        addressTextField.setText("");
        interfaceNameTextField.setText("");
        expectationTextField.setText("");
        encodeTextField.setText("");
        throughSSHComboBox.setSelectedItem("");
        paramsTextArea.setInitialText("");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals(JSON_ACTION)) {
            currentParamsContentType = JSON_ACTION;
            paramsTextArea.setInitialText(prettyParams(paramsTextArea.getText()));
            paramsTextArea.setCaretPosition(0);
        } else if (action.equals(TEXT_ACTION)) {
            currentParamsContentType = TEXT_ACTION;
            paramsTextArea.setInitialText(getParamsText());
            paramsTextArea.setCaretPosition(0);
        }
    }

    private Component createAddressTextField() {
        if (addressTextField == null) {
            addressTextField = JMeterGuiUtil.createTextField(DubboTelnetSampler.ADDRESS);
        }
        return addressTextField;
    }

    private Component createAddressLabel() {
        return JMeterGuiUtil.createLabel("服务器地址：", createAddressTextField());
    }

    private Component createInterfaceNameTextField() {
        if (interfaceNameTextField == null) {
            interfaceNameTextField = JMeterGuiUtil.createTextField(DubboTelnetSampler.INTERFACE_NAME);
        }
        return interfaceNameTextField;
    }

    private Component createInterfaceNameLabel() {
        return JMeterGuiUtil.createLabel("接口名称：", createInterfaceNameTextField());
    }

    private Component createExpectationTextField() {
        if (expectationTextField == null) {
            expectationTextField = JMeterGuiUtil.createTextField(DubboTelnetSampler.EXPECTATION);
        }
        return expectationTextField;
    }

    private Component createExpectationLabel() {
        return JMeterGuiUtil.createLabel("预期结果：", createExpectationTextField());
    }

    private Component createEncodeTextField() {
        if (encodeTextField == null) {
            encodeTextField = JMeterGuiUtil.createTextField(DubboTelnetSampler.EXPECTATION);
        }
        return encodeTextField;
    }

    private Component createEncodeLabel() {
        return JMeterGuiUtil.createLabel("字符编码：", createEncodeTextField());
    }

    private Component createThroughSSHComboBox() {
        if (throughSSHComboBox == null) {
            throughSSHComboBox = JMeterGuiUtil.createComboBox(DubboTelnetSampler.THROUGH_SSH);
            throughSSHComboBox.addItem("false");
            throughSSHComboBox.addItem("true");
        }
        return throughSSHComboBox;
    }

    private Component createThroughSSHLabel() {
        return JMeterGuiUtil.createLabel("SSH：", createThroughSSHComboBox());
    }

    private Component createParamsTextArea() {
        if (paramsTextArea == null) {
            paramsTextArea = JMeterGuiUtil.createTextArea(DubboTelnetSampler.PARAMS, 20);
        }
        return paramsTextArea;
    }

    private Component createParamsLabel() {
        return JMeterGuiUtil.createLabel("请求报文：", createParamsTextArea());
    }

    private Component createParamsPanel() {
        return JTextScrollPane.getInstance((JSyntaxTextArea) createParamsTextArea());
    }

    private Component createButtonPanel() {
        JButton jsonButton = new JButton(JSON_ACTION);
        jsonButton.setActionCommand(JSON_ACTION);
        jsonButton.addActionListener(this);
        JButton textButton = new JButton(TEXT_ACTION);
        textButton.setActionCommand(TEXT_ACTION);
        textButton.addActionListener(this);

        JPanel panel = new HorizontalPanel();
        panel.add(jsonButton);
        panel.add(textButton);
        return panel;
    }

    /**
     * 格式化json
     *
     * @param params json字符串
     * @return String
     */
    private String prettyParams(String params) {
        if (JSON_ACTION.equals(currentParamsContentType)) {
            return JsonUtil.prettyJsonIgnorePlaceholder(JsonUtil.removeSpacesAndLineBreaks(params));
        }
        return params;
    }

    private String getParamsText() {
        return JsonUtil.removeSpacesAndLineBreaks(paramsTextArea.getText());
    }

}
