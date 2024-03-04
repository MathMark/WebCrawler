package com.experimental.webcrawler.service.event;

import com.experimental.webcrawler.crawler.model.CrawlData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CrawlCompletedEvent {
    private final CrawlData crawlData;
}
