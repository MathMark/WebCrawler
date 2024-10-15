package com.seo.mapper;

import com.seo.model.Content;
import com.seo.model.Link;
import com.seo.model.WebPage;
import com.seo.model.CrawlData;
import com.seo.model.Website;
import com.seo.dto.page.WebPageDto;

import com.seo.ContentEntity;
import com.seo.model.document.IncomingLinkDocument;
import com.seo.model.document.WebPageDocument;
import com.seo.model.document.WebsiteProjectDocument;

import java.util.List;
import java.util.stream.Collectors;

public class WebMapper {
    private WebMapper() {
    }
    

    public static WebPageDto mapToPageDto(WebPageDocument webPageDocument) {
        WebPageDto webPageDto = new WebPageDto();
//        webPageDto.setTitle(webPageDocument.getTitle());
//        webPageDto.setDescription(webPageDocument.getDescription());
//        webPageDto.setRobotsContent(webPageDocument.getRobotsContent());
        webPageDto.setUrl(webPageDocument.getUrl());
        return webPageDto;
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


    
}
