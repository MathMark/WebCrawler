package com.seo.config;

import com.seo.crawler.CrawlClient;
import com.seo.crawler.impl.ConnectionClient;
import com.seo.parser.impl.ContentParser;
import com.seo.parser.Parser;
import com.seo.model.CrawlData;
import com.seo.crawler.impl.CrawlTask;
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
    public CrawlClient crawlClientImpl() {
        HttpClient httpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
        return new ConnectionClient(httpClient);
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public Parser webParser(CrawlData crawlData) {
        return new ContentParser(crawlData);
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public CrawlTask crawlTask(CrawlData crawlData, int threadsCount) {
        return new CrawlTask(threadsCount, webParser(crawlData), crawlData);
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public ExecutorService executorService(int threadsCount) {
        return Executors.newFixedThreadPool(threadsCount);
    }


}
