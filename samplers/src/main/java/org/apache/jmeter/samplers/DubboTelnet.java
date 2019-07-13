package org.apache.jmeter.samplers;

import groovy.lang.Binding;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.utils.GroovyUtil;
import org.apache.jmeter.samplers.utils.TelnetUtil;
import org.slf4j.Logger;
import pers.kelvin.util.exception.ExceptionUtil;
import pers.kelvin.util.log.LogUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author KelvinYe
 */
public class DubboTelnet extends AbstractJavaSamplerClient {
    private static final Logger logger = LogUtil.getLogger(DubboTelnet.class);

    private String dubboHost;
    private String dubboPort;
    private String interfaceName;
    private String encode;
    private static final int defaultTimeout = 5000;
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
        params.addArgument("expectation", "");
        params.addArgument("encode", "");
        return params;
    }

    /**
     * 执行测试前的初始化操作
     */
    @Override
    public void setupTest(JavaSamplerContext ctx) {
        String[] address = ctx.getParameter("address").split(":");
        dubboHost = address[0];
        dubboPort = address.length == 1 ? "0" : address[1];
        interfaceName = ctx.getParameter("interface", "");
        encode = ctx.getParameter("encode", StandardCharsets.UTF_8.name());
    }

    /**
     * 测试方法主体
     */
    @Override
    public SampleResult runTest(JavaSamplerContext ctx) {
        String params = ctx.getParameter("params", "");
        String expectation = ctx.getParameter("expectation", "");

        SampleResult result = new SampleResult();
        result.setEncodingAndType(StandardCharsets.UTF_8.name());
        boolean isSuccess = false;
        String responseData = "";
        try {
            result.setSamplerData(interfaceName + "(" + params + ")");
            result.sampleStart();
            responseData = invokeDubbo(interfaceName, params);
            isSuccess = getSuccessful(responseData, expectation);
        } catch (Exception e) {
            // 异常后，判断是否已开始统计sample时间，没有则开始统计
            if (result.isStampedAtStart()) {
                result.sampleStart();
            }
            responseData = ExceptionUtil.getStackTrace(e);
        } finally {
            result.sampleEnd();
            result.setSuccessful(isSuccess);
            result.setResponseData(responseData, StandardCharsets.UTF_8.name());
        }

        return result;
    }

    /**
     * 执行测试后的释放资源工作
     */
    @Override
    public void teardownTest(JavaSamplerContext ctx) {
    }

    private String invokeDubbo(String interfaceName, String requestData) throws IOException {
        TelnetUtil telnet = new TelnetUtil(dubboHost, dubboPort, encode, defaultTimeout);
        String response = telnet.invokeDubbo(interfaceName, requestData);
        telnet.disconnect();
        return response;
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

}
