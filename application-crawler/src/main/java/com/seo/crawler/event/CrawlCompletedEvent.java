package com.seo.crawler.event;

import com.seo.model.CrawlData;

public record CrawlCompletedEvent(CrawlData crawlData) {
}
