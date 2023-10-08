package com.coderiders.AggregateService.services.Impl;

import com.coderiders.AggregateService.exceptions.AggregateException;
import com.coderiders.AggregateService.services.GetLibraryService;
import com.coderiders.AggregateService.utilities.AggregateConstants;
import com.coderiders.commonutils.models.UserLibraryWithBookDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class GetLibraryServiceImpl implements GetLibraryService {

    private final WebClient webClient;

    @Value("${endpoints.user.library}")
    private String usersLibraryEndpoint;

    public GetLibraryServiceImpl(@Qualifier("userServiceClient") WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Cacheable(value = "userLibraries", key = "#userId")
    public List<UserLibraryWithBookDetails> getUsersLibrary(String userId) {
        List<UserLibraryWithBookDetails> response = webClient
                .get()
                .uri(builder -> builder.path(usersLibraryEndpoint)
                        .queryParam(AggregateConstants.CLERK_ID, userId)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("4xx Response from GET " + usersLibraryEndpoint, errorMessage))))
                .onStatus(HttpStatusCode::is5xxServerError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("5xx Response from GET " + usersLibraryEndpoint, errorMessage))))
                .bodyToFlux(UserLibraryWithBookDetails.class)
                .map(item -> {
                    item.setInLibrary(true);
                    return item;
                }).collectList()
                .block();

        if (response == null) {
            throw new AggregateException("Failed to retrieve user library");
        }

        log.debug("Retrieved library size: {}", response.size());
        return response;
    }
}
