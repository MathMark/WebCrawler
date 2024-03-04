package com.experimental.webcrawler.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document("crawledPagesReport")
public class CrawledPagesReport {
    @Id
    private String id;
    private List<PageEntity> pages;
}
