package com.experimental.webcrawler.model.report.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class BrokenPageEntity {
    private List<IncomingLink> incomingLinks;
    private String url;
    private String textAttribute;
    private int statusCode;
}
