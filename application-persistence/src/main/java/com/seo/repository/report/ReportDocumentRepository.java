package com.seo.repository.report;

import com.seo.model.report.BaseReportDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportDocumentRepository extends MongoRepository<BaseReportDocument, String> {
    Page<BaseReportDocument> findAllByWebsiteProjectId(String websiteProjectId, Pageable pageable);
}
