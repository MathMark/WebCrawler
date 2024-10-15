package com.seo.service;

import com.seo.dto.PageInfo;
import com.seo.dto.page.WebPageDto;
import com.seo.model.document.WebPageDocument;
import com.seo.repository.PageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PageService {
    
    private static final int PAGE_SIZE_LIMIT = 30;
    private final PageRepository pageRepository;
    
    public Page<WebPageDto> getAllPages(String websiteProjectId, PageInfo pagingForm) {
        Pageable pageable = createPageable(pagingForm);
        long total = pageRepository.countAllByWebProjectId(websiteProjectId);
        Page<WebPageDocument> pageEntities = pageRepository.findAllByWebProjectIdLike(websiteProjectId, pageable);
        List<WebPageDto> pages = pageEntities.stream().map(this::mapToPageDto).collect(Collectors.toList());
        return new PageImpl<>(pages, pageable, total);
    }
    
    private Pageable createPageable(PageInfo pageInfo) {
        if (pageInfo.getPageSize() > PAGE_SIZE_LIMIT) {
            throw new IllegalArgumentException(String.format("Page must not contain more than %s elements.", PAGE_SIZE_LIMIT));
        }
        return PageRequest.of(pageInfo.getPageNumber(), pageInfo.getPageSize());
    }
    
    private WebPageDto mapToPageDto(WebPageDocument webPageDocument) {
        WebPageDto webPageDto = new WebPageDto();
//        webPageDto.setTitle(webPageDocument.getTitle());
//        webPageDto.setDescription(webPageDocument.getDescription());
//        webPageDto.setRobotsContent(webPageDocument.getRobotsContent());
        webPageDto.setUrl(webPageDocument.getUrl());
        return webPageDto;
    }
}
