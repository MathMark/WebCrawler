package com.experimental.webcrawler.service;

import com.experimental.webcrawler.crawler.impl.CrawlTask;
import com.experimental.webcrawler.crawler.model.Website;
import com.experimental.webcrawler.dto.crawl.BasicCrawlStatus;
import com.experimental.webcrawler.dto.crawl.CrawlRequest;
import com.experimental.webcrawler.dto.crawl.CrawlResponse;
import com.experimental.webcrawler.dto.crawl.CrawlStatus;
import com.experimental.webcrawler.exception.TaskNotFoundException;
import com.experimental.webcrawler.crawler.model.CrawlData;
import com.experimental.webcrawler.mapper.WebMapper;
import com.experimental.webcrawler.model.BrokenPagesReport;
import com.experimental.webcrawler.model.CrawledPagesReport;
import com.experimental.webcrawler.model.WebsiteProject;
import com.experimental.webcrawler.repository.BrokenPagesReportRepository;

import com.experimental.webcrawler.repository.CrawledPagesReportRepository;
import com.experimental.webcrawler.repository.ProjectRepository;
import com.experimental.webcrawler.service.event.CrawlCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
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
    private final BrokenPagesReportRepository brokenPagesReportRepository;
    private final CrawledPagesReportRepository crawledPagesReportRepository;
    
    private static final Pattern pattern = Pattern.compile("^(https?://[^/]+)");
    private final Map<String, CrawlTask> tasks = new HashMap<>();
    
    public List<BasicCrawlStatus> getAllTasks() {
        return tasks.entrySet().stream().map(e -> BasicCrawlStatus.builder().taskId(e.getKey())
                .projectName(e.getValue().getCrawlData().getWebsite().getProjectName())
                .domain(e.getValue().getCrawlData().getWebsite().getDomain())
                .status(e.getValue().getStatus()).build()).collect(Collectors.toList());
    }
    
    public CrawlResponse startCrawling(CrawlRequest crawlRequest) {
        Website website = generateWebsiteProject(crawlRequest);
        String dataId = UUID.randomUUID().toString();
        CrawlData data = new CrawlData(dataId, website);
        CrawlTask crawlTask = objectProvider.getObject(data);
        crawlTask.addListener(this);
        crawlTask.crawl(crawlRequest.getThreadsCount());
        String taskId = UUID.randomUUID().toString();
        tasks.put(taskId, crawlTask);
        return CrawlResponse.builder()
                .initialUrl(crawlRequest.getStartUrl())
                .domain(website.getDomain())
                .taskId(taskId)
                .websiteProjectId(dataId)
                .projectName(website.getProjectName())
                .build();
    }
    
    public CrawlStatus getCrawlStatus(String taskId) {
        CrawlTask crawlTask = tasks.get(taskId);
        if (crawlTask == null) {
            throw new TaskNotFoundException(String.format("Task with id %s not found.", taskId));
        }
        return createCrawlStatus(crawlTask);
    }
    
    public CrawlStatus stopCrawling(String taskId) {
        CrawlTask crawlTask = tasks.get(taskId);
        if (crawlTask == null) {
            throw new TaskNotFoundException(String.format("Task with id %s not found.", taskId));
        }
        crawlTask.shutDown();
        return createCrawlStatus(crawlTask);
    }
    
    private CrawlStatus createCrawlStatus(CrawlTask task) {
        CrawlData crawlData = task.getCrawlData();
        Website website = crawlData.getWebsite();
         return CrawlStatus.builder()
                .projectName(website.getProjectName())
                .domain(website.getDomain())
                .crawledPages(crawlData.getCrawledPages().size())
                .remainedPages(crawlData.getInternalLinks().size())
                .brokenPagesCount(crawlData.getBrokenPages().size())
                .status(task.getStatus())
                .build();
    }
    
    private Website generateWebsiteProject(CrawlRequest crawlRequest) {
        String url = crawlRequest.getStartUrl();
        String domain = cutDomain(url);
        String projectName = crawlRequest.getProjectName();
        if (projectName == null || "".equals(projectName)) {
            projectName = domain;
        }
        return new Website(projectName, url, domain);
    }
    
    private String cutDomain(String url) {
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    @Override
    public void onCrawlCompete(CrawlCompletedEvent event) {
        WebsiteProject websiteProject = WebMapper.mapToWebsiteProject(event.getCrawlData());
        websiteProjectRepository.save(websiteProject);
        BrokenPagesReport brokenPagesReport = WebMapper.mapToBrokenPageReport(event.getCrawlData().getBrokenPages(), websiteProject.getId());
        brokenPagesReportRepository.save(brokenPagesReport);
        CrawledPagesReport crawledPagesReport = WebMapper.mapToCrawledPagesReport(event.getCrawlData().getCrawledPages().values().stream().toList());
        crawledPagesReportRepository.save(crawledPagesReport);
        log.info("Report for website {} has been successfully saved.", event.getCrawlData().getWebsite().getDomain());
    }
}
