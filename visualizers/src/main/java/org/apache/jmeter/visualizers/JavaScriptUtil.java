package org.apache.jmeter.visualizers;

import javax.script.*;
import java.io.IOException;

/**
 * Description
 *
 * @author KelvinYe
 * Date     2019-01-30
 * Time     17:45
 */
public class JavaScriptUtil {

    public static void main(String[] args) throws IOException, ScriptException {
        // 获取 js引擎
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
        // 为文件注入全局变量
        Bindings bindings = engine.createBindings();
        // 设置绑定参数的作用域
        engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        String path = "F:\\Jmeter\\apache-jmeter-3.1\\htmlreport\\repor_test_testt.html";
        String jsContent = JsoupUtil.getScriptData(path);
        // 获得js文件
        engine.eval(jsContent, bindings);
        Object map = bindings.get("app");
        System.out.println(map.toString());
    }
}

