package com.seo.crawler.impl;

import com.seo.crawler.ContentParser;
import com.seo.model.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class ContentParserImpl implements ContentParser {

    private static final String ROBOTS_QUERY = "meta[name=robots]";
    private static final String DESCRIPTION_QUERY = "meta[name=description]";
    private static final String CONTENT_ATTRIBUTE = "content";

    @Override
    public Content parseContent(String htmlSource) {
        Content content = new Content();
        if (htmlSource != null && !"".equals(htmlSource)) {
            Document doc = Jsoup.parse(htmlSource);
            String metaRobots = parseTag(doc, ROBOTS_QUERY, CONTENT_ATTRIBUTE);
            String metaDescription = parseTag(doc, DESCRIPTION_QUERY, CONTENT_ATTRIBUTE);
            String title = doc.title();
            content.setTitle(title);
            content.setMetaRobots(metaRobots);
            content.setMetaDescription(metaDescription);
            content.setH1(parseHeaders(doc, "h1"));
            content.setH2(parseHeaders(doc, "h2"));
            content.setH3(parseHeaders(doc, "h3"));
            content.setH4(parseHeaders(doc, "h4"));
            content.setH5(parseHeaders(doc, "h5"));
            content.setH6(parseHeaders(doc, "h6"));
        }

        return content;
    }

    private String parseTag(Document document, String cssQuery, String attr) {
        Element element = document.select(cssQuery).first();
        if (element != null) {
            return element.attr(attr);
        }
        return null;
    }

    private List<String> parseHeaders(Document document, String tag) {
        List<String> parsedHeaders = new ArrayList<>();
        Elements headers = document.select(tag);
        for (Element header : headers) {
            String text = header.text();
            parsedHeaders.add(text);
        }
        return parsedHeaders;
    }
}
