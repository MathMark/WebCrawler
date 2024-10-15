package com.seo.model.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "reports")
@TypeAlias("emptyTitleReport")
public class EmptyTitleReportDocument extends BaseReportDocument {
    private String title;
    private String url;
}
