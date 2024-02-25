package com.experimental.webcrawler.service;

import com.experimental.webcrawler.crawler.model.Website;

public interface CrawlCompleteListener {
    void onCrawlCompete(Website website);
}
