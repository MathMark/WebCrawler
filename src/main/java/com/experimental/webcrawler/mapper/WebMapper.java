package com.experimental.webcrawler.mapper;

import com.experimental.webcrawler.crawler.model.BrokenPage;
import com.experimental.webcrawler.crawler.model.Website;
import com.experimental.webcrawler.model.BrokenPageEntity;
import com.experimental.webcrawler.model.BrokenPagesReport;
import com.experimental.webcrawler.model.WebsiteProject;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WebMapper {
    private WebMapper() {}
    
    public static BrokenPagesReport mapToBrokenPageReport(List<BrokenPage> brokenPages) {
        List<BrokenPageEntity> brokenPageEntityList = brokenPages.stream()
                .map(WebMapper::mapToBrokenPageEntity).collect(Collectors.toList());
        BrokenPagesReport brokenPagesReport = new BrokenPagesReport();
        brokenPagesReport.setId(UUID.randomUUID().toString());
        brokenPagesReport.setBrokenPages(brokenPageEntityList);
        return brokenPagesReport;
    }
    
    public static WebsiteProject mapToWebsiteProject(Website website, BrokenPagesReport brokenPagesReport) {
        WebsiteProject websiteProject = new WebsiteProject();
        websiteProject.setId(website.getId());
        websiteProject.setInitialUrl(website.getUrl());
        websiteProject.setDomain(website.getDomain());
        websiteProject.setName(website.getProjectName());
        websiteProject.setBrokenPagesReportId(brokenPagesReport.getId());
        websiteProject.setCrawledPages(website.getPagesCrawled());
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
