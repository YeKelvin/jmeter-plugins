# Functions
## 1、 ExtractSQLValue
### 1.1、 函数说明
- 根据列名提取数据库表第一行的值

### 1.2、 使用说明
```
/**
 * @param tableName 必填，表名
 * @param columnName 必填，列名
 * @param defaultValue 必填，默认值
 * @param variable 选填，存在时把结果存入vars变量中
 *
 * @return sql值
 */
${__ExtractSQLValue(tableName, columnName, defaultValue, variable)}
```


## 2、 GoogleAuth
### 2.1、 函数说明
- 谷歌动态认证码

### 2.2、 使用说明
```
/**
 * @param secretKey 必填，秘钥
 *
 * @return 认证码
 */
${__GoogleAuth(secretKey)}
```


## 3、 JmeterHome
### 3.1、 函数说明
- 获取JMeter根目录的绝对路径
- 如果有入参则把参数拼接在路径后

### 3.2、 使用说明
```
${__JmeterHome}
${__JmeterHome()}
${__JmeterHome(pathA)}
${__JmeterHome(pathA, pathB, fileName)}

// 在BeanShell中引入bsh文件
source("${__JmeterHome(beanshell, xxx.bsh)}");
```


## 4、 MD5
### 4.1、 函数说明
- MD5加密

### 4.2、 使用说明
```
/**
 * @param plaintext 必填，明文
 * @param md5Key 选填，秘钥
 * @param encode 选填，编码
 * @param variable 选填，存在时把结果存入vars变量中
 *
 * @return md5加密字符串
 */
${__MD5(plaintext, md5Key, encode, variable)}
```


## 5、 ExtractPrevResponse
### 5.1、 函数说明
- 根据JsonPath表达式提取上一个SamplerResponse的Json值

### 5.2、 使用说明
```
/**
 * @param jsonPath 必填，JsonPath表达式
 * @param variable 选填，存在时把结果存入vars变量中
 *
 * @return json值
 */
${__ExtractPrevResponse(jsonPath, variable)}
```


## 6、 RIdCard
### 6.1、 函数说明
- 随机生成国内身份证号

### 6.2、 使用说明
```
/**
 * @param variable 选填，存在时把结果存入vars变量中
 *
 * @return 随机国内身份证号
 */
${__RIdCard(variable)}
```


## 7、 RMobile
### 7.1、 函数说明
- 随机生成国内手机号

### 7.2、 使用说明
```
/**
 * @param variable 选填，存在时把结果存入vars变量中
 *
 * @return 随机国内手机号
 */
${__RMobile()}
```


## 8、 RNumber
### 8.1、 函数说明
- 随机生成数字

### 8.2、 使用说明
```
/**
 * @param pattern 必填，随机数长度或随机数生成规则
 * @param variable 选填，存在时把结果存入vars变量中
 *
 * @return 随机数
 */
${__RNumber(pattern, variable)}
${__RNumber(8, variable)}   // 123456789
${__RNumber(str:8)}         // str12345678
${__RNumber(8:str)}         // 12345678str
${__RNumber(str:8:str)}     // str12345678str
${__RNumber(8:str:8)}       // 12345678str12345678
${__RNumber(\8:8)}          // 812345678
```


## 9、 ScriptAbsPath
### 9.1、 函数说明
- 获取JMeter脚本所在目录的绝对路径
- 如果有入参则把参数拼接在路径后

### 9.2、 使用说明
```
${__ScriptAbsPath}
${__ScriptAbsPath()}
${__ScriptAbsPath(pathA)}
${__ScriptAbsPath(pathA, pathB, fileName)}
```
