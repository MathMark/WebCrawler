package com.experimental.webcrawler.service;

import com.experimental.webcrawler.dto.CrawlResponse;
import com.experimental.webcrawler.model.Website;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CrawlerService {
    
    private static final Pattern pattern = Pattern.compile("^(https?://[^/]+)");
    
    public CrawlResponse startCrawling(String url, int threadCount) {
        String domain = cutDomain(url);
        Website website = new Website(url, domain);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        CrawlInitializer crawlInitializer = new CrawlInitializer(website, countDownLatch);
        crawlInitializer.crawl(threadCount);
        
        return CrawlResponse.builder()
                .initialUrl(url)
                .domain(domain)
                .taskId(crawlInitializer.getId())
                .build();
    }
    
    private String cutDomain(String url) {
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }
}
