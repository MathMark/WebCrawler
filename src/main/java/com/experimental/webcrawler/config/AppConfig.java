package com.experimental.webcrawler.config;

import com.experimental.webcrawler.crawler.ContentParser;
import com.experimental.webcrawler.crawler.CrawlClient;
import com.experimental.webcrawler.crawler.Parser;
import com.experimental.webcrawler.crawler.impl.CrawlClientImpl;
import com.experimental.webcrawler.crawler.impl.CrawlTask;
import com.experimental.webcrawler.crawler.impl.ContentParserImpl;
import com.experimental.webcrawler.crawler.model.CrawlData;
import com.experimental.webcrawler.crawler.impl.LinkParser;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.net.http.HttpClient;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppConfig {
    
    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
    }
    
    @Bean
    public CrawlClient crawlClientImpl() {
        return new CrawlClientImpl(httpClient());
    }
    
    @Bean
    public ContentParser contentParserImpl() {
        return new ContentParserImpl();
    }
    
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public Parser webParser(CrawlData crawlData) {
        return new LinkParser(crawlData);
    }
    
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public CrawlTask crawlTask(CrawlData crawlData) {
        return new CrawlTask(crawlData, webParser(crawlData), contentParserImpl(), crawlClientImpl());
    }
    
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public ExecutorService executorService(int threadsCount) {
        return Executors.newFixedThreadPool(threadsCount);
    }
}
