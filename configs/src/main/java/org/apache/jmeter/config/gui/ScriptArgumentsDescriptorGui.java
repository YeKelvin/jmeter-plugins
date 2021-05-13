package org.apache.jmeter.config.gui;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.ScriptArgumentsDescriptor;
import org.apache.jmeter.testelement.TestElement;

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

}
