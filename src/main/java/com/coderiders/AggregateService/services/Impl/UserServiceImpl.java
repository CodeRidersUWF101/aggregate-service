package com.coderiders.AggregateService.services.Impl;

import com.coderiders.AggregateService.exceptions.AggregateException;
import com.coderiders.AggregateService.models.SaveToLibraryResponse;
import com.coderiders.AggregateService.services.UserService;
import com.coderiders.commonutils.models.googleBooks.SaveBookRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {

    private final WebClient webClient;

    public UserServiceImpl(@Qualifier("userServiceClient") WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
    @Override
    public Mono<SaveToLibraryResponse> saveToUsersLibrary(SaveBookRequest saveBookRequest) {
        return webClient
                .post()
                .uri("/users/library")
                .body(saveBookRequest, SaveBookRequest.class)
                .retrieve()
                .bodyToMono(SaveToLibraryResponse.class).
                onErrorResume(e -> { throw new AggregateException(e); });
    }
}
