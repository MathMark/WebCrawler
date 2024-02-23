package com.experimental.webcrawler.service;

import com.experimental.webcrawler.model.PageLink;
import com.experimental.webcrawler.model.Website;
import com.experimental.webcrawler.validator.UrlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Map;

@Slf4j
public class CrawlInitializer {

    private final Website website;
    private final Set<String> synchronizedUrls;
    private final Map<String, Runnable> threads = new ConcurrentHashMap<>();
    private final CountDownLatch countDownLatch;


    public CrawlInitializer(Website website, CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
        this.website = website;
        this.synchronizedUrls = Collections.synchronizedSet(new HashSet<>());
    }

    public void crawl(int threadCount) throws IOException {
        scanLinks(this.website.getUrl(), website);
        List<PageLink> startLinks = website.getInternalLinks().stream().limit(threadCount).toList();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        log.info("Starting crawling website {} with {} threads", website.getDomain(), threadCount);
        for (PageLink startLink : startLinks) {
            Runnable thread = new AsyncWebCrawler(startLink.getHref(), this.website, this.synchronizedUrls, countDownLatch);
            String id = UUID.randomUUID().toString();
            threads.put(id, thread);
            executorService.execute(thread);
        }
    }

    public synchronized int getFoundPagesCount() {
        return this.website.getInternalLinks().size();
    }

    public int getCrawledPagesCount() {
        return this.synchronizedUrls.size();
    }

    public Runnable getThread(String id) {
        return threads.get(id);
    }

    public static void scanLinks(String url, Website website) throws IOException {
        int statusCode = UrlValidator.getStatusCode(url);
        if (statusCode == 200) {
            Document doc = Jsoup.connect(url).get();
            Elements pages = doc.select("a");
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
    }

    @RequiredArgsConstructor
    public static class AsyncWebCrawler implements Runnable {

        private final String startUrl;
        private final Website website;
        private final Set<String> synchronizedUrls;
        private final CountDownLatch countDownLatch;


        public void scan(String url) throws IOException {
            if (!synchronizedUrls.contains(url)) {
                synchronizedUrls.add(url);
                log.info("Scanning internal page {}", url);
                CrawlInitializer.scanLinks(url, website);
            }
        }


        @Override
        public void run() {
            try {
                scan(this.startUrl);
                while (this.synchronizedUrls.size() < 100) {
                    Thread.sleep(100);
                    scan(this.website.getInternalLinks().poll().getHref());
                    log.info("Internal links size {}", this.website.getInternalLinks().size());
                }
            } catch (IOException e) {
                log.warn(e.getMessage(), e);
            } catch (InterruptedException e) {
                log.warn(e.getMessage(), e);
                Thread.currentThread().interrupt();
            } finally {
                countDownLatch.countDown();
            }
        }
    }
}
