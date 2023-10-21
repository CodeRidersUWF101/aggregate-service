package com.coderiders.AggregateService.services.Impl;

import com.coderiders.AggregateService.exceptions.AggregateException;
import com.coderiders.AggregateService.models.UserContext;
import com.coderiders.AggregateService.services.GamificationService;
import com.coderiders.commonutils.models.LatestAchievement;
import com.coderiders.commonutils.models.Status;
import com.coderiders.commonutils.models.UserChallengesExtraDTO;
import com.coderiders.commonutils.models.records.UserBadge;
import com.coderiders.commonutils.models.requests.SaveUserChallenges;
import com.coderiders.commonutils.models.requests.UpdateProgress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class GamificationServiceImpl implements GamificationService {

    @Value("${endpoints.gamification.saveChallenge}")
    private String saveUserChallengesEndpoint;

    @Value("${endpoints.gamification.badges}")
    private String userBadgesEndpoint;


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
                        .flatMap(errorMessage -> Mono.error(new AggregateException("4xx Response from GET " + saveUserChallengesEndpoint, errorMessage))))
                .onStatus(HttpStatusCode::is5xxServerError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("5xx Response from POST " + saveUserChallengesEndpoint, errorMessage))))
                .bodyToMono(String.class)
                .block();

        if (response == null) {
            throw new AggregateException("Failed to save to Gamification Library");
        }
    }

    @Override
    public List<UserChallengesExtraDTO> getUserChallenges(String clerkId) {
        String url = String.format("%s/%s", saveUserChallengesEndpoint, clerkId);

        return webClient
                .get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("4xx Response from POST " + saveUserChallengesEndpoint, errorMessage))))
                .onStatus(HttpStatusCode::is5xxServerError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("5xx Response from POST " + saveUserChallengesEndpoint, errorMessage))))
                .bodyToMono(new ParameterizedTypeReference<List<UserChallengesExtraDTO>>() {})
                .block();
    }

    @Override
    public Map<String, List<UserBadge>> getUserBadges(String clerkId) {
        String url = String.format("%s/%s", userBadgesEndpoint, clerkId);

        return webClient
                .get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("4xx Response from GET " + userBadgesEndpoint, errorMessage))))
                .onStatus(HttpStatusCode::is5xxServerError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("5xx Response from GET " + userBadgesEndpoint, errorMessage))))
                .bodyToMono(new ParameterizedTypeReference<Map<String, List<UserBadge>>>() {})
                .block();
    }

    @Override
    public Status saveUserPages(UpdateProgress progress) {

        return  webClient.post()
                .uri("/gamification/pages")
                .bodyValue(progress)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("4xx Response from POST " + "/gamification/pages", errorMessage))))
                .onStatus(HttpStatusCode::is5xxServerError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("5xx Response from POST " + "/gamification/pages", errorMessage))))
                .bodyToMono(Status.class)
                .block();
    }

    @Override
    public List<LatestAchievement> getLatestUserAchievements() {

        return  webClient.get()
                .uri("/gamification/achievements/" + UserContext.getCurrentUserContext().getClerkId())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("4xx Response from POST " + "/gamification/pages", errorMessage))))
                .onStatus(HttpStatusCode::is5xxServerError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("5xx Response from POST " + "/gamification/pages", errorMessage))))
                .bodyToMono(new ParameterizedTypeReference<List<LatestAchievement>>() {})
                .block();
    }
}
