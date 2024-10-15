package com.seo.parser.impl;

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

}
