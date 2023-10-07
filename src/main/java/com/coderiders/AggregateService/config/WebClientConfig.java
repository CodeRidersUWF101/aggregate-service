package com.coderiders.AggregateService.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;



@Configuration
public class WebClientConfig {

  @Value("${serviceUrls.user}")
  private String userServiceBase;

  @Value("${urls.googlebooks}")
  private String googleBooksBase;

  @Value("${serviceUrls.recommendation}")
  private String recommendationServiceBase;

  @Value("${serviceUrls.gamification}")
  private String gamificationServiceBase;


  @Bean
  @Qualifier("userServiceClient")
  public WebClient.Builder userServiceWebClientBuilder() {
    return WebClient.builder().baseUrl(userServiceBase)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
  }

  @Bean
  @Qualifier("bookSearchServiceClient")
  public WebClient.Builder bookSearchServiceWebClientBuilder() {
    return WebClient.builder().baseUrl(googleBooksBase)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
  }

  @Bean
  @Qualifier("recommendationServiceClient")
  public WebClient.Builder recommendationServiceWebClientBuilder() {
    return WebClient.builder().baseUrl(recommendationServiceBase)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
  }

  @Bean
  @Qualifier("gamificationServiceClient")
  public WebClient.Builder gamificationServiceWebClientBuilder() {
    return WebClient.builder().baseUrl(gamificationServiceBase)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
  }
}
