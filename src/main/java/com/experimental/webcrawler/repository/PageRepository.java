package com.experimental.webcrawler.repository;

import com.experimental.webcrawler.model.PageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageRepository extends MongoRepository<PageEntity, String> {
}
