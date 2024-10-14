package com.experimental.webcrawler.controller;

import com.experimental.webcrawler.dto.report.BrokenPagesReportResponse;
import com.experimental.webcrawler.dto.report.ReportDto;
import com.experimental.webcrawler.service.ReportsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @GetMapping("/{projectId}")
    public ResponseEntity<List<ReportDto>> getAllReports(@PathVariable String projectId) {
        return ResponseEntity.ok(reportsService.getAllReports(projectId));
    }
    
    @Operation(
            summary = "Get broken pages report",
            description = "REST API to get broken pages report"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status OK"
    )
    @GetMapping("/{projectId}/broken-pages")
    public ResponseEntity<List<List<BrokenPagesReportResponse>>> getAllBrokenPages(@PathVariable String projectId) {
        return ResponseEntity.ok(reportsService.getBrokenPagesReport(projectId));
    }
    
}
