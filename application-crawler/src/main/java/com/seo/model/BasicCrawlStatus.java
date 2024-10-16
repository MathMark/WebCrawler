package com.seo.model;

import com.seo.dto.response.AuditStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BasicCrawlStatus {
    private String taskId;
    private String domain;
    private String projectName;
    private AuditStatus.Status status;
}
