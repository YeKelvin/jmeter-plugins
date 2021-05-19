package org.apache.jmeter.config.gui;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.common.jmeter.JMeterGuiUtil;
import org.apache.jmeter.common.utils.DesktopUtil;
import org.apache.jmeter.common.utils.ExceptionUtil;
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
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kelvin.Ye
 */
public class EnvDataSetGui extends AbstractConfigGui implements ActionListener {

    private static final Logger log = LoggerFactory.getLogger(EnvDataSetGui.class);

    /**
     * Action命令
     */
    private static final String OPEN_ACTION = "OPEN";

    /**
     * swing组件
     */
    private final JComboBox<String> configNameComboBox;
    private final JLabel configNameLabel;

    private final ObjectTableModel tableModel;
    private final JTable table;
    private final JPanel tablePanel;

    /**
     * 配置目录路径
     */
    private final String configDirectory;

    /**
     * 静态缓存
     */
    public static final Map<String, String> CACHED_SELECTED_CONFIG_PATH = Collections.synchronizedMap(new HashMap<>());
    public static final Map<String, Long> CACHED_CONFIG_LAST_MODIFIED = Collections.synchronizedMap(new HashMap<>());
    public static final Map<String, Map<String, String>> CACHED_CONFIG_VARIABLES = Collections.synchronizedMap(new HashMap<>());

    /**
     * 插件说明
     */
    private static final String NOTE =
            "1、配置文件为yaml格式 ，目前仅支持放置在 ${JMETER_HOME}/config 目录下\n" +
                    "2、Non-Gui命令说明：存在 -JconfigName 选项时，优先读取 ${__P(configName)} 配置文件";

    public EnvDataSetGui() {
        log.debug("start new EnvDataSetGui");
        configDirectory = JMeterUtils.getJMeterHome() + File.separator + "config";

        configNameComboBox = createConfigNameComboBox();
        configNameLabel = createConfigNameLabel();

        tableModel = createTableModel();
        table = createTable();
        tablePanel = createTablePanel();

        init();
    }

