package com.experimental.webcrawler.repository;

import com.experimental.webcrawler.model.report.BrokenPagesReportDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrokenPageReportRepository extends MongoRepository<BrokenPagesReportDocument, String> {
    
    @Query("{ 'websiteProjectId': ?0 }")
    List<BrokenPagesReportDocument> findBrokenPagesReportsByWebsiteProjectId(String websiteProjectId);
}
