package com.experimental.webcrawler.crawler;

public interface ThreadCompleteListener {
    /**
     * Notifies a listener that a certain thread has finished its work.
     * @param threadId UUID of a thread that should be generated during thread creation.
     */
    void notifyOnThreadComplete(final String threadId);
}
