package com.experimental.webcrawler.mapper;

import com.experimental.webcrawler.crawler.model.BrokenPage;
import com.experimental.webcrawler.crawler.model.Link;
import com.experimental.webcrawler.crawler.model.Page;
import com.experimental.webcrawler.crawler.model.CrawlData;
import com.experimental.webcrawler.crawler.model.Website;
import com.experimental.webcrawler.dto.report.BrokenPagesReportResponse;
import com.experimental.webcrawler.model.BrokenPageEntity;
import com.experimental.webcrawler.model.BrokenPagesReport;
import com.experimental.webcrawler.model.IncomingLink;
import com.experimental.webcrawler.model.PageEntity;
import com.experimental.webcrawler.model.WebsiteProject;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WebMapper {
    private WebMapper() {}
    
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
        Website website = crawlData.getWebsite();
        websiteProject.setId(crawlData.getId());
        websiteProject.setInitialUrl(website.getStartUrl());
        websiteProject.setDomain(website.getDomain());
        websiteProject.setName(website.getProjectName());
        websiteProject.setCrawledPages(crawlData.getCrawledPages().size());
        return websiteProject;
    }
    
    public static List<PageEntity> mapToPageEntities(List<Page> pages, String websiteProjectId) {
        return pages.stream().map(p -> WebMapper.mapToPageEntity(p, websiteProjectId)).collect(Collectors.toList());
    }
    
    private static IncomingLink mapToLinkEntity(Link link) {
        IncomingLink incomingLink = new IncomingLink();
        incomingLink.setUrl(link.getUrl());
        incomingLink.setHrefText(link.getHrefText());
        return incomingLink;
    }
    
    private static PageEntity mapToPageEntity(Page page, String webProjectId) {
        List<IncomingLink> incomingLinks = page.getIncomingLinks().stream().map(WebMapper::mapToLinkEntity).collect(Collectors.toList());
       return PageEntity.builder().url(page.getUrl()).title(page.getTitle()).incomingLinks(incomingLinks).webProjectId(webProjectId).build();
    }
    
    private static BrokenPageEntity mapToBrokenPageEntity(BrokenPage brokenPage) {
        BrokenPageEntity brokenPageEntity = new BrokenPageEntity();
        brokenPageEntity.setInitialUrl(null);
        brokenPageEntity.setHref(brokenPage.getPage().getUrl());
        brokenPageEntity.setTextAttribute(null);
        brokenPageEntity.setStatusCode(brokenPage.getStatusCode());
        return brokenPageEntity;
    }
}
