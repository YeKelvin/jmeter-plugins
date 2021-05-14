package org.apache.jmeter.samplers.gui;


import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.JMeter;
import org.apache.jmeter.common.utils.ExceptionUtil;
import org.apache.jmeter.common.utils.GuiUtil;
import org.apache.jmeter.common.utils.YamlUtil;
import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.ScriptArgumentsDescriptor;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.config.gui.EnvDataSetGui;
import org.apache.jmeter.engine.util.SimpleValueReplacer;
import org.apache.jmeter.exceptions.IllegalUserActionException;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.gui.tree.JMeterTreeModel;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.samplers.JMeterScriptSampler;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.services.FileServer;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.SearchByClass;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author KelvinYe
 */
public class JMeterScriptSamplerGui extends AbstractSamplerGui implements ActionListener {

    private static final Logger log = LoggerFactory.getLogger(JMeterScriptSamplerGui.class);

    private static final String OPEN_SCRIPT_ACTION = "OPEN_SCRIPT";
    private static final String OPEN_DIRECTORY_ACTION = "OPEN_DIRECTORY";
    private static final String PULL_ARGUMENTS_ACTION = "PULL_ARGUMENTS";

    private static final String JMX_SUFFIX = ".jmx";

    private static final String NOTE =
            "1、【脚本目录】: 脚本所在目录路径，建议使用变量\n" +
                    "2、【脚本名称】: 脚本文件名称，需要包含jmx后缀\n" +
                    "3、【同步vars至props】: 将目标脚本中新增的线程变量同步至全局变量中\n" +
                    "4、【传递vars至脚本】:\n" +
                    "       4.1、将调用方的线程变量同步至目标脚本中（不会覆盖目标脚本中已存在的Key）\n" +
                    "       4.2、执行结束时将目标脚本新增的线程变量返回给调用方";


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
        switch (action) {
            case OPEN_DIRECTORY_ACTION:
                openScriptDirectory();
                break;
            case PULL_ARGUMENTS_ACTION:
                pullArguments();
                break;
            case OPEN_SCRIPT_ACTION:
                openScript();
                break;
            default:
                break;
        }
    }

    private void openScript() {
    }

    private void pullArguments() {
        ScriptArgumentsDescriptor argsDesc = getArgumentsDescriptor();
        if (argsDesc != null) {
            argsDesc.getArgumentsAsMap().forEach((key, value) -> {
                log.info("key={}, value={}", key, value);
            });
        } else {
            log.info("no ScriptArgumentsDescriptor");
        }
    }

    private void openScriptDirectory() {
        try {
            String scriptDirectory = getScriptDirectoryPath();
            if (StringUtils.isNotBlank(scriptDirectory)) {
                File file = new File(scriptDirectory);
                if (file.exists() && file.isDirectory()) {
                    Desktop.getDesktop().open(file);
                } else {
                    log.warn("打开目录失败，目录不存在，路径:[ {} ]", scriptDirectory);
                }
            }
        } catch (IOException | InvalidVariableException exception) {
            log.error(ExceptionUtil.getStackTrace(exception));
        }
    }

    private String getScriptDirectoryPath() throws InvalidVariableException {
        String scriptDirectory = scriptDirectoryField.getText();
        String configName = EnvDataSetGui.CONFIG_NAME_WITH_SCRIPT.get(scriptName);
        if (StringUtils.isNotBlank(scriptDirectory) && StringUtils.isNotBlank(configName)) {
            String configPath = configDirectory + File.separator + configName;
            Map<String, String> variables = new HashMap<>();
            YamlUtil.parseYamlAsMap(configPath).forEach((key, value) -> {
                variables.put(key, value.toString());
            });
            SimpleValueReplacer replacer = new SimpleValueReplacer(variables);
            replacer.setParameters(scriptDirectory);
            return replacer.replace();
        }
        return scriptDirectory;
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
        scriptPanel.add(createOpenDirectoryButton(), GuiUtil.GridBag.mostRightConstraints);

        scriptPanel.add(scriptNameLabel, GuiUtil.GridBag.mostLeftConstraints);
        scriptPanel.add(scriptNameField, GuiUtil.GridBag.middleConstraints);
        scriptPanel.add(createPullArgumentsButton(), GuiUtil.GridBag.mostRightConstraints);

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

    private Component createOpenDirectoryButton() {
        JButton button = new JButton("OPEN");
        button.setActionCommand(OPEN_DIRECTORY_ACTION);
        button.addActionListener(this);
        return button;
    }

    private Component createPullArgumentsButton() {
        JButton button = new JButton("Pull Args");
        button.setActionCommand(PULL_ARGUMENTS_ACTION);
        button.addActionListener(this);
        return button;
    }

    private HashTree getScriptTree() throws IOException, IllegalUserActionException, InvalidVariableException {
        String scriptName = scriptNameField.getText();
        String scriptDirectory = getScriptDirectoryPath();

        if (StringUtils.isBlank(scriptDirectory) || StringUtils.isBlank(scriptName)) {
            log.debug("脚本路径为空, scriptDirectory:[ {} ] scriptName:[ {} ]", scriptDirectory, scriptName);
            return null;
        }

        if (!scriptName.endsWith(JMX_SUFFIX)) {
            log.debug("脚本名称必须包含jmx后缀, scriptName:[ {} ]", scriptName);
            return null;
        }

        String scriptPath = scriptDirectory + File.separator + scriptName;
        File file = new File(scriptPath);
        if (!file.exists() || !file.isFile()) {
            log.debug("脚本不存在, scriptPath:[ {} ]", scriptPath);
            return null;
        }

        // 加载脚本
        HashTree tree = SaveService.loadTree(file);

        // 对脚本做一些处理
        JMeterTreeModel treeModel = new JMeterTreeModel(new TestPlan());
        JMeterTreeNode root = (JMeterTreeNode) treeModel.getRoot();
        treeModel.addSubTree(tree, root);

        // 删除已禁用的组件
        return JMeter.convertSubTree(tree, false);
    }

    private ScriptArgumentsDescriptor getArgumentsDescriptor() {
        try {
            HashTree hashTree = getScriptTree();
            if (hashTree == null) {
                return null;
            }

            // 获取 TestPlan的HashTree对象
            HashTree testPlanTree = hashTree.get(hashTree.getArray()[0]);
            // 从 HashTree中搜索对应的组件对象
            SearchByClass<ScriptArgumentsDescriptor> argsDescSearcher = new SearchByClass<>(ScriptArgumentsDescriptor.class);
            testPlanTree.traverse(argsDescSearcher);
            Iterator<ScriptArgumentsDescriptor> argsDescIter = argsDescSearcher.getSearchResults().iterator();
            if (!argsDescIter.hasNext()) {
                return null;
            }
            return argsDescIter.next();
        } catch (IOException | IllegalUserActionException | InvalidVariableException e) {
            e.printStackTrace();
        }
        return null;
    }

}
