package com.experimental.webcrawler.repository;

import com.experimental.webcrawler.model.report.BaseReportDocument;
import com.experimental.webcrawler.model.report.BrokenPagesReportDocument;
import com.experimental.webcrawler.model.report.EmptyTitleReportDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportDocumentRepository extends MongoRepository<BaseReportDocument, String> {
    
    @Query("{'websiteProjectId': ?0 }")
    List<BaseReportDocument> findAllByWebsiteProjectId2(String websiteProjectId);
    
    
    
    @Query("{ '_class': 'emptyTitleReport', 'websiteProjectId': ?0 }")
    List<EmptyTitleReportDocument> findEmptyTitleReportsByWebsiteProjectId(String websiteProjectId);
}
