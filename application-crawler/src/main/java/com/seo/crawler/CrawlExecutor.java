package com.seo.crawler;

public interface CrawlExecutor extends Runnable {
    void requestToStop();
}
