package com.experimental.webcrawler.service;

import com.experimental.webcrawler.dto.report.BrokenPagesReportResponse;
import com.experimental.webcrawler.dto.report.ReportDto;
import com.experimental.webcrawler.mapper.WebMapper;
import com.experimental.webcrawler.model.report.BaseReportDocument;
import com.experimental.webcrawler.model.report.BrokenPagesReportDocument;
import com.experimental.webcrawler.model.report.EmptyTitleReportDocument;
import com.experimental.webcrawler.repository.report.BrokenPageReportRepository;
import com.experimental.webcrawler.repository.report.ReportDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportsService {
    
    private final ReportDocumentRepository reportDocumentRepository;
    private final BrokenPageReportRepository brokenPageReportRepository;
    
    public Page<ReportDto> getAllReports(String websiteProjectId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<BaseReportDocument> reports = reportDocumentRepository.findAllByWebsiteProjectId(websiteProjectId, pageable);
        List<ReportDto> converted = reports.stream().map(this::mapToReportDto).toList();
        return new PageImpl<>(converted, pageable, reports.getTotalElements());
    }
    
    public List<List<BrokenPagesReportResponse>> getBrokenPagesReport(String websiteProjectId) {
        List<BrokenPagesReportDocument> brokenPagesReports = brokenPageReportRepository.findBrokenPagesReportsByWebsiteProjectId(websiteProjectId);
        return brokenPagesReports.stream().map(WebMapper::mapToBrokenPagesReportResponse).toList();
    }
    
    private ReportDto mapToReportDto(BaseReportDocument report) {
        ReportDto reportDto = new ReportDto();
        reportDto.setReportId(report.getId());
     
        if (report instanceof BrokenPagesReportDocument brokenPagesReport) {
            reportDto.setReportType(ReportDto.ReportType.BROKEN_PAGES);
            reportDto.setPagesCount(brokenPagesReport.getBrokenPages().size());
        } else if (report instanceof EmptyTitleReportDocument) {
            reportDto.setReportType(ReportDto.ReportType.EMPTY_TITLES);
        }
        return reportDto;
    }
}
