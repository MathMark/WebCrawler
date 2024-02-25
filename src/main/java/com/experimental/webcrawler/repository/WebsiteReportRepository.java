package com.experimental.webcrawler.repository;

import com.experimental.webcrawler.model.WebsiteReport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebsiteReportRepository extends MongoRepository<WebsiteReport, String> {
}
