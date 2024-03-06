package com.experimental.webcrawler.crawler.impl;

import com.experimental.webcrawler.crawler.CompletableRunnable;
import com.experimental.webcrawler.crawler.CrawlExecutor;
import com.experimental.webcrawler.crawler.Parser;
import com.experimental.webcrawler.crawler.ThreadCompleteListener;
import com.experimental.webcrawler.crawler.model.WebPage;
import com.experimental.webcrawler.crawler.model.CrawlData;
import com.experimental.webcrawler.service.CrawlCompleteListener;
import com.experimental.webcrawler.service.event.CrawlCompletedEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class CrawlTask implements ThreadCompleteListener, CrawlExecutor {

    @Autowired
    private ObjectProvider<ExecutorService> executorServiceProvider;
    @Getter
    private final CrawlData crawlData;
    private final Parser parser;
    private ExecutorService executorService;
    private Status status;

    private final Map<String, CompletableRunnable> threads = new ConcurrentHashMap<>();
    private final List<CrawlCompleteListener> listeners = new ArrayList<>();


    public void addListener(CrawlCompleteListener listener) {
        listeners.add(listener);
    }

    public void removeListener(CrawlCompleteListener listener) {
        listeners.remove(listener);
    }

    public Status getStatus() {
        return this.status;
    }


    @Override
    public void crawl(int threadCount) {
        WebPage pageToCrawl = new WebPage();
        pageToCrawl.setUrl(this.crawlData.getWebsite().getStartUrl());
        parser.parseLinks(pageToCrawl);
        List<WebPage> startLinks = crawlData.getInternalLinks().stream()
                .limit(threadCount)
                .collect(Collectors.toList());
        if (startLinks.isEmpty()) {
            status = Status.ERROR;
            log.error("Couldn't find amy links on start page.");
            return;
        } else if (startLinks.size() < threadCount) {
            threadCount = startLinks.size();
        }
        executorService = executorServiceProvider.getObject(threadCount);
        log.info("Starting crawling website {} with {} threads", crawlData.getWebsite().getDomain(), threadCount);

        for (WebPage startLink : startLinks) {
            String id = UUID.randomUUID().toString();
            CompletableRunnable thread = new CrawlThread(id, startLink, this.crawlData, parser);
            thread.addThreadCompleteListener(this);
            threads.put(id, thread);
            executorService.execute(thread);
        }

        status = Status.RUNNING;
    }

    @Override
    public void shutDown() {
        log.info("Crawling requested to stop.");
        for (Map.Entry<String, CompletableRunnable> entry : threads.entrySet()) {
            entry.getValue().stop();
        }
    }

    @Override
    public void onThreadComplete(final String threadId) {
        threads.remove(threadId);
        log.info("Thread {} exited.", threadId);
        if (threads.isEmpty()) {
            status = Status.STOPPED;
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
    

    public enum Status {
        RUNNING,
        STOPPED,
        ERROR
    }
}
