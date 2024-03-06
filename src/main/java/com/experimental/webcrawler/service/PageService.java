package com.experimental.webcrawler.service;

import com.experimental.webcrawler.dto.page.WebPageDto;
import com.experimental.webcrawler.mapper.WebMapper;
import com.experimental.webcrawler.model.WebPageDocument;
import com.experimental.webcrawler.repository.PageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PageService {
    private final PageRepository pageRepository;
    
    public Page<WebPageDto> getAllPages(String websiteProjectId, Pageable pageable) {
        long total = pageRepository.countAllByWebProjectId(websiteProjectId);
        Page<WebPageDocument> pageEntities = pageRepository.findAllByWebProjectIdLike(websiteProjectId, pageable);
        List<WebPageDto> pages = pageEntities.stream().map(WebMapper::mapToPageDto).collect(Collectors.toList());
        return new PageImpl<>(pages, pageable, total);
    }
}
