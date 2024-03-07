package com.experimental.webcrawler.crawler;

public interface CrawlExecutor extends Runnable {
    void requestToStop();
}
