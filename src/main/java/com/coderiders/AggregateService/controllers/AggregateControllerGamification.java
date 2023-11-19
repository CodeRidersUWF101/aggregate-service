package com.coderiders.AggregateService.controllers;


import com.coderiders.AggregateService.models.LeaderboardUser;
import com.coderiders.AggregateService.models.UserContext;
import com.coderiders.AggregateService.models.commonutils.models.LatestAchievement;
import com.coderiders.AggregateService.models.commonutils.models.SingleBookStats;
import com.coderiders.AggregateService.models.commonutils.models.UserChallengesExtraDTO;
import com.coderiders.AggregateService.models.commonutils.models.records.UserBadge;
import com.coderiders.AggregateService.models.commonutils.models.requests.SaveUserChallenges;
import com.coderiders.AggregateService.services.GamificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/gamification")
@RequiredArgsConstructor
public class AggregateControllerGamification {

    private final GamificationService gamificationService;

    @GetMapping("/")
    public String myRoute() {
        log.info("Gamification Base Route Hit");
        return "Successful Gamification AggregateController";
    }

    @PostMapping("/challenge")
    public ResponseEntity<String> saveUserChallenge(@RequestBody SaveUserChallenges saveUserChallenges) {
        log.info("/gamification/challenge POST ENDPOINT HIT: " + saveUserChallenges.getClerkId());
        gamificationService.saveUserChallenge(saveUserChallenges);

        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }

    @GetMapping("/challenge")
    public ResponseEntity<List<UserChallengesExtraDTO>> getUserChallenges() {
        log.info("/gamification/challenge GET ENDPOINT HIT");
        return new ResponseEntity<>(gamificationService.getUserChallenges(UserContext.getCurrentUserContext().getClerkId()), HttpStatus.OK);
    }

    @GetMapping("/badges")
    public ResponseEntity<Map<String, List<UserBadge>>> getUserBadges() {
        log.info("/gamification/badges GET ENDPOINT HIT");
        return new ResponseEntity<>(gamificationService.getUserBadges(UserContext.getCurrentUserContext().getClerkId()), HttpStatus.OK);
    }

    @GetMapping("/achievements")
    public ResponseEntity<List<LatestAchievement>> getLatestUserAchievements() {
        log.info("/achievements GET ENDPOINT HIT");

        return new ResponseEntity<>(gamificationService.getLatestUserAchievements(UserContext.getCurrentUserContext().getClerkId()), HttpStatus.OK);
    }

    @GetMapping("/points")
    public ResponseEntity<Integer> getUserPoints() {
        log.info("/points GET ENDPOINT HIT");

        return new ResponseEntity<>(gamificationService.getUserPoints(UserContext.getCurrentUserContext().getClerkId()), HttpStatus.OK);
    }

    @GetMapping("/stats/singlebook")
    public ResponseEntity<SingleBookStats> getSingleBookStats(@RequestParam(name = "book_id") String bookId) {
        log.info("/stats/singlebook GET ENDPOINT HIT");

        return new ResponseEntity<>(gamificationService.getSingleBookStats(UserContext.getCurrentUserContext().getClerkId(), bookId), HttpStatus.OK);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardUser>> getLeaderboard() {
        log.info("/leaderboard GET ENDPOINT HIT");

        return new ResponseEntity<>(gamificationService.getLeaderboard("LEADERBOARD"), HttpStatus.OK);
    }

    @GetMapping("leaderboard/friends/")
    public ResponseEntity<List<LeaderboardUser>> getLeaderBoardFriends(@RequestParam("clerk_id") String clerk_id) {
        log.info("/leaderboard/friends GET ENDPOINT HIT with ClerkId: " + clerk_id);
        return new ResponseEntity<>(gamificationService.getLeaderboardFriends(clerk_id), HttpStatus.OK);
    }

}
