package com.seo.crawler.event;

import com.seo.model.CrawlData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CrawlCompletedEvent {
    private final CrawlData crawlData;
}
