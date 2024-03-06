package com.experimental.webcrawler.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("incomingLinks")
public class IncomingLinkDocument {
    private String url;
    private String hrefText;
}
