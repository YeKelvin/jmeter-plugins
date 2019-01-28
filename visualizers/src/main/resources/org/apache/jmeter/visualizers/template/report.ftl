<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <title>Title</title>
    <script src="https://cdn.jsdelivr.net/npm/vue@2.5.22/dist/vue.min.js"></script>
    <script src="https://unpkg.com/element-ui@2.4.11/lib/index.js"></script>
    <script>
        !function(a){var e,c='<svg><symbol id="icon-pass" viewBox="0 0 1024 1024"><path d="M512 512m-477.86666667 0a477.86666667 477.86666667 0 1 0 955.73333334 0 477.86666667 477.86666667 0 1 0-955.73333334 0Z" fill="#029E4A" ></path><path d="M830.61333333 364.69333333l-406.4 406.4c-13.22666667 13.22666667-34.98666667 13.22666667-48.32 0-13.22666667-13.22666667-13.22666667-34.98666667 0-48.32l406.4-406.4c13.22666667-13.22666667 34.98666667-13.22666667 48.32 0 13.22666667 13.33333333 13.22666667 34.98666667 0 48.32z" fill="#FFFFFF" ></path><path d="M240.64 541.86666667l181.86666667 181.86666666c13.22666667 13.22666667 13.22666667 34.98666667 0 48.32l-5.86666667 5.86666667c-10.02666667 10.02666667-26.56 10.02666667-36.58666667 0l-187.73333333-187.73333333c-13.22666667-13.22666667-13.22666667-34.98666667 0-48.32 13.33333333-13.22666667 34.98666667-13.22666667 48.32 0z" fill="#FFFFFF" ></path></symbol><symbol id="icon-failure" viewBox="0 0 1024 1024"><path d="M512 32C246.875 32 32 246.875 32 512s214.875 480 480 480 480-214.875 480-480S777.125 32 512 32z" fill="#E4393C" ></path><path d="M554.28125 520.53125L700.4375 666.59375c10.40625 10.40625 10.40625 27.28125 0 37.6875l-9.375 9.46875c-10.40625 10.40625-27.28125 10.40625-37.6875 0l-146.25-146.0625L361.0625 713.75c-10.40625 10.40625-27.28125 10.40625-37.6875 0l-9.46875-9.46875c-10.40625-10.40625-10.40625-27.28125 0-37.6875L460.0625 520.4375 313.90625 374.375c-10.40625-10.40625-10.40625-27.28125 0-37.6875l9.46875-9.46875c10.40625-10.40625 27.28125-10.40625 37.6875 0l146.15625 146.15625 146.15625-146.15625c10.40625-10.40625 27.28125-10.40625 37.6875 0l9.375 9.46875c10.40625 10.40625 10.40625 27.28125 0 37.6875L554.28125 520.53125z" fill="#FFFFFF" ></path></symbol></svg>',t=(e=document.getElementsByTagName("script"))[e.length-1].getAttribute("data-injectcss");if(t&&!a.__iconfont__svg__cssinject__){a.__iconfont__svg__cssinject__=!0;try{document.write("<style>.svgfont {display: inline-block;width: 1em;height: 1em;fill: currentColor;vertical-align: -0.1em;font-size:16px;}</style>")}catch(e){console&&console.log(e)}}!function(e){if(document.addEventListener)if(~["complete","loaded","interactive"].indexOf(document.readyState))setTimeout(e,0);else{var t=function(){document.removeEventListener("DOMContentLoaded",t,!1),e()};document.addEventListener("DOMContentLoaded",t,!1)}else document.attachEvent&&(n=e,i=a.document,l=!1,o=function(){l||(l=!0,n())},(c=function(){try{i.documentElement.doScroll("left")}catch(e){return void setTimeout(c,50)}o()})(),i.onreadystatechange=function(){"complete"==i.readyState&&(i.onreadystatechange=null,o())});var n,i,l,o,c}(function(){var e,t,n,i,l,o;(e=document.createElement("div")).innerHTML=c,c=null,(t=e.getElementsByTagName("svg")[0])&&(t.setAttribute("aria-hidden","true"),t.style.position="absolute",t.style.width=0,t.style.height=0,t.style.overflow="hidden",n=t,(i=document.body).firstChild?(l=n,(o=i.firstChild).parentNode.insertBefore(l,o)):i.appendChild(n))})}(window);
    </script>
    <link
            rel="stylesheet"
            type="text/css"
            href="https://unpkg.com/element-ui@2.4.11/lib/theme-chalk/index.css"
    />
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
            display: -webkit-box; /* Chrome 4+, Safari 3.1, iOS Safari 3.2+ */
            display: -moz-box; /* Firefox 17- */
            display: -webkit-flex; /* Chrome 21+, Safari 6.1+, iOS Safari 7+, Opera 15/16 */
            display: -moz-flex; /* Firefox 18+ */
            display: -ms-flexbox; /* IE 10 */
            display: flex;
        }

        .vertical {
            flex-direction: column;
        }

        .header,
        .footer {
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
            width: 2em;
            height: 2em;
            vertical-align: -0.15em;
            fill: currentColor;
            overflow: hidden;
        }

        .test-suite {
        }

        .test-suite-list {
            list-style-type: none;
            text-align: left;
            margin-top: 16px;
            margin-bottom: 16px;
        }

        .test-suite-list li {
            display: block;
            border-bottom: 1px solid #ebeef5;
        }

        .test-suite-list li:first-child {
            border-top: 1px solid #ebeef5;
        }

        .test-suite-name {
            margin: 10px;
        }

        .test-case-name {
            margin: 10px;
            font-size: 20px;
        }

        .test-case-step-name {
            margin: 15px;
            display: inline-flex;
            align-items: center;
        }

        .test-case-step el-collapse:last-child{
            border: 0;
        }

        .test-data {
            justify-content: center;
            /* align-items: center; */
        }

        .test-case-step-detail{
            padding-left: 40px;
            padding-right: 40px;
        }

        .test-case-step-detail table{
            border-collapse: collapse;
            border: none;
        }

        .test-case-step-detail table tr{
            border-bottom: 1px solid #ebeef5;
        }

        .test-case-step-detail table tr:last-child{
            border-bottom: 0;
        }
    </style>
