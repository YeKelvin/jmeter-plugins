# Samplers
## 1 DubboTelnet
#### 1.1 插件说明
Dubbo接口测试插件。

#### 1.2 使用说明
a. 在 jmeter GUI中右键线程组（Thread）添加-Sampler-Java Sample；  
b. 在 Java Sample GUI中类名称选择 `org.apache.jmeter.samplers.DubboTelnet`；  

#### 1.3 插件参数说明
a. address为服务器telnet地址，格式为： `ip:port`；  
b. interface为接口名，一般类似 `className.methodName`；  
c. params为请求报文；  
d. expection为预期结果，判断逻辑是根据接口响应报文是否包含 expection的值来设置该 sample成功或失败。  

#### 1.4 截图
![dubbo-telnet.png](https://i.loli.net/2019/02/20/5c6cf1e6222aa.png)

## 2 DubboTelnetByFile
#### 2.1 插件说明
Dubbo接口测试插件，报文维护在文件中，可基于复用原则维护脚本。

#### 2.2 使用说明
a. 在 jmeter GUI中右键线程组（Thread）添加-Sampler-Dubbo Telnet By File；  

#### 2.3 参数说明
...

#### 2.4 截图
![dubbo-telnet-by-file.png](https://i.loli.net/2019/03/05/5c7e22dbec283.png)