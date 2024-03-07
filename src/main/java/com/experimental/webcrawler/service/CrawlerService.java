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
import com.experimental.webcrawler.model.BrokenPagesDocument;
import com.experimental.webcrawler.model.WebPageDocument;
import com.experimental.webcrawler.model.WebsiteProjectDocument;
import com.experimental.webcrawler.repository.BrokenPagesReportRepository;

import com.experimental.webcrawler.repository.PageRepository;
import com.experimental.webcrawler.repository.ProjectRepository;
import com.experimental.webcrawler.service.event.CrawlCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final BrokenPagesReportRepository brokenPagesReportRepository;
    private final PageRepository pageRepository;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    
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
        CrawlTask crawlTask = objectProvider.getObject(data, crawlRequest.getThreadsCount());
        crawlTask.addListener(this);
        executorService.execute(crawlTask);
    
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
        crawlTask.requestToStop();
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
                .brokenPagesCount(crawlData.getBrokenWebPages().size())
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
        WebsiteProjectDocument websiteProjectDocument = WebMapper.mapToWebsiteProject(event.getCrawlData());
        websiteProjectRepository.save(websiteProjectDocument);
        List<WebPageDocument> pageEntities = WebMapper.mapToPageEntities(event.getCrawlData().getCrawledPages().values().stream().toList(), websiteProjectDocument.getId());
        pageRepository.saveAll(pageEntities);
        BrokenPagesDocument brokenPagesDocument = WebMapper.mapToBrokenPageReport(event.getCrawlData().getBrokenWebPages(), websiteProjectDocument.getId());
        brokenPagesReportRepository.save(brokenPagesDocument);
        log.info("Report for website {} has been successfully saved.", event.getCrawlData().getWebsite().getDomain());
    }
}
