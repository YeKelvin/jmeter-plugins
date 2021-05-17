package org.apache.jmeter.config.gui;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.common.jmeter.JMeterGuiUtil;
import org.apache.jmeter.common.utils.DesktopUtil;
import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.common.utils.YamlUtil;
import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.HTTPHeaderReader;
import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
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
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP请求头文件读取器
 *
 * @author Kaiwen.Ye
 */
public class HTTPHeaderReaderGui extends AbstractConfigGui implements ActionListener {

    private static final Logger log = LoggerFactory.getLogger(HTTPHeaderReaderGui.class);

    /**
     * Action命令
     */
    private static final String OPEN_ACTION = "OPEN";

    /**
     * swing组件
     */
    private final JComboBox<String> headerFileNameComboBox;
    private final JLabel headerFileNameLabel;

    private final ObjectTableModel tableModel;
    private final JTable table;
    private final JPanel tablePanel;

    /**
     * Header目录路径
     */
    private final String headerDirectory;

    /**
     * 静态缓存
     */
    public static final Map<String, Long> CACHED_HEADER_FILE_LAST_MODIFIED = new HashMap<>();
    public static final Map<String, Map<String, String>> CACHED_HEADER_VARIABLES = new HashMap<>();

    /**
     * 插件说明
     */
    private static final String NOTE = "HTTP请求头文件为yaml格式，目前仅支持放置在 ${JMETER_HOME}/header 目录下";

    public HTTPHeaderReaderGui() {
        headerDirectory = JMeterUtils.getJMeterHome() + File.separator + "header";

        headerFileNameComboBox = createHeadersFileNameComboBox();
        headerFileNameLabel = createHeadersFileNameLabel();

        tableModel = createTableModel();
        table = createTable();
        tablePanel = createTablePanel();

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
        return "HTTP请求头读取器";
    }


    @Override
    public String getLabelResource() {
        return null;
    }


    @Override
    public TestElement createTestElement() {
        HTTPHeaderReader httpHeaderReader = new HTTPHeaderReader();
        modifyTestElement(httpHeaderReader);
        return httpHeaderReader;
    }

    /**
     * GUI -> TestElement
     */
    @Override
    public void modifyTestElement(TestElement el) {
        super.configureTestElement(el);
        el.setProperty(HTTPHeaderReader.HEADER_FILE_NAME, (String) headerFileNameComboBox.getSelectedItem());
    }

    /**
     * TestElement -> GUI
     */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        String fileName = el.getPropertyAsString(HTTPHeaderReader.HEADER_FILE_NAME);
        headerFileNameComboBox.setSelectedItem(fileName);
        configureTable(el);
    }

    @Override
    public void clearGui() {
        super.clearGui();
        headerFileNameComboBox.setSelectedItem("");
        GuiUtils.stopTableEditing(table);
        tableModel.clearData();
    }

    /**
     * 打开http头部文件或http头部文件目录
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals(OPEN_ACTION)) {
            openDirectoryOrHeader();
        }
    }

    private void configureTable(TestElement el) {
        tableModel.clearData();

        if (!(el instanceof HTTPHeaderReader)) {
            return;
        }

        HTTPHeaderReader httpHeaderReader = (HTTPHeaderReader) el;
        String headerFilePath = httpHeaderReader.getHeaderFilePath();
        File file = new File(headerFilePath);
        if (!file.exists() || !file.isFile() || !headerFilePath.endsWith(YamlUtil.YAML_SUFFIX)) {
            log.debug("配置文件不存在或非.yaml文件，headerFilePath:[ {} ]", headerFilePath);
            return;
        }

        getHeaderVariables(file).forEach((key, value) -> tableModel.addRow(new Argument(key, value)));
    }

    private synchronized Map<String, String> getHeaderVariables(File file) {
        String headerPath = file.getPath();
        long configLastModified = file.lastModified();

        // 获取静态缓存
        Map<String, String> cachedHeaderVariables = CACHED_HEADER_VARIABLES.get(headerPath);
        long cachedHeaderLastModified = CACHED_HEADER_FILE_LAST_MODIFIED.getOrDefault(headerPath, (long) 0);

        // 如果缓存为空或配置文件有修改，则重新读取文件
        if (cachedHeaderVariables == null || cachedHeaderLastModified < configLastModified) {
            log.info("配置数据为空或配置文件有更新，重新缓存");

            log.debug("缓存headerVariables");
            log.debug("缓存headerLastModified");
            CACHED_HEADER_VARIABLES.put(headerPath, loadYaml(file));
            CACHED_HEADER_FILE_LAST_MODIFIED.put(headerPath, configLastModified);
        }

        return CACHED_HEADER_VARIABLES.get(headerPath);
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

    private void openDirectoryOrHeader() {
        String headersFileName = String.valueOf(headerFileNameComboBox.getSelectedItem());
        String openPath;
        if (StringUtils.isNotBlank(headersFileName)) {
            openPath = getHeaderFilePath(headersFileName);
        } else {
            openPath = headerDirectory;
        }
        DesktopUtil.openFile(openPath);
    }

    private JComboBox<String> createHeadersFileNameComboBox() {
        JComboBox<String> comboBox = JMeterGuiUtil.createComboBox(HTTPHeaderReader.HEADER_FILE_NAME);
        comboBox.addItem("");
        for (File file : getHeaderFileList(headerDirectory)) {
            comboBox.addItem(file.getName());
        }
        return comboBox;
    }

    private JLabel createHeadersFileNameLabel() {
        return JMeterGuiUtil.createLabel("HTTP请求头文件名称：", headerFileNameComboBox);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(makeScrollPane(table), BorderLayout.CENTER);
        panel.add(Box.createVerticalStrut(70), BorderLayout.WEST);
        return panel;
    }

    private Component createBodyPanel() {
        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBorder(JMeterGuiUtil.createTitledBorder("通过文件配置请求头"));

        bodyPanel.add(headerFileNameLabel, JMeterGuiUtil.GridBag.mostLeftConstraints);
        bodyPanel.add(headerFileNameComboBox, JMeterGuiUtil.GridBag.middleConstraints);
        bodyPanel.add(createButton(), JMeterGuiUtil.GridBag.mostRightConstraints);

        bodyPanel.add(tablePanel, JMeterGuiUtil.GridBag.fillBottomConstraints);

        return bodyPanel;
    }

    private Component createNoteArea() {
        return JMeterGuiUtil.createNoteArea(NOTE, this.getBackground());
    }

    private Component createButton() {
        JButton button = new JButton(OPEN_ACTION);
        button.setActionCommand(OPEN_ACTION);
        button.addActionListener(this);

        return button;
    }

    /**
     * 初始化表格模型
     */
    private ObjectTableModel createTableModel() {
        return new ObjectTableModel(new String[]{"HeaderName", "HeaderValue"},
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
     * 获取httpHeader文件的列表
     *
     * @param dirPath 配置文件所在目录
     */
    private ArrayList<File> getHeaderFileList(String dirPath) {
        ArrayList<File> fileList = new ArrayList<>();
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null) {
            return fileList;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                fileList.addAll(getHeaderFileList(file.getAbsolutePath()));
            } else if (file.getName().endsWith(YamlUtil.YAML_SUFFIX)) {
                fileList.add(file);
            }
        }

        return fileList;
    }

    /**
     * 根据httpHeader文件名称获取文件路径
     */
    private String getHeaderFilePath(String headerFileName) {
        return headerDirectory + File.separator + headerFileName;
    }
}
