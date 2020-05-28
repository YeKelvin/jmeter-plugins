package org.apache.jmeter.samplers.utils;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

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
    private static final String AND_OR_NOT_PATTERN = "([^!()]*(\\&\\&|\\|\\|))|[^!()]*";
    private static final Pattern andOrNotregex = Pattern.compile(AND_OR_NOT_PATTERN);
    private static final String AND = "&&";
    private static final String OR = "||";
    private static final String NOT = "!";
    private static final String LEFT_BRACKETS = "(";
    private static final String RIGHT_BRACKETS = ")";


    /**
     * 判断预期结果是否为表达式
     */
    public static boolean isExpression(String expression) {
        return expression.contains(AND) || expression.contains(OR) || expression.contains(NOT);
    }

    /**
     * 验证表达式语法
     */
    public static boolean verifyExpression(String expression) {
        return !expression.startsWith(AND) && !expression.startsWith(OR) &&
                !expression.endsWith(AND) && !expression.endsWith(OR) &&
                !expression.endsWith(NOT);
    }

    /**
     * 验证是否有括号和开始结束括号是否相等
     */
    public static boolean verifyBrackets(String expression) {
        if (expression.contains(LEFT_BRACKETS) || expression.contains(RIGHT_BRACKETS)) {
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

        // 匹配结果列表
        ArrayList<String> expressionMatchslist = new ArrayList<>();
        // 匹配结果 start index列表
        ArrayList<Integer> startPositionList = new ArrayList<>();
        // 匹配结果 end index列表
        ArrayList<Integer> endPositionList = new ArrayList<>();

        while (matcher.find()) {
            String groupResult = matcher.group();
            if (!"".equals(groupResult)) {
                startPositionList.add(matcher.start());
                endPositionList.add(matcher.end());
                expressionMatchslist.add(groupResult);
            }
        }

        StringBuffer newExpression = new StringBuffer();
        int beginIndex = 0;

        // 转换表达式
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

    public static String transformExpression2(String expression) {
        // 去除空格
        StringBuffer sb = new StringBuffer();

        // 上一个字符
        char previous = '\u0000';
        // 是否在双引号内
        boolean isInsideQuotes = false;

        for (char ch : expression.toCharArray()) {
            switch (ch) {
                case '\"':
                    // 非转义符引号才标记
                    if (previous != '\\') {
                        isInsideQuotes = !isInsideQuotes;
                    }
                    sb.append(ch);
                    break;
                case ' ':
                    // 双引号内保留空格
                    if (isInsideQuotes) {
                        sb.append(ch);
                    }
                    break;
                case '|':
                    if (previous == '|') {
                        sb.append("')||response.contains('");
                    } else if (previous == '\\') {
                        sb.append(ch);
                    }
                    break;
                case '&':
                    if (previous == '&') {
                        sb.append("')&&response.contains('");
                    } else if (previous == '\\') {
                        sb.append(ch);
                    }
                    break;
                case '!':
                    if (previous != '\\') {
                        sb.append("')!response.contains('");
                    } else {
                        sb.append(ch);
                    }
                    break;
                case '\\':
                    if (previous != '\\') {
                        sb.append(ch);
                    }
                    break;
                default:
                    sb.append(ch);
                    break;
            }
            previous = ch;
        }

        String groovyExpression = sb.toString() + "')";
        if (expression.startsWith("!")) {
            groovyExpression = groovyExpression.substring(2);
        } else {
            groovyExpression = "response.contains('" + groovyExpression;
        }

        return groovyExpression;
    }

    /**
     * groovy脚本执行
     *
     * @param binding    Binding对象
     * @param expression groovy代码
     * @return 执行结果
     */
    public static Object eval(Binding binding, String expression) {
        GroovyShell groovyShell = new GroovyShell(binding);
        return groovyShell.evaluate(expression);
    }

}
