package org.apache.jmeter.config.gui;

import org.apache.jmeter.config.TraverseEmptyValue;
import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.gui.util.JTextScrollPane;
import org.apache.jmeter.testelement.TestElement;

import javax.swing.*;
import java.awt.*;

/**
 * User: KelvinYe
 * Date: 2018-04-17
 * Time: 11:10
 */
public class TraverseEmptyValueGui extends AbstractConfigGui {
    private JSyntaxTextArea patamsTextArea;
    private JSyntaxTextArea emptyCheckExpectionTextArea;

    public TraverseEmptyValueGui() {
        super();
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());

        Box box = Box.createVerticalBox();
        box.add(makeTitlePanel());
        box.add(createPatamsPanel());
        box.add(createEmptyCheckExpectionPanel());
        add(box, BorderLayout.NORTH);
    }

    @Override
    public String getStaticLabel() {
        return "Traverse Empty Value";
    }


    @Override
    public String getLabelResource() {
        return null;
    }


    @Override
    public TestElement createTestElement() {
        TraverseEmptyValue dataSet = new TraverseEmptyValue();
        modifyTestElement(dataSet);
        return dataSet;
    }

    /**
     * 将数据从GUI元素移动到TestElement
     */
    @Override
    public void modifyTestElement(TestElement el) {
        super.configureTestElement(el);
        el.setProperty(TraverseEmptyValue.PATAMS, patamsTextArea.getText());
        el.setProperty(TraverseEmptyValue.EMPTY_CHECK_EXPECTION, emptyCheckExpectionTextArea.getText());
    }

    /**
     * 将数据设置到GUI元素中
     */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        patamsTextArea.setInitialText(el.getPropertyAsString(TraverseEmptyValue.PATAMS));
        patamsTextArea.setCaretPosition(0);
        emptyCheckExpectionTextArea.setInitialText(el.getPropertyAsString(TraverseEmptyValue.EMPTY_CHECK_EXPECTION));
        emptyCheckExpectionTextArea.setCaretPosition(0);
    }

    @Override
    public void clearGui() {
        super.clearGui();
        patamsTextArea.setInitialText("");
        emptyCheckExpectionTextArea.setInitialText("");
    }

    private JPanel createPatamsPanel() {
        patamsTextArea = JSyntaxTextArea.getInstance(10, 10);

        JLabel label = new JLabel(TraverseEmptyValue.PATAMS);
        label.setLabelFor(patamsTextArea);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(label, BorderLayout.NORTH);
        panel.add(JTextScrollPane.getInstance(patamsTextArea), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createEmptyCheckExpectionPanel() {
        emptyCheckExpectionTextArea = JSyntaxTextArea.getInstance(10, 10);

        JLabel label = new JLabel(TraverseEmptyValue.EMPTY_CHECK_EXPECTION);
        label.setLabelFor(emptyCheckExpectionTextArea);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(label, BorderLayout.NORTH);
        panel.add(JTextScrollPane.getInstance(emptyCheckExpectionTextArea), BorderLayout.CENTER);

        String note = "说明：\n" +
                "1. Params为原接口请求报文，例如： \"key1\":\"val1\",\"key2\":\"val2\"\n" +
                "2. EmptyCheckExpection为接口各字段非空校验预期结果，例如： \"key1\":true,\"key2\":false\n" +
                "3. 遍历的key以EmptyCheckExpection的内容为准\n"+
                "4. 请将线程组设置为无限循环，数据遍历完毕时线程组将自动停止循环\n" +
                "5. 请求报文变量名默认=params，预期结果变量名默认=expection，当前JsonPath变量名默认=jsonPath\n"+
                "6. 该插件中数据参数化不会替换具体的值，请在使用的位置利用${__eval(${params})}函数替换";
        JTextArea textArea = new JTextArea(note);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setBackground(this.getBackground());
        panel.add(textArea, BorderLayout.SOUTH);

        return panel;
    }

}


