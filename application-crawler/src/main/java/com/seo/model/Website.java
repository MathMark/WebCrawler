package com.seo.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Website {
    private final String projectName;
    private final String startUrl;
    private final String domain;
}
