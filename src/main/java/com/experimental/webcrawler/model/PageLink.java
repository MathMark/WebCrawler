package com.experimental.webcrawler.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
public class PageLink {
    private String href;
    private String text;
    
    private final Object hrefMonitor = new Object();
    private final Object textMonitor = new Object();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageLink pageLink = (PageLink) o;
        return getHref().equals(pageLink.getHref());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHref());
    }

    public String getHref() {
        synchronized (hrefMonitor) {
            return href;
        }
    }

    public void setHref(String href) {
        synchronized (hrefMonitor) {
            this.href = href;
        }
    }

    public String getText() {
        synchronized (textMonitor) {
            return text;
        }
    }

    public void setText(String text) {
        synchronized (textMonitor) {
            this.text = text;
        }
    }
}
