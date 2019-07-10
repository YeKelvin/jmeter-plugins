package pers.kelvin.util.http;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.slf4j.Logger;
import pers.kelvin.util.exception.ExceptionUtil;
import pers.kelvin.util.log.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class HttpRequestFileData {

    private static final Logger logger = LogUtil.getLogger(HttpRequestFileData.class);

    private MultipartEntityBuilder entityBuilder;

    public HttpRequestFileData() {
        entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
    }

    public HttpRequestFileData addBinaryBody(String keyName, String filePath, String contentType) {
        entityBuilder.addBinaryBody(keyName, new File(filePath), ContentType.create(contentType), filePath);
        return this;
    }

    public HttpRequestFileData addTextBody(String keyName, String value) {
        entityBuilder.addTextBody(keyName, value, ContentType.DEFAULT_TEXT);
        return this;
    }

    public HttpEntity build() {
        return entityBuilder.build();
    }

    @Override
    public String toString() {
        String requestData = "";
        try {
            requestData = String.valueOf(entityBuilder.build().getContent());
        } catch (IOException e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }
        return requestData;
    }
}
