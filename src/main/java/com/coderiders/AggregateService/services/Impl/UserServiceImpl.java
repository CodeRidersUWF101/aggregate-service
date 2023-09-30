package com.coderiders.AggregateService.services.Impl;

import com.coderiders.AggregateService.exceptions.AggregateException;
import com.coderiders.AggregateService.models.SaveToLibraryResponse;
import com.coderiders.AggregateService.models.UserContext;
import com.coderiders.AggregateService.services.UserService;
import com.coderiders.commonutils.models.googleBooks.SaveBookRequest;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
public class UserServiceImpl implements UserService {

    private final WebClient webClient;

    public UserServiceImpl(@Qualifier("userServiceClient") WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Mono<SaveToLibraryResponse> saveToUsersLibrary(SaveBookRequest saveBookRequest) {
        UserContext usr = UserContext.getCurrentUserContext();
        saveBookRequest.setClerkId(usr.getClerkId());

        return webClient
                .post()
                .uri("/users/library")
                .body(saveBookRequest, SaveBookRequest.class)
                .retrieve()
                .bodyToMono(SaveToLibraryResponse.class).
                onErrorResume(e -> {
                    throw new AggregateException(e);
                });
    }

    @Override
    public Mono<SaveToLibraryResponse> removeFromUsersLibrary(String bookId) {
        UserContext usr = UserContext.getCurrentUserContext();

        URI uri;
        try {
            uri = new URIBuilder()
                    .setPath("/users/library")
                    .setParameter("book_id", bookId)
                    .setParameter("clerk_id", usr.getClerkId())
                    .build();
        } catch (Exception e) {
            throw new AggregateException("Unable to build delete URI");
        }

        return webClient
                .delete()
                .uri(uri)
                .retrieve()
                .bodyToMono(SaveToLibraryResponse.class)
                .onErrorResume(e -> {
                    throw new AggregateException(e);
                });
    }

}
