package org.apache.jmeter.functions;

import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.services.FileServer;

import java.io.File;
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
public class ScriptBaseDir extends AbstractFunction {
    /**
     * 自定义function的描述
     */
    private static final List<String> DESC = new LinkedList<>();

    static {
        DESC.add("获取jmx脚本所在目录路径");
        DESC.add("如有入參则把参数值拼接到jmx脚本所在目录路径后");
    }

    /**
     * function名称
     */
    private static final String KEY = "__ScriptBaseDir";

    /**
     * function传入的参数的值
     */
    private Object[] values = null;

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
        //将入參的实际值存入values中
        values = parameters.toArray();
    }

    /**
     * function执行
     */
    @Override
    public String execute(SampleResult sampleResult, Sampler sampler) throws InvalidVariableException {
        try {
            String scriptBasePath = FileServer.getFileServer().getBaseDir();
            if (values.length != 0) {
                for (Object value : values) {
                    String childPath = ((CompoundVariable) value).execute().trim();
                    scriptBasePath = pathJoin(scriptBasePath, childPath);
                }
            }
            return scriptBasePath.replace("\\", "/");
        } catch (Exception ex) {
            throw new InvalidVariableException(ex);
        }
    }

    private String pathJoin(String parentPath, String childPath) {
        String winSep = "\\";
        String unixSep = "/";
        if (parentPath.endsWith(winSep) || parentPath.endsWith(unixSep)) {
            parentPath = parentPath.substring(0, parentPath.length() - 1);
        }
        if (childPath.startsWith(winSep) || childPath.startsWith(unixSep)) {
            childPath = childPath.substring(1);
        }
        if (childPath.endsWith(winSep) || childPath.endsWith(unixSep)) {
            childPath = childPath.substring(0, childPath.length() - 1);
        }
        return parentPath + File.separator + childPath;
    }

}
