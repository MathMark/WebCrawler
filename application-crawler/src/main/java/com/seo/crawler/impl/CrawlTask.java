package com.seo.crawler.impl;

import com.seo.crawler.CompletableRunnable;
import com.seo.dto.response.AuditStatus;
import com.seo.parser.ContentParser;
import com.seo.crawler.CrawlClient;
import com.seo.crawler.CrawlCompleteListener;
import com.seo.crawler.CrawlExecutor;
import com.seo.parser.Parser;
import com.seo.crawler.event.CrawlCompletedEvent;
import com.seo.model.ConnectionResponse;
import com.seo.model.WebPage;
import com.seo.model.CrawlData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

@Slf4j
@RequiredArgsConstructor
public class CrawlTask implements CrawlExecutor {

    @Autowired
    private ObjectProvider<ExecutorService> executorServiceProvider;

    private final int threadCount;
    @Getter
    private final CrawlData crawlData;
    private final Parser parser;
    private final ContentParser contentParser;
    private ExecutorService executorService;
    private final CrawlClient crawlClient;
    @Getter
    private AuditStatus.Status status;
    private final List<CrawlCompleteListener> listeners = new ArrayList<>();
    private final List<CompletableRunnable> threads = new ArrayList<>();
    private CountDownLatch countDownLatch;
    
    public void addListener(CrawlCompleteListener listener) {
        listeners.add(listener);
    }

    public void removeListener(CrawlCompleteListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void run() {
        crawl();
    }
    
    private void crawl() {
        WebPage pageToCrawl = new WebPage();
        String startUrl = this.crawlData.getWebsite().startUrl();
        pageToCrawl.setUrl(startUrl);
        ConnectionResponse connectionResponse = crawlClient.connect(startUrl);
        parser.parseLinks(pageToCrawl, connectionResponse);
        int computedThreadsCount = threadCount;
        List<WebPage> startLinks = crawlData.getInternalLinks().stream()
                .limit(computedThreadsCount)
                .toList();
        if (startLinks.isEmpty()) {
            status = AuditStatus.Status.ERROR;
            log.error("Couldn't find amy links on start page.");
            return;
        } else if (startLinks.size() < computedThreadsCount) {
            computedThreadsCount = startLinks.size();
        }
        executorService = executorServiceProvider.getObject(computedThreadsCount);
        log.info("Starting crawling website {} with {} threads", crawlData.getWebsite().domain(), computedThreadsCount);
        countDownLatch = new CountDownLatch(computedThreadsCount);
        for (WebPage startLink : startLinks) {
            CompletableRunnable thread = new CrawlThread(startLink, this.crawlData, parser, contentParser, crawlClient, countDownLatch);
            threads.add(thread);
            executorService.execute(thread);
        }

        status = AuditStatus.Status.RUNNING;
        complete();
    }

    @Override
    public void requestToStop() {
        log.info("Crawling requested to stop.");
        for (CompletableRunnable thread : threads) {
            thread.stop();
        }
        if (countDownLatch != null) {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    public void complete() {
        if (countDownLatch != null) {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                log.error("Crawling completion has been interrupted.", e);
                Thread.currentThread().interrupt();
            }
            status = AuditStatus.Status.STOPPED;
            if (executorService != null && !executorService.isShutdown()) {
                executorService.shutdown();
            }
            notifyListeners();
        }
    }

    private void notifyListeners() {
        for (CrawlCompleteListener listener : listeners) {
            CrawlCompletedEvent event = new CrawlCompletedEvent(crawlData);
            listener.onCrawlCompete(event);
        }
    }

    
    
}
