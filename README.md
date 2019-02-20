# jmeter-plugins
#### 项目介绍
jmeter 用于自动化接口测试的一系列插件。

#### 打包：
1. 编译打包：`mvn clean assembly:assembly`。
2. 将编译好的jar包放至 `jmeterHome/lib/ext`目录下。

#### 插件介绍：
- Configs [查看详细](https://github.com/YeKelvin/jmeter-plugins/tree/master/configs)

  - ENV Data Set：根据 .env后缀文件加载测试环境配置信息，脚本中通过${key}占位符引用。
  - CSV Data Set In Script：把csv文件内容直接维护在脚本中，用于枚举遍历测试，遍历完毕后线程自动停止。
  - Traverse Empty Value：用于遍历报文字段做非空校验，遍历完毕后线程自动停止。

- Samplers [查看详细](https://github.com/YeKelvin/jmeter-plugins/tree/master/samplers)

  - DubboTelnet：Dubbo接口测试插件。
  - DubboTelnetByFile：Dubbo接口测试插件，报文维护在文件中，可基于复用原则维护脚本（待优化）。

- Visualizers [查看详细](https://github.com/YeKelvin/jmeter-plugins/tree/master/visualizers)

  - Local Html Report：收集和记录所有 sample的数据保存至本地html文件。

- Functions [查看详细](https://github.com/YeKelvin/jmeter-plugins/tree/master/functions)

  - JmeterHome：获取jmeterHome路径。

- Dubbo-Zookeeper [查看详细](https://github.com/YeKelvin/jmeter-plugins/tree/master/dubbo-zookeeper)

  - Dubbo性能测试插件。

- common-util [查看详细](https://github.com/YeKelvin/jmeter-plugins/tree/master/common-util)

  - 一系列公共函数，用于给 jmeter中 groovy或 beanshell脚本调用。
