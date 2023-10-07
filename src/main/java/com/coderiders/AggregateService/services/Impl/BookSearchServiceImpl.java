package com.coderiders.AggregateService.services.Impl;

import com.coderiders.AggregateService.exceptions.AggregateException;
import com.coderiders.AggregateService.models.SearchResults;
import com.coderiders.AggregateService.models.UserContext;
import com.coderiders.AggregateService.services.BookSearchService;
import com.coderiders.AggregateService.services.UserService;
import com.coderiders.AggregateService.utilities.AggregateConstants;
import com.coderiders.AggregateService.utilities.AggregateUtils;
import com.coderiders.AggregateService.utilities.UriBuilderWrapper;
import com.coderiders.commonutils.models.UserLibraryWithBookDetails;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookSearchServiceImpl implements BookSearchService {

    private final WebClient bookSearchWebClient;
    private final UserService userService;

    public BookSearchServiceImpl(@Qualifier("bookSearchServiceClient") WebClient.Builder bookSearchWebClientBuilder,
                                 UserService userService) {
        this.bookSearchWebClient = bookSearchWebClientBuilder.build();
        this.userService = userService;
    }

    @Override
    public Mono<List<UserLibraryWithBookDetails>> getBasicSearch(String query) {
        String uri = new UriBuilderWrapper(AggregateConstants.GOOGLE_PATH)
                .setParameter(AggregateConstants.START_INDEX, AggregateConstants.BASIC_START_INDEX)
                .setParameter(AggregateConstants.MAX_RESULTS, AggregateConstants.MAX_RESULTS_COUNT)
                .setParameter(AggregateConstants.QUERY, query)
                .build();

        return makeQuery(uri);
    }

    @Override
    public Mono<List<UserLibraryWithBookDetails>> getPaginatedSearch(String query, String startIndex) {
        String uri = new UriBuilderWrapper(AggregateConstants.GOOGLE_PATH)
                .setParameter(AggregateConstants.START_INDEX, startIndex)
                .setParameter(AggregateConstants.MAX_RESULTS, AggregateConstants.MAX_RESULTS_COUNT)
                .setParameter(AggregateConstants.QUERY, query)
                .build();

        return makeQuery(uri);
    }

    private Mono<List<UserLibraryWithBookDetails>> makeQuery(String uri) {
        List<UserLibraryWithBookDetails> inLibrary = userService.getUsersLibrary(UserContext.getCurrentUserContext().getClerkId());
        List<UserLibraryWithBookDetails> toUse = inLibrary == null || inLibrary.isEmpty() ? new ArrayList<>() : inLibrary;

        return bookSearchWebClient
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<SearchResults>() {})
                .flatMap(searchResults -> searchResults.getItems() != null && !searchResults.getItems().isEmpty()
                        ? Mono.just(AggregateUtils.googleBookToLibraryWithDetails(searchResults.getItems(), toUse))
                        : Mono.empty())
                .onErrorResume(e -> {
                    throw new AggregateException("Error with Google Books Call", e);
                });
    }

}
