package com.experimental.webcrawler.repository;

import com.experimental.webcrawler.model.WebsiteProject;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectRepository extends MongoRepository<WebsiteProject, String> {
}
