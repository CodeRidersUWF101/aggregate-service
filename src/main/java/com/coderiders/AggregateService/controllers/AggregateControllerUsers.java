package com.coderiders.AggregateService.controllers;

import com.coderiders.AggregateService.models.SaveToLibraryResponse;
import com.coderiders.AggregateService.services.UserService;
import com.coderiders.AggregateService.utilities.AggregateConstants;
import com.coderiders.commonutils.models.UserLibrary;
import com.coderiders.commonutils.models.googleBooks.SaveBookRequest;
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

    @PostMapping("/library")
    public Mono<SaveToLibraryResponse> saveBookToLibrary(@RequestBody SaveBookRequest sbr) {
        log.info("/users/library POST ENDPOINT HIT: " + sbr);
        return mockSaveBook
                ? Mono.just(new SaveToLibraryResponse("yourHardcodedIdHere - Added Book"))
                : userService.saveToUsersLibrary(sbr);
    }

    @DeleteMapping("/library")
    public Mono<SaveToLibraryResponse> removeBookFromLibrary(@RequestParam(AggregateConstants.BOOK_ID) String bookId) {
        log.info("/users/library DELETE ENDPOINT HIT: " + bookId);
        return mockDeleteBook
                ? Mono.just(new SaveToLibraryResponse("yourHardcodedIdHere - Removed Book"))
                : userService.removeFromUsersLibrary(bookId);
    }

    @GetMapping("/library")
    public Mono<List<UserLibrary>> getUsersLibrary() {
        log.info("/users/library");
        return mockUsersLibrary
                ? Mono.just(new ArrayList<>())
                : userService.getUsersLibrary();
    }

    @GetMapping("/friends")
    public Mono<SaveToLibraryResponse> getFriendsCurrentlyReading() {
        log.info("/users/friends");
        return mockFriendsCurrRead
                ? Mono.just(new SaveToLibraryResponse("yourHardcodedIdHere - Friends List"))
                : userService.getFriendsCurrentlyReading();
    }
}
