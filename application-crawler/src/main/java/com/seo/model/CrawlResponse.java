package com.seo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CrawlResponse {
    private String initialUrl;
    private String projectName;
    private String domain;
    private String taskId;
    private String websiteProjectId;
}
