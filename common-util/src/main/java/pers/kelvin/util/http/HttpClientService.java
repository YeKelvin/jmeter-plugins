package pers.kelvin.util.http;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author KelvinYe
 */
public class HttpClientService {
    // 编码格式
    private static final String DEFAULT_ENCODE = "UTF-8";

    // 设置连接超时时间，单位毫秒
    private static final int CONNECT_TIMEOUT = 5000;

    // 请求获取响应的超时时间(即响应时间)，单位毫秒
    private static final int SOCKET_TIMEOUT = 5000;

    // 请求头keep-alive配置
    private static final ConnectionKeepAliveStrategy KEEP_ALIVE_STRATEGY = new DefaultConnectionKeepAliveStrategy() {
        @Override
        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            long keepAlive = super.getKeepAliveDuration(response, context);
            if (keepAlive == -1) {
                //如果服务器没有设置keep-alive，就把它设置成1分钟
                keepAlive = 60000;
            }
            return keepAlive;
        }
    };

    // 请求配置
    private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom()
            // 设置连接超时时间
            .setConnectTimeout(CONNECT_TIMEOUT)
            // 设置从connect Manager(连接池)获取Connection的超时时间
            .setSocketTimeout(SOCKET_TIMEOUT)
            // 设置 HttpClient接收 Cookie，用与浏览器一样的策略
            .setCookieSpec(CookieSpecs.DEFAULT)
            .build();

    //响应处理器
    private static ResponseHandler<String> responseHandler;

    // 默认请求地址
    private String basicUrl;

    // Cookie存储对象
    private CookieStore cookieStore;

    private CloseableHttpClient httpClients;

    public HttpClientService() {
        cookieStore = new BasicCookieStore();
        httpClients = HttpClients.custom()
                // 设置keep-alive
                .setKeepAliveStrategy(KEEP_ALIVE_STRATEGY)
                // 设置重定向
                .setRedirectStrategy(new LaxRedirectStrategy())
                // 设置Cookie存储对象
                .setDefaultCookieStore(cookieStore)
                .build();
        responseHandler = new BasicResponseHandler();
    }

    public HttpClientService(String basicUrl) {
        this();
        this.basicUrl = basicUrl;
    }

    public HttpClientService(String protocol, String host, String port) {
        this();
        basicUrl = protocol + "://" + host + ":" + port;
    }

    public String getUrl(String url) {
        if (StringUtils.isEmpty(basicUrl)) {
            return url;
        }
        return basicUrl + url;
    }

    /**
     * http get 请求
     *
     * @param url         请求地址
     * @param headers     请求头
     * @param requestData 请求参数
     */
    public String sendGet(String url, HttpHeader headers, HttpRequestData requestData) throws Exception {
        // 创建访问的地址
        URIBuilder uriBuilder = new URIBuilder(getUrl(url));

        // 设置请求参数
        if (requestData != null) {
            requestData.toMap().forEach(uriBuilder::setParameter);
        }

        // 创建httpGet请求对象
        HttpGet httpGet = new HttpGet(uriBuilder.build());

        // 设置请求配置
        httpGet.setConfig(REQUEST_CONFIG);

        // 设置请求头
        setHeader(httpGet, headers.toMap());

//         CloseableHttpResponse httpResponse = null;

        return httpClients.execute(httpGet, responseHandler);
    }

    /**
     * http post 请求
     *
     * @param url         请求地址
     * @param headers     请求头
     * @param requestData 请求参数
     */
    public String sendPost(String url, HttpHeader headers, HttpRequestData requestData) throws IOException {
        // 创建httpPost请求对象
        HttpPost httpPost = new HttpPost(getUrl(url));

        // 设置请求配置
        httpPost.setConfig(REQUEST_CONFIG);

        // 设置请求头
        setHeader(httpPost, headers.toMap());

        // 设置请求参数
        httpPost.setEntity(new StringEntity(requestData.toString(), DEFAULT_ENCODE));

        return httpClients.execute(httpPost, responseHandler);
    }

    /**
     * http post 上传文件
     *
     * @param url         请求地址
     * @param headers     请求头
     * @param requestData 请求参数
     */
    public String sendPostFile(String url, HttpHeader headers, HttpRequestFileData requestFileData) throws IOException {
        // 创建httpPost请求对象
        HttpPost httpPost = new HttpPost(getUrl(url));

        // 设置请求配置
        httpPost.setConfig(REQUEST_CONFIG);

        // 设置请求头
        setHeader(httpPost, headers.toMap());

        // 设置请求参数
        httpPost.setEntity(requestFileData.build());

        return httpClients.execute(httpPost, responseHandler);
    }

    public String getCookie(String cookieName) {
        String cookieValue = "";
        List<Cookie> cookies = cookieStore.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                cookieValue = cookie.getValue();
                break;
            }
        }
        return cookieValue;
    }

    /**
     * 设置请求头
     */
    private void setHeader(HttpRequestBase httpMethod, Map<String, String> headers) {
        if (headers != null) {
            headers.forEach(httpMethod::setHeader);
        }
    }

    /**
     * 释放资源
     **/
    public void release() throws IOException {
        if (httpClients != null) {
            httpClients.close();
        }
    }
}
