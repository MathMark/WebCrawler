package com.experimental.webcrawler.crawler.impl;

import com.experimental.webcrawler.crawler.Parser;
import com.experimental.webcrawler.crawler.model.ConnectionResponse;
import com.experimental.webcrawler.crawler.model.Link;
import com.experimental.webcrawler.crawler.model.WebPage;
import com.experimental.webcrawler.crawler.model.CrawlData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class LinkParser implements Parser {

    private static final String A_TAG = "a[href]";
    private static final String HREF_ATTRIBUTE = "href";
    private static final String ANCHOR_LINK = "#";
    private static final String GET_PARAM = "?";
    ;
    private final CrawlData crawlData;

    @Override
    public void parseLinks(WebPage webPage, ConnectionResponse connectionResponse) {
        log.info("Scanning internal page {}", webPage.getUrl());
        if (isHtml(connectionResponse.getContentType())) {
            String htmlBody = connectionResponse.getHtmlBody();
            if (htmlBody != null && !htmlBody.isEmpty()) {
                Document doc = Jsoup.parse(htmlBody);
                Elements links = doc.select(A_TAG);
                for (Element link : links) {
                    parseLink(webPage.getUrl(), link);
                }
            }
        }
    }

    private void parseLink(String currentUrl, Element nextPage) {
        String hrefUrl = parseIfRelated(nextPage.attr(HREF_ATTRIBUTE));

        if (!hrefUrl.contains(ANCHOR_LINK) && !hrefUrl.contains(GET_PARAM)) {
            WebPage webPage = new WebPage();
            webPage.setUrl(hrefUrl);

            if (hrefUrl.startsWith(crawlData.getWebsite().getDomain())) {
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
            return crawlData.getWebsite().getDomain() + url;
        }
        return url;
    }

    private boolean isHtml(List<ConnectionResponse.ContentType> contentTypeList) {
        if (contentTypeList == null || contentTypeList.isEmpty()) {
            return false;
        }
        return contentTypeList.stream().anyMatch(ct -> ct == ConnectionResponse.ContentType.TEXT_HTML);
    }

}
