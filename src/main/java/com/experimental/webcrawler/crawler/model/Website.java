package com.experimental.webcrawler.crawler.model;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

@RequiredArgsConstructor
public class Website {
    private final String url;
    private final String domain;
    
    private final BlockingQueue<Page> internalLinks = new LinkedBlockingDeque<>();
    private final Set<Page> externalLinks = new HashSet<>();
    private final List<BrokenPage> brokenPages = Collections.synchronizedList(new ArrayList<>());
    
    private final Object externalLinkMonitor = new Object();
    private final Object urlMonitor = new Object();
    private final Object domainMonitor = new Object();

    public String getUrl() {
        synchronized (urlMonitor) {
            return url;
        }
    }

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
    
    public String getDomain() {
        synchronized (domainMonitor) {
            return domain;
        }
    }
    
}
