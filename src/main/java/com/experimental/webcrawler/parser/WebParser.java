package com.experimental.webcrawler.parser;

import com.experimental.webcrawler.crawler.model.BrokenPage;
import com.experimental.webcrawler.crawler.model.Page;
import com.experimental.webcrawler.crawler.model.Website;
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
    private final Website website;

    public void parseLinks(Page pageToParse) {

        String htmlBody = getHtmlSource(pageToParse);
        if (htmlBody != null && !htmlBody.isEmpty()) {
            Document doc = Jsoup.parse(htmlBody);
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                Page page = new Page();
                page.setPreviousUrl(pageToParse.getCurrentUrl());
                page.setCurrentUrl(link.attr("href"));
                page.setHrefText(link.text());
                if (page.getCurrentUrl().startsWith(website.getDomain())) {
                    website.getInternalLinks().add(page);
                } else {
                    website.getExternalLinks().add(page);
                }
            }
        }

    }

    private String getHtmlSource(Page page) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(page.getCurrentUrl()))
                    .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)")
                    .GET()
                    .build();
            HttpResponse<String> httpResponse = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (!isHtml(httpResponse)) {
                return null;
            }
            if (isBroken(httpResponse.statusCode())) {
                BrokenPage brokenPage = BrokenPage.builder()
                        .page(page).statusCode(httpResponse.statusCode()).build();
                website.getBrokenPages().add(brokenPage);
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

    private boolean isBroken(int statusCode) {
        return statusCode == HttpStatus.NOT_FOUND.value() ||
                statusCode == HttpStatus.GONE.value() ||
                statusCode == HttpStatus.FORBIDDEN.value();
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
