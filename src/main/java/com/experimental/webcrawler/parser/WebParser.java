package com.experimental.webcrawler.parser;

import com.experimental.webcrawler.crawler.model.BrokenPage;
import com.experimental.webcrawler.crawler.model.Page;
import com.experimental.webcrawler.crawler.model.Website;
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
    
    public void parseLinks(Page pageToParse, Website website) {
        try {
            Connection.Response response = Jsoup.connect(pageToParse.getCurrentUrl()).timeout(3000).execute();
            Document doc = response.parse();
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
        } catch (UnsupportedMimeTypeException ignored) {
        } catch (HttpStatusException e) {
            int statusCode = e.getStatusCode();
            log.warn("Couldn't read content from page {}. Status code is: {}", pageToParse.getCurrentUrl(), statusCode);
            if (isBroken(statusCode)) {
                BrokenPage brokenPage = new BrokenPage();
                brokenPage.setPage(pageToParse);
                brokenPage.setStatusCode(statusCode);
                website.getBrokenPages().add(brokenPage);
            }
        } catch (IOException e) {
            log.warn("Exception while trying to scan page {}.", pageToParse.getCurrentUrl(), e);
        }
    }

    private boolean isBroken(int statusCode) {
        return statusCode == HttpStatus.NOT_FOUND.value() ||
                statusCode == HttpStatus.GONE.value() ||
                statusCode == HttpStatus.FORBIDDEN.value();
    }
}