    private void init() {
        log.debug("start init");
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
     * GUI -> TestElement
     */
    @Override
    public void modifyTestElement(TestElement el) {
        super.configureTestElement(el);
        String configName = (String) configNameComboBox.getSelectedItem();
        el.setProperty(EnvDataSet.CONFIG_NAME, configName);
        cachedSelectedConfig(configName);
    }

    /**
     * TestElement -> GUI
     */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        configNameComboBox.setSelectedItem(el.getPropertyAsString(EnvDataSet.CONFIG_NAME));
        configureTable(el);
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
            openDirectoryOrConfig();
        }
    }

    private synchronized void cachedSelectedConfig(String configName) {
        String configPath = getConfigPath(configName);
        log.debug("缓存configPath:[ {} ]", configPath);
        CACHED_SELECTED_CONFIG_PATH.put(getScriptName(), configPath);
    }

    private void configureTable(TestElement el) {
        tableModel.clearData();

        if (!(el instanceof EnvDataSet)) {
            return;
        }

        EnvDataSet envDataSet = (EnvDataSet) el;
        String configPath = envDataSet.getConfigPath();
        File file = new File(configPath);
        if (!file.exists() || !file.isFile() || !configPath.endsWith(YamlUtil.YAML_SUFFIX)) {
            log.debug("配置文件不存在或非.yaml文件，configPath:[ {} ]", configPath);
            return;
        }

        getConfigVariables(file).forEach((key, value) -> tableModel.addRow(new Argument(key, value)));
    }

    private synchronized Map<String, String> getConfigVariables(File file) {
        String configPath = file.getPath();
        long configLastModified = file.lastModified();

        // 获取静态缓存
        Map<String, String> cachedConfigVariables = CACHED_CONFIG_VARIABLES.get(configPath);
        long cachedConfigLastModified = CACHED_CONFIG_LAST_MODIFIED.getOrDefault(configPath, (long) 0);
        log.debug("cachedConfigVariables:[ {} ]", cachedConfigVariables);
        log.debug("cachedConfigLastModified:[ {} ]", cachedConfigLastModified);

        // 如果缓存为空或配置文件有修改，则重新读取文件
        if (cachedConfigVariables == null || cachedConfigLastModified < configLastModified) {
            log.info("配置数据为空或配置文件有更新，重新缓存");

            Map<String, String> configVariables = loadYaml(file);
            log.debug("缓存configVariables:[ {} ]", configVariables);
            log.debug("缓存configLastModified:[ {} ]", configLastModified);
            CACHED_CONFIG_VARIABLES.put(configPath, configVariables);
            CACHED_CONFIG_LAST_MODIFIED.put(configPath, configLastModified);
        }

        return CACHED_CONFIG_VARIABLES.get(configPath);
    }

    private Map<String, String> loadYaml(File file) {
        Map<String, String> variables = new HashMap<>();
        try {
            YamlUtil.parseYamlAsMap(file).forEach((key, value) -> variables.put(key, value.toString()));
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            log.error(ExceptionUtil.getStackTrace(e));
        }
        return variables;
    }

    private void openDirectoryOrConfig() {
        String configName = String.valueOf(configNameComboBox.getSelectedItem());
        String openPath;
        if (StringUtils.isNotBlank(configName)) {
            openPath = getConfigPath(configName);
        } else {
            openPath = configDirectory;
        }
        DesktopUtil.openFile(openPath);
    }

    private JComboBox<String> createConfigNameComboBox() {
        JComboBox<String> comboBox = JMeterGuiUtil.createComboBox(EnvDataSet.CONFIG_NAME);
        comboBox.addItem("");
        for (File file : getConfigList(configDirectory)) {
            comboBox.addItem(file.getName());
        }
        return comboBox;
    }

    private JLabel createConfigNameLabel() {
        return JMeterGuiUtil.createLabel("配置文件名称：", configNameComboBox);
    }

    private Component createBodyPanel() {
        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBorder(JMeterGuiUtil.createTitledBorder("请选择测试环境"));

        bodyPanel.add(configNameLabel, JMeterGuiUtil.GridBag.mostLeftConstraints);
        bodyPanel.add(configNameComboBox, JMeterGuiUtil.GridBag.middleConstraints);
        bodyPanel.add(createButton(), JMeterGuiUtil.GridBag.mostRightConstraints);

        bodyPanel.add(tablePanel, JMeterGuiUtil.GridBag.fillBottomConstraints);
        return bodyPanel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(makeScrollPane(table), BorderLayout.CENTER);
        panel.add(Box.createVerticalStrut(70), BorderLayout.WEST);
        return panel;
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
     * 创建表格模型
     */
    private ObjectTableModel createTableModel() {
        return new ObjectTableModel(new String[]{"配置名称", "配置值"},
                Argument.class,
                new Functor[]{
                        new Functor("getName"),
                        new Functor("getValue")},
                new Functor[]{
                        new Functor("setName"),
                        new Functor("setValue")},
                new Class[]{String.class, String.class});
    }

    private JTable createTable() {
        // 列排序
        TableRowSorter<ObjectTableModel> sorter = new TableRowSorter<>(tableModel);
        // 设置只有第一列可以排序，其他均不可以
        sorter.setSortable(0, true);
        sorter.setSortable(1, false);

        JTable table = new JTable(tableModel);
        table.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.revalidate();
        table.setRowSorter(sorter);

        return table;
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
        if (files == null) {
            return fileList;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                fileList.addAll(getConfigList(file.getAbsolutePath()));
            } else if (file.getName().endsWith(YamlUtil.YAML_SUFFIX)) {
                fileList.add(file);
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

    /**
     * 获取当前脚本名称
     */
    private String getScriptName() {
        String scriptName = FileServer.getFileServer().getScriptName();
        log.debug("scriptName:[ {} ]", scriptName);
        return scriptName;
    }
}
