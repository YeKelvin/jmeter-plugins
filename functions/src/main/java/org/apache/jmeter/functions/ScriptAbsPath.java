package org.apache.jmeter.functions;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.services.FileServer;
import org.apache.jmeter.common.utils.PathUtil;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * 获取JmeterHome
 *
 * @author KelvinYe
 * Date     2018-08-22
 * Time     17:11
 */
public class ScriptAbsPath extends AbstractFunction {
    /**
     * 自定义function的描述
     */
    private static final List<String> DESC = new LinkedList<>();

    static {
        DESC.add("获取jmx脚本所在目录路径");
    }

    /**
     * function名称
     */
    private static final String KEY = "__ScriptAbsPath";

    /**
     * function传入的参数的值
     */
    private Collection<CompoundVariable> parameters = null;

    /**
     * function引用关键字
     */
    @Override
    public String getReferenceKey() {
        return KEY;
    }

    /**
     * function描述
     */
    @Override
    public List<String> getArgumentDesc() {
        return DESC;
    }

    /**
     * 设置function参数
     */
    @Override
    public void setParameters(Collection<CompoundVariable> parameters) {
        this.parameters = parameters;
    }

    /**
     * function执行
     */
    @Override
    public String execute(SampleResult sampleResult, Sampler sampler) throws InvalidVariableException {
        try {
            String scriptAbsPath = FileServer.getFileServer().getBaseDir();
            if (CollectionUtils.isNotEmpty(parameters)) {
                for (CompoundVariable parameter : parameters) {
                    String childPath = parameter.execute().trim();
                    scriptAbsPath = PathUtil.join(scriptAbsPath, childPath);
                }
            }
            return scriptAbsPath.replace("\\", "/");
        } catch (Exception ex) {
            throw new InvalidVariableException(ex);
        }
    }



}
