package org.apache.jmeter.samplers.gui;

import org.apache.jmeter.common.utils.GuiUtil;
import org.apache.jmeter.common.utils.json.JsonUtil;
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
 * Description
 *
 * @author KelvinYe
 * Date     2019-02-22
 * Time     11:47
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
        interfacePanel.setBorder(GuiUtil.createTitledBorder("配置接口信息"));
        interfacePanel.add(createAddressLabel(), GuiUtil.GridBag.labelConstraints);
        interfacePanel.add(createAddressTextField(), GuiUtil.GridBag.editorConstraints);
        interfacePanel.add(createInterfaceNameLabel(), GuiUtil.GridBag.labelConstraints);
        interfacePanel.add(createInterfaceNameTextField(), GuiUtil.GridBag.editorConstraints);
        interfacePanel.add(createExpectationLabel(), GuiUtil.GridBag.labelConstraints);
        interfacePanel.add(createExpectationTextField(), GuiUtil.GridBag.editorConstraints);
        interfacePanel.add(createEncodeLabel(), GuiUtil.GridBag.labelConstraints);
        interfacePanel.add(createEncodeTextField(), GuiUtil.GridBag.editorConstraints);
        interfacePanel.add(createParamsLabel(), GuiUtil.GridBag.labelConstraints);
        interfacePanel.add(createButtonPanel(), GuiUtil.GridBag.editorConstraints);
        interfacePanel.add(createParamsPanel(), GuiUtil.GridBag.fillBottomConstraints);

        JPanel sshPanel = new JPanel(new GridBagLayout());
        sshPanel.setBorder(GuiUtil.createTitledBorder("配置SSH"));
        sshPanel.add(createThroughSSHLabel(), GuiUtil.GridBag.labelConstraints);
        sshPanel.add(createThroughSSHComboBox(), GuiUtil.GridBag.editorConstraints);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("接口配置", interfacePanel);
        tabbedPane.add("SSH配置", sshPanel);

        return tabbedPane;
    }

    @Override
    public String getStaticLabel() {
        return "DubboTelnet取样器";
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
            addressTextField = GuiUtil.createTextField(DubboTelnetSampler.ADDRESS);
        }
        return addressTextField;
    }

    private Component createAddressLabel() {
        return GuiUtil.createLabel("服务器地址：", createAddressTextField());
    }

    private Component createInterfaceNameTextField() {
        if (interfaceNameTextField == null) {
            interfaceNameTextField = GuiUtil.createTextField(DubboTelnetSampler.INTERFACE_NAME);
        }
        return interfaceNameTextField;
    }

    private Component createInterfaceNameLabel() {
        return GuiUtil.createLabel("接口名称：", createInterfaceNameTextField());
    }

    private Component createExpectationTextField() {
        if (expectationTextField == null) {
            expectationTextField = GuiUtil.createTextField(DubboTelnetSampler.EXPECTATION);
        }
        return expectationTextField;
    }

    private Component createExpectationLabel() {
        return GuiUtil.createLabel("预期结果：", createExpectationTextField());
    }

    private Component createEncodeTextField() {
        if (encodeTextField == null) {
            encodeTextField = GuiUtil.createTextField(DubboTelnetSampler.EXPECTATION);
        }
        return encodeTextField;
    }

    private Component createEncodeLabel() {
        return GuiUtil.createLabel("字符编码：", createEncodeTextField());
    }

    private Component createThroughSSHComboBox() {
        if (throughSSHComboBox == null) {
            throughSSHComboBox = GuiUtil.createComboBox(DubboTelnetSampler.THROUGH_SSH);
            throughSSHComboBox.addItem("false");
            throughSSHComboBox.addItem("true");
        }
        return throughSSHComboBox;
    }

    private Component createThroughSSHLabel() {
        return GuiUtil.createLabel("SSH：", createThroughSSHComboBox());
    }

    private Component createParamsTextArea() {
        if (paramsTextArea == null) {
            paramsTextArea = GuiUtil.createTextArea(DubboTelnetSampler.PARAMS, 20);
        }
        return paramsTextArea;
    }

    private Component createParamsLabel() {
        return GuiUtil.createLabel("请求报文：", createParamsTextArea());
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
            return JsonUtil.prettyJsonWithPlaceholder(JsonUtil.removeSpacesAndLineBreaks(params));
        }
        return params;
    }

    private String getParamsText() {
        return JsonUtil.removeSpacesAndLineBreaks(paramsTextArea.getText());
    }

}
