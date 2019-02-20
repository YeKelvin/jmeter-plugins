# Configs
## 1 ENV Data Set
#### 1.1 插件说明
根据 .env后缀文件加载测试环境配置信息，脚本中通过${key}占位符引用。

将测试环境信息类的配置变量化，如服务器地址、数据库等；目的是提高脚本的可移植性和可维护性，如测试环境更换后，只需新增一个对应的配置文件，脚本也只需修改配置文件名称即可复用。

#### 1.2 使用说明
a. 在 jmeter根目录新建 config文件夹；  
b. 在 config文件夹下新建 xxx.env文件；  
c. 编辑 xxx.env文件，格式为: `{"key1":"value1","key2":"value2",...}`；  
d. 在 jmeter GUI中右键测试计划（TestPlan）添加-配置元件-ENV Data Set；  
e. 在 ENV Data Set GUI中选择对应的ConfigName即可读取文件中自定义的变量；  
f. 在编写脚本时可通过占位符 `${keyName}`应用。  

#### 1.3 截图
![env-data-set.png](https://i.loli.net/2019/02/20/5c6cc7c6592c6.png)

## 2 CSV Data Set In Script
#### 2.1 插件说明
把csv文件内容直接维护在脚本中，用于枚举遍历测试，遍历完毕后线程自动停止。

目的是便于维护枚举数量少的字段，不用另外打开文件再维护，提高编写效率。

#### 2.2 使用说明
a. 在 jmeter GUI中右键线程组（Thread）添加-配置元件-CSV Data Set In Script；  
b. 在 CSV Data Set In Script GUI中填写 VariableNames和 Data；  
c. 设置线程组循环次数为永远，当遍历完所有枚举后会自动停止，无需手动计算循环次数。  

#### 2.3 截图
![csv-date-set-in-script.png](https://i.loli.net/2019/02/20/5c6cc9860db22.png)

## 3 Traverse Empty Value
#### 3.1 插件说明
用于遍历报文字段做非空校验，遍历完毕后线程自动停止。

#### 3.2 使用说明
a. 在 jmeter GUI中右键线程组（Thread）添加-配置元件-Traverse Empty Value；  
b. 在 Traverse Empty Value GUI中填写 Params和 EmptyCheckExpection；  
c. 设置线程组循环次数为永远，当遍历完所有字段后会自动停止，无需手动计算循环次数。  

#### 3.3 截图
![traverse-empty-value.png](https://i.loli.net/2019/02/20/5c6ccb6fb86ee.png)
