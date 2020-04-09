package org.apache.jmeter.samplers.gui;

import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.gui.util.JTextScrollPane;
import org.apache.jmeter.samplers.DubboTelnetSampler;
import org.apache.jmeter.testelement.TestElement;
import org.slf4j.Logger;
import org.apache.jmeter.common.utils.GuiUtil;
import org.apache.jmeter.common.utils.StringUtil;
import org.apache.jmeter.common.utils.exception.ServiceException;
import org.apache.jmeter.common.utils.json.JsonFileUtil;
import org.apache.jmeter.common.utils.json.JsonUtil;
import org.apache.jmeter.common.utils.LogUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Description
 *
 * @author KelvinYe
 * Date     2019-02-22
 * Time     11:47
 */
public class DubboTelnetSamplerGui extends AbstractSamplerGui implements ActionListener {

    private static final Logger logger = LogUtil.getLogger(DubboTelnetSamplerGui.class);

    private static final String JSON_ACTION = "json";
    private static final String TEXT_ACTION = "text";

    private JTextField addressTextField;
    private JTextField interfaceNameTextField;
    private JTextField expectationTextField;
    private JTextField encodeTextField;
    private JComboBox<String> throughSSHComboBox;
    private JSyntaxTextArea paramsTextArea;

    private JComboBox<String> useTemplateComboBox;
    private JTextField interfacePathTextField;
    private JSyntaxTextArea jsonPathsTextArea;
    private JSyntaxTextArea templateContentTextArea;

    private String currentParamsContentType = TEXT_ACTION;

