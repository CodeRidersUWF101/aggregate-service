package com.coderiders.AggregateService.controllers;

import com.coderiders.AggregateService.models.SaveToLibraryResponse;
import com.coderiders.AggregateService.services.UserService;
import com.coderiders.commonutils.models.googleBooks.SaveBookRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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

    @PostMapping("/library")
    public Mono<SaveToLibraryResponse> saveBookToLibrary(@RequestBody SaveBookRequest sbr) {
        log.info("/users/library POST ENDPOINT HIT: " + sbr);
        return mockSaveBook
                ? Mono.just(new SaveToLibraryResponse("yourHardcodedIdHere - Added Book"))
                : userService.saveToUsersLibrary(sbr);
    }

    @DeleteMapping("/library")
    public Mono<SaveToLibraryResponse> removeBookFromLibrary(@RequestParam("book_id") String bookId) {
        log.info("/users/library DELETE ENDPOINT HIT: " + bookId);
        return mockDeleteBook
                ? Mono.just(new SaveToLibraryResponse("yourHardcodedIdHere - Removed Book"))
                : userService.removeFromUsersLibrary(bookId);
    }
}
