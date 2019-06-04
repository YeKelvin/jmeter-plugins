package org.apache.jmeter.config.gui;


import org.apache.jmeter.config.SSHPortForwarding;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;
import pers.kelvin.util.GuiUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author Kelvin.Ye
 */
public class SSHPortForwardingGui extends AbstractConfigGui {
    private static final int H_GAP = 5;
    private static final int V_GAP = 10;
    private static final int LABEL_WIDTH = 100;
    private static final int LABEL_HEIGHT = 10;

    private JTextField sshAddressTextField;
    private JTextField sshUserNameTextField;
    private JTextField sshPasswordTextField;
    private JTextField localForwardingPortTextField;
    private JTextField remoteAddressTextField;

    public SSHPortForwardingGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(getSSHAddressPanel());
        mainPanel.add(getSSHUserNamePanel());
        mainPanel.add(getSSHPasswordPanel());
        mainPanel.add(getLocalForwardingPortPanel());
        mainPanel.add(getRemoteAddressPanel());

        add(mainPanel, BorderLayout.CENTER);
    }

    @Override
    public String getStaticLabel() {
        return "SSH Port Forwarding";
    }


    @Override
    public String getLabelResource() {
        return null;
    }


    @Override
    public TestElement createTestElement() {
        SSHPortForwarding el = new SSHPortForwarding();
        modifyTestElement(el);
        return el;
    }

    /**
     * 将数据从GUI元素移动到TestElement
     */
    @Override
    public void modifyTestElement(TestElement el) {
        super.configureTestElement(el);
        el.setProperty(SSHPortForwarding.SSH_ADDRESS, sshAddressTextField.getText());
        el.setProperty(SSHPortForwarding.SSH_USER_NAME, sshUserNameTextField.getText());
        el.setProperty(SSHPortForwarding.SSH_PASSWORD, sshPasswordTextField.getText());
        el.setProperty(SSHPortForwarding.LOCAL_FORWARDING_PORT, localForwardingPortTextField.getText());
        el.setProperty(SSHPortForwarding.REMOTE_ADDRESS, remoteAddressTextField.getText());
    }

    /**
     * 将数据设置到GUI元素中
     */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        sshAddressTextField.setText(el.getPropertyAsString(SSHPortForwarding.SSH_ADDRESS));
        sshUserNameTextField.setText(el.getPropertyAsString(SSHPortForwarding.SSH_USER_NAME));
        sshPasswordTextField.setText(el.getPropertyAsString(SSHPortForwarding.SSH_PASSWORD));
        localForwardingPortTextField.setText(el.getPropertyAsString(SSHPortForwarding.LOCAL_FORWARDING_PORT));
        remoteAddressTextField.setText(el.getPropertyAsString(SSHPortForwarding.REMOTE_ADDRESS));
    }

    @Override
    public void clearGui() {
        super.clearGui();
        sshAddressTextField.setText("");
        sshUserNameTextField.setText("");
        sshPasswordTextField.setText("");
        localForwardingPortTextField.setText("");
        remoteAddressTextField.setText("");
    }

    private JPanel getSSHAddressPanel() {
        sshAddressTextField = new JTextField(10);
        sshAddressTextField.setName(SSHPortForwarding.SSH_ADDRESS);

        JLabel label = GuiUtil.createTextFieldLabel("SSHAddress:", sshAddressTextField, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(sshAddressTextField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getSSHUserNamePanel() {
        sshUserNameTextField = new JTextField(10);
        sshUserNameTextField.setName(SSHPortForwarding.SSH_USER_NAME);

        JLabel label = GuiUtil.createTextFieldLabel("SSHUserName:", sshUserNameTextField, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(sshUserNameTextField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getSSHPasswordPanel() {
        sshPasswordTextField = new JTextField(10);
        sshPasswordTextField.setName(SSHPortForwarding.SSH_PASSWORD);

        JLabel label = GuiUtil.createTextFieldLabel("SSHPassword:", sshPasswordTextField, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(sshPasswordTextField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getLocalForwardingPortPanel() {
        localForwardingPortTextField = new JTextField(10);
        localForwardingPortTextField.setName(SSHPortForwarding.LOCAL_FORWARDING_PORT);

        JLabel label = GuiUtil.createTextFieldLabel("LocalForwardingPort:", localForwardingPortTextField, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(localForwardingPortTextField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getRemoteAddressPanel() {
        remoteAddressTextField = new JTextField(10);
        remoteAddressTextField.setName(SSHPortForwarding.REMOTE_ADDRESS);

        JLabel label = GuiUtil.createTextFieldLabel("RemoteAddress:", remoteAddressTextField, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(remoteAddressTextField, BorderLayout.CENTER);
        return panel;
    }
}
