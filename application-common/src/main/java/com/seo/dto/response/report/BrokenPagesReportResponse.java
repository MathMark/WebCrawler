package com.seo.dto.response.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "URL of the broken page")
public class BrokenPagesReportResponse {
    
    @Schema(
            title = "Incoming links",
            description = "Pages that have at least one link to current broken page"
    )
    private List<IncomingLinkResponse> incomingLinks;
    
    @Schema(
            title = "URI",
            description = "URI of the broken page"
    )
    private String uri;
    
    @Schema(
            title = "Status code",
            description = "HTTP status code of the broken page"
    )
    private int statusCode;
}
