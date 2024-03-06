package com.experimental.webcrawler.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PageEntity {
    private String url;
    private String title;
    private String description;
    private String robotsContent;
    private List<LinkEntity> incomingLinks;
}
