package com.experimental.webcrawler.repository;

import com.experimental.webcrawler.model.BrokenPagesDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrokenPagesReportRepository extends MongoRepository<BrokenPagesDocument, String> {
    Optional<BrokenPagesDocument> findByWebsiteProjectId(String websiteProjectId);
}
