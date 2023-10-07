package com.coderiders.AggregateService.controllers;

import com.coderiders.AggregateService.models.SaveToLibraryResponse;
import com.coderiders.AggregateService.models.UserContext;
import com.coderiders.AggregateService.services.UserService;
import com.coderiders.commonutils.models.User;
import com.coderiders.commonutils.models.UserLibraryWithBookDetails;
import com.coderiders.commonutils.models.requests.UpdateProgress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class AggregateControllerUsers {

    private final UserService userService;

    @Value("${flags.aggregateService.mockdeletebook:false}")
    private boolean mockDeleteBook;
    @Value("${flags.aggregateService.mocksavebook:false}")
    private boolean mockSaveBook;
    @Value("${flags.aggregateService.mockfriendscurrread:false}")
    private boolean mockFriendsCurrRead;
    @Value("${flags.aggregateService.mockgetuserslibrary:false}")
    private boolean mockUsersLibrary;
    @Value("${flags.aggregateService.mockupdateprogress:false}")
    private boolean mockUpdateProgress;

    @PostMapping("/signup")
    public User saveUserToDB(@RequestBody User user) {
        log.info("/users/signup POST ENDPOINT HIT: " + user.getClerkId());
        return userService.addUser(user);
    }

    @GetMapping("/library")
    public List<UserLibraryWithBookDetails> getUsersLibrary() {
        log.info("/users/library GET ENDPOINT HIT: " + UserContext.getCurrentUserContext().getClerkId());
        return mockUsersLibrary
                ? new ArrayList<>()
                : userService.getUsersLibrary(UserContext.getCurrentUserContext().getClerkId());
    }

    @PostMapping("/library")
    public UserLibraryWithBookDetails saveBookToLibrary(@RequestBody UserLibraryWithBookDetails book) {
        log.info("/users/library POST ENDPOINT HIT: " + book.getBook_id() + " for: " + UserContext.getCurrentUserContext().getClerkId());
        book.setInLibrary(true);

        if (mockSaveBook) {
            return new UserLibraryWithBookDetails();
        } else {
            List<UserLibraryWithBookDetails> books = userService.saveToUsersLibrary(UserContext.getCurrentUserContext().getClerkId(), book);
            return books.get(books.size() - 1);
        }
    }

    @PatchMapping("/library")
    public UpdateProgress updateBook(@RequestBody UpdateProgress updateProgress) {
        return mockUpdateProgress
                ? new UpdateProgress()
                : userService.updateBookProgress(updateProgress);
    }

    @DeleteMapping("/library")
    public UserLibraryWithBookDetails removeBookFromLibrary(@RequestBody UserLibraryWithBookDetails book) {
        log.info("/users/library DELETE ENDPOINT HIT: " + book.getBook_id() + " for: " + UserContext.getCurrentUserContext().getClerkId());

        if (!mockDeleteBook) {
            userService.removeFromUsersLibrary(UserContext.getCurrentUserContext().getClerkId(), book);
        }

        return new UserLibraryWithBookDetails();
    }


    @GetMapping("/friends")
    public Mono<SaveToLibraryResponse> getFriendsCurrentlyReading() {
        log.info("/users/friends");
        return mockFriendsCurrRead
                ? Mono.just(new SaveToLibraryResponse(1234L))
                : userService.getFriendsCurrentlyReading();
    }
}
