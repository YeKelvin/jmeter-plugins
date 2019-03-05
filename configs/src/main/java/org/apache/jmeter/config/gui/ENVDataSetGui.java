package org.apache.jmeter.config.gui;

import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.ENVDataSet;
import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.GuiUtils;
import org.apache.jorphan.gui.ObjectTableModel;
import org.apache.jorphan.reflect.Functor;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author KelvinYe
 */
public class ENVDataSetGui extends AbstractConfigGui {
    private static final int H_GAP = 5;
    private static final int V_GAP = 10;
    private static final int LABEL_WIDTH = 100;
    private static final int LABEL_HEIGHT = 10;

    private JComboBox<String> configNameComboBox;
    private JTable table;
    private ObjectTableModel tableModel;

    public ENVDataSetGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);

        VerticalPanel configPanel = new VerticalPanel();
        configPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Configure the Data Source"));
        configPanel.add(getConfigNamePanel());

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(configPanel);
        mainPanel.add(createTablePanel());

        add(mainPanel, BorderLayout.CENTER);
        add(getNotePanel(), BorderLayout.SOUTH);

    }

    @Override
    public String getStaticLabel() {
        return "ENV Date Set";
    }


    @Override
    public String getLabelResource() {
        return null;
    }


    @Override
    public TestElement createTestElement() {
        ENVDataSet envDataSet = new ENVDataSet();
        modifyTestElement(envDataSet);
        return envDataSet;
    }

    /**
     * 将数据从GUI元素移动到TestElement
     */
    @Override
    public void modifyTestElement(TestElement el) {
        super.configureTestElement(el);
        el.setProperty(ENVDataSet.CONFIG_NAME, (String) configNameComboBox.getSelectedItem());
    }

    /**
     * 将数据设置到GUI元素中
     */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        String fileName = el.getPropertyAsString(ENVDataSet.CONFIG_NAME);
        configNameComboBox.setSelectedItem(fileName);
        tableModel.clearData();
        if (el instanceof ENVDataSet && fileName != null && !fileName.isEmpty()) {
            ENVDataSet envDataSet = (ENVDataSet) el;
            HashMap<String, String> envMap = envDataSet.getEnvMap(envDataSet.getFilePath());
            for (Map.Entry<String, String> entry : envMap.entrySet()) {
                Argument arg = new Argument();
                arg.setName(entry.getKey());
                arg.setValue(entry.getValue());
                tableModel.addRow(arg);
            }
        }
    }

    @Override
    public void clearGui() {
        super.clearGui();
        configNameComboBox.setSelectedItem("");
        GuiUtils.stopTableEditing(table);
        tableModel.clearData();
    }

    private JPanel getConfigNamePanel() {
        configNameComboBox = new JComboBox<>();
        configNameComboBox.setName(ENVDataSet.CONFIG_NAME);
        comboBoxAddItem(getEnvList(getConfigPath()));

        JLabel label = new JLabel(ENVDataSet.CONFIG_NAME + ":");
        label.setLabelFor(configNameComboBox);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(LABEL_WIDTH, LABEL_HEIGHT));

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(label, BorderLayout.WEST);
        panel.add(configNameComboBox, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getNotePanel() {
        String note = "\n说明：\n" +
                "1. ConfigName为配置文件名称，文件后缀为.env ，内容为json ，且必须存放在 jmeterHome/config 目录下；\n" +
                "2. Non-Gui模式下，命令行存在 -JconfigName 参数时，优先读取 ${__P(configName)} 配置文件。\n";
        JTextArea textArea = new JTextArea(note);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setBackground(this.getBackground());

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(textArea, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTablePanel() {
        initializeTableModel();
        table = new JTable(tableModel);
        table.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.revalidate();
        JMeterUtils.applyHiDPI(table);

        JPanel panel = new JPanel(new BorderLayout(H_GAP, V_GAP));
        panel.add(makeScrollPane(table), BorderLayout.CENTER);
        panel.add(Box.createVerticalStrut(70), BorderLayout.WEST);

        return panel;
    }

    private void initializeTableModel() {
        tableModel = new ObjectTableModel(new String[]{"name", "value"},
                Argument.class,
                new Functor[]{
                        new Functor("getName"),
                        new Functor("getValue")},
                new Functor[]{
                        new Functor("setName"),
                        new Functor("setValue")},
                new Class[]{String.class, String.class});
    }

    private void comboBoxAddItem(ArrayList<File> fileList) {
        for (File file : fileList) {
            configNameComboBox.addItem(file.getName());
        }
    }

    private ArrayList<File> getEnvList(String dirPath) {
        ArrayList<File> envList = new ArrayList<>();
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    envList.addAll(getEnvList(file.getAbsolutePath()));
                } else if (file.getName().endsWith("env")) {
                    envList.add(file);
                }
            }
        }
        return envList;
    }

    private String getConfigPath() {
        return JMeterUtils.getJMeterHome() + File.separator + "config";
    }

}

