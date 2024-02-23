package com.experimental.webcrawler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CrawlResponse {
    private String domain;
    private String initialUrl;
    private String taskId;
}