</head>
<body>
<div id="app" class="max-size">
    <div class="container vertical max-size">
        <div class="container header"><div>Header</div></div>

        <div class="container max-size">
            <div class="container aside">
                <div class="test-suite max-size">
                    <ul class="test-suite-list">
                        <li v-for="(testSuite, index) in testSuiteList">
                            <div class="test-suite-name" @click="showThisTestSuiteDetail(index)">
                                <span>{{ testSuite['testSuiteName'] }}</span>
                                <div>
                                    <svg class="icon" aria-hidden="true">
                                        <use xlink:href="#icon-pass"></use>
                                    </svg>
                                </div>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>

            <div class="container main max-size">
                <div class="test-case max-size">
                    <el-collapse accordion>
                        <el-collapse-item name="1">
                            <template slot="title">
                                <div class="test-case-name">
                                    【个账】重置登录密码，baseReqKeyType=OPERATOR_NO 1-1
                                </div>
                            </template>

                            <div class="test-case-step">
                                <el-collapse accordion>
                                    <el-collapse-item name="1">
                                        <template slot="title">
                                            <div class="test-case-step-name">
                                                <svg class="icon" aria-hidden="true">
                                                    <use xlink:href="#icon-pass"></use>
                                                </svg>
                                                <svg class="icon" aria-hidden="true">
                                                    <use xlink:href="#icon-failure"></use>
                                                </svg>
                                                <span>CustomerProdFacade.createPersonCustomer</span>
                                            </div>
                                        </template>
                                        <div class="test-case-step-detail max-size">
                                            <table>
                                                <tr>
                                                    <td>Request:</td>
                                                    <td>RequestData</td>
                                                </tr>
                                                <tr>
                                                    <td>Response:</td>
                                                    <td>ResponseData</td>
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

        <!-- <div class="container footer"><div>Footer</div></div> -->
    </div>
</div>

<script>
    var app = new Vue({
        el: '#app',
        data: {
            testSuiteList: ${testSuiteList}
        },
        methods: {
            showThisTestSuiteDetail: function(index){
                console.info(index)
            }
        }
    })
</script>
</body>
</html>
