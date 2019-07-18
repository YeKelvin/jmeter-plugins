package pers.kelvin.util;

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

    public static JLabel createTextFieldLabel(String text, int width, int height) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(width, height));
        return label;
    }

    public static JLabel createTextFieldLabel(String text, Component labelFor, int width, int height) {
        JLabel label = new JLabel(text);
        label.setLabelFor(labelFor);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setVerticalAlignment(SwingConstants.CENTER);
//        label.setPreferredSize(new Dimension(width, height));
        return label;
    }


    public static JLabel createTextFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setVerticalAlignment(SwingConstants.CENTER);
//        label.setHorizontalAlignment(SwingConstants.TRAILING);
        return label;
    }

    public static JLabel createTextFieldLabel(String text, Component labelFor) {
        JLabel label = new JLabel(text);
        label.setLabelFor(labelFor);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setVerticalAlignment(SwingConstants.CENTER);
        return label;
    }

    public static JLabel createTextAreaLabel(String text, int width, int height) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setVerticalAlignment(SwingConstants.TOP);
        label.setPreferredSize(new Dimension(width, height));
        return label;
    }

    public static JLabel createTextAreaLabel(String text, Component labelFor, int width, int height) {
        JLabel label = new JLabel(text);
        label.setLabelFor(labelFor);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setVerticalAlignment(SwingConstants.TOP);
        label.setPreferredSize(new Dimension(width, height));
        return label;
    }

    public static JLabel createTextAreaLabel(String text) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setVerticalAlignment(SwingConstants.TOP);
        return label;
    }

    public static JLabel createTextAreaLabel(String text, Component labelFor) {
        JLabel label = new JLabel(text);
        label.setLabelFor(labelFor);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setVerticalAlignment(SwingConstants.TOP);
        return label;
    }

    public static JPanel createNotePanel(String note, Color bg, int width, int height) {
        JTextArea textArea = new JTextArea(note);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setBackground(bg);

        JPanel panel = new JPanel(new BorderLayout(width, height));
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
//        public static GridBagConstraints multiLineLabelConstraints; // for labels
//        public static GridBagConstraints multiLineEditorConstraints;  // for multi line editors

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
            editorConstraints.weightx = 1.0;
            editorConstraints.insets = new Insets(1, 1, 1, 1);

//            multiLineLabelConstraints = new GridBagConstraints();
//            multiLineLabelConstraints.gridx = 0;
//            multiLineLabelConstraints.anchor = GridBagConstraints.CENTER;
//            multiLineLabelConstraints.insets = new Insets(1, 1, 1, 1);
//
//            multiLineEditorConstraints = new GridBagConstraints();
//            multiLineEditorConstraints.fill = GridBagConstraints.BOTH;
//            multiLineEditorConstraints.gridx = 0;
//            multiLineEditorConstraints.gridy = GridBagConstraints.RELATIVE;
//            multiLineEditorConstraints.gridwidth = 2;
//            multiLineEditorConstraints.weightx = 1.0;
//            multiLineEditorConstraints.insets = new Insets(1, 1, 1, 1);

        }

        public static GridBagConstraints createLabelConstraints() {
            GridBagConstraints labelConstraints = new GridBagConstraints();
            labelConstraints.gridx = 0;
            labelConstraints.anchor = GridBagConstraints.EAST;
            labelConstraints.insets = new Insets(1, 1, 1, 1);
            return labelConstraints;
        }

        public static GridBagConstraints createEditorConstraints() {
            GridBagConstraints editorConstraints = new GridBagConstraints();
            editorConstraints.fill = GridBagConstraints.BOTH;
            editorConstraints.gridx = 1;
            editorConstraints.gridy = GridBagConstraints.RELATIVE;
            editorConstraints.gridwidth = 2;
            editorConstraints.weightx = 1.0;
            editorConstraints.insets = new Insets(1, 1, 1, 1);
            return editorConstraints;
        }

        public static GridBagConstraints createMultiLineLabelConstraints() {
            GridBagConstraints multiLineLabelConstraints = new GridBagConstraints();
            multiLineLabelConstraints.gridx = 0;
            multiLineLabelConstraints.anchor = GridBagConstraints.CENTER;
            multiLineLabelConstraints.insets = new Insets(1, 1, 1, 1);
            return multiLineLabelConstraints;
        }

        public static GridBagConstraints createMultiLineEditorConstraints() {
            GridBagConstraints multiLineEditorConstraints = new GridBagConstraints();
            multiLineEditorConstraints.fill = GridBagConstraints.BOTH;
            multiLineEditorConstraints.gridx = 0;
            multiLineEditorConstraints.gridy = GridBagConstraints.RELATIVE;
            multiLineEditorConstraints.gridwidth = 2;
            multiLineEditorConstraints.weightx = 1.0;
            multiLineEditorConstraints.insets = new Insets(1, 1, 1, 1);
            return multiLineEditorConstraints;
        }
    }
}
