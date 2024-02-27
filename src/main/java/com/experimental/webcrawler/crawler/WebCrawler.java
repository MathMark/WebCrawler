package com.experimental.webcrawler.crawler;

import com.experimental.webcrawler.crawler.model.Page;
import com.experimental.webcrawler.crawler.model.Website;
import com.experimental.webcrawler.parser.WebParser;
import com.experimental.webcrawler.service.CrawlCompleteListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
public class WebCrawler implements ThreadCompleteListener {

    private final Website website;
    private final Set<Page> scannedPages;
    private final Map<String, CompletableRunnable> threads = new ConcurrentHashMap<>();
    private ExecutorService executorService;
    private Status status;
    private final WebParser parser = new WebParser();
    private final List<CrawlCompleteListener> listeners = new ArrayList<>();

    public WebCrawler(Website website) {
        this.website = website;
        this.scannedPages = Collections.synchronizedSet(new HashSet<>());
    }

    public void addListener(CrawlCompleteListener listener) {
        listeners.add(listener);
    }

    public void removeListener(CrawlCompleteListener listener) {
        listeners.remove(listener);
    }

    public int getRemainedPagesCount() {
        return this.website.getInternalLinks().size();
    }

    public int getCrawledPagesCount() {
        return this.scannedPages.size();
    }

    public Status getStatus() {
        return this.status;
    }

    public int getBrokenLinksCount() {
        return this.website.getBrokenPages().size();
    }

    public void crawl(int threadCount) {
        Page pageToCrawl = new Page();
        pageToCrawl.setCurrentUrl(this.website.getUrl());
        parser.parseLinks(pageToCrawl, website);
        List<Page> startLinks = website.getInternalLinks().stream()
                .limit(threadCount)
                .collect(Collectors.toList());
        if (startLinks.isEmpty()) {
            status = Status.ERROR;
            log.error("Couldn't find amy links on start page.");
            return;
        } else if (startLinks.size() < threadCount) {
            threadCount = startLinks.size();
        }
        executorService = Executors.newFixedThreadPool(threadCount);
        log.info("Starting crawling website {} with {} threads", website.getDomain(), threadCount);

        for (Page startLink : startLinks) {
            String id = UUID.randomUUID().toString();
            CompletableRunnable thread = new CrawlingThread(id, startLink,
                    this.website,
                    this.scannedPages,
                    parser);
            thread.addThreadCompleteListener(this);
            threads.put(id, thread);
            executorService.execute(thread);
        }
        status = Status.RUNNING;
    }

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
            listener.onCrawlCompete(website);
        }
    }

    @RequiredArgsConstructor
    public static class CrawlingThread implements CompletableRunnable {

        private final String id;
        private final Page startPage;
        private final Website website;
        private final Set<Page> scannedPages;
        private final AtomicBoolean isRequestedToStop = new AtomicBoolean();
        private final List<ThreadCompleteListener> listeners = new ArrayList<>();
        private final WebParser parser;

        public void scan(Page page) {
            if (!scannedPages.contains(page)) {
                scannedPages.add(page);
                log.info("Scanning internal page {}", page.getCurrentUrl());
                parser.parseLinks(page, website);
            }
        }

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
            return this.website.getInternalLinks().isEmpty() ||
                    Thread.currentThread().isInterrupted()
                    || isRequestedToStop.get();
        }

        @Override
        public void run() {
            isRequestedToStop.set(false);
            scan(this.startPage);
            while (!isStopped()) {
                scan(this.website.getInternalLinks().poll());
            }
            notifyListeners();
        }
    }

    public enum Status {
        RUNNING,
        STOPPED,
        ERROR
    }
}
