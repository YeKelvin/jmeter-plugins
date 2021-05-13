package org.apache.jmeter.samplers.gui;


import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.common.utils.GuiUtil;
import org.apache.jmeter.common.utils.YamlUtil;
import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.config.gui.EnvDataSetGui;
import org.apache.jmeter.samplers.JMeterScriptSampler;
import org.apache.jmeter.services.FileServer;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.ObjectTableModel;
import org.apache.jorphan.reflect.Functor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * @author KelvinYe
 */
public class JMeterScriptSamplerGui extends AbstractSamplerGui implements ActionListener {

    private static final Logger log = LoggerFactory.getLogger(JMeterScriptSamplerGui.class);

    private static final String NOTE =
            "1、【脚本目录】: 脚本所在目录路径，建议使用变量\n" +
                    "2、【脚本名称】: 脚本文件名称，需要包含jmx后缀\n" +
                    "3、【同步vars至props】: 将目标脚本中新增的线程变量同步至全局变量中\n" +
                    "4、【传递vars至脚本】:\n" +
                    "       4.1、将调用方的线程变量同步至目标脚本中（不会覆盖目标脚本中已存在的Key）\n" +
                    "       4.2、执行结束时将目标脚本新增的线程变量返回给调用方";

    private static final String OPEN_DIRECTORY_ACTION = "OPEN_DIRECTORY";

    private final JTextField scriptDirectoryField;
    private final JLabel scriptDirectoryLabel;

    private final JTextField scriptNameField;
    private final JLabel scriptNameLabel;

    private final JComboBox<String> syncToPropsComboBox;
    private final JLabel syncToPropsLabel;

    private final JComboBox<String> syncToVarsComboBox;
    private final JLabel syncToVarsLabel;

    private final ArgumentsPanel argsPanel;

    private final String scriptName;
    private final String configDirectory;

    public JMeterScriptSamplerGui() {
        scriptName = FileServer.getFileServer().getScriptName();
        configDirectory = JMeterUtils.getJMeterHome() + File.separator + "config";

        scriptDirectoryField = createScriptDirectoryTextField();
        scriptDirectoryLabel = createScriptDirectoryLabel();

        scriptNameField = createScriptNameTextField();
        scriptNameLabel = createScriptNameLabel();

        syncToPropsComboBox = createSyncToPropsComboBox();
        syncToPropsLabel = createSyncToPropsLabel();

        syncToVarsComboBox = createSyncToVarsComboBox();
        syncToVarsLabel = createSyncToVarsLabel();

        argsPanel = createArgumentsPanel();
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
        return "JMeter脚本取样器";
    }


    @Override
    public String getLabelResource() {
        return null;
    }


    @Override
    public TestElement createTestElement() {
        JMeterScriptSampler el = new JMeterScriptSampler();
        modifyTestElement(el);
        return el;
    }

    /**
     * 将数据从GUI元素移动到TestElement
     */
    @Override
    public void modifyTestElement(TestElement el) {
        super.configureTestElement(el);
        if (el instanceof JMeterScriptSampler) {
            JMeterScriptSampler script = (JMeterScriptSampler) el;
            el.setProperty(JMeterScriptSampler.SCRIPT_DIRECTORY, scriptDirectoryField.getText());
            el.setProperty(JMeterScriptSampler.SCRIPT_NAME, scriptNameField.getText());
            el.setProperty(JMeterScriptSampler.SYNC_TO_PROPS, (String) syncToPropsComboBox.getSelectedItem());
            el.setProperty(JMeterScriptSampler.SYNC_TO_VARS, (String) syncToVarsComboBox.getSelectedItem());
            script.setArguments((Arguments) argsPanel.createTestElement());
        }
    }

    /**
     * 将数据设置到GUI元素中
     */
    @Override
    public void configure(TestElement el) {
        super.configure(el);
        if (el instanceof JMeterScriptSampler) {
            JMeterScriptSampler script = (JMeterScriptSampler) el;
            scriptDirectoryField.setText(el.getPropertyAsString(JMeterScriptSampler.SCRIPT_DIRECTORY));
            scriptNameField.setText(el.getPropertyAsString(JMeterScriptSampler.SCRIPT_NAME));
            syncToPropsComboBox.setSelectedItem(el.getPropertyAsString(JMeterScriptSampler.SYNC_TO_PROPS));
            syncToVarsComboBox.setSelectedItem(el.getPropertyAsString(JMeterScriptSampler.SYNC_TO_VARS));
            final JMeterProperty argsProp = script.getArgumentsAsProperty();
            if (argsProp != null) {
                argsPanel.configure((Arguments) argsProp.getObjectValue());
            }
        }
    }

