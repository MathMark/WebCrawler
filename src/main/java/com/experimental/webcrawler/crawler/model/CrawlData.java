package com.experimental.webcrawler.crawler.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.Map;

@RequiredArgsConstructor
public class CrawlData {
    private final String id;
    private final Website website;
    private final BlockingQueue<Page> internalLinks = new LinkedBlockingDeque<>();
    private final Set<Page> externalLinks = new HashSet<>();
    private final List<BrokenPage> brokenPages = Collections.synchronizedList(new ArrayList<>());
    @Getter
    private final Map<String, Page> crawledPages = new ConcurrentHashMap<>();
    
    private final Object externalLinkMonitor = new Object();
    private final Object websiteMonitor = new Object();
    
    public BlockingQueue<Page> getInternalLinks() {
        return internalLinks;
    }
    
    public List<BrokenPage> getBrokenPages() {
        return brokenPages;
    }

    public Set<Page> getExternalLinks() {
        synchronized (externalLinkMonitor) {
            return externalLinks;
        }
    }

    public Website getWebsite() {
        synchronized (websiteMonitor) {
            return website;
        }
    }
    
    public synchronized String getId() {
        return this.id;
    }
}
