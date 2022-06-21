package org.apache.jmeter.samplers.gui;


import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.common.jmeter.JMeterGuiUtil;
import org.apache.jmeter.common.jmeter.ValueReplaceUtil;
import org.apache.jmeter.common.utils.DesktopUtil;
import org.apache.jmeter.common.utils.RuntimeUtil;
import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.ScriptArgumentsDescriptor;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.config.gui.EnvDataSetGui;
import org.apache.jmeter.exceptions.IllegalUserActionException;
import org.apache.jmeter.samplers.JMeterScriptSampler;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.services.FileServer;
import org.apache.jmeter.testelement.TestElement;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Kelvin.Ye
 */
public class JMeterScriptSamplerGui extends AbstractSamplerGui implements ActionListener {

    private static final Logger log = LoggerFactory.getLogger(JMeterScriptSamplerGui.class);

    /**
     * Action命令
     */
    private static final String OPEN_DIRECTORY_ACTION = "OPEN_DIRECTORY";
    private static final String OPEN_SCRIPT_ACTION = "OPEN_SCRIPT";

    /**
     * 脚本后缀名称
     */
    private static final String JMX_SUFFIX = ".jmx";

    /**
     * 无效参数提示
     */
    private static final String INVALID_ARGUMENTS_MSG = "无效参数，建议删除";

    /**
     * swing组件
     */
    private final JTextField scriptDirectoryField;
    private final JLabel scriptDirectoryLabel;

    private final JTextField scriptNameField;
    private final JLabel scriptNameLabel;

    private final JComboBox<String> syncToPropsComboBox;
    private final JLabel syncToPropsLabel;

    private final JComboBox<String> syncToVarsComboBox;
    private final JLabel syncToVarsLabel;

    private final ArgumentsPanel argsPanel;

    /**
     * 缓存数据
     */
    public static final Map<String, Arguments> CACHED_SCRIPT_ARGUMENTS = Collections.synchronizedMap(new HashMap<>());
    public static final Map<String, Long> CACHED_SCRIPT_LAST_MODIFIED = Collections.synchronizedMap(new HashMap<>());

    /**
     * 插件说明
     */
    private static final String NOTE =
            "1、【脚本目录】: 脚本所在目录路径，建议使用变量\n" +
                    "2、【脚本名称】: 脚本文件名称，需要包含jmx后缀\n" +
                    "3、【同步vars至props】: 将目标脚本中新增的线程变量同步至全局变量中\n" +
                    "4、【传递vars至脚本】:\n" +
                    "       4.1、将调用方的线程变量同步至目标脚本中（不会覆盖目标脚本中已存在的Key）\n" +
                    "       4.2、执行结束时将目标脚本新增的线程变量返回给调用方";

