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

    private static final int H_GAP = 5;
    private static final int V_GAP = 10;
    private static final int LABEL_WIDTH = 100;
    private static final int LABEL_HEIGHT = 10;

    private JTextField addressTextField;
    private JTextField interfaceNameTextField;
    private JSyntaxTextArea paramsTextArea;
    private JSyntaxTextArea jsonPathsTextArea;
    private JTextField expectionTextField;
    private JTextField encodeTextField;
    private JComboBox<String> useTemplateComboBox;
    private JTextField interfaceSystemTextField;
    private JSyntaxTextArea templateContentTextArea;

    public DubboTelnetByFileGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout(0, 10));
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);

//        JPanel interfaceBodyPanel = new JPanel(new GridBagLayout());
//        interfaceBodyPanel.setBorder(GuiUtil.createTitledBorder("配置接口信息"));
//        interfaceBodyPanel.add(getAddressLabel(), GuiUtil.GridBag.labelConstraints);
//        interfaceBodyPanel.add(getAddressField(), GuiUtil.GridBag.editorConstraints);
//        interfaceBodyPanel.add(getInterfaceNameLabel(), GuiUtil.GridBag.labelConstraints);
//        interfaceBodyPanel.add(getInterfaceNameField(), GuiUtil.GridBag.editorConstraints);
//        interfaceBodyPanel.add(getJsonPathLabel(), GuiUtil.GridBag.multiLineLabelConstraints);
//        interfaceBodyPanel.add(getJsonPathTextArea(), GuiUtil.GridBag.multiLineEditorConstraints);
//        interfaceBodyPanel.add(getExpectationLabel(), GuiUtil.GridBag.labelConstraints);
//        interfaceBodyPanel.add(getExpectationField(), GuiUtil.GridBag.editorConstraints);
//        interfaceBodyPanel.add(getEncodeLabel(), GuiUtil.GridBag.labelConstraints);
//        interfaceBodyPanel.add(getEncodeField(), GuiUtil.GridBag.editorConstraints);
//        interfaceBodyPanel.add(getParamsLabel(), GuiUtil.GridBag.multiLineLabelConstraints);
//        interfaceBodyPanel.add(getParamsTextArea(), GuiUtil.GridBag.multiLineEditorConstraints);
//
//        JPanel templateBodyPanel = new JPanel(new GridBagLayout());
//        templateBodyPanel.setBorder(GuiUtil.createTitledBorder("配置模板信息"));
//        templateBodyPanel.add(getUseTemplateLabel(), GuiUtil.GridBag.labelConstraints);
//        templateBodyPanel.add(getUseTemplateComboBox(), GuiUtil.GridBag.editorConstraints);
//        templateBodyPanel.add(getInterfaceSystemLabel(), GuiUtil.GridBag.labelConstraints);
//        templateBodyPanel.add(getInterfaceSystemField(), GuiUtil.GridBag.editorConstraints);
//        templateBodyPanel.add(getTemplateContentLabel(), GuiUtil.GridBag.multiLineLabelConstraints);
//        templateBodyPanel.add(getTemplateContentTextArea(), GuiUtil.GridBag.multiLineEditorConstraints);

        VerticalPanel interfacePanel = new VerticalPanel();
        interfacePanel.setBorder(GuiUtil.createTitledBorder("配置接口信息"));
        interfacePanel.add(getAddressPanel());
        interfacePanel.add(getInterfaceNamePanel());
        interfacePanel.add(getParamsPanel());
        interfacePanel.add(getJsonPathPanel());
        interfacePanel.add(getExpectationPanel());
        interfacePanel.add(getEncodePanel());

        VerticalPanel templatePanel = new VerticalPanel();
        templatePanel.setBorder(GuiUtil.createTitledBorder("配置模板信息"));
        templatePanel.add(getUseTemplatePanel());
        templatePanel.add(getInterfaceSystemPanel());
        templatePanel.add(getTemplateContentPanel());

