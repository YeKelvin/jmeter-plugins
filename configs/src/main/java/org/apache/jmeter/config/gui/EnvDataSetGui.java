package org.apache.jmeter.config.gui;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.common.utils.GuiUtil;
import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.EnvDataSet;
import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.GuiUtils;
import org.apache.jorphan.gui.ObjectTableModel;
import org.apache.jorphan.reflect.Functor;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author KelvinYe
 */
public class EnvDataSetGui extends AbstractConfigGui {

    private JComboBox<String> configNameComboBox;
    private JTable table;
    private ObjectTableModel tableModel;

    public EnvDataSetGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);
        add(createBodyPanel(), BorderLayout.CENTER);
        add(createNoteArea(), BorderLayout.SOUTH);
    }

    @Override
    public String getStaticLabel() {
        return "环境变量配置器";
    }


    @Override
    public String getLabelResource() {
        return null;
    }


    @Override
    public TestElement createTestElement() {
        EnvDataSet envDataSet = new EnvDataSet();
        modifyTestElement(envDataSet);
        return envDataSet;
    }

    /**
     * 将数据从GUI元素移动到TestElement
     */
    @Override
    public void modifyTestElement(TestElement el) {
        super.configureTestElement(el);
        el.setProperty(EnvDataSet.CONFIG_NAME, (String) configNameComboBox.getSelectedItem());
    }

    /**
     * 将数据设置到GUI元素中
     */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        String fileName = el.getPropertyAsString(EnvDataSet.CONFIG_NAME);
        configNameComboBox.setSelectedItem(fileName);
        tableModel.clearData();
        if (el instanceof EnvDataSet && StringUtils.isNotBlank(fileName)) {
            EnvDataSet envDataSet = (EnvDataSet) el;
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

    private Component createConfigNameComboBox() {
        if (configNameComboBox == null) {
            configNameComboBox = GuiUtil.createComboBox(EnvDataSet.CONFIG_NAME);
            comboBoxAddItem(getConfigFileList(getConfigPath()));
        }
        return configNameComboBox;
    }

    private Component createConfigNameLabel() {
        return GuiUtil.createLabel("配置文件名称：", createConfigNameComboBox());
    }

    private Component createTablePanel() {
        initializeTableModel();
        // 列排序
        TableRowSorter<ObjectTableModel> sorter = new TableRowSorter<>(tableModel);
        // 设置只有第一列可以排序，其他均不可以
        sorter.setSortable(0, true);
        sorter.setSortable(1, false);

        table = new JTable(tableModel);
        table.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.revalidate();
        table.setRowSorter(sorter);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(makeScrollPane(table), BorderLayout.CENTER);
        panel.add(Box.createVerticalStrut(70), BorderLayout.WEST);
        return panel;
    }

    private Component createBodyPanel() {
        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBorder(GuiUtil.createTitledBorder("请选择测试环境"));
        bodyPanel.add(createConfigNameLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(createConfigNameComboBox(), GuiUtil.GridBag.editorConstraints);
        bodyPanel.add(createTablePanel(), GuiUtil.GridBag.fillBottomConstraints);
        return bodyPanel;
    }

    private Component createNoteArea() {
        String note =
                "1. 配置文件必须是 Yaml格式 ，且必须放在 ${JMETER_HOME}/config 目录下\n" +
                        "2. Non-Gui命令说明：存在 -JconfigName 选项时，优先读取 ${__P(configName)} 配置文件";
        return GuiUtil.createNoteArea(note, this.getBackground());
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
     * 获取配置文件的列表
     *
     * @param dirPath 配置文件所在目录
     */
    private ArrayList<File> getConfigFileList(String dirPath) {
        ArrayList<File> fileList = new ArrayList<>();
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    fileList.addAll(getConfigFileList(file.getAbsolutePath()));
                } else if (file.getName().endsWith("yaml")) {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }

    private String getConfigPath() {
        return JMeterUtils.getJMeterHome() + File.separator + "config";
    }

}
