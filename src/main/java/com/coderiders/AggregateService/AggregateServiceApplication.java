package com.coderiders.AggregateService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AggregateServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AggregateServiceApplication.class, args);
	}

}
