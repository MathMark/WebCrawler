package com.experimental.webcrawler.crawler;

import com.experimental.webcrawler.model.PageLink;
import com.experimental.webcrawler.model.Website;
import com.experimental.webcrawler.parser.WebParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
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
    private final Set<String> scannedPages;
    private final Map<String, CompletableRunnable> threads = new ConcurrentHashMap<>();
    private ExecutorService executorService;
    private Status status;
    private final WebParser parser = new WebParser();

    public WebCrawler(Website website) {
        this.website = website;
        this.scannedPages = Collections.synchronizedSet(new HashSet<>());
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
        parser.parseLinks(this.website.getUrl(), website);
        List<PageLink> startLinks = website.getInternalLinks().stream()
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

        for (PageLink startLink : startLinks) {
            String id = UUID.randomUUID().toString();
            CompletableRunnable thread = new CrawlingThread(id, startLink.getHref(), 
                    this.website, 
                    this.scannedPages, 
                    this, parser);
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
    public void notifyOnThreadComplete(final String threadId) {
        threads.remove(threadId);
        log.info("Thread {} exited.", threadId);
        if (threads.isEmpty()) {
            status = Status.STOPPED;
            if (executorService != null && !executorService.isShutdown()) {
                executorService.shutdown();
            }
        }
    }

    @RequiredArgsConstructor
    public static class CrawlingThread implements CompletableRunnable {

        private final String id;
        private final String startUrl;
        private final Website website;
        private final Set<String> scannedPages;
        private final AtomicBoolean isRequestedToStop = new AtomicBoolean();
        private final ThreadCompleteListener listener;
        private final WebParser parser;


        public void scan(String url) throws IOException {
            if (!scannedPages.contains(url)) {
                scannedPages.add(url);
                log.info("Scanning internal page {}", url);
                parser.parseLinks(url, website);
            }
        }

        public synchronized void stop() {
            isRequestedToStop.set(true);
        }

        private boolean isStopped() {
            return this.website.getInternalLinks().isEmpty() ||
                    Thread.currentThread().isInterrupted()
                    || isRequestedToStop.get();
        }

        @Override
        public void run() {
            isRequestedToStop.set(false);
            try {
                scan(this.startUrl);
                while (!isStopped()) {
                    scan(this.website.getInternalLinks().poll().getHref());
                }
                listener.notifyOnThreadComplete(this.id);
            } catch (IOException e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    public enum Status {
        RUNNING,
        STOPPED,
        ERROR
    }
}
