package com.experimental.webcrawler.dto;

import com.experimental.webcrawler.crawler.WebCrawler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CrawlStatus {
    private long crawledPages;
    private long foundPage;
    private WebCrawler.Status status;
}
