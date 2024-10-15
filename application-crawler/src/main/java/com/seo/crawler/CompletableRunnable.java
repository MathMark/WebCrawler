package com.seo.crawler;

public interface CompletableRunnable extends Runnable {
    /**
     * Sends a signal to a thread to stop, so thread has possibility
     * to clear resources and finish its work gracefully.
     */
    void stop();
}
