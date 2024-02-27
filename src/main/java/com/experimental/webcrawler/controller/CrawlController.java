package com.experimental.webcrawler.controller;

import com.experimental.webcrawler.dto.CrawlRequest;
import com.experimental.webcrawler.dto.CrawlResponse;
import com.experimental.webcrawler.dto.CrawlStatus;
import com.experimental.webcrawler.service.CrawlerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/crawl")
@RequiredArgsConstructor
public class CrawlController {
    
    private final CrawlerService crawlerService;
    
    @PostMapping("/start")
    public ResponseEntity<CrawlResponse> startCrawl(@RequestBody @Valid CrawlRequest crawlRequest) {
        CrawlResponse crawlResponse = crawlerService.startCrawling(crawlRequest.getStartUrl(), crawlRequest.getThreadsCount());
        return ResponseEntity.ok(crawlResponse);
    }
    
    @GetMapping("/status")
    public ResponseEntity<CrawlStatus> getCrawlStatus(@RequestParam String taskId) {
        CrawlStatus crawlStatus = crawlerService.getCrawlStatus(taskId);
        return ResponseEntity.ok(crawlStatus);
    }
    
    @GetMapping("/stop")
    public ResponseEntity<CrawlStatus> stopCrawl(@RequestParam String taskId) {
        CrawlStatus crawlStatus = crawlerService.stopCrawling(taskId);
        return ResponseEntity.ok(crawlStatus);
    }

    /** TODO: add the following endpoints:
     * pause crawling
     * get reports
     */
}
