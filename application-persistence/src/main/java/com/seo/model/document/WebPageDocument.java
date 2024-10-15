package com.seo.model.document;

import com.seo.ContentEntity;
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
    private ContentEntity content;
    private String webProjectId;
    private List<IncomingLinkDocument> incomingLinkDocuments;
}
