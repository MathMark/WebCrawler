package com.experimental.webcrawler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(value = "websiteReport")
public class WebsiteProject {
    @Id
    private String id;
    private String url;
    private String domain;
    
    private List<BrokenPageEntity> brokenPages;
}
