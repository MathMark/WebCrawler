package com.experimental.webcrawler.crawler;

import com.experimental.webcrawler.crawler.model.WebPage;

public interface Parser {
    void parseLinks(WebPage webPage);
}
