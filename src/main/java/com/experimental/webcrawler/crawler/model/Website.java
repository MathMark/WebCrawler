package com.experimental.webcrawler.crawler.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Website {
    private final String projectName;
    private final String startUrl;
    private final String domain;
}
