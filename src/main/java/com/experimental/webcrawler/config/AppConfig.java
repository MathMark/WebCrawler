package com.experimental.webcrawler.config;


import com.experimental.webcrawler.crawler.CrawlTask;
import com.experimental.webcrawler.crawler.model.CrawlData;
import com.experimental.webcrawler.parser.WebParser;
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
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public WebParser webParser(CrawlData crawlData) {
        return new WebParser(httpClient(), crawlData);
    }
    
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public CrawlTask crawlTask(CrawlData crawlData) {
        return new CrawlTask(crawlData, webParser(crawlData));
    }
    
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public ExecutorService executorService(int threadsCount) {
        return Executors.newFixedThreadPool(threadsCount);
    }
}
