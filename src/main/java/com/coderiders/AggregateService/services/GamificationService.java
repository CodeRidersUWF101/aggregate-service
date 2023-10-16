package com.coderiders.AggregateService.services;

import com.coderiders.commonutils.models.requests.SaveUserChallenges;
import org.bouncycastle.asn1.cmp.Challenge;

public interface GamificationService {
    void saveUserChallenge(SaveUserChallenges saveUserChallenges);

}
