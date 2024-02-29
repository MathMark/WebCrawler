package com.experimental.webcrawler.controller;

import com.experimental.webcrawler.dto.BrokenPagesReportResponse;
import com.experimental.webcrawler.dto.ReportsDto;
import com.experimental.webcrawler.service.ReportsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportsController {
    
    private final ReportsService reportsService;
    
    @GetMapping("/all")
    public ResponseEntity<ReportsDto> getAllReports(@RequestParam String projectId) {
        ReportsDto reportsDto = reportsService.getAllReports(projectId);
        return ResponseEntity.ok(reportsDto);
    }
    
    @GetMapping
    public ResponseEntity<List<BrokenPagesReportResponse>> getAllBrokenPages(@RequestParam String reportId) {
        List<BrokenPagesReportResponse> brokenPagesReport = reportsService.getBrokenPagesReport(reportId);
        return ResponseEntity.ok(brokenPagesReport);
    }
    
}
