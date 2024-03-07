package com.experimental.webcrawler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document("brokenPagesReport")
public class BrokenPagesDocument {
    private String id;
    private List<BrokenPageEntity> brokenPages;
    private String websiteProjectId;
}
