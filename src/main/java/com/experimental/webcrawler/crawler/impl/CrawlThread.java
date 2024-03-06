package com.experimental.webcrawler.crawler.impl;

import com.experimental.webcrawler.crawler.CompletableRunnable;
import com.experimental.webcrawler.crawler.Parser;
import com.experimental.webcrawler.crawler.ThreadCompleteListener;
import com.experimental.webcrawler.crawler.model.CrawlData;
import com.experimental.webcrawler.crawler.model.WebPage;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public class CrawlThread implements CompletableRunnable {
    
    private final String id;
    private final WebPage startPage;
    private final CrawlData crawlData;
    private final AtomicBoolean isRequestedToStop = new AtomicBoolean();
    private final List<ThreadCompleteListener> listeners = new ArrayList<>();
    private final Parser parser;

    public synchronized void stop() {
        isRequestedToStop.set(true);
    }

    @Override
    public void addThreadCompleteListener(ThreadCompleteListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeThreadCompleteListener(ThreadCompleteListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (ThreadCompleteListener listener : listeners) {
            listener.onThreadComplete(this.id);
        }
    }

    private boolean isStopped() {
        return this.crawlData.getInternalLinks().isEmpty() ||
                Thread.currentThread().isInterrupted()
                || isRequestedToStop.get();
    }

    @Override
    public void run() {
        isRequestedToStop.set(false);
        parser.parseLinks(this.startPage);
        while (!isStopped()) {
            parser.parseLinks(this.crawlData.getInternalLinks().poll());
        }
        notifyListeners();
    }
}

