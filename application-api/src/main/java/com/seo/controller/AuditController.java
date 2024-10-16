package com.seo.controller;

import com.seo.model.BasicCrawlStatus;
import com.seo.dto.request.AuditRequest;
import com.seo.dto.response.AuditResponse;
import com.seo.dto.response.AuditStatus;
import com.seo.dto.error.ErrorResponseDto;
import com.seo.service.CrawlerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
        name = "AUDIT",
        description = "REST API for controlling audit process"
)
@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
public class AuditController {

    private final CrawlerService crawlerService;

    @Operation(
            summary = "Start audit",
            description = "REST API to initiate website audit",
            parameters = @Parameter(
                    schema = @Schema(implementation = AuditRequest.class)
            )
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
    public ResponseEntity<AuditResponse> startAudit(@RequestBody @Valid AuditRequest crawlRequest) {
        AuditResponse auditResponse = crawlerService.startCrawling(crawlRequest);
        return ResponseEntity.ok(auditResponse);
    }

    @Operation(
            summary = "Get audit status",
            description = "REST API to get status of a certain audit process"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status OK"
    )
    @GetMapping("/status/{taskId}")
    public ResponseEntity<AuditStatus> getAuditStatus(@PathVariable String taskId) {
        AuditStatus auditStatus = crawlerService.getCrawlStatus(taskId);
        return ResponseEntity.ok(auditStatus);
    }


    @Operation(
            summary = "Stop audit",
            description = "REST API to stop a certain audit process"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status OK"
    )
    @GetMapping("/stop/{taskId}")
    public ResponseEntity<AuditStatus> stopAudit(@PathVariable String taskId) {
        AuditStatus auditStatus = crawlerService.stopCrawling(taskId);
        return ResponseEntity.ok(auditStatus);
    }

    @Operation(
            summary = "Get all",
            description = "REST API to get all initiated audit processes."
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
