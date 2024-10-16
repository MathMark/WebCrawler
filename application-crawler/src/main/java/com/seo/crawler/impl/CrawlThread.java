package com.seo.crawler.impl;

import com.seo.crawler.CompletableRunnable;
import com.seo.parser.ContentParser;
import com.seo.crawler.CrawlClient;
import com.seo.parser.Parser;
import com.seo.model.BrokenWebPage;
import com.seo.model.ConnectionResponse;
import com.seo.model.Content;
import com.seo.model.CrawlData;
import com.seo.model.WebPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
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
        Optional<ConnectionResponse> initConnectionResponse = connect(this.startPage.getUrl());
        BlockingQueue<WebPage> queue = crawlData.getInternalLinks();
        initConnectionResponse.ifPresent(response -> {
            parser.parseLinks(this.startPage, response);
            while (!isStopped()) {
                WebPage nextPage = queue.poll();
                Optional<ConnectionResponse> connectionResponse = connect(nextPage.getUrl());
                connectionResponse.ifPresent(nextPageResponse -> {
                    auditPage(nextPageResponse, nextPage);
                });
            }
        });

        latch.countDown();
        log.info("Thread {} exited", Thread.currentThread().getName());
    }

    private Optional<ConnectionResponse> connect(String uri) {
        try {
            return Optional.of(crawlClient.connect(uri));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private void auditPage(ConnectionResponse connectionResponse, WebPage webPage) {
        if (isBroken(connectionResponse.getHttpStatus())) {
            BrokenWebPage brokenWebPage = BrokenWebPage.builder()
                    .webPage(webPage)
                    .statusCode(connectionResponse.getHttpStatus().value())
                    .build();
            crawlData.getBrokenWebPages().add(brokenWebPage);
        } else {
            parser.parseLinks(webPage, connectionResponse);
            Content content = contentParser.parseContent(connectionResponse.getHtmlBody());
            webPage.setContent(content);
        }
    }
}

