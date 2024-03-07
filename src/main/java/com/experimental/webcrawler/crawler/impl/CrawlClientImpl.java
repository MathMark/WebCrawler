package com.experimental.webcrawler.crawler.impl;

import com.experimental.webcrawler.crawler.CrawlClient;
import com.experimental.webcrawler.crawler.model.ConnectionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class CrawlClientImpl implements CrawlClient {
    
    private final HttpClient httpClient;
    
    @Override
    public ConnectionResponse connect(String uri) {
        ConnectionResponse connectionResponse = new ConnectionResponse();
        java.net.http.HttpResponse<String> httpResponse = connectTo(uri);
        if (httpResponse != null) {
            connectionResponse.setHttpStatus(HttpStatus.resolve(httpResponse.statusCode()));
            connectionResponse.setHtmlBody(httpResponse.body());
            List<ConnectionResponse.ContentType> contentTypes = parseContentTypes(httpResponse);
            connectionResponse.setContentType(contentTypes);
        }
        return connectionResponse;
    }
    
    private java.net.http.HttpResponse<String> connectTo(String uri) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .GET()
                    .build();
            return this.httpClient.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException | IOException e) {
            log.warn("Incorrect URI syntax {}", uri, e);
        } catch (InterruptedException e) {
            log.warn("HTTP request has been interrupted.");
            Thread.currentThread().interrupt();
        }
        return null;
    }
    
    private List<ConnectionResponse.ContentType> parseContentTypes(java.net.http.HttpResponse<String> response) {
        Map<String, List<String>> headers = response.headers().map();
        List<String> contentTypeList = headers.get("content-type");
        if (contentTypeList.isEmpty()) {
            return Collections.singletonList(ConnectionResponse.ContentType.UNSUPPORTED_OR_UNKNOWN);
        }
        List<ConnectionResponse.ContentType> contentTypes = new ArrayList<>();
        for (String contentType : contentTypeList) {
            contentTypes.add(ConnectionResponse.ContentType.fromString(removeParameter(contentType)));
        }
        return contentTypes;
    }
    
    private String removeParameter(String contentType) {
        if (contentType.contains(";")) {
            String[] values = contentType.split(";");
            return values[0];
        } 
        return contentType;
    }
        
}
