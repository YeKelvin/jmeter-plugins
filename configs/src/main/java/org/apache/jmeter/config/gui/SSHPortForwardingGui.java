package org.apache.jmeter.config.gui;


import org.apache.jmeter.config.SSHPortForwarding;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;
import pers.kelvin.util.GuiUtil;

import javax.swing.*;
import java.awt.*;

/**
 * SSH本地端口转发插件
 *
 * @author Kelvin.Ye
 */
public class SSHPortForwardingGui extends AbstractConfigGui {

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

        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBorder(GuiUtil.createTitledBorder("配置SSH信息"));

        bodyPanel.add(getSSHAddressLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getSSHAddressTextField(), GuiUtil.GridBag.editorConstraints);

        bodyPanel.add(getSSHUserNameLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getSSHUserNameTextField(), GuiUtil.GridBag.editorConstraints);

        bodyPanel.add(getSSHPasswordLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getSSHPasswordTextField(), GuiUtil.GridBag.editorConstraints);

        bodyPanel.add(getLocalForwardingPortLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getLocalForwardingPortTextField(), GuiUtil.GridBag.editorConstraints);

        bodyPanel.add(getRemoteAddressLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getRemoteAddressTextField(), GuiUtil.GridBag.editorConstraints);

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(bodyPanel);
        mainPanel.add(getNotePanel());

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

    private Component getSSHAddressTextField() {
        if (sshAddressTextField == null) {
            sshAddressTextField = GuiUtil.createTextField(SSHPortForwarding.SSH_ADDRESS);
        }
        return sshAddressTextField;
    }

    private Component getSSHAddressLabel() {
        return GuiUtil.createLabel("SSH地址：", getSSHAddressTextField());
    }

    private Component getSSHUserNameTextField() {
        if (sshUserNameTextField == null) {
            sshUserNameTextField = GuiUtil.createTextField(SSHPortForwarding.SSH_USER_NAME);
        }
        return sshUserNameTextField;
    }

    private Component getSSHUserNameLabel() {
        return GuiUtil.createLabel("SSH用户名称：", getSSHAddressLabel());
    }

    private Component getSSHPasswordTextField() {
        if (sshPasswordTextField == null) {
            sshPasswordTextField = GuiUtil.createTextField(SSHPortForwarding.SSH_PASSWORD);
        }
        return sshPasswordTextField;
    }

    private Component getSSHPasswordLabel() {
        return GuiUtil.createLabel("SSH密码：", getSSHPasswordTextField());
    }

    private Component getLocalForwardingPortTextField() {
        if (localForwardingPortTextField == null) {
            localForwardingPortTextField = GuiUtil.createTextField(SSHPortForwarding.LOCAL_FORWARDING_PORT);
        }
        return localForwardingPortTextField;
    }

    private Component getLocalForwardingPortLabel() {
        return GuiUtil.createLabel("本地转发端口：", getLocalForwardingPortTextField());
    }

    private Component getRemoteAddressTextField() {
        if (remoteAddressTextField == null) {
            remoteAddressTextField = GuiUtil.createTextField(SSHPortForwarding.REMOTE_ADDRESS);
        }
        return remoteAddressTextField;
    }

    private Component getRemoteAddressLabel() {
        return GuiUtil.createLabel("远程地址：", getRemoteAddressTextField());
    }

    private Component getNotePanel() {
        String note = "\n注意：请把此组件放在JDBC组件后面，不然连接关闭时会报IOException。";
        return GuiUtil.createNotePanel(note, this.getBackground());
    }
}
