package com.seo.parser;

import com.seo.model.ConnectionResponse;
import com.seo.model.Content;
import com.seo.model.WebPage;

public interface Parser {
    Content parseContent(String htmlSource);
    void parseLinks(WebPage webPage, ConnectionResponse connectionResponse);
}
