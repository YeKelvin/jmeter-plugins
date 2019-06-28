package pers.kelvin.util.http;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class HttpRequestFileData {

    private MultipartEntityBuilder entityBuilder;

    public HttpRequestFileData() {
        entityBuilder = MultipartEntityBuilder.create();
        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
    }

    public HttpRequestFileData addBinaryBody(String filePath) {
        entityBuilder.addBinaryBody("upfile", new File(filePath), ContentType.DEFAULT_BINARY, filePath);
        return this;
    }

    public HttpRequestFileData addZipBinaryBody(String zipFilePath) throws FileNotFoundException {
        entityBuilder.addBinaryBody(
                "upstream", new FileInputStream(zipFilePath), ContentType.create("application/zip"), zipFilePath);
        return this;
    }


    public HttpRequestFileData addTextBody(String value) {
        entityBuilder.addTextBody("text", value, ContentType.DEFAULT_BINARY);
        return this;
    }

    public HttpEntity build() {
        return entityBuilder.build();
    }
}
