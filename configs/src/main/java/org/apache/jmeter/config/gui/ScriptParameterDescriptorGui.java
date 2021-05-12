package org.apache.jmeter.config.gui;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.common.utils.GuiUtil;
import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.ScriptParameterDescriptor;
import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.GuiUtils;
import org.apache.jorphan.gui.ObjectTableModel;
import org.apache.jorphan.reflect.Functor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Iterator;

/**
 * Description:
 *
 * @author: KelvinYe
 * Date: 2021-05-13
 * Time: 00:20
 */
public class ScriptParameterDescriptorGui extends AbstractConfigGui implements ActionListener {

    private static final Logger log = LoggerFactory.getLogger(EnvDataSetGui.class);

    private static final String NOTE = "";

    private JLabel tableLabel;
    private transient JTable table;
    private transient ObjectTableModel tableModel;

    private JButton add;
    private JButton delete;

    private JButton up;
    private JButton down;

    private static final String ADD = "add";
    private static final String ADD_FROM_CLIPBOARD = "addFromClipboard";
    private static final String DELETE = "delete";
    private static final String UP = "up";
    private static final String DOWN = "down";
    private static final String CLIPBOARD_LINE_DELIMITERS = "\n";
    private static final String CLIPBOARD_ARG_DELIMITERS = "\t";

    public static final String COLUMN_RESOURCE_NAMES_0 = "name";
    public static final String COLUMN_RESOURCE_NAMES_1 = "value";
    public static final String COLUMN_RESOURCE_NAMES_2 = "description";

    public ScriptParameterDescriptorGui() {
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
        return "脚本参数描述器";
    }


    @Override
    public String getLabelResource() {
        return null;
    }


    @Override
    public TestElement createTestElement() {
        ScriptParameterDescriptor args = new ScriptParameterDescriptor();
        modifyTestElement(args);
        return args;
    }

    @Override
    public void modifyTestElement(TestElement el) {
        GuiUtils.stopTableEditing(table);
        if (el instanceof ScriptParameterDescriptor) {
            ScriptParameterDescriptor args = (ScriptParameterDescriptor) el;
            args.clear();
            @SuppressWarnings("unchecked")
            Iterator<Argument> modelData = (Iterator<Argument>) tableModel.iterator();
            while (modelData.hasNext()) {
                Argument arg = modelData.next();
                if (StringUtils.isEmpty(arg.getName()) && StringUtils.isEmpty(arg.getValue())) {
                    continue;
                }
                arg.setMetaData("=");
                args.addArgument(arg);
            }
        }
        super.configureTestElement(el);
    }

    @Override
    public void configure(TestElement el) {
        super.configure(el);
        if (el instanceof Arguments) {
            tableModel.clearData();
            for (JMeterProperty jMeterProperty : (Arguments) el) {
                Argument arg = (Argument) jMeterProperty.getObjectValue();
                tableModel.addRow(arg);
            }
        }
    }

    @Override
    public void clearGui() {
        super.clearGui();
        GuiUtils.stopTableEditing(table);
        tableModel.clearData();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals(DELETE)) {
            deleteArgument();
        } else if (action.equals(ADD)) {
            addArgument();
        } else if (action.equals(ADD_FROM_CLIPBOARD)) {
            addFromClipboard();
        } else if (action.equals(UP)) {
            moveUp();
        } else if (action.equals(DOWN)) {
            moveDown();
        }
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

    private Component createBodyPanel() {
        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.add(makeLabelPanel(), BorderLayout.NORTH);
        bodyPanel.add(makeTablePanel(), BorderLayout.CENTER);
        bodyPanel.add(Box.createVerticalStrut(70), BorderLayout.WEST);
        bodyPanel.add(makeButtonPanel(), BorderLayout.SOUTH);

        return bodyPanel;
    }

    private Component makeLabelPanel() {
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        labelPanel.add(tableLabel);
        return labelPanel;
    }

    private Component makeTablePanel() {
        initializeTableModel();
        table = new JTable(tableModel);
        table.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JMeterUtils.applyHiDPI(table);
        return makeScrollPane(table);
    }

    private JPanel makeButtonPanel() {
        add = new JButton("添加");
        add.setActionCommand(ADD);
        add.setEnabled(true);

        JButton addFromClipboard = new JButton("从剪贴板添加");
        addFromClipboard.setActionCommand(ADD_FROM_CLIPBOARD);
        addFromClipboard.setEnabled(true);

        delete = new JButton("删除");
        delete.setActionCommand(DELETE);

        up = new JButton("上移");
        up.setActionCommand(UP);

        down = new JButton("下移");
        down.setActionCommand(DOWN);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        add.addActionListener(this);
        addFromClipboard.addActionListener(this);
        delete.addActionListener(this);
        buttonPanel.add(add);
        buttonPanel.add(addFromClipboard);
        buttonPanel.add(delete);
        up.addActionListener(this);
        down.addActionListener(this);
        buttonPanel.add(up);
        buttonPanel.add(down);

        return buttonPanel;
    }

    private Component createNoteArea() {
        return GuiUtil.createNoteArea(NOTE, this.getBackground());
    }

