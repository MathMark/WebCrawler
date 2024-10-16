package com.seo.service;

import com.seo.ContentEntity;
import com.seo.model.BasicCrawlStatus;
import com.seo.dto.request.AuditRequest;
import com.seo.dto.response.AuditResponse;
import com.seo.dto.response.AuditStatus;
import com.seo.crawler.CrawlCompleteListener;
import com.seo.crawler.impl.CrawlTask;
import com.seo.exception.TaskNotFoundException;
import com.seo.model.BrokenWebPage;
import com.seo.model.Content;
import com.seo.model.Link;
import com.seo.model.WebPage;
import com.seo.model.Website;

import com.seo.model.CrawlData;
import com.seo.model.document.IncomingLinkDocument;
import com.seo.model.report.BrokenPagesReportDocument;
import com.seo.model.document.WebPageDocument;
import com.seo.model.document.WebsiteProjectDocument;
import com.seo.model.report.entity.BrokenPageEntity;
import com.seo.model.report.entity.IncomingLink;
import com.seo.repository.report.ReportDocumentRepository;

import com.seo.repository.PageRepository;
import com.seo.repository.ProjectRepository;
import com.seo.crawler.event.CrawlCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CrawlerService implements CrawlCompleteListener {

    private final ObjectProvider<CrawlTask> objectProvider;
    private final ProjectRepository websiteProjectRepository;
    private final ReportDocumentRepository reportDocumentRepository;
    private final PageRepository pageRepository;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    private static final Pattern pattern = Pattern.compile("^(https?://[^/]+)");
    private final Map<String, CrawlTask> tasks = new HashMap<>();


    public List<BasicCrawlStatus> getAllTasks() {
        return tasks.entrySet().stream().map(e -> BasicCrawlStatus.builder().taskId(e.getKey())
                .projectName(e.getValue().getCrawlData().getWebsite().projectName())
                .domain(e.getValue().getCrawlData().getWebsite().domain())
                .status(e.getValue().getStatus()).build()).collect(Collectors.toList());
    }

    public AuditResponse startCrawling(AuditRequest auditRequest) {
        Website website = generateWebsiteProject(auditRequest);
        String dataId = UUID.randomUUID().toString();
        CrawlData data = new CrawlData(dataId, website);
        CrawlTask crawlTask = objectProvider.getObject(data, auditRequest.getThreadsCount());
        crawlTask.addListener(this);
        executorService.execute(crawlTask);

        String taskId = UUID.randomUUID().toString();
        tasks.put(taskId, crawlTask);
        return AuditResponse.builder()
                .initialUri(auditRequest.getStartUri())
                .domain(website.domain())
                .taskId(taskId)
                .websiteProjectId(dataId)
                .projectName(website.projectName())
                .build();
    }

    public AuditStatus getCrawlStatus(String taskId) {
        CrawlTask crawlTask = tasks.get(taskId);
        if (crawlTask == null) {
            throw new TaskNotFoundException(String.format("Task with id %s not found.", taskId));
        }
        return createCrawlStatus(crawlTask);
    }

    public AuditStatus stopCrawling(String taskId) {
        CrawlTask crawlTask = tasks.get(taskId);
        if (crawlTask == null) {
            throw new TaskNotFoundException(String.format("Task with id %s not found.", taskId));
        }
        crawlTask.requestToStop();
        return createCrawlStatus(crawlTask);
    }

    private AuditStatus createCrawlStatus(CrawlTask task) {
        CrawlData crawlData = task.getCrawlData();
        Website website = crawlData.getWebsite();
        return AuditStatus.builder()
                .projectName(website.projectName())
                .domain(website.domain())
                .auditedPages(crawlData.getCrawledPages().size())
                .remainedPages(crawlData.getInternalLinks().size())
                .brokenPagesCount(crawlData.getBrokenWebPages().size())
                .status(task.getStatus())
                .build();
    }

    private Website generateWebsiteProject(AuditRequest auditRequest) {
        String url = auditRequest.getStartUri();
        String domain = cutDomain(url);
        String projectName = auditRequest.getProjectName();
        if (projectName == null || projectName.isBlank()) {
            projectName = domain;
        }
        return new Website(projectName, url, domain);
    }

    private String cutDomain(String url) {
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        }
        return StringUtils.EMPTY;
    }

    @Override
    public void onCrawlCompete(CrawlCompletedEvent event) {
        WebsiteProjectDocument websiteProjectDocument = Mapper.mapToWebsiteProject(event.crawlData());
        websiteProjectRepository.save(websiteProjectDocument);
        List<WebPageDocument> pageEntities = Mapper.mapToPageEntities(event.crawlData().getCrawledPages().values().stream().toList(), websiteProjectDocument.getId());
        pageRepository.saveAll(pageEntities);
        BrokenPagesReportDocument brokenPagesReportDocument = Mapper.mapToBrokenPageReport(event.crawlData().getBrokenWebPages(), websiteProjectDocument.getId());
        reportDocumentRepository.save(brokenPagesReportDocument);
        log.info("Report for website {} has been successfully saved.", event.crawlData().getWebsite().domain());
    }

    private static class Mapper {
        
        private Mapper() {}
        
        private static BrokenPagesReportDocument mapToBrokenPageReport(List<BrokenWebPage> brokenWebPages, String websiteProjectId) {
            List<BrokenPageEntity> brokenPageEntityList = brokenWebPages.stream()
                    .map(Mapper::mapToBrokenPageEntity).collect(Collectors.toList());
            BrokenPagesReportDocument brokenPagesReportDocument = new BrokenPagesReportDocument();
            brokenPagesReportDocument.setId(UUID.randomUUID().toString());
            brokenPagesReportDocument.setBrokenPages(brokenPageEntityList);
            brokenPagesReportDocument.setWebsiteProjectId(websiteProjectId);
            return brokenPagesReportDocument;
        }

        private static BrokenPageEntity mapToBrokenPageEntity(BrokenWebPage brokenWebPage) {
            BrokenPageEntity brokenPageEntity = new BrokenPageEntity();
            brokenPageEntity.setIncomingLinks(brokenWebPage.getWebPage().getIncomingLinks().stream().map(p -> {
                IncomingLink incomingLink = new IncomingLink();
                incomingLink.setUrl(p.getUrl());
                incomingLink.setHrefText(p.getHrefText());
                return incomingLink;
            }).toList());
            brokenPageEntity.setUrl(brokenWebPage.getWebPage().getUrl());
            brokenPageEntity.setTextAttribute(null);
            brokenPageEntity.setStatusCode(brokenWebPage.getStatusCode());
            return brokenPageEntity;
        }

        private static WebsiteProjectDocument mapToWebsiteProject(CrawlData crawlData) {
            WebsiteProjectDocument websiteProjectDocument = new WebsiteProjectDocument();
            Website website = crawlData.getWebsite();
            websiteProjectDocument.setId(crawlData.getId());
            websiteProjectDocument.setInitialUrl(website.startUrl());
            websiteProjectDocument.setDomain(website.domain());
            websiteProjectDocument.setName(website.projectName());
            websiteProjectDocument.setCrawledPages(crawlData.getCrawledPages().size());
            return websiteProjectDocument;
        }

        private static List<WebPageDocument> mapToPageEntities(List<WebPage> pages, String websiteProjectId) {
            return pages.stream().map(p -> mapToPageEntity(p, websiteProjectId)).collect(Collectors.toList());
        }

        private static WebPageDocument mapToPageEntity(WebPage webPage, String webProjectId) {
            List<IncomingLinkDocument> incomingLinkDocuments = webPage.getIncomingLinks().stream().map(Mapper::mapToLinkEntity).collect(Collectors.toList());
            return WebPageDocument.builder().url(webPage.getUrl())
                    .content(mapToContentEntity(webPage.getContent()))
                    .incomingLinkDocuments(incomingLinkDocuments)
                    .webProjectId(webProjectId).build();
        }

        private static IncomingLinkDocument mapToLinkEntity(Link link) {
            IncomingLinkDocument incomingLinkDocument = new IncomingLinkDocument();
            incomingLinkDocument.setUrl(link.getUrl());
            incomingLinkDocument.setHrefText(link.getHrefText());
            return incomingLinkDocument;
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
}
