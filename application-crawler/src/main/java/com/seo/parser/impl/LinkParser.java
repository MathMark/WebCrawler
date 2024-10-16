package com.seo.parser.impl;

import com.seo.model.Content;
import com.seo.parser.Parser;
import com.seo.model.ConnectionResponse;
import com.seo.model.Link;
import com.seo.model.WebPage;
import com.seo.model.CrawlData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class LinkParser implements Parser {

    private static final String A_TAG = "a[href]";
    private static final String HREF_ATTRIBUTE = "href";
    private static final String ANCHOR_LINK = "#";
    private static final String GET_PARAM = "?";
    
    private final CrawlData crawlData;

    @Override
    public void parseLinks(WebPage webPage, ConnectionResponse connectionResponse) {
        log.info("Scanning internal page {}", webPage.getUrl());
        String htmlBody = connectionResponse.getHtmlBody();
        if (connectionResponse.isHasHtmlSource()) {
            Document doc = Jsoup.parse(htmlBody);
            Elements links = doc.select(A_TAG);
            for (Element link : links) {
                parseLink(webPage.getUrl(), link);
            }
        }
    }

    private void parseLink(String currentUrl, Element nextPage) {
        String hrefUrl = parseIfRelated(nextPage.attr(HREF_ATTRIBUTE));

        if (!hrefUrl.contains(ANCHOR_LINK) && !hrefUrl.contains(GET_PARAM)) {
            WebPage webPage = new WebPage();
            webPage.setUrl(hrefUrl);

            if (hrefUrl.startsWith(crawlData.getWebsite().domain())) {
                Link incomingLink = new Link();
                incomingLink.setUrl(currentUrl);
                incomingLink.setHrefText(nextPage.text());
                WebPage existingWebPage = crawlData.getCrawledPages().get(hrefUrl);
                if (existingWebPage == null) {
                    webPage.getIncomingLinks().add(incomingLink);
                    crawlData.getCrawledPages().put(hrefUrl, webPage);
                    crawlData.getInternalLinks().add(webPage);
                } else {
                    existingWebPage.getIncomingLinks().add(incomingLink);
                }
            } else {
                crawlData.getExternalLinks().add(webPage);
            }
        }
    }

    private String parseIfRelated(String url) {
        if (url.startsWith("/")) {
            return crawlData.getWebsite().domain() + url;
        }
        return url;
    }
    
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
