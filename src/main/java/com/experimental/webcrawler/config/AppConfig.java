package com.experimental.webcrawler.config;


import com.experimental.webcrawler.crawler.CrawlTask;
import com.experimental.webcrawler.crawler.model.Website;
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
    public WebParser webParser() {
        return new WebParser(httpClient());
    }
    
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public CrawlTask crawlTask(Website website) {
        return new CrawlTask(website, webParser());
    }
    
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public ExecutorService executorService(int threadsCount) {
        return Executors.newFixedThreadPool(threadsCount);
    }
}
