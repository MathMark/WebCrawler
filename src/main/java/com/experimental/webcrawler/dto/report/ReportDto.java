package com.experimental.webcrawler.dto.report;

import lombok.Data;

@Data
public class ReportDto {
    private String reportId;
    private ReportType reportType;
    private int pagesCount;
    
    public enum ReportType {
        BROKEN_PAGES,
        EMPTY_TITLES
    }
}
