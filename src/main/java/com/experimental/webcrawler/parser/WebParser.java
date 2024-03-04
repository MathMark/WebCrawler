package com.experimental.webcrawler.parser;

import com.experimental.webcrawler.crawler.model.BrokenPage;
import com.experimental.webcrawler.crawler.model.Page;
import com.experimental.webcrawler.crawler.model.CrawlData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

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
@Component
@RequiredArgsConstructor
public class WebParser {

    private final HttpClient httpClient;
    private final CrawlData crawlData;

    private static final String A_TAG = "a[href]";
    private static final String TITLE_TAG = "title";
    private static final String HREF_ATTRIBUTE = "href";
    private static final String ANCHOR_LINK = "#";
    private static final String GET_PARAM = "?";

    public void parseLinks(Page pageToParse) {
        String htmlBody = getHtmlSource(pageToParse);
        if (htmlBody != null && !htmlBody.isEmpty()) {
            Document doc = Jsoup.parse(htmlBody);
            parseContent(doc, pageToParse);
            Elements links = doc.select(A_TAG);
            for (Element link : links) {
                parseLink(pageToParse.getCurrentUrl(), link);
            }
        }
    }
    
    private void parseLink(String currentUrl, Element link) {
        Page page = new Page();
        page.setPreviousUrl(currentUrl);
        page.setHrefText(link.text());
        String hrefUrl = parseIfRelated(link.attr(HREF_ATTRIBUTE));
        if (!hrefUrl.contains(ANCHOR_LINK) && !hrefUrl.contains(GET_PARAM)) {
            page.setCurrentUrl(hrefUrl);
            if (hrefUrl.startsWith(crawlData.getDomain())) {
                crawlData.getInternalLinks().add(page);
            } else {
                crawlData.getExternalLinks().add(page);
            }
        }
    }
    
    private void parseContent(Document document, Page page) {
        page.setTitle(document.title());
    }

    private String parseIfRelated(String url) {
        if (url.startsWith("/")) {
            return crawlData.getDomain() + url;
        }
        return url;
    }

    private String getHtmlSource(Page page) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(page.getCurrentUrl()))
                    .GET()
                    .build();
            HttpResponse<String> httpResponse = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (!isHtml(httpResponse)) {
                return null;
            }
            HttpStatus httpStatus = HttpStatus.resolve(httpResponse.statusCode());
            if (isBroken(httpStatus)) {
                BrokenPage brokenPage = BrokenPage.builder()
                        .page(page)
                        .statusCode(httpResponse.statusCode())
                        .build();
                crawlData.getBrokenPages().add(brokenPage);
            }
            return httpResponse.body();
        } catch (URISyntaxException e) {
            log.warn("Incorrect URI syntax {}", page.getCurrentUrl());
        } catch (InterruptedException e) {
            log.warn("HttpRequest was interrupted.");
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
