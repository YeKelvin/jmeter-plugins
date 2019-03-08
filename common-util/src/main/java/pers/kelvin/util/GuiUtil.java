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
        label.setPreferredSize(new Dimension(width, height));
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

    public static JPanel createNotePanel(String note, Color bg, int width, int height) {
        JTextArea textArea = new JTextArea(note);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setBackground(bg);

        JPanel panel = new JPanel(new BorderLayout(width, height));
        panel.add(textArea, BorderLayout.CENTER);
        return panel;
    }
}