    public DubboTelnetSamplerGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout(0, 10));
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);

        JPanel interfacePanel = new JPanel(new GridBagLayout());
        interfacePanel.setBorder(GuiUtil.createTitledBorder("配置接口信息"));
        interfacePanel.add(getAddressLabel(), GuiUtil.GridBag.labelConstraints);
        interfacePanel.add(getAddressTextField(), GuiUtil.GridBag.editorConstraints);
        interfacePanel.add(getInterfaceNameLabel(), GuiUtil.GridBag.labelConstraints);
        interfacePanel.add(getInterfaceNameTextField(), GuiUtil.GridBag.editorConstraints);
        interfacePanel.add(getExpectationLabel(), GuiUtil.GridBag.labelConstraints);
        interfacePanel.add(getExpectationTextField(), GuiUtil.GridBag.editorConstraints);
        interfacePanel.add(getEncodeLabel(), GuiUtil.GridBag.labelConstraints);
        interfacePanel.add(getEncodeTextField(), GuiUtil.GridBag.editorConstraints);
        interfacePanel.add(getThroughSSHLabel(), GuiUtil.GridBag.labelConstraints);
        interfacePanel.add(getThroughSSHComboBox(), GuiUtil.GridBag.editorConstraints);
        interfacePanel.add(getParamsLabel(), GuiUtil.GridBag.labelConstraints);
        interfacePanel.add(getButtonPanel(), GuiUtil.GridBag.editorConstraints);
        interfacePanel.add(getParamsPanel(), GuiUtil.GridBag.fillBottomConstraints);

        JPanel templatePanel = new JPanel(new GridBagLayout());
        templatePanel.setBorder(GuiUtil.createTitledBorder("配置模板信息"));
        templatePanel.add(getUseTemplateLabel(), GuiUtil.GridBag.labelConstraints);
        templatePanel.add(getUseTemplateComboBox(), GuiUtil.GridBag.editorConstraints);
        templatePanel.add(getInterfacePathLabel(), GuiUtil.GridBag.labelConstraints);
        templatePanel.add(getInterfacePathTextField(), GuiUtil.GridBag.editorConstraints);
        templatePanel.add(getJsonPathLabel(), GuiUtil.GridBag.labelConstraints);
        templatePanel.add(GuiUtil.createBlankPanel(), GuiUtil.GridBag.editorConstraints);
        templatePanel.add(getJsonPathPanel(), GuiUtil.GridBag.multiLineEditorConstraints);
        templatePanel.add(getTemplateContentLabel(), GuiUtil.GridBag.labelConstraints);
        templatePanel.add(GuiUtil.createBlankPanel(), GuiUtil.GridBag.editorConstraints);
        templatePanel.add(getTemplateContentPanel(), GuiUtil.GridBag.fillBottomConstraints);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("接口配置", interfacePanel);
        tabbedPane.add("模板配置", templatePanel);

        add(tabbedPane, BorderLayout.CENTER);
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

    @Override
    public void modifyTestElement(TestElement element) {
        super.configureTestElement(element);
        element.setProperty(DubboTelnetSampler.ADDRESS, addressTextField.getText());
        element.setProperty(DubboTelnetSampler.INTERFACE_NAME, interfaceNameTextField.getText());
        element.setProperty(DubboTelnetSampler.EXPECTATION, expectationTextField.getText());
        element.setProperty(DubboTelnetSampler.ENCODE, encodeTextField.getText());
        element.setProperty(DubboTelnetSampler.THROUGH_SSH, (String) throughSSHComboBox.getSelectedItem());
        element.setProperty(DubboTelnetSampler.PARAMS, getParamsText());
        element.setProperty(DubboTelnetSampler.USE_TEMPLATE, (String) useTemplateComboBox.getSelectedItem());
        element.setProperty(DubboTelnetSampler.INTERFACE_PATH, interfacePathTextField.getText());
        element.setProperty(DubboTelnetSampler.JSON_PATHS, jsonPathsTextArea.getText());
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
        useTemplateComboBox.setSelectedItem(el.getPropertyAsString(DubboTelnetSampler.USE_TEMPLATE));
        interfacePathTextField.setText(el.getPropertyAsString(DubboTelnetSampler.INTERFACE_PATH));
        jsonPathsTextArea.setInitialText(el.getPropertyAsString(DubboTelnetSampler.JSON_PATHS));
        jsonPathsTextArea.setCaretPosition(0);
        templateContentTextArea.setInitialText(getTemplateContent(
                el.getPropertyAsBoolean(DubboTelnetSampler.USE_TEMPLATE, false),
                el.getPropertyAsString(DubboTelnetSampler.INTERFACE_NAME)));
        templateContentTextArea.setCaretPosition(0);
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
        useTemplateComboBox.setSelectedItem("");
        interfacePathTextField.setText("");
        jsonPathsTextArea.setInitialText("");
        templateContentTextArea.setInitialText("");
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

    private Component getAddressTextField() {
        if (addressTextField == null) {
            addressTextField = GuiUtil.createTextField(DubboTelnetSampler.ADDRESS);
        }
        return addressTextField;
    }

    private Component getAddressLabel() {
        return GuiUtil.createLabel("服务器地址：", getAddressTextField());
    }

    private Component getInterfaceNameTextField() {
        if (interfaceNameTextField == null) {
            interfaceNameTextField = GuiUtil.createTextField(DubboTelnetSampler.INTERFACE_NAME);
        }
        return interfaceNameTextField;
    }

    private Component getInterfaceNameLabel() {
        return GuiUtil.createLabel("接口名称：", getInterfaceNameTextField());
    }

    private Component getExpectationTextField() {
        if (expectationTextField == null) {
            expectationTextField = GuiUtil.createTextField(DubboTelnetSampler.EXPECTATION);
        }
        return expectationTextField;
    }

    private Component getExpectationLabel() {
        return GuiUtil.createLabel("预期结果：", getExpectationTextField());
    }

    private Component getEncodeTextField() {
        if (encodeTextField == null) {
            encodeTextField = GuiUtil.createTextField(DubboTelnetSampler.EXPECTATION);
        }
        return encodeTextField;
    }

    private Component getEncodeLabel() {
        return GuiUtil.createLabel("字符编码：", getEncodeTextField());
    }

    private Component getThroughSSHComboBox() {
        if (throughSSHComboBox == null) {
            throughSSHComboBox = GuiUtil.createComboBox(DubboTelnetSampler.THROUGH_SSH);
            throughSSHComboBox.addItem("false");
            throughSSHComboBox.addItem("true");
        }
        return throughSSHComboBox;
    }

    private Component getThroughSSHLabel() {
        return GuiUtil.createLabel("是否使用ssh：", getThroughSSHComboBox());
    }

    private Component getParamsTextArea() {
        if (paramsTextArea == null) {
            paramsTextArea = GuiUtil.createTextArea(DubboTelnetSampler.PARAMS, 20);
        }
        return paramsTextArea;
    }

    private Component getParamsLabel() {
        return GuiUtil.createLabel("请求报文：", getParamsTextArea());
    }

    private Component getParamsPanel() {
        return JTextScrollPane.getInstance((JSyntaxTextArea) getParamsTextArea());
    }

    private Component getUseTemplateComboBox() {
        if (useTemplateComboBox == null) {
            useTemplateComboBox = GuiUtil.createComboBox(DubboTelnetSampler.USE_TEMPLATE);
            useTemplateComboBox.addItem("false");
            useTemplateComboBox.addItem("true");
        }
        return useTemplateComboBox;
    }

    private Component getUseTemplateLabel() {
        return GuiUtil.createLabel("是否使用模板：", getUseTemplateComboBox());
    }

    private Component getInterfacePathTextField() {
        if (interfacePathTextField == null) {
            interfacePathTextField = GuiUtil.createTextField(DubboTelnetSampler.INTERFACE_PATH);
        }
        return interfacePathTextField;
    }

    private Component getInterfacePathLabel() {
        return GuiUtil.createLabel("模板目录：", getInterfacePathTextField());
    }

    private Component getJsonPathTextArea() {
        if (jsonPathsTextArea == null) {
            jsonPathsTextArea = GuiUtil.createTextArea(DubboTelnetSampler.JSON_PATHS, 6);
        }
        return jsonPathsTextArea;
    }

    private Component getJsonPathLabel() {
        return GuiUtil.createLabel("JsonPaths：", getJsonPathTextArea());
    }

    private Component getJsonPathPanel() {
        return JTextScrollPane.getInstance((JSyntaxTextArea) getJsonPathTextArea());
    }

    private Component getTemplateContentTextArea() {
        if (templateContentTextArea == null) {
            templateContentTextArea = GuiUtil.createTextArea(DubboTelnetSampler.TEMPLATE_CONTENT, 20);
        }
        return templateContentTextArea;
    }

    private Component getTemplateContentLabel() {
        return GuiUtil.createLabel("模板内容：", getTemplateContentTextArea());
    }

    private Component getTemplateContentPanel() {
        return JTextScrollPane.getInstance((JSyntaxTextArea) getTemplateContentTextArea());
    }

    private Component getButtonPanel() {
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
     * 获取json模版内容
     */
    private String getTemplateContent(boolean useTemplate, String interfaceName) {
        if (useTemplate && StringUtil.isNotBlank(interfaceName)) {
            try {
                return readJsonFile(interfaceName);
            } catch (IOException | ServiceException e) {
                return e.getMessage();
            }
        }
        return "";
    }

    /**
     * 读取 Json模板文件
     *
     * @param interfaceName 文件名
     */
    private String readJsonFile(String interfaceName) throws IOException, ServiceException {
        String interfaceDir = interfacePathTextField.getText();

        if (StringUtil.isBlank(interfaceDir)) {
            throw new ServiceException("接口路径不允许为空");
        }
        // 根据入參 interfacePath递归搜索获取绝对路径
        String path = JsonFileUtil.findInterfacePathByKeywords(interfaceDir, interfaceName);
        if (path == null) {
            throw new ServiceException(String.format("\"%s\" 接口模版不存在", interfaceName));
        }
        // 根据绝对路径获取json模版内容
        return JsonFileUtil.readJsonFileToString(path);
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
