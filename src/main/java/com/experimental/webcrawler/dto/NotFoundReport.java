package com.experimental.webcrawler.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class NotFoundReport {
    private String initialUrl;
    private String brokerUrl;
    private String href;
    private int statusCode;
}
