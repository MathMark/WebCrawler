package com.seo.service;

import com.seo.dto.response.report.BrokenPagesReportResponse;
import com.seo.dto.response.report.IncomingLinkResponse;
import com.seo.dto.response.report.ReportDto;
import com.seo.model.report.BaseReportDocument;
import com.seo.model.report.BrokenPagesReportDocument;
import com.seo.model.report.EmptyTitleReportDocument;
import com.seo.model.report.entity.BrokenPageEntity;
import com.seo.repository.report.BrokenPageReportRepository;
import com.seo.repository.report.ReportDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Page<BrokenPagesReportResponse> getBrokenPagesReport(String websiteProjectId, int pageNumber, int pageSize) {
        Optional<BrokenPagesReportDocument> brokenPagesReport = brokenPageReportRepository.findBrokenPagesReportsByWebsiteProjectId(websiteProjectId);
        if (brokenPagesReport.isPresent()) {
            List<BrokenPagesReportResponse> reportResponse = mapToBrokenPagesReportResponse(brokenPagesReport.get());
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            return new PageImpl<>(reportResponse, pageable, reportResponse.size());
        }
        return new PageImpl<>(Collections.emptyList());
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

    private List<BrokenPagesReportResponse> mapToBrokenPagesReportResponse(BrokenPagesReportDocument brokenPagesReportDocument) {
        List<BrokenPageEntity> brokenPageEntityList = brokenPagesReportDocument.getBrokenPages();
        return brokenPageEntityList.stream().map(bp -> {
            BrokenPagesReportResponse response = new BrokenPagesReportResponse();
            response.setUri(bp.getUrl());
            response.setIncomingLinks(bp.getIncomingLinks().stream().map(link -> {
                IncomingLinkResponse linkResponse = new IncomingLinkResponse();
                //linkResponse.setHrefText(link.getHrefText());
                linkResponse.setUrl(link.getUrl());
                return linkResponse;
            }).toList());
            //response.setTextAttribute(bp.getTextAttribute());
            response.setStatusCode(bp.getStatusCode());
            return response;
        }).collect(Collectors.toList());
    }
}
