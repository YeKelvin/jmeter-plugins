package org.apache.jmeter.samplers.gui;

import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.gui.util.JTextScrollPane;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.DubboTelnetByFile;
import pers.kelvin.util.json.JsonFileUtil;
import org.apache.jmeter.testelement.TestElement;
import org.slf4j.Logger;
import pers.kelvin.util.GuiUtil;
import pers.kelvin.util.StringUtil;
import pers.kelvin.util.exception.ServiceException;
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
    private JTextField telnetEncodeTextField;
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

        VerticalPanel interfacePanel = new VerticalPanel();
        interfacePanel.setBorder(GuiUtil.createTitledBorder("Configure the Interface Data Source"));
        interfacePanel.add(getAddressPanel());
        interfacePanel.add(getInterfaceNamePanel());
        interfacePanel.add(getParamsPanel());
        interfacePanel.add(getJsonPathPanel());
        interfacePanel.add(getExpectionPanel());
        interfacePanel.add(getTelnetEncodePanel());

        VerticalPanel templatePanel = new VerticalPanel();
        templatePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Configure the Template"));
        templatePanel.add(getUseTemplatePanel());
        templatePanel.add(getInterfaceSystemPanel());
        templatePanel.add(getTemplateContentPanel());

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(interfacePanel);
        mainPanel.add(templatePanel);

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
        element.setProperty(DubboTelnetByFile.EXPECTION, expectionTextField.getText());
        element.setProperty(DubboTelnetByFile.TELNET_ENCODE, telnetEncodeTextField.getText());
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
        expectionTextField.setText(el.getPropertyAsString(DubboTelnetByFile.EXPECTION));
        telnetEncodeTextField.setText(el.getPropertyAsString(DubboTelnetByFile.TELNET_ENCODE));
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
        paramsTextArea = JSyntaxTextArea.getInstance(5, 20);
        paramsTextArea.setName(DubboTelnetByFile.PARAMS);

        JLabel label = GuiUtil.createTextAreaLabel("Params:", paramsTextArea, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(JTextScrollPane.getInstance(paramsTextArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel getJsonPathPanel() {
        jsonPathsTextArea = JSyntaxTextArea.getInstance(2, 20);
        jsonPathsTextArea.setName(DubboTelnetByFile.JSON_PATHS);

        JLabel label = GuiUtil.createTextAreaLabel("JsonPaths:", jsonPathsTextArea, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(JTextScrollPane.getInstance(jsonPathsTextArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel getExpectionPanel() {
        expectionTextField = new JTextField(10);
        expectionTextField.setName(DubboTelnetByFile.EXPECTION);

        JLabel label = GuiUtil.createTextFieldLabel("Expection:", expectionTextField, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(expectionTextField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getTelnetEncodePanel() {
        telnetEncodeTextField = new JTextField(10);
        telnetEncodeTextField.setName(DubboTelnetByFile.EXPECTION);

        JLabel label = GuiUtil.createTextFieldLabel("TelnetEncode:", telnetEncodeTextField, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(telnetEncodeTextField, BorderLayout.CENTER);
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
        templateContentTextArea = JSyntaxTextArea.getInstance(8, 20);
        templateContentTextArea.setName(DubboTelnetByFile.TEMPLATE_CONTENT);

        JLabel label = GuiUtil.createTextAreaLabel("TemplateContent:", templateContentTextArea, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(JTextScrollPane.getInstance(templateContentTextArea), BorderLayout.CENTER);
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

    private String readJsonFile(String interfaceName) throws IOException, ServiceException {
        if (StringUtil.isNotBlank(interfaceSystemTextField.getText())) {
            return JsonFileUtil.readJsonFile(DubboTelnetByFile.CONFIG_FILE_PATH, interfaceSystemTextField.getText(), interfaceName);
        } else {
            return JsonFileUtil.readJsonFile(DubboTelnetByFile.CONFIG_FILE_PATH, interfaceName);
        }
    }

}
