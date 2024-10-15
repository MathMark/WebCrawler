package com.seo.crawler;

import com.seo.model.Content;

public interface ContentParser {
    Content parseContent(String htmlSource);
}
