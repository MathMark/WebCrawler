package com.experimental.webcrawler.repository;

import com.experimental.webcrawler.model.IncomingLinkDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IncomingLinkRepository extends MongoRepository<IncomingLinkDocument, String> {
}
