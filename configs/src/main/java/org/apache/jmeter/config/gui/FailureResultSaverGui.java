package org.apache.jmeter.config.gui;

import org.apache.jmeter.config.FailureResultSaver;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;
import pers.kelvin.util.GuiUtil;

import javax.swing.*;
import java.awt.*;

public class FailureResultSaverGui extends AbstractConfigGui {
    private static final int H_GAP = 5;
    private static final int V_GAP = 10;
    private static final int LABEL_WIDTH = 100;
    private static final int LABEL_HEIGHT = 10;

    private JTextField logPathTextField;

    public FailureResultSaverGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(getLogPathPanel());

        add(mainPanel, BorderLayout.CENTER);
    }

    @Override
    public String getStaticLabel() {
        return "Failure Result Saver";
    }


    @Override
    public String getLabelResource() {
        return null;
    }


    @Override
    public TestElement createTestElement() {
        FailureResultSaver el = new FailureResultSaver();
        modifyTestElement(el);
        return el;
    }

    /**
     * 将数据从GUI元素移动到TestElement
     */
    @Override
    public void modifyTestElement(TestElement el) {
        super.configureTestElement(el);
        el.setProperty(FailureResultSaver.LOG_PATH, logPathTextField.getText());
    }

    /**
     * 将数据设置到GUI元素中
     */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        logPathTextField.setText(el.getPropertyAsString(FailureResultSaver.LOG_PATH));
    }

    @Override
    public void clearGui() {
        super.clearGui();
        logPathTextField.setText("");
    }

    private JPanel getLogPathPanel() {
        logPathTextField = new JTextField(10);
        logPathTextField.setName(FailureResultSaver.LOG_PATH);

        JLabel label = GuiUtil.createTextFieldLabel("LogPath:", logPathTextField, LABEL_WIDTH, LABEL_HEIGHT);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(logPathTextField, BorderLayout.CENTER);
        return panel;
    }
}
