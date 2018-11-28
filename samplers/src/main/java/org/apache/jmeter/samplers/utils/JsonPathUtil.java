package org.apache.jmeter.samplers.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.slf4j.Logger;
import pers.kelvin.util.log.LogUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pers.kelvin.util.json.JsonUtil.*;

/**
 * User: KelvinYe
 * Date: 2018-03-27
 * Time: 11:13
 */
public class JsonPathUtil {
    private static final Logger logger = LogUtil.getLogger(JsonPathUtil.class);

    private Gson gson = new Gson();

    /**
     * 判断jsonPath在json中是否存在
     */
    public Boolean isExisted(DocumentContext ctx, String jsonPath) {
        try {
            ctx.read(jsonPath);
            return true;
        } catch (PathNotFoundException e) {
            logger.debug("{ {} }jsonPath不存在", jsonPath);
            return false;
        }
    }

    /**
     * 判断jsonPath是否为 dot-notation（点节点表达式）
     */
    public Boolean isDotNotation(String jsonPath) {
        return jsonPath.contains(".");
    }

    /**
     * 判断jsonPath是否为 bracket-notation（括号节点表达式）
     */
    public Boolean isBracketNotation(String jsonPath) {
        return !jsonPath.contains(".");
    }

    /**
     * 判断jsonPath是否为 根节点
     */
    public Boolean isRootNode(String jsonPath) {
        String rootNode = "(^\\$$)|" + // $
                "(^\\$\\.\\*$)|" + // $.*
                "(^\\$\\.\\[\\*\\]$)|" + // $.[*]
                "(^\\$\\[\\*\\]$)"; // $[*]
        return Pattern.matches(rootNode, jsonPath);
    }

    /**
     * 获取当前path节点的父节点的path（目前只支持点节点表达式）
     *
     * @return 父节点jsonPath
     */
    public String getParentPath(String jsonPath) {
        String or = "|";
        String lastNode = "([.][\\w\\*]+$)" + or +// $.aa.bb
                "([.][\\[][\\d\\*\\-,:]+[\\]]$)" + or +// $.aa.[0]
                "([\\[][\\d\\*\\-,:]+[\\]]$)";// $.aa.bb[0]
        String parentPath = "";

        if (isRootNode(jsonPath)) {
            parentPath = "$";
        } else {
            Matcher m = Pattern.compile(lastNode).matcher(jsonPath);
            if (m.find()) {
                parentPath = m.replaceAll("");
                logger.debug("parentPath={}", parentPath);
            } else {
                logger.warn("{ {} }获取parentPath失败", jsonPath);
            }
        }
        return parentPath;
    }

    /**
     * 根据jsonPath获取keyname（仅针对jsonObject）
     */
    public String getKeyName(String jsonPath) {
        if (isDotNotation(jsonPath)) {
            return getKeyNameByDot(jsonPath);
        } else if (isBracketNotation(jsonPath)) {
            return getKeyNameByBracket(jsonPath);
        } else {
            return null;
        }
    }

    private String getKeyNameByDot(String jsonPath) {
        String[] paths = jsonPath.split("\\.");
        return paths[paths.length - 1];
    }

    private String getKeyNameByBracket(String jsonPath) {
        String[] paths = jsonPath.split("\\[|]");
        return paths[paths.length - 1];
    }

    /**
     * 判断json结构类型
     *
     * @return OBJECT | ARRAY | UNKNOWN
     */
    public String getJsonType(String json) {
        if (isObjectJson(json)) {
            return "OBJECT";
        } else if (isArrayJson(json)) {
            return "ARRAY";
        } else {
            return "UNKNOWN";
        }
    }

    /**
     * 根据jsonPath更新节点的值，不存在则插入
     */
    private void updateNode(DocumentContext ctx, String jsonPath, Object value) {
        if (isExisted(ctx, jsonPath)) {
            // 如当前path存在则直接更新value
            ctx.set(jsonPath, value);
        } else {
            // 当前path不存在则插入，需先判断 Object 还是 Array
            String parentPath = getParentPath(jsonPath);
            String parentJsonType = "";
            Boolean isParentPathNotBlank = !(parentPath == null || parentPath.isEmpty());

            if (isParentPathNotBlank && isExisted(ctx, parentPath)) {
                String parentJson = ctx.read(parentPath).toString();
                parentJsonType = getJsonType(parentJson);
                logger.debug("parentJsonType={}", parentJsonType);

                if ("OBJECT".equals(parentJsonType)) {
                    ctx.put(parentPath, getKeyName(jsonPath), value);
                } else if ("ARRAY".equals(parentJsonType)) {
                    ctx.add(parentPath, value);
                } else {
                    logger.warn("parentJsonType未知，updateNode失败");
                }
            } else {
                logger.warn("{ {} }的parentPath不存在，updateNode失败",jsonPath);
            }
        }
        logger.debug("{ {} }节点执行完毕", jsonPath);
    }

    /**
     * 更新或新增json节点
     *
     * @param jsonPaths    从jmeter.jsonPaths入參
     * @param templateJson 基础json模版
     * @return 更新完成的jsonString
     */
    public String updateJson(String jsonPaths, String templateJson) {
        jsonPaths = toObjectJson(jsonPaths);
        templateJson = toArrayJson(templateJson);

        if (jsonPaths == null || jsonPaths.isEmpty()) {
            logger.debug("jsonPaths为空，直接返回templateJson");
            return templateJson;
        } else {
            Configuration conf = Configuration.defaultConfiguration();
            DocumentContext ctx = JsonPath.using(conf).parse(templateJson);

            Type type = new TypeToken<HashMap<String, Object>>() {
            }.getType();
            HashMap<String, Object> jsonPathMap = gson.fromJson(jsonPaths, type);

            for (Map.Entry<String, Object> path : jsonPathMap.entrySet()) {
                String jsonPath = path.getKey();
                Object value = path.getValue();
                logger.debug("开始执行，jsonPath={}，value={}", jsonPath, value);
                updateNode(ctx, jsonPath, value);
            }
            logger.debug("Json报文更新完成");
            String jsonStr = ctx.jsonString();
            return jsonStr.substring(1, jsonStr.length() - 1);
        }
    }

}
