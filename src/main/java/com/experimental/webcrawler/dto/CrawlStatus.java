package com.experimental.webcrawler.dto;

import com.experimental.webcrawler.crawler.CrawlTask;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CrawlStatus {
    private long crawledPages;
    private long remainedPages;
    private long brokenPagesCount;
    private CrawlTask.Status status;
}
