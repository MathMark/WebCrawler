package com.experimental.webcrawler.repository;

import com.experimental.webcrawler.model.IncomingLink;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IncomingLinkRepository extends MongoRepository<IncomingLink, String> {
}
