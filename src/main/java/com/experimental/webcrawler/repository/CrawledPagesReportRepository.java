package com.experimental.webcrawler.repository;

import com.experimental.webcrawler.model.CrawledPagesReport;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CrawledPagesReportRepository extends MongoRepository<CrawledPagesReport, String> {
}
