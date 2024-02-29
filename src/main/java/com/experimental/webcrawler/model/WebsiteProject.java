package com.experimental.webcrawler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(value = "websiteReport")
public class WebsiteProject {
    @Id
    private String id;
    private String name;
    private String domain;
    private String initialUrl;
    private int crawledPages;
}
