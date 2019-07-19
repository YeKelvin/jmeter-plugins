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

        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBorder(GuiUtil.createTitledBorder("配置SSH信息"));

        bodyPanel.add(getSSHAddressLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getSSHAddressTextField(), GuiUtil.GridBag.editorConstraints);

        bodyPanel.add(getSSHUserNameLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getSSHUserNameTextField(), GuiUtil.GridBag.editorConstraints);

        bodyPanel.add(getSSHPasswordLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getSSHPasswordTextField(), GuiUtil.GridBag.editorConstraints);

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(bodyPanel);

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

    private Component getSSHAddressTextField() {
        if (sshAddressTextField == null) {
            sshAddressTextField = GuiUtil.createTextField(SSHConnectionConfiguration.SSH_ADDRESS);
        }
        return sshAddressTextField;
    }

    private Component getSSHAddressLabel() {
        return GuiUtil.createLabel("SSH地址：", getSSHAddressTextField());
    }

    private Component getSSHUserNameTextField() {
        if (sshUserNameTextField == null) {
            sshUserNameTextField = GuiUtil.createTextField(SSHConnectionConfiguration.SSH_USER_NAME);
        }
        return sshUserNameTextField;
    }

    private Component getSSHUserNameLabel() {
        return GuiUtil.createLabel("SSH用户名称：", getSSHUserNameTextField());
    }

    private Component getSSHPasswordTextField() {
        if (sshPasswordTextField == null) {
            sshPasswordTextField = GuiUtil.createTextField(SSHConnectionConfiguration.SSH_PASSWORD);
        }
        return sshPasswordTextField;
    }

    private Component getSSHPasswordLabel() {
        return GuiUtil.createLabel("SSH密码：", getSSHPasswordTextField());
    }

}
