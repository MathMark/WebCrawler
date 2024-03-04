package com.experimental.webcrawler.crawler.model;

import java.util.Objects;

public class Page {
    private String previousUrl;
    private String currentUrl;
    private String hrefText;
    private String title;
    private String description;
    private String robotsContent;
    
    private final Object previousUrlMonitor = new Object();
    private final Object currentUrlMonitor = new Object();
    private final Object hrefTextMonitor  = new Object();
    private final Object titleMonitor = new Object();
    private final Object descriptionMonitor = new Object();
    private final Object robotsContentMonitor = new Object();

    public String getPreviousUrl() {
        synchronized (previousUrlMonitor) {
            return previousUrl;
        }
    }

    public void setPreviousUrl(String previousUrl) {
        synchronized (previousUrlMonitor) {
            this.previousUrl = previousUrl;
        }
    }

    public String getCurrentUrl() {
        synchronized (currentUrlMonitor) {
            return currentUrl;
        }
    }

    public void setCurrentUrl(String currentUrl) {
        synchronized (currentUrlMonitor) {
            this.currentUrl = currentUrl;
        }
    }

    public String getHrefText() {
        synchronized (hrefTextMonitor) {
             return hrefText;
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

    public void setHrefText(String hrefText) {
        this.hrefText = hrefText;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page page = (Page) o;
        return Objects.equals(getCurrentUrl(), page.getCurrentUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCurrentUrl());
    }
}
