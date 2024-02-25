package com.experimental.webcrawler.crawler;

public interface CompletableRunnable extends Runnable {
    /**
     * Sends a signal to a thread to stop, so thread has possibility
     * to clear resources finish its work gracefully.
     */
    void stop();
}
