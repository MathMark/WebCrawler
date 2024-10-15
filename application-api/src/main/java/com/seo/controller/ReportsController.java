package com.seo.controller;

import com.seo.dto.report.BrokenPagesReportResponse;
import com.seo.dto.report.ReportDto;
import com.seo.service.ReportsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @Parameters({
            @Parameter(
                    name = "pageNumber",
                    description = "The page number to retrieve (starting from 0)",
                    example = "0"
            ),
            @Parameter(
                    name = "pageSize",
                    description = "The number of items per page",
                    example = "10"
            )
    })
    @GetMapping("/{projectId}")
    public ResponseEntity<Page<ReportDto>> getAllReports(@PathVariable String projectId,
                                                         @RequestParam int pageNumber,
                                                         @RequestParam int pageSize) {
        return ResponseEntity.ok(reportsService.getAllReports(projectId, pageNumber, pageSize));
    }

    @Operation(
            summary = "Get broken pages report",
            description = "REST API to get broken pages report"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status OK"
    )
    @Parameters({
            @Parameter(
                    name = "pageNumber",
                    description = "The page number to retrieve (starting from 0)",
                    example = "0"
            ),
            @Parameter(
                    name = "pageSize",
                    description = "The number of items per page",
                    example = "10"
            )
    })
    @GetMapping("/{projectId}/broken-pages")
    public ResponseEntity<Page<BrokenPagesReportResponse>> getAllBrokenPages(@PathVariable String projectId,
                                                                             @RequestParam int pageNumber,
                                                                             @RequestParam int pageSize) {
        return ResponseEntity.ok(reportsService.getBrokenPagesReport(projectId, pageNumber, pageSize));
    }

}
