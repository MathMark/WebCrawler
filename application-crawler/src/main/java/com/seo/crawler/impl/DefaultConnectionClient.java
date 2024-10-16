package com.seo.crawler.impl;

import com.seo.crawler.ConnectionClient;
import com.seo.model.ConnectionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class DefaultConnectionClient implements ConnectionClient {

    private final ConnectionProvider connectionProvider;

    public DefaultConnectionClient(HttpClient httpClient) {
        this.connectionProvider = new ConnectionProvider(httpClient);
    }

    @Override
    public Optional<ConnectionResponse> connect(String uri) {
        Optional<HttpResponse<String>> httpResponse = connectionProvider.connectTo(uri);
        if (httpResponse.isEmpty()) {
            return Optional.empty();
        }
        HttpResponse<String> response = httpResponse.get();
        return mapToConnectionResponse(response);
    }

    private Optional<ConnectionResponse> mapToConnectionResponse(HttpResponse<String> response) {
        ConnectionResponse connectionResponse = new ConnectionResponse();
        connectionResponse.setHttpStatus(HttpStatus.resolve(response.statusCode()));
        List<ConnectionResponse.ContentType> contentTypes = parseContentTypes(response);
        connectionResponse.setContentType(contentTypes);
        if (isHtml(contentTypes)) {
            String responseBody = response.body();
            if (responseBody != null && !responseBody.isEmpty()) {
                connectionResponse.setHasHtmlSource(true);
                connectionResponse.setHtmlBody(response.body());
            }
        } else {
            connectionResponse.setHasHtmlSource(false);
        }
        return Optional.of(connectionResponse);
    }

    private boolean isHtml(List<ConnectionResponse.ContentType> contentTypeList) {
        if (contentTypeList == null || contentTypeList.isEmpty()) {
            return false;
        }
        return contentTypeList.stream().anyMatch(ct -> ct == ConnectionResponse.ContentType.TEXT_HTML);
    }

    private List<ConnectionResponse.ContentType> parseContentTypes(java.net.http.HttpResponse<String> response) {
        Map<String, List<String>> headers = response.headers().map();
        List<String> contentTypeList = headers.get("content-type");
        if (contentTypeList == null || contentTypeList.isEmpty()) {
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

    private record ConnectionProvider(HttpClient httpClient) {

        private static final int MAX_RETRIES = 2;

        private Optional<HttpResponse<String>> connectTo(String uri) {
            for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
                try {
                    return Optional.of(sendRequest(uri));
                } catch (IOException e) {
                    log.warn("Attempt {}/{} - IOException for URI: {}. Retrying...", attempt, MAX_RETRIES, uri, e);
                } catch (InterruptedException e) {
                    log.warn("HTTP request has been interrupted.", e);
                    Thread.currentThread().interrupt();
                } catch (URISyntaxException e) {
                    log.warn("Incorrect URI syntax {}", uri, e);
                    break;
                }
            }
            log.error("Failed to connect to URI: {} after {} attempts.", uri, MAX_RETRIES);
            return Optional.empty();
        }

        private HttpResponse<String> sendRequest(String uri) throws IOException, InterruptedException, URISyntaxException {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(uri)).GET().build();
            return this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        }
    }

}
