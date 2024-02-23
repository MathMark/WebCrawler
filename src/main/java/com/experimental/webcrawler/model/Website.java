package com.experimental.webcrawler.model;

import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

@RequiredArgsConstructor
public class Website {
    private final String url;
    private BlockingQueue<PageLink> internalLinks = new LinkedBlockingDeque<>();
    private Set<PageLink> externalLinks = new HashSet<>();
    private final String domain;
    
    private final Object externalLinkMonitor = new Object();
    private final Object urlMonitor = new Object();
    private final Object domainMonitor = new Object();

    public String getUrl() {
        synchronized (urlMonitor) {
            return url;
        }
    }

    public BlockingQueue<PageLink> getInternalLinks() {
        return internalLinks;
    }

    public Set<PageLink> getExternalLinks() {
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
