package com.coderiders.AggregateService.controllers;

import com.coderiders.AggregateService.models.UserContext;
import com.coderiders.AggregateService.services.GamificationService;
import com.coderiders.AggregateService.services.GetLibraryService;
import com.coderiders.AggregateService.services.UserService;
import com.coderiders.commonutils.models.SmallUser;
import com.coderiders.commonutils.models.Status;
import com.coderiders.commonutils.models.UserLibraryWithBookDetails;
import com.coderiders.commonutils.models.UtilsUser;
import com.coderiders.commonutils.models.googleBooks.SaveBookRequest;
import com.coderiders.commonutils.models.requests.AddFriend;
import com.coderiders.commonutils.models.requests.GetFriendsBooks;
import com.coderiders.commonutils.models.requests.UpdateProgress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class AggregateControllerUsers {

    private final UserService userService;
    private final GetLibraryService getLibraryService;
    private final GamificationService gamificationService;

    @PostMapping("/signup")
    public UtilsUser saveUserToDB(@RequestBody UtilsUser user) {
        log.info("/users/signup POST ENDPOINT HIT: " + user.getClerkId());
        return userService.addUser(user);
    }

    @GetMapping("/library")
    public List<UserLibraryWithBookDetails> getUsersLibrary() {
        log.info("/users/library GET ENDPOINT HIT: " + UserContext.getCurrentUserContext().getClerkId());
        return getLibraryService.getUsersLibrary(UserContext.getCurrentUserContext().getClerkId());
    }

    @PostMapping("/library")
    public UserLibraryWithBookDetails saveBookToLibrary(@RequestBody UserLibraryWithBookDetails book) {
        log.info("/users/library POST ENDPOINT HIT: {} for {}", book.getBook_id(), UserContext.getCurrentUserContext().getClerkId());

        UserContext userContext = UserContext.getCurrentUserContext();
        UtilsUser user = UtilsUser.builder()
                .clerkId(userContext.getClerkId())
                .firstName(userContext.getFirstname())
                .lastName(userContext.getLastname())
                .imageUrl(userContext.getImageUrl())
                .username(userContext.getUsername())
                .build();

        SaveBookRequest req = SaveBookRequest.builder()
                .user(user)
                .book(book)
                .build();
        List<UserLibraryWithBookDetails> books = userService.saveToUsersLibrary(req.getUser().getClerkId(), req);
        log.info("SavedToLibrary's Return Size: {}", books.size());
        return books.get(books.size() - 1);
    }

    @PatchMapping("/library")
    public Status updateBook(@RequestBody UpdateProgress updateProgress) {
        log.info("/users/library PATCH ENDPOINT HIT: {} for {}", updateProgress.getBookId(), updateProgress.getClerkId());

        userService.updateBookProgress(updateProgress);
        return gamificationService.saveUserPages(updateProgress);
    }

    @DeleteMapping("/library")
    public UserLibraryWithBookDetails removeBookFromLibrary(@RequestBody UserLibraryWithBookDetails book) {
        log.info("/users/library DELETE ENDPOINT HIT: " + book.getBook_id() + " for: " + UserContext.getCurrentUserContext().getClerkId());

        userService.removeFromUsersLibrary(UserContext.getCurrentUserContext().getClerkId(), book);
        return new UserLibraryWithBookDetails();
    }

    @GetMapping("/friends/pending")
    public Mono<List<SmallUser>> getFriends() {
        log.info("/users/friends");
        return userService.getPendingFriends();
    }

    @GetMapping("/getUsers/")
    public List<UtilsUser> getFriendsNotBlocked(@RequestParam("clerk_id") String clerkId) {
        log.info("users/getUsers GET ENDPOINT HIT for clerkId: " + clerkId);
        return userService.getFriendsNotBlocked(clerkId);
    }

    @GetMapping("/retrieveFriends")
    public List<GetFriendsBooks> GetFriendsBooks(@RequestParam("clerkId") String clerkId) {
        log.info("/users/retrieveFriends GET ENDPOINT HIT: " + clerkId);
        return userService.getFriendsBooks(clerkId);
    }

    @PostMapping("/addFriends")
    public AddFriend addFriend(@RequestBody AddFriend friendRequest) {
        log.info("/users/signup POST ENDPOINT HIT: " + friendRequest.getRequestingClerkId() + "   " + friendRequest.getFriendToAddClerkId());
        return userService.addFriend(friendRequest);
    }

}