package com.coderiders.AggregateService.services.Impl;

import com.coderiders.AggregateService.exceptions.AggregateException;
import com.coderiders.AggregateService.models.googleBooks.GoogleBook;
import com.coderiders.AggregateService.services.BookSearchService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class BookSearchServiceImpl implements BookSearchService {

  private final WebClient webClient;

  public BookSearchServiceImpl(@Qualifier("bookSearchServiceClient") WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.build();
  }

  @Override
  public Mono<List<GoogleBook>> getGoogleBooksMockData() {
    return webClient.get()
        .uri("/") // Base URL is already set in WebClient.Builder
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<List<GoogleBook>>() {})
        .onErrorResume(e -> {
          throw new AggregateException(e);
        });
  }

}
