package com.seo.crawler;

import com.seo.model.ConnectionResponse;

public interface CrawlClient {
    ConnectionResponse connect(String uri);
}
