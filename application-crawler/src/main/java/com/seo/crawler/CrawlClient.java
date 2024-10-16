package com.seo.crawler;

import com.seo.model.ConnectionResponse;

import java.util.Optional;

public interface CrawlClient {
    Optional<ConnectionResponse> connect(String uri);
}
