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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class CrawlTask implements CrawlExecutor {

    @Autowired
    private ObjectProvider<ExecutorService> executorServiceProvider;
    private final int threadCount;
    private final Parser parser;
    private final ContentParser contentParser;
    private ExecutorService executorService;
    private final CrawlClient crawlClient;

    @Getter
    private final CrawlData crawlData;

    @Getter
    private AuditStatus.Status status;

    private final List<CrawlCompleteListener> listeners = Collections.synchronizedList(new ArrayList<>());
    private final List<CompletableRunnable> threads = Collections.synchronizedList(new ArrayList<>());
    private CountDownLatch countDownLatch;

    @Override
    public void run() {
        crawl();
    }

    public void addListener(CrawlCompleteListener listener) {
        listeners.add(listener);
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
                log.error("Thread was interrupted.", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    public void shutdownIfCompleted() {
        if (countDownLatch != null) {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                log.error("Crawling completion has been interrupted.", e);
                Thread.currentThread().interrupt();
            }
            status = AuditStatus.Status.STOPPED;
            if (executorService != null && !executorService.isShutdown()) {
                try {
                    if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                        executorService.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executorService.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
            notifyListeners();
        }
    }

    private void crawl() {
        WebPage pageToCrawl = new WebPage();
        String startUrl = this.crawlData.getWebsite().startUrl();
        pageToCrawl.setUrl(startUrl);
        ConnectionResponse connectionResponse = crawlClient.connect(startUrl);
        parser.parseLinks(pageToCrawl, connectionResponse);

        BlockingQueue<WebPage> internalLinks = crawlData.getInternalLinks();
        Optional<Integer> computedThreadsCountOptional = computeNumberOfThreads(internalLinks);
        computedThreadsCountOptional.ifPresent(numberOfThreads -> {
            List<WebPage> initialPages = internalLinks.stream().limit(numberOfThreads).toList();
            executorService = executorServiceProvider.getObject(numberOfThreads);
            log.info("Starting crawling website {} with {} threads", crawlData.getWebsite().domain(), numberOfThreads);
            initiateThreads(initialPages, numberOfThreads);
            shutdownIfCompleted();
        });
    }

    private Optional<Integer> computeNumberOfThreads(BlockingQueue<WebPage> internalLinks) {
        if (internalLinks.isEmpty()) {
            status = AuditStatus.Status.ERROR;
            log.error("Couldn't find any links on start page.");
            return Optional.empty();
        }
        int computedThreadsCount = Math.min(threadCount, internalLinks.size());
        return Optional.of(computedThreadsCount);
    }

    private void initiateThreads(Collection<WebPage> initialPages, int threadsCount) {
        countDownLatch = new CountDownLatch(threadsCount);
        for (WebPage startLink : initialPages) {
            CompletableRunnable thread = new CrawlThread(startLink, this.crawlData, parser, contentParser, crawlClient, countDownLatch);
            threads.add(thread);
            executorService.execute(thread);
        }
        status = AuditStatus.Status.RUNNING;
    }

    private void notifyListeners() {
        for (CrawlCompleteListener listener : listeners) {
            try {
                CrawlCompletedEvent event = new CrawlCompletedEvent(crawlData);
                listener.onCrawlCompete(event);
            } catch (Exception e) {
                log.error("Error notifying listener: {}", listener, e);
            }
        }
    }
}
