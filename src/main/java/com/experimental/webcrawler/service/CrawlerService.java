package com.experimental.webcrawler.service;

import com.experimental.webcrawler.crawler.WebCrawler;
import com.experimental.webcrawler.dto.CrawlResponse;
import com.experimental.webcrawler.dto.CrawlStatus;
import com.experimental.webcrawler.exception.TaskNotFoundException;
import com.experimental.webcrawler.model.Website;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;

@Service
public class CrawlerService {
    
    private static final Pattern pattern = Pattern.compile("^(https?://[^/]+)");
    private final Map<String, WebCrawler> processes = new HashMap<>();
    
    public CrawlResponse startCrawling(String url, int threadCount) {
        String domain = cutDomain(url);
        Website website = new Website(url, domain);
        WebCrawler webCrawler = new WebCrawler(website);
        webCrawler.crawl(threadCount);
        String taskId = UUID.randomUUID().toString();
        processes.put(taskId, webCrawler);
        return CrawlResponse.builder()
                .initialUrl(url)
                .domain(domain)
                .taskId(taskId)
                .build();
    }
    
    public CrawlStatus getCrawlStatus(String taskId) {
        WebCrawler webCrawler = processes.get(taskId);
        if (webCrawler == null) {
            throw new TaskNotFoundException(String.format("Task with id %s not found.", taskId));
        }
        CrawlStatus crawlStatus = new CrawlStatus();
        crawlStatus.setCrawledPages(webCrawler.getCrawledPagesCount());
        crawlStatus.setFoundPage(webCrawler.getFoundPagesCount());
        crawlStatus.setStatus(webCrawler.getStatus());
        return crawlStatus;
    }
    
    public void stopCrawling(String taskId) {
        WebCrawler webCrawler = processes.get(taskId);
        if (webCrawler == null) {
            throw new TaskNotFoundException(String.format("Task with id %s not found.", taskId));
        }
        webCrawler.shutDown();
    }
    
    private String cutDomain(String url) {
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }
}
