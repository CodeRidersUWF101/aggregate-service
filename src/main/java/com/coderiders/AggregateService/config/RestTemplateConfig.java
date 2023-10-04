package com.coderiders.AggregateService.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Value("${serviceUrls.user}")
    private String userServiceBase;

    @Value("${serviceUrls.bookSearch}")
    private String bookSearchServiceBase;

    @Value("${serviceUrls.recommendation}")
    private String recommendationServiceBase;

    @Value("${serviceUrls.gamification}")
    private String gamificationServiceBase;

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);
        return factory;
    }

    @Bean
    @Qualifier("userServiceClient")
    public RestTemplate userServiceRestTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplateBuilder()
                .rootUri(userServiceBase)
                .requestFactory(() -> factory)
                .build();
    }

    @Bean
    @Qualifier("bookSearchServiceClient")
    public RestTemplate bookSearchServiceRestTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplateBuilder()
                .rootUri(bookSearchServiceBase)
                .requestFactory(() -> factory)
                .build();
    }

    @Bean
    @Qualifier("recommendationServiceClient")
    public RestTemplate recommendationServiceRestTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplateBuilder()
                .rootUri(recommendationServiceBase)
                .requestFactory(() -> factory)
                .build();
    }

    @Bean
    @Qualifier("gamificationServiceClient")
    public RestTemplate gamificationServiceRestTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplateBuilder()
                .rootUri(gamificationServiceBase)
                .requestFactory(() -> factory)
                .build();
    }
}
