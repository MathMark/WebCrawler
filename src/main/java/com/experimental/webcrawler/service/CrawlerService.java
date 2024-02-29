package com.experimental.webcrawler.service;

import com.experimental.webcrawler.crawler.CrawlTask;
import com.experimental.webcrawler.dto.CrawlRequest;
import com.experimental.webcrawler.dto.CrawlResponse;
import com.experimental.webcrawler.dto.CrawlStatus;
import com.experimental.webcrawler.exception.TaskNotFoundException;
import com.experimental.webcrawler.crawler.model.Website;
import com.experimental.webcrawler.mapper.WebMapper;
import com.experimental.webcrawler.model.BrokenPagesReport;
import com.experimental.webcrawler.model.WebsiteProject;
import com.experimental.webcrawler.repository.BrokenPagesReportRepository;
import com.experimental.webcrawler.repository.WebsiteProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CrawlerService implements CrawlCompleteListener {
    
    private final WebsiteProjectRepository websiteProjectRepository;
    private final BrokenPagesReportRepository brokenPagesReportRepository;
    
    private static final Pattern pattern = Pattern.compile("^(https?://[^/]+)");
    private final Map<String, CrawlTask> tasks = new HashMap<>();
    
    public CrawlResponse startCrawling(CrawlRequest crawlRequest) {
        Website website = generateWebsiteProject(crawlRequest);
        CrawlTask crawlTask = new CrawlTask(website);
        crawlTask.addListener(this);
        crawlTask.crawl(crawlRequest.getThreadsCount());
        String taskId = UUID.randomUUID().toString();
        tasks.put(taskId, crawlTask);
        return CrawlResponse.builder()
                .initialUrl(crawlRequest.getStartUrl())
                .domain(website.getDomain())
                .taskId(taskId)
                .websiteProjectId(website.getId())
                .projectName(website.getProjectName())
                .build();
    }
    
    public CrawlStatus getCrawlStatus(String taskId) {
        CrawlTask crawlTask = tasks.get(taskId);
        if (crawlTask == null) {
            throw new TaskNotFoundException(String.format("Task with id %s not found.", taskId));
        }
        CrawlStatus crawlStatus = new CrawlStatus();
        crawlStatus.setCrawledPages(crawlTask.getCrawledPagesCount());
        crawlStatus.setRemainedPages(crawlTask.getRemainedPagesCount());
        crawlStatus.setBrokenPagesCount(crawlTask.getBrokenLinksCount());
        crawlStatus.setStatus(crawlTask.getStatus());
        return crawlStatus;
    }
    
    public CrawlStatus stopCrawling(String taskId) {
        CrawlTask crawlTask = tasks.get(taskId);
        if (crawlTask == null) {
            throw new TaskNotFoundException(String.format("Task with id %s not found.", taskId));
        }
        crawlTask.shutDown();
        return CrawlStatus.builder()
                .crawledPages(crawlTask.getCrawledPagesCount())
                .remainedPages(crawlTask.getRemainedPagesCount())
                .brokenPagesCount(crawlTask.getBrokenLinksCount())
                .status(crawlTask.getStatus())
                .build();
    }
    
    private Website generateWebsiteProject(CrawlRequest crawlRequest) {
        String url = crawlRequest.getStartUrl();
        String domain = cutDomain(url);
        String projectName = crawlRequest.getProjectName();
        if (projectName == null || "".equals(projectName)) {
            projectName = domain;
        }
        String projectId = UUID.randomUUID().toString();
        Website website = new Website(projectId, url, domain);
        website.setProjectName(projectName);
        return website;
    }
    
    private String cutDomain(String url) {
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    @Override
    public void onCrawlCompete(Website website) {
        BrokenPagesReport brokenPagesReport = WebMapper.mapToBrokenPageReport(website.getBrokenPages());
        brokenPagesReportRepository.save(brokenPagesReport);
        WebsiteProject websiteProject = WebMapper.mapToWebsiteProject(website, brokenPagesReport);
        websiteProjectRepository.save(websiteProject);
        log.info("Report for website {} has been successfully saved.", website.getDomain());
    }
}
