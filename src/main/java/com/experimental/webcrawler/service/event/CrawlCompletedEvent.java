package com.experimental.webcrawler.service.event;

import com.experimental.webcrawler.crawler.model.Page;
import com.experimental.webcrawler.crawler.model.CrawlData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class CrawlCompletedEvent {
    private final CrawlData crawlData;
    private final List<Page> crawledPages;
}
