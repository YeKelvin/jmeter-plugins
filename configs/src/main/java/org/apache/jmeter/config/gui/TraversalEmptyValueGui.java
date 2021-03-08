package org.apache.jmeter.config.gui;

import org.apache.jmeter.common.utils.GuiUtil;
import org.apache.jmeter.common.utils.StringUtil;
import org.apache.jmeter.common.utils.exception.ServiceException;
import org.apache.jmeter.common.utils.json.JsonFileUtil;
import org.apache.jmeter.config.TraversalEmptyValue;
import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.gui.util.JTextScrollPane;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * @author KelvinYe
 * Date: 2018-04-17
 * Time: 11:10
 */
public class TraversalEmptyValueGui extends AbstractConfigGui {

    private JComboBox<String> blankTypeComboBox;
    private JSyntaxTextArea paramsTextArea;
    private JSyntaxTextArea emptyCheckExpectationTextArea;

    private JComboBox<String> useTemplateComboBox;
    private JTextField interfacePathTextField;
    private JTextField interfaceNameTextField;

    public TraversalEmptyValueGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);
        add(createJTabbedPane(), BorderLayout.CENTER);
    }

    @Override
    public String getStaticLabel() {
        return "空值遍历配置器";
    }


    @Override
    public String getLabelResource() {
        return null;
    }


    @Override
    public TestElement createTestElement() {
        TraversalEmptyValue dataSet = new TraversalEmptyValue();
        modifyTestElement(dataSet);
        return dataSet;
    }

    /**
     * 将数据从GUI元素移动到TestElement
     */
    @Override
    public void modifyTestElement(TestElement el) {
        super.configureTestElement(el);
        el.setProperty(TraversalEmptyValue.BLANK_TYPE, (String) blankTypeComboBox.getSelectedItem());
        if (!el.getPropertyAsBoolean(TraversalEmptyValue.USE_TEMPLATE, false)) {
            el.setProperty(TraversalEmptyValue.PATAMS, paramsTextArea.getText());
        }
        el.setProperty(TraversalEmptyValue.EMPTY_CHECK_EXPECTATION, emptyCheckExpectationTextArea.getText());
        el.setProperty(TraversalEmptyValue.USE_TEMPLATE, (String) useTemplateComboBox.getSelectedItem());
        el.setProperty(TraversalEmptyValue.INTERFACE_PATH, interfacePathTextField.getText());
        el.setProperty(TraversalEmptyValue.INTERFACE_NAME, interfaceNameTextField.getText());
    }

    /**
     * 将数据设置到GUI元素中
     */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        blankTypeComboBox.setSelectedItem(el.getPropertyAsString(TraversalEmptyValue.BLANK_TYPE));
        paramsTextArea.setInitialText(el.getPropertyAsString(TraversalEmptyValue.PATAMS));
        paramsTextArea.setCaretPosition(0);
        emptyCheckExpectationTextArea.setInitialText(el.getPropertyAsString(TraversalEmptyValue.EMPTY_CHECK_EXPECTATION));
        emptyCheckExpectationTextArea.setInitialText(getTemplateContent(
                el.getPropertyAsBoolean(TraversalEmptyValue.USE_TEMPLATE, false),
                el.getPropertyAsString(TraversalEmptyValue.INTERFACE_NAME)));
        emptyCheckExpectationTextArea.setCaretPosition(0);
        useTemplateComboBox.setSelectedItem(el.getPropertyAsString(TraversalEmptyValue.USE_TEMPLATE));
        interfacePathTextField.setText(el.getPropertyAsString(TraversalEmptyValue.INTERFACE_PATH));
        interfacePathTextField.setText(el.getPropertyAsString(TraversalEmptyValue.INTERFACE_NAME));
    }

    @Override
    public void clearGui() {
        super.clearGui();
        blankTypeComboBox.setSelectedItem("");
        paramsTextArea.setInitialText("");
        emptyCheckExpectationTextArea.setInitialText("");
        useTemplateComboBox.setSelectedItem("");
        interfacePathTextField.setText("");
        interfaceNameTextField.setText("");
    }

    private Component createBlankTypeComboBox() {
        if (blankTypeComboBox == null) {
            blankTypeComboBox = GuiUtil.createComboBox(TraversalEmptyValue.BLANK_TYPE);
            blankTypeComboBox.addItem("null");
            blankTypeComboBox.addItem("\"\"");
        }
        return blankTypeComboBox;
    }

    private Component createBlankTypeLabel() {
        return GuiUtil.createLabel("空类型：", createBlankTypeComboBox());
    }

    private Component createParamsTextArea() {
        if (paramsTextArea == null) {
            paramsTextArea = GuiUtil.createTextArea(TraversalEmptyValue.PATAMS, 20);
        }
        return paramsTextArea;
    }

    private Component createParamsLabel() {
        return GuiUtil.createLabel("请求报文：", createParamsTextArea());
    }

    private Component createParamsPanel() {
        return JTextScrollPane.getInstance((JSyntaxTextArea) createParamsTextArea());
    }

    private Component createEmptyCheckExpectationTextArea() {
        if (emptyCheckExpectationTextArea == null) {
            emptyCheckExpectationTextArea = GuiUtil.createTextArea(TraversalEmptyValue.EMPTY_CHECK_EXPECTATION, 20);
        }
        return emptyCheckExpectationTextArea;

    }

    private Component createEmptyCheckExpectationLabel() {
        return GuiUtil.createLabel("预期结果：", createEmptyCheckExpectationTextArea());
    }

    private Component createEmptyCheckExpectationPanel() {
        return JTextScrollPane.getInstance((JSyntaxTextArea) createEmptyCheckExpectationTextArea());
    }

    private Component createUseTemplateComboBox() {
        if (useTemplateComboBox == null) {
            useTemplateComboBox = GuiUtil.createComboBox(TraversalEmptyValue.USE_TEMPLATE);
            useTemplateComboBox.addItem("false");
            useTemplateComboBox.addItem("true");
        }
        return useTemplateComboBox;
    }

    private Component createUseTemplateLabel() {
        return GuiUtil.createLabel("使用模板：", createUseTemplateComboBox());
    }

    private Component createInterfacePathTextField() {
        if (interfacePathTextField == null) {
            interfacePathTextField = GuiUtil.createTextField(TraversalEmptyValue.INTERFACE_PATH);
        }
        return interfacePathTextField;
    }

    private Component createInterfacePathLabel() {
        return GuiUtil.createLabel("接口目录：", createInterfacePathTextField());
    }

    private Component createInterfaceNameTextField() {
        if (interfaceNameTextField == null) {
            interfaceNameTextField = GuiUtil.createTextField(TraversalEmptyValue.INTERFACE_NAME);
        }
        return interfaceNameTextField;
    }

    private Component createInterfaceNameLabel() {
        return GuiUtil.createLabel("接口名称：", createInterfaceNameTextField());
    }

    private Component createJTabbedPane() {
        JPanel interfacePanel = new JPanel(new GridBagLayout());
        interfacePanel.setBorder(GuiUtil.createTitledBorder("配置非空校验信息"));
        interfacePanel.add(createBlankTypeLabel(), GuiUtil.GridBag.labelConstraints);
        interfacePanel.add(createBlankTypeComboBox(), GuiUtil.GridBag.editorConstraints);
        interfacePanel.add(createParamsLabel(), GuiUtil.GridBag.labelConstraints);
        interfacePanel.add(GuiUtil.createBlankPanel(), GuiUtil.GridBag.editorConstraints);
        interfacePanel.add(createParamsPanel(), GuiUtil.GridBag.multiLineEditorConstraints);
        interfacePanel.add(createEmptyCheckExpectationLabel(), GuiUtil.GridBag.labelConstraints);
        interfacePanel.add(GuiUtil.createBlankPanel(), GuiUtil.GridBag.editorConstraints);
        interfacePanel.add(createEmptyCheckExpectationPanel(), GuiUtil.GridBag.multiLineEditorConstraints);

        JPanel templateBodyPanel = new JPanel(new GridBagLayout());
        templateBodyPanel.setBorder(GuiUtil.createTitledBorder("配置模板信息"));
        templateBodyPanel.add(createUseTemplateLabel(), GuiUtil.GridBag.labelConstraints);
        templateBodyPanel.add(createUseTemplateComboBox(), GuiUtil.GridBag.editorConstraints);
        templateBodyPanel.add(createInterfacePathLabel(), GuiUtil.GridBag.labelConstraints);
        templateBodyPanel.add(createInterfacePathTextField(), GuiUtil.GridBag.editorConstraints);
        templateBodyPanel.add(createInterfaceNameLabel(), GuiUtil.GridBag.labelConstraints);
        templateBodyPanel.add(createInterfaceNameTextField(), GuiUtil.GridBag.editorConstraints);
        VerticalPanel templatePanel = new VerticalPanel();
        templatePanel.add(templateBodyPanel);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("非空校验配置", interfacePanel);
        tabbedPane.add("模板配置", templatePanel);
        tabbedPane.add("说明", createNoteArea());

        return tabbedPane;
    }

    private Component createNoteArea() {
        String note =
                "1. 请将线程组设置为无限循环，数据遍历完毕时线程组将自动停止循环；\n" +
                "2. 请求报文变量名=params，预期结果变量名=expectation，当前 JsonPath变量名=jsonPath；\n" +
                "3. 该插件中数据引用变量或函数不会替换为具体的值，请在使用的位置利用 ${__eval(${params})} 函数替换。";
        return GuiUtil.createNoteArea(note, this.getBackground());
    }

    /**
     * 获取json模版内容
     */
    private String getTemplateContent(boolean useTemplate, String interfaceName) {
        if (useTemplate && StringUtil.isNotBlank(interfaceName)) {
            try {
                return readJsonFile(interfaceName);
            } catch (IOException | ServiceException e) {
                return e.getMessage();
            }
        }
        return "";
    }

    /**
     * 读取 Json文件的内容
     *
     * @param interfaceName 接口名称
     */
    private String readJsonFile(String interfaceName) throws IOException, ServiceException {
        String interfaceDir = interfacePathTextField.getText();

        if (StringUtil.isBlank(interfaceDir)) {
            throw new ServiceException("接口路径不允许为空");
        }
        // 根据入參 interfacePath递归搜索获取绝对路径
        String path = JsonFileUtil.findInterfacePathByKeywords(interfaceDir, interfaceName);
        if (path == null) {
            throw new ServiceException(String.format("\"%s\" 接口模版不存在", interfaceName));
        }
        // 根据绝对路径获取json模版内容
        return JsonFileUtil.readJsonFileToString(path);
    }

}


