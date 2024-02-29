package com.experimental.webcrawler.service;

import com.experimental.webcrawler.dto.BrokenPagesReportResponse;
import com.experimental.webcrawler.dto.ReportDto;
import com.experimental.webcrawler.dto.ReportsDto;
import com.experimental.webcrawler.exception.ReportNotFoundException;
import com.experimental.webcrawler.mapper.WebMapper;
import com.experimental.webcrawler.model.BrokenPagesReport;
import com.experimental.webcrawler.repository.BrokenPagesReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportsService {
    
    private final BrokenPagesReportRepository brokenPagesReportRepository;
    
    public ReportsDto getAllReports(String websiteProjectId) {
        ReportsDto reportsDto = new ReportsDto();
        Optional<BrokenPagesReport> brokenPagesReportOptional = brokenPagesReportRepository.findByWebsiteProjectId(websiteProjectId);
        brokenPagesReportOptional.ifPresent(brokenPagesReport -> {
            ReportDto report = new ReportDto();
            report.setReportId(brokenPagesReport.getId());
            report.setReportType(ReportDto.ReportType.BROKEN_PAGES);
            report.setPagesCount(brokenPagesReport.getBrokenPages().size());
            reportsDto.setReport(report);
        });
        return reportsDto;
    }
    
    public List<BrokenPagesReportResponse> getBrokenPagesReport(String reportId) {
        Optional<BrokenPagesReport> brokenPagesReportOptional = brokenPagesReportRepository.findById(reportId);
        BrokenPagesReport brokenPagesReport = brokenPagesReportOptional.orElseThrow(() -> new ReportNotFoundException(""));
        return WebMapper.mapToBrokenPagesReportResponse(brokenPagesReport);
    }
}
