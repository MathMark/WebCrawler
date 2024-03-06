package com.experimental.webcrawler.crawler;

import com.experimental.webcrawler.crawler.model.Page;

public interface Parser {
    void parseLinks(Page page);
}
