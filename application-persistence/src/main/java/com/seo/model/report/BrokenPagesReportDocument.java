package com.seo.model.report;

import com.seo.model.report.entity.BrokenPageEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "reports")
@TypeAlias("brokenPagesReport")
public class BrokenPagesReportDocument extends BaseReportDocument {
    private List<BrokenPageEntity> brokenPages;
}
