package com.seo.dto.response.report;

import lombok.Data;

import java.util.List;

@Data
public class BrokenPagesReportResponse {
    private List<IncomingLinkResponse> incomingLinks;
    private String url;
    private String textAttribute;
    private int statusCode;
}
