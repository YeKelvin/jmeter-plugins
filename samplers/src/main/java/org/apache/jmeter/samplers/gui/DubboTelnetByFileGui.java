package org.apache.jmeter.samplers.gui;

import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.gui.util.JTextScrollPane;
import org.apache.jmeter.samplers.DubboTelnetByFile;
import org.apache.jmeter.testelement.TestElement;
import org.slf4j.Logger;
import pers.kelvin.util.log.LogUtil;

import javax.swing.*;
import java.awt.*;

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
    private JSyntaxTextArea paramsTextArea;
    private JTextField expectionTextField;
    private JComboBox<String> useTemplateComboBox;
    private JTextField interfaceSystemTextField;
    private JTextField templateNameTextField;
    private JSyntaxTextArea templateContentTextArea;

    public DubboTelnetByFileGui() {
        super();
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());

        Box box = Box.createVerticalBox();
        box.add(makeTitlePanel());
        box.add(getAddressPanel());
        box.add(getInterfaceNamePanel());
        box.add(getParamsPanel());
        box.add(getExpectionPanel());
        box.add(getUseTemplatePanel());
        box.add(getInterfaceSystemPanel());
        box.add(getTemplateNamePanel());
        box.add(getTemplateContentPanel());

        add(box, BorderLayout.NORTH);
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
        element.setProperty(DubboTelnetByFile.EXPECTION, expectionTextField.getText());
        element.setProperty(DubboTelnetByFile.USE_TEMPLATE, (String) useTemplateComboBox.getSelectedItem());
        element.setProperty(DubboTelnetByFile.INTERFACE_SYSTEM, interfaceSystemTextField.getText());
        element.setProperty(DubboTelnetByFile.TEMPLATE_NAME, templateNameTextField.getText());
        element.setProperty(DubboTelnetByFile.TEMPLATE_CONTENT, templateContentTextArea.getText());
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        addressTextField.setText(element.getPropertyAsString(DubboTelnetByFile.ADDRESS));
        interfaceNameTextField.setText(element.getPropertyAsString(DubboTelnetByFile.INTERFACE_NAME));
        paramsTextArea.setInitialText(element.getPropertyAsString(DubboTelnetByFile.PARAMS));
        paramsTextArea.setCaretPosition(0);
        expectionTextField.setText(element.getPropertyAsString(DubboTelnetByFile.EXPECTION));
        useTemplateComboBox.setSelectedItem(element.getPropertyAsString(DubboTelnetByFile.USE_TEMPLATE));
        interfaceSystemTextField.setText(element.getPropertyAsString(DubboTelnetByFile.INTERFACE_SYSTEM));
        templateNameTextField.setText(element.getPropertyAsString(DubboTelnetByFile.TEMPLATE_NAME));
        templateContentTextArea.setInitialText(element.getPropertyAsString(DubboTelnetByFile.TEMPLATE_CONTENT));
        templateContentTextArea.setCaretPosition(0);
    }

    @Override
    public void clearGui() {
        super.clearGui();
        addressTextField.setText("");
        interfaceNameTextField.setText("");
        paramsTextArea.setInitialText("");
        expectionTextField.setText("");
        useTemplateComboBox.setSelectedItem("");
        interfaceSystemTextField.setText("");
        templateNameTextField.setText("");
        templateContentTextArea.setInitialText("");
    }

    private JPanel getAddressPanel() {
        addressTextField = new JTextField(10);
        addressTextField.setName(DubboTelnetByFile.ADDRESS);
        JLabel label = new JLabel(DubboTelnetByFile.ADDRESS);
        label.setLabelFor(addressTextField);
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(label, BorderLayout.WEST);
        panel.add(addressTextField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getInterfaceNamePanel() {
        interfaceNameTextField = new JTextField(10);
        interfaceNameTextField.setName(DubboTelnetByFile.INTERFACE_NAME);
        JLabel label = new JLabel(DubboTelnetByFile.INTERFACE_NAME);
        label.setLabelFor(interfaceNameTextField);
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(label, BorderLayout.WEST);
        panel.add(interfaceNameTextField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getParamsPanel() {
        paramsTextArea = JSyntaxTextArea.getInstance(10, 20);
        JLabel label = new JLabel(DubboTelnetByFile.PARAMS);
        label.setLabelFor(paramsTextArea);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(label, BorderLayout.NORTH);
        panel.add(JTextScrollPane.getInstance(paramsTextArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel getExpectionPanel() {
        expectionTextField = new JTextField(10);
        expectionTextField.setName(DubboTelnetByFile.EXPECTION);
        JLabel label = new JLabel(DubboTelnetByFile.EXPECTION);
        label.setLabelFor(expectionTextField);
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(label, BorderLayout.WEST);
        panel.add(expectionTextField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getUseTemplatePanel() {
        useTemplateComboBox = new JComboBox<>();
        useTemplateComboBox.setName(DubboTelnetByFile.USE_TEMPLATE);
        useTemplateComboBox.addItem("true");
        useTemplateComboBox.addItem("false");
        JLabel label = new JLabel(DubboTelnetByFile.USE_TEMPLATE);
        label.setLabelFor(useTemplateComboBox);
        JPanel jPanel = new JPanel(new BorderLayout(5, 0));
        jPanel.add(label, BorderLayout.WEST);
        jPanel.add(useTemplateComboBox, BorderLayout.CENTER);
        return jPanel;
    }

    private JPanel getInterfaceSystemPanel() {
        interfaceSystemTextField = new JTextField(10);
        interfaceSystemTextField.setName(DubboTelnetByFile.INTERFACE_SYSTEM);
        JLabel label = new JLabel(DubboTelnetByFile.INTERFACE_SYSTEM);
        label.setLabelFor(interfaceSystemTextField);
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(label, BorderLayout.WEST);
        panel.add(interfaceSystemTextField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getTemplateNamePanel() {
        templateNameTextField = new JTextField(10);
        templateNameTextField.setName(DubboTelnetByFile.TEMPLATE_NAME);
        JLabel label = new JLabel(DubboTelnetByFile.TEMPLATE_NAME);
        label.setLabelFor(templateNameTextField);
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(label, BorderLayout.WEST);
        panel.add(templateNameTextField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getTemplateContentPanel() {
        templateContentTextArea = JSyntaxTextArea.getInstance(10, 20);
        JLabel label = new JLabel(DubboTelnetByFile.TEMPLATE_CONTENT);
        label.setLabelFor(templateContentTextArea);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(label, BorderLayout.NORTH);
        panel.add(JTextScrollPane.getInstance(templateContentTextArea), BorderLayout.CENTER);
        return panel;
    }
}
