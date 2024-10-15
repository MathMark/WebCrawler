package com.seo.dto.crawl;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import org.hibernate.validator.constraints.Range;

@Data
public class CrawlRequest {
    
    @NotEmpty(message = "URL must not be empty.")
    @Pattern(regexp = "^(https?://).*", message = "Value is not a URL.")
    private String startUrl;
    @Range(min = 1, max = 30, message = "Allowed setting 1 to 30 threads")
    private int threadsCount;
    @Range(min = 0, max = 3000, message = "Allowed setting max 3000ms (3 seconds) delay.")
    private int delayMs;
    private String projectName;
}
