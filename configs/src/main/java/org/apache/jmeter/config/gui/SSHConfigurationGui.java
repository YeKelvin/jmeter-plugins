package org.apache.jmeter.config.gui;


import org.apache.jmeter.common.jmeter.JMeterGuiUtil;
import org.apache.jmeter.config.SSHConfiguration;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;

import javax.swing.*;
import java.awt.*;

/**
 * SSH本地端口转发插件
 *
 * @author Kelvin.Ye
 */
public class SSHConfigurationGui extends AbstractConfigGui {

    private static final String NOTE = "请把此组件放在JDBC组件后面，不然连接关闭时会报IOException";

    private JTextField sshAddressTextField;
    private JTextField sshUserNameTextField;
    private JTextField sshPasswordTextField;
    private JComboBox<String> sshLocalForwardingComboBox;
    private JTextField localForwardingPortTextField;
    private JTextField remoteAddressTextField;

    public SSHConfigurationGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);
        add(createBodyPanel(),BorderLayout.CENTER);
        add(createNoteArea(), BorderLayout.SOUTH);
    }

    @Override
    public String getStaticLabel() {
        return "SSH配置器";
    }


    @Override
    public String getLabelResource() {
        return null;
    }


    @Override
    public TestElement createTestElement() {
        SSHConfiguration el = new SSHConfiguration();
        modifyTestElement(el);
        return el;
    }

    /**
     * GUI -> TestElement
     */
    @Override
    public void modifyTestElement(TestElement el) {
        super.configureTestElement(el);
        el.setProperty(SSHConfiguration.SSH_ADDRESS, sshAddressTextField.getText());
        el.setProperty(SSHConfiguration.SSH_USER_NAME, sshUserNameTextField.getText());
        el.setProperty(SSHConfiguration.SSH_PASSWORD, sshPasswordTextField.getText());
        el.setProperty(SSHConfiguration.SSH_PORT_FORWARDING, (String) sshLocalForwardingComboBox.getSelectedItem());
        el.setProperty(SSHConfiguration.LOCAL_FORWARDING_PORT, localForwardingPortTextField.getText());
        el.setProperty(SSHConfiguration.REMOTE_ADDRESS, remoteAddressTextField.getText());
    }

    /**
     * TestElement -> GUI
     */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        sshAddressTextField.setText(el.getPropertyAsString(SSHConfiguration.SSH_ADDRESS));
        sshUserNameTextField.setText(el.getPropertyAsString(SSHConfiguration.SSH_USER_NAME));
        sshPasswordTextField.setText(el.getPropertyAsString(SSHConfiguration.SSH_PASSWORD));
        sshLocalForwardingComboBox.setSelectedItem(el.getPropertyAsString(SSHConfiguration.SSH_PORT_FORWARDING));
        localForwardingPortTextField.setText(el.getPropertyAsString(SSHConfiguration.LOCAL_FORWARDING_PORT));
        remoteAddressTextField.setText(el.getPropertyAsString(SSHConfiguration.REMOTE_ADDRESS));
    }

    @Override
    public void clearGui() {
        super.clearGui();
        sshAddressTextField.setText("");
        sshUserNameTextField.setText("");
        sshPasswordTextField.setText("");
        sshLocalForwardingComboBox.setSelectedItem("");
        localForwardingPortTextField.setText("");
        remoteAddressTextField.setText("");
    }

    private Component createSSHAddressTextField() {
        if (sshAddressTextField == null) {
            sshAddressTextField = JMeterGuiUtil.createTextField(SSHConfiguration.SSH_ADDRESS);
        }
        return sshAddressTextField;
    }

    private Component createSSHAddressLabel() {
        return JMeterGuiUtil.createLabel("SSH地址：", createSSHAddressTextField());
    }

    private Component createSSHUserNameTextField() {
        if (sshUserNameTextField == null) {
            sshUserNameTextField = JMeterGuiUtil.createTextField(SSHConfiguration.SSH_USER_NAME);
        }
        return sshUserNameTextField;
    }

    private Component createSSHUserNameLabel() {
        return JMeterGuiUtil.createLabel("SSH用户名称：", createSSHAddressLabel());
    }

    private Component createSSHPasswordTextField() {
        if (sshPasswordTextField == null) {
            sshPasswordTextField = JMeterGuiUtil.createTextField(SSHConfiguration.SSH_PASSWORD);
        }
        return sshPasswordTextField;
    }

    private Component createSSHPasswordLabel() {
        return JMeterGuiUtil.createLabel("SSH密码：", createSSHPasswordTextField());
    }

    private Component createSSHLocalForwardingComboBox() {
        if (sshLocalForwardingComboBox == null) {
            sshLocalForwardingComboBox = JMeterGuiUtil.createComboBox(SSHConfiguration.SSH_PORT_FORWARDING);
            sshLocalForwardingComboBox.addItem("true");
            sshLocalForwardingComboBox.addItem("false");
        }
        return sshLocalForwardingComboBox;
    }

    private Component createSSHLocalForwardingLabel() {
        return JMeterGuiUtil.createLabel("启用本地端口转发：", createSSHLocalForwardingComboBox());
    }


    private Component createLocalForwardingPortTextField() {
        if (localForwardingPortTextField == null) {
            localForwardingPortTextField = JMeterGuiUtil.createTextField(SSHConfiguration.LOCAL_FORWARDING_PORT);
        }
        return localForwardingPortTextField;
    }

    private Component createLocalForwardingPortLabel() {
        return JMeterGuiUtil.createLabel("本地转发端口：", createLocalForwardingPortTextField());
    }

    private Component createRemoteAddressTextField() {
        if (remoteAddressTextField == null) {
            remoteAddressTextField = JMeterGuiUtil.createTextField(SSHConfiguration.REMOTE_ADDRESS);
        }
        return remoteAddressTextField;
    }

    private Component createRemoteAddressLabel() {
        return JMeterGuiUtil.createLabel("远程地址：", createRemoteAddressTextField());
    }

    private Component createBodyPanel() {
        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBorder(JMeterGuiUtil.createTitledBorder("配置SSH信息"));

        bodyPanel.add(createSSHAddressLabel(), JMeterGuiUtil.GridBag.labelConstraints);
        bodyPanel.add(createSSHAddressTextField(), JMeterGuiUtil.GridBag.editorConstraints);

        bodyPanel.add(createSSHUserNameLabel(), JMeterGuiUtil.GridBag.labelConstraints);
        bodyPanel.add(createSSHUserNameTextField(), JMeterGuiUtil.GridBag.editorConstraints);

        bodyPanel.add(createSSHPasswordLabel(), JMeterGuiUtil.GridBag.labelConstraints);
        bodyPanel.add(createSSHPasswordTextField(), JMeterGuiUtil.GridBag.editorConstraints);

        bodyPanel.add(createSSHLocalForwardingLabel(), JMeterGuiUtil.GridBag.labelConstraints);
        bodyPanel.add(createSSHLocalForwardingComboBox(), JMeterGuiUtil.GridBag.editorConstraints);

        bodyPanel.add(createLocalForwardingPortLabel(), JMeterGuiUtil.GridBag.labelConstraints);
        bodyPanel.add(createLocalForwardingPortTextField(), JMeterGuiUtil.GridBag.editorConstraints);

        bodyPanel.add(createRemoteAddressLabel(), JMeterGuiUtil.GridBag.labelConstraints);
        bodyPanel.add(createRemoteAddressTextField(), JMeterGuiUtil.GridBag.editorConstraints);

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(bodyPanel);
        return mainPanel;
    }

    private Component createNoteArea() {
        return JMeterGuiUtil.createNoteArea(NOTE, this.getBackground());
    }
}
