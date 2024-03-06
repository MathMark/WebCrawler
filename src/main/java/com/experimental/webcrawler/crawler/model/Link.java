package com.experimental.webcrawler.crawler.model;

import lombok.Data;

@Data
public class Link {
    private String url;
    private String hrefText;
}
