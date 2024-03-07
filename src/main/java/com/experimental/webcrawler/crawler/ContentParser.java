package com.experimental.webcrawler.crawler;

import com.experimental.webcrawler.crawler.model.Content;

public interface ContentParser {
    Content parseContent(String htmlSource);
}
