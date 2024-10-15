package com.seo.repository;

import com.seo.model.document.WebsiteProjectDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectRepository extends MongoRepository<WebsiteProjectDocument, String> {
}
