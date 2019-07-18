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
         *        0   1   2   3   4   5
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
         *      ↓
         *
         * fill：组件尺寸小于其被指定的表格尺寸时，组件的拉伸模式
         *      - NONE          不进行尺寸处理 默认居中
         *      - BOTH          水平和竖直均拉伸到充满
         *      - HORIZONTAL    水平方向拉伸充满
         *      - VERTICAL      竖直方向拉伸充满
         *
         * anchor：组件尺寸小于其被指定的表格尺寸时，组件的布局位置
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
         * gridwidth与gridheight：组件的宽度与高度，特殊的值
         *      - RELATIVE      占据其他组件布局后余下的尺寸
         *      - REMAINDER     暂居此行或者此列的剩下全部，后置的组件另起一行或一列
         *
         * gridx与gridy：设置组件布局左上角所在的单元格，单位为单元格，默认会排列在上一个单元格之后
         *
         * weightx与weighty：设置组件布局的水平权重和竖直权重
         *
         * insets：设置组件边距
         */
        public static GridBagConstraints labelConstraints; // for labels
        public static GridBagConstraints editorConstraints; // for editors
        public static GridBagConstraints multiLineLabelConstraints; // for labels
        public static GridBagConstraints multiLineEditorConstraints;  // for multi line editors
        public static GridBagConstraints panelConstraints = new GridBagConstraints(); // for panels

        static {
            labelConstraints = new GridBagConstraints();
            labelConstraints.gridx = 0;
            labelConstraints.anchor = GridBagConstraints.EAST;
            labelConstraints.insets = new Insets(1, 1, 1, 1);

            editorConstraints = new GridBagConstraints();
            editorConstraints.fill = GridBagConstraints.BOTH;
            editorConstraints.gridx = 1;
            editorConstraints.weightx = 1.0;
            editorConstraints.insets = new Insets(1, 1, 1, 1);

            multiLineLabelConstraints = new GridBagConstraints();
            multiLineLabelConstraints.gridx = 0;
            multiLineLabelConstraints.gridy = 0;
            multiLineLabelConstraints.gridwidth = 2;
            multiLineLabelConstraints.anchor = GridBagConstraints.CENTER;
            labelConstraints.insets = new Insets(1, 1, 1, 1);

            multiLineEditorConstraints = new GridBagConstraints();
            multiLineEditorConstraints.gridx = 0;
            multiLineEditorConstraints.gridy = 0;
            multiLineEditorConstraints.gridwidth = 2;
            multiLineEditorConstraints.weighty = 1.0;
            multiLineEditorConstraints.insets = new Insets(1, 1, 1, 1);

            panelConstraints.fill = GridBagConstraints.BOTH;
            panelConstraints.gridx = 1;
            panelConstraints.gridy = GridBagConstraints.RELATIVE;
            panelConstraints.gridwidth = 2;
            panelConstraints.weightx = 1.0;
        }

    }
}
