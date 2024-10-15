package com.seo.repository;

import com.seo.model.document.IncomingLinkDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IncomingLinkRepository extends MongoRepository<IncomingLinkDocument, String> {
}
