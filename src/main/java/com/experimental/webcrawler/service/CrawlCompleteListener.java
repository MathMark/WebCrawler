package com.experimental.webcrawler.service;

import com.experimental.webcrawler.service.event.CrawlCompletedEvent;

public interface CrawlCompleteListener {
    void onCrawlCompete(CrawlCompletedEvent event);
}
