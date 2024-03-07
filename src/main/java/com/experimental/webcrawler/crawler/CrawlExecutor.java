package com.experimental.webcrawler.crawler;

public interface CrawlExecutor {
    void crawl(int threadCount);
    void shutDown();
}
