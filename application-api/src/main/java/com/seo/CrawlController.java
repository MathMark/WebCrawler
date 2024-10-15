package com.seo;

import com.seo.crawl.BasicCrawlStatus;
import com.seo.crawl.CrawlRequest;
import com.seo.crawl.CrawlResponse;
import com.seo.crawl.CrawlStatus;
import com.seo.dto.error.ErrorResponseDto;
import com.seo.service.CrawlerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(
        name = "CRAWL",
        description = "CRUD REST APIs for controlling crawling process."
)
@RestController
@RequestMapping("/crawl")
@RequiredArgsConstructor
public class CrawlController {

    private final CrawlerService crawlerService;

    @Operation(
            summary = "Start crawling",
            description = "REST API to initiate crawling process"
    )

    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status OK"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            )
    )
    @PostMapping("/start")
    public ResponseEntity<CrawlResponse> startCrawl(@RequestBody @Valid CrawlRequest crawlRequest) {
        CrawlResponse crawlResponse = crawlerService.startCrawling(crawlRequest);
        return ResponseEntity.ok(crawlResponse);
    }

    @Operation(
            summary = "Get status",
            description = "REST API to get status of a certain crawling process"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status OK"
    )
    @GetMapping("/status/{taskId}")
    public ResponseEntity<CrawlStatus> getCrawlStatus(@PathVariable String taskId) {
        CrawlStatus crawlStatus = crawlerService.getCrawlStatus(taskId);
        return ResponseEntity.ok(crawlStatus);
    }


    @Operation(
            summary = "Stop crawling",
            description = "REST API to stop a certain crawling process"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status OK"
    )
    @GetMapping("/stop/{taskId}")
    public ResponseEntity<CrawlStatus> stopCrawl(@PathVariable String taskId) {
        CrawlStatus crawlStatus = crawlerService.stopCrawling(taskId);
        return ResponseEntity.ok(crawlStatus);
    }

    @Operation(
            summary = "Get all",
            description = "REST API to get all initiated crawling processes."
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status OK"
    )
    @GetMapping("/all")
    public ResponseEntity<List<BasicCrawlStatus>> getAllTasks() {
        List<BasicCrawlStatus> tasks = crawlerService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }
}