    private void addArgument() {
        GuiUtils.stopTableEditing(table);

        tableModel.addRow(makeNewArgument());

        int rowToSelect = tableModel.getRowCount() - 1;
        table.setRowSelectionInterval(rowToSelect, rowToSelect);
        table.scrollRectToVisible(table.getCellRect(rowToSelect, 0, true));
    }

    private void addFromClipboard(String lineDelimiter, String argDelimiter) {
        GuiUtils.stopTableEditing(table);
        int rowCount = table.getRowCount();
        try {
            String clipboardContent = GuiUtils.getPastedText();
            if (clipboardContent == null) {
                return;
            }
            String[] clipboardLines = clipboardContent.split(lineDelimiter);
            for (String clipboardLine : clipboardLines) {
                String[] clipboardCols = clipboardLine.split(argDelimiter);
                if (clipboardCols.length > 0) {
                    Argument argument = createArgumentFromClipboard(clipboardCols);
                    tableModel.addRow(argument);
                }
            }
            if (table.getRowCount() > rowCount) {

                // Highlight (select) and scroll to the appropriate rows.
                int rowToSelect = tableModel.getRowCount() - 1;
                table.setRowSelectionInterval(rowCount, rowToSelect);
                table.scrollRectToVisible(table.getCellRect(rowCount, 0, true));
            }
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(this,
                    "Could not add read arguments from clipboard:\n" + ioe.getLocalizedMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (UnsupportedFlavorException ufe) {
            JOptionPane.showMessageDialog(this,
                    "Could not add retrieve " + DataFlavor.stringFlavor.getHumanPresentableName()
                            + " from clipboard" + ufe.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addFromClipboard() {
        addFromClipboard(CLIPBOARD_LINE_DELIMITERS, CLIPBOARD_ARG_DELIMITERS);
    }

    private Argument createArgumentFromClipboard(String[] clipboardCols) {
        Argument argument = makeNewArgument();
        argument.setName(clipboardCols[0]);
        if (clipboardCols.length > 1) {
            argument.setValue(clipboardCols[1]);
            if (clipboardCols.length > 2) {
                argument.setDescription(clipboardCols[2]);
            }
        }
        return argument;
    }

    private Argument makeNewArgument() {
        return new Argument("", "");
    }

    private void deleteArgument() {
        GuiUtils.cancelEditing(table);

        int[] rowsSelected = table.getSelectedRows();
        int anchorSelection = table.getSelectionModel().getAnchorSelectionIndex();
        table.clearSelection();
        if (rowsSelected.length > 0) {
            for (int i = rowsSelected.length - 1; i >= 0; i--) {
                tableModel.removeRow(rowsSelected[i]);
            }

            if (tableModel.getRowCount() > 0) {
                if (anchorSelection >= tableModel.getRowCount()) {
                    anchorSelection = tableModel.getRowCount() - 1;
                }
                table.setRowSelectionInterval(anchorSelection, anchorSelection);
            }
        }
    }

    private void moveUp() {
        int[] rowsSelected = table.getSelectedRows();
        GuiUtils.stopTableEditing(table);

        if (rowsSelected.length > 0 && rowsSelected[0] > 0) {
            table.clearSelection();
            for (int rowSelected : rowsSelected) {
                tableModel.moveRow(rowSelected, rowSelected + 1, rowSelected - 1);
            }

            for (int rowSelected : rowsSelected) {
                table.addRowSelectionInterval(rowSelected - 1, rowSelected - 1);
            }

            scrollToRowIfNotVisible(rowsSelected[0] - 1);
        }
    }

    private void scrollToRowIfNotVisible(int rowIndx) {
        if (table.getParent() instanceof JViewport) {
            Rectangle visibleRect = table.getVisibleRect();
            final int cellIndex = 0;
            Rectangle cellRect = table.getCellRect(rowIndx, cellIndex, false);
            if (visibleRect.y > cellRect.y) {
                table.scrollRectToVisible(cellRect);
            } else {
                Rectangle rect2 = table.getCellRect(rowIndx + getNumberOfVisibleRows(table), cellIndex, true);
                int width = rect2.y - cellRect.y;
                table.scrollRectToVisible(new Rectangle(cellRect.x, cellRect.y, cellRect.width, cellRect.height + width));
            }
        }
    }

    private static int getNumberOfVisibleRows(JTable table) {
        Rectangle vr = table.getVisibleRect();
        int first = table.rowAtPoint(vr.getLocation());
        vr.translate(0, vr.height);
        return table.rowAtPoint(vr.getLocation()) - first;
    }

    private void moveDown() {
        int[] rowsSelected = table.getSelectedRows();
        GuiUtils.stopTableEditing(table);

        if (rowsSelected.length > 0 && rowsSelected[rowsSelected.length - 1] < table.getRowCount() - 1) {
            table.clearSelection();
            for (int i = rowsSelected.length - 1; i >= 0; i--) {
                int rowSelected = rowsSelected[i];
                tableModel.moveRow(rowSelected, rowSelected + 1, rowSelected + 1);
            }
            for (int rowSelected : rowsSelected) {
                table.addRowSelectionInterval(rowSelected + 1, rowSelected + 1);
            }

            scrollToRowIfNotVisible(rowsSelected[0] + 1);
        }
    }
}
