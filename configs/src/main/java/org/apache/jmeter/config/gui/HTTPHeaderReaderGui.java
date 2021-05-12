package org.apache.jmeter.config.gui;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.common.utils.GuiUtil;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * HTTP请求头文件读取器
 *
 * @author Kaiwen.Ye
 */
public class HTTPHeaderReaderGui extends AbstractConfigGui implements ActionListener {

    private static final Logger log = LoggerFactory.getLogger(HTTPHeaderReaderGui.class);

    private static final String NOTE = "HTTP请求头文件为yaml格式，目前仅支持放置在 ${JMETER_HOME}/header 目录下";

    private static final String OPEN_ACTION = "OPEN";

    private JComboBox<String> headersFileNameComboBox;
    private JTable table;
    private ObjectTableModel tableModel;

    public HTTPHeaderReaderGui() {
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
     * 将数据从GUI元素移动到TestElement
     */
    @Override
    public void modifyTestElement(TestElement el) {
        super.configureTestElement(el);
        el.setProperty(HTTPHeaderReader.HEADERS_FILE_NAME, (String) headersFileNameComboBox.getSelectedItem());
    }

    /**
     * 将数据设置到GUI元素中
     */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        String fileName = el.getPropertyAsString(HTTPHeaderReader.HEADERS_FILE_NAME);
        headersFileNameComboBox.setSelectedItem(fileName);
        tableModel.clearData();
        if (el instanceof HTTPHeaderReader && StringUtils.isNotBlank(fileName)) {
            HTTPHeaderReader httpHeaderReader = (HTTPHeaderReader) el;
            Map<String, String> headerMap = httpHeaderReader.getHeaderMap(httpHeaderReader.getHeadersFilePath());
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                Argument arg = new Argument(entry.getKey(), entry.getValue());
                tableModel.addRow(arg);
            }
        }
    }

    @Override
    public void clearGui() {
        super.clearGui();
        headersFileNameComboBox.setSelectedItem("");
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
            try {
                String headersFileName = String.valueOf(headersFileNameComboBox.getSelectedItem());
                String openFilePath;
                if (StringUtils.isNotBlank(headersFileName)) {
                    openFilePath = getHeadersFilePath(headersFileName);
                } else {
                    openFilePath = getHeadersFileDirectory();
                }
                Desktop.getDesktop().open(new File(openFilePath));
            } catch (IOException ioException) {
                log.error(ExceptionUtil.getStackTrace(ioException));
            }
        }
    }

    private Component createHeadersFileNameComboBox() {
        if (headersFileNameComboBox == null) {
            headersFileNameComboBox = GuiUtil.createComboBox(HTTPHeaderReader.HEADERS_FILE_NAME);
            comboBoxAddItem(getHeadersFileList(getHeadersFileDirectory()));
        }
        return headersFileNameComboBox;
    }

    private Component createHeadersFileNameLabel() {
        return GuiUtil.createLabel("HTTP请求头文件名称：", createHeadersFileNameComboBox());
    }

    private JPanel createTablePanel() {
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
        bodyPanel.setBorder(GuiUtil.createTitledBorder("通过文件配置请求头"));
        bodyPanel.add(createHeadersFileNameLabel(), GuiUtil.GridBag.mostLeftConstraints);
        bodyPanel.add(createHeadersFileNameComboBox(), GuiUtil.GridBag.middleConstraints);
        bodyPanel.add(createButton(), GuiUtil.GridBag.mostRightConstraints);
        bodyPanel.add(createTablePanel(), GuiUtil.GridBag.fillBottomConstraints);
        return bodyPanel;
    }

    private Component createNoteArea() {
        return GuiUtil.createNoteArea(NOTE, this.getBackground());
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
        headersFileNameComboBox.addItem("");
        for (File file : fileList) {
            headersFileNameComboBox.addItem(file.getName());
        }
    }

    /**
     * 获取httpHeader文件的列表
     *
     * @param dirPath 配置文件所在目录
     */
    private ArrayList<File> getHeadersFileList(String dirPath) {
        ArrayList<File> fileList = new ArrayList<>();
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    fileList.addAll(getHeadersFileList(file.getAbsolutePath()));
                } else if (file.getName().endsWith("yaml")) {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }

    /**
     * 获取httpHeader文件目录路径
     */
    private String getHeadersFileDirectory() {
        return JMeterUtils.getJMeterHome() + File.separator + "header";
    }

    /**
     * 根据httpHeader文件名称获取文件路径
     */
    private String getHeadersFilePath(String headersFileName) {
        return getHeadersFileDirectory() + File.separator + headersFileName;
    }
}
