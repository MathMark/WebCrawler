package com.experimental.webcrawler.repository;

import com.experimental.webcrawler.model.document.WebsiteProjectDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectRepository extends MongoRepository<WebsiteProjectDocument, String> {
}
