package com.seo.model;

import lombok.Getter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class WebPage {
    @Getter
    private final Set<Link> incomingLinks = Collections.synchronizedSet(new HashSet<>());
    private String url;
    private Content content;
    
    private final Object currentUrlMonitor = new Object();
    private final Object contentMonitor = new Object();
    
    public String getUrl() {
        synchronized (currentUrlMonitor) {
            return url;
        }
    }

    public void setUrl(String url) {
        synchronized (currentUrlMonitor) {
            this.url = url;
        }
    }

    public Content getContent() {
        synchronized (contentMonitor) {
            return content;
        }
    }

    public void setContent(Content content) {
        synchronized (contentMonitor) {
            this.content = content;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebPage page = (WebPage) o;
        return Objects.equals(getUrl(), page.getUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl());
    }
}