    @Override
    public void clearGui() {
        super.clearGui();
        scriptDirectoryField.setText("");
        scriptNameField.setText("");
        syncToPropsComboBox.setSelectedItem("false");
        syncToVarsComboBox.setSelectedItem("true");
        argsPanel.clear();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals(OPEN_DIRECTORY_ACTION)) {
            String scriptDirectory = scriptDirectoryField.getText();
            File file = new File(scriptDirectory);
            try {
                if (!file.exists()) {
                    String configName = EnvDataSetGui.CONFIG_NAME_WITH_SCRIPT.get(scriptName);
                    if (StringUtils.isNotBlank(configName)) {
                        String directoryPath = getScriptDirectoryPath(configName);
                        Desktop.getDesktop().open(new File(directoryPath));
                    } else {
                        log.warn("目录不存在");
                    }
                } else {
                    if (file.isDirectory()) {
                        Desktop.getDesktop().open(file);
                    } else {
                        log.warn("目录不存在");
                    }
                }
            } catch (IOException ioException) {
                log.error(ExceptionUtil.getStackTrace(ioException));
            }
        }
    }

    private String getScriptDirectoryPath(String configName) {
        String configPath = configDirectory + File.separator + configName;
        return (String) (YamlUtil.parseYamlAsMap(configPath).get(JMeterScriptSampler.SCRIPT_DIRECTORY));
    }

    private ObjectTableModel createTableModel() {
        return new ObjectTableModel(new String[]{"参数名称", "参数值", "参数描述"},
                Argument.class,
                new Functor[]{
                        new Functor("getName"),
                        new Functor("getValue"),
                        new Functor("getDescription")},
                new Functor[]{
                        new Functor("setName"),
                        new Functor("setValue"),
                        new Functor("setDescription")},
                new Class[]{String.class, String.class, String.class});
    }

    private JTextField createScriptDirectoryTextField() {
        return GuiUtil.createTextField(JMeterScriptSampler.SCRIPT_DIRECTORY);
    }

    private JLabel createScriptDirectoryLabel() {
        return GuiUtil.createLabel("脚本目录：", scriptDirectoryField);
    }

    private JTextField createScriptNameTextField() {
        return GuiUtil.createTextField(JMeterScriptSampler.SCRIPT_NAME);
    }

    private JLabel createScriptNameLabel() {
        return GuiUtil.createLabel("脚本名称：", scriptNameField);
    }

    private JComboBox<String> createSyncToPropsComboBox() {
        JComboBox<String> comboBox = GuiUtil.createComboBox(JMeterScriptSampler.SYNC_TO_PROPS);
        comboBox.addItem("false");
        comboBox.addItem("true");
        return comboBox;
    }

    private JLabel createSyncToPropsLabel() {
        return GuiUtil.createLabel("同步vars至props：", syncToPropsComboBox);
    }

    private JComboBox<String> createSyncToVarsComboBox() {
        JComboBox<String> comboBox = GuiUtil.createComboBox(JMeterScriptSampler.SYNC_TO_VARS);
        comboBox.addItem("true");
        comboBox.addItem("false");
        return comboBox;
    }

    private JLabel createSyncToVarsLabel() {
        return GuiUtil.createLabel("传递vars至脚本：", syncToVarsComboBox);
    }

    private Component createBodyPanel() {
        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.add(createScriptPanel(), BorderLayout.NORTH);
        bodyPanel.add(argsPanel, BorderLayout.CENTER);
        return bodyPanel;
    }

    private JPanel createScriptPanel() {
        JPanel scriptPanel = new JPanel(new GridBagLayout());
        scriptPanel.setBorder(GuiUtil.createTitledBorder("配置执行脚本信息"));

        scriptPanel.add(scriptDirectoryLabel, GuiUtil.GridBag.mostLeftConstraints);
        scriptPanel.add(scriptDirectoryField, GuiUtil.GridBag.middleConstraints);
        scriptPanel.add(createButton(), GuiUtil.GridBag.mostRightConstraints);

        scriptPanel.add(scriptNameLabel, GuiUtil.GridBag.labelConstraints);
        scriptPanel.add(scriptNameField, GuiUtil.GridBag.editorConstraints);

        scriptPanel.add(syncToPropsLabel, GuiUtil.GridBag.labelConstraints);
        scriptPanel.add(syncToPropsComboBox, GuiUtil.GridBag.editorConstraints);

        scriptPanel.add(syncToVarsLabel, GuiUtil.GridBag.labelConstraints);
        scriptPanel.add(syncToVarsComboBox, GuiUtil.GridBag.editorConstraints);

        return scriptPanel;
    }

    private ArgumentsPanel createArgumentsPanel() {
        ArgumentsPanel argsPanel = new ArgumentsPanel(
                null,
                this.getBackground(),
                true,
                false,
                createTableModel(),
                false
        );
        argsPanel.setBorder(GuiUtil.createTitledBorder("脚本入参"));
        return argsPanel;
    }

    private Component createNoteArea() {
        return GuiUtil.createNoteArea(NOTE, this.getBackground());
    }

    private Component createButton() {
        JButton button = new JButton("OPEN");
        button.setActionCommand(OPEN_DIRECTORY_ACTION);
        button.addActionListener(this);
        return button;
    }

}
