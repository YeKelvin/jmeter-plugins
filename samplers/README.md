# Samplers
## 1 DubboTelnetSampler
#### 1.1 插件说明
Dubbo接口测试插件。报文可维护在Json文件中，可基于复用原则维护脚本。

#### 1.2 使用说明
a. 在 jmeter GUI中右键线程组（Thread）添加-Sampler；  
b. 在 Java Sample GUI中类名称选择 `org.apache.jmeter.samplers.DubboTelnetSampler`；  

#### 1.3 插件参数说明
a. address为服务器telnet地址，格式为： `ip:port`；  
b. interface为接口名，一般类似 `className.methodName`；  
c. params为请求报文；  
d. expection为预期结果，判断逻辑是根据接口响应报文是否包含 expection的值来设置该 sample成功或失败。  

#### 1.4 截图
