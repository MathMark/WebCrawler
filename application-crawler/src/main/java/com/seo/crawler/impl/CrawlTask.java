package com.seo.crawler.impl;

import com.seo.crawler.CompletableRunnable;
import com.seo.dto.response.AuditStatus;
import com.seo.crawler.ConnectionClient;
import com.seo.crawler.CrawlExecutor;
import com.seo.parser.Parser;
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
import java.util.concurrent.CompletableFuture;
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
    private ExecutorService executorService;
    @Autowired
    private ObjectProvider<ConnectionClient> crawlClientProvider;

    @Getter
    private final CrawlData crawlData;

    @Getter
    private AuditStatus.Status status;
    
    private final List<CompletableRunnable> threads = Collections.synchronizedList(new ArrayList<>());
    private CountDownLatch countDownLatch;

    public CompletableFuture<CrawlData> asyncCrawl() {
        return CompletableFuture.supplyAsync(() -> {
            crawl();
            return crawlData;
        });
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
        }
    }

    private void crawl() {
        WebPage pageToCrawl = new WebPage();
        String startUrl = this.crawlData.getWebsite().startUrl();
        pageToCrawl.setUrl(startUrl);
        ConnectionClient crawlClient = crawlClientProvider.getObject();
        Optional<ConnectionResponse> connectionResponseOptional = crawlClient.connect(startUrl);
        if (connectionResponseOptional.isEmpty()) {
            return;
        }
        parser.parseLinks(pageToCrawl, connectionResponseOptional.get());

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
            ConnectionClient connectionClient = crawlClientProvider.getObject();
            CompletableRunnable thread = new CrawlThread(startLink, this.crawlData, parser, connectionClient, countDownLatch);
            threads.add(thread);
            executorService.execute(thread);
        }
        status = AuditStatus.Status.RUNNING;
    }
    

}
