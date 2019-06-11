package pers.kelvin.util.ssh;

import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

import javax.swing.*;
import java.awt.*;


public class SSHUserInfo implements UserInfo, UIKeyboardInteractive {
    private String passwd;
    private JTextField passwordField = new JPasswordField(20);

    private final GridBagConstraints gbc =
            new GridBagConstraints(0, 0, 1, 1, 1, 1,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.NONE,
                    new Insets(0, 0, 0, 0), 0, 0);

    @Override
    public String getPassword() {
        return passwd;
    }

    @Override
    public boolean promptYesNo(String str) {
        Object[] options = {"yes", "no"};
        int foo = JOptionPane.showOptionDialog(null,
                str,
                "Warning",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null, options, options[0]);
        return foo == 0;
    }


    @Override
    public String getPassphrase() {
        return null;
    }

    @Override
    public boolean promptPassphrase(String message) {
        return false;
    }

    @Override
    public boolean promptPassword(String message) {
        Object[] ob = {passwordField};
        int result =
                JOptionPane.showConfirmDialog(null, ob, message,
                        JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            passwd = passwordField.getText();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    @Override
    public String[] promptKeyboardInteractive(String destination,
                                              String name,
                                              String instruction,
                                              String[] prompt,
                                              boolean[] echo) {
        Container panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridx = 0;
        panel.add(new JLabel(instruction), gbc);
        gbc.gridy++;

        gbc.gridwidth = GridBagConstraints.RELATIVE;

        JTextField[] texts = new JTextField[prompt.length];
        for (int i = 0; i < prompt.length; i++) {
            gbc.fill = GridBagConstraints.NONE;
            gbc.gridx = 0;
            gbc.weightx = 1;
            panel.add(new JLabel(prompt[i]), gbc);

            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weighty = 1;
            if (echo[i]) {
                texts[i] = new JTextField(20);
            } else {
                texts[i] = new JPasswordField(20);
            }
            panel.add(texts[i], gbc);
            gbc.gridy++;
        }

        if (JOptionPane.showConfirmDialog(null, panel,
                destination + ": " + name,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE)
                == JOptionPane.OK_OPTION) {
            String[] response = new String[prompt.length];
            for (int i = 0; i < prompt.length; i++) {
                response[i] = texts[i].getText();
            }
            return response;
        } else {
            // cancel
            return null;
        }
    }
}

