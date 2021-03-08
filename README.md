# jmeter-plugins
## 一、项目介绍
一些自用的JMeter插件，可提高脚本的可维护性，可复用性，可读性和编写效率。

## 二、打包和安装
1. 编译打包：`mvn clean assembly:assembly`。
2. 将编译好的jar包放至 `jmeterHome/lib/ext`目录下。

## 三、JMeter版本说明
该项目所有JMeter插件均在5.1.1版本上开发，理论上5.x版本均可正常使用，如使用过程中出现问题请使用5.1.1版本或联系我

## 四、插件介绍
### Configs [→](https://github.com/YeKelvin/jmeter-plugins/tree/master/configs)
- **环境变量配置器(EnvDataSet):** 根据`.yaml`配置文件加载测试环境变量，脚本中通过${key}占位符引用
- **失败请求保存器(FailureResultSaver):** 用于性能测试时，把失败的请求数据单独保存下来，方便定位问题
- **HTTP请求头读取器(HTTPHeaderReader):** HTTP信息头管理器的文件版，根据`.yaml`配置文件加载请求头
- **SSH配置器(SSHConfiguration):** 配置ssh，多用于内网跳板机端口转发，目的是本地直连跳板机后的内部服务
- **数据遍历配置器(TraversalDataSet):** 用于枚举遍历测试，完成遍历后线程自动停止线程
- **空值遍历配置器(TraversalEmptyValue):** 用于遍历报文字段做非空校验，完成遍历后线程自动停止线程

### Samplers [→](https://github.com/YeKelvin/jmeter-plugins/tree/master/samplers)
- **DubboTelnet取样器(DubboTelnetSampler):** Dubbo接口插件，通过Telnet方式调用Dubbo接口
- **JMeterScript取样器(JMeterScriptSampler):** 可在当前脚本中执行外部JMeter脚本并获取执行结果，当前脚本和外部脚本可传递变量，该插件是提高脚本复用性的大杀器

### Visualizers [→](https://github.com/YeKelvin/jmeter-plugins/tree/master/visualizers)
- **LocalHtmlReport(HTML报告):** 收集所有sampler数据保存至html文件中

### Functions [→](https://github.com/YeKelvin/jmeter-plugins/tree/master/functions)
- **{__ExtractSQLColumn()}:** 根据列名提取数据库表第一行的值
- **{__GoogleAuth()}:** 谷歌动态认证码
- **{__JmeterHome()}:** 获取JMeter根目录路径
- **{__MD5()}:** MD5加密
- **{__PrevResJson()}:** 根据JsonPath表达式提取上一个SamplerResponse的Json值
- **{__RIdCard()}:** 随机生成国内身份证号
- **{__RMobile()}:** 随机生成国内手机号
- **{__RNumber()}:** 随机生成数字
- **{__ScriptAbsPath()}:** 获取JMeter脚本所在的目录路径

### Dubbo-Zookeeper [→](https://github.com/YeKelvin/jmeter-plugins/tree/master/dubbo-zookeeper)
- **DubboZookeeperSampler:** DubboZookeeper插件（年久失修了，直接用Dubbo官方推荐的JMeter插件吧）

### common-util [→](https://github.com/YeKelvin/jmeter-plugins/tree/master/common-util)
- 平时写脚本时常用的方法
