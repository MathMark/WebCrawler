package com.experimental.webcrawler.dto.report;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter 
public class IncomingLinkResponse {
    private String url;
    private String hrefText;
}
