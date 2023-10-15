package com.coderiders.AggregateService.controllers;


import com.coderiders.AggregateService.services.GamificationService;
import com.coderiders.commonutils.models.User;
import com.coderiders.commonutils.models.requests.SaveUserChallenges;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    public void saveUserChallenge(@RequestBody SaveUserChallenges saveUserChallenges) {
        log.info("Saving User Challenge with aggregation Service");
//        log.info("/users/signup POST ENDPOINT HIT: " + user.getClerkId());
        gamificationService.saveUserChallenge(saveUserChallenges);
    }
}
