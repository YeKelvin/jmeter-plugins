package org.apache.jmeter.config.gui;


import org.apache.jmeter.config.SSHConnectionConfiguration;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;
import pers.kelvin.util.GuiUtil;

import javax.swing.*;
import java.awt.*;

/**
 * SSH连接信息配置插件
 *
 * @author Kelvin.Ye
 */
public class SSHConnectionConfigurationGui extends AbstractConfigGui {
    private static final int H_GAP = 5;
    private static final int V_GAP = 10;
    private static final int LABEL_WIDTH = 100;
    private static final int LABEL_HEIGHT = 10;

    private JTextField sshAddressTextField;
    private JTextField sshUserNameTextField;
    private JTextField sshPasswordTextField;

    public SSHConnectionConfigurationGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.setBorder(GuiUtil.createTitledBorder("Configure the SSH Info"));
        mainPanel.add(getSSHAddressPanel());
        mainPanel.add(getSSHUserNamePanel());
        mainPanel.add(getSSHPasswordPanel());

        add(mainPanel, BorderLayout.CENTER);
    }

    @Override
    public String getStaticLabel() {
        return "SSH Connection Configuration";
    }


    @Override
    public String getLabelResource() {
        return null;
    }


    @Override
    public TestElement createTestElement() {
        SSHConnectionConfiguration el = new SSHConnectionConfiguration();
        modifyTestElement(el);
        return el;
    }

    /**
     * 将数据从GUI元素移动到TestElement
     */
    @Override
    public void modifyTestElement(TestElement el) {
        super.configureTestElement(el);
        el.setProperty(SSHConnectionConfiguration.SSH_ADDRESS, sshAddressTextField.getText());
        el.setProperty(SSHConnectionConfiguration.SSH_USER_NAME, sshUserNameTextField.getText());
        el.setProperty(SSHConnectionConfiguration.SSH_PASSWORD, sshPasswordTextField.getText());
    }

    /**
     * 将数据设置到GUI元素中
     */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        sshAddressTextField.setText(el.getPropertyAsString(SSHConnectionConfiguration.SSH_ADDRESS));
        sshUserNameTextField.setText(el.getPropertyAsString(SSHConnectionConfiguration.SSH_USER_NAME));
        sshPasswordTextField.setText(el.getPropertyAsString(SSHConnectionConfiguration.SSH_PASSWORD));
    }

    @Override
    public void clearGui() {
        super.clearGui();
        sshAddressTextField.setText("");
        sshUserNameTextField.setText("");
        sshPasswordTextField.setText("");
    }

    private JPanel getSSHAddressPanel() {
        sshAddressTextField = new JTextField(10);
        sshAddressTextField.setName(SSHConnectionConfiguration.SSH_ADDRESS);

        JLabel label = GuiUtil.createTextFieldLabel("SSHAddress:", sshAddressTextField, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(sshAddressTextField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getSSHUserNamePanel() {
        sshUserNameTextField = new JTextField(10);
        sshUserNameTextField.setName(SSHConnectionConfiguration.SSH_USER_NAME);

        JLabel label = GuiUtil.createTextFieldLabel("SSHUserName:", sshUserNameTextField, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(sshUserNameTextField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getSSHPasswordPanel() {
        sshPasswordTextField = new JTextField(10);
        sshPasswordTextField.setName(SSHConnectionConfiguration.SSH_PASSWORD);

        JLabel label = GuiUtil.createTextFieldLabel("SSHPassword:", sshPasswordTextField, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(sshPasswordTextField, BorderLayout.CENTER);
        return panel;
    }

}