//        VerticalPanel interfacePanel = new VerticalPanel();
//        interfacePanel.add(interfaceBodyPanel);
//        VerticalPanel templatePanel = new VerticalPanel();
//        templatePanel.add(templateBodyPanel);


        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Interface", interfacePanel);
        tabbedPane.add("Template", templatePanel);

//        VerticalPanel mainPanel = new VerticalPanel();
//        mainPanel.add(makeTitlePanel());
//        mainPanel.add(tabbedPane);

        add(tabbedPane, BorderLayout.CENTER);
//        add(mainPanel, BorderLayout.CENTER);
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

    private JPanel getAddressPanel() {
        addressTextField = new JTextField(10);
        addressTextField.setName(DubboTelnetByFile.ADDRESS);

        JLabel label = GuiUtil.createTextFieldLabel("Address:", addressTextField, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(addressTextField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getInterfaceNamePanel() {
        interfaceNameTextField = new JTextField(10);
        interfaceNameTextField.setName(DubboTelnetByFile.INTERFACE_NAME);

        JLabel label = GuiUtil.createTextFieldLabel("InterfaceName:", interfaceNameTextField, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(interfaceNameTextField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getParamsPanel() {
        paramsTextArea = JSyntaxTextArea.getInstance(10, 10);
        paramsTextArea.setName(DubboTelnetByFile.PARAMS);

        JLabel label = GuiUtil.createTextAreaLabel("Params:", paramsTextArea, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(JTextScrollPane.getInstance(paramsTextArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel getJsonPathPanel() {
        jsonPathsTextArea = JSyntaxTextArea.getInstance(2, 10);
        jsonPathsTextArea.setName(DubboTelnetByFile.JSON_PATHS);

        JLabel label = GuiUtil.createTextAreaLabel("JsonPaths:", jsonPathsTextArea, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(JTextScrollPane.getInstance(jsonPathsTextArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel getExpectationPanel() {
        expectionTextField = new JTextField(10);
        expectionTextField.setName(DubboTelnetByFile.EXPECTATION);

        JLabel label = GuiUtil.createTextFieldLabel("Expectation:", expectionTextField, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(expectionTextField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getEncodePanel() {
        encodeTextField = new JTextField(10);
        encodeTextField.setName(DubboTelnetByFile.EXPECTATION);

        JLabel label = GuiUtil.createTextFieldLabel("Encode:", encodeTextField, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(encodeTextField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getUseTemplatePanel() {
        useTemplateComboBox = new JComboBox<>();
        useTemplateComboBox.setName(DubboTelnetByFile.USE_TEMPLATE);
        useTemplateComboBox.addItem("false");
        useTemplateComboBox.addItem("true");

        JLabel label = GuiUtil.createTextFieldLabel("UseTemplate:", useTemplateComboBox, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel jPanel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        jPanel.add(label, BorderLayout.WEST);
        jPanel.add(useTemplateComboBox, BorderLayout.CENTER);
        return jPanel;
    }

    private JPanel getInterfaceSystemPanel() {
        interfaceSystemTextField = new JTextField(10);
        interfaceSystemTextField.setName(DubboTelnetByFile.INTERFACE_SYSTEM);

        JLabel label = GuiUtil.createTextFieldLabel("InterfaceSystem:", interfaceSystemTextField, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(interfaceSystemTextField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getTemplateContentPanel() {
        templateContentTextArea = JSyntaxTextArea.getInstance(8, 10);
        templateContentTextArea.setName(DubboTelnetByFile.TEMPLATE_CONTENT);

        JLabel label = GuiUtil.createTextAreaLabel("TemplateContent:", templateContentTextArea, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(JTextScrollPane.getInstance(templateContentTextArea), BorderLayout.CENTER);
        return panel;
    }

    ////////////////


    private Component getAddressField() {
        if (addressTextField == null) {
            addressTextField = new JTextField(10);
            addressTextField.setName(DubboTelnetByFile.ADDRESS);
        }
        return addressTextField;
    }

    private Component getAddressLabel() {
        return GuiUtil.createTextFieldLabel("服务器地址：", getAddressField());
    }

    private Component getInterfaceNameField() {
        if (interfaceNameTextField == null) {
            interfaceNameTextField = new JTextField(10);
            interfaceNameTextField.setName(DubboTelnetByFile.INTERFACE_NAME);
        }
        return interfaceNameTextField;
    }

    private Component getInterfaceNameLabel() {
        return GuiUtil.createTextFieldLabel("接口名称：", getInterfaceNameField());
    }

    private Component getParamsTextArea() {
        if (paramsTextArea == null) {
            paramsTextArea = JSyntaxTextArea.getInstance(10, 10);
            paramsTextArea.setName(DubboTelnetByFile.PARAMS);
        }
        return paramsTextArea;
    }

    private Component getParamsLabel() {
        return GuiUtil.createTextAreaLabel("请求报文：", getParamsTextArea());
    }

    private Component getJsonPathTextArea() {
        if (jsonPathsTextArea == null) {
            jsonPathsTextArea = JSyntaxTextArea.getInstance(2, 10);
            jsonPathsTextArea.setName(DubboTelnetByFile.JSON_PATHS);
        }
        return jsonPathsTextArea;
    }

    private Component getJsonPathLabel() {
        return GuiUtil.createTextAreaLabel("JsonPaths:", getJsonPathTextArea());
    }

    private Component getExpectationField() {
        if (expectionTextField == null) {
            expectionTextField = new JTextField(10);
            expectionTextField.setName(DubboTelnetByFile.EXPECTATION);
        }
        return expectionTextField;
    }

    private Component getExpectationLabel() {
        return GuiUtil.createTextFieldLabel("预期结果：", getExpectationField());
    }

    private Component getEncodeField() {
        if (encodeTextField == null) {
            encodeTextField = new JTextField(10);
            encodeTextField.setName(DubboTelnetByFile.EXPECTATION);
        }
        return encodeTextField;
    }

    private Component getEncodeLabel() {
        return GuiUtil.createTextFieldLabel("字符编码：", encodeTextField, LABEL_WIDTH, LABEL_HEIGHT);
    }

    private Component getUseTemplateComboBox() {
        if (useTemplateComboBox == null) {
            useTemplateComboBox = new JComboBox<>();
            useTemplateComboBox.setName(DubboTelnetByFile.USE_TEMPLATE);
            useTemplateComboBox.addItem("false");
            useTemplateComboBox.addItem("true");
        }
        return useTemplateComboBox;
    }

    private Component getUseTemplateLabel() {
        return GuiUtil.createTextFieldLabel("是否使用模板：", getUseTemplateComboBox());
    }

    private Component getInterfaceSystemField() {
        if (interfaceSystemTextField == null) {
            interfaceSystemTextField = new JTextField(10);
            interfaceSystemTextField.setName(DubboTelnetByFile.INTERFACE_SYSTEM);
        }
        return interfaceSystemTextField;
    }

    private Component getInterfaceSystemLabel() {
        return GuiUtil.createTextFieldLabel("模板目录：", getInterfaceSystemField());
    }

    private Component getTemplateContentTextArea() {
        if (templateContentTextArea == null) {
            templateContentTextArea = JSyntaxTextArea.getInstance(8, 10);
            templateContentTextArea.setName(DubboTelnetByFile.TEMPLATE_CONTENT);
        }
        return templateContentTextArea;
    }

    private Component getTemplateContentLabel() {
        return GuiUtil.createTextAreaLabel("模板内容：", getTemplateContentTextArea());
    }


    ////////////////

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

    private String readJsonFile(String interfaceName) throws IOException, ServiceException {
        if (StringUtil.isNotBlank(interfaceSystemTextField.getText())) {
            return JsonFileUtil.readJsonFile(DubboTelnetByFile.CONFIG_FILE_PATH, interfaceSystemTextField.getText(), interfaceName);
        } else {
            return JsonFileUtil.readJsonFile(DubboTelnetByFile.CONFIG_FILE_PATH, interfaceName);
        }
    }

}
