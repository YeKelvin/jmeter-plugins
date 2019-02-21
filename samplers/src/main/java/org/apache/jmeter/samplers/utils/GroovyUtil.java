package org.apache.jmeter.samplers.utils;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description
 *
 * @author KelvinYe
 * Date     2019-02-21
 * Time     17:58
 */
public class GroovyUtil {
    public static String AND_OR_NOT_PATTERN = "([^!()]*(\\&\\&|\\|\\|))|[^!()]*";
    public static Pattern andOrNotregex = Pattern.compile(AND_OR_NOT_PATTERN);

    /**
     * 判断预期结果是否为表达式
     */
    public static boolean isExpression(String expression) {
        return expression.contains("&&") && expression.contains("||");
    }

    /**
     * 验证表达式语法
     */
    public static boolean verifyExpression(String expression) {
        return !expression.startsWith("&&") && !expression.startsWith("||") &&
                !expression.endsWith("&&") && !expression.endsWith("||");
    }

    /**
     * 验证是否有括号和开始结束括号是否相等
     */
    public static boolean verifyBrackets(String expression) {
        if (expression.contains("(") || expression.contains(")")) {
            int startBracketsTotal = 0;
            int endBracketsTotal = 0;
            for (int i = 0; i < expression.length(); i++) {
                if (expression.charAt(i) == '(') {
                    startBracketsTotal++;
                }
                if (expression.charAt(i) == ')') {
                    endBracketsTotal++;
                }
            }
            return startBracketsTotal == endBracketsTotal;
        }
        return true;
    }

    /**
     * 将Jmeter脚本中的预期结果表达式转换为groovy表达式
     */
    public static String transformExpression(String expression) {
        // 去除空格
        expression = expression.replace(" ", "");
        Matcher matcher = andOrNotregex.matcher(expression);

        // 正则匹配结果列表
        ArrayList<String> expressionMatchslist = new ArrayList<>();
        ArrayList<Integer> startPositionList = new ArrayList<>();
        ArrayList<Integer> endPositionList = new ArrayList<>();

        while (matcher.find()) {
            String groupResult = matcher.group();
            if (!"".equals(groupResult)) {
                startPositionList.add(matcher.start());
                endPositionList.add(matcher.end());
                expressionMatchslist.add(groupResult);
            }
        }

        StringBuilder newExpression = new StringBuilder();
        int beginIndex = 0;

        for (int i = 0; i < expressionMatchslist.size(); i++) {
            newExpression.append(expression, beginIndex, startPositionList.get(i));
            String currentExp = expressionMatchslist.get(i);
            if (currentExp.endsWith("&&") || currentExp.endsWith("||")) {
                newExpression.append("response.contains('")
                        .append(currentExp, 0, currentExp.length() - 2)
                        .append("')")
                        .append(currentExp.substring(currentExp.length() - 2));
            } else {
                newExpression.append("response.contains('").append(currentExp).append("')");
            }
            beginIndex = endPositionList.get(i);
            if (i == expressionMatchslist.size() - 1) {
                newExpression.append(expression.substring(beginIndex));
            }
        }
        return newExpression.toString();
    }

    public static Object eval(Binding binding, String expression) {
        GroovyShell groovyShell = new GroovyShell(binding);
        return groovyShell.evaluate(expression);
    }

    public static void main(String[] args) throws IOException {
        String response = "{\"result\":{\"isCreate\":null,\"contractNo\":\"3177002083010211\",\"orgCode\":\"132837736180\",\"operatorNo\":\"3178002076180412\",\"accountNo\":{\"BALANCE\":\"7100848323127156\"},\"mobile\":\"14735621201\",\"loginPassword\":\"921116\",\"payPassword\":\"887863\",\"customerNo\":\"5188002858924958\"},\"primaryErrorMsg\":null,\"success\":true,\"primaryErrorCode\":null,\"primaryErrorIP\":null,\"errorCode\":null,\"errorMsg\":null}";
        String expression = "\"success\":true && \"errorCode\":null";
        Binding binding = new Binding();
        binding.setVariable("response", response);
        GroovyShell groovyShell = new GroovyShell(binding);

        expression = expression.replace(" ", "");
        System.out.println(expression);
        String AND_OR_NOT_PATTERN = "([^!()]*(\\&\\&|\\|\\|))|[^!()]*";
        Pattern regex = Pattern.compile(AND_OR_NOT_PATTERN);
        Matcher matcher = regex.matcher(expression);
        ArrayList<String> expressionMatchslist = new ArrayList<>();
        ArrayList<Integer> startPositionList = new ArrayList<>();
        ArrayList<Integer> endPositionList = new ArrayList<>();

        while (matcher.find()) {
            String groupResult = matcher.group();
            if (!"".equals(groupResult)) {
                startPositionList.add(matcher.start());
                endPositionList.add(matcher.end());
                expressionMatchslist.add(groupResult);
            }
        }

        StringBuilder newExpression = new StringBuilder();
        int beginIndex = 0;

        for (int i = 0; i < expressionMatchslist.size(); i++) {
            newExpression.append(expression, beginIndex, startPositionList.get(i));
            String currentExp = expressionMatchslist.get(i);
            if (currentExp.endsWith("&&") || currentExp.endsWith("||")) {
                newExpression.append("response.contains('")
                        .append(currentExp, 0, currentExp.length() - 2)
                        .append("')")
                        .append(currentExp.substring(currentExp.length() - 2));
            } else {
                newExpression.append("response.contains('").append(currentExp).append("')");
            }
            beginIndex = endPositionList.get(i);
            if (i == expressionMatchslist.size() - 1) {
                newExpression.append(expression.substring(beginIndex));
            }
        }
        System.out.println(newExpression.toString());
        System.out.println(groovyShell.evaluate(newExpression.toString()));


    }
}
