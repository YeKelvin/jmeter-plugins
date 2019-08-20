<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8" />
    <title>HTML Report</title>
    <script src="https://cdn.jsdelivr.net/npm/vue@2.5.22/dist/vue.min.js"></script>
    <script src="https://unpkg.com/element-ui@2.4.11/lib/index.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/echarts/4.2.1/echarts.min.js"></script>
    <script>
      !function(a){var e,c='<svg><symbol id="icon-close" viewBox="0 0 1024 1024"><path d="M806.4 172.8l-633.6 633.6c-12.8 12.8-12.8 32 0 44.8 12.8 12.8 32 12.8 44.8 0l633.6-633.6c12.8-12.8 12.8-32 0-44.8-12.8-12.8-32-12.8-44.8 0z" fill="#333333" ></path><path d="M172.8 172.8c-12.8 12.8-12.8 32 0 44.8l633.6 633.6c12.8 12.8 32 12.8 44.8 0 12.8-12.8 12.8-32 0-44.8L217.6 172.8c-12.8-12.8-32-12.8-44.8 0z" fill="#333333" ></path></symbol><symbol id="icon-pass" viewBox="0 0 1024 1024"><path d="M512 512m-477.86666667 0a477.86666667 477.86666667 0 1 0 955.73333334 0 477.86666667 477.86666667 0 1 0-955.73333334 0Z" fill="#029E4A" ></path><path d="M830.61333333 364.69333333l-406.4 406.4c-13.22666667 13.22666667-34.98666667 13.22666667-48.32 0-13.22666667-13.22666667-13.22666667-34.98666667 0-48.32l406.4-406.4c13.22666667-13.22666667 34.98666667-13.22666667 48.32 0 13.22666667 13.33333333 13.22666667 34.98666667 0 48.32z" fill="#FFFFFF" ></path><path d="M240.64 541.86666667l181.86666667 181.86666666c13.22666667 13.22666667 13.22666667 34.98666667 0 48.32l-5.86666667 5.86666667c-10.02666667 10.02666667-26.56 10.02666667-36.58666667 0l-187.73333333-187.73333333c-13.22666667-13.22666667-13.22666667-34.98666667 0-48.32 13.33333333-13.22666667 34.98666667-13.22666667 48.32 0z" fill="#FFFFFF" ></path></symbol><symbol id="icon-failure" viewBox="0 0 1024 1024"><path d="M512 32C246.875 32 32 246.875 32 512s214.875 480 480 480 480-214.875 480-480S777.125 32 512 32z" fill="#E4393C" ></path><path d="M554.28125 520.53125L700.4375 666.59375c10.40625 10.40625 10.40625 27.28125 0 37.6875l-9.375 9.46875c-10.40625 10.40625-27.28125 10.40625-37.6875 0l-146.25-146.0625L361.0625 713.75c-10.40625 10.40625-27.28125 10.40625-37.6875 0l-9.46875-9.46875c-10.40625-10.40625-10.40625-27.28125 0-37.6875L460.0625 520.4375 313.90625 374.375c-10.40625-10.40625-10.40625-27.28125 0-37.6875l9.46875-9.46875c10.40625-10.40625 27.28125-10.40625 37.6875 0l146.15625 146.15625 146.15625-146.15625c10.40625-10.40625 27.28125-10.40625 37.6875 0l9.375 9.46875c10.40625 10.40625 10.40625 27.28125 0 37.6875L554.28125 520.53125z" fill="#FFFFFF" ></path></symbol></svg>',t=(e=document.getElementsByTagName("script"))[e.length-1].getAttribute("data-injectcss");if(t&&!a.__iconfont__svg__cssinject__){a.__iconfont__svg__cssinject__=!0;try{document.write("<style>.svgfont {display: inline-block;width: 1em;height: 1em;fill: currentColor;vertical-align: -0.1em;font-size:16px;}</style>")}catch(e){console&&console.log(e)}}!function(e){if(document.addEventListener)if(~["complete","loaded","interactive"].indexOf(document.readyState))setTimeout(e,0);else{var t=function(){document.removeEventListener("DOMContentLoaded",t,!1),e()};document.addEventListener("DOMContentLoaded",t,!1)}else document.attachEvent&&(n=e,l=a.document,i=!1,o=function(){i||(i=!0,n())},(c=function(){try{l.documentElement.doScroll("left")}catch(e){return void setTimeout(c,50)}o()})(),l.onreadystatechange=function(){"complete"==l.readyState&&(l.onreadystatechange=null,o())});var n,l,i,o,c}(function(){var e,t,n,l,i,o;(e=document.createElement("div")).innerHTML=c,c=null,(t=e.getElementsByTagName("svg")[0])&&(t.setAttribute("aria-hidden","true"),t.style.position="absolute",t.style.width=0,t.style.height=0,t.style.overflow="hidden",n=t,(l=document.body).firstChild?(i=n,(o=l.firstChild).parentNode.insertBefore(i,o)):l.appendChild(n))})}(window);
    </script>
    <link rel="stylesheet" type="text/css" href="https://unpkg.com/element-ui@2.4.11/lib/theme-chalk/index.css" />
    <style>
        html,
        body {
            font-family: 'Helvetica Neue', Helvetica, 'PingFang SC',
            'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
            width: 100%;
            height: 100%;
            background: #f5f5f5;
            overflow: hidden;
        }

        * {
            margin: 0;
            padding: 0;
        }

        .max-size {
            width: 100%;
            height: 100%;
        }

        .container {
            display: -webkit-box;
            display: -moz-box;
            display: -webkit-flex;
            display: -moz-flex;
            display: -ms-flexbox;
            display: flex;
        }

        .vertical {
            -webkit-flex-direction: column;
            -moz-flex-direction: column;
            -ms-flex-direction: column;
            -o-flex-direction: column;
            flex-direction: column;
        }

        .header {
            background: -webkit-linear-gradient(left, #319be9, #3448a1);
            background: -moz-linear-gradient(left, #319be9, #3448a1);
            background: -ms-linear-gradient(left, #319be9, #3448a1);
            background: -o-linear-gradient(left, #319be9, #3448a1);
            background: linear-gradient(left, #319be9, #3448a1);
            color: white;
            font-size: 20px;
            width: 100%;
            min-height: 60px;
            padding-left: 10px;

            -webkit-justify-content: space-between;
            -moz-justify-content: space-between;
            -ms-justify-content: space-between;
            -o-justify-content: space-between;
            justify-content: space-between;

            -webkit-align-items:center;
            -moz-align-items:center;
            -ms-align-items:center;
            -o-align-items:center;
            align-items: center;
        }

        .header-info {
            padding-right: 20px;
        }

        .header-info .el-tag {
            background-color: #ff3300dd;
        }

        .aside {
            background-color: white;
            color: #333;
            min-width: 300px;
            width: 400px;
            height: 100%;
            border: 1px solid #ebeef5;

            -webkit-justify-content: center;
            -moz-justify-content: center;
            -ms-justify-content: center;
            -o-justify-content: center;
            justify-content: center;

            -webkit-align-items:center;
            -moz-align-items:center;
            -ms-align-items:center;
            -o-align-items:center;
            align-items: center;
        }

        .main {
            background-color: white;
            color: #333;
            border: 1px solid #ebeef5;

            -webkit-justify-content: center;
            -moz-justify-content: center;
            -ms-justify-content: center;
            -o-justify-content: center;
            justify-content: center;

            -webkit-align-items:center;
            -moz-align-items:center;
            -ms-align-items:center;
            -o-align-items:center;
            align-items: center;
        }

        .icon {
            width: 1.5em;
            height: 1.5em;
            vertical-align: -0.15em;
            fill: currentColor;
            overflow: hidden;
        }

        .break-word-title {
            word-wrap: break-word;
            word-break: break-all;
        }

        .break-word {
            white-space: pre-wrap;
            word-wrap: break-word;
            word-break: break-all;
        }

        .unable-select {
            -moz-user-select: none;
            -webkit-user-select: none;
            -ms-user-select: none;
            -o-user-select: none;
            user-select: none;
        }

        .test-suite {
            overflow: auto;
        }

        .test-suite__header {
            margin: 10px;
            font-size: 15px;
            min-height: 20px;
            -webkit-justify-content: space-between;
            -moz-justify-content: space-between;
            -ms-justify-content: space-between;
            -o-justify-content: space-between;
            justify-content: space-between;
        }

        .test-suite__filter {
            border: 2px solid rgb(30, 144, 255);
            -webkit-border-radius: 10px;
            -moz-border-radius: 10px;
            -o-border-radius: 10px;
            border-radius: 10px;
            padding-left: 5px;
        }

        .test-suite__filter svg {
            margin-right: 5px;
            cursor: pointer;
        }

        .overview {
            cursor: pointer;
            border-top: 1px solid #ebeef5;
        }

        .overview:hover {
            background: #EBEEF5;
        }

        .test-suite_list {
            list-style-type: none;
            text-align: left;
        }

        .test-suite_list li {
            border-top: 1px solid transparent;
            border-bottom: 1px solid #ebeef5;
        }

        .test-suite_list li:first-child {
            border-top: 1px solid #ebeef5;
        }

        .test-suite_list li:hover {
            background: #EBEEF5;
        }

        .test-suite__title {
            margin: 10px;
            cursor: pointer;
        }

        .test-suite__time_and_status {
            -webkit-justify-content: space-between;
            -moz-justify-content: space-between;
            -ms-justify-content: space-between;
            -o-justify-content: space-between;
            justify-content: space-between;

            -webkit-align-items:center;
            -moz-align-items:center;
            -ms-align-items:center;
            -o-align-items:center;
            align-items: center;
        }

        .test-case {
            overflow: auto;
        }

        .test-case__header {
            margin: 5px 20px 5px 20px;
            font-size: 20px;
            min-height: 40px;
        }

        .test-case__time_and_filter {
            -webkit-justify-content: space-between;
            -moz-justify-content: space-between;
            -ms-justify-content: space-between;
            -o-justify-content: space-between;
            justify-content: space-between;

            -webkit-align-items:center;
            -moz-align-items:center;
            -ms-align-items:center;
            -o-align-items:center;
            align-items: center;
        }

        .test-case__filter {
            border: 2px solid #1e90ff;
            -webkit-border-radius: 10px;
            -moz-border-radius: 10px;
            -o-border-radius: 10px;
            border-radius: 10px;
            padding-left: 5px;
            font-size: 15px;
        }

        .test-case__filter svg {
            margin-right: 5px;
            cursor: pointer;
        }

        .test-case__item {
            margin: 10px;
            font-size: 15px;
        }

        .test-case__time_and_status {
            display: flex;
            align-items: center;
        }

        .test-case__time_and_status * {
            margin-right: 5px;
        }

        .test-case-step__title {
            -webkit-align-items:center;
            -moz-align-items:center;
            -ms-align-items:center;
            -o-align-items:center;
            align-items: center;
        }

        .test-case-step__title * {
            margin-right: 5px;
        }

        .test-case-step__detail {
            padding-left: 20px;
            padding-right: 20px;
        }

        .test-case-step__detail table {
            border-collapse: collapse;
            border: none;
        }

        .test-case-step__detail table tr {
            border-bottom: 1px solid #ebeef5;
        }

        .test-case-step__detail table td {
            padding-left: 2px;
            padding-right: 2px;
        }

        .test-case-step__detail table tr:last-child {
            border-bottom: 0;
        }

        .test-case-step__detail table tr td:first-child {
            vertical-align: top;
            border-right: 1px solid #ebeef5;
        }

        .success {
            color: #029e4a;
        }

        .failure {
            color: #ff2121;
        }

        .time {
            color: #909399;
            font-size: 15px;
        }

        .time-tag {
            background-color: #bdbdbd;
        }

        .start-time-tag {
            background-color: #00c853;
        }

        .end-time-tag {
            background-color: #ef5350;
        }

        .elapsed-time-tag {
            background-color: #bdbdbd;
        }

        /* Element-ui style */
        .test-case .el-collapse {
            margin-left: 20px;
            margin-right: 20px;
        }

        .test-case .el-collapse-item {
            margin-top: 15px;
            border-top: 1px solid #ebeef5;
            border-left: 1px solid #ebeef5;
            border-right: 1px solid #ebeef5;
        }

        .test-case-step .el-collapse-item {
            margin: 0;
            border-top: 0;
            border-left: 0;
            border-right: 0;
        }

        .test-case .el-collapse-item__header{
            min-height: 60px;
            height: 100%;
            line-height: 20px;
        }
        .test-case-step .el-collapse-item:last-child .el-collapse-item__wrap {
            border-bottom: 0;
        }

        .test-case-step .el-collapse-item:last-child .el-collapse-item__header {
            border-bottom: 0;
        }

        .test-case-step .el-collapse-item__header {
            min-height: 30px;
            height: 100%;
            line-height: 30px;
        }

        .el-collapse-item__header:hover {
            background: #EBEEF5;
        }

        .el-tag{
            color: white;
        }
    </style>
</head>

<body>
    <div id="app" class="max-size">
        <div class="container vertical max-size">
            <div class="container header">
                <span><b>接口测试报告</b></span>
                <div class="header-info">
                    <el-tag size="medium">创建时间：{{ reportInfo['createTime'] }}</el-tag>
                    <el-tag size="medium">更新时间：{{ reportInfo['lastUpdateTime'] }}</el-tag>
                    <el-tag size="medium">{{ reportInfo['toolName'] }}</el-tag>
                </div>
            </div>

            <div class="container max-size">
                <div class="container vertical aside">
                    <div class="test-suite max-size">
                        <div class="container test-suite__header">
                            <b>测试集</b>
                            <div class="container test-suite__filter">
                                <span class="unable-select">筛选：</span>
                                <svg class="icon" aria-hidden="true" @click="updateTestSuiteFilterValue(true)">
                                    <use xlink:href="#icon-pass"></use>
                                </svg>
                                <svg class="icon" aria-hidden="true" @click="updateTestSuiteFilterValue(false)">
                                    <use xlink:href="#icon-failure"></use>
                                </svg>
                                <svg class="icon" aria-hidden="true" @click="updateTestSuiteFilterValue(null)">
                                    <use xlink:href="#icon-close"></use>
                                </svg>
                            </div>
                        </div>
                        <div class="container overview" @click="isShowOverview=true">
                            <span style="margin:10px">报告分析</span>
                        </div>
                        <ul class="test-suite_list">
                            <li v-for="(testSuite, index) in filterTestSuiteList">
                                <div class="test-suite__title" @click="showThisTestSuiteDetail(index)">
                                    <div class="break-word-title" :class="{failure : !testSuite['status']}">{{ testSuite['title'] }}</div>
                                    <div class="container test-suite__time_and_status">
                                        <span class="time">{{ testSuite['startTime'] }}</span>
                                        <svg class="icon" aria-hidden="true">
                                            <use v-if="testSuite['status']" xlink:href="#icon-pass"></use>
                                            <use v-else xlink:href="#icon-failure"></use>
                                        </svg>
                                    </div>
                                </div>
                            </li>
                        </ul>
                    </div>
                </div>

                <div class="container vertical main max-size">
                    <div class="container vertical max-size overview-info" v-show="isShowOverview">
                        <div class="overview_table max-size">
                            <el-table :data="overviewTableData" stripe style="width:100%">
                                <el-table-column prop="totalType" label="统计"></el-table-column>
                                <el-table-column prop="scriptTotal" label="脚本"></el-table-column>
                                <el-table-column prop="threadTotal" label="线程组"></el-table-column>
                                <el-table-column prop="samplerTotal" label="样本"></el-table-column>
                            </el-table>
                        </div>
                        <div class="container max-size pie-chart">
                            <div id="overview_script_pie_chart" class="max-size"></div>
                            <div id="overview_thread_pie_chart" class="max-size"></div>
                            <div id="overview_sampler_pie_chart" class="max-size"></div>
                        </div>
                    </div>
                    <div class="test-case max-size"  v-show="!isShowOverview">
                        <div class="container vertical test-case__header">
                            <div class="break-word-title"><b>{{ filterTestSuiteList[currentTestSuiteIndex]['title'] }}</b></div>
                            <div class="container test-case__time_and_filter">
                                <div>
                                    <el-tag class="start-time-tag" size="mini">{{ filterTestSuiteList[currentTestSuiteIndex]['startTime'] }}</el-tag>
                                    <el-tag class="end-time-tag" size="mini">{{ filterTestSuiteList[currentTestSuiteIndex]['endTime'] }}</el-tag>
                                    <el-tag class="elapsed-time-tag" size="mini">{{ filterTestSuiteList[currentTestSuiteIndex]['elapsedTime'] }}</el-tag>
                                </div>
                                <div class="container test-case__filter">
                                    <span class="unable-select">筛选：</span>
                                    <svg class="icon" aria-hidden="true" @click="updateTestCaseFilterValue(true)">
                                        <use xlink:href="#icon-pass"></use>
                                    </svg>
                                    <svg class="icon" aria-hidden="true" @click="updateTestCaseFilterValue(false)">
                                        <use xlink:href="#icon-failure"></use>
                                    </svg>
                                    <svg class="icon" aria-hidden="true" @click="updateTestCaseFilterValue(null)">
                                        <use xlink:href="#icon-close"></use>
                                    </svg>
                                </div>
                            </div>
                        </div>
                        <el-collapse v-model="testCaseActiveName" accordion>
                            <el-collapse-item v-for="testCase in filterCurrentTestCaseList" :name="testCase['id']"
                                              :key="testCase['id']">
                                <template slot="title">
                                    <div class="container vertical test-case__item">
                                        <div class="test-case__title">
                                            <span class="break-word-title" :class="{failure : !testCase['status']}">{{ testCase['title'] }}</span>
                                        </div>
                                        <div class="test-case__time_and_status">
                                            <el-tag class="time-tag" size="mini">{{ testCase['startTime'] }}</el-tag>
                                            <el-tag class="time-tag" size="mini">{{ testCase['elapsedTime'] }}</el-tag>
                                            <svg class="icon" aria-hidden="true">
                                                <use v-if="testCase['status']" xlink:href="#icon-pass"></use>
                                                <use v-else xlink:href="#icon-failure"></use>
                                            </svg>
                                        </div>
                                    </div>
                                </template>

                                <div class="test-case-step">
                                    <el-collapse v-model="testCaseStepActiveName" accordion>
                                        <el-collapse-item v-for="testCaseStep in testCase['testCaseStepList']" :name="testCaseStep['id']"
                                                          :key="testCaseStep['id']">
                                            <template slot="title">
                                                <div class="container test-case-step__title">
                                                    <svg class="icon" aria-hidden="true">
                                                        <use v-if="testCaseStep['status']" xlink:href="#icon-pass"></use>
                                                        <use v-else xlink:href="#icon-failure"></use>
                                                    </svg>
                                                    <span class="break-word" :class="{failure : !testCaseStep['status']}">{{ testCaseStep['tile'] }}</span>
                                                    <el-tag class="time-tag" size="mini">{{ testCaseStep['elapsedTime'] }}</el-tag>
                                                </div>
                                            </template>
                                            <div class="test-case-step__detail">
                                                <table>
                                                    <tr>
                                                        <td>Request:</td>
                                                        <td class="break-word">{{ testCaseStep['request'] }}</td>
                                                    </tr>
                                                    <tr>
                                                        <td>Response:</td>
                                                        <td class="break-word">{{ testCaseStep['response'] }}</td>
                                                    </tr>
                                                </table>
                                            </div>
                                        </el-collapse-item>
                                    </el-collapse>
                                </div>
                            </el-collapse-item>
                        </el-collapse>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
      var app = new Vue({
        el: '#app',
        data: {
          isShowOverview: true,
          currentTestSuiteIndex: 0,
          testCaseActiveName: '',
          testCaseStepActiveName: '',
          testSuiteFilterValue: null,
          testCaseFilterValue: null,
          pieChartOption: {
            title: {
              text: '饼图分析',
              left: 'center',
              top: 20,
              textStyle: {
                  color: '#ccc'
              }
            },
            tooltip: {
              trigger: 'item',
              formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            visualMap: {
              show: false,
              min: 80,
              max: 600,
              inRange: {
                colorLightness: [0, 1]
              }
            },
            series: [
              {
                name: '访问来源',
                type: 'pie',
                radius : '55%',
                center: ['50%', '50%'],
                data:[
                  {value:335, name:'直接访问'},
                  {value:310, name:'邮件营销'},
                  {value:274, name:'联盟广告'},
                  {value:235, name:'视频广告'},
                  {value:400, name:'搜索引擎'}
                ].sort(function(a, b) { return a.value - b.value; }),
                roseType: 'radius',
                label: {
                  normal: {
                    textStyle: {
                      color: 'rgba(255, 255, 255, 0.3)'
                    }
                  }
                },
                labelLine: {
                  normal: {
                    lineStyle: {
                      color: 'rgba(255, 255, 255, 0.3)'
                    },
                    smooth: 0.2,
                    length: 10,
                    length2: 20
                  }
                },
                itemStyle: {
                  normal: {
                    color: '#c23531',
                    shadowBlur: 200,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                  }
                },
                animationType: 'scale',
                animationEasing: 'elasticOut',
                animationDelay: function(idx) {
                  return Math.random() * 200;
                }
              }
            ]
          },
          reportInfo: ${reportInfo},
          overviewInfo: ${overviewInfo},
          testSuiteList: ${testSuiteList}
        },
        methods: {
          showThisTestSuiteDetail: function(index) {
            this.isShowOverview = false
            this.currentTestSuiteIndex = index
            this.testCaseActiveName = ''
            this.testCaseStepActiveName = ''
            this.testCaseFilterValue = null
          },
          isTrueFilter: function(currentObj) {
            return currentObj['status'] === true
          },
          isFalseFilter: function(currentObj) {
            return currentObj['status'] === false
          },
          listFilter: function(list, filterValue) {
            if(filterValue === null) {
              return list
            }
            if(filterValue === true) {
              return list.filter(this.isTrueFilter)
            }
            if(filterValue === false) {
              return list.filter(this.isFalseFilter)
            }
          },
          updateTestSuiteFilterValue: function(newValue) {
            this.testSuiteFilterValue = newValue
            this.currentTestSuiteIndex = 0
            this.testCaseActiveName = ''
            this.testCaseStepActiveName = ''
            this.testCaseFilterValue = null
          },
          updateTestCaseFilterValue: function(newValue) {
            this.testCaseFilterValue = newValue
            this.testCaseActiveName = ''
            this.testCaseStepActiveName = ''
          },
          successRate: function(total, errorTotal){
            return ((total - errorTotal) / total).toFixed(4) * 100 + '%'
          }
        },
        computed: {
          filterTestSuiteList: function() {
            return this.listFilter(this.testSuiteList, this.testSuiteFilterValue)
          },
          filterCurrentTestCaseList: function() {
            return this.listFilter(this.filterTestSuiteList[this.currentTestSuiteIndex]['testCaseList'], this.testCaseFilterValue)
          },
          overviewTableData: function() {
            return [
              {
                totalType:'总数',
                scriptTotal:this.overviewInfo.testSuiteTotal,
                threadTotal:this.overviewInfo.testCaseTotal,
                samplerTotal:this.overviewInfo.testCaseStepTotal
              },
              {
                totalType:'成功总数',
                scriptTotal:this.overviewInfo.testSuiteTotal - this.overviewInfo.testSuiteTotal,
                threadTotal:this.overviewInfo.testCaseTotal - this.overviewInfo.errorTestCaseTotal,
                samplerTotal:this.overviewInfo.testCaseStepTotal - this.overviewInfo.errorTestCaseStepTotal
              },
              {
                totalType:'失败总数',
                scriptTotal:this.overviewInfo.errorTestSuiteTotal,
                threadTotal:this.overviewInfo.errorTestCaseTotal,
                samplerTotal:this.overviewInfo.errorTestCaseStepTotal
              },
              {
                totalType:'成功率',
                scriptTotal:this.successRate(this.overviewInfo.testSuiteTotal - this.overviewInfo.errorTestSuiteTotal),
                threadTotal:this.successRate(this.overviewInfo.testCaseTotal - this.overviewInfo.errorTestCaseTotal),
                samplerTotal:this.successRate(this.overviewInfo.testCaseStepTotal - this.overviewInfo.errorTestCaseStepTotal)
              }
            ]
          }
        },
        mounted: function() {
          echarts.init(document.getElementById('overview_script_pie_chart')).setOption(this.pieChartOption)
          echarts.init(document.getElementById('overview_thread_pie_chart')).setOption(this.pieChartOption)
          echarts.init(document.getElementById('overview_sampler_pie_chart')).setOption(this.pieChartOption)
        }
      })
    </script>
</body>

</html>