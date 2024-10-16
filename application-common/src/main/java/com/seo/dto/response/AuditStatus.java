package com.seo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AuditStatus {
    
    @Schema(
            title = "Project name",
            description = "Project name of auditing website",
            example = "Apple website"
    )
    private String projectName;
    
    @Schema(
            title = "Domain",
            description = "URI Domain",
            example = "https://www.apple.com/"
    )
    private String domain;
    
    @Schema(
            title = "Audited pages",
            description = "Number of audited pages",
            example = "133"
    )
    private long auditedPages;
    
    @Schema(
            title = "Remained pages",
            description = "Number of pages to audit",
            example = "1050"
    )
    private long remainedPages;
    
    @Schema(
            title = "Broken pages count",
            description = "Number of broken links found during audit",
            example = "3"
    )
    private long brokenPagesCount;
    
    @Schema(
            title = "Status",
            description = "Status of current task",
            example = "RUNNING",
            allowableValues = {"RUNNING", "STOPPED", "ERROR"}
    )
    private Status status;
    
    public enum Status {
        RUNNING,
        STOPPED,
        ERROR
    }
}
