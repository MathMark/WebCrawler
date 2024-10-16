package com.seo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import org.hibernate.validator.constraints.Range;

@Data
public class AuditRequest {

    @Schema(
            description = "Initial uri which will be used as a starting point for crawling",
            example = "https://www.apple.com/"
    )
    @NotEmpty(message = "URL must not be empty.")
    @Pattern(regexp = "^(https?://).*", message = "Value is not a URL.")
    private String startUri;

    @Schema(
            description = "Number of parallel connections used for crawling.",
            example = "2"
    )
    @Range(min = 1, max = 30, message = "Allowed setting 1 to 30 threads")
    private int threadsCount;

    @Schema(
            description = "Delay between requests",
            example = "0"
    )
    @Range(min = 0, max = 3000, message = "Allowed setting max 3000ms (3 seconds) delay.")
    private int delayMs;

    @Schema(
            description = "Project name",
            example = "Apple website"
    )
    private String projectName;
}
