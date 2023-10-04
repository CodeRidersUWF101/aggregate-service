package com.coderiders.AggregateService.services.Impl;

import com.coderiders.AggregateService.exceptions.AggregateException;

import com.coderiders.AggregateService.models.UserContext;
import com.coderiders.AggregateService.services.BookSearchService;
import com.coderiders.AggregateService.services.UserService;
import com.coderiders.AggregateService.utilities.AggregateUtils;
import com.coderiders.commonutils.models.UserLibraryWithBookDetails;
import com.coderiders.commonutils.models.googleBooks.GoogleBook;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookSearchServiceImpl implements BookSearchService {

    private final WebClient bookSearchWebClient;
    private final WebClient userServiceWebClient;
    private final UserService userService;

    @Value("${endpoints.booksearch.search}")
    private String bookSearchEndpoint;
    @Value("${endpoints.user.booksave}")
    private String userBookSaveEndpoint;

    public BookSearchServiceImpl(@Qualifier("bookSearchServiceClient") WebClient.Builder bookSearchWebClientBuilder,
                                 @Qualifier("userServiceClient") WebClient.Builder userServiceWebClientBuilder,
                                 UserService userService) {
        this.bookSearchWebClient = bookSearchWebClientBuilder.build();
        this.userServiceWebClient = userServiceWebClientBuilder.build();
        this.userService = userService;
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
    public Mono<List<UserLibraryWithBookDetails>> getBasicSearch(String query) {
        List<UserLibraryWithBookDetails> inLibrary = userService.getUsersLibrary(UserContext.getCurrentUserContext().getClerkId());
        List<UserLibraryWithBookDetails> toUse = inLibrary == null || inLibrary.isEmpty() ? new ArrayList<>() : inLibrary;

        return bookSearchWebClient.get()
                .uri(bookSearchEndpoint + "?term=" + query)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<GoogleBook>>() {})
                .flatMap(books -> {
                    return userServiceWebClient.post()
                            .uri(userBookSaveEndpoint)
                            .bodyValue(books)
                            .retrieve()
                            .bodyToMono(Void.class)
                            .onErrorResume(e -> Mono.empty())  // Handle the error if POST request fails
                            .then(Mono.just(AggregateUtils.googleBookToLibraryWithDetails(books, toUse))); // Return the books after the POST request
                })
                .onErrorResume(e -> {
                    throw new AggregateException(e);
                });
    }

}
