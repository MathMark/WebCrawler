package com.experimental.webcrawler.crawler;

import com.experimental.webcrawler.model.PageLink;
import com.experimental.webcrawler.model.Website;
import com.experimental.webcrawler.validator.UrlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

@Slf4j
public class WebCrawler implements ThreadCompleteListener {

    private final Website website;
    private final Set<String> synchronizedUrls;
    private final Map<String, CompletableRunnable> threads = new ConcurrentHashMap<>();
    private ExecutorService executorService;
    private Status status;

    public WebCrawler(Website website) {
        this.website = website;
        this.synchronizedUrls = Collections.synchronizedSet(new HashSet<>());
    }

    public void crawl(int threadCount) {
        scanLinks(this.website.getUrl(), website);
        List<PageLink> startLinks = website.getInternalLinks().stream().limit(threadCount).toList();
        executorService = Executors.newFixedThreadPool(threadCount);
        log.info("Starting crawling website {} with {} threads", website.getDomain(), threadCount);
        
        for (PageLink startLink : startLinks) {
            String id = UUID.randomUUID().toString();
            CrawlingThread thread = new CrawlingThread(id, startLink.getHref(), this.website, this.synchronizedUrls, this);
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
        status = Status.STOPPED;
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        threads.clear();
    }

    public int getFoundPagesCount() {
        return this.website.getInternalLinks().size();
    }

    public int getCrawledPagesCount() {
        return this.synchronizedUrls.size();
    }

    public Status getStatus() {
        return this.status;
    }

    public static void scanLinks(String url, Website website) {
        try {
            int statusCode = UrlValidator.getStatusCode(url);
            if (statusCode == 200) {
                Document doc = Jsoup.connect(url).get();
                Elements pages = doc.select("a[href]");
                for (Element page : pages) {
                    String href = page.attr("href");
                    String text = page.text();
                    PageLink pageLink = new PageLink(href, text);
                    if (href.startsWith(website.getDomain())) {
                        website.getInternalLinks().add(pageLink);
                    } else {
                        website.getExternalLinks().add(pageLink);
                    }
                }
            } else {
                log.warn("Url {} does not reply. Status code is: {}", url, statusCode);
            }
        } catch (UnsupportedMimeTypeException ignored) {
        } catch (IOException e) {
            log.warn("Exception while trying to scan page {}.", url, e);
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
        private final Set<String> synchronizedUrls;
        private final AtomicBoolean isRequestedToStop = new AtomicBoolean();
        private final ThreadCompleteListener listener;


        public void scan(String url) throws IOException {
            if (!synchronizedUrls.contains(url)) {
                synchronizedUrls.add(url);
                log.info("Scanning internal page {}", url);
                WebCrawler.scanLinks(url, website);
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
