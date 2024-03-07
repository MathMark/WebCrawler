package com.experimental.webcrawler.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ContentEntity {
    private String title;
    private String metaDescription;
    private String metaRobots;
    private List<String> h1;
    private List<String> h2;
    private List<String> h3;
    private List<String> h4;
    private List<String> h5;
    private List<String> h6;
}
