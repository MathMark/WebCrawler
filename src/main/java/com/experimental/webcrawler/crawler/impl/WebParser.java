package com.experimental.webcrawler.crawler.impl;

import com.experimental.webcrawler.crawler.Parser;
import com.experimental.webcrawler.crawler.model.BrokenWebPage;
import com.experimental.webcrawler.crawler.model.Link;
import com.experimental.webcrawler.crawler.model.WebPage;
import com.experimental.webcrawler.crawler.model.CrawlData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class WebParser implements Parser {

    private static final String A_TAG = "a[href]";
    private static final String TITLE_TAG = "title";
    private static final String HREF_ATTRIBUTE = "href";
    private static final String ANCHOR_LINK = "#";
    private static final String GET_PARAM = "?";

    private final HttpClient httpClient;
    private final CrawlData crawlData;

    @Override
    public void parseLinks(WebPage webPage) {
        log.info("Scanning internal page {}", webPage.getUrl());
        HttpResponse<String> httpResponse = connect(webPage);
        if (httpResponse != null && isHtml(httpResponse)) {
            HttpStatus httpStatus = HttpStatus.resolve(httpResponse.statusCode());
            if (isBroken(httpStatus)) {
                BrokenWebPage brokenWebPage = BrokenWebPage.builder()
                        .webPage(webPage)
                        .statusCode(httpResponse.statusCode())
                        .build();
                crawlData.getBrokenWebPages().add(brokenWebPage);
            }
            String htmlBody = httpResponse.body();
            if (htmlBody != null && !htmlBody.isEmpty()) {
                Document doc = Jsoup.parse(htmlBody);
                parseContent(doc, webPage);
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

    private HttpResponse<String> connect(WebPage webPage) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(webPage.getUrl()))
                    .GET()
                    .build();
            return this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException | IOException e) {
            log.warn("Incorrect URI syntax {}", webPage.getUrl(), e);
        } catch (InterruptedException e) {
            log.warn("HTTP request has been interrupted.");
            Thread.currentThread().interrupt();
        }
        return null;
    }


    private void parseContent(Document document, WebPage webPage) {
        webPage.setTitle(document.title());
    }

    private String parseIfRelated(String url) {
        if (url.startsWith("/")) {
            return crawlData.getWebsite().getDomain() + url;
        }
        return url;
    }
    
    private boolean isBroken(HttpStatus httpStatus) {
        return httpStatus == null || httpStatus.is4xxClientError();
    }

    private boolean isHtml(HttpResponse<String> response) {
        Map<String, List<String>> headers = response.headers().map();
        List<String> contentTypeList = headers.get("content-type");
        if (!contentTypeList.isEmpty()) {
            List<String> htmlContentTypes = contentTypeList.stream().filter(ct -> ct.contains("text/html")).collect(Collectors.toList());
            return !htmlContentTypes.isEmpty();
        }
        return false;
    }
}
