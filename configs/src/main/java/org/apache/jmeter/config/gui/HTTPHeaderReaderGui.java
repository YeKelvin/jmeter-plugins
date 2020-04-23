package org.apache.jmeter.config.gui;

import org.apache.jmeter.common.utils.GuiUtil;
import org.apache.jmeter.common.utils.StringUtil;
import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.HTTPHeaderReader;
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
 * 未完成不可用
 */
public class HTTPHeaderReaderGui extends AbstractConfigGui {

    private JComboBox<String> commonHeadersFileNameComboBox;
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

        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setBorder(GuiUtil.createTitledBorder("通过文件配置请求头"));
        bodyPanel.add(getHTTPFileNameLabel(), GuiUtil.GridBag.labelConstraints);
        bodyPanel.add(getHTTPFileNameComboBox(), GuiUtil.GridBag.editorConstraints);
        bodyPanel.add(createTablePanel(), GuiUtil.GridBag.fillBottomConstraints);

        add(bodyPanel, BorderLayout.CENTER);
        add(getNotePanel(), BorderLayout.SOUTH);
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
        el.setProperty(HTTPHeaderReader.FILE_NAME, (String) headersFileNameComboBox.getSelectedItem());
    }

    /**
     * 将数据设置到GUI元素中
     */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        String fileName = el.getPropertyAsString(HTTPHeaderReader.FILE_NAME);
        headersFileNameComboBox.setSelectedItem(fileName);
        tableModel.clearData();
        if (el instanceof HTTPHeaderReader && StringUtil.isNotBlank(fileName)) {
            HTTPHeaderReader httpHeaderReader = (HTTPHeaderReader) el;
            Map<String, String> headerMap = httpHeaderReader.getHeaderMap(httpHeaderReader.getFilePath());
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

    private Component getHTTPFileNameComboBox() {
        if (headersFileNameComboBox == null) {
            headersFileNameComboBox = GuiUtil.createComboBox(HTTPHeaderReader.FILE_NAME);
            comboBoxAddItem(getHTTPHeaderFileList(getHTTPHeaderDirectoryPath()));
        }
        return headersFileNameComboBox;
    }

    private Component getHTTPFileNameLabel() {
        return GuiUtil.createLabel("HTTP请求头文件名称：", getHTTPFileNameComboBox());
    }

    private JPanel createTablePanel() {
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
        JMeterUtils.applyHiDPI(table);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(makeScrollPane(table), BorderLayout.CENTER);
        panel.add(Box.createVerticalStrut(70), BorderLayout.WEST);
        return panel;
    }

    private JPanel getNotePanel() {
        String note = "说明：HTTP请求头文件必须是 Yaml格式，文件后缀为 .http.header，且必须放在 ${JMETER_HOME}/header 目录下。";
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
        headersFileNameComboBox.addItem("");
        for (File file : fileList) {
            headersFileNameComboBox.addItem(file.getName());
        }
    }

    /**
     * 获取配置文件的列表
     *
     * @param dirPath 配置文件所在目录
     */
    private ArrayList<File> getHTTPHeaderFileList(String dirPath) {
        ArrayList<File> fileList = new ArrayList<>();
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    fileList.addAll(getHTTPHeaderFileList(file.getAbsolutePath()));
                } else if (file.getName().endsWith("yaml")) {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }

    private String getHTTPHeaderDirectoryPath() {
        return JMeterUtils.getJMeterHome() + File.separator + "header";
    }
}
