package com.seo.crawler;

import com.seo.model.ConnectionResponse;
import com.seo.model.WebPage;

public interface Parser {
    void parseLinks(WebPage webPage, ConnectionResponse connectionResponse);
}
