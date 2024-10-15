package com.seo.repository.report;

import com.seo.model.report.BrokenPagesReportDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BrokenPageReportRepository extends MongoRepository<BrokenPagesReportDocument, String> {
    @Query("{ 'websiteProjectId': ?0 }")
    Optional<BrokenPagesReportDocument> findBrokenPagesReportsByWebsiteProjectId(String websiteProjectId);
}
