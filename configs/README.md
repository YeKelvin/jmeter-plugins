# Configs
## 1、环境变量配置器(EnvDataSet)
### 1.1、插件说明
- 根据`.yaml`配置文件加载测试环境变量，脚本中通过${key}占位符引用
- 将测试环境变量的键值对写入配置文件中，如服务器地址、数据库配置等；目的是提高脚本的可移植性和可维护性
- 如果存在多个测试环境，只需增加多个对应环境的配置文件（键名一致），执行时只需选择对应名称的配置文件即可

### 1.2、使用说明
- 在`JMeter根目录`下新建`config`文件夹
- 在`config文件夹`下新建`.yaml`配置文件
- 在`.yaml`配置文件中添加变量键值对，e.g.:
  ```yaml
  # uat.yaml
  http.host: xxx.xxx 
  http.port: 443
  db.url: jdbc
  db.username: xxx
  db.password: xxx
  ...
  ```
- 在`测试计划`下添加`环境变量配置器`（配置元件，与线程组同级，建议添加至线程组前面）
- 在`环境变量配置器`中选择对应的配置文件
- 在需要使用环境变量的地方通过占位符 `${keyName}`引用即可

### 1.3、截图
![EnvDataSet](https://github.com/YeKelvin/jmeter-plugins/blob/master/docs/images/EnvDataSet_001.png)


## 2、失败请求保存器(FailureResultSaver)
### 2.1、插件说明
- 用于性能测试时，把失败的请求数据单独保存下来，方便定位问题

### 2.2、使用说明
- 在`测试计划`下添加`失败请求保存器`（配置元件，与线程组同级，建议添加至线程组前面）
- 脚本执行过程中，如果取样器断言失败，则会把取样器的请求和响应数据保存至指定路径的日志文件中
  
### 2.3、参数说明
- `日志路径`： 自定义日志文件路径
- `错误分类`： 仅适用于Json报文，通过JsonPath表达式获取ResponseData的错误码，把错误码分组为不同的日志文件，日志文件名称以错误码命名，目的是方便统计和分类错误类型，为空时不做分类
- `排除指定错误`： 指定需要排除的错误码，如果ResponseData包含该错误码，则不输出至日志文件中

### 2.4、截图
![FailureResultSaver](https://github.com/YeKelvin/jmeter-plugins/blob/master/docs/images/FailureResultSaver_001.png)


## 3、HTTP请求头读取器(HTTPHeaderReader)
### 3.1、插件说明
- `HTTP信息头管理器`的文件版，根据`.yaml`配置文件加载请求头
- 当存在多个`HTTP请求头读取器`且有父子关系时，读取优先级和继承逻辑与`HTTP信息头管理器`一致，不同的是数据来源于配置文件
- 开发该插件的目的是为了提高请求头的可复用性和可维护性，系统在迭代的过程中或多或少会增删改请求头，JMeter内建的`HTTP信息头管理器`只能在脚本中维护请求头，如果已经维护了大量的脚本，此时系统发展需要增删改请求头，那么修改脚本需要花费大量的时间，但是改用该插件后，只需修改配置文件即可轻松达到上述效果，节省大量时间和精力且不易出错

### 3.2、使用说明
- 在`JMeter根目录`下新建`header`文件夹
- 在`header文件夹`下新建`.yaml`请求头配置文件
- 在`.yaml`配置文件中添加请求头键值对，e.g.:
  ```yaml
  # headers.yaml
  Accept-Language: zh-CN,zh;
  Content-Type: application/json;charset=UTF-8
  User-Agent: AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36
  ...
  ```
- 可在`测试计划`、`线程组`或`HTTP取样器`下添加`HTTP请求头读取器`（配置元件）
- 在`HTTP请求头读取器`中选择对应的请求头配置文件
- 在执行HTTP请求前会把所有请求头合并后添加至当前HTTP的请求头中

### 3.3、截图
![HTTPHeaderReader](https://github.com/YeKelvin/jmeter-plugins/blob/master/docs/images/HTTPHeaderReader_001.png)


## 4、SSH配置器(SSHConfiguration)
### 4.1、插件说明
- 配置ssh，多用于内网跳板机端口转发，目的是本地直连跳板机后的内部服务

### 4.2、使用说明
- 在`测试计划`下添加`SSH配置器`（配置元件，与线程组同级，建议添加至线程组前面）

### 4.3、参数说明
- `SSH地址`： host:port
- `SSH用户名称`： username
- `SSH密码`： password
- `启用本地端口转发`： true or false
- `本地转发端口`： local port
- `远程地址`： remote host:port

### 4.4、截图
![SSHConfiguration](https://github.com/YeKelvin/jmeter-plugins/blob/master/docs/images/SSHConfiguration_001.png)


## 5、数据遍历配置器(TraversalDataSet)
### 5.1、插件说明
- 用于枚举遍历测试，完成遍历后线程自动停止线程

### 5.2、使用说明
- 将`线程组`的`循环次数`设置为`永远`，当完成遍历后会自动停止，无需手动计算循环次数
- 在`线程组`下添加`数据遍历配置器`（配置元件，建议排在线程组下的第一位）
- 在使用枚举的地方把枚举值替换为自定义的`变量名称`的占位符

### 5.3、参数说明
- `变量名称`： 变量名称，有多个时用“,”逗号分隔
- `数据集`： 变量值，与CSV格式一致

### 5.4、截图
![TraversalDataSet001](https://github.com/YeKelvin/jmeter-plugins/blob/master/docs/images/TraversalDataSet_001.png)
![TraversalDataSet002](https://github.com/YeKelvin/jmeter-plugins/blob/master/docs/images/TraversalDataSet_002.png)
![TraversalDataSet003](https://github.com/YeKelvin/jmeter-plugins/blob/master/docs/images/TraversalDataSet_003.png)


## 6、空值遍历配置器(TraversalEmptyValue)
### 6.1、插件说明
- 用于遍历报文字段做非空校验，完成遍历后线程自动停止线程

### 6.2、使用说明
- 将`线程组`的`循环次数`设置为`永远`，当完成遍历后会自动停止，无需手动计算循环次数
- 在`线程组`下添加`空值遍历配置器`（配置元件，建议排在线程组下的第一位）
- 请求执行前会把当次请求报文赋值给`params`变量，单次空值校验预期结果赋值给`expression`变量，在脚本请求报文的位置使用函数`${__eval(${params})}`替换原来的请求内容，在请求断言处使用占位符`${expression}`替换原来的断言

### 6.3、参数说明
- `空类型`： 空值是`null`还是`""`
- `请求报文`： 请求报文
- `预期结果`： 结果表达式

### 6.4、截图
![TraversalEmptyValue001](https://github.com/YeKelvin/jmeter-plugins/blob/master/docs/images/TraversalEmptyValue_001.png)
![TraversalEmptyValue002](https://github.com/YeKelvin/jmeter-plugins/blob/master/docs/images/TraversalEmptyValue_002.png)
![TraversalEmptyValue003](https://github.com/YeKelvin/jmeter-plugins/blob/master/docs/images/TraversalEmptyValue_003.png)
