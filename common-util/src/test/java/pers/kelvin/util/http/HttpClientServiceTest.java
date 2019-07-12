package pers.kelvin.util.http;

import org.testng.annotations.Test;

import java.io.IOException;

public class HttpClientServiceTest {

    @Test
    public void testSendPost() throws IOException {
        String url = "https://uat.x-vipay.com:443/boss_web/boss/auth/authentication.do";
        HttpHeader.setDefaultHeaders("Accept-Encoding", "gzip, deflate, br");
        HttpHeader.setDefaultHeaders("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
        HttpHeader.setDefaultHeaders("Content-Type", "application/json;charset=UTF-8");
        HttpHeader httpHeader = HttpHeader.useDefaultHeaders();
        httpHeader.put("Origin", "https://uat.x-vipay.com")
                .put("Referer", "https://uat.x-vipay.com/boss/")
                .put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36")
                .put("termTyp", "WEB");
        HttpRequestData requestData = new HttpRequestData("{\"loginName\":\"ykw\",\"password\":\"9acb55f6de728856f78c1937e0742d2f\",\"captcha\":\"\"}");
        HttpClientService httpClient = new HttpClientService();
        System.out.println(httpClient.sendPost(url, httpHeader, requestData));
    }
}