package com.experimental.webcrawler.dto.crawl;

import com.experimental.webcrawler.crawler.impl.CrawlTask;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CrawlStatus {
    private String projectName;
    private String domain;
    private long crawledPages;
    private long remainedPages;
    private long brokenPagesCount;
    private CrawlTask.Status status;
}
