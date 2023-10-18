package com.coderiders.AggregateService.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;


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
    return WebClient.builder()
            .baseUrl(gamificationServiceBase)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
//            .filter(logRequest())
//            .filter(logResponse());
  }

  private ExchangeFilterFunction logRequest() {
    return (clientRequest, next) -> {
      System.out.println("Request: " + clientRequest.method() + " " + clientRequest.url());
      clientRequest.headers().forEach((name, values) -> values.forEach(value -> System.out.println(name + "=" + value)));
      return next.exchange(clientRequest);
    };
  }

  private ExchangeFilterFunction logResponse() {
    return (clientRequest, next) -> next.exchange(clientRequest)
            .flatMap(clientResponse -> {
              Flux<DataBuffer> body = clientResponse.bodyToFlux(DataBuffer.class);
              return body.collectList().flatMap(dataBuffers -> {
                StringBuilder sb = new StringBuilder();
                dataBuffers.forEach(buffer -> {
                  byte[] bytes = new byte[buffer.readableByteCount()];
                  buffer.read(bytes);
                  DataBufferUtils.release(buffer);
                  sb.append(new String(bytes, StandardCharsets.UTF_8));
                });
                System.out.println("Response Body: " + sb.toString());
                return Mono.just(clientResponse);
              });
            });
  }
}
