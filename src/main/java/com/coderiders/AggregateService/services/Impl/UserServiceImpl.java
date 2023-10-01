package com.coderiders.AggregateService.services.Impl;

import com.coderiders.AggregateService.exceptions.AggregateException;
import com.coderiders.AggregateService.models.SaveToLibraryResponse;
import com.coderiders.AggregateService.models.UserContext;
import com.coderiders.AggregateService.services.UserService;
import com.coderiders.AggregateService.utilities.AggregateConstants;
import com.coderiders.AggregateService.utilities.UriBuilderWrapper;
import com.coderiders.commonutils.models.UserLibrary;
import com.coderiders.commonutils.models.googleBooks.SaveBookRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final WebClient webClient;

    @Value("${endpoints.user.library}")
    private String usersLibraryEndpoint;
    @Value("${endpoints.user.friends}")
    private String usersFriendsEndpoint;

    public UserServiceImpl(@Qualifier("userServiceClient") WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Mono<SaveToLibraryResponse> saveToUsersLibrary(SaveBookRequest saveBookRequest) {
        UserContext usr = UserContext.getCurrentUserContext();
        saveBookRequest.setClerkId(usr.getClerkId());

        return webClient
                .post()
                .uri(usersLibraryEndpoint)
                .body(Mono.just(saveBookRequest), SaveBookRequest.class)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(SaveToLibraryResponse.class)
                .onErrorResume(e -> { throw new AggregateException(e); });
    }

    @Override
    public Mono<SaveToLibraryResponse> removeFromUsersLibrary(String bookId) {
        UserContext usr = UserContext.getCurrentUserContext();

        String uri = new UriBuilderWrapper(usersLibraryEndpoint)
                .setParameter(AggregateConstants.BOOK_ID, bookId)
                .setParameter(AggregateConstants.CLERK_ID, usr.getClerkId())
                .build();

        return webClient
                .delete()
                .uri(uri)
                .retrieve()
                .bodyToMono(SaveToLibraryResponse.class)
                .onErrorResume(e -> { throw new AggregateException(e); });
    }

    @Override
    public Mono<SaveToLibraryResponse> getFriendsCurrentlyReading() {
        UserContext usr = UserContext.getCurrentUserContext();

        String uri = new UriBuilderWrapper(usersFriendsEndpoint)
                .setParameter(AggregateConstants.CLERK_ID, usr.getClerkId())
                .build();

        return webClient
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(SaveToLibraryResponse.class).
                onErrorResume(e -> { throw new AggregateException(e); });
    }

    @Override
    public Mono<List<UserLibrary>> getUsersLibrary() {
        UserContext usr = UserContext.getCurrentUserContext();

        String uri = new UriBuilderWrapper(usersLibraryEndpoint)
                .setParameter(AggregateConstants.CLERK_ID, usr.getClerkId())
                .build();

        return webClient
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserLibrary>>() {}).
                onErrorResume(e -> { throw new AggregateException(e); });
    }


}
