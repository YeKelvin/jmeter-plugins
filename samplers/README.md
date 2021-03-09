# Samplers
## 1、DubboTelnet取样器(DubboTelnetSampler)
### 1.1、插件说明
- Dubbo接口插件，通过Telnet方式调用Dubbo接口

### 1.2、使用说明
a. 在 jmeter GUI中右键线程组（Thread）添加-Sampler；  
b. 在 Java Sample GUI中类名称选择 `org.apache.jmeter.samplers.DubboTelnetSampler`；  

### 1.3、参数说明
- `服务器地址`： 
- `接口名称`： 
- `预期结果`： 
- `字符编码`： 
- `请求报文`： 
- `SSH`： 

### 1.4、截图
![DubboTelnetSampler](https://github.com/YeKelvin/jmeter-plugins/blob/master/docs/images/DubboTelnetSampler_001.png)


## 2、JMeterScript取样器(JMeterScriptSampler)
### 2.1、插件说明
- 可在当前脚本中执行外部JMeter脚本并获取执行结果，当前脚本和外部脚本可传递变量

### 2.2、使用说明

### 2.3、参数说明
- `脚本目录`： 
- `脚本名称`： 
- `增量同步vars至props`： 
- `同步vars至子脚本`： 

### 2.4、截图

