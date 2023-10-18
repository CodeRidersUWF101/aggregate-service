package com.coderiders.AggregateService.controllers;


import com.coderiders.AggregateService.models.UserContext;
import com.coderiders.AggregateService.services.GamificationService;
import com.coderiders.commonutils.models.UserChallengesExtraDTO;
import com.coderiders.commonutils.models.records.UserBadge;
import com.coderiders.commonutils.models.requests.SaveUserChallenges;
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
        log.info("Saving User Challenge with aggregation Service");
//        log.info("/users/signup POST ENDPOINT HIT: " + user.getClerkId());
        gamificationService.saveUserChallenge(saveUserChallenges);
        String successMessage = "Successfully saved a challenge through the aggregate service for user: " + saveUserChallenges.getClerkId();

        return new ResponseEntity<>(successMessage, HttpStatus.OK);
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
}
