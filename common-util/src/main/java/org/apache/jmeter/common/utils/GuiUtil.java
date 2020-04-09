package org.apache.jmeter.common.utils;

import org.apache.jmeter.gui.util.JSyntaxTextArea;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Description
 *
 * @author KelvinYe
 * Date     2019-03-05
 * Time     14:11
 */
public class GuiUtil {

    public static TitledBorder createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title);
    }

    public static JPanel createBlankPanel() {
        return new JPanel(new BorderLayout());
    }

    public static JLabel createLabel(String text, Component labelFor) {
        JLabel label = new JLabel(text);
        label.setLabelFor(labelFor);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        return label;
    }

    public static JTextField createTextField(String name) {
        JTextField textField = new JTextField(10);
        textField.setName(name);
        return textField;
    }

    public static JSyntaxTextArea createTextArea(String name, int rows) {
        JSyntaxTextArea textArea = JSyntaxTextArea.getInstance(rows, 20);
        textArea.setName(name);
        textArea.setLineWrap(true);
        return textArea;
    }

    public static JComboBox<String> createComboBox(String name) {
        JComboBox<String> comboBox=new JComboBox<>();
        comboBox.setName(name);
        return comboBox;
    }

    public static JPanel createNotePanel(String note, Color bg) {
        JTextArea textArea = new JTextArea(note);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setBackground(bg);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(textArea, BorderLayout.CENTER);
        return panel;
    }

    public static class GridBag {
        /**
         * GridBagConstraints
         *
         *        0   1   2   3   4   5       X
         *      ┏━━━┳━━━┳━━━┳━━━┳━━━┳━━━┳━━━━━━→
         *    0 ┃   │   │   │   │   │   │
         *      ┣───┼───┼───┼───┼───┼───┼──
         *    1 ┃   │   │   │   │   │   │
         *      ┣───┼───┼───┼───┼───┼───┼──
         *    2 ┃   │   │   │   │   │   │
         *      ┣───┼───┼───┼───┼───┼───┼──
         *    3 ┃   │   │   │   │   │   │
         *      ┣───┼───┼───┼───┼───┼───┼──
         *    4 ┃   │   │   │   │   │   │
         *      ┣───┼───┼───┼───┼───┼───┼──
         *      ┃
         *    Y ┃
         *      ↓
         *
         * fill：填充
         *      - NONE          不进行尺寸处理 默认居中
         *      - BOTH          水平和竖直均拉伸到充满
         *      - HORIZONTAL    水平方向拉伸充满
         *      - VERTICAL      竖直方向拉伸充满
         *
         * anchor：代表在单元格中的绝对值对齐方式
         *      - CENTER        居中
         *      - NORTH         布局在上方
         *      - NORTHEAST     布局在右上方
         *      - EAST          布局在右方
         *      - SOUTHEAST     布局在右下方
         *      - SOUTH         布局在下方
         *      - SOUTHWEST     布局在左下方
         *      - WEST          布局在左方
         *      - NORTHWEST     布局在左上方
         *
         * gridwidth与gridheight：确定组件在x轴（y轴）所占的单元格数
         *      - RELATIVE      占据其他组件布局后余下的尺寸
         *      - REMAINDER     占据此行或者此列的剩下全部，后置的组件另起一行或一列
         *
         * gridx与gridy：确定组件在当前坐标系的位置（x,y）
         *
         * weightx与weighty：权重（默认权重是把多的空间放在容器边框和单元格边框之间）；权重值越大，分到空间（组件到它所占网格的距离空间）越多
         *
         * ipadx与ipady：内边距
         *
         * insets：外边距
         */
        public static GridBagConstraints labelConstraints; // for labels
        public static GridBagConstraints editorConstraints; // for editors
        public static GridBagConstraints multiLineEditorConstraints;  // for multi line editors
        public static GridBagConstraints fillBottomConstraints;  // for fill the bottom editors

        static {
            labelConstraints = new GridBagConstraints();
            labelConstraints.gridx = 0;
            labelConstraints.anchor = GridBagConstraints.EAST;
            labelConstraints.insets = new Insets(1, 1, 1, 1);

            editorConstraints = new GridBagConstraints();
            editorConstraints.fill = GridBagConstraints.BOTH;
            editorConstraints.gridx = 1;
            editorConstraints.gridy = GridBagConstraints.RELATIVE;
            editorConstraints.gridwidth = 2;
            editorConstraints.weightx = 1;
            editorConstraints.insets = new Insets(1, 1, 1, 1);

            multiLineEditorConstraints = new GridBagConstraints();
            multiLineEditorConstraints.fill = GridBagConstraints.BOTH;
            multiLineEditorConstraints.gridx = 0;
            multiLineEditorConstraints.gridy = GridBagConstraints.RELATIVE;
            multiLineEditorConstraints.gridwidth = 3;
            multiLineEditorConstraints.weightx = 1;
            multiLineEditorConstraints.insets = new Insets(1, 1, 1, 1);

            fillBottomConstraints = new GridBagConstraints();
            fillBottomConstraints.fill = GridBagConstraints.BOTH;
            fillBottomConstraints.gridx = 0;
            fillBottomConstraints.gridy = GridBagConstraints.RELATIVE;
            fillBottomConstraints.gridwidth = 3;
            fillBottomConstraints.gridheight = GridBagConstraints.RELATIVE;
            fillBottomConstraints.weightx = 1;
            fillBottomConstraints.weighty = 1;
            fillBottomConstraints.insets = new Insets(1, 1, 1, 1);
        }
    }
}
