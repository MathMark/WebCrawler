package com.experimental.webcrawler.dto.report;

import lombok.Data;

@Data
public class BrokenPagesReportResponse {
    private String initialUrl;
    private String href;
    private String textAttribute;
    private int statusCode;
}
