package com.experimental.webcrawler.controller;

import com.experimental.webcrawler.controller.model.PagingForm;
import com.experimental.webcrawler.dto.page.WebPageDto;
import com.experimental.webcrawler.service.PageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pages")
@RequiredArgsConstructor
public class WebPageController {

    private final PageService pageService;

    @GetMapping("/all")
    public ResponseEntity<Page<WebPageDto>> getAllPages(@RequestParam String websiteProjectId, PagingForm pagingForm) {
        Page<WebPageDto> page = pageService.getAllPages(websiteProjectId, pagingForm);
        return ResponseEntity.ok(page);
    }
    

}
