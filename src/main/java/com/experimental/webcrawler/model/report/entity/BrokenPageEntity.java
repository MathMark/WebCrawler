package com.experimental.webcrawler.model.report.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrokenPageEntity {
    private String initialUrl;
    private String href;
    private String textAttribute;
    private int statusCode;
}