    public JMeterScriptSamplerGui() {
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
        return "JMX 取样器";
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
     * GUI -> TestElement
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

            Arguments mergedArguments = mergeArguments((Arguments) argsPanel.createTestElement());
            for (JMeterProperty mergedProp : mergedArguments) {
                Argument arg = (Argument) mergedProp.getObjectValue();
                arg.setDescription("");
            }
            script.setArguments(mergedArguments);
        }
    }

    /**
     * TestElement -> GUI
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
            argsPanel.configure(mergeArguments(script.getArguments()));
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
            case OPEN_SCRIPT_ACTION:
                openScript();
                break;
            default:
                break;
        }
    }

    private void openScript() {
        try {
            String scriptPath = getScriptPath();
            if (StringUtils.isBlank(scriptPath)) {
                return;
            }

            String jmeterBinDir = JMeterUtils.getJMeterBinDir();
            String logPath = jmeterBinDir + File.separator + "jmeter-subscript.log";
            String command = String.format("jmeter -t %s -j %s", scriptPath, logPath);
            RuntimeUtil.exec(command, jmeterBinDir);
        } catch (IOException e) {
            log.info(e.getMessage());
        }
    }

    private Arguments mergeArguments(Arguments selfArgs) {
        Arguments targetArgs;
        try {
            targetArgs = getScriptArgumentsWithCache();
        } catch (IOException | IllegalUserActionException e) {
            log.info(e.getMessage());
            return selfArgs;
        }

        // 指定脚本的参数个数为0 且 调用者的参数个数也为0时，直接返回调用者的参数
        if (targetArgs.getArgumentCount() == 0 && selfArgs.getArgumentCount() == 0) {
            return selfArgs;
        }

        // 指定脚本的参数个数为0 且 调用者的参数个数大于0时，提示调用者的参数为无效参数
        if (targetArgs.getArgumentCount() == 0 && selfArgs.getArgumentCount() > 0) {
            for (JMeterProperty selfProp : selfArgs) {
                Argument selfArg = (Argument) selfProp.getObjectValue();
                selfArg.setDescription(INVALID_ARGUMENTS_MSG);
            }
            return selfArgs;
        }

        // 指定脚本的参数个数大于0 且 调用者的参数个数为0时，复制指定脚本的参数给调用者
        if (targetArgs.getArgumentCount() > 0 && selfArgs.getArgumentCount() == 0) {
            for (JMeterProperty targetProp : targetArgs) {
                Argument targetArg = (Argument) targetProp.getObjectValue();
                selfArgs.addArgument(
                        targetArg.getName(),
                        targetArg.getValue(),
                        targetArg.getDescription(),
                        targetArg.getMetaData()
                );
            }
            return selfArgs;
        }

        // 以下是双方参数个数都大于0的情况
        // 遍历指定脚本的参数，判断调用者是否存在相同的参数，存在则更新描述，不存在则新增参数
        for (JMeterProperty targetProp : targetArgs) {
            Argument targetArg = (Argument) targetProp.getObjectValue();
            String targetArgName = targetArg.getName();

            boolean exist = false;
            for (JMeterProperty selfProp : selfArgs) {
                Argument selfArg = (Argument) selfProp.getObjectValue();
                // 存在时更新描述后退出for循环
                if (selfArg.getName().equals(targetArgName)) {
                    selfArg.setDescription(targetArg.getDescription());
                    exist = true;
                    break;
                }
            }

            // 不存在时添加
            if (!exist) {
                selfArgs.addArgument(
                        targetArg.getName(),
                        targetArg.getValue(),
                        targetArg.getDescription()
                );
            }
        }

        // 遍历调用者的参数，判断指定脚本是否存在相同的参数，存在则更新描述，不存在则新增参数
        for (JMeterProperty selfProp : selfArgs) {
            Argument selfArg = (Argument) selfProp.getObjectValue();
            String selfArgName = selfArg.getName();

            boolean exist = false;
            for (JMeterProperty targetProp : targetArgs) {
                Argument targetArg = (Argument) targetProp.getObjectValue();
                // 存在时退出for循环
                if (targetArg.getName().equals(selfArgName)) {
                    exist = true;
                    break;
                }
            }

            // 不存在时描述更新为无效的参数
            if (!exist) {
                selfArg.setDescription(INVALID_ARGUMENTS_MSG);
            }
        }

        return selfArgs;
    }

    /**
     * 打开脚本目录
     */
    private void openScriptDirectory() {
        String scriptDirectory = getScriptDirectoryPath();
        DesktopUtil.openFile(scriptDirectory);
    }

    /**
     * 获取脚本目录路径
     */
    private String getScriptDirectoryPath() {
        String scriptDirectory = scriptDirectoryField.getText();
        if (StringUtils.isNotBlank(scriptDirectory)) {
            return ValueReplaceUtil.replace(scriptDirectory, getConfigVariables());
        }
        return scriptDirectory;
    }

    /**
     * 根据配置文件名称反序列化yaml文件并返回，
     */
    private Map<String, String> getConfigVariables() {
        String configPath = EnvDataSetGui.CACHED_SELECTED_CONFIG_PATH.get(getScriptName());
        log.debug("configPath:[ {} ]", configPath);

        if (configPath == null) {
            log.debug("CACHED_CONFIG_PATH:[ {} ]", EnvDataSetGui.CACHED_SELECTED_CONFIG_PATH);

            Map<String, String> blankVariables = new HashMap<>();
            log.debug("configVariables:[ {} ]", blankVariables);
            return blankVariables;
        }

        log.debug("configPath:[ {} ]", configPath);
        Map<String, String> configVariables =
                EnvDataSetGui.CACHED_CONFIG_VARIABLES.getOrDefault(configPath, new HashMap<>());
        log.debug("configVariables:[ {} ]", configVariables);
        return configVariables;
    }

    /**
     * 创建表格模型
     */
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
        return JMeterGuiUtil.createTextField(JMeterScriptSampler.SCRIPT_DIRECTORY);
    }

    private JLabel createScriptDirectoryLabel() {
        return JMeterGuiUtil.createLabel("脚本目录：", scriptDirectoryField);
    }

    private JTextField createScriptNameTextField() {
        return JMeterGuiUtil.createTextField(JMeterScriptSampler.SCRIPT_NAME);
    }

    private JLabel createScriptNameLabel() {
        return JMeterGuiUtil.createLabel("脚本名称：", scriptNameField);
    }

    private JComboBox<String> createSyncToPropsComboBox() {
        JComboBox<String> comboBox = JMeterGuiUtil.createComboBox(JMeterScriptSampler.SYNC_TO_PROPS);
        comboBox.addItem("false");
        comboBox.addItem("true");
        return comboBox;
    }

    private JLabel createSyncToPropsLabel() {
        return JMeterGuiUtil.createLabel("同步vars至props：", syncToPropsComboBox);
    }

    private JComboBox<String> createSyncToVarsComboBox() {
        JComboBox<String> comboBox = JMeterGuiUtil.createComboBox(JMeterScriptSampler.SYNC_TO_VARS);
        comboBox.addItem("true");
        comboBox.addItem("false");
        return comboBox;
    }

    private JLabel createSyncToVarsLabel() {
        return JMeterGuiUtil.createLabel("传递vars至脚本：", syncToVarsComboBox);
    }

    private Component createBodyPanel() {
        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.add(createScriptPanel(), BorderLayout.NORTH);
        bodyPanel.add(argsPanel, BorderLayout.CENTER);
        return bodyPanel;
    }

    private JPanel createScriptPanel() {
        JPanel scriptPanel = new JPanel(new GridBagLayout());
        scriptPanel.setBorder(JMeterGuiUtil.createTitledBorder("脚本配置"));

        scriptPanel.add(scriptDirectoryLabel, JMeterGuiUtil.GridBag.mostLeftConstraints);
        scriptPanel.add(scriptDirectoryField, JMeterGuiUtil.GridBag.middleConstraints);
        scriptPanel.add(createOpenDirectoryButton(), JMeterGuiUtil.GridBag.mostRightConstraints);

        scriptPanel.add(scriptNameLabel, JMeterGuiUtil.GridBag.mostLeftConstraints);
        scriptPanel.add(scriptNameField, JMeterGuiUtil.GridBag.middleConstraints);
        scriptPanel.add(createOpenScriptButton(), JMeterGuiUtil.GridBag.mostRightConstraints);

        scriptPanel.add(syncToPropsLabel, JMeterGuiUtil.GridBag.labelConstraints);
        scriptPanel.add(syncToPropsComboBox, JMeterGuiUtil.GridBag.editorConstraints);

        scriptPanel.add(syncToVarsLabel, JMeterGuiUtil.GridBag.labelConstraints);
        scriptPanel.add(syncToVarsComboBox, JMeterGuiUtil.GridBag.editorConstraints);

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
        argsPanel.setBorder(JMeterGuiUtil.createTitledBorder("脚本入参"));
        return argsPanel;
    }

    private Component createNoteArea() {
        return JMeterGuiUtil.createNoteArea(NOTE, this.getBackground());
    }

    private Component createOpenDirectoryButton() {
        JButton button = new JButton("OPEN");
        button.setActionCommand(OPEN_DIRECTORY_ACTION);
        button.addActionListener(this);
        return button;
    }

    private Component createOpenScriptButton() {
        JButton button = new JButton("OPEN");
        button.setActionCommand(OPEN_SCRIPT_ACTION);
        button.addActionListener(this);
        return button;
    }

    /**
     * 获取脚本绝对路径路径
     */
    private String getScriptPath() throws FileNotFoundException {
        String scriptName = scriptNameField.getText();
        String scriptDirectory = getScriptDirectoryPath();

        if (StringUtils.isBlank(scriptDirectory) || StringUtils.isBlank(scriptName)) {
            throw new FileNotFoundException(
                    String.format("脚本路径为空, scriptDirectory:[ %s ] scriptName:[ %s ]", scriptDirectory, scriptName));
        }

        if (!scriptName.endsWith(JMX_SUFFIX)) {
            throw new FileNotFoundException(String.format("脚本非.jmx文件, scriptName:[ %s ]", scriptName));
        }

        String scriptPath = scriptDirectory + File.separator + scriptName;
        log.debug("scriptPath:[ {} ]", scriptPath);
        return scriptPath;
    }

    private File getScriptFile() throws FileNotFoundException {
        String scriptPath = getScriptPath();
        return getScriptFile(scriptPath);
    }

    private File getScriptFile(String scriptPath) throws FileNotFoundException {
        File file = new File(scriptPath);
        if (!file.exists() || !file.isFile() || !scriptPath.endsWith(JMX_SUFFIX)) {
            throw new FileNotFoundException(String.format("脚本不存在或非.jmx文件, scriptPath:[ %s ]", scriptPath));
        }

        return file;
    }

    /**
     * 获取脚本HashTree对象中的ScriptArgumentsDescriptor对象
     */
    private Arguments getArgumentsDescriptor(File scriptFile) throws IllegalUserActionException, IOException {
        // 加载脚本
        HashTree hashTree = SaveService.loadTree(scriptFile);
        // 获取TestPlan的HashTree对象
        HashTree testPlanTree = hashTree.get(hashTree.getArray()[0]);
        // 搜索ScriptArgumentsDescriptor对象
        SearchByClass<ScriptArgumentsDescriptor> argsDescSearcher = new SearchByClass<>(ScriptArgumentsDescriptor.class);
        testPlanTree.traverse(argsDescSearcher);
        Iterator<ScriptArgumentsDescriptor> argsDescIter = argsDescSearcher.getSearchResults().iterator();
        if (!argsDescIter.hasNext()) {
            return new Arguments();
        }
        return argsDescIter.next();
    }

    /**
     * 获取脚本Arguments对象
     */
    private synchronized Arguments getScriptArgumentsWithCache() throws IOException, IllegalUserActionException {
        String scriptPath = getScriptPath();
        File scriptFile = getScriptFile(scriptPath);
        long scriptLastModified = scriptFile.lastModified();

        // 获取静态缓存
        Arguments cachedScriptArguments = CACHED_SCRIPT_ARGUMENTS.get(scriptPath);
        long cachedScriptLastModified = CACHED_SCRIPT_LAST_MODIFIED.getOrDefault(scriptPath, (long) 0);

        // 如果缓存为空或脚本有更新，则重新读取脚本
        if (cachedScriptArguments == null || cachedScriptLastModified < scriptLastModified) {
            log.info("脚本有更新，重新缓存");

            Arguments scriptArguments = getArgumentsDescriptor(scriptFile);
            log.debug("缓存scriptArguments:[ {} ]", scriptArguments);
            log.debug("缓存scriptLastModified:[ {} ]", scriptLastModified);
            CACHED_SCRIPT_ARGUMENTS.put(scriptPath, scriptArguments);
            CACHED_SCRIPT_LAST_MODIFIED.put(scriptPath, scriptLastModified);
        }

        return CACHED_SCRIPT_ARGUMENTS.get(scriptPath);
    }

    /**
     * 获取当前脚本名称
     */
    private String getScriptName() {
        String scriptName = FileServer.getFileServer().getScriptName();
        log.debug("scriptName:[ {} ]", scriptName);
        return scriptName;
    }
}
