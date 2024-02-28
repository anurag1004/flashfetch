package com.main.filedownloader.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileByeSizeRetriever {
    public static float getFileSize(String url) throws IOException {
        URL fileUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) fileUrl.openConnection();
        connection.setRequestMethod("HEAD");
        return connection.getContentLength(); // bytes
    }
}
