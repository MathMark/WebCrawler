package com.seo.crawler;


import com.seo.crawler.event.CrawlCompletedEvent;

public interface CrawlCompleteListener {
    void onCrawlCompete(CrawlCompletedEvent event);
}
