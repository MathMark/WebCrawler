package com.experimental.webcrawler.mapper;

import com.experimental.webcrawler.crawler.model.BrokenPage;
import com.experimental.webcrawler.crawler.model.Website;
import com.experimental.webcrawler.model.BrokenPageEntity;
import com.experimental.webcrawler.model.WebsiteReport;

import java.util.List;
import java.util.stream.Collectors;

public class WebMapper {
    private WebMapper() {}
    
    public static WebsiteReport mapToWebSiteReport(Website website) {
        WebsiteReport websiteReport = new WebsiteReport();
        websiteReport.setUrl(website.getUrl());
        websiteReport.setDomain(website.getDomain());
        List<BrokenPageEntity> brokenPageEntityList = website.getBrokenPages().stream()
                .map(WebMapper::mapToBrokenPageEntity).collect(Collectors.toList());
        websiteReport.setBrokenPages(brokenPageEntityList);
        return websiteReport;
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
