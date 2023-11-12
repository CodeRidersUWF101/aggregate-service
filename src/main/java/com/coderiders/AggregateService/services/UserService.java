package com.coderiders.AggregateService.services;

import com.coderiders.AggregateService.models.SaveToLibraryResponse;
import com.coderiders.AggregateService.models.commonutils.models.SmallUser;
import com.coderiders.AggregateService.models.commonutils.models.UserLibraryWithBookDetails;
import com.coderiders.AggregateService.models.commonutils.models.UtilsUser;
import com.coderiders.AggregateService.models.commonutils.models.googleBooks.SaveBookRequest;
import com.coderiders.AggregateService.models.commonutils.models.requests.AddFriend;
import com.coderiders.AggregateService.models.commonutils.models.requests.GetFriendsBooks;
import com.coderiders.AggregateService.models.commonutils.models.requests.UpdateFriendRequest;
import com.coderiders.AggregateService.models.commonutils.models.requests.UpdateProgress;
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
    UpdateFriendRequest updateFriendRequest(UpdateFriendRequest updateRequest);
}
