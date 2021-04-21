# Samplers
## 1、DubboTelnet取样器(DubboTelnetSampler)
### 1.1、插件说明
- Dubbo接口插件，通过Telnet方式调用Dubbo接口
- Dubbo泛化调用推荐官方插件： `https://github.com/thubbo/jmeter-plugins-for-apache-dubbo`

### 1.2、使用说明
- 在`线程组`下添加`DubboTelnet取样器`

### 1.3、参数说明
- `服务器地址`： 必填，Dubbo服务地址 host:port
- `接口名称`： 必填，接口名称 package.class.interface
- `预期结果`： true | false
- `字符编码`： 选填，默认utf-8
- `请求报文`： 必填，json
- `SSH`： 是否使用ssh端口转发

### 1.4、截图
![DubboTelnetSampler](https://github.com/YeKelvin/jmeter-plugins/blob/master/docs/images/DubboTelnetSampler_001.png)


## 2、JMeterScript取样器(JMeterScriptSampler)
### 2.1、插件说明
- 可在当前脚本中执行指定位置的JMeter脚本并获取执行结果，当前脚本和目标脚本可传递变量

### 2.2、使用说明
- 在`线程组`下添加`JMeterScript取样器`

### 2.3、参数说明
- `脚本目录`： JMeter脚本所在目录路径
- `脚本名称`： JMeter脚本名称
- `同步增量vars至props`： 将目标脚本中新增的局部变量同步至全局变量中
- `同步vars至目标脚本`： 将调用者的局部变量同步至目标脚本中（不会覆盖目标脚本中已存在的key），执行结束时将目标脚本新增的局部变量返回给调用者

### 2.4、截图
![JMeterScriptSampler](https://github.com/YeKelvin/jmeter-plugins/blob/master/docs/images/JMeterScriptSampler_001.png)
