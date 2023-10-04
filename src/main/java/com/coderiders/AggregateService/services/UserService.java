package com.coderiders.AggregateService.services;

import com.coderiders.AggregateService.models.SaveToLibraryResponse;
import com.coderiders.commonutils.models.UserLibraryWithBookDetails;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserService {
    List<UserLibraryWithBookDetails> saveToUsersLibrary(String userId, UserLibraryWithBookDetails userLibraryWithBookDetails);
    List<UserLibraryWithBookDetails> removeFromUsersLibrary(String userId, UserLibraryWithBookDetails userLibraryWithBookDetails);
    Mono<SaveToLibraryResponse> getFriendsCurrentlyReading();
    List<UserLibraryWithBookDetails> getUsersLibrary(String userId);
}
