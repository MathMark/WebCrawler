package com.experimental.webcrawler.controller;

import com.experimental.webcrawler.dto.report.BrokenPagesReportResponse;
import com.experimental.webcrawler.dto.report.ReportsDto;
import com.experimental.webcrawler.service.ReportsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(
        name = "REPORTS",
        description = "REST API for reports."
)
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportsController {
    
    private final ReportsService reportsService;
    
    @Operation(
            summary = "Get all reports",
            description = "REST API to get all available reports for particular project"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status OK"
    )
    @GetMapping
    public ResponseEntity<ReportsDto> getAllReports(@RequestParam String projectId) {
        ReportsDto reportsDto = reportsService.getAllReports(projectId);
        return ResponseEntity.ok(reportsDto);
    }
    
    @Operation(
            summary = "Get broken pages report",
            description = "REST API to get broken pages report"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status OK"
    )
    @GetMapping("/broken-pages")
    public ResponseEntity<List<BrokenPagesReportResponse>> getAllBrokenPages(@RequestParam String reportId) {
        List<BrokenPagesReportResponse> brokenPagesReport = reportsService.getBrokenPagesReport(reportId);
        return ResponseEntity.ok(brokenPagesReport);
    }
    
}
