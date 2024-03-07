package com.experimental.webcrawler.crawler.impl;

import com.experimental.webcrawler.crawler.ContentParser;
import com.experimental.webcrawler.crawler.model.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class ContentParserImpl implements ContentParser {
    
    @Override
    public Content parseContent(String htmlSource) {
        Document doc = Jsoup.parse(htmlSource);
        String metaRobots = doc.select("meta[name=robots]").first().attr("content");
        String metaDescription = doc.select("meta[name=description]").first().attr("content");
        Content content = new Content();
        content.setMetaRobots(metaRobots);
        content.setMetaDescription(metaDescription);
        content.setH1(parseHeaders(doc, "h1"));
        content.setH2(parseHeaders(doc, "h2"));
        content.setH3(parseHeaders(doc, "h3"));
        content.setH4(parseHeaders(doc, "h4"));
        content.setH5(parseHeaders(doc, "h5"));
        content.setH6(parseHeaders(doc, "h6"));
        return content;
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
