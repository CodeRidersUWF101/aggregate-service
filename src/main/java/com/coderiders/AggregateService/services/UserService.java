package com.coderiders.AggregateService.services;

import com.coderiders.AggregateService.models.SaveToLibraryResponse;
import com.coderiders.commonutils.models.SmallUser;
import com.coderiders.commonutils.models.UserLibraryWithBookDetails;
import com.coderiders.commonutils.models.UtilsUser;
import com.coderiders.commonutils.models.googleBooks.SaveBookRequest;
import com.coderiders.commonutils.models.requests.AddFriend;
import com.coderiders.commonutils.models.requests.UpdateProgress;
import com.coderiders.commonutils.models.requests.GetFriendsBooks;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserService {
    List<UserLibraryWithBookDetails> saveToUsersLibrary(String userId, SaveBookRequest saveBookRequest);
    List<UserLibraryWithBookDetails> removeFromUsersLibrary(String userId, UserLibraryWithBookDetails userLibraryWithBookDetails);
    Mono<SaveToLibraryResponse> getFriendsCurrentlyReading();
    UtilsUser addUser(UtilsUser user);
    List<UserLibraryWithBookDetails> updateBookProgress(UpdateProgress updateProgress);
    Mono<List<SmallUser>> getPendingFriends();
    List<UtilsUser> getFriendsNotBlocked(String clerkId);
    List<GetFriendsBooks> getFriendsBooks(String clerkId);
    AddFriend addFriend(AddFriend friendRequest);
}
