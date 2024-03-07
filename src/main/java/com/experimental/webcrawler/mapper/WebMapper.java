package com.experimental.webcrawler.mapper;

import com.experimental.webcrawler.crawler.model.BrokenWebPage;
import com.experimental.webcrawler.crawler.model.Content;
import com.experimental.webcrawler.crawler.model.Link;
import com.experimental.webcrawler.crawler.model.WebPage;
import com.experimental.webcrawler.crawler.model.CrawlData;
import com.experimental.webcrawler.crawler.model.Website;
import com.experimental.webcrawler.dto.page.WebPageDto;
import com.experimental.webcrawler.dto.report.BrokenPagesReportResponse;
import com.experimental.webcrawler.model.BrokenPageEntity;
import com.experimental.webcrawler.model.BrokenPagesDocument;
import com.experimental.webcrawler.model.ContentEntity;
import com.experimental.webcrawler.model.IncomingLinkDocument;
import com.experimental.webcrawler.model.WebPageDocument;
import com.experimental.webcrawler.model.WebsiteProjectDocument;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WebMapper {
    private WebMapper() {
    }


    public static List<WebPageDto> mapToPageDtos(List<WebPageDocument> pageEntities) {
        return pageEntities.stream().map(WebMapper::mapToPageDto).collect(Collectors.toList());
    }

    public static WebPageDto mapToPageDto(WebPageDocument webPageDocument) {
        WebPageDto webPageDto = new WebPageDto();
//        webPageDto.setTitle(webPageDocument.getTitle());
//        webPageDto.setDescription(webPageDocument.getDescription());
//        webPageDto.setRobotsContent(webPageDocument.getRobotsContent());
        webPageDto.setUrl(webPageDocument.getUrl());
        return webPageDto;
    }

    public static BrokenPagesDocument mapToBrokenPageReport(List<BrokenWebPage> brokenWebPages, String websiteProjectId) {
        List<BrokenPageEntity> brokenPageEntityList = brokenWebPages.stream()
                .map(WebMapper::mapToBrokenPageEntity).collect(Collectors.toList());
        BrokenPagesDocument brokenPagesDocument = new BrokenPagesDocument();
        brokenPagesDocument.setId(UUID.randomUUID().toString());
        brokenPagesDocument.setBrokenPages(brokenPageEntityList);
        brokenPagesDocument.setWebsiteProjectId(websiteProjectId);
        return brokenPagesDocument;
    }

    public static List<BrokenPagesReportResponse> mapToBrokenPagesReportResponse(BrokenPagesDocument brokenPagesDocument) {
        List<BrokenPageEntity> brokenPageEntityList = brokenPagesDocument.getBrokenPages();
        return brokenPageEntityList.stream().map(bp -> {
            BrokenPagesReportResponse response = new BrokenPagesReportResponse();
            response.setInitialUrl(bp.getInitialUrl());
            response.setHref(bp.getHref());
            response.setTextAttribute(bp.getTextAttribute());
            response.setStatusCode(bp.getStatusCode());
            return response;
        }).collect(Collectors.toList());
    }

    public static WebsiteProjectDocument mapToWebsiteProject(CrawlData crawlData) {
        WebsiteProjectDocument websiteProjectDocument = new WebsiteProjectDocument();
        Website website = crawlData.getWebsite();
        websiteProjectDocument.setId(crawlData.getId());
        websiteProjectDocument.setInitialUrl(website.getStartUrl());
        websiteProjectDocument.setDomain(website.getDomain());
        websiteProjectDocument.setName(website.getProjectName());
        websiteProjectDocument.setCrawledPages(crawlData.getCrawledPages().size());
        return websiteProjectDocument;
    }

    public static List<WebPageDocument> mapToPageEntities(List<WebPage> pages, String websiteProjectId) {
        return pages.stream().map(p -> WebMapper.mapToPageEntity(p, websiteProjectId)).collect(Collectors.toList());
    }

    private static IncomingLinkDocument mapToLinkEntity(Link link) {
        IncomingLinkDocument incomingLinkDocument = new IncomingLinkDocument();
        incomingLinkDocument.setUrl(link.getUrl());
        incomingLinkDocument.setHrefText(link.getHrefText());
        return incomingLinkDocument;
    }

    private static WebPageDocument mapToPageEntity(WebPage webPage, String webProjectId) {
        List<IncomingLinkDocument> incomingLinkDocuments = webPage.getIncomingLinks().stream().map(WebMapper::mapToLinkEntity).collect(Collectors.toList());
        return WebPageDocument.builder().url(webPage.getUrl())
                .content(mapToContentEntity(webPage.getContent()))
                .incomingLinkDocuments(incomingLinkDocuments)
                .webProjectId(webProjectId).build();
    }

    private static ContentEntity mapToContentEntity(Content content) {
        if (content != null) {
            return ContentEntity.builder()
                    .h1(content.getH1())
                    .h2(content.getH2())
                    .h3(content.getH3())
                    .h4(content.getH4())
                    .h5(content.getH5())
                    .h6(content.getH6())
                    .metaDescription(content.getMetaDescription())
                    .metaRobots(content.getMetaRobots())
                    .title(content.getTitle()).build();
        }
        return null;
    }


    private static BrokenPageEntity mapToBrokenPageEntity(BrokenWebPage brokenWebPage) {
        BrokenPageEntity brokenPageEntity = new BrokenPageEntity();
        brokenPageEntity.setInitialUrl(null);
        brokenPageEntity.setHref(brokenWebPage.getWebPage().getUrl());
        brokenPageEntity.setTextAttribute(null);
        brokenPageEntity.setStatusCode(brokenWebPage.getStatusCode());
        return brokenPageEntity;
    }
}
