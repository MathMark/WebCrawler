package com.seo.model;

import lombok.Data;

import java.util.List;

@Data
public class Content {
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
