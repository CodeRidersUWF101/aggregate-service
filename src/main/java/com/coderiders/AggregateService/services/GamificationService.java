package com.coderiders.AggregateService.services;

import com.coderiders.AggregateService.models.LeaderboardUser;
import com.coderiders.AggregateService.models.commonutils.models.LatestAchievement;
import com.coderiders.AggregateService.models.commonutils.models.SingleBookStats;
import com.coderiders.AggregateService.models.commonutils.models.Status;
import com.coderiders.AggregateService.models.commonutils.models.UserChallengesExtraDTO;
import com.coderiders.AggregateService.models.commonutils.models.records.UserBadge;
import com.coderiders.AggregateService.models.commonutils.models.requests.SaveUserChallenges;
import com.coderiders.AggregateService.models.commonutils.models.requests.UpdateProgress;

import java.util.List;
import java.util.Map;

public interface GamificationService {
    void saveUserChallenge(SaveUserChallenges saveUserChallenges);
    List<UserChallengesExtraDTO> getUserChallenges(String clerkId);
    Map<String, List<UserBadge>> getUserBadges(String clerkId);
    Status saveUserPages(UpdateProgress progress);
    List<LatestAchievement> getLatestUserAchievements(String clerkId);
    Integer getUserPoints(String clerkId);
    SingleBookStats getSingleBookStats(String clerkId, String bookId);
    List<LeaderboardUser> getLeaderboard(String leaderboardId);
}
