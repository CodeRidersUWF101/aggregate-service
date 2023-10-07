package com.coderiders.AggregateService.services;

import com.coderiders.AggregateService.models.SaveToLibraryResponse;
import com.coderiders.commonutils.models.User;
import com.coderiders.commonutils.models.UserLibraryWithBookDetails;
import com.coderiders.commonutils.models.googleBooks.SaveBookRequest;
import com.coderiders.commonutils.models.requests.UpdateProgress;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserService {
    List<UserLibraryWithBookDetails> saveToUsersLibrary(String userId, SaveBookRequest saveBookRequest);
    List<UserLibraryWithBookDetails> removeFromUsersLibrary(String userId, UserLibraryWithBookDetails userLibraryWithBookDetails);
    Mono<SaveToLibraryResponse> getFriendsCurrentlyReading();
    List<UserLibraryWithBookDetails> getUsersLibrary(String userId);
    User addUser(User user);
    List<UserLibraryWithBookDetails> updateBookProgress(UpdateProgress updateProgress);
}
