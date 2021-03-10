# BeanShell
## beanshell脚本依赖bsh脚本的方法
```java
// bsh路径：xx/jmeterHome/beanshell/xxx.bsh

source("${__JmeterHome(beanshell, assert.bsh)}");
source("${__JmeterHome(beanshell, sql.bsh)}");
```


## assert.bsh
- 常用断言方法
```java
source("${__JmeterHome(beanshell, assert.bsh)}");
 
assertSQLResultSize("TABLE_NAME", 1);
assertEquals(source, expection);
assertNotEquals(source, expection);
assertTrue(boolean);
assertFalse(boolean);
assertNotNull(source);
assertNull(source);
 
String errorMsg = "测试失败了";
assertSQLResultSize("TABLE_NAME", 1, errorMsg);
assertEquals(source, expection, errorMsg);
assertNotEquals(source, expection, errorMsg);
assertTrue(boolean, errorMsg);
assertFalse(boolean, errorMsg);
assertNotNull(source, errorMsg);
assertNull(source, errorMsg);
```


## sql.bsh
- 对获取数据库表结果的方法封装
```java
source("${__JmeterHome(beanshell, sql.bsh)}");

String value = getTableValue("tableName", "columnName");
String valueWithQuotes = getTableValue("tableName", "columnName", "defualtValue", true);
String count = getTableCount("tableName");
```


## beanshell调试技巧
- 有时beanshell报错信息就只有一条异常，完全不知道哪里出错了，可以把脚本内容套一层try-catch ，能输出更多异常堆栈信息
```java
try {
    // 脚本内容写这里
}catch (Throwable ex) {
    log.error("", ex);
}
```

## bsh脚本source依赖
```java
import org.apache.jmeter.util.JMeterUtils;
import java.io.File;

String bshHome = JMeterUtils.getJMeterHome() + File.separator + "beanshell";
source(bshHome  + File.separator + "xxx.bsh");
```