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
public class BasicCrawlStatus {
    private String taskId;
    private String domain;
    private String projectName;
    private CrawlTask.Status status;
}
