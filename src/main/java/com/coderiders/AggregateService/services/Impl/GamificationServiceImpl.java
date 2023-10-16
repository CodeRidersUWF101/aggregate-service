package com.coderiders.AggregateService.services.Impl;

import com.coderiders.AggregateService.exceptions.AggregateException;
import com.coderiders.AggregateService.services.GamificationService;
import com.coderiders.commonutils.models.UserLibraryWithBookDetails;
import com.coderiders.commonutils.models.googleBooks.SaveBookRequest;
import com.coderiders.commonutils.models.requests.SaveUserChallenges;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;



@Slf4j
@Service
public class GamificationServiceImpl implements GamificationService {

    @Value("${endpoints.gamification.saveChallenge}")
    private String saveUserChallengesEndpoint;

    private final WebClient webClient;

    public GamificationServiceImpl(@Qualifier("gamificationServiceClient") WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public void saveUserChallenge(SaveUserChallenges saveUserChallenges) {



        String response = webClient
                .post()
                .uri(saveUserChallengesEndpoint)
                .bodyValue(saveUserChallenges)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("4xx Response from POST " + saveUserChallengesEndpoint, errorMessage))))
                .onStatus(HttpStatusCode::is5xxServerError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("5xx Response from POST " + saveUserChallengesEndpoint, errorMessage))))
                .bodyToMono(String.class)
                .block();

        if (response == null) {
            throw new AggregateException("Failed to save to Gamification Library");
        }
    }


}
