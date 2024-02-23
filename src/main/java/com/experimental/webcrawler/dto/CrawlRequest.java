package com.experimental.webcrawler.dto;

import lombok.Data;

@Data
public class CrawlRequest {
    private String startUrl;
    private int threadsCount;
    
    // TODO: validate input data
    // TODO: add delay value
}
