package com.experimental.webcrawler.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BrokenPage {
    private String initialUrl;
    private String brokerUrl;
    private String href;
    private int statusCode;
}
