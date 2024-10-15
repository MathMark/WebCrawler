package com.seo;

import com.seo.dto.PageInfo;
import com.seo.dto.page.WebPageDto;
import com.seo.service.PageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "PAGES",
        description = "REST API for viewing crawled pages."
)
@RestController
@RequestMapping("/pages")
@RequiredArgsConstructor
public class WebPageController {

    private final PageService pageService;

    @Operation(
            summary = "Get all pages",
            description = "REST API to get all found pages by crawler"
    )
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status OK"
    )
    @GetMapping
    public ResponseEntity<Page<WebPageDto>> getAllPages(@RequestParam String websiteProjectId, PageInfo pageInfo) {
        Page<WebPageDto> page = pageService.getAllPages(websiteProjectId, pageInfo);
        return ResponseEntity.ok(page);
    }


}
