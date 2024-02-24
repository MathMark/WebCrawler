package com.experimental.webcrawler.parser;

import com.experimental.webcrawler.model.BrokenPage;
import com.experimental.webcrawler.model.PageLink;
import com.experimental.webcrawler.model.Website;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@Slf4j
public class WebParser {

    private volatile String previousUrl;

    public void parseLinks(String url, Website website) {
        try {
            Connection.Response response = Jsoup.connect(url).timeout(3000).execute();
            Document doc = response.parse();
            Elements pages = doc.select("a[href]");
            for (Element page : pages) {
                PageLink pageLink = new PageLink();
                pageLink.setHref(page.attr("href"));
                pageLink.setText(page.text());
                if (pageLink.getHref().startsWith(website.getDomain())) {
                    website.getInternalLinks().add(pageLink);
                } else {
                    website.getExternalLinks().add(pageLink);
                }
            }
            previousUrl = url;
        } catch (UnsupportedMimeTypeException ignored) {
        } catch (HttpStatusException e) {
            int statusCode = e.getStatusCode();
            log.warn("Couldn't read content from page {}. Status code is: {}", url, statusCode);
            if (isBroken(statusCode)) {
                BrokenPage brokenPage = BrokenPage.builder()
                        .initialUrl(previousUrl)
                        .brokerUrl(url)
                        .href("")
                        .statusCode(statusCode)
                        .build();
                website.getBrokenPages().add(brokenPage);
            }
        } catch (IOException e) {
            log.warn("Exception while trying to scan page {}.", url, e);
        }
    }

    private boolean isBroken(int statusCode) {
        return statusCode == HttpStatus.NOT_FOUND.value() ||
                statusCode == HttpStatus.GONE.value() ||
                statusCode == HttpStatus.FORBIDDEN.value();
    }
}
