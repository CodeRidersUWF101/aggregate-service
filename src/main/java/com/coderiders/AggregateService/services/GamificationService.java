package com.coderiders.AggregateService.services;

import com.coderiders.commonutils.models.UserChallengesExtraDTO;
import com.coderiders.commonutils.models.records.UserBadge;
import com.coderiders.commonutils.models.requests.SaveUserChallenges;

import java.util.List;
import java.util.Map;

public interface GamificationService {
    void saveUserChallenge(SaveUserChallenges saveUserChallenges);
    List<UserChallengesExtraDTO> getUserChallenges(String clerkId);
    Map<String, List<UserBadge>> getUserBadges(String clerkId);
}
