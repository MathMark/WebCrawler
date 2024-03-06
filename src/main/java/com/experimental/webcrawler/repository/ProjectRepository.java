package com.experimental.webcrawler.repository;

import com.experimental.webcrawler.model.WebsiteProjectDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectRepository extends MongoRepository<WebsiteProjectDocument, String> {
}
