package com.coderiders.AggregateService.services.Impl;

import com.coderiders.AggregateService.exceptions.AggregateException;

import com.coderiders.AggregateService.services.BookSearchService;
import com.coderiders.commonutils.models.googleBooks.GoogleBook;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class BookSearchServiceImpl implements BookSearchService {

    private final WebClient bookSearchWebClient;
    private final WebClient userServiceWebClient;

    @Value("${endpoints.booksearch.search}")
    private String bookSearchEndpoint;
    @Value("${endpoints.user.booksave}")
    private String userBookSaveEndpoint;

    public BookSearchServiceImpl(@Qualifier("bookSearchServiceClient") WebClient.Builder bookSearchWebClientBuilder,
                                 @Qualifier("userServiceClient") WebClient.Builder userServiceWebClientBuilder) {
        this.bookSearchWebClient = bookSearchWebClientBuilder.build();
        this.userServiceWebClient = userServiceWebClientBuilder.build();
    }


  @Override
  public Mono<List<GoogleBook>> getGoogleBooksMockData() {
    return bookSearchWebClient.get()
        .uri("/") // Base URL is already set in WebClient.Builder
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<List<GoogleBook>>() {})
        .onErrorResume(e -> {
          throw new AggregateException(e);
        });
  }

    @Override
    public Mono<List<GoogleBook>> getBasicSearch(String query) {
        return bookSearchWebClient.get()
                .uri(bookSearchEndpoint + "?term=" + query)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<GoogleBook>>() {})
                .flatMap(books -> {
                    // Save books to the userBookSaveEndpoint using userServiceWebClient
                    return userServiceWebClient.post()
                            .uri(userBookSaveEndpoint)
                            .bodyValue(books)
                            .retrieve()
                            .bodyToMono(Void.class)
                            .onErrorResume(e -> Mono.empty())  // Handle the error if POST request fails
                            .then(Mono.just(books)); // Return the books after the POST request
                })
                .onErrorResume(e -> {
                    throw new AggregateException(e);
                });
    }

}
