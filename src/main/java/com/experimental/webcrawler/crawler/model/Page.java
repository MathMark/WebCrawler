package com.experimental.webcrawler.crawler.model;

import lombok.Getter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Page {
    @Getter
    private final Set<Link> incomingLinks = Collections.synchronizedSet(new HashSet<>());
    private String url;
    private String title;
    private String description;
    private String robotsContent;
    
    private final Object currentUrlMonitor = new Object();
    private final Object titleMonitor = new Object();
    private final Object descriptionMonitor = new Object();
    private final Object robotsContentMonitor = new Object();
    
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

    public String getTitle() {
        synchronized (titleMonitor) {
            return title;
        }
    }

    public void setTitle(String title) {
        synchronized (titleMonitor) {
            this.title = title;
        }
    }

    public String getDescription() {
        synchronized (descriptionMonitor) {
            return description;
        }
    }

    public void setDescription(String description) {
        synchronized (descriptionMonitor) {
            this.description = description;
        }
    }

    public String getRobotsContent() {
        synchronized (robotsContentMonitor) {
            return robotsContent;
        }
    }

    public void setRobotsContent(String robotsContent) {
        synchronized (robotsContentMonitor) {
            this.robotsContent = robotsContent;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page page = (Page) o;
        return Objects.equals(getUrl(), page.getUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl());
    }
}
