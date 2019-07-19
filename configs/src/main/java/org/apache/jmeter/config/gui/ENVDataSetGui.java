package org.apache.jmeter.config.gui;

import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.ENVDataSet;
import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.GuiUtils;
import org.apache.jorphan.gui.ObjectTableModel;
import org.apache.jorphan.reflect.Functor;
import pers.kelvin.util.GuiUtil;
import pers.kelvin.util.StringUtil;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.File;
import java.util.*;

/**
 * @author KelvinYe
 */
public class ENVDataSetGui extends AbstractConfigGui {

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

        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBorder(GuiUtil.createTitledBorder("配置环境信息"));
        bodyPanel.add(getConfigNameLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getConfigNameComboBox(), GuiUtil.GridBag.editorConstraints);
        bodyPanel.add(createTablePanel(), GuiUtil.GridBag.fillBottomConstraints);

        add(bodyPanel, BorderLayout.CENTER);
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
        if (el instanceof ENVDataSet && StringUtil.isNotBlank(fileName)) {
            ENVDataSet envDataSet = (ENVDataSet) el;
            Map<String, String> envMap = envDataSet.getEnvMap(envDataSet.getFilePath());
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

    private Component getConfigNameComboBox() {
        if (configNameComboBox == null) {
            configNameComboBox = GuiUtil.createComboBox(ENVDataSet.CONFIG_NAME);
            comboBoxAddItem(getEnvList(getConfigPath()));
        }
        return configNameComboBox;
    }

    private Component getConfigNameLabel() {
        return GuiUtil.createLabel("ENV文件名称：", getConfigNameComboBox());
    }

    private JPanel createTablePanel() {
        initializeTableModel();
        table = new JTable(tableModel);
        table.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.revalidate();
        JMeterUtils.applyHiDPI(table);


        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(makeScrollPane(table), BorderLayout.CENTER);
        panel.add(Box.createVerticalStrut(70), BorderLayout.WEST);
        return panel;
    }

    private JPanel getNotePanel() {
        String note = "说明：\n" +
                "1. 配置文件名称的后缀必须为 .env ，内容为 json ，且必须放在 ${JMETER_HOME}/config 目录下；\n" +
                "2. Non-Gui模式下，命令行存在 -JconfigName 参数时，优先读取 ${__P(configName)} 配置文件。";
        return GuiUtil.createNotePanel(note, this.getBackground());
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
        configNameComboBox.addItem("");
        for (File file : fileList) {
            configNameComboBox.addItem(file.getName());
        }
    }

    /**
     * 获取 env文件的列表
     *
     * @param dirPath env文件所在目录
     */
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

