package org.apache.jmeter.samplers.gui;

import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.gui.util.JTextScrollPane;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.DubboTelnetByFile;
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
public class DubboTelnetByFileGui extends AbstractSamplerGui {

    private static final Logger logger = LogUtil.getLogger(DubboTelnetByFileGui.class);

    private JTextField addressTextField;
    private JTextField interfaceNameTextField;
    private JTextField expectionTextField;
    private JTextField encodeTextField;
    private JSyntaxTextArea paramsTextArea;

    private JComboBox<String> useTemplateComboBox;
    private JTextField interfaceSystemTextField;
    private JSyntaxTextArea jsonPathsTextArea;
    private JSyntaxTextArea templateContentTextArea;

    public DubboTelnetByFileGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout(0, 10));
        setBorder(makeBorder());

        JPanel interfaceBodyPanel = new JPanel(new GridBagLayout());
        interfaceBodyPanel.setBorder(GuiUtil.createTitledBorder("配置接口信息"));
        interfaceBodyPanel.add(getAddressLabel(), GuiUtil.GridBag.labelConstraints);
        interfaceBodyPanel.add(getAddressTextField(), GuiUtil.GridBag.editorConstraints);
        interfaceBodyPanel.add(getInterfaceNameLabel(), GuiUtil.GridBag.labelConstraints);
        interfaceBodyPanel.add(getInterfaceNameTextField(), GuiUtil.GridBag.editorConstraints);
        interfaceBodyPanel.add(getExpectationLabel(), GuiUtil.GridBag.labelConstraints);
        interfaceBodyPanel.add(getExpectationTextField(), GuiUtil.GridBag.editorConstraints);
        interfaceBodyPanel.add(getEncodeLabel(), GuiUtil.GridBag.labelConstraints);
        interfaceBodyPanel.add(getEncodeTextField(), GuiUtil.GridBag.editorConstraints);
        interfaceBodyPanel.add(getParamsLabel(), GuiUtil.GridBag.labelConstraints);
        interfaceBodyPanel.add(GuiUtil.createBlankPanel(), GuiUtil.GridBag.editorConstraints);
        interfaceBodyPanel.add(getParamsPanel(), GuiUtil.GridBag.fillBottomConstraints);

        JPanel templateBodyPanel = new JPanel(new GridBagLayout());
        templateBodyPanel.setBorder(GuiUtil.createTitledBorder("配置模板信息"));
        templateBodyPanel.add(getUseTemplateLabel(), GuiUtil.GridBag.labelConstraints);
        templateBodyPanel.add(getUseTemplateComboBox(), GuiUtil.GridBag.editorConstraints);
        templateBodyPanel.add(getInterfaceSystemLabel(), GuiUtil.GridBag.labelConstraints);
        templateBodyPanel.add(getInterfaceSystemTextField(), GuiUtil.GridBag.editorConstraints);
        templateBodyPanel.add(getJsonPathLabel(), GuiUtil.GridBag.labelConstraints);
        templateBodyPanel.add(GuiUtil.createBlankPanel(), GuiUtil.GridBag.editorConstraints);
        templateBodyPanel.add(getJsonPathPanel(), GuiUtil.GridBag.multiLineEditorConstraints);
        templateBodyPanel.add(getTemplateContentLabel(), GuiUtil.GridBag.labelConstraints);
        templateBodyPanel.add(GuiUtil.createBlankPanel(), GuiUtil.GridBag.editorConstraints);
        templateBodyPanel.add(getTemplateContentPanel(), GuiUtil.GridBag.multiLineEditorConstraints);

        VerticalPanel interfaceMainPanel = new VerticalPanel();
        interfaceMainPanel.add(interfaceBodyPanel);
        VerticalPanel templateMainPanel = new VerticalPanel();
        templateMainPanel.add(templateBodyPanel);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("接口配置", interfaceMainPanel);
        tabbedPane.add("模板配置", templateMainPanel);

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(makeTitlePanel());
        mainPanel.add(tabbedPane);

        add(mainPanel, BorderLayout.CENTER);
    }

    @Override
    public String getStaticLabel() {
        return "Dubbo Telnet By File";
    }

    @Override
    public String getLabelResource() {
        return getStaticLabel();
    }

    @Override
    public TestElement createTestElement() {
        DubboTelnetByFile dubboTelnet = new DubboTelnetByFile();
        modifyTestElement(dubboTelnet);
        return dubboTelnet;
    }

    @Override
    public void modifyTestElement(TestElement element) {
        super.configureTestElement(element);
        element.setProperty(DubboTelnetByFile.ADDRESS, addressTextField.getText());
        element.setProperty(DubboTelnetByFile.INTERFACE_NAME, interfaceNameTextField.getText());
        element.setProperty(DubboTelnetByFile.PARAMS, paramsTextArea.getText());
        element.setProperty(DubboTelnetByFile.JSON_PATHS, jsonPathsTextArea.getText());
        element.setProperty(DubboTelnetByFile.EXPECTATION, expectionTextField.getText());
        element.setProperty(DubboTelnetByFile.ENCODE, encodeTextField.getText());
        element.setProperty(DubboTelnetByFile.USE_TEMPLATE, (String) useTemplateComboBox.getSelectedItem());
        element.setProperty(DubboTelnetByFile.INTERFACE_SYSTEM, interfaceSystemTextField.getText());
    }

    @Override
    public void configure(TestElement el) {
        super.configure(el);
        addressTextField.setText(el.getPropertyAsString(DubboTelnetByFile.ADDRESS));
        interfaceNameTextField.setText(el.getPropertyAsString(DubboTelnetByFile.INTERFACE_NAME));
        paramsTextArea.setInitialText(el.getPropertyAsString(DubboTelnetByFile.PARAMS));
        paramsTextArea.setCaretPosition(0);
        jsonPathsTextArea.setInitialText(el.getPropertyAsString(DubboTelnetByFile.JSON_PATHS));
        jsonPathsTextArea.setCaretPosition(0);
        expectionTextField.setText(el.getPropertyAsString(DubboTelnetByFile.EXPECTATION));
        encodeTextField.setText(el.getPropertyAsString(DubboTelnetByFile.ENCODE));
        useTemplateComboBox.setSelectedItem(el.getPropertyAsString(DubboTelnetByFile.USE_TEMPLATE));
        interfaceSystemTextField.setText(el.getPropertyAsString(DubboTelnetByFile.INTERFACE_SYSTEM));
        templateContentTextArea.setInitialText(getTemplateContent(
                el.getPropertyAsBoolean(DubboTelnetByFile.USE_TEMPLATE, false),
                el.getPropertyAsString(DubboTelnetByFile.INTERFACE_NAME)));
        templateContentTextArea.setCaretPosition(0);
    }

    @Override
    public void clearGui() {
        super.clearGui();
        addressTextField.setText("");
        interfaceNameTextField.setText("");
        paramsTextArea.setInitialText("");
        jsonPathsTextArea.setInitialText("");
        expectionTextField.setText("");
        useTemplateComboBox.setSelectedItem("");
        interfaceSystemTextField.setText("");
        templateContentTextArea.setInitialText("");
    }

    private Component getAddressTextField() {
        if (addressTextField == null) {
            addressTextField = GuiUtil.createTextField(DubboTelnetByFile.ADDRESS);
        }
        return addressTextField;
    }

    private Component getAddressLabel() {
        return GuiUtil.createLabel("服务器地址：", getAddressTextField());
    }

    private Component getInterfaceNameTextField() {
        if (interfaceNameTextField == null) {
            interfaceNameTextField = GuiUtil.createTextField(DubboTelnetByFile.INTERFACE_NAME);
        }
        return interfaceNameTextField;
    }

    private Component getInterfaceNameLabel() {
        return GuiUtil.createLabel("接口名称：", getInterfaceNameTextField());
    }

    private Component getExpectationTextField() {
        if (expectionTextField == null) {
            expectionTextField = GuiUtil.createTextField(DubboTelnetByFile.EXPECTATION);
        }
        return expectionTextField;
    }

    private Component getExpectationLabel() {
        return GuiUtil.createLabel("预期结果：", getExpectationTextField());
    }

    private Component getEncodeTextField() {
        if (encodeTextField == null) {
            encodeTextField = GuiUtil.createTextField(DubboTelnetByFile.EXPECTATION);
        }
        return encodeTextField;
    }

    private Component getParamsTextArea() {
        if (paramsTextArea == null) {
            paramsTextArea = GuiUtil.createTextArea(DubboTelnetByFile.PARAMS, 20);
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
            useTemplateComboBox = GuiUtil.createComboBox(DubboTelnetByFile.USE_TEMPLATE);
            useTemplateComboBox.addItem("false");
            useTemplateComboBox.addItem("true");
        }
        return useTemplateComboBox;
    }

    private Component getUseTemplateLabel() {
        return GuiUtil.createLabel("是否使用模板：", getUseTemplateComboBox());
    }

    private Component getInterfaceSystemTextField() {
        if (interfaceSystemTextField == null) {
            interfaceSystemTextField = GuiUtil.createTextField(DubboTelnetByFile.INTERFACE_SYSTEM);
        }
        return interfaceSystemTextField;
    }

    private Component getInterfaceSystemLabel() {
        return GuiUtil.createLabel("模板目录：", getInterfaceSystemTextField());
    }

    private Component getJsonPathTextArea() {
        if (jsonPathsTextArea == null) {
            jsonPathsTextArea = GuiUtil.createTextArea(DubboTelnetByFile.JSON_PATHS, 6);
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
            templateContentTextArea = GuiUtil.createTextArea(DubboTelnetByFile.TEMPLATE_CONTENT, 20);
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
        if (StringUtil.isNotBlank(interfaceSystemTextField.getText())) {
            return JsonFileUtil.readJsonFile(DubboTelnetByFile.CONFIG_FILE_PATH, interfaceSystemTextField.getText(), interfaceName);
        } else {
            return JsonFileUtil.readJsonFile(DubboTelnetByFile.CONFIG_FILE_PATH, interfaceName);
        }
    }

}
