package com.experimental.webcrawler.dto;

import lombok.Data;

@Data
public class ReportDto {
    private String reportId;
    private ReportType reportType;
    private int pagesCount;
    
    public enum ReportType {
        BROKEN_PAGES
    }
}
