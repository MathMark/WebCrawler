package com.experimental.webcrawler;

import com.experimental.webcrawler.model.Website;
import com.experimental.webcrawler.service.CrawlInitializer;
import com.experimental.webcrawler.util.WebsiteUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

@SpringBootApplication
public class WebcrawlerApplication {
 	Pattern pattern = Pattern.compile("^(https?://[^/]+)");
 	
	public static void main(String[] args) throws IOException, InterruptedException {
		SpringApplication.run(WebcrawlerApplication.class, args);
		//String url = "https://rozetka.com.ua/ua/";
		//String url = "https://netpeaksoftware.com/";
		String url = "https://bookmap.com/ru";
		String domain = WebsiteUtil.cutDomain(url);
		Website website = new Website(url, domain);
		int threadCount = 10;
		CountDownLatch countDownLatch = new CountDownLatch(threadCount);
		CrawlInitializer crawlInitializer = new CrawlInitializer(website, countDownLatch);
		crawlInitializer.crawl(threadCount);
		
		countDownLatch.await();

		System.out.println("Found pages: " + crawlInitializer.getFoundPagesCount());
		System.out.println("Crawled pages: " + crawlInitializer.getCrawledPagesCount());
		
	}
	

}
