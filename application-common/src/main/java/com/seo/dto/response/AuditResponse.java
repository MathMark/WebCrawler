package com.seo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditResponse {
    
    @Schema(
            title = "Initial URI",
            description = "Initial uri used as a starting point for website audit",
            example = "https://www.apple.com/"
    )
    private String initialUri;

    @Schema(
            title = "Project name",
            description = "Project name",
            example = "Apple website"
    )
    private String projectName;
    
    @Schema(
            title = "Domain",
            description = "URI domain",
            example = "https://www.apple.com/"
    )
    private String domain;
    
    @Schema(
            title = "Task ID",
            description = "UUID of generated async task",
            example = "cda9357b-3fa3-4885-9429-4f2c6eb8698b"
    )
    private String taskId;
    
    @Schema(
            title = "Website project ID",
            description = "Project id bind to current audit",
            example = "https://www.apple.com/"
    )
    private String websiteProjectId;
}
