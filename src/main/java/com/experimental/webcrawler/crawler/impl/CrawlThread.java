package com.experimental.webcrawler.crawler.impl;

import com.experimental.webcrawler.crawler.CompletableRunnable;
import com.experimental.webcrawler.crawler.ContentParser;
import com.experimental.webcrawler.crawler.CrawlClient;
import com.experimental.webcrawler.crawler.Parser;
import com.experimental.webcrawler.crawler.ThreadCompleteListener;
import com.experimental.webcrawler.crawler.model.BrokenWebPage;
import com.experimental.webcrawler.crawler.model.ConnectionResponse;
import com.experimental.webcrawler.crawler.model.CrawlData;
import com.experimental.webcrawler.crawler.model.WebPage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public class CrawlThread implements CompletableRunnable {
    
    private final String id;
    private final WebPage startPage;
    private final CrawlData crawlData;
    private final AtomicBoolean isRequestedToStop = new AtomicBoolean();
    private final List<ThreadCompleteListener> listeners = new ArrayList<>();
    private final Parser parser;
    private final ContentParser contentParser;
    private final CrawlClient crawlClient;

    public synchronized void stop() {
        isRequestedToStop.set(true);
    }

    @Override
    public void addThreadCompleteListener(ThreadCompleteListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeThreadCompleteListener(ThreadCompleteListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (ThreadCompleteListener listener : listeners) {
            listener.onThreadComplete(this.id);
        }
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
            } else {
                parser.parseLinks(webPage, connectionResponse);
            }
            
            //contentParser.parseContent()
        }
        notifyListeners();
    }
}

