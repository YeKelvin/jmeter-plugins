package pers.kelvin.util.http;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class HttpRequestFileData {

    private MultipartEntityBuilder entityBuilder;

    public HttpRequestFileData() {
        entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
    }

    public HttpRequestFileData addBinaryBody(String keyName, String filePath) {
        entityBuilder.addBinaryBody(keyName, new File(filePath), ContentType.DEFAULT_BINARY, filePath);
        return this;
    }

    public HttpRequestFileData addZipBinaryBody(String keyName, String zipFilePath) throws FileNotFoundException {
        entityBuilder.addBinaryBody(
                keyName, new FileInputStream(zipFilePath), ContentType.create("application/zip"), zipFilePath);
        return this;
    }


    public HttpRequestFileData addTextBody(String keyName, String value) {
        entityBuilder.addTextBody(keyName, value, ContentType.DEFAULT_BINARY);
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
            e.printStackTrace();
        }
        return requestData;
    }
}
