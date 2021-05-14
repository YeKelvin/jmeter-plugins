package org.apache.jmeter.config.gui;

import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.ScriptArgumentsDescriptor;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.gui.ObjectTableModel;
import org.apache.jorphan.reflect.Functor;

/**
 * Description:
 *
 * @author: KelvinYe
 * Date: 2021-05-13
 * Time: 00:20
 */
public class ScriptArgumentsDescriptorGui extends ArgumentsPanel {

    public ScriptArgumentsDescriptorGui() {
        super("定义脚本入参", null, true, true, null, false);
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
        ScriptArgumentsDescriptor args = new ScriptArgumentsDescriptor();
        modifyTestElement(args);
        return args;
    }

    @Override
    protected void initializeTableModel() {
        if (tableModel == null) {
            tableModel = new ObjectTableModel(new String[]{"参数名称", "默认值", "参数描述"},
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
    }
}
