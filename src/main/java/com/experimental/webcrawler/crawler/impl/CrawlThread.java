package com.experimental.webcrawler.crawler.impl;

import com.experimental.webcrawler.crawler.CompletableRunnable;
import com.experimental.webcrawler.crawler.ContentParser;
import com.experimental.webcrawler.crawler.CrawlClient;
import com.experimental.webcrawler.crawler.Parser;
import com.experimental.webcrawler.crawler.model.BrokenWebPage;
import com.experimental.webcrawler.crawler.model.ConnectionResponse;
import com.experimental.webcrawler.crawler.model.Content;
import com.experimental.webcrawler.crawler.model.CrawlData;
import com.experimental.webcrawler.crawler.model.WebPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@Slf4j
public class CrawlThread implements CompletableRunnable {
    
    private final WebPage startPage;
    private final CrawlData crawlData;
    private final AtomicBoolean isRequestedToStop = new AtomicBoolean();
    private final Parser parser;
    private final ContentParser contentParser;
    private final CrawlClient crawlClient;
    private final CountDownLatch latch;
    //TODO: Use separate http client for each thread!!!

    public synchronized void stop() {
        isRequestedToStop.set(true);
    }
    

    private boolean isStopped() {
        return this.crawlData.getInternalLinks().isEmpty() ||
                Thread.currentThread().isInterrupted()
                || isRequestedToStop.get();
    }
    
    private boolean isBroken(HttpStatus httpStatus) {
        return httpStatus == null || httpStatus.is4xxClientError();
    }

    @Override
    public void run() {
        isRequestedToStop.set(false);
        ConnectionResponse initConnectionResponse = crawlClient.connect(this.startPage.getUrl());
        parser.parseLinks(this.startPage, initConnectionResponse);
        while (!isStopped()) {
            WebPage webPage = crawlData.getInternalLinks().poll();
            ConnectionResponse connectionResponse = crawlClient.connect(webPage.getUrl());
            if (isBroken(connectionResponse.getHttpStatus())) {
                BrokenWebPage brokenWebPage = BrokenWebPage.builder()
                        .webPage(webPage)
                        .statusCode(connectionResponse.getHttpStatus().value())
                        .build();
                crawlData.getBrokenWebPages().add(brokenWebPage);
            } else  {
                parser.parseLinks(webPage, connectionResponse);
                Content content = contentParser.parseContent(connectionResponse.getHtmlBody());
                webPage.setContent(content);
            } 
        }
        latch.countDown();
        log.info(String.format("Thread %s exited", Thread.currentThread().getName()));
    }
}

