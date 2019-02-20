# dubbo-zookeeper
### 插件说明
Dubbo性能测试插件。

该插件依赖spring、dubbo、zookeeper等依赖，把jmeter作为dubbo consumer连接zookeeper调用接口。

### 使用说明
1. 将所需依赖jar包放入ext目录，为便于维护建议在ext目录下新建一个spring文件夹，依赖包放入`jmeterHome/lib/ext/spring`中；
2. 新建 `config/spring/consumer`文件夹和 `config/spring/dubbo.properties`文件；
3. consumer文件夹里放 consumer-xxx.xml文件（dubbo 消费者配置）；
4. 编辑 dubbo.properties文件：
    ```
    zookeeper.url=xxx.xxx.xxx.xxx:0000 
    dubbo.timeout=20000
    ```
5. 在 jmeter GUI中测试计划（TestPlan）最下方 Add directory or jar to classpath添加`../lib/ext/spring`和`../config/spring`；
6. 在 jmeter GUI中右键线程组（Thread）添加-Sampler-Java Sample；
7. 在 Java Sample GUI中类名称选择 `org.apache.jmeter.samplers.DubboZK`；

#### 插件参数说明
- classFullName为 Api类名，包含包名，如 `packageName.className`；
- methodName为 Api类的方法名；
- params为请求报文；
- expection为预期结果，true或 false。

### 依赖列表
- 所测项目的dubbo Api包
- aopalliance.jar
- apache-mime4j-core.jar
- classmate.jar
- commons.jar
- commons-io.jar
- commons-lang3.jar
- commons-logging.jar
- commons-net.jar
- dubbo.jar
- geronimo-activation.jar
- geronimo-stax-api.jar
- guava.jar
- hibernate-validator.jar
- javassist.jar
- jaxen.jar
- jboss-logging.jar
- log4j.jar
- lombok.jar
- netty.jar
- slf4j-api.jar
- slf4j-nop.jar
- spring-aop.jar
- spring-beans.jar
- spring-context.jar
- spring-context-support.jar
- spring-core.jar
- spring-expression.jar
- spring-web.jar
- validation-api.jar
- zkclient.jar
- zookeeper.jar

### 截图
![dubbo-zk.png](https://i.loli.net/2019/02/20/5c6d087fdd397.png)
