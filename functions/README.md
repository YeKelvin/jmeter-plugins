# Functions
## 1 JmeterHome
#### 1.1 函数说明
获取JmeterHome路径。

如有入參，则把入參当做子路径拼接在JmeterHome路径后。

返回的路径分隔符固定为 `/` 。

#### 1.2 用法
```
${__JmeterHome}
${__JmeterHome()}
${__JmeterHome(beanshell)}
${__JmeterHome(beanshell, xxx.bsh)}
```

#### 1.3 作用
目前主要用于Beanshell脚本引用外部beanshell脚本时的绝对路径引用。

Beanshell脚本存放路径默认为 JmeterHome/beanshell 。

在脚本中引用外部脚本代码如下：
```
source("${__JmeterHome(beanshell, xxx.bsh)}");
```

#### 1.4 目的
通过绝对路径引用脚本，比较于使用相对路径时，可减少因工作路径的变化或不确定性而引起的脚本路径错误，使用绝对路径一劳永逸。
