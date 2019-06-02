package org.apache.jmeter.samplers;

import groovy.lang.Binding;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.utils.GroovyUtil;
import org.apache.jmeter.samplers.utils.TelnetUtil;
import org.slf4j.Logger;
import pers.kelvin.util.StringUtil;
import pers.kelvin.util.exception.ExceptionUtil;
import pers.kelvin.util.log.LogUtil;

import java.io.IOException;

/**
 * @author KelvinYe
 */
public class DubboTelnet extends AbstractJavaSamplerClient {
    private static final Logger logger = LogUtil.getLogger(DubboTelnet.class);

    private String inf;
    private TelnetUtil telnet;
    private String errorMsg;

    /**
     * 设置需传参数名和默认值
     */
    @Override
    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();
        params.addArgument("address", "");
        params.addArgument("interface", "");
        params.addArgument("params", "");
        params.addArgument("expection", "");
        params.addArgument("encode", "");
//        params.addArgument("sshAddress", "");
//        params.addArgument("sshUserName", "");
//        params.addArgument("sshPassword", "");
        return params;
    }

    /**
     * 执行测试前的初始化操作
     */
    @Override
    public void setupTest(JavaSamplerContext ctx) {
        String[] address = ctx.getParameter("address").split(":");
        String host = address[0];
        String port = address.length == 1 ? "0" : address[1];

        inf = ctx.getParameter("interface", "");
        String encode = ctx.getParameter("encode", "UTF-8");
        initTelnet(host, port, encode);
    }

    /**
     * 测试方法主体
     */
    @Override
    public SampleResult runTest(JavaSamplerContext ctx) {
        String params = ctx.getParameter("params", "");
        String expection = ctx.getParameter("expection", "");
        String dubboResponse = "";
        boolean isSuccess = false;

        SampleResult result = new SampleResult();
        result.setEncodingAndType("UTF-8");
        result.setSamplerData(params);

        // 记录sample开始时间
        result.sampleStart();
        if (telnet != null) {
            // telnet连接成功则invoke报文
            dubboResponse = telnet.invokeDubbo(inf, params);
            // 预期结果判断
            if (StringUtil.isNotBlank(expection)) {
                isSuccess = getSuccessful(dubboResponse, expection);
            } else {
                isSuccess = true;
            }
        } else {
            // telnet连接失败输出报错信息
            dubboResponse = errorMsg;
        }

        // 记录sample结束时间
        result.sampleEnd();
        result.setSuccessful(isSuccess);
        result.setResponseData(dubboResponse, "UTF-8");
        return result;
    }

    /**
     * 执行测试后的释放资源工作
     */
    @Override
    public void teardownTest(JavaSamplerContext ctx) {
        if (telnet != null) {
            telnet.disconnect();
        }
    }

    private boolean getSuccessful(String responseData, String expection) {
        if (GroovyUtil.isExpression(expection)) {
            if (GroovyUtil.verifyExpression(expection) && GroovyUtil.verifyBrackets(expection)) {
                try {
                    String expression = GroovyUtil.transformExpression(expection);
                    Binding binding = new Binding();
                    binding.setVariable("response", responseData);
                    return (boolean) GroovyUtil.eval(binding, expression);
                } catch (Exception e) {
                    logger.error(ExceptionUtil.getStackTrace(e));
                    return false;
                }
            } else {
                logger.error("预期结果表达式语法有误");
                return false;
            }
        }
        return responseData.contains(expection);
    }

    private void initTelnet(String ip, String port, String charset) {
        try {
            telnet = new TelnetUtil(ip, port, charset);
        } catch (IOException e) {
            errorMsg = ExceptionUtil.getStackTrace(e);
        }
    }

}
