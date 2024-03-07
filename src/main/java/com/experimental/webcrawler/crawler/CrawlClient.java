package com.experimental.webcrawler.crawler;

import com.experimental.webcrawler.crawler.model.ConnectionResponse;

public interface CrawlClient {
    ConnectionResponse connect(String uri);
}
