# Visualizers
## 1、 LocalHtmlReport
### 1.1、 插件说明
- 收集所有sampler数据保存至html文件中
- 批量执行多个脚本时可以把不同脚本的的数据保存至同一个html文件，方便查看所有测试数据

### 1.2、 使用说明
- 在`测试计划`下添加`HTML 报告`（监听器，与线程组同级，建议添加至线程组前面） 

### 1.3、 参数说明
- `报告名称`： html文件名称
- `追加写报告`： 是否追加写报告（文件名一致的前提下），如果选择true，每次写报告都添加在原报告的最后添加数据，如果选择false则每次都覆盖原报告

### 1.4、 截图
![LocalHtmlReport001](https://github.com/YeKelvin/jmeter-plugins/blob/master/docs/images/LocalHtmlReport_001.png)
![LocalHtmlReport002](https://github.com/YeKelvin/jmeter-plugins/blob/master/docs/images/LocalHtmlReport_002.png)
![LocalHtmlReport003](https://github.com/YeKelvin/jmeter-plugins/blob/master/docs/images/LocalHtmlReport_003.png)
![LocalHtmlReport004](https://github.com/YeKelvin/jmeter-plugins/blob/master/docs/images/LocalHtmlReport_004.png)
