package com.seo.dto.page;

import lombok.Data;

@Data
public class WebPageDto {
    private String url;
    private String title;
    private String description;
    private String robotsContent;
}
