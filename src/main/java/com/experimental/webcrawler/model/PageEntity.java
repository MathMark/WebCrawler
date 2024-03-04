package com.experimental.webcrawler.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PageEntity {
    private String url;
    private String title;
    private String description;
    private String robotsContent;
}
