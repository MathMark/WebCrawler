package com.seo.model.report;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter @Setter
@Document(collection = "reports")
public abstract class BaseReportDocument {
    private String id;
    private String websiteProjectId;
}
