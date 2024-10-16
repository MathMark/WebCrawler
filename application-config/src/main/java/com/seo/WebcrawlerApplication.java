package com.seo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@SpringBootApplication
public class WebcrawlerApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(WebcrawlerApplication.class, args);
	}
	

}
