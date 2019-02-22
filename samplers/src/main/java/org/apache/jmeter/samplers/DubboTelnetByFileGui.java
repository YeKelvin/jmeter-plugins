package org.apache.jmeter.samplers;

import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.gui.util.JTextScrollPane;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
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
    ;
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

    }

    @Override
    public void configure(TestElement el) {
        super.configure(el);
    }

    @Override
    public void clearGui() {
        super.clearGui();
    }

    private JPanel createAddressPanel() {
        addressTextField = new JTextField(10);
        addressTextField.setName(DubboTelnetByFile.ADDRESS);

        JLabel label = new JLabel(DubboTelnetByFile.ADDRESS);
        label.setLabelFor(addressTextField);

        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(label, BorderLayout.WEST);
        panel.add(addressTextField, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInterfaceNamePanel() {
        interfaceNameTextField = new JTextField(10);
        interfaceNameTextField.setName(DubboTelnetByFile.INTERFACE_NAME);

        JLabel label = new JLabel(DubboTelnetByFile.INTERFACE_NAME);
        label.setLabelFor(interfaceNameTextField);

        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(label, BorderLayout.WEST);
        panel.add(interfaceNameTextField, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createParamsPanel() {
        paramsTextArea = JSyntaxTextArea.getInstance(20, 20);

        JLabel label = new JLabel(DubboTelnetByFile.PARAMS);
        label.setLabelFor(paramsTextArea);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(label, BorderLayout.NORTH);
        panel.add(JTextScrollPane.getInstance(paramsTextArea), BorderLayout.CENTER);

        return panel;
    }
}
