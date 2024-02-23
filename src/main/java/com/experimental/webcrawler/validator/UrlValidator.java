package com.experimental.webcrawler.validator;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class UrlValidator {

    private UrlValidator() {
    }

    public static int getStatusCode(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        return connection.getResponseCode();
    }
}
