<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8"/>
    <title>Title</title>
    <!-- <script src="https://cdn.jsdelivr.net/npm/vue@2.5.22/dist/vue.min.js"></script> -->
    <script src="https://cdn.jsdelivr.net/npm/vue@2.5.22/dist/vue.js"></script>
    <script src="https://unpkg.com/element-ui@2.4.11/lib/index.js"></script>
    <script>
        !function(a){var e,c='<svg><symbol id="icon-pass" viewBox="0 0 1024 1024"><path d="M512 512m-477.86666667 0a477.86666667 477.86666667 0 1 0 955.73333334 0 477.86666667 477.86666667 0 1 0-955.73333334 0Z" fill="#029E4A" ></path><path d="M830.61333333 364.69333333l-406.4 406.4c-13.22666667 13.22666667-34.98666667 13.22666667-48.32 0-13.22666667-13.22666667-13.22666667-34.98666667 0-48.32l406.4-406.4c13.22666667-13.22666667 34.98666667-13.22666667 48.32 0 13.22666667 13.33333333 13.22666667 34.98666667 0 48.32z" fill="#FFFFFF" ></path><path d="M240.64 541.86666667l181.86666667 181.86666666c13.22666667 13.22666667 13.22666667 34.98666667 0 48.32l-5.86666667 5.86666667c-10.02666667 10.02666667-26.56 10.02666667-36.58666667 0l-187.73333333-187.73333333c-13.22666667-13.22666667-13.22666667-34.98666667 0-48.32 13.33333333-13.22666667 34.98666667-13.22666667 48.32 0z" fill="#FFFFFF" ></path></symbol><symbol id="icon-failure" viewBox="0 0 1024 1024"><path d="M512 32C246.875 32 32 246.875 32 512s214.875 480 480 480 480-214.875 480-480S777.125 32 512 32z" fill="#E4393C" ></path><path d="M554.28125 520.53125L700.4375 666.59375c10.40625 10.40625 10.40625 27.28125 0 37.6875l-9.375 9.46875c-10.40625 10.40625-27.28125 10.40625-37.6875 0l-146.25-146.0625L361.0625 713.75c-10.40625 10.40625-27.28125 10.40625-37.6875 0l-9.46875-9.46875c-10.40625-10.40625-10.40625-27.28125 0-37.6875L460.0625 520.4375 313.90625 374.375c-10.40625-10.40625-10.40625-27.28125 0-37.6875l9.46875-9.46875c10.40625-10.40625 27.28125-10.40625 37.6875 0l146.15625 146.15625 146.15625-146.15625c10.40625-10.40625 27.28125-10.40625 37.6875 0l9.375 9.46875c10.40625 10.40625 10.40625 27.28125 0 37.6875L554.28125 520.53125z" fill="#FFFFFF" ></path></symbol></svg>',t=(e=document.getElementsByTagName("script"))[e.length-1].getAttribute("data-injectcss");if(t&&!a.__iconfont__svg__cssinject__){a.__iconfont__svg__cssinject__=!0;try{document.write("<style>.svgfont {display: inline-block;width: 1em;height: 1em;fill: currentColor;vertical-align: -0.1em;font-size:16px;}</style>")}catch(e){console&&console.log(e)}}!function(e){if(document.addEventListener)if(~["complete","loaded","interactive"].indexOf(document.readyState))setTimeout(e,0);else{var t=function(){document.removeEventListener("DOMContentLoaded",t,!1),e()};document.addEventListener("DOMContentLoaded",t,!1)}else document.attachEvent&&(n=e,i=a.document,l=!1,o=function(){l||(l=!0,n())},(c=function(){try{i.documentElement.doScroll("left")}catch(e){return void setTimeout(c,50)}o()})(),i.onreadystatechange=function(){"complete"==i.readyState&&(i.onreadystatechange=null,o())});var n,i,l,o,c}(function(){var e,t,n,i,l,o;(e=document.createElement("div")).innerHTML=c,c=null,(t=e.getElementsByTagName("svg")[0])&&(t.setAttribute("aria-hidden","true"),t.style.position="absolute",t.style.width=0,t.style.height=0,t.style.overflow="hidden",n=t,(i=document.body).firstChild?(l=n,(o=i.firstChild).parentNode.insertBefore(l,o)):i.appendChild(n))})}(window);
    </script>
    <link rel="stylesheet" type="text/css" href="https://unpkg.com/element-ui@2.4.11/lib/theme-chalk/index.css"/>
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
            flex-direction: column;
        }

        .header {
            background: -webkit-linear-gradient(left, #319be9, #3448a1);
            background: -moz-linear-gradient(left, #319be9, #3448a1);
            background: -o-linear-gradient(left, #319be9, #3448a1);
            background: -ms-linear-gradient(left, #319be9, #3448a1);
            background: linear-gradient(left, #319be9, #3448a1);
            color: #d9d9d9;
            justify-content: center;
            align-items: center;
            width: 100%;
            height: 60px;
        }

        .aside {
            background-color: white;
            color: #333;
            justify-content: center;
            align-items: center;
            width: 400px;
            height: 100%;
            border: 1px solid #ebeef5;
        }

        .main {
            background-color: white;
            color: #333;
            justify-content: center;
            align-items: center;
            border: 1px solid #ebeef5;
        }

        .icon {
            width: 1.5em;
            height: 1.5em;
            vertical-align: -0.15em;
            fill: currentColor;
            overflow: hidden;
        }

        .break-word {
            word-wrap: break-word;
            word-break: break-all;
        }

        .test-suite-filter {
            margin: 10px;
            font-size: 15px;
            height: 20px;
        }

        .test-suite {
            overflow: auto;
        }

        .test-suite-list {
            list-style-type: none;
            text-align: left;
        }

        .test-suite-list li {
            border-top: 1px solid transparent;
            border-bottom: 1px solid #ebeef5;
        }

        .test-suite-list li:first-child {
            border-top: 1px solid #ebeef5;
        }

        .test-suite-list li:hover {
            background: #EBEEF5;
        }

        .test-suite-title {
            margin: 10px;
            cursor: pointer;
        }

        .test-suite-title-time {
            display: inline-flex;
            align-items: center;
        }

        .test-case-filter {
            margin: 10px;
            font-size: 15px;
            height: 20px;
        }

        .test-case {
            overflow: auto;
        }

        .test-case-title {
            margin-left: 10px;
            margin-right: 10px;
            font-size: 15px;
        }

        .test-case-title-time {
            display: inline-flex;
            align-items: center;
        }

        .test-case-step-title {
            margin: 0px 20px 0px 20px;
            display: inline-flex;
            align-items: center;
        }

        .test-case-step-detail {
            padding-left: 40px;
            padding-right: 40px;
        }

        .test-case-step-detail table {
            border-collapse: collapse;
            border: none;
        }

        .test-case-step-detail table tr {
            border-bottom: 1px solid #ebeef5;
        }

        .test-case-step-detail table td {
            padding-left: 2px;
            padding-right: 2px;
        }

        .test-case-step-detail table tr:last-child {
            border-bottom: 0;
        }

        .test-case-step-detail table tr td:first-child {
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
        }

        /* Element-ui style */
        .test-case-step .el-collapse-item:last-child .el-collapse-item__wrap {
            border-bottom: 0;
        }

        .test-case-step .el-collapse-item:last-child .el-collapse-item__header {
            border-bottom: 0;
        }

        .el-collapse-item__header:hover {
            background: #EBEEF5;
        }

        .test-case-step .el-collapse-item__header {
            height: auto;
            line-height: 30px;
        }
    </style>
</head>

<body>
    <div id="app" class="max-size">
        <div class="container vertical max-size">
            <div class="container header">
                <div>Header</div>
            </div>

            <div class="container max-size">
                <div class="container vertical aside">
                    <div class="test-suite max-size">
                        <div class="test-suite-filter">
                            <b>测试集</b>
                        </div>
                        <ul class="test-suite-list">
                            <li v-for="(testSuite, index) in testSuiteList">
                                <div class="test-suite-title break-word" @click="showThisTestSuiteDetail(index)">
                                    <div>{{ testSuite['title'] }}</div>
                                    <div class="test-suite-title-time">
                                        <span class="time">timestamp</span>
                                        <span class="time">elapsed</span>
                                        <svg class="icon" aria-hidden="true">
                                            <use xlink:href="#icon-pass"></use>
                                        </svg>
                                    </div>
                                </div>
                            </li>
                        </ul>
                    </div>
                </div>

                <div class="container vertical main max-size">
                    <div class="test-case max-size">
                        <div class="test-case-filter">
                            <b>测试案例</b>
                        </div>
                        <el-collapse accordion>
                            <el-collapse-item v-for="testCase in testSuiteList[currentTestSuiteIndex]['testCaseList']"
                                              :name="testCase['id']"
                                              :key="testCase['id']">
                                <template slot="title">
                                    <div class="test-case-title-time">
                        <span class="test-case-title" :class="{failure : !testCase['status']}">
                          {{ testCase['title']}}
                        </span>
                                        <svg class="icon" aria-hidden="true">
                                            <use v-if="testCase['status']" xlink:href="#icon-pass"></use>
                                            <use v-else xlink:href="#icon-failure"></use>
                                        </svg>
                                    </div>
                                </template>

                                <div class="test-case-step">
                                    <el-collapse accordion>
                                        <el-collapse-item v-for="testCaseStep in testCase['testCaseStepList']"
                                                          :name="testCaseStep['id']"
                                                          :key="testCaseStep['id']">
                                            <template slot="title">
                                                <div class="test-case-step-title">
                                                    <svg class="icon" aria-hidden="true">
                                                        <use v-if="testCaseStep['status']" xlink:href="#icon-pass"></use>
                                                        <use v-else xlink:href="#icon-failure"></use>
                                                    </svg>
                                                    <span>{{ testCaseStep['tile'] }}</span>
                                                </div>
                                            </template>
                                            <div class="test-case-step-detail">
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
                currentTestSuiteIndex: 0,
                testCaseActiveName: '',
                testCaseStepActiveName: '',
                testSuiteList: ${testSuiteList}
            },
            methods: {
                showThisTestSuiteDetail: function (index) {
                    this.currentTestSuiteIndex = index
                    this.testCaseActiveName = ''
                    this.testCaseStepActiveName = ''
                }
            }
        })
    </script>
</body>

</html>