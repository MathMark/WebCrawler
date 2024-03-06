package com.experimental.webcrawler.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Builder
@Document("pages")
public class WebPageDocument {
    private String url;
    private String title;
    private String description;
    private String robotsContent;
    private String webProjectId;
    private List<IncomingLinkDocument> incomingLinkDocuments;
}
