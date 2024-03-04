package com.experimental.webcrawler.mapper;

import com.experimental.webcrawler.crawler.model.BrokenPage;
import com.experimental.webcrawler.crawler.model.Page;
import com.experimental.webcrawler.crawler.model.CrawlData;
import com.experimental.webcrawler.dto.report.BrokenPagesReportResponse;
import com.experimental.webcrawler.model.BrokenPageEntity;
import com.experimental.webcrawler.model.BrokenPagesReport;
import com.experimental.webcrawler.model.CrawledPagesReport;
import com.experimental.webcrawler.model.PageEntity;
import com.experimental.webcrawler.model.WebsiteProject;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WebMapper {
    private WebMapper() {}
    
    public static CrawledPagesReport mapToCrawledPagesReport(List<Page> pages) {
        List<PageEntity> pageEntities = pages.stream().map(WebMapper::mapToPageEntity).collect(Collectors.toList());
        CrawledPagesReport crawledPagesReport = new CrawledPagesReport();
        crawledPagesReport.setPages(pageEntities);
        return crawledPagesReport;
    }
    
    private static PageEntity mapToPageEntity(Page page) {
        return PageEntity.builder()
                .title(page.getTitle())
                .description(page.getDescription())
                .url(page.getCurrentUrl())
                .robotsContent(page.getRobotsContent())
                .build();
    }
    
    public static BrokenPagesReport mapToBrokenPageReport(List<BrokenPage> brokenPages, String websiteProjectId) {
        List<BrokenPageEntity> brokenPageEntityList = brokenPages.stream()
                .map(WebMapper::mapToBrokenPageEntity).collect(Collectors.toList());
        BrokenPagesReport brokenPagesReport = new BrokenPagesReport();
        brokenPagesReport.setId(UUID.randomUUID().toString());
        brokenPagesReport.setBrokenPages(brokenPageEntityList);
        brokenPagesReport.setWebsiteProjectId(websiteProjectId);
        return brokenPagesReport;
    }
    
    public static List<BrokenPagesReportResponse> mapToBrokenPagesReportResponse(BrokenPagesReport brokenPagesReport) {
        List<BrokenPageEntity> brokenPageEntityList = brokenPagesReport.getBrokenPages();
        return brokenPageEntityList.stream().map(bp -> {
            BrokenPagesReportResponse response = new BrokenPagesReportResponse();
            response.setInitialUrl(bp.getInitialUrl());
            response.setHref(bp.getHref());
            response.setTextAttribute(bp.getTextAttribute());
            response.setStatusCode(bp.getStatusCode());
            return response;
        }).collect(Collectors.toList());
    }
    
    public static WebsiteProject mapToWebsiteProject(CrawlData crawlData) {
        WebsiteProject websiteProject = new WebsiteProject();
        websiteProject.setId(crawlData.getId());
        websiteProject.setInitialUrl(crawlData.getUrl());
        websiteProject.setDomain(crawlData.getDomain());
        websiteProject.setName(crawlData.getProjectName());
        websiteProject.setCrawledPages(crawlData.getPagesCrawled());
        return websiteProject;
    }
    
    private static BrokenPageEntity mapToBrokenPageEntity(BrokenPage brokenPage) {
        BrokenPageEntity brokenPageEntity = new BrokenPageEntity();
        brokenPageEntity.setInitialUrl(brokenPage.getPage().getPreviousUrl());
        brokenPageEntity.setHref(brokenPage.getPage().getCurrentUrl());
        brokenPageEntity.setTextAttribute(brokenPage.getPage().getHrefText());
        brokenPageEntity.setStatusCode(brokenPage.getStatusCode());
        return brokenPageEntity;
    }
}
