package com.experimental.webcrawler.repository;

import com.experimental.webcrawler.model.BrokenPagesReport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrokenPagesReportRepository extends MongoRepository<BrokenPagesReport, String> {
    Optional<BrokenPagesReport> findByWebsiteProjectId(String websiteProjectId);
}