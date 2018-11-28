# jmeter-plugins
#### 项目介绍
本人在使用jmeter做自动化接口测试时编写的一系列插件。

#### 使用步骤：
1. 编译打包：`mvn clean assembly:assembly`。
2. 将编译好的jar包放至jmeterHome/lib/ext目录下。

#### 项目包含以下jmeter插件：
- Configs [详细](https://github.com/YeKelvin/jmeter-plugins/tree/master/common-util)
    - ENV Data Set：根据 .env 文件加载测试环境配置信息，脚本中通过${key}占位符引用
    - CSV Data Set In Script：把csv文件内容直接维护在脚本中，用于枚举遍历测试
    - Traverse Empty Value：通过设置线程无限循环，遍历报文字段做非空校验，遍历完毕线程自动停止

- Java Simples [详细](https://github.com/YeKelvin/jmeter-plugins/tree/master/simples)
    - dubbo-telnet：Dubbo接口测试
    - dubbo-telnet-ver2：Dubbo接口测试，报文维护在文件中，可基于复用原则维护脚本（暂时可用性不高）
    - dubbo-zookeeper：Dubbo性能测试

- Visualizers [详细](https://github.com/YeKelvin/jmeter-plugins/tree/master/visualizers)
    - Extent Html Report：记录所有simple的测试数据和结果至本地单html文件

- Functions [详细](https://github.com/YeKelvin/jmeter-plugins/tree/master/functions)
    - JmeterHome：获取jmeterHome路径