package com.experimental.webcrawler.repository;

import com.experimental.webcrawler.model.WebPageDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageRepository extends MongoRepository<WebPageDocument, String> {
    List<WebPageDocument> findByWebProjectId(String webProjectId);
    Page<WebPageDocument> findAllByWebProjectIdLike(String webProjectId, Pageable pageable);
    long countAllByWebProjectId(String webProjectId);
}
