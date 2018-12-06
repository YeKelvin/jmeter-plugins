package org.apache.jmeter.samplers;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.utils.TelnetUtil;
import pers.kelvin.util.ExceptionUtil;

import java.io.IOException;

/**
 * @author KelvinYe
 */
public class DubboTelnet extends AbstractJavaSamplerClient {
    private String inf;
    private TelnetUtil telnet;
    private String connectErrorMessage;

    /**
     * 设置需传参数名和默认值
     */
    @Override
    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();
        params.addArgument("address", "");
        params.addArgument("interface", "");
        params.addArgument("params", "");
        params.addArgument("expection", "\"success\":true");
        return params;
    }

    /**
     * 执行测试前的初始化操作
     */
    @Override
    public void setupTest(JavaSamplerContext ctx) {
        String[] address = ctx.getParameter("address").split(":");
        String ip = address[0];
        String port = address.length == 1 ? "0" : address[1];
        inf = ctx.getParameter("interface", "");
        try {
            telnet = new TelnetUtil(ip, port);
        } catch (IOException e) {
            connectErrorMessage = ExceptionUtil.getStackTrace(e);
        }
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
            if (dubboResponse.contains(expection)) {
                // 判断结果是否包含期望值
                isSuccess = true;
            }
        } else {
            // telnet连接失败输出报错信息
            dubboResponse = connectErrorMessage;
        }
        // 记录sample结束时间
        result.sampleEnd();
        result.setSuccessful(isSuccess);
        result.setResponseData(dubboResponse, "UTF-8");
        result.setResponseCode(getResponseCode(dubboResponse));
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

    private String getResponseCode(String responseData) {
        if (responseData.contains("\"success\":true")) {
            return "true";
        }
        return "false";
    }

    /*private static String EXPECTION_PATTERN_STR = "[^\\&\\&|\\|\\|]*[\\&\\&|\\|\\|]{2}[^\\&\\&|\\|\\|]*";
    private static String EXPECTION_MATCHES_PATTERN_STR = "(" + EXPECTION_PATTERN_STR + ")*";
    private static Pattern expectionPattern = Pattern.compile(EXPECTION_PATTERN_STR);

    *//**
     * 根据预期结果表达式断言响应数据转并返回布尔值结果
     *
     * @param response  响应数据
     * @param expection 预期结果表达式
     * @return 结果
     *//*
    private boolean getExpectionAsBoolean(String response, String expection) {
        // 去空格
        expection = expection = expection.trim();
        // 判断是否为多条件表达式
        boolean isMatch = Pattern.matches(EXPECTION_MATCHES_PATTERN_STR, expection);
        if (!isMatch) {
            // 如匹配不到 && 或 || 字符则直接返回
            return response.contains(expection);
        } else {

            return false;
        }
    }

    private static ArrayList<String> expectionConvertToList(String expection) {
        ArrayList<String> expectionList = new ArrayList<>();
        Matcher matcher = expectionPattern.matcher(expection);
        while (matcher.find()) {
            if (!matcher.group().isEmpty()) {
                expectionList.add(matcher.group());
            }
        }
        return expectionList;
    }

    private static boolean exec(ArrayList<String> expectionList) {
        boolean result = false;
        for (int i = 0; i < expectionList.size(); i++) {
            String expection= expectionList.get(i);
            if (i == 0) {
                if (isAnd(expection)) {
                    String[] expections = expection.split("&&|\\|\\|");
                }
            }
        }

    }

    private static boolean isAnd(String expection) {
        return expection.contains("&&");
    }

    public static void main(String[] args) {
        String data = "{\"result\":\"aa\",\"isSuccess\"true}";
        String expection = "\"isSuccess\"true||\"aa\":\"aa\"&&\"bb\":\"bb\"||\"cc\":\"cc\"";
        //String expection = "\"isSuccess\"true";
        //String patternStr = "([^\\&\\&|\\|\\|]*[\\&\\&|\\|\\|]{2}[^\\&\\&|\\|\\|]*)*";
        //String patternStr = "[^\\&\\&|\\|\\|]*[\\&\\&|\\|\\|]{2}[^\\&\\&|\\|\\|]*";
        //
        //Pattern pattern = Pattern.compile(EXPECTION_HEAD_PATTERN_STR);
        //Matcher matcher = pattern.matcher(expection);
        //while (matcher.find()) {
        //    System.out.println("result:[" + matcher.group() + "]");
        //}
        ArrayList<String> expectionList = expectionConvertToList(expection);
        for (String str : expectionList) {
            System.out.println(str);
        }
    }*/
}
