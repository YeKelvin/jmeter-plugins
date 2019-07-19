package org.apache.jmeter.samplers.gui;

import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.gui.util.JTextScrollPane;
import org.apache.jmeter.samplers.DubboTelnetSampler;
import org.apache.jmeter.testelement.TestElement;
import org.slf4j.Logger;
import pers.kelvin.util.GuiUtil;
import pers.kelvin.util.StringUtil;
import pers.kelvin.util.exception.ServiceException;
import pers.kelvin.util.json.JsonFileUtil;
import pers.kelvin.util.log.LogUtil;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Description
 *
 * @author KelvinYe
 * Date     2019-02-22
 * Time     11:47
 */
public class DubboTelnetSamplerGui extends AbstractSamplerGui {

    private static final Logger logger = LogUtil.getLogger(DubboTelnetSamplerGui.class);

    private JTextField addressTextField;
    private JTextField interfaceNameTextField;
    private JTextField expectationTextField;
    private JTextField encodeTextField;
    private JSyntaxTextArea paramsTextArea;

    private JComboBox<String> useTemplateComboBox;
    private JTextField interfacePathTextField;
    private JSyntaxTextArea jsonPathsTextArea;
    private JSyntaxTextArea templateContentTextArea;

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
        interfacePanel.add(getParamsLabel(), GuiUtil.GridBag.labelConstraints);
        interfacePanel.add(GuiUtil.createBlankPanel(), GuiUtil.GridBag.editorConstraints);
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
        return "Dubbo Telnet Sampler";
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
        element.setProperty(DubboTelnetSampler.PARAMS, paramsTextArea.getText());
        element.setProperty(DubboTelnetSampler.JSON_PATHS, jsonPathsTextArea.getText());
        element.setProperty(DubboTelnetSampler.EXPECTATION, expectationTextField.getText());
        element.setProperty(DubboTelnetSampler.ENCODE, encodeTextField.getText());
        element.setProperty(DubboTelnetSampler.USE_TEMPLATE, (String) useTemplateComboBox.getSelectedItem());
        element.setProperty(DubboTelnetSampler.INTERFACE_PATH, interfacePathTextField.getText());
    }

    @Override
    public void configure(TestElement el) {
        super.configure(el);
        addressTextField.setText(el.getPropertyAsString(DubboTelnetSampler.ADDRESS));
        interfaceNameTextField.setText(el.getPropertyAsString(DubboTelnetSampler.INTERFACE_NAME));
        paramsTextArea.setInitialText(el.getPropertyAsString(DubboTelnetSampler.PARAMS));
        paramsTextArea.setCaretPosition(0);
        jsonPathsTextArea.setInitialText(el.getPropertyAsString(DubboTelnetSampler.JSON_PATHS));
        jsonPathsTextArea.setCaretPosition(0);
        expectationTextField.setText(el.getPropertyAsString(DubboTelnetSampler.EXPECTATION));
        encodeTextField.setText(el.getPropertyAsString(DubboTelnetSampler.ENCODE));
        useTemplateComboBox.setSelectedItem(el.getPropertyAsString(DubboTelnetSampler.USE_TEMPLATE));
        interfacePathTextField.setText(el.getPropertyAsString(DubboTelnetSampler.INTERFACE_PATH));
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
        paramsTextArea.setInitialText("");
        jsonPathsTextArea.setInitialText("");
        expectationTextField.setText("");
        useTemplateComboBox.setSelectedItem("");
        interfacePathTextField.setText("");
        templateContentTextArea.setInitialText("");
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

    private Component getEncodeLabel() {
        return GuiUtil.createLabel("字符编码：", getEncodeTextField());
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
        if (StringUtil.isNotBlank(interfacePathTextField.getText())) {
            return JsonFileUtil.readJsonFile(DubboTelnetSampler.CONFIG_FILE_PATH, interfacePathTextField.getText(), interfaceName);
        } else {
            return JsonFileUtil.readJsonFile(DubboTelnetSampler.CONFIG_FILE_PATH, interfaceName);
        }
    }

}
