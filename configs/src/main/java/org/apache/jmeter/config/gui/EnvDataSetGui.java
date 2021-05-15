package org.apache.jmeter.config.gui;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.common.jmeter.JMeterGuiUtil;
import org.apache.jmeter.common.utils.YamlUtil;
import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.EnvDataSet;
import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
import org.apache.jmeter.services.FileServer;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.GuiUtils;
import org.apache.jorphan.gui.ObjectTableModel;
import org.apache.jorphan.reflect.Functor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author KelvinYe
 */
public class EnvDataSetGui extends AbstractConfigGui implements ActionListener {

    private static final Logger log = LoggerFactory.getLogger(EnvDataSetGui.class);

    private static final String NOTE =
            "1、配置文件为yaml格式 ，目前仅支持放置在 ${JMETER_HOME}/config 目录下\n" +
                    "2、Non-Gui命令说明：存在 -JconfigName 选项时，优先读取 ${__P(configName)} 配置文件";

    private static final String OPEN_ACTION = "OPEN";

    private JComboBox<String> configNameComboBox;
    private JTable table;
    private ObjectTableModel tableModel;

    private final String scriptName;
    private final String configDirectory;

    public static final HashMap<String, String> CACHED_CONFIG_NAME_WITH_SCRIPT = new HashMap<>();

    public EnvDataSetGui() {
        scriptName = FileServer.getFileServer().getScriptName();
        configDirectory = JMeterUtils.getJMeterHome() + File.separator + "config";
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
        String configName = (String) configNameComboBox.getSelectedItem();
        el.setProperty(EnvDataSet.CONFIG_NAME, configName);
        CACHED_CONFIG_NAME_WITH_SCRIPT.put(scriptName, configName);
    }

    /**
     * 将数据设置到GUI元素中
     */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        String configName = el.getPropertyAsString(EnvDataSet.CONFIG_NAME);
        configNameComboBox.setSelectedItem(configName);
        tableModel.clearData();
        if (el instanceof EnvDataSet && StringUtils.isNotBlank(configName)) {
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

    /**
     * 打开配置文件或配置文件目录
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals(OPEN_ACTION)) {
            try {
                String configName = String.valueOf(configNameComboBox.getSelectedItem());
                String openFilePath;
                if (StringUtils.isNotBlank(configName)) {
                    openFilePath = getConfigPath(configName);
                } else {
                    openFilePath = configDirectory;
                }
                Desktop.getDesktop().open(new File(openFilePath));
            } catch (IOException ioException) {
                log.error(ExceptionUtil.getStackTrace(ioException));
            }
        }
    }

    private Component createConfigNameComboBox() {
        if (configNameComboBox == null) {
            configNameComboBox = JMeterGuiUtil.createComboBox(EnvDataSet.CONFIG_NAME);
            comboBoxAddItem(getConfigList(configDirectory));
        }
        return configNameComboBox;
    }

    private Component createConfigNameLabel() {
        return JMeterGuiUtil.createLabel("配置文件名称：", createConfigNameComboBox());
    }

    private Component createTablePanel() {
        // 初始化表格模型
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
        bodyPanel.setBorder(JMeterGuiUtil.createTitledBorder("请选择测试环境"));
        bodyPanel.add(createConfigNameLabel(), JMeterGuiUtil.GridBag.mostLeftConstraints);
        bodyPanel.add(createConfigNameComboBox(), JMeterGuiUtil.GridBag.middleConstraints);
        bodyPanel.add(createButton(), JMeterGuiUtil.GridBag.mostRightConstraints);
        bodyPanel.add(createTablePanel(), JMeterGuiUtil.GridBag.fillBottomConstraints);
        return bodyPanel;
    }

    private Component createNoteArea() {
        return JMeterGuiUtil.createNoteArea(NOTE, this.getBackground());
    }

    private Component createButton() {
        JButton button = new JButton("OPEN");
        button.setActionCommand(OPEN_ACTION);
        button.addActionListener(this);
        return button;
    }

    /**
     * 初始化表格模型
     */
    private void initializeTableModel() {
        tableModel = new ObjectTableModel(new String[]{"配置名称", "配置值"},
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
     * @param dirPath 配置目录
     */
    private ArrayList<File> getConfigList(String dirPath) {
        ArrayList<File> fileList = new ArrayList<>();
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    fileList.addAll(getConfigList(file.getAbsolutePath()));
                } else if (file.getName().endsWith(YamlUtil.YAML_SUFFIX)) {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }

    /**
     * 根据配置文件名称获取文件路径
     */
    private String getConfigPath(String configName) {
        return configDirectory + File.separator + configName;
    }
}
