package com.coderiders.AggregateService.services.Impl;

import com.coderiders.AggregateService.exceptions.AggregateException;
import com.coderiders.AggregateService.models.LeaderboardUser;
import com.coderiders.AggregateService.services.GamificationService;
import com.coderiders.AggregateService.utilities.AggregateUtils;
import com.coderiders.AggregateService.utilities.UriBuilderWrapper;
import com.coderiders.commonutils.models.*;
import com.coderiders.commonutils.models.records.UserBadge;
import com.coderiders.commonutils.models.requests.SaveUserChallenges;
import com.coderiders.commonutils.models.requests.UpdateProgress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
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
    private final WebClient usrWebClient;

    public GamificationServiceImpl(@Qualifier("gamificationServiceClient") WebClient.Builder webClientBuilder,
                                   @Qualifier("userServiceClient") WebClient.Builder usrWebClientBuilder) {
        this.webClient = webClientBuilder.build();
        this.usrWebClient = usrWebClientBuilder.build();
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
                        .flatMap(errorMessage -> Mono.error(new AggregateException("4xx Response from POST " + url, errorMessage))))
                .onStatus(HttpStatusCode::is5xxServerError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("5xx Response from POST " + url, errorMessage))))
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

        return  webClient
                .post()
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
    public List<LatestAchievement> getLatestUserAchievements(String clerkId) {

        return  webClient
                .get()
                .uri("/gamification/achievements/" + clerkId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("4xx Response from GET " + "/gamification/achievements", errorMessage))))
                .onStatus(HttpStatusCode::is5xxServerError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("5xx Response from GET " + "/gamification/achievements", errorMessage))))
                .bodyToMono(new ParameterizedTypeReference<List<LatestAchievement>>() {})
                .block();
    }

    @Override
    public Integer getUserPoints(String clerkId) {
        return  webClient
                .get()
                .uri("/gamification/points/" + clerkId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("4xx Response from GET " + "/gamification/points", errorMessage))))
                .onStatus(HttpStatusCode::is5xxServerError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("5xx Response from GET " + "/gamification/points", errorMessage))))
                .bodyToMono(Integer.class)
                .block();
    }

    @Override
    public SingleBookStats getSingleBookStats(String clerkId, String bookId) {
        String uri = new UriBuilderWrapper("/gamification/stats/singlebook")
                .setParameter("clerk_id", clerkId)
                .setParameter("book_id", bookId)
                .build();

        return webClient
                .get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("4xx Response from GET " + uri, errorMessage))))
                .onStatus(HttpStatusCode::is5xxServerError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("5xx Response from GET " + uri, errorMessage))))
                .bodyToMono(SingleBookStats.class)
                .block();
    }


    @Override
    public List<LeaderboardUser> getLeaderboard(String leaderboardId) {
        List<GamificationLeaderboard> gl = webClient
                .get()
                .uri("/gamification/leaderboard")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("4xx Response from GET " + "/gamification/leaderboard", errorMessage))))
                .onStatus(HttpStatusCode::is5xxServerError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("5xx Response from GET " + "/gamification/leaderboard", errorMessage))))
                .bodyToMono(new ParameterizedTypeReference<List<GamificationLeaderboard>>() {})
                .block();

        if (gl == null) {
            throw new AggregateException("Failed to get leaderboard");
        }

        List<String> clerkIds = gl.stream().map(GamificationLeaderboard::getClerkId).toList();

        List<UtilsUser> ul = usrWebClient
                .post()
                .uri("/users/getByClerkIds")
                .bodyValue(clerkIds)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("4xx Response from GET " + "/gamification/leaderboard", errorMessage))))
                .onStatus(HttpStatusCode::is5xxServerError, resp -> resp.bodyToMono(String.class)
                        .flatMap(errorMessage -> Mono.error(new AggregateException("5xx Response from GET " + "/gamification/leaderboard", errorMessage))))
                .bodyToMono(new ParameterizedTypeReference<List<UtilsUser>>() {})
                .block();

        if (ul == null) {
            throw new AggregateException("Failed to get leaderboard Users from Client Service");
        }

        return AggregateUtils.gamificationLeaderboardToLeaderboardUser(gl, ul);
    }
}
